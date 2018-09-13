package com.orange.oy.activity.black;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.adapter.TaskitemReqPgAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.info.BlackoutstoreInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.photoview.PhotoView;

import java.util.ArrayList;

public class OutSurveyEditillustrateActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle,
        View.OnClickListener, AdapterView.OnItemClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.editill_title_outsurvey);
        appTitle.settingName("问卷任务");
        appTitle.showBack(this);
    }

    private ArrayList<BlackoutstoreInfo> list;
    private GridView editmpg_gridview_outsurvey;
    private ArrayList<String> picList = new ArrayList<>();
    private TaskitemReqPgAdapter adapter;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_survey_editillustrate);
        initTitle();
        imageLoader = new ImageLoader(this);
        list = (ArrayList<BlackoutstoreInfo>) getIntent().getBundleExtra("data").getSerializable("list");
        BlackoutstoreInfo blackoutstoreInfo = list.get(0);
        ((TextView) findViewById(R.id.editill_name_outsurvey)).setText(blackoutstoreInfo.getTaskname());
        ((TextView) findViewById(R.id.editill_desc_outsurvey)).setText(blackoutstoreInfo.getNote());
        editmpg_gridview_outsurvey = (GridView) findViewById(R.id.editmpg_gridview_outsurvey);
        adapter = new TaskitemReqPgAdapter(this, picList);
        editmpg_gridview_outsurvey.setAdapter(adapter);
        String picStr = blackoutstoreInfo.getPics();
        if (TextUtils.isEmpty(picStr) || "null".equals(picStr) || picStr.length() == 4) {
            findViewById(R.id.shili).setVisibility(View.GONE);
            editmpg_gridview_outsurvey.setVisibility(View.GONE);
        } else {
            picStr = picStr.substring(1, picStr.length() - 1);
            String[] pics = picStr.split(",");
            for (int i = 0; i < pics.length; i++) {
                picList.add(Urls.ImgIp + pics[i].replaceAll("\"", "").replaceAll("\\\\", ""));
            }
            if (pics.length > 0) {
                int t = (int) Math.ceil(pics.length / 3d);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) editmpg_gridview_outsurvey.getLayoutParams();
                lp.height = (int) ((Tools.getScreeInfoWidth(OutSurveyEditillustrateActivity.this) -
                        getResources().getDimension(R.dimen.taskphoto_gridview_mar) * 2 -
                        getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) * 2) / 3) * t;
                editmpg_gridview_outsurvey.setLayoutParams(lp);
            }
            adapter.notifyDataSetChanged();
        }
        findViewById(R.id.editill_button_outsurvey).setOnClickListener(this);
        editmpg_gridview_outsurvey.setOnItemClickListener(this);
    }

    @Override
    public void onBack() {
        BlackDZXListActivity.isRefresh = true;
        baseFinish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editill_button_outsurvey) {
            Intent intent = new Intent(OutSurveyEditillustrateActivity.this, OutSurveyEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", list);
            intent.putExtra("data", bundle);
            startActivity(intent);
        }
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
