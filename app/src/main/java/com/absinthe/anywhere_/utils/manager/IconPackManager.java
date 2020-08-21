package com.absinthe.anywhere_.utils.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.blankj.utilcode.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Icon Pack Manager
 */
public class IconPackManager {

    private Context mContext;

    public void setContext(Context context) {
        mContext = context;
    }

    public class IconPack {
        public String packageName;
        public String name;

        private boolean mLoaded = false;
        private HashMap<String, String> mPackagesDrawables = new HashMap<>();

        private List<Bitmap> mBackImages = new ArrayList<>();
        private Bitmap mMaskImage = null;
        private Bitmap mFrontImage = null;
        private float mFactor = 1.0f;
        private int totalIcons;

        Resources iconPackres = null;

        void load() {
            // load AppFilter.xml from the icon pack package
            PackageManager pm = mContext.getPackageManager();
            if (pm == null) {
                return;
            }

            try {
                XmlPullParser xpp = null;

                iconPackres = pm.getResourcesForApplication(packageName);

                int appFilterId = iconPackres.getIdentifier("appfilter", "xml", packageName);
                if (appFilterId > 0) {
                    xpp = iconPackres.getXml(appFilterId);
                } else {
                    // no resource found, try to open it from assests folder
                    try {
                        InputStream appfilterstream = iconPackres.getAssets().open("appfilter.xml");

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        xpp = factory.newPullParser();
                        xpp.setInput(appfilterstream, "utf-8");
                    } catch (IOException e1) {
                        //Ln.d("No appfilter.xml file");
                    }
                }

                if (xpp != null) {
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            switch (xpp.getName()) {
                                case "iconback":
                                    int count = xpp.getAttributeCount();
                                    for (int i = 0; i < count; i++) {
                                        if (xpp.getAttributeName(i).startsWith("img")) {
                                            String drawableName = xpp.getAttributeValue(i);
                                            Bitmap iconback = loadBitmap(drawableName);
                                            if (iconback != null) {
                                                mBackImages.add(iconback);
                                            }
                                        }
                                    }
                                    break;
                                case "iconmask":
                                    if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1")) {
                                        String drawableName = xpp.getAttributeValue(0);
                                        mMaskImage = loadBitmap(drawableName);
                                    }
                                    break;
                                case "iconupon":
                                    if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("img1")) {
                                        String drawableName = xpp.getAttributeValue(0);
                                        mFrontImage = loadBitmap(drawableName);
                                    }
                                    break;
                                case "scale":
                                    // mFactor
                                    if (xpp.getAttributeCount() > 0 && xpp.getAttributeName(0).equals("factor")) {
                                        mFactor = Float.parseFloat(xpp.getAttributeValue(0));
                                    }
                                    break;
                                case "item":
                                    String componentName = null;
                                    String drawableName = null;
                                    count = xpp.getAttributeCount();

                                    for (int i = 0; i < count; i++) {
                                        if (xpp.getAttributeName(i).equals("component")) {
                                            componentName = xpp.getAttributeValue(i);
                                        } else if (xpp.getAttributeName(i).equals("drawable")) {
                                            drawableName = xpp.getAttributeValue(i);
                                        }
                                    }
                                    if (!mPackagesDrawables.containsKey(componentName)) {
                                        mPackagesDrawables.put(componentName, drawableName);
                                        totalIcons = totalIcons + 1;
                                    }
                                    break;
                            }
                        }
                        eventType = xpp.next();
                    }
                }
                mLoaded = true;
            } catch (PackageManager.NameNotFoundException e) {
                //Ln.d("Cannot load icon pack");
            } catch (XmlPullParserException e) {
                //Ln.d("Cannot parse icon pack appfilter.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Bitmap loadBitmap(String drawableName) {
            int id = iconPackres.getIdentifier(drawableName, "drawable", packageName);
            if (id > 0) {
                Drawable bitmap = ResourcesCompat.getDrawable(iconPackres, id, null);
                if (bitmap instanceof BitmapDrawable) {
                    return ((BitmapDrawable) bitmap).getBitmap();
                }
            }
            return null;
        }

        private Drawable loadDrawable(String drawableName) {
            int id = iconPackres.getIdentifier(drawableName, "drawable", packageName);
            if (id > 0) {
                try {
                    return ResourcesCompat.getDrawable(iconPackres, id, null);
                } catch (Resources.NotFoundException e) {
                    return null;
                }
            }
            return null;
        }

        @Nullable
        public Drawable getDrawableIconForPackage(String appPackageName, Drawable defaultDrawable) {
            if (!mLoaded) {
                load();
            }

            PackageManager pm = mContext.getPackageManager();

            Intent launchIntent = pm.getLaunchIntentForPackage(appPackageName);

            String componentName = null;

            if (launchIntent != null) {
                componentName = Objects.requireNonNull(
                        Objects.requireNonNull(
                                pm.getLaunchIntentForPackage(appPackageName)).getComponent()).toString();
            }

            String drawable = mPackagesDrawables.get(componentName);

            if (drawable != null) {
                return loadDrawable(drawable);
            } else {
                // try to get a resource with the component filename
                if (componentName != null) {
                    int start = componentName.indexOf("{") + 1;
                    int end = componentName.indexOf("}", start);
                    if (end > start) {
                        drawable = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");
                        if (iconPackres.getIdentifier(drawable, "drawable", packageName) > 0)
                            return loadDrawable(drawable);
                    }
                }
            }
            return defaultDrawable;
        }

        public Bitmap getIconForPackage(String appPackageName, Bitmap defaultBitmap) {
            if (!mLoaded) {
                load();
            }

            PackageManager pm = mContext.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(appPackageName);
            String componentName = null;
            if (launchIntent != null) {
                componentName = Objects.requireNonNull(
                        Objects.requireNonNull(
                        pm.getLaunchIntentForPackage(appPackageName))
                        .getComponent()).toString();
            }
            String drawable = mPackagesDrawables.get(componentName);
            if (drawable != null) {
                Bitmap BMP = loadBitmap(drawable);
                if (BMP == null) {
                    return generateBitmap(defaultBitmap);
                } else {
                    return BMP;
                }
            } else {
                // try to get a resource with the component filename
                if (componentName != null) {
                    int start = componentName.indexOf("{") + 1;
                    int end = componentName.indexOf("}", start);
                    if (end > start) {
                        drawable = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");
                        if (iconPackres.getIdentifier(drawable, "drawable", packageName) > 0)
                            return loadBitmap(drawable);
                    }
                }
            }
            return generateBitmap(defaultBitmap);
        }

        public int getTotalIcons() {
            return totalIcons;
        }

        private Bitmap generateBitmap(Bitmap defaultBitmap) {
            // if no support images in the icon pack return the bitmap itself
            if (mBackImages.size() == 0) {
                return defaultBitmap;
            }

            Random r = new Random();
            int backImageInd = r.nextInt(mBackImages.size());
            Bitmap backImage = mBackImages.get(backImageInd);
            int w = backImage.getWidth();
            int h = backImage.getHeight();

            // create a bitmap for the result
            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(result);

            // draw the background first
            mCanvas.drawBitmap(backImage, 0, 0, null);

            // create a mutable mask bitmap with the same mask
            Bitmap scaledBitmap;
            if (defaultBitmap.getWidth() > w || defaultBitmap.getHeight() > h) {
                scaledBitmap = Bitmap.createScaledBitmap(defaultBitmap, (int) (w * mFactor), (int) (h * mFactor), false);
            } else {
                scaledBitmap = Bitmap.createBitmap(defaultBitmap);
            }

            Bitmap mutableMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(mutableMask);

            if (mMaskImage != null) {
                // draw the scaled bitmap with mask
                maskCanvas.drawBitmap(mMaskImage, 0, 0, new Paint());

                // paint the bitmap with mask into the result
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                mCanvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2f, (h - scaledBitmap.getHeight()) / 2f, null);
                mCanvas.drawBitmap(mutableMask, 0, 0, paint);
                paint.setXfermode(null);
            } else {    // draw the scaled bitmap with the back image as mask
                maskCanvas.drawBitmap(backImage, 0, 0, new Paint());

                // paint the bitmap with mask into the result
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                mCanvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2f, (h - scaledBitmap.getHeight()) / 2f, null);
                mCanvas.drawBitmap(mutableMask, 0, 0, paint);
                paint.setXfermode(null);

            }

            // paint the front
            if (mFrontImage != null) {
                mCanvas.drawBitmap(mFrontImage, 0, 0, null);
            }

            // store the bitmap in cache
