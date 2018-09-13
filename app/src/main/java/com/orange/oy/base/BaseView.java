package com.orange.oy.base;

import java.util.Objects;

/**
 * Created by Administrator on 2018/4/3.
 */

public interface BaseView {
    void onResume(Object object);

    void onPause(Object object);

    void onStop(Object object);

    void onDestory(Object object);

    Object getBaseData();
}
