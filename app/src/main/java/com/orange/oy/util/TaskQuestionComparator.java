package com.orange.oy.util;

import com.orange.oy.info.TaskEditInfo;

import java.util.Comparator;

public class TaskQuestionComparator implements Comparator<TaskEditInfo> {
    public int compare(TaskEditInfo lhs, TaskEditInfo rhs) {
        return lhs.getQuestion_num() - rhs.getQuestion_num();
    }
}
