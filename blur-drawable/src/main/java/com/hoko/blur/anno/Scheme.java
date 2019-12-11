package com.hoko.blur.anno;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hoko.blur.drawable.BlurDrawable.SCHEME_JAVA;
import static com.hoko.blur.drawable.BlurDrawable.SCHEME_NATIVE;
import static com.hoko.blur.drawable.BlurDrawable.SCHEME_OPENGL;
import static com.hoko.blur.drawable.BlurDrawable.SCHEME_RENDER_SCRIPT;

/**
 * Created by yuxfzju on 2017/2/9.
 */

@IntDef({SCHEME_RENDER_SCRIPT, SCHEME_OPENGL, SCHEME_NATIVE, SCHEME_JAVA})
@Retention(RetentionPolicy.SOURCE)
public @interface Scheme {
}
