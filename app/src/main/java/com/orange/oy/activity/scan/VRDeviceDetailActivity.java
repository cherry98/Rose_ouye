package com.orange.oy.activity.scan;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.fragment.MyFragment;
import com.orange.oy.service.VRService;
import com.orange.oy.view.AppTitle;
import com.zmer.zmersainuo.common.CommConst;
import com.zmer.zmersainuo.entity.VideoInfoEntity;
import com.zmer.zmersainuo.interfaces.DeviceOnlineCheckInterface;
import com.zmer.zmersainuo.interfaces.DeviceRecordVideoInterface;
import com.zmer.zmersainuo.interfaces.DeviceRecordingCheckInterface;
import com.zmer.zmersainuo.interfaces.DeviceResourceOperationInterface;
import com.zmer.zmersainuo.interfaces.DeviceStatusInterface;
import com.zmer.zmersainuo.utils.ControllZmerDevice;
import com.zmer.zmersainuo.utils.GetZmerResourceInfos;
import com.zmer.zmersainuo.utils.Log;
import com.zmer.zmersainuo.utils.WifiUtil;
import com.zmer.zmersainuo.utils.sputils.SharedPreferencesUtil;

import java.util.List;

import tv.danmaku.ijk.media.player.AvClipCore;

/**
 * VR设备状态页
 */
