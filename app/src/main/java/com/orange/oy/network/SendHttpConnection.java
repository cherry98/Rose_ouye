package com.orange.oy.network;

import android.text.TextUtils;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2018/6/11.
 */

class SendHttpConnection {
    private String url = "";
    private String parameter;
    private HttpURLConnection httpURLConnection = null;
    private String requestMethod = "POST";
    private int timeout = 10000;

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    SendHttpConnection(String url, String parameter) {
        this.url = url;
        this.parameter = parameter;
    }

    /**
     * 请求方式默认POST
     * 超时时间默认10s
     *
     * @return
     */
    Object sendHttp() {
        InputStream is = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        URL url = null;
        String result = null;
        Exception exception = null;
        try {
            url = new URL(this.url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            settingHttpURLConnection(httpURLConnection);
            if (requestMethod.equals("POST")) {
                os = httpURLConnection.getOutputStream();
                byte[] bs = parameter.getBytes("UTF-8");
                os.write(bs);
                os.flush();
                os.close();
                os = null;
            } else {
                httpURLConnection.connect();
            }
            is = httpURLConnection.getInputStream();
            bis = new BufferedInputStream(is);
            byte[] bytes = new byte[1024 * 8];
            int index;
            while ((index = bis.read(bytes)) > 0) {
                if (TextUtils.isEmpty(result)) {
                    result = new String(bytes, 0, index, "utf-8");
                } else {
                    result = result + new String(bytes, 0, index, "utf-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        } finally {
            IOUtils.safeClose(bis);
            IOUtils.safeClose(is);
            IOUtils.safeClose(os);
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return (result == null) ? exception : result;
    }

    void stopHttp() {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
            httpURLConnection = null;
        }
    }

    /**
     * 设置httpurlconnection
     *
     * @param httpURLConnection
     * @throws ProtocolException
     */
    private void settingHttpURLConnection(HttpURLConnection httpURLConnection) throws ProtocolException {
        if (requestMethod.equals("POST")) {
            httpURLConnection.setChunkedStreamingMode(1280 * 1024);// 128K
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        }
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod(requestMethod);
        httpURLConnection.setConnectTimeout(timeout);
        httpURLConnection.setReadTimeout(timeout);
        httpURLConnection.setUseCaches(false);
    }
}
