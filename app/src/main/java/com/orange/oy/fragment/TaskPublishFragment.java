package com.orange.oy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.createtask_317.TaskMouldActivity;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.activity.scan.IdentityVerActivity;
import com.orange.oy.activity.shakephoto_316.CollectPhotoActivity;
import com.orange.oy.activity.shakephoto_318.PutInTask2Activity;
import com.orange.oy.activity.shakephoto_320.AllmodelActivity;
import com.orange.oy.activity.shakephoto_320.MySponsorshipActivity;
import com.orange.oy.adapter.mycorps_314.TaskPublicAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.MyGridView;
import com.sobot.chat.SobotApi;
import com.sobot.chat.api.model.Information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * 首页中间按钮==随手发任务 V3.19
 */
public class TaskPublishFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    public TaskPublishFragment() {
        // Required empty public constructor
    }

    private void initNetwork() {
        releaseTask = new NetworkConnection(getContext()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("token", Tools.getToken());
                return params;
            }
        };
    }

    private NetworkConnection releaseTask;
    private boolean bindidcard;
    private MyGridView taskpublish_gridview;
    private TaskPublicAdapter taskPublicAdapter;
    private ArrayList<TemplateInfo> list;
    private TextView taskpublish_ing, taskpublish_drafts, taskpublish_end, taskpublish_ing2, taskpublish_end2;
    private View taskpublish_top_layout, taskpublish_center_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_task_publish, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        taskpublish_top_layout = inflate.findViewById(R.id.taskpublish_top_layout);
        taskpublish_center_layout = inflate.findViewById(R.id.taskpublish_center_layout);
        taskpublish_ing = (TextView) inflate.findViewById(R.id.taskpublish_ing);
        taskpublish_ing2 = (TextView) inflate.findViewById(R.id.taskpublish_ing2);
        taskpublish_drafts = (TextView) inflate.findViewById(R.id.taskpublish_drafts);
        taskpublish_end = (TextView) inflate.findViewById(R.id.taskpublish_end);
        taskpublish_end2 = (TextView) inflate.findViewById(R.id.taskpublish_end2);
        taskpublish_gridview = (MyGridView) inflate.findViewById(R.id.taskpublish_gridview);
        inflate.findViewById(R.id.taskpublish_ing_ly).setOnClickListener(this);
        inflate.findViewById(R.id.taskpublish_ing_ly2).setOnClickListener(this);
        inflate.findViewById(R.id.taskpublish_drafts_ly).setOnClickListener(this);
        inflate.findViewById(R.id.taskpublish_end_ly).setOnClickListener(this);
        inflate.findViewById(R.id.taskpublish_end_ly2).setOnClickListener(this);
        inflate.findViewById(R.id.taskpublish_more).setOnClickListener(this);
        inflate.findViewById(R.id.taskpublish_invite).setOnClickListener(this);
        inflate.findViewById(R.id.taskpublish_kefu).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = new ArrayList<>();
        initNetwork();
        taskPublicAdapter = new TaskPublicAdapter(getContext(), list);
        taskpublish_gridview.setAdapter(taskPublicAdapter);
        taskpublish_gridview.setOnItemClickListener(this);
    }

    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(Tools.getToken())) {
            taskpublish_top_layout.setVisibility(View.VISIBLE);
            getData();
        } else {
            taskpublish_top_layout.setVisibility(View.INVISIBLE);
        }
    }

    private void getData() {
        releaseTask.sendPostRequest(Urls.ReleaseTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (!list.isEmpty()) {
                            list.clear();
                        }
                        JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                        //我创建的
                        String ongoing_num = jsonObject1.getString("ongoing_num");
                        String drafts_num = jsonObject1.getString("drafts_num");
                        String end_num = jsonObject1.getString("end_num");
                        if ("0".equals(ongoing_num) && "0".equals(drafts_num) && "0".equals(end_num)) {
                            taskpublish_top_layout.setVisibility(View.GONE);
                        } else {
                            taskpublish_top_layout.setVisibility(View.VISIBLE);
                            taskpublish_ing.setText(ongoing_num);
                            taskpublish_drafts.setText(drafts_num);
                            taskpublish_end.setText(end_num);
                        }

                        //我赞助的
                        String sponsorship_ongoing_num = jsonObject1.getString("sponsorship_ongoing_num");
                        String sponsorship_end_num = jsonObject1.getString("sponsorship_end_num");
                        if ("0".equals(sponsorship_ongoing_num) && "0".equals(sponsorship_end_num)) {
                            taskpublish_center_layout.setVisibility(View.GONE);
                        } else {
                            taskpublish_center_layout.setVisibility(View.VISIBLE);
                            taskpublish_ing2.setText(sponsorship_ongoing_num);
                            taskpublish_end2.setText(sponsorship_end_num);
                        }

                        bindidcard = "1".equals(jsonObject1.getString("bindidcard"));
                        JSONArray jsonArray = jsonObject.optJSONArray("template_list");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                TemplateInfo templateInfo = new TemplateInfo();
                                templateInfo.setTemplate_id(object.getString("template_id"));
                                templateInfo.setTemplate_img(object.getString("template_img"));
                                templateInfo.setTemplate_name(object.getString("template_name"));
                                templateInfo.setTemplate_type(object.getString("template_type"));
                                list.add(templateInfo);
                            }
                            if (taskPublicAdapter != null) {
                                taskPublicAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Tools.showToast(getContext(), jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!bindidcard) {
            startActivity(new Intent(getContext(), IdentityVerActivity.class));
            return;
        }
        switch (v.getId()) {
            // activity_status    活动状态1：草稿箱未发布；2：投放中；3：已结束
            case R.id.taskpublish_ing_ly: {//投放中的任务
                Intent intent = new Intent(getContext(), PutInTask2Activity.class);
                intent.putExtra("activity_status", "2");
                startActivity(intent);
            }
            break;
            case R.id.taskpublish_drafts_ly: {//草稿箱的任务
                Intent intent = new Intent(getContext(), PutInTask2Activity.class);
                intent.putExtra("activity_status", "1");
                startActivity(intent);
            }
            break;
            case R.id.taskpublish_end_ly: {//已结束的任务
                Intent intent = new Intent(getContext(), PutInTask2Activity.class);
                intent.putExtra("activity_status", "3");
                startActivity(intent);
            }
            break;
            case R.id.taskpublish_ing_ly2: {//投放中的任务(我赞助的)
                Intent intent = new Intent(getContext(), MySponsorshipActivity.class);
                intent.putExtra("activity_status", "2");
                startActivity(intent);
            }
            break;
            case R.id.taskpublish_end_ly2: {//已结束的任务(我赞助的)
                Intent intent = new Intent(getContext(), MySponsorshipActivity.class);
                intent.putExtra("activity_status", "3");
                startActivity(intent);
            }
            break;
            case R.id.taskpublish_more: {//更多模板
                Intent intent = new Intent(getContext(), AllmodelActivity.class);
                intent.putExtra("state", "1");
                startActivity(intent);
            }
            break;
            case R.id.taskpublish_invite: {//邀请发任务
                UMShareDialog.showDialog(getContext(), false, new UMShareDialog.UMShareListener() {
                    @Override
                    public void shareOnclick(int type) {
                        String url = Urls.InviteReleaseTask + "usermobile=" + AppInfo.getName(getContext());
                        MyUMShareUtils.umShare(getContext(), type, url, 0
                                , "偶业，任务自主发布平台", "我在偶业上发布了一个价值共创任务，你也来发一个吧!");
                    }
                });
            }
            break;
            case R.id.taskpublish_kefu: {//客服

                Information info = new Information();
                info.setAppkey(Urls.ZHICHI_KEY);
                info.setColor("#FFFFFF");
                if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
                    info.setUname("游客");
                } else {
                    String netHeadPath = AppInfo.getUserImagurl(getContext());
                    info.setFace(netHeadPath);
                    info.setUid(AppInfo.getKey(getContext()));
                    info.setUname(AppInfo.getUserName(getContext()));
                }
                SobotApi.startSobotChat(getContext(), info);
            }
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            startActivity(intent);
                        }
                    });
            return;
        }
        if (bindidcard) {
            TemplateInfo templateInfo = list.get(position);
            if ("1".equals(templateInfo.getTemplate_type())) {
                Intent intent = new Intent(getContext(), CollectPhotoActivity.class);//集图活动
                intent.putExtra("which_page", "0");
                intent.putExtra("template_id", templateInfo.getTemplate_id());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), TaskMouldActivity.class);
                intent.putExtra("which_page", "0");//0创建 1编辑
                intent.putExtra("template_id", templateInfo.getTemplate_id());
                startActivity(intent);//任务模板
            }
        } else {
            startActivity(new Intent(getContext(), IdentityVerActivity.class));//身份认证
        }
    }

    public class TemplateInfo {

        /**
         * template_id : 模板id
         * template_name : 模板名称
         * template_img : 模板图标
         */

        private String template_id;
        private String template_name;
        private String template_img;
        private String template_type;

        public String getTemplate_type() {
            return template_type;
        }

        public void setTemplate_type(String template_type) {
            this.template_type = template_type;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getTemplate_name() {
            return template_name;
        }

        public void setTemplate_name(String template_name) {
            this.template_name = template_name;
        }

        public String getTemplate_img() {
            return template_img;
        }

        public void setTemplate_img(String template_img) {
            this.template_img = template_img;
        }
    }
}
