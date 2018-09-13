package com.orange.oy.allinterface;


import com.orange.oy.view.ObservableScrollView;

/**
 * author：shixinxin on 2017/2/21
 * version：v1.0
 */
public interface ScrollViewListener {

    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}
