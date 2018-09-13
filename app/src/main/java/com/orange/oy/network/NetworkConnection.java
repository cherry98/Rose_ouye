package com.orange.oy.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

abstract public class NetworkConnection {
    private static RequestQueue requestQueue;
    private Context context;
    private boolean isShowDialog = false;
    private String msg;
    private boolean isTimeCount = false;//true为网络在3秒内未加载出来，弹出提示
    private OnOutTimeListener onOutTimeListener;
    private HashMap<String, String> mapParams;

    public void setMapParams(HashMap<String, String> mapParams) {
        this.mapParams = mapParams;
    }

    public NetworkConnection(Context context) {
        this.context = context;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            requestQueue.start();
        }
    }

    private void addRequest(Request request) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            requestQueue.start();
        }
        requestQueue.add(request);
    }

    public void setIsShowDialog(boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
    }

    public void setTimeCount(boolean timeCount) {
        isTimeCount = timeCount;
    }

    public void sendPostRequest(String url, Response.Listener<String> success, Response.ErrorListener error, String
            message) {
        this.msg = message;
        if (isShowDialog)
            CustomProgressDialog.showProgressDialog(context, message);
        sendPostRequest(url, success, error);
    }

    public void sendPostRequest(String url, Response.Listener<String> success, Response.ErrorListener error) {
        sendPostRequest(url, success, error, 30000);
    }

    public void sendPostRequest(String url, Response.Listener<String> success, Response.ErrorListener error, int initialTimeoutMs) {
        if (isShowDialog && msg == null)
            CustomProgressDialog.showProgressDialog(context, null);
        if (isTimeCount) {
            checkTimeOut();
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, success, error) {
            protected Map<String, String> getParams() {
                Map<String, String> params = getNetworkParams();
                if (params == null) {
                    params = mapParams;
                }
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(context));
                params.put("mac", Tools.getLocalMacAddress(context));
                params.put("imei", Tools.getDeviceId(context));
                try {
                    params.put("versionnum", Tools.getVersionName(context));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("name", context.getResources().getString(R.string.app_name));
                params.put("resolution", Tools.getScreeInfoWidth(context) + "*" + Tools.getScreeInfoHeight(context));
                String usermobile = AppInfo.getName(context);
                params.put("newusermobile", usermobile);
                params.put("usermobile", usermobile);
                params.put("user_mobile", usermobile);
                return params;
            }
        };
        postRequest.setTag(url);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(initialTimeoutMs, DefaultRetryPolicy
                .DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(postRequest);
    }

    public void sendPostRequest(String url, Response.Listener<String> success, Response.ErrorListener error, boolean isAgain) {
        if (isShowDialog && msg == null)
            CustomProgressDialog.showProgressDialog(context, null);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, success, error) {
            protected Map<String, String> getParams() {
                Map<String, String> params = getNetworkParams();
                if (params == null) {
                    params = mapParams;
                }
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(context));
                params.put("mac", Tools.getLocalMacAddress(context));
                params.put("imei", Tools.getDeviceId(context));
                try {
                    params.put("versionnum", Tools.getVersionName(context));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("name", context.getResources().getString(R.string.app_name));
                params.put("resolution", Tools.getScreeInfoWidth(context) + "*" + Tools.getScreeInfoHeight(context));
                String usermobile = AppInfo.getName(context);
                params.put("newusermobile", usermobile);
                params.put("usermobile", usermobile);
                params.put("user_mobile", usermobile);
                return params;
            }
        };
        postRequest.setTag(url);
        //网络请求时间过长出现请求多次情况 maxNumRetries设为0无需重复调用
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(postRequest);
    }


    private void checkTimeOut() {
        if (handler != null)
            handler.sendEmptyMessageDelayed(1, 3000);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                onOutTimeListener.outTime();
                stopTimer();
            }
        }
    };

    public void upPostRequest(String url, Response.Listener<String> success, Response.ErrorListener error, String
            message) {
        if (isShowDialog)
            CustomProgressDialog.showProgressDialog(context, message);
        upPostRequest(url, success, error);
    }

    private String BOUNDARY = "--------------520-13-14"; //数据分隔线
    private String MULTIPART_FORM_DATA = "multipart/form-data";

    public void upPostRequest(String url, Response.Listener<String> success, Response.ErrorListener error) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, success, error) {
            protected Map<String, String> getParams() {
                Map<String, String> params = getNetworkParams();
                if (params == null) {
                    params = mapParams;
                }
                params.put("comname", Tools.getDeviceType());
                params.put("phonemodle", Tools.getDeviceModel());
                params.put("sysversion", Tools.getSystemVersion() + "");
                params.put("operator", Tools.getCarrieroperator(context));
                params.put("mac", Tools.getLocalMacAddress(context));
                params.put("imei", Tools.getDeviceId(context));
                try {
                    params.put("versionnum", Tools.getVersionName(context));
                } catch (PackageManager.NameNotFoundException e) {
                    params.put("versionnum", "not found");
                }
                params.put("name", context.getResources().getString(R.string.app_name));
                params.put("resolution", Tools.getScreeInfoWidth(context) + "*" + Tools.getScreeInfoHeight(context));
                params.put("newusermobile", AppInfo.getName(context));
                return params;
            }

            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String mString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(mString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }

            public String getBodyContentType() {
                return MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY;
            }

            public byte[] getBody() throws AuthFailureError {
                Map<String, String> map = getParams();
                if (map == null || map.isEmpty()) {
                    return super.getBody();
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                StringBuffer sb = new StringBuffer();
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    sb.append("--" + BOUNDARY);
                    sb.append("\r\n");
                    sb.append("Content-Disposition: form-data;");
                    sb.append(" name=\"");
                    sb.append(key);
                    sb.append("\"\r\n");
                    sb.append("\r\n");
                    sb.append(map.get(key) + "\r\n");
                    try {
                        bos.write(sb.toString().getBytes("utf-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String endLine = "--" + BOUNDARY + "--" + "\r\n";
                try {
                    bos.write(endLine.toString().getBytes("utf-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bos.toByteArray();
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        requestQueue.add(postRequest);
    }

    public void sendGetRequest(String url, Response.Listener<String> success, Response.ErrorListener error, String
            message) {
        if (isShowDialog)
            CustomProgressDialog.showProgressDialog(context, message);
        sendGetRequest(url, success, error);
    }

    public void sendGetRequest(String url, Response.Listener<String> success, Response.ErrorListener error) {
        String paramsString = "";
        Map<String, String> params = getNetworkParams();
        if (params == null) {
            params = mapParams;
        }
        if (params != null) {
            Iterator<String> keyIterator = params.keySet().iterator();
            String key, name;
            if (keyIterator.hasNext()) {
                key = keyIterator.next();
                name = params.get(key);
                if (paramsString.length() == 0) {
                    paramsString = "?" + key + "=" + name;
                } else {
                    paramsString = paramsString + "&" + key + "=" + name;
                }
            }
        }
        StringRequest getRequest = new StringRequest(Request.Method.GET, url + paramsString, success, error);
        getRequest.setTag(url + paramsString);
        getRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        addRequest(getRequest);
    }

    abstract public Map<String, String> getNetworkParams();

    public void stop(final String url) {
        if (requestQueue == null) {
            return;
        }
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            public boolean apply(Request<?> request) {
                Object tag = request.getTag();
                if (tag != null && url != null) {
                    return tag.equals(url);
                } else {
                    return false;
                }
            }
        });
        stopTimer();
    }

    /**
     * 关闭定时器
     */
    public void stopTimer() {
        if (handler != null)
            handler.removeMessages(1);
    }

    public static void stopNetwork() {
        if (requestQueue != null) {
            requestQueue.stop();
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
            requestQueue = null;
        }
    }

    public interface OnOutTimeListener {
        void outTime();
    }

    public void setOnOutTimeListener(OnOutTimeListener onOutTimeListener) {
        this.onOutTimeListener = onOutTimeListener;
    }
}
