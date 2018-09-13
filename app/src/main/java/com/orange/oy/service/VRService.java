package com.orange.oy.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.scan.VRDeviceDetailActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.zmer.zmersainuo.common.CommConst;
import com.zmer.zmersainuo.entity.VideoInfoEntity;
import com.zmer.zmersainuo.interfaces.DeviceOnlineCheckInterface;
import com.zmer.zmersainuo.interfaces.DeviceRecordVideoInterface;
import com.zmer.zmersainuo.interfaces.DeviceRecordingCheckInterface;
import com.zmer.zmersainuo.interfaces.DeviceResourceOperationInterface;
import com.zmer.zmersainuo.interfaces.DeviceStatusInterface;
import com.zmer.zmersainuo.utils.ControllZmerDevice;
import com.zmer.zmersainuo.utils.GetZmerResourceInfos;
import com.zmer.zmersainuo.utils.WifiUtil;
import com.zmer.zmersainuo.utils.sputils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VR设备信息上传服务
 */
public class VRService extends Service implements DeviceRecordingCheckInterface, Runnable {
    public VRService() {
    }

    private NetworkConnection vRState;
    private ControllZmerDevice controllZmerDevice;
    private GetZmerResourceInfos getZmerResourceInfos;
    private final String TAG = VRDeviceDetailActivity.class.getSimpleName();

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public static Thread thread;
    private static String vrid, memory, electricity, is_online, record_state, video_num, photo_num;

