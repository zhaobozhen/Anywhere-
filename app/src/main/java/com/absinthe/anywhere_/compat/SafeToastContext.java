package com.absinthe.anywhere_.compat;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import timber.log.Timber;

/**
 * @author drakeet
 */
final class SafeToastContext extends ContextWrapper {


  SafeToastContext(@NonNull Context base, @NonNull Toast toast) {
    super(base);
  }


  @Override
  public Context getApplicationContext() {
    return new ApplicationContextWrapper(getBaseContext().getApplicationContext());
  }


  private static final class ApplicationContextWrapper extends ContextWrapper {

    private ApplicationContextWrapper(@NonNull Context base) {
      super(base);
    }


    @Override
    public Object getSystemService(@NonNull String name) {
      if (Context.WINDOW_SERVICE.equals(name)) {
        return new WindowManagerWrapper((WindowManager) getBaseContext().getSystemService(name));
      }
      return super.getSystemService(name);
    }
  }


  private static final class WindowManagerWrapper implements WindowManager {

    private static final String TAG = "WindowManagerWrapper";
    private final @NonNull
    WindowManager base;


    private WindowManagerWrapper(@NonNull WindowManager base) {
      this.base = base;
    }


    @Override
    public Display getDefaultDisplay() {
      return base.getDefaultDisplay();
    }


    @Override
    public void removeViewImmediate(View view) {
      base.removeViewImmediate(view);
    }


    @Override
    public void addView(View view, ViewGroup.LayoutParams params) {
      try {
        Timber.tag(TAG).d("WindowManager's addView(view, params) has been hooked.");
        base.addView(view, params);
      } catch (BadTokenException e) {
        Timber.tag(TAG).i(e);
      } catch (Throwable throwable) {
        Timber.tag(TAG).e(throwable, "[addView]");
      }
    }


    @Override
    public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
      base.updateViewLayout(view, params);
    }


    @Override
    public void removeView(View view) {
      base.removeView(view);
    }
  }
}
