package com.orange.oy.activity.createtask_321;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.WebpageCommentlistDialog;
import com.orange.oy.info.WebpageComListInfo;
import com.orange.oy.info.WebpagetaskDBInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CustomViewPager;
import com.orange.oy.view.WebpageCommentView;
import com.orange.oy.view.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/7.
 * 体验任务查看大图
 */

public class LargeImageWebtaskActivity extends BaseActivity {
    private CustomViewPager lin_viewpager;
    private WebpageCommentView limgwt_webpagecomment;
    private ImageLoader imageLoader;
    private String project_id = "", store_id = "", task_bath = "", task_id = "";
    private SystemDBHelper systemDBHelper;
    private View limgwt_bottom;
    private ImageView limgwt_i1;
    private TextView limgwt_txt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        project_id = getIntent().getStringExtra("project_id");
        store_id = getIntent().getStringExtra("store_id");
        task_bath = getIntent().getStringExtra("task_bath");
        task_id = getIntent().getStringExtra("task_id");
        nowPosition = getIntent().getIntExtra("position", 0);
        largeImagePageInfoList = (ArrayList<WebpagetaskDBInfo>) getIntent().getSerializableExtra("listinfo");
        systemDBHelper = new SystemDBHelper(this);
        setContentView(R.layout.activity_largeimagewebtask);
        imageLoader = new ImageLoader(this);
        AppTitle appTitle = (AppTitle) findViewById(R.id.limgwt_title);
        appTitle.settingName("查看大图");
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        if (largeImagePageInfoList == null) {
            largeImagePageInfoList = new SystemDBHelper(this).getWebpagephoto(project_id, store_id, task_id, task_bath,
                    AppInfo.getName(LargeImageWebtaskActivity.this));
        }
        limgwt_i1 = (ImageView) findViewById(R.id.limgwt_i1);
        limgwt_txt = (TextView) findViewById(R.id.limgwt_txt);
        limgwt_bottom = findViewById(R.id.limgwt_bottom);
        limgwt_webpagecomment = (WebpageCommentView) findViewById(R.id.limgwt_webpagecomment);
        lin_viewpager = (CustomViewPager) findViewById(R.id.limgwt_viewpager);
        lin_viewpager.setAdapter(new MyViewPageAdapter(largeImagePageInfoList));
        lin_viewpager.addOnPageChangeListener(onPageChangeListener);
        lin_viewpager.setCurrentItem(nowPosition);
        showDetail(largeImagePageInfoList.get(nowPosition));
        limgwt_webpagecomment.setOnWebpageCommentViewListener(new WebpageCommentView.OnWebpageCommentViewListener() {
            public void submit(int state, String comment) {
                limgwt_webpagecomment.setVisibility(View.GONE);
                limgwt_webpagecomment.setType(0);
                WebpagetaskDBInfo webpagetaskDBInfo = largeImagePageInfoList.get(nowPosition);
                webpagetaskDBInfo.setCommentState(state + "");
                webpagetaskDBInfo.setCommentTxt(comment);
                systemDBHelper.upWebpagephotoForComment(project_id, store_id, task_id, task_bath, AppInfo.getName(LargeImageWebtaskActivity.this)
                        , webpagetaskDBInfo);
                ((PhotoView) lin_viewpager.findViewWithTag(nowPosition)).
                        setImageBitmap(Tools.getBitmap(largeImagePageInfoList.get(nowPosition).getPath(), 1024, 1024));
                showDetail(webpagetaskDBInfo);
                WebpageCommentlistDialog webpageCommentlistDialog =
                        WebpageCommentlistDialog.ShowWebpageCommentlistDialog(LargeImageWebtaskActivity.this,
                                webpagetaskDBInfo.getWebUrl(), systemDBHelper.getWebpagephoto(project_id, store_id,
                                        task_id, task_bath, AppInfo.getName(LargeImageWebtaskActivity.this)));
                webpageCommentlistDialog.setOnWebpageComDialogunOnlinePraiseListener(
                        new WebpageCommentlistDialog.OnWebpageComDialogunOnlinePraiseListener() {
                            public void clickPraise(WebpageComListInfo webpageComListInfo) {
                                WebpagetaskDBInfo webpagetaskDBInfo1 = new WebpagetaskDBInfo();
                                webpagetaskDBInfo1.setIspraise(webpageComListInfo.getIs_praise());
                                webpagetaskDBInfo1.setPath(webpageComListInfo.getLocalpath());
                                systemDBHelper.upWebpagephotoForPraise(project_id, store_id, task_id, task_bath,
                                        AppInfo.getName(LargeImageWebtaskActivity.this), webpagetaskDBInfo1);
                            }
                        }
                );
            }

            public void screenshot() {
            }
        });
    }

    private ArrayList<WebpagetaskDBInfo> largeImagePageInfoList = new ArrayList<>();
    private PhotoView photoView1;
    private PhotoView photoView2;
    private PhotoView photoView3;
    private int nowPosition = 0;

    /**
     * 设置当前图片信息
     */
    private void showDetail(WebpagetaskDBInfo largeImagePageInfo) {
        if (new File(largeImagePageInfo.getPath()).exists() && TextUtils.isEmpty(largeImagePageInfo.getCommentState())) {
            limgwt_bottom.setVisibility(View.GONE);
            limgwt_webpagecomment.setVisibility(View.VISIBLE);
            limgwt_webpagecomment.setPath(largeImagePageInfo.getPath());
            limgwt_webpagecomment.setType(2);
        } else {
            limgwt_webpagecomment.setVisibility(View.GONE);
            limgwt_webpagecomment.setType(0);
            limgwt_bottom.setVisibility(View.VISIBLE);
            switch (largeImagePageInfo.getCommentState()) {
                case "1": {
                    limgwt_i1.setImageResource(R.mipmap.wbpgtk_i1);
                }
                break;
                case "2": {
                    limgwt_i1.setImageResource(R.mipmap.wbpgtk_i2);
                }
                break;
                case "3": {
                    limgwt_i1.setImageResource(R.mipmap.wbpgtk_i3);
                }
                break;
                case "4": {
                    limgwt_i1.setImageResource(R.mipmap.wbpgtk_i4);
                }
                break;
                case "5": {
                    limgwt_i1.setImageResource(R.mipmap.wbpgtk_i5);
                }
                break;
            }
            limgwt_txt.setText(largeImagePageInfo.getCommentTxt());
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            nowPosition = position;
            if (photoView1 != null) {
                photoView1.setScale(1);
            }
            if (photoView2 != null) {
                photoView2.setScale(1);
            }
            if (photoView3 != null) {
                photoView3.setScale(1);
            }
            showDetail(largeImagePageInfoList.get(position));
        }

        public void onPageScrollStateChanged(int state) {
        }
    };

    private class MyViewPageAdapter extends PagerAdapter {
        ArrayList<WebpagetaskDBInfo> largeImagePageInfos = new ArrayList<>();

        public MyViewPageAdapter(ArrayList<WebpagetaskDBInfo> largeImagePageInfos) {
            this.largeImagePageInfos = largeImagePageInfos;
        }

        public int getCount() {
            return largeImagePageInfos.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = instantiateItem(position);
            if (view.getParent() != null) {
                container.removeView(view);
            }
            container.addView(view);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        private PhotoView getPhotoView() {
            PhotoView photoView = new PhotoView(LargeImageWebtaskActivity.this);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return photoView;
        }

        View instantiateItem(int position) {
            int index = position + 1;
            if (index > 3) {
                index = index % 3;
            }
            WebpagetaskDBInfo webpagetaskDBInfo = largeImagePageInfos.get(position);
            String url = webpagetaskDBInfo.getPath();
            if (url != null && (!(url.startsWith("http://") || url.startsWith("https://")))) {
                if (!new File(url).exists()) {
                    url = Urls.Endpoint3 + url;
                }
            }
            switch (index) {
                case 1: {
                    if (photoView1 != null) {
                        photoView1.setScale(1);
                        imageLoader.setShowWH(1024).DisplayImage(url, photoView1);
                    } else {
                        photoView1 = getPhotoView();
                        imageLoader.setShowWH(1024).DisplayImage(url, photoView1);
                    }
                    photoView1.setTag(position);
                }
                return photoView1;
                case 2: {
                    if (photoView2 != null) {
                        photoView2.setScale(1);
                        imageLoader.setShowWH(1024).DisplayImage(url, photoView2);
                    } else {
                        photoView2 = getPhotoView();
                        imageLoader.setShowWH(1024).DisplayImage(url, photoView2);
                    }
                    photoView2.setTag(position);
                }
                return photoView2;
                default: {
                    if (photoView3 != null) {
                        photoView3.setScale(1);
                        imageLoader.setShowWH(1024).DisplayImage(url, photoView3);
                    } else {
                        photoView3 = getPhotoView();
                        imageLoader.setShowWH(1024).DisplayImage(url, photoView3);
                    }
                    photoView3.setTag(position);
                }
                return photoView3;
            }
        }
    }
}
