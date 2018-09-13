package com.handmark.pulltorefresh.library.internal;

public interface SlideTouchEventListener {
    void onTouchEvent(int slide);

    void onTouchUp();

    void onTouchMove(int sx, int sy);

    void onTouchDown(int x, int y);
}
