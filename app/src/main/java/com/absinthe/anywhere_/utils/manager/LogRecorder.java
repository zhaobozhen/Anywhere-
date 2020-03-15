package com.absinthe.anywhere_.utils.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * LogRecorder
 */
public class LogRecorder {

    public static final int LOG_LEVEL_NO_SET = 0;

    public static final int LOG_BUFFER_MAIN = 1;
    public static final int LOG_BUFFER_SYSTEM = 1 << 1;
    public static final int LOG_BUFFER_RADIO = 1 << 2;
    public static final int LOG_BUFFER_EVENTS = 1 << 3;
    public static final int LOG_BUFFER_KERNEL = 1 << 4; // not be supported by now

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARNING = 5;
    public static final int ERROR = 6;

    private static final int LOG_BUFFER_DEFAULT = LOG_BUFFER_MAIN | LOG_BUFFER_SYSTEM;

    private static final int INVALID_PID = -1;

    private static LogRecorder sInstance;

    private String mFileSuffix;
    private String mFolderPath;
    private int mFileSizeLimitation;
    private int mLevel;
    private List<String> mFilterTags = new ArrayList<>();
    private int mPID = INVALID_PID;

    private boolean mUseLogcatFileOut = false;

    private LogDumper mLogDumper = null;

    private static final int EVENT_RESTART_LOG = 1001;

    private RestartHandler mHandler;

    public static LogRecorder getInstance() {
        if (sInstance == null) {
            sInstance = new LogRecorder();
        }
        return sInstance;
    }

    public static void setInstance(LogRecorder logRecorder) {
        sInstance = logRecorder;
    }

    private static class RestartHandler extends Handler {
        final LogRecorder logRecorder;