//            BitmapCache.getInstance(mContext).putBitmap(key, result);

            // return it
            return result;
        }
    }

    private HashMap<String, IconPack> iconPacks = null;

    public HashMap<String, IconPack> getAvailableIconPacks(boolean forceReload) {
        if (iconPacks == null || forceReload) {
            iconPacks = new HashMap<>();

            // find apps with intent-filter "com.gau.go.launcherex.theme" and return build the HashMap
            PackageManager pm = mContext.getPackageManager();

            List<ResolveInfo> adwlauncherthemes = pm.queryIntentActivities(new Intent("org.adw.launcher.THEMES"), PackageManager.GET_META_DATA);
            List<ResolveInfo> golauncherthemes = pm.queryIntentActivities(new Intent("com.gau.go.launcherex.theme"), PackageManager.GET_META_DATA);

            // merge those lists
            List<ResolveInfo> rinfo = new ArrayList<>(adwlauncherthemes);
            rinfo.addAll(golauncherthemes);

            for (ResolveInfo ri : rinfo) {
                IconPack ip = new IconPack();
                ip.packageName = ri.activityInfo.packageName;

                ApplicationInfo ai;
                try {
                    ai = pm.getApplicationInfo(ip.packageName, PackageManager.GET_META_DATA);
                    ip.name = mContext.getPackageManager().getApplicationLabel(ai).toString();
                    iconPacks.put(ip.packageName, ip);
                } catch (PackageManager.NameNotFoundException e) {
                    // shouldn't happen
                    e.printStackTrace();
                }
            }
        }
        return iconPacks;
    }
}