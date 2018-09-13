package com.orange.oy.allinterface;

import com.orange.oy.info.TaskQuestionInfo;

/**
 * 任务-单题模式提交按钮
 */
public interface OnTaskQuestionSumbitListener {
    void sumbit(TaskQuestionInfo[] answers, String[] notes);
}
