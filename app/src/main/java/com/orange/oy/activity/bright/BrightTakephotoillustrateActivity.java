package com.orange.oy.activity.bright;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BrightTakephotoillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle taskitmpg_title = (AppTitle) findViewById(R.id.takephotoill_title_bright);
        taskitmpg_title.settingName("拍照任务");
        taskitmpg_title.showBack(this);
    }

    private void initNetworkConnection() {
        Photo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("token", Tools.getToken());
                return params;
            }
        };
        Photo.setIsShowDialog(true);
    }

    private Intent data;
    private NetworkConnection Photo;
    private TextView takephoto_name_bright, takephoto_desc_bright;
    private GridView takephoto_gridview_bright;
    private Button takephoto_button_bright;
    private String taskid;
    private String num, isphoto, photo_type, min_num;
    private String batch;
    private int is_watermark;
    private TaskitemReqPgAdapter adapter;
    private ImageLoader imageLoader;
    private ArrayList<String> picList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.gc();
        setContentView(R.layout.activity_bright_takephotoillustrate);
        data = getIntent();
        if (data == null) {
            baseFinish();
            return;
        }
        taskid = data.getIntExtra("taskid", 0) + "";
        imageLoader = new ImageLoader(this);
        initTitle();
        initNetworkConnection();
        takephoto_name_bright = (TextView) findViewById(R.id.takephoto_name_bright);
        takephoto_desc_bright = (TextView) findViewById(R.id.takephoto_desc_bright);
        takephoto_gridview_bright = (GridView) findViewById(R.id.takephotoill_gridview_bright);
        takephoto_button_bright = (Button) findViewById(R.id.takephoto_button_bright);
        adapter = new TaskitemReqPgAdapter(this, picList);
        takephoto_gridview_bright.setAdapter(adapter);
        takephoto_gridview_bright.setOnItemClickListener(this);
        getData();
    }

    String taskName;

    private void getData() {
        Photo.sendPostRequest(Urls.Photo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        taskName = jsonObject.getString("name");
                        takephoto_name_bright.setText(taskName);
                        takephoto_desc_bright.setText(jsonObject.getString("desc"));
                        photo_type = jsonObject.getString("photo_type");
                        isphoto = jsonObject.getString("isphoto");
                        num = jsonObject.getString("num");
                        min_num = jsonObject.getString("min_num");
                        batch = jsonObject.getString("batch");
                        is_watermark = Tools.StringToInt(jsonObject.getString("is_watermark"));
                        String picStr = jsonObject.getString("pics");
                        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
                            findViewById(R.id.shili).setVisibility(View.GONE);
                            takephoto_gridview_bright.setVisibility(View.GONE);
                        } else {
                            picStr = picStr.substring(1, picStr.length() - 1);
                            String[] pics = picStr.split(",");
                            for (int i = 0; i < pics.length; i++) {
                                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
                            }
                            if (pics.length > 0) {
                                int t = (int) Math.ceil(pics.length / 3d);
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) takephoto_gridview_bright
                                        .getLayoutParams();
                                lp.height = (int) ((Tools.getScreeInfoWidth(BrightTakephotoillustrateActivity.this) -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                                takephoto_gridview_bright.setLayoutParams(lp);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(BrightTakephotoillustrateActivity.this, jsonObject.getString("msg"));
                    }
                    takephoto_button_bright.setOnClickListener(BrightTakephotoillustrateActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Tools.showToast(BrightTakephotoillustrateActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(BrightTakephotoillustrateActivity.this, getResources().getString(R.string
                        .network_volleyerror));
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.takephoto_button_bright) {
            data.setClass(this, BrightTakephotoActivity.class);
            data.putExtra("tasktype", "1");
            data.putExtra("photo_type", photo_type);
            data.putExtra("num", num);
            data.putExtra("min_num", min_num);
            data.putExtra("isphoto", isphoto);
            data.putExtra("batch", batch);
            data.putExtra("is_watermark", is_watermark);
            data.putExtra("taskName", taskName);
            startActivity(data);
            baseFinish();
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PhotoView imageView = new PhotoView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageLoader.DisplayImage(picList.get(position), imageView);
        SelecterDialog.showView(this, imageView);
    }
}
