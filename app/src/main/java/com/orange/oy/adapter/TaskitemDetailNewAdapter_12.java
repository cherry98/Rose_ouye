package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.SlideTouchEventListener;
import com.orange.oy.R;
import com.orange.oy.activity.CloseTaskitemPhotographyNextYActivity;
import com.orange.oy.activity.CloseTaskitemShotActivity;
import com.orange.oy.activity.TaskitemDetailActivity_12;
import com.orange.oy.allinterface.NewOnItemClickListener;
import com.orange.oy.allinterface.OfflineStoreClickViewListener;
import com.orange.oy.allinterface.OnCloseTaskListener;
import com.orange.oy.allinterface.OnRefreshListener;
import com.orange.oy.allinterface.OnRightClickListener;
import com.orange.oy.allinterface.PullToRefreshDeleteListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.TaskitemDetail_12View;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TaskitemDetailNewAdapter_12 extends BaseAdapter implements OfflineStoreClickViewListener,
        PullToRefreshDeleteListener {
    private ArrayList<TaskitemDetailNewInfo> list;
    private Context context;
    private UpdataDBHelper updataDBHelper;
    private String photo_compression, codeStr, brand, is_watermark;
    private PullToRefreshListView listView;
    private boolean isShowProgressbar = false;//是否显示进度条
    private String index;

    public TaskitemDetailNewAdapter_12(Context context, PullToRefreshListView listView, ArrayList<TaskitemDetailNewInfo> list, String index) {
        this.listView = listView;
        this.context = context;
        this.list = list;
        this.index = index;
        updataDBHelper = new UpdataDBHelper(context);
        Closepackage = new NetworkConnection(context) {
            public Map<String, String> getNetworkParams() {
                if (closeMap == null) closeMap = new HashMap<>();
                return closeMap;
            }
        };
        Closepackage.setIsShowDialog(true);
    }

    public void isShowProgressbar(boolean showProgressbar) {
        isShowProgressbar = showProgressbar;
    }

    public void setPhoto_compression(String photo_compression, String is_watermark) {
        this.photo_compression = photo_compression;
        this.is_watermark = is_watermark;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCodeStr(String codeStr) {
        this.codeStr = codeStr;
    }

    public void resetList(ArrayList<TaskitemDetailNewInfo> list) {
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private OnRefreshListener onRefreshListener;

    public void setOnRefushListener(OnRefreshListener listener) {
        onRefreshListener = listener;
    }

    private String rightText;

    public void settingRightText(String rightText) {
        this.rightText = rightText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TaskitemDetail_12View taskitemDetail_12View = null;
        if (convertView == null) {
            taskitemDetail_12View = new TaskitemDetail_12View(context);
            taskitemDetail_12View.settingRightText(rightText + "");
            taskitemDetail_12View.setOfflineStoreClickViewListener(this);
            taskitemDetail_12View.setPullToRefreshDeleteListener(this);
        } else {
            taskitemDetail_12View = (TaskitemDetail_12View) convertView;
        }
        TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(position);
        taskitemDetail_12View.setting(taskitemDetailNewInfo, closeTaskClickListener, position);
        if (isShowProgressbar && taskitemDetailNewInfo.getIsPackage().equals("0")) {
            taskitemDetail_12View.isShowProgressbar(true, taskitemDetailNewInfo.is_Record());
            if (taskitemDetailNewInfo.getState().equals("2")) {
                taskitemDetail_12View.settingProgressbar(100);
            } else {
                taskitemDetail_12View.settingProgressbar(taskitemDetailNewInfo.getProgress());
                if (taskitemDetailNewInfo.getProgress() >= 100) {
                    taskitemDetailNewInfo.setState("2");
                }
            }
            taskitemDetailNewInfo.setTaskitemDetail_12View(taskitemDetail_12View);
        } else {
            taskitemDetail_12View.isShowProgressbar(false, taskitemDetailNewInfo.is_Record());
        }
        return taskitemDetail_12View;
    }

    private int position;
    /***
     * 点击事件
     */
    private View.OnClickListener closeTaskClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.item_taskitemdetaill_package_switch) {
                Object object = v.getTag();
                if (object != null) {
                    position = (int) object;
                    TaskitemDetailNewInfo taskitemDetailInfo = list.get(position);
                    if ("1".equals(taskitemDetailInfo.getCloseInvalidtype())) {
                        CloseTaskDialog.showDialog(context, taskitemDetailInfo, "close/" + AppInfo.getName(context) + "/" +
                                taskitemDetailInfo.getId() + taskitemDetailInfo.getStoreid(), new CloseTaskDialog
                                .OnCloseTaskDialogListener() {
                            public void sumbit(String edittext) { // 关闭包
                                if (index != null && "0".equals(index)) {//判断是否预览状态
                                    CloseTaskDialog.close();
                                } else {
                                    close(position, edittext);
                                }
                            }

                            public void video(int index) {
                            }
                        });
                    } else if ("2".equals(taskitemDetailInfo.getCloseInvalidtype())) {//拍照
                        list.get(position).setIsClose("2");
                        notifyDataSetChanged();
                        Intent intent = new Intent(context, CloseTaskitemPhotographyNextYActivity.class);
                        intent.putExtra("project_id", taskitemDetailInfo.getProjectid());
                        intent.putExtra("project_name", taskitemDetailInfo.getProjectname());
                        intent.putExtra("task_pack_id", taskitemDetailInfo.getId());
                        intent.putExtra("task_pack_name", taskitemDetailInfo.getName());
                        intent.putExtra("task_id", taskitemDetailInfo.getCloseTaskid());
                        intent.putExtra("task_name", taskitemDetailInfo.getCloseTaskname());
                        intent.putExtra("store_id", taskitemDetailInfo.getStoreid());
                        intent.putExtra("store_num", taskitemDetailInfo.getStoreNum());
                        intent.putExtra("store_name", taskitemDetailInfo.getStorename());
                        intent.putExtra("photo_compression", taskitemDetailInfo.getPhoto_compression());
                        intent.putExtra("code", codeStr);
                        intent.putExtra("isOffline", false);
                        intent.putExtra("brand", brand);
                        intent.putExtra("index", index);
                        intent.putExtra("outlet_batch", taskitemDetailInfo.getOutlet_batch());
                        intent.putExtra("p_batch", taskitemDetailInfo.getP_batch());
                        TaskitemDetailActivity_12.isRefresh = true;
                        context.startActivity(intent);
                    } else if ("3".equals(taskitemDetailInfo.getCloseInvalidtype())) {//视频
                        list.get(position).setIsClose("2");
                        notifyDataSetChanged();
                        Intent intent = new Intent(context, CloseTaskitemShotActivity.class);
                        intent.putExtra("project_id", taskitemDetailInfo.getProjectid());
                        intent.putExtra("project_name", taskitemDetailInfo.getProjectname());
                        intent.putExtra("task_pack_id", taskitemDetailInfo.getId());
                        intent.putExtra("task_pack_name", taskitemDetailInfo.getName());
                        intent.putExtra("task_id", taskitemDetailInfo.getCloseTaskid());
                        intent.putExtra("task_name", taskitemDetailInfo.getCloseTaskname());
                        intent.putExtra("store_id", taskitemDetailInfo.getStoreid());
                        intent.putExtra("store_num", taskitemDetailInfo.getStoreNum());
                        intent.putExtra("store_name", taskitemDetailInfo.getStorename());
                        intent.putExtra("code", codeStr);
                        intent.putExtra("isOffline", false);
                        intent.putExtra("index", index);
                        intent.putExtra("brand", brand);
                        intent.putExtra("outlet_batch", taskitemDetailInfo.getOutlet_batch());
                        intent.putExtra("p_batch", taskitemDetailInfo.getP_batch());
                        TaskitemDetailActivity_12.isRefresh = true;
                        context.startActivity(intent);
                    }
                }
            }
        }
    };

    public void close(int position, String edittext) {
        if (edittext == null || edittext.equals("")) {
            Tools.showToast(context, "请填写备注");
            return;
        }
        try {//这里为什么不直接encode呢？因为后面数据存储有两种方式，一种会encode，另一种不会。。。。。所以这里就先简单处理了
            URLDecoder.decode(edittext, "utf-8");
        } catch (UnsupportedEncodingException e) {
            edittext = edittext.replaceAll("%", "%25");
        }
        TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(position);
        closeMap = new HashMap<>();
        closeMap.put("token", Tools.getToken());
        closeMap.put("projectid", taskitemDetailNewInfo.getProjectid());
        closeMap.put("projectname", taskitemDetailNewInfo.getProjectname());
        closeMap.put("pid", taskitemDetailNewInfo.getId());
        closeMap.put("pname", taskitemDetailNewInfo.getName());
        closeMap.put("storeid", taskitemDetailNewInfo.getStoreid());
        closeMap.put("storename", taskitemDetailNewInfo.getStorename());
        closeMap.put("note", edittext);
        closeMap.put("code", taskitemDetailNewInfo.getCode());
        closeMap.put("brand", taskitemDetailNewInfo.getBrand());
        closeMap.put("outlet_batch", taskitemDetailNewInfo.getOutlet_batch());
        closeMap.put("p_batch", taskitemDetailNewInfo.getP_batch());
        closeMap.put("taskid", "0");
        list.get(position).setIsClose("2");
        closeTask();
        notifyDataSetChanged();
        CloseTaskDialog.close();
    }

    public void stopUpdata() {
        if (Closepackage != null) {
            Closepackage.stop(Urls.Closepackage);
        }
    }

    private Map<String, String> closeMap;
    private NetworkConnection Closepackage;

    private void closeTask() {
        Closepackage.upPostRequest(Urls.Closepackage, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                CustomProgressDialog.Dissmiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if ((code == 200 || code == 2) && closeMap != null) {
                        String projectid = closeMap.get("projectid");
                        String projectname = closeMap.get("projectname");
                        String packageid = closeMap.get("pid");
                        String codeStr = closeMap.get("code");
                        String brand = closeMap.get("brand");
                        closeMap.remove("projectid");
                        closeMap.remove("projectname");
                        closeMap.remove("code");
                        closeMap.remove("brand");
                        String username = AppInfo.getName(context);
                        updataDBHelper.addUpdataTask(username, projectid, projectname, closeMap.get("storenum"), brand,
                                closeMap.get("storeid"), closeMap.get("storename"), closeMap.get("pid"), closeMap.get
                                        ("pname"), "01", null, null, null, null, null, username + projectid +
                                        closeMap.get("storeid") + packageid, Urls.Closepackagecomplete, null, null,
                                UpdataDBHelper.Updata_file_type_video, closeMap, null, true, Urls.Closepackage,
                                paramsToString(), false);
                        Intent service = new Intent("com.orange.oy.UpdataNewService");
                        service.setPackage("com.orange.oy");
                        context.startService(service);
                        if (onRefreshListener != null) {
                            onRefreshListener.refresh(packageid);
                        }
                        if (code == 2) {
                            ConfirmDialog.showDialog(context, null, jsonObject.getString("msg"), null,
                                    "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                        @Override
                                        public void leftClick(Object object) {

                                        }

                                        @Override
                                        public void rightClick(Object object) {
                                        }
                                    }).goneLeft();
                        }
                    } else {
                        Tools.showToast(context, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(context, context.getResources().getString(R.string.network_error));
                } catch (UnsupportedEncodingException e) {
                    Tools.showToast(context, "存储失败，未知异常！");
                    MobclickAgent.reportError(context, "offline map y:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(context, context.getResources().getString(R.string.network_volleyerror));
            }
        }, null);
    }

    private String paramsToString() throws UnsupportedEncodingException {
        String data = "";
        Iterator<String> iterator = closeMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (TextUtils.isEmpty(data)) {
                data = key + "=" + URLEncoder.encode(closeMap.get(key).trim(), "utf-8");
            } else {
                data = data + "&" + key + "=" + URLEncoder.encode(closeMap.get(key).trim(), "utf-8");
            }
        }
        return data;
    }

    public void select(View view) {
        if (view instanceof SlideTouchEventListener)
            listView.setSlideTouchEventListener((SlideTouchEventListener) view);
    }

    public void delete(Object object) {
        if (onRightClickListener != null) {
            onRightClickListener.onRightClick(object);
        }
    }

    public void click(Object object) {
        if (newOnItemClickListener != null) {
            newOnItemClickListener.onItemClick(object);
        }
    }

    private NewOnItemClickListener newOnItemClickListener;
    private OnRightClickListener onRightClickListener;

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    public void setNewOnItemClickListener(NewOnItemClickListener newOnItemClickListener) {
        this.newOnItemClickListener = newOnItemClickListener;
    }
}