public class VRDeviceDetailActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        DeviceRecordVideoInterface, DeviceRecordingCheckInterface,
        View.OnClickListener {

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.vrdevice_title);
        appTitle.settingName("VR设备状态");
        appTitle.showBack(this);
    }

    private TextView vrdevice_id, vrdevice_memory, vrdevice_electric, vrdevice_online,
            vrdevice_videostate, vrdevice_videonumber, vrdevice_picnumber;
    private ControllZmerDevice controllZmerDevice;
    private final String TAG = VRDeviceDetailActivity.class.getSimpleName();
    private Button vrdevice_sync, vrdevice_nosync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vrdevice_detail);
        initTitle();
        vrdevice_id = (TextView) findViewById(R.id.vrdevice_id);
        vrdevice_memory = (TextView) findViewById(R.id.vrdevice_memory);
        vrdevice_electric = (TextView) findViewById(R.id.vrdevice_electric);
        vrdevice_online = (TextView) findViewById(R.id.vrdevice_online);
        vrdevice_videostate = (TextView) findViewById(R.id.vrdevice_videostate);
        vrdevice_videonumber = (TextView) findViewById(R.id.vrdevice_videonumber);
        vrdevice_picnumber = (TextView) findViewById(R.id.vrdevice_picnumber);
        vrdevice_sync = (Button) findViewById(R.id.vrdevice_sync);
        vrdevice_sync.setOnClickListener(this);
        vrdevice_nosync = (Button) findViewById(R.id.vrdevice_nosync);
        vrdevice_nosync.setOnClickListener(this);
        if (VRService.thread == null) {
            vrdevice_sync.getBackground().setAlpha(255);
            vrdevice_nosync.getBackground().setAlpha(100);
        } else {
            vrdevice_sync.getBackground().setAlpha(100);
            vrdevice_nosync.getBackground().setAlpha(255);
        }
        vrdevice_id.setText(getIntent().getStringExtra("vrid"));
        controllZmerDevice = ControllZmerDevice.getControllZmerDeviceInstance();
        getIsOnLine();
        getDeviceId();
        getBatteryInfos();
        getZmerSDCardStorage();
        getPictureFileNums();
        controllZmerDevice.checkDeviceIsRecording(this);
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    /**
     * 获取VR设备id
     */
    private void getDeviceId() {
        controllZmerDevice.getDeviceInfo(new DeviceStatusInterface() {
            @Override
            public void getDeviceStatus(final int status, final String deviceInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (!vrdevice_id.toString().equals(deviceInfo)) {//如果设备ID不同 弹窗提醒
//                            ConfirmDialog.showDialogForHint(VRDeviceDetailActivity.this, "您输入的设备ID有误");
//                        }
                    }
                });
            }

            @Override
            public void getDeviceStatusError(final int status, final String errorInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }, 1);
    }

    /**
     * 获取VR电量
     */
    private void getBatteryInfos() {
        controllZmerDevice.getDeviceInfo(new DeviceStatusInterface() {
            @Override
            public void getDeviceStatus(final int status, final String deviceInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vrdevice_electric.setText(deviceInfo);
                    }
                });
            }

            @Override
            public void getDeviceStatusError(final int status, final String errorInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vrdevice_electric.setText(errorInfo);
                    }
                });
            }
        }, 2);
    }

    /**
     * 获取VR内存
     */
    private void getZmerSDCardStorage() {
        controllZmerDevice.getDeviceInfo(new DeviceStatusInterface() {
            @Override
            public void getDeviceStatus(final int status, final String deviceInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vrdevice_memory.setText(deviceInfo);
                    }
                });
            }

            @Override
            public void getDeviceStatusError(final int status, final String errorInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vrdevice_memory.setText(errorInfo);
                    }
                });
            }
        }, 3);
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
                @Override
                public void deviceIsOnLine(final int status, final String ipAddress, String ssid) {
//                    0：ZMER已自动连接wifi，当前手机直连ZMER
//                    1：ZMR已自动连接到wifi，手机也已连接到该wifi。
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == 1 || status == 0) {
                                if (status == 1) {
                                    controllZmerDevice.updateDeviceIpAddress(ipAddress);
                                }
                                vrdevice_online.setText("在线");
                            } else {
                                vrdevice_online.setText("不在线");
                            }
                        }
                    });

                }

                @Override
                public void deviceIsOffLine(int status) {
//                    1：网络不可用
//                    2：手机未连接wifi
//                    3：ZMER未在线，手机直连ZMER
//                    4、手机直连ZMER，返回异常
//                    5、ZMER未在线，手机已连接其他wifi。
                }
            });
        }
    }

    GetZmerResourceInfos getZmerResourceInfos;

    /**
     * 获取拍摄照片张数
     */
    private void getPictureFileNums() {
//        getZmerResourceInfos.getPictureNumbers();
        String ip = controllZmerDevice.getIpAddress();
        if (ip != null) {
            getZmerResourceInfos = GetZmerResourceInfos.getZmerResourceInfos(ip, new DeviceResourceOperationInterface() {
                @Override
                public void onGetFileNumsSuccess(boolean isVideoFile, int size, List<String> filePaths) {
                    vrdevice_picnumber.setText(size + "张");
                    getFileNumbers();
                }

                @Override
                public void onGetVideoDurationsSuccess(List<VideoInfoEntity> list) {

                }

                @Override
                public void onOperationFailed(int errorOpearation, String errorMsg) {

                }

                @Override
                public void onRenameSuccess() {

                }

                @Override
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
                @Override
                public void onGetFileNumsSuccess(boolean isVideoFile, int size, List<String> filePaths) {
                    vrdevice_videonumber.setText(size + "段");
                }

                @Override
                public void onGetVideoDurationsSuccess(List<VideoInfoEntity> list) {

                }

                @Override
                public void onOperationFailed(int errorOpearation, String errorMsg) {

                }

                @Override
                public void onRenameSuccess() {

                }

                @Override
                public void onCheckFileNoExistenceSuccess() {

                }
            });
            getZmerResourceInfos.initWebDav(this);
            getZmerResourceInfos.getVideoNumbers();
        }
    }

    @Override
    public void operationSuccess(final boolean isRecordVideo, final boolean bStart, final String videopath) {
        Log.d(TAG, "videopath: " + videopath + ",recordStatus: " + bStart + ",isRecordVide: " + isRecordVideo);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vrdevice_picnumber.setText("isRecordVideo: " + isRecordVideo + ",bStart:" + bStart + ",videopath: " + videopath);
            }
        });
        if (!bStart && !videopath.equals("")) {
            int result = new AvClipCore().getFrame(videopath, 3, "/sdcard/" + SystemClock.uptimeMillis() + ".jpg");
            Log.d(TAG, "result: " + result);
        }
    }

    @Override
    public void operationFailed(final boolean isRecordVideo, final int status, final String errorMsg) {
        Log.d(TAG, "isRecordVideo: " + isRecordVideo + "status: " + status + ",errorMsg:  " + errorMsg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vrdevice_picnumber.setText("isRecordVideo: " + isRecordVideo + "status: " + status + ",errorMsg:" + errorMsg);
            }
        });
    }

    @Override
    public void deviceIsRecording(String videoPath, final boolean isRecording) {
//        videoPath：录制结束或未录制，返回最近一次视频路径
//        isRecording：
//        true:正在录制视频
//        false:未录制视频
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    vrdevice_videostate.setText("正在录制");
                } else {
                    vrdevice_videostate.setText("停止录制");
                }
            }
        });
    }

    @Override
    public void deviceError(final int status, String ipAddress) {
//        Status:
//        1：IP地址错误或网络错误
//        2：ZMER返回值错误
        runOnUiThread(new Runnable() {
            public void run() {
                if (status == 1) {
                    vrdevice_videostate.setText("IP地址错误或网络错误");
                } else if (status == 2) {
                    vrdevice_videostate.setText("ZMER返回值错误");
                }
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.vrdevice_sync) {
            MyFragment.isRefresh = true;
            vrdevice_sync.getBackground().setAlpha(100);
            vrdevice_nosync.getBackground().setAlpha(255);
            Intent intent = new Intent("com.orange.oy.VRService");
            intent.setPackage("com.orange.oy");
            startService(intent);
        } else if (v.getId() == R.id.vrdevice_nosync) {
            MyFragment.isRefresh = true;
            vrdevice_sync.getBackground().setAlpha(255);
            vrdevice_nosync.getBackground().setAlpha(100);
            Intent intent = new Intent("com.orange.oy.VRService");
            intent.setPackage("com.orange.oy");
            stopService(intent);
        }
    }
}
