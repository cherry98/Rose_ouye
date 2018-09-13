package com.orange.oy.activity.black;

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

import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.info.BlackoutstoreInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.photoview.PhotoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OutSurveyTakephotoillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener, View.OnClickListener {
    private void initTitle() {
        AppTitle taskitmpg_title = (AppTitle) findViewById(R.id.takephoto_title_outsurvey);
        taskitmpg_title.settingName("拍照任务");
        taskitmpg_title.showBack(this);
    }

    private NetworkConnection blackCloseTakephotoFinish;

    private void initNetworkConnection() {
        blackCloseTakephotoFinish = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("taskid", taskid);
                params.put("storeid", storeid);
                params.put("taskbatch", taskbatch);
                params.put("batch", batch);
                params.put("note", note);
                params.put("usermobile", username);
                return params;
            }
        };
        blackCloseTakephotoFinish.setIsShowDialog(true);
    }

    private String username;
    private String task_name, taskid, taskbatch, batch, note, storeid;
    private ArrayList<BlackoutstoreInfo> list;
    private BlackoutstoreInfo blackoutstoreInfo;
    private GridView takephoto_gridview_outsurvey;
    private Button takephoto_button_outsurvey, takephoto_button2_outsurvey;
    private TaskitemReqPgAdapter adapter;
    private ImageLoader imageLoader;
    private ArrayList<String> picList = new ArrayList<>();
    private TextView takephoto_desc_outsurvey, takephoto_name_outsurvey;
    private String wuxiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_survey_takephotoillustrate);
        username = AppInfo.getName(this);
        initTitle();
        initNetworkConnection();
        imageLoader = new ImageLoader(this);
        takephoto_gridview_outsurvey = (GridView) findViewById(R.id.takephoto_gridview_outsurvey);
        takephoto_button_outsurvey = (Button) findViewById(R.id.takephoto_button_outsurvey);
        takephoto_button2_outsurvey = (Button) findViewById(R.id.takephoto_button2_outsurvey);
        takephoto_name_outsurvey = (TextView) findViewById(R.id.takephoto_name_outsurvey);
        takephoto_desc_outsurvey = (TextView) findViewById(R.id.takephoto_desc_outsurvey);
        adapter = new TaskitemReqPgAdapter(this, picList);
        takephoto_gridview_outsurvey.setAdapter(adapter);
        takephoto_gridview_outsurvey.setOnItemClickListener(this);
        getData();
    }

    @Override
    public void onBack() {
        BlackDZXListActivity.isRefresh = true;
        baseFinish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PhotoView imageView = new PhotoView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageLoader.DisplayImage(picList.get(position), imageView);
        SelecterDialog.showView(this, imageView);
    }

    public void getData() {
        list = (ArrayList<BlackoutstoreInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        blackoutstoreInfo = list.get(0);
        task_name = blackoutstoreInfo.getTaskname();
        taskid = blackoutstoreInfo.getTaskid();
        taskbatch = blackoutstoreInfo.getTaskbatch();
        batch = blackoutstoreInfo.getBatch();
        note = blackoutstoreInfo.getNote();
        storeid = blackoutstoreInfo.getStroeid();
        wuxiao = blackoutstoreInfo.getWuxiao();
        takephoto_name_outsurvey.setText(task_name);
        takephoto_desc_outsurvey.setText(note);
        if ("1".equals(wuxiao)) {
            takephoto_button2_outsurvey.setVisibility(View.VISIBLE);
        } else {
            takephoto_button_outsurvey.setLayoutParams(findViewById(R.id.taskitmpg_button3).getLayoutParams());
            takephoto_button2_outsurvey.setVisibility(View.GONE);
        }
        String picStr = blackoutstoreInfo.getPics();
        picStr = picStr.substring(1, picStr.length() - 1);
        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 2) {
            findViewById(R.id.shili).setVisibility(View.GONE);
            takephoto_gridview_outsurvey.setVisibility(View.GONE);
        } else {
            String[] pics = picStr.split(",");
            for (int i = 0; i < pics.length; i++) {
                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
            }
            if (pics.length > 0) {
                int t = (int) Math.ceil(pics.length / 3d);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) takephoto_gridview_outsurvey.getLayoutParams();
                lp.height = (int) ((Tools.getScreeInfoWidth(OutSurveyTakephotoillustrateActivity.this) -
                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                takephoto_gridview_outsurvey.setLayoutParams(lp);
            }
            adapter.notifyDataSetChanged();
        }

        takephoto_button_outsurvey.setOnClickListener(this);
        takephoto_button2_outsurvey.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takephoto_button_outsurvey:
                Intent intent = new Intent(OutSurveyTakephotoillustrateActivity.this, OutSurveyTakephotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", list);
                intent.putExtra("data", bundle);
                startActivity(intent);
                break;
            case R.id.takephoto_button2_outsurvey:
                Intent intent2 = new Intent(OutSurveyTakephotoillustrateActivity.this, OutSurveyTakephotoNActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("list", list);
                intent2.putExtra("data", bundle2);
                startActivity(intent2);
                break;
        }
        baseFinish();
    }

}
