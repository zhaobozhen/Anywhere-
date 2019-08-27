package com.absinthe.anywhere_.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.absinthe.anywhere_.MainActivity;
import com.absinthe.anywhere_.R;
import com.absinthe.anywhere_.services.CollectorService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CollectorView extends LinearLayout {
    public static final String TAG = "CollectorView";

    private final Context mContext;
    private final WindowManager mWindowManager;
    private ImageButton mIbCollector;

    private String packageName, className;

    public CollectorView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.layout_collector, this);
        mIbCollector = findViewById(R.id.ib_collector);

        mIbCollector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startService(
                        new Intent(mContext, CollectorService.class)
                                .putExtra(CollectorService.COMMAND, CollectorService.COMMAND_CLOSE)
                );
                mContext.startActivity(
                        new Intent(mContext, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("packageName", packageName)
                                .putExtra("className", className));
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    @Subscribe
    public void onEventMainThread(CollectorService.ActivityChangedEvent event){
        Log.d(TAG, "event:" + event.getPackageName() + ": " + event.getClassName());
        String packageName = event.getPackageName(),
                className = event.getClassName();

        this.packageName = packageName;
        this.className = className.startsWith(packageName)?
                        className.substring(packageName.length()):
                        className;
    }

    Point preP, curP;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                preP = new Point((int)event.getRawX(), (int)event.getRawY());
                break;

            case MotionEvent.ACTION_MOVE:
                curP = new Point((int)event.getRawX(), (int)event.getRawY());
                int dx = curP.x - preP.x,
                        dy = curP.y - preP.y;

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) this.getLayoutParams();
                layoutParams.x += dx;
                layoutParams.y += dy;
                Log.d("Motion",dx+","+dy);
                mWindowManager.updateViewLayout(this, layoutParams);

                preP = curP;
                break;
        }

        return false;
    }
}
