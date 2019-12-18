package com.absinthe.anywhere_.builder;

import android.view.View;

interface IViewBuilder {

    void init();

    void addView(View view);

    void removeView(View view);
}