        RestartHandler(LogRecorder logRecorder) {
            this.logRecorder = logRecorder;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == EVENT_RESTART_LOG) {
                logRecorder.stop();
                logRecorder.start();
            }
        }
    }

    private LogRecorder() {
        mHandler = new RestartHandler(this);
    }

    public void start() {
        // make sure the out folder exist
        // TODO support multi-phase path
        File file = new File(mFolderPath);
        if (!file.exists()) {
            boolean result = file.mkdirs();

            if (!result) {
                return;
            }
        }

        String cmdStr = collectLogcatCommand();

        if (mLogDumper != null) {
            mLogDumper.stopDumping();
            mLogDumper = null;
        }

        mLogDumper = new LogDumper(mFolderPath, mFileSuffix, mFileSizeLimitation, cmdStr, mHandler);
        mLogDumper.start();
    }

    public void stop() {
        // TODO maybe should clean the log buffer first?
        if (mLogDumper != null) {
            mLogDumper.stopDumping();
            mLogDumper = null;
        }
    }

    private String collectLogcatCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        final String SPACE = " ";
        stringBuilder.append("logcat");

        // TODO select ring buffer, -b

        // TODO set out format
        stringBuilder.append(SPACE);
        stringBuilder.append("-v time");

        // append tag filters
        String levelStr = getLevelStr();

        if (!mFilterTags.isEmpty()) {
            stringBuilder.append(SPACE);
            stringBuilder.append("-s");
            for (int i = 0; i < mFilterTags.size(); i++) {
                String tag = mFilterTags.get(i) + ":" + levelStr;
                stringBuilder.append(SPACE);
                stringBuilder.append(tag);
            }
        } else {
            if (!TextUtils.isEmpty(levelStr)) {
                stringBuilder.append(SPACE);
                stringBuilder.append("*:").append(levelStr);
            }
        }

        // logcat -f , but the rotated count default is 4?
        // can`t be sure to use that feature
        if (mPID != INVALID_PID) {
            mUseLogcatFileOut = false;
            String pidStr = adjustPIDStr();
            if (!TextUtils.isEmpty(pidStr)) {
                stringBuilder.append(SPACE);
                stringBuilder.append("|");
                stringBuilder.append(SPACE);
                stringBuilder.append("grep (").append(pidStr).append(")");
            }
        }

        return stringBuilder.toString();
    }

    private String getLevelStr() {
        switch (mLevel) {
            case 2:
                return "V";
            case 3:
                return "D";
            case 4:
                return "I";
            case 5:
                return "W";
            case 6:
                return "E";
            case 7:
                return "F";
        }

        return "V";
    }

    /**
     * Android`s user app pid is bigger than 1000.
     *
     * @return adjusted PID
     */
    private String adjustPIDStr() {
        if (mPID == INVALID_PID) {
            return null;
        }

        String pidStr = String.valueOf(mPID);
        int length = pidStr.length();
        if (length < 4) {
            pidStr = " 0" + pidStr;
        }

        if (length == 4) {
            pidStr = " " + pidStr;
        }

        return pidStr;
    }


    private static class LogDumper extends Thread {
        final String logPath;
        final String logFileSuffix;
        final String logCmd;
        final int logFileLimitation;
        final private Object mRunningLock = new Object();

        final RestartHandler restartHandler;

        private Process logcatProcess;
        private BufferedReader mReader = null;
        private FileOutputStream out = null;

        private boolean mRunning = true;
        private long currentFileSize;

        LogDumper(String folderPath, String suffix,
                  int fileSizeLimitation, String command,
                  RestartHandler handler) {
            logPath = folderPath;
            logFileSuffix = suffix;
            logFileLimitation = fileSizeLimitation;
            logCmd = command;
            restartHandler = handler;

            String date = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault())
                    .format(new Date(System.currentTimeMillis()));
            String fileName = (TextUtils.isEmpty(logFileSuffix)) ? date : (logFileSuffix + "-" + date);
            try {
                out = new FileOutputStream(new File(logPath, fileName + ".log"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        void stopDumping() {
            synchronized (mRunningLock) {
                mRunning = false;
            }
        }

        @Override
        public void run() {
            try {
                logcatProcess = Runtime.getRuntime().exec(logCmd);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProcess.getInputStream()), 1024);
                String line;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null) {
                        byte[] data = (line + "\n").getBytes();
                        out.write(data);
                        if (logFileLimitation != 0) {
                            currentFileSize += data.length;
                            if (currentFileSize > logFileLimitation * 1024) {
                                restartHandler.sendEmptyMessage(EVENT_RESTART_LOG);
                                break;
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProcess != null) {
                    logcatProcess.destroy();
                    logcatProcess = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }

    public static class Builder {

        /**
         * context object
         */
        private Context mContext;

        /**
         * the folder name that we save log files to,
         * just folder name, not the whole path,
         * if set this, will save log files to /sdcard/$mLogFolderName folder,
         * use /sdcard/$ApplicationName as default.
         */
        private String mLogFolderName;

        /**
         * the whole folder path that we save log files to,
         * this setting`s priority is bigger than folder name.
         */
        private String mLogFolderPath;

        /**
         * the log file suffix,
         * if this is sot, it will be appended to log file name automatically
         */
        private String mLogFileNameSuffix = "";

        /**
         * single log file size limitation,
         * in k-bytes, ex. set to 16, is 16KB limitation.
         */
        private int mLogFileSizeLimitation = 0;

        /**
         * log level, see android.util.Log, 2 - 7,
         * if not be set, will use verbose as default
         */
        private int mLogLevel = LogRecorder.LOG_LEVEL_NO_SET;

        /**
         * can set several filter tags
         * logcat -s ActivityManager:V SystemUI:V
         */
        private List<String> mLogFilterTags = new ArrayList<>();

        /**
         * filter through pid, by setting this with your APP PID,
         * the log recorder will just record the APP`s own log,
         * use one call: android.os.Process.myPid().
         */
        private int mPID = LogRecorder.INVALID_PID;

        /**
         * which log buffer to catch...
         * <p/>
         * Request alternate ring buffer, 'main', 'system', 'radio'
         * or 'events'. Multiple -b parameters are allowed and the
         * results are interleaved.
         * <p/>
         * The default is -b main -b system.
         */
        private int mLogBuffersSelected = LogRecorder.LOG_BUFFER_DEFAULT;

        /**
         * log output format, don`t support config yet, use $time format as default.
         * <p/>
         * Log messages contain a number of metadata fields, in addition to the tag and priority.
         * You can modify the output format for messages so that they display a specific metadata
         * field. To do so, you use the -v option and specify one of the supported output formats
         * listed below.
         * <p/>
         * brief       — Display priority/tag and PID of the process issuing the message.
         * process     — Display PID only.
         * tag         — Display the priority/tag only.
         * thread      — Display the priority, tag, and the PID(process ID) and TID(thread ID)
         * of the thread issuing the message.
         * raw         — Display the raw log message, with no other metadata fields.
         * time        — Display the date, invocation time, priority/tag, and PID of
         * the process issuing the message.
         * thread time — Display the date, invocation time, priority, tag, and the PID(process ID)
         * and TID(thread ID) of the thread issuing the message.
         * long        — Display all metadata fields and separate messages with blank lines.
         */
        private int mLogOutFormat;

        /**
         * set log out folder name
         *
         * @param logFolderName folder name
         * @return The same Builder.
         */
        public Builder setLogFolderName(String logFolderName) {
            this.mLogFolderName = logFolderName;
            return this;
        }

        /**
         * set log out folder path
         *
         * @param logFolderPath out folder absolute path
         * @return the same Builder
         */
        public Builder setLogFolderPath(String logFolderPath) {
            this.mLogFolderPath = logFolderPath;
            return this;
        }

        /**
         * set log file name suffix
         *
         * @param logFileNameSuffix auto append suffix
         * @return the same Builder
         */
        public Builder setLogFileNameSuffix(String logFileNameSuffix) {
            this.mLogFileNameSuffix = logFileNameSuffix;
            return this;
        }

        /**
         * set the file size limitation
         *
         * @param fileSizeLimitation file size limitation in KB
         * @return the same Builder
         */
        public Builder setLogFileSizeLimitation(int fileSizeLimitation) {
            this.mLogFileSizeLimitation = fileSizeLimitation;
            return this;
        }

        /**
         * set the log level
         *
         * @param logLevel log level, 2-7
         * @return the same Builder
         */
        public Builder setLogLevel(int logLevel) {
            this.mLogLevel = logLevel;
            return this;
        }

        /**
         * add log filterspec tag name, can add multiple ones,
         * they use the same log level set by setLogLevel()
         *
         * @param tag tag name
         * @return the same Builder
         */
        public Builder addLogFilterTag(String tag) {
            mLogFilterTags.add(tag);
            return this;
        }

        /**
         * which process`s log
         *
         * @param mPID process id
         * @return the same Builder
         */
        public Builder setPID(int mPID) {
            this.mPID = mPID;
            return this;
        }

        /**
         * -b radio, -b main, -b system, -b events
         * -b main -b system as default
         *
         * @param logBuffersSelected one of
         *                           LOG_BUFFER_MAIN = 1 << 0;
         *                           LOG_BUFFER_SYSTEM = 1 << 1;
         *                           LOG_BUFFER_RADIO = 1 << 2;
         *                           LOG_BUFFER_EVENTS = 1 << 3;
         *                           LOG_BUFFER_KERNEL = 1 << 4;
         * @return the same Builder
         */
        public Builder setLogBufferSelected(int logBuffersSelected) {
            this.mLogBuffersSelected = logBuffersSelected;
            return this;
        }

        /**
         * sets log out format, -v parameter
         *
         * @param logOutFormat out format, like -v time
         * @return the same Builder
         */
        public Builder setLogOutFormat(int logOutFormat) {
            this.mLogOutFormat = logOutFormat;
            return this;
        }

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * call this only if mLogFolderName and mLogFolderPath not
         * be set both.
         */
        private void applyAppNameAsOutFolderName() {
            try {
                String appName = mContext.getPackageName();
                String versionName = mContext.getPackageManager().getPackageInfo(
                        appName, 0).versionName;
                int versionCode = mContext.getPackageManager()
                        .getPackageInfo(appName, 0).versionCode;
                mLogFolderName = appName + "-" + versionName + "-" + versionCode;
                mLogFolderPath = applyOutFolderPath();
            } catch (Exception ignored) {
            }
        }

        private String applyOutFolderPath() {
            return mContext.getExternalFilesDir(null) + File.separator + mLogFolderName;
        }

        /**
         * Combine all of the options that have been set and return
         * a new {@link LogRecorder} object.
         */
        public LogRecorder build() {
            LogRecorder logRecorder = new LogRecorder();

            // no folder name & folder path be set
            if (TextUtils.isEmpty(mLogFolderName)
                    && TextUtils.isEmpty(mLogFolderPath)) {
                applyAppNameAsOutFolderName();
            }

            // make sure out path be set
            if (TextUtils.isEmpty(mLogFolderPath)) {
                mLogFolderPath = applyOutFolderPath();
            }

            logRecorder.mFolderPath = mLogFolderPath;
            logRecorder.mFileSuffix = mLogFileNameSuffix;
            logRecorder.mFileSizeLimitation = mLogFileSizeLimitation;
            logRecorder.mLevel = mLogLevel;
            if (!mLogFilterTags.isEmpty()) {
                logRecorder.mFilterTags.addAll(mLogFilterTags);
            }
            logRecorder.mPID = mPID;

            return logRecorder;
        }
    }

}
