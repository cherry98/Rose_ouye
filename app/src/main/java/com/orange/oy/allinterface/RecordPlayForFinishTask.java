package com.orange.oy.allinterface;

/**
 * Created by Administrator on 2018/4/1.
 */

public interface RecordPlayForFinishTask {

    void startPlaying();

    void stopPlaying();

    int getCurrentPosition();

    int getDuration();

    void seekTo(int seek);

    void setProgressFroRecord(int progress);

    boolean isPlaying();

    void setTime(String time);

    void onFinishView();
}
