package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.CloseTaskitemPhotographyActivity;
import com.orange.oy.activity.CloseTaskitemShotillustrateActivity;
import com.orange.oy.activity.TaskitemDetailActivity;
import com.orange.oy.allinterface.OnCloseTaskListener;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.db.OfflineDBHelper;
import com.orange.oy.db.UpdataDBHelper;
import com.orange.oy.dialog.CloseTaskDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//TODO NEW
public class TaskitemDetailNewAdapter extends BaseAdapter {
    private ArrayList<TaskitemDetailNewInfo> list;
    private Context context;
    private UpdataDBHelper updataDBHelper;
    private String photo_compression, codeStr, brand;
    private boolean showTitle = false;
    private boolean is_takephoto;

    public TaskitemDetailNewAdapter(Context context, ArrayList<TaskitemDetailNewInfo> list) {
        this.context = context;
        this.list = list;
        updataDBHelper = new UpdataDBHelper(context);
        Closepackage = new NetworkConnection(context) {
            public Map<String, String> getNetworkParams() {
                if (closeMap == null) closeMap = new HashMap<>();
                return closeMap;
            }
        };
        Closepackage.setIsShowDialog(true);
    }

    public void setIs_takephoto(boolean is_takephoto) {
        this.is_takephoto = is_takephoto;
    }

