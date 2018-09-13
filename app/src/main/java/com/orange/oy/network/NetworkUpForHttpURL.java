package com.orange.oy.network;

import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/11.
 * 自定义网络请求
 */

public class NetworkUpForHttpURL {
    private NetworkUpForHttpURL() {
    }

    public interface OnNetworkUpForHttpURLListener {
        void success(String result);

        void fail(Object object);
    }

    private OnNetworkUpForHttpURLListener onNetworkUpForHttpURLListener;

    public void setOnNetworkUpForHttpURLListener(OnNetworkUpForHttpURLListener onNetworkUpForHttpURLListener) {
        this.onNetworkUpForHttpURLListener = onNetworkUpForHttpURLListener;
    }

    private ArrayList<SendHttpAsyncTask> sendHttpAsyncTasks = new ArrayList<>();
    private static NetworkUpForHttpURL networkUpForHttpURL;
    private boolean isAgain = false;//是否允许重复访问，默认禁止,此参数只对异步访问有效

    public boolean isAgain() {
        return isAgain;
    }

    public void setAgain(boolean again) {
        this.isAgain = again;
    }

    public static NetworkUpForHttpURL getNetworkUpForHttpURL() {
        if (networkUpForHttpURL == null) {
            networkUpForHttpURL = new NetworkUpForHttpURL();
        }
        return networkUpForHttpURL;
    }

    /**
     * 通过自定义的唯一标识，结束第一个此tag的访问
     *
     * @param tag
     */
    public void stopHttp(Object tag) {
        for (SendHttpAsyncTask sendHttpAsyncTask : sendHttpAsyncTasks) {
            if (sendHttpAsyncTask.getTag().equals(tag)) {
                sendHttpAsyncTask.stopHttp();
                break;
            }
        }
    }

    /**
     * 发送异步post请求
     *
     * @param url
     * @param parameter
     */
    public void sendPostAsync(String url, String parameter, OnNetworkUpForHttpURLListener onNetworkUpForHttpURLListener) {
        if (!isAgain && havAgainUrl(url)) {
            return;
        }
        SendHttpAsyncTask sendHttpAsyncTask = new SendHttpAsyncTask(url, parameter, sendHttpAsyncTasks.size());
        sendHttpAsyncTasks.add(sendHttpAsyncTask);
        this.onNetworkUpForHttpURLListener = onNetworkUpForHttpURLListener;
        sendHttpAsyncTask.execute();
    }

    /**
     * 发送同步post请求
     *
     * @param url
     * @param parameter
     * @return 返回值两种：Exception类型为异常；String类型为返回值
     */
    public Object sendPost(String url, String parameter) {
        SendHttpConnection sendHttpConnection = new SendHttpConnection(url, parameter);
        return sendHttpConnection.sendHttp();
    }

    /**
     * 发送同步get请求
     *
     * @param url
     * @param parameter
     * @return 返回值两种：Exception类型为异常；String类型为返回值
     */
    public Object sendGet(String url) {
        SendHttpConnection sendHttpConnection = new SendHttpConnection(url, "");
        sendHttpConnection.setRequestMethod("GET");
        sendHttpConnection.setTimeout(2000);
        return sendHttpConnection.sendHttp();
    }

    private boolean havAgainUrl(String url) {
        for (SendHttpAsyncTask sendHttpAsyncTask : sendHttpAsyncTasks) {
            if (sendHttpAsyncTask.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 异步请求
     */
    private class SendHttpAsyncTask extends AsyncTask {
        Object tag;
        String url;
        String parameter;
        int num;
        SendHttpConnection sendHttpConnection;

        SendHttpAsyncTask(String url, String parameter, int num) {
            this.url = url;
            this.parameter = parameter;
            this.num = num;
        }

        void stopHttp() {
            if (sendHttpConnection != null) {
                sendHttpConnection.stopHttp();
                sendHttpConnection = null;
            }
        }

        public String getUrl() {
            return url;
        }

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

        protected Object doInBackground(Object[] params) {
            sendHttpConnection = new SendHttpConnection(url, parameter);
            return sendHttpConnection.sendHttp();
        }

        protected void onPostExecute(Object object) {
            if (sendHttpConnection != null) {
                if (object != null) {
                    if (object instanceof Exception) {
                        if (onNetworkUpForHttpURLListener != null) {
                            onNetworkUpForHttpURLListener.fail(object);
                        }
                    } else {
                        if (onNetworkUpForHttpURLListener != null) {
                            onNetworkUpForHttpURLListener.success(object + "");
                        }
                    }
                } else {
                    if (onNetworkUpForHttpURLListener != null) {
                        onNetworkUpForHttpURLListener.fail(null);
                    }
                }
            }
            if (sendHttpAsyncTasks != null) {
                sendHttpAsyncTasks.remove(this);
            }
        }
    }
}
