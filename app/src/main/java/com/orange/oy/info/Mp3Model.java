package com.orange.oy.info;


import com.orange.oy.util.Player;
import com.orange.oy.view.RecodePlayView;

/**
 * Created by niut.l on 2017/5/5.
 */

public class Mp3Model {
    private String path;
    private Player player;

    public static int STATE_STOP = 0;
    public static int STATE_PLAY = 1;
    public static int STATE_PAUSE = 2;

    private RecodePlayView recodePlayView;

    public RecodePlayView getRecodePlayView() {
        return recodePlayView;
    }

    public Mp3Model setRecodePlayView(RecodePlayView recodePlayView) {
        this.recodePlayView = recodePlayView;
        return this;
    }

    private int CurrentState = 0;

    public int getCurrentState() {
        return CurrentState;
    }

    public void setCurrentState(int currentState) {
        CurrentState = currentState;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private int currentTime = 0;
    private int totelTime = 0;

    public int getTotelTime() {
        return totelTime;
    }

    public void setTotelTime(int totelTime) {
        this.totelTime = totelTime;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public Mp3Model(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