    public void setPhoto_compression(String photo_compression) {
        this.photo_compression = photo_compression;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCodeStr(String codeStr) {
        this.codeStr = codeStr;
    }

    private OfflineDBHelper offlineDBHelper;
    private boolean isOffline;

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public void isOffline(boolean isOffline) {
        this.isOffline = isOffline;
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

    private TaskitemDetailActivity.OnRefreshListener onRefreshListener;

    public void setOnRefushListener(TaskitemDetailActivity.OnRefreshListener listener) {
        onRefreshListener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold = null;
        if (convertView == null) {
            viewHold = new ViewHold();
            convertView = Tools.loadLayout(context, R.layout.listviewitem_taskitemdetaill);
            viewHold.image = (ImageView) convertView.findViewById(R.id.item_taskitemdetaill_ico);
            viewHold.name = (TextView) convertView.findViewById(R.id.item_taskitemdetaill_name);
            viewHold.name_package = (TextView) convertView.findViewById(R.id.item_taskitemdetaill_package_name);
            viewHold.layout_task = convertView.findViewById(R.id.item_taskitemdetaill_tasklayout);
            viewHold.layout_package = convertView.findViewById(R.id.item_taskitemdetaill_packagelayout);
            viewHold.layout_title = convertView.findViewById(R.id.item_taskitemdetaill_titleLayout);
            viewHold.item_taskitemdetaill_package_switch = (ImageView) convertView.findViewById(R.id
                    .item_taskitemdetaill_package_switch);
            viewHold.title_img = (ImageView) convertView.findViewById(R.id.item_taskitemdetaill_title_img);
            viewHold.title_text = (TextView) convertView.findViewById(R.id.item_taskitemdetaill_title_name);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        if (is_takephoto && showTitle && position < 2) {
            viewHold.layout_task.setVisibility(View.GONE);
            viewHold.layout_package.setVisibility(View.GONE);
            viewHold.layout_title.setVisibility(View.VISIBLE);
            if (position == 0) {
                viewHold.title_img.setImageResource(R.mipmap.camera_task);
                viewHold.title_text.setText("连续拍照");
            } else {
                viewHold.title_img.setImageResource(R.mipmap.camera_show);
                viewHold.title_text.setText("预览照片");
            }
        } else {
            viewHold.layout_title.setVisibility(View.GONE);
            TaskitemDetailNewInfo item = list.get(position);
            if (item != null) {
                if (item.getIsPackage().equals("1")) {//任务包
                    viewHold.layout_package.setVisibility(View.VISIBLE);
                    viewHold.layout_task.setVisibility(View.GONE);
                    viewHold.name_package.setText(item.getName());
                    if (item.getIs_invalid().equals("1")) {//可以关闭
                        if (item.getIsClose().equals("1")) {//1开
                            viewHold.item_taskitemdetaill_package_switch.setImageResource(R.mipmap.switch_on);
                            viewHold.item_taskitemdetaill_package_switch.setTag(position);
                            viewHold.item_taskitemdetaill_package_switch.setOnClickListener(closeTaskClickListener);
                        } else {//2关
                            viewHold.item_taskitemdetaill_package_switch.setImageResource(R.mipmap.switch_off);
                            viewHold.item_taskitemdetaill_package_switch.setTag(null);
                            viewHold.item_taskitemdetaill_package_switch.setOnClickListener(null);
                        }
                    } else {//不可以关闭
                        viewHold.item_taskitemdetaill_package_switch.setVisibility(View.GONE);
                        viewHold.item_taskitemdetaill_package_switch.setTag(null);
                        viewHold.item_taskitemdetaill_package_switch.setOnClickListener(null);
                    }
                } else {//任务
                    viewHold.layout_package.setVisibility(View.GONE);
                    viewHold.layout_task.setVisibility(View.VISIBLE);
                    viewHold.name.setText(item.getName());
                    if (item.getTask_type().equals("1")) {
                        viewHold.image.setImageResource(R.mipmap.take_photo);
                    } else if (item.getTask_type().equals("2")) {
                        viewHold.image.setImageResource(R.mipmap.take_viedo);
                    } else if (item.getTask_type().equals("3")) {
                        viewHold.image.setImageResource(R.mipmap.take_record);
                    } else if (item.getTask_type().equals("4")) {
                        viewHold.image.setImageResource(R.mipmap.take_location);
                    } else if (item.getTask_type().equals("5")) {
                        viewHold.image.setImageResource(R.mipmap.take_record);
                    } else if (item.getTask_type().equals("8")) {
                        viewHold.image.setImageResource(R.mipmap.take_photo);
                    }
                }
            }
        }
        return convertView;
    }

    class ViewHold {
        private ImageView image, item_taskitemdetaill_package_switch;
        private TextView name, name_package;
        private View layout_task, layout_package, layout_title;
        private ImageView title_img;
        private TextView title_text;
    }

    private int position;
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
                                close(position, edittext);
                            }
                        });
                    } else if ("2".equals(taskitemDetailInfo.getCloseInvalidtype())) {//拍照
                        list.get(position).setIsClose("2");
                        notifyDataSetChanged();
                        Intent intent = new Intent(context, CloseTaskitemPhotographyActivity.class);
                        intent.putExtra("project_id", taskitemDetailInfo.getProjectid());
                        intent.putExtra("project_name", taskitemDetailInfo.getProjectname());
                        intent.putExtra("task_pack_id", taskitemDetailInfo.getId());
                        intent.putExtra("task_pack_name", taskitemDetailInfo.getName());
                        intent.putExtra("task_id", taskitemDetailInfo.getCloseTaskid());
                        intent.putExtra("task_name", taskitemDetailInfo.getCloseTaskname());
                        intent.putExtra("store_id", taskitemDetailInfo.getStoreid());
                        intent.putExtra("store_num", taskitemDetailInfo.getStoreNum());
                        intent.putExtra("store_name", taskitemDetailInfo.getStorename());
                        intent.putExtra("photo_compression", photo_compression);
                        intent.putExtra("code", codeStr);
                        intent.putExtra("isOffline", isOffline);
                        intent.putExtra("brand", brand);
                        intent.putExtra("outlet_batch", taskitemDetailInfo.getOutlet_batch());
                        intent.putExtra("p_batch", taskitemDetailInfo.getP_batch());
                        context.startActivity(intent);
                    } else if ("3".equals(taskitemDetailInfo.getCloseInvalidtype())) {//视频
                        list.get(position).setIsClose("2");
                        notifyDataSetChanged();
                        Intent intent = new Intent(context, CloseTaskitemShotillustrateActivity.class);
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
                        intent.putExtra("isOffline", isOffline);
                        intent.putExtra("brand", brand);
                        intent.putExtra("outlet_batch", taskitemDetailInfo.getOutlet_batch());
                        intent.putExtra("p_batch", taskitemDetailInfo.getP_batch());
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
        TaskitemDetailNewInfo taskitemDetailNewInfo = list.get(position);
        closeMap = new HashMap<>();
        closeMap.put("token", Tools.getToken());
        closeMap.put("projectid", taskitemDetailNewInfo.getProjectid());
        closeMap.put("projectname", taskitemDetailNewInfo.getProjectname());
        closeMap.put("pid", taskitemDetailNewInfo.getId());
        closeMap.put("pname", taskitemDetailNewInfo.getName());
        closeMap.put("storeid", taskitemDetailNewInfo.getStoreid());
        closeMap.put("storenum", taskitemDetailNewInfo.getStoreNum());
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
        if (isOffline) {//离线
            if (offlineDBHelper == null) {
                offlineDBHelper = new OfflineDBHelper(context);
            }
            try {
                String projectid = closeMap.get("projectid");
                String projectname = closeMap.get("projectname");
                String storeid = closeMap.get("storeid");
                String packageid = closeMap.get("pid");
                String code = closeMap.get("code");
                String brand = closeMap.get("brand");
                closeMap.remove("projectid");
                closeMap.remove("projectname");
                closeMap.remove("code");
                closeMap.remove("brand");
                String username = AppInfo.getName(context);
                updataDBHelper.addUpdataTask(username, projectid, projectname, closeMap.get("storenum"), brand,
                        closeMap.get("storeid"), closeMap.get("storename"), closeMap.get("pid"), closeMap.get("pname"), "01",
                        null, null, null, null, null, username + projectid + closeMap.get("storeid") + packageid,
                        Urls.Closepackagecomplete, null, null,
                        UpdataDBHelper.Updata_file_type_video, closeMap, null, true, Urls.Closepackage, paramsToString(), true);
                offlineDBHelper.closePackage(username, projectid, storeid, packageid);
                Intent service = new Intent("com.orange.oy.UpdataNewService");
                service.setPackage("com.orange.oy");
                context.startService(service);
                if (onRefreshListener != null) {
                    onRefreshListener.refresh(packageid);
                }
            } catch (UnsupportedEncodingException e) {
                Tools.showToast(context, "存储失败，未知异常！");
                MobclickAgent.reportError(context, "offline map y:" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Closepackage.upPostRequest(Urls.Closepackage, new Response.Listener<String>() {
                public void onResponse(String s) {
                    Tools.d(s);
                    CustomProgressDialog.Dissmiss();
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int code = jsonObject.getInt("code");
                        if (code == 200 && closeMap != null) {
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
}
