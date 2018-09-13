package com.orange.oy.info;

import com.orange.oy.allinterface.IType;

/**
 * Created by xiedongyan on 2017/5/25.
 * 左边显示内容
 */

public class Message1 implements IType {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getType() {
        return 0;
    }
}
