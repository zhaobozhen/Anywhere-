package com.hoko.blur.anno;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hoko.blur.drawable.BlurDrawable.BOTH;
import static com.hoko.blur.drawable.BlurDrawable.HORIZONTAL;
import static com.hoko.blur.drawable.BlurDrawable.VERTICAL;

/**
 * Created by yuxfzju on 2017/2/20.
 */

@IntDef({HORIZONTAL, VERTICAL, BOTH})
@Retention(RetentionPolicy.SOURCE)
public @interface Direction {
}
