package com.orange.oy.activity.createtask_317;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.ToWhomInVisibleRedActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

import static com.orange.oy.R.id.find_citysearch;
import static com.orange.oy.R.id.lin_onlymyself;


/**
 * beibei 对谁可见   集图活动和赞助活动里面都有
 */
public class ToWhomVisibleActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.album_title);
        appTitle.settingName("对谁可见");
        appTitle.showBack(this);
    }

    private String isFrist, city; //等于 1 是创建活动  2是赞助活动
    private TextView tv_name1, tv_name2;
    private String visible_type; //对谁可见或者是不可见的回显控制
    private String ischart;  //  1是集图活动   2是任务模板
    private LinearLayout lin_onlymyselfs;
    private View view_line;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_whom_visible);
        tv_name1 = (TextView) findViewById(R.id.tv_name1);
        tv_name2 = (TextView) findViewById(R.id.tv_name2);
        lin_onlymyselfs = (LinearLayout) findViewById(R.id.lin_onlymyselfs);
        view_line = findViewById(R.id.view_line);

        initTitle();
        isFrist = getIntent().getStringExtra("isFrist");
        visible_type = getIntent().getStringExtra("visible_type");
        ischart = getIntent().getStringExtra("ischart");
        city = getIntent().getStringExtra("city");


        if (!Tools.isEmpty(ischart)) {
            if ("1".equals(ischart)) {     //  1是集图活动   2是任务模板
                view_line.setVisibility(View.GONE);
                lin_onlymyselfs.setVisibility(View.GONE);
            } else {
                view_line.setVisibility(View.VISIBLE);
                lin_onlymyselfs.setVisibility(View.VISIBLE);
            }
        }

        if (!Tools.isEmpty(isFrist)) {
            if ("1".equals(isFrist)) {
                tv_name1.setText("谁不可见活动");
                tv_name2.setText("谁可见活动");
            } else {
                tv_name1.setText("谁不可见红包");
                tv_name2.setText("谁可见红包");
            }
        }
        findViewById(R.id.lin_allthing).setOnClickListener(this);
        findViewById(R.id.lin_onlymyselfs).setOnClickListener(this);
        findViewById(R.id.lin_invisible).setOnClickListener(this);
        findViewById(R.id.lin_visible).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {   //visible_type	对谁可见的类型【必传】1为全部，2为仅自己可见，3为谁不可见任务，4为谁可见任务【到店红包必传】
            //全部
            case R.id.lin_allthing: {
                if (!Tools.isEmpty(isFrist)) {
                    if ("1".equals(isFrist)) {
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", "1");
                        intent.putExtra("isFrist", "1");
                        setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                        finish();
                    } else {
                        //赞助活动
                        Intent intent = new Intent();
                        intent.putExtra("visible_type", "1");
                        intent.putExtra("isFrist", "2");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

            }
            break;
            //仅自己可见
            case R.id.lin_onlymyselfs: {

                if (!Tools.isEmpty(isFrist)) {
                    if ("1".equals(isFrist)) {
                        Intent intent = new Intent();
                        intent.putExtra("invisible_type", "2");
                        intent.putExtra("isFrist", "1");
                        setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                        finish();
                    } else {
                        //赞助活动
                        Intent intent = new Intent();
                        intent.putExtra("visible_type", "2");
                        intent.putExtra("isFrist", "2");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

            }
            break;
            //对谁不可见
            case R.id.lin_invisible: {

                if (!Tools.isEmpty(isFrist)) {
                    if ("1".equals(isFrist)) {
                        //  Isvisible; //对谁可见 1   不可见 2
                        Intent intent = new Intent(this, ToWhomInVisibleActivity.class);
                        intent.putExtra("Isvisible", "2");
                        intent.putExtra("ischart", ischart);
                        startActivityForResult(intent, 0);
                    } else {
                        //赞助活动
                        Intent intent = new Intent(this, ToWhomInVisibleRedActivity.class);
                        intent.putExtra("visible_type", "3");
                        intent.putExtra("isFrist", "2");
                        if (!Tools.isEmpty(visible_type)) {
                            if (visible_type.equals("3")) {
                                intent.putExtra("city", city);
                            }
                        }
                        startActivityForResult(intent, 1);
                    }
                }

            }
            break;
            //对谁可见
            case R.id.lin_visible: {
                if (!Tools.isEmpty(isFrist)) {
                    if ("1".equals(isFrist)) {
                        Intent intent = new Intent(this, ToWhomInVisibleActivity.class);
                        intent.putExtra("Isvisible", "1");
                        intent.putExtra("ischart", ischart);
                        startActivityForResult(intent, 0);
                    } else {
                        //赞助活动
                        Intent intent = new Intent(this, ToWhomInVisibleRedActivity.class);
                        intent.putExtra("visible_type", "4");
                        intent.putExtra("isFrist", "2");
                        if (!Tools.isEmpty(visible_type)) {
                            if (visible_type.equals("4")) {
                                intent.putExtra("city", city);
                            }
                        }
                        startActivityForResult(intent, 1);
                    }
                }

            }
            break;
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }

    private String invisible_label;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ("1".equals(isFrist)) {
            if (resultCode == AppInfo.REQUEST_CODE_ALL) {
                switch (requestCode) {
                    case 0: {//可见或者是不可见
                        if (data != null) {
                            invisible_label = data.getStringExtra("invisible_label");
                            if (Tools.isEmpty(invisible_label)) {
                                String invisible_team = data.getStringExtra("invisible_team");
                                String outlet_package_type = data.getStringExtra("outlet_package_type");
                                String invisible_type = data.getStringExtra("invisible_type");
                                Intent intent = new Intent();
                                intent.putExtra("invisible_type", invisible_type);
                                intent.putExtra("invisible_team", invisible_team);
                                intent.putExtra("outlet_package_type", outlet_package_type);
                                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                                finish();
                            } else {
                                String usermobile_list = data.getStringExtra("usermobile_list");
                                String invisible_type = data.getStringExtra("invisible_type");
                                Intent intent = new Intent();
                                intent.putExtra("invisible_type", invisible_type);
                                intent.putExtra("usermobile_list", usermobile_list);
                                intent.putExtra("invisible_label", invisible_label);
                                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                                finish();
                            }
                        }
                    }
                    break;
                }
            } else {
                if (requestCode == 1 && RESULT_OK == resultCode) {
                    if (data != null) {
                        String city = data.getStringExtra("city");
                        String visible_type = data.getStringExtra("visible_type");
                        Intent intent = new Intent();
                        intent.putExtra("city", city);
                        intent.putExtra("visible_type", visible_type);
                        setResult(RESULT_OK, intent);
                        baseFinish();

                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}