    private void initNetworkConnection() {
        vRState = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(VRService.this));
                params.put("vrid", checkStr(vrid));
                params.put("memory", checkStr(memory));
                params.put("electricity", checkStr(electricity));
                params.put("is_online", checkStr(is_online));
                params.put("record_state", checkStr(record_state));
                params.put("video_num", checkStr(video_num));
                params.put("photo_num", checkStr(photo_num));
                return params;
            }
        };
    }

    private String checkStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str;
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        controllZmerDevice = ControllZmerDevice.getControllZmerDeviceInstance();
        Tools.d("vrService start");
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private static boolean bStart = false; //录制视频控制回调
    private DeviceRecordVideoInterface RecordVideoInterface = new DeviceRecordVideoInterface() {
        //        isRecordVideo：
//                true:录制视频操作
//        false:拍摄照片操作
//        bStart：
//                true:正在录制视频
//        false:结束视频录制
//        videopath：结束录制视频，返回值为视频路径
        public void operationSuccess(boolean isRecordVideo, boolean b1, String s) {
            Tools.d("isRecordVideo:" + isRecordVideo + ",b1:" + b1 + ",s:" + s);
            if (isRecordVideo) {
                bStart = b1;
                if (!b1) {
                    if (thread == null) {
                        isSaving = false;
                        controllZmerDevice.recordVideo(RecordVideoInterface, false, 3);
                        thread = new Thread(VRService.this);
                        thread.start();
                    }
                }
            }
        }

        public void operationFailed(boolean b, int i, String s) {

        }
    };
    private static boolean isSaving = false;//是否正在存储
    private static final int sleepTime = 2000;//线程刷新时间2s

    public void run() {
        try {
            for (int i = 20000, j = 0; thread != null; i += sleepTime, j += sleepTime) {
                Tools.d("VRService>>>> i:" + i + ",j:" + j);
                if (i == 30000) {
                    i = 0;
                    send();
                } else if (i == 20000) {
                    getIsOnLine();//是否在线
                    getDeviceId();//id
                    getBatteryInfos();//电量
                    getZmerSDCardStorage();//内存
                    controllZmerDevice.checkDeviceIsRecording(VRService.this);//录制状态
                    getPictureFileNums();//照片数量
                } else if (j > 600000) {//10分钟
                    j = 0;
//                    do {
//                        if (bStart) {//录制已开
//                            Tools.d("开始保存。。。。。");
//                            controllZmerDevice.recordVideo(RecordVideoInterface, true, 3);
//                            isSaving = true;
//                            Thread.sleep(10000);
//                        } else if (isSaving) {
//                            isSaving = false;
//                            controllZmerDevice.recordVideo(RecordVideoInterface, false, 3);
//                            Tools.d("保存结束。。。");
//                        }
//                    } while (isSaving);
                    if (bStart) {//录制已开
                        Tools.d("开始保存。。。。。");
                        controllZmerDevice.recordVideo(RecordVideoInterface, true, 3);
                        int index = 0;
                        while (index < 5) {
                            Thread.sleep(sleepTime);
                            index++;
                        }
                        controllZmerDevice.recordVideo(RecordVideoInterface, false, 3);
                        Tools.d("保存结束。。。");
                    }
                }
                Thread.sleep(sleepTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    private void send() {
        if (thread == null) {
            return;
        }
        if (vRState == null) {
            initNetworkConnection();
        }
        Tools.d("-----zuivr" + vrid + memory + electricity + is_online + video_num + photo_num + record_state);
        sendData();
    }

    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
        if (vRState != null)
            vRState.stop(Urls.VRState);
        thread = null;
    }

    private CharSequence contentTitle = "正在同步";
    private CharSequence contentText = "";

    private void startNotification() {
        Notification notification;
        int icon = R.mipmap.ic_launcher;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            CharSequence tickerText = this.getResources().getString(R.string.app_name);
            Notification.Builder builder = new Notification.Builder(this).setTicker(tickerText).setSmallIcon(icon);
            Intent i = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = builder.setContentIntent(pendingIntent).setContentTitle(contentTitle).setContentText
                    (contentText).build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(2016, notification);
        } else {
            CharSequence tickerText = this.getResources().getString(R.string.app_name);
            Notification.Builder builder = new Notification.Builder(this).setTicker(tickerText).setSmallIcon(icon);
            builder.setContentTitle(contentTitle);
            builder.setContentText(contentText);
            notification = builder.getNotification();
            notification.flags = Notification.FLAG_NO_CLEAR;
            startForeground(2016, notification);
        }
    }

    private void sendData() {
        vRState.sendPostRequest(Urls.VRState, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d("=====s" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(VRService.this, "同步成功");
                    } else {
                        Tools.showToast(VRService.this, "同步失败");
                    }
                } catch (JSONException e) {
                    Tools.showToast(VRService.this, "同步失败 code2");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(VRService.this, "同步失败，请检查网络");
            }
        }, null);
    }

    /**
     * 获取拍摄照片张数
     */
    private void getPictureFileNums() {
//        getZmerResourceInfos.getPictureNumbers();
        String ip = controllZmerDevice.getIpAddress();
        if (ip != null) {
            getZmerResourceInfos = GetZmerResourceInfos.getZmerResourceInfos(ip, new DeviceResourceOperationInterface() {
                public void onGetFileNumsSuccess(boolean isVideoFile, int size, List<String> filePaths) {
                    photo_num = size + "";
                    Tools.d("获取拍摄照片张数" + photo_num);
                    getFileNumbers();
                }

                public void onGetVideoDurationsSuccess(List<VideoInfoEntity> list) {
                }

                public void onOperationFailed(int errorOpearation, String errorMsg) {
                }

                public void onRenameSuccess() {
                }

                public void onCheckFileNoExistenceSuccess() {
                }
            });
            getZmerResourceInfos.initWebDav(this);
            getZmerResourceInfos.getPictureNumbers();
        }
    }

    /**
     * 获取视频个数
     */
    private void getFileNumbers() {

//        getZmerResourceInfos.getVideoNumbers();
        String ip = controllZmerDevice.getIpAddress();
        if (ip != null) {
            getZmerResourceInfos = GetZmerResourceInfos.getZmerResourceInfos(ip, new DeviceResourceOperationInterface() {

                public void onGetFileNumsSuccess(boolean isVideoFile, int size, List<String> filePaths) {
                    video_num = size + "";
                    Tools.d("获取视频个数:" + video_num);
                }


                public void onGetVideoDurationsSuccess(List<VideoInfoEntity> list) {

                }


                public void onOperationFailed(int errorOpearation, String errorMsg) {

                }

                public void onRenameSuccess() {

                }


                public void onCheckFileNoExistenceSuccess() {

                }
            });
            getZmerResourceInfos.initWebDav(this);
            getZmerResourceInfos.getVideoNumbers();
        }
    }

    /**
     * 获取VR设备id
     */
    private void getDeviceId() {
        controllZmerDevice.getDeviceInfo(new DeviceStatusInterface() {
            public void getDeviceStatus(int status, String deviceInfo) {
                vrid = deviceInfo;
                Tools.d("获取VR设备id:" + vrid);
            }

            public void getDeviceStatusError(final int status, final String errorInfo) {
            }
        }, 1);
    }

    /**
     * 获取VR电量
     */
    private void getBatteryInfos() {
        controllZmerDevice.getDeviceInfo(new DeviceStatusInterface() {
            public void getDeviceStatus(final int status, String deviceInfo) {
                electricity = deviceInfo;
                Tools.d("获取VR电量:" + electricity);
            }

            public void getDeviceStatusError(final int status, String errorInfo) {
                electricity = "信息获取失败" + status;
                Tools.d("获取VR电量:" + electricity);
            }
        }, 2);
    }

    /**
     * 获取VR内存
     */
    private void getZmerSDCardStorage() {
        controllZmerDevice.getDeviceInfo(new DeviceStatusInterface() {
            public void getDeviceStatus(final int status, String deviceInfo) {
                memory = deviceInfo;
                Tools.d("获取VR内存:" + memory);
            }

            public void getDeviceStatusError(final int status, final String errorInfo) {
                memory = "信息获取失败" + status;
                Tools.d("获取VR内存:" + memory);
            }
        }, 3);
    }


    public void deviceIsRecording(String videoPath, boolean isRecording) {
        bStart = isRecording;
        if (isRecording) {
            record_state = "正在录制";
            Tools.d(record_state);
        } else {
            record_state = "停止录制";
            Tools.d(record_state);
        }
    }

    public void deviceError(int status, String ipAddress) {
        record_state = "信息获取失败" + status;
        Tools.d(record_state);
//        if (status == 1) {
//            record_state = "IP地址错误或网络错误";
//        } else if (status == 2) {
//            record_state = "ZMER返回值错误";
//        }
    }

    private String mIpAddress = "192.168.21.3";

    public void getIsOnLine() {
        if (controllZmerDevice != null) {
            final SharedPreferencesUtil sp = SharedPreferencesUtil.getInstance(this);
            String wifiName = WifiUtil.getWifiName(this);
            final String connectWifi = sp.getString(CommConst.wifi + wifiName, "");
            boolean hasConnectWifi = false;
            if (connectWifi.equals(wifiName)) {
                hasConnectWifi = true;
                mIpAddress = sp.getString(CommConst.ip + connectWifi, "");
            } else {
                hasConnectWifi = !TextUtils.isEmpty(wifiName) && !wifiName.startsWith("ZMER");
            }
            controllZmerDevice.checkDeviceIsConnected(this, hasConnectWifi, mIpAddress, new DeviceOnlineCheckInterface() {
                public void deviceIsOnLine(int status, String ipAddress, String ssid) {
//                    0：ZMER已自动连接wifi，当前手机直连ZMER
//                    1：ZMR已自动连接到wifi，手机也已连接到该wifi。
                    if (status == 1 || status == 0) {
                        if (status == 1) {
                            controllZmerDevice.updateDeviceIpAddress(ipAddress);
                        }
                        is_online = "在线";
                        Tools.d(is_online);
                    } else {
                        is_online = "不在线";
                        Tools.showToast(VRService.this, "您的VR设备已掉线");
                        Tools.d(is_online);
                    }

                }

                public void deviceIsOffLine(int status) {
                    is_online = "信息获取失败" + status;
                    Tools.showToast(VRService.this, "您的VR设备已掉线");
                    Tools.d(is_online);
//                    1：网络不可用
//                    2：手机未连接wifi
//                    3：ZMER未在线，手机直连ZMER
//                    4、手机直连ZMER，返回异常
//                    5、ZMER未在线，手机已连接其他wifi。
                }
            });
        }
    }

}
