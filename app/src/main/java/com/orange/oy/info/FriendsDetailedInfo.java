package com.orange.oy.info;

/**
 * Created by Lenovo on 2018/3/6.
 */

public class FriendsDetailedInfo {

    /**
     * id : 2
     * usersMobileId : 1322
     * friendMobile : 13081891499
     * createTime : 2018-03-05 16:14:36
     * state : 0
     * stateStr : 身份信息未绑定
     */

    private String id;
    private String usersMobileId;
    private String friendMobile;
    private String createTime;
    private String state;
    private String stateStr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsersMobileId() {
        return usersMobileId;
    }

    public void setUsersMobileId(String usersMobileId) {
        this.usersMobileId = usersMobileId;
    }

    public String getFriendMobile() {
        return friendMobile;
    }

    public void setFriendMobile(String friendMobile) {
        this.friendMobile = friendMobile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateStr() {
        return stateStr;
    }

    public void setStateStr(String stateStr) {
        this.stateStr = stateStr;
    }
}
