package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_318.LargeImageActivity;
import com.orange.oy.activity.shakephoto_318.UploadPicturesActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CustomViewPager;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * beibei  查看大图页
 */
public class LargeImagePageActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private LinearLayout lin_teamSpeciality, lin_shrink, lin_onclick, lin_bottom;
    //    private FrameLayout lin_alls;
    //    private String file_url, address, create_time;
//    private String key_concent;
    private TextView tv_address, tv_time, tv_theme;
    private TextView tv_username;
    private ImageLoader imageLoader;
    private ImageView iv_tips, iv_del;
    //    private String ai_id, show_address;
//    private String IsHaveShare; //是否有分享 1=没有，2=有
//    private String IsHaveDelete; //是否有删除 1=没有，2=有
    private NetworkConnection delPhoto;
    //    private String fi_id, aitivity_name;
    private CustomViewPager lin_viewpager;
    private AppTitle appTitle;
    private String del_fi_id = "";

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.mydetail_title);
        appTitle.settingName("图片详情");
        appTitle.showBack(this);
        if (0 != isRight && isRight == 1) {
            appTitle.settingExit("上传", onExitClickForAppTitle2);
        }
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            if (!photoList.isEmpty()) {
                Tools.d("zpf", photoList.toString());
                Intent intent = new Intent(LargeImagePageActivity.this, UploadPicturesActivity.class);
                ArrayList<PhotoListBean> list1 = new ArrayList<>();
                String[] str = photoList.get(position).getFile_url().split(",");
                String province = photoList.get(position).getProvince();
                String city = photoList.get(position).getCity();
                String county = photoList.get(position).getCounty();
                String address = photoList.get(position).getAddress();
                String area = photoList.get(position).getArea();
                String latitude = photoList.get(position).getLatitude();
                String longitude = photoList.get(position).getLongitude();
                for (String aStr : str) {
                    PhotoListBean photoListBean = new PhotoListBean();
                    photoListBean.setFile_url(aStr);
                    photoListBean.setCheck(false);
                    photoListBean.setShow(false);
                    photoListBean.setProvince(province);
                    photoListBean.setCity(city);
                    photoListBean.setDai_id(photoList.get(position).getDai_id());
                    photoListBean.setCounty(county);
                    photoListBean.setLongitude(longitude);
                    photoListBean.setLatitude(latitude);
                    photoListBean.setArea(area);
                    photoListBean.setAddress(address);
                    list1.add(photoListBean);
                }
                intent.putExtra("photoList", list1);
                startActivity(intent);
                finish();
            }
        }
    };


    private void initNetworkConnection() {
        delPhoto = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImagePageActivity.this));
                params.put("fi_id", del_fi_id);  //	照片id【必传】
                return params;
            }
        };
        delPhoto.setIsShowDialog(true);
    }

    private View lip_detail_layout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image_page);

        imageLoader = new ImageLoader(this);
        lin_viewpager = (CustomViewPager) findViewById(R.id.lin_viewpager);
        lip_detail_layout = findViewById(R.id.lip_detail_layout);
        tv_theme = (TextView) findViewById(R.id.tv_theme);
        lin_teamSpeciality = (LinearLayout) findViewById(R.id.lin_teamSpeciality);
        lin_shrink = (LinearLayout) findViewById(R.id.lin_shrink);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_time = (TextView) findViewById(R.id.tv_time);
        lin_onclick = (LinearLayout) findViewById(R.id.lin_onclick);
//        lin_alls = (FrameLayout) findViewById(R.id.lin_alls);
        iv_tips = (ImageView) findViewById(R.id.iv_tips);
        iv_del = (ImageView) findViewById(R.id.iv_del);
        tv_username = (TextView) findViewById(R.id.tv_username);
        lin_bottom = (LinearLayout) findViewById(R.id.lin_bottom);
        initNetworkConnection();
        init();
        initTitle();
        lin_onclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    flag = false;
                    lin_shrink.setVisibility(View.VISIBLE);
                    iv_tips.setImageResource(R.mipmap.tpxq_button_shouqi);
                } else {
                    flag = true;
                    lin_shrink.setVisibility(View.GONE);
                    iv_tips.setImageResource(R.mipmap.tpxq_button_zhankai);
                }
            }
        });
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            if (photoView1 != null) {
                photoView1.setScale(1);
            }
            if (photoView2 != null) {
                photoView2.setScale(1);
            }
            if (photoView3 != null) {
                photoView3.setScale(1);
            }
            LargeImagePageActivity.this.position = position; //去赋值
            showDetail(largeImagePageInfoList.get(position));
        }

        public void onPageScrollStateChanged(int state) {
        }
    };

    private ArrayList<LargeImagePageInfo> largeImagePageInfoList = new ArrayList<>();
    private PhotoView photoView1;
    private PhotoView photoView2;
    private PhotoView photoView3;

    private class MyViewPageAdapter extends PagerAdapter {
        ArrayList<LargeImagePageInfo> largeImagePageInfos = new ArrayList<>();

        public MyViewPageAdapter(ArrayList<LargeImagePageInfo> largeImagePageInfos) {
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
//                imageLoader.cancelLoad((ImageView) view);
                container.removeView(view);
            }
            container.addView(view);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
//            if (object instanceof PhotoView) {
//                imageLoader.cancelLoad((ImageView) object);
//            }
//            container.removeView((View) object);
        }

        private PhotoView getPhotoView() {
            PhotoView photoView = new PhotoView(LargeImagePageActivity.this);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return photoView;
        }

        View instantiateItem(int position) {
            int index = position + 1;
            if (index > 3) {
                index = index % 3;
            }
            String url = largeImagePageInfos.get(position).getFile_url();
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
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
                }
                return photoView3;
            }
        }
    }

    private boolean flag = false;
    private int state = 0;//0:全部都有，1：只看图片其他隐藏
    private ArrayList<PhotoListBean> photoList = new ArrayList<>();
    private int position; //点击第几个
    private int isRight;//查看大图，如果有值的话，右上角有上传

    private void init() {
        Intent data = getIntent();
        boolean isList = data.getBooleanExtra("isList", false);
        position = data.getIntExtra("position", 0);
        isRight = data.getIntExtra("position", 0);
        state = data.getIntExtra("state", 0);
        photoList = (ArrayList<PhotoListBean>) data.getSerializableExtra("photoList");
        if (isList) {
            largeImagePageInfoList = (ArrayList<LargeImagePageInfo>) data.getSerializableExtra("list");
            if (state == 1) {
                lip_detail_layout.setVisibility(View.GONE);
            } else {
                lip_detail_layout.setVisibility(View.VISIBLE);
            }
        } else {
            LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
            if (state == 1) {//只看图片
                largeImagePageInfo.setFile_url(data.getStringExtra("file_url"));
            } else {
                largeImagePageInfo.setAi_id(data.getStringExtra("ai_id"));
                largeImagePageInfo.setFile_url(data.getStringExtra("file_url"));
                largeImagePageInfo.setAitivity_name(data.getStringExtra("aitivity_name"));
                largeImagePageInfo.setKey_concent(data.getStringExtra("key_concent"));
                largeImagePageInfo.setAddress(data.getStringExtra("address"));
                largeImagePageInfo.setCreate_time(data.getStringExtra("create_time"));
                largeImagePageInfo.setFi_id(data.getStringExtra("fi_id"));//照片id
                largeImagePageInfo.setIsHaveDelete(data.getStringExtra("IsHaveDelete"));//是否有删除 1=没有，2=有
                largeImagePageInfo.setShow_address(data.getStringExtra("show_address"));//1显示 0不显示
                largeImagePageInfo.setIsHaveShare(data.getStringExtra("IsHaveShare"));//是否有分享 1=没有，2=有
                largeImagePageInfo.setUser_name(data.getStringExtra("user_name"));
            }
            largeImagePageInfoList.add(largeImagePageInfo);
        }
        lin_viewpager.setAdapter(new MyViewPageAdapter(largeImagePageInfoList));
        lin_viewpager.addOnPageChangeListener(onPageChangeListener);
        lin_viewpager.setCurrentItem(position);
        showDetail(largeImagePageInfoList.get(position));
    }

    /**
     * 设置当前图片信息
     *
     * @param largeImagePageInfo
     */
    private void showDetail(LargeImagePageInfo largeImagePageInfo) {
        if (state == 1) {//只看图片
            lip_detail_layout.setVisibility(View.GONE);
            lin_bottom.setVisibility(View.GONE);
            return;
        }
        del_fi_id = largeImagePageInfo.getFi_id();
        final String webUrl = Urls.ShareActivity + "?&ai_id=" + largeImagePageInfo.getAi_id() + "&usermobile=" +
                AppInfo.getName(LargeImagePageActivity.this) + "&fi_id=" + largeImagePageInfo.getFi_id();
        if (!Tools.isEmpty(largeImagePageInfo.getIsHaveShare()) && largeImagePageInfo.getIsHaveShare().equals("2")) {
            appTitle.showIllustrate(R.mipmap.zdxx_button_fenxiang, new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    UMShareDialog.showDialog(LargeImagePageActivity.this, false, new UMShareDialog.UMShareListener() {
                        public void shareOnclick(int type) {
                            MyUMShareUtils.umShare(LargeImagePageActivity.this, type, webUrl);
                        }
                    });
                }
            });
        }
        if (!TextUtils.isEmpty(largeImagePageInfo.getUser_name())) {
            tv_username.setVisibility(View.VISIBLE);
            tv_username.setText("拍摄者：" + largeImagePageInfo.getUser_name());
        }
        if (!Tools.isEmpty(largeImagePageInfo.getShow_address())) {
            if (largeImagePageInfo.getShow_address().equals("1")) {
                if (!Tools.isEmpty(largeImagePageInfo.getAddress())) {
                    tv_address.setVisibility(View.VISIBLE);
                    tv_address.setText("照片位置：" + largeImagePageInfo.getAddress());
                }
            } else {
                tv_address.setVisibility(View.GONE);
            }

        } else {
            tv_address.setVisibility(View.VISIBLE);
        }
        if (!Tools.isEmpty(largeImagePageInfo.getCreate_time())) {
            tv_time.setVisibility(View.VISIBLE);
            tv_time.setText("拍摄时间：" + largeImagePageInfo.getCreate_time());
        } else {
            tv_time.setVisibility(View.GONE);
        }
        if (!Tools.isEmpty(largeImagePageInfo.getAitivity_name())) {
            tv_theme.setText(largeImagePageInfo.getAitivity_name());
        }
        if (!Tools.isEmpty(largeImagePageInfo.getKey_concent())) {
            lin_teamSpeciality.setVisibility(View.VISIBLE);
            String[] key_concent2 = largeImagePageInfo.getKey_concent().split(",");
            autoAddTab(key_concent2);
        } else {
            lin_teamSpeciality.setVisibility(View.GONE);
        }
        if (!Tools.isEmpty(largeImagePageInfo.getIsHaveDelete()) && largeImagePageInfo.getIsHaveDelete().equals("2")) {
            lin_bottom.setVisibility(View.VISIBLE);
            lin_bottom.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ConfirmDialog.showDialog(LargeImagePageActivity.this, "提示", 1, "确定删除这张图片吗？", "取消", "确定", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                        }

                        public void rightClick(Object object) {
                            delete();
                        }
                    });
                }
            });
        } else {
            lin_bottom.setVisibility(View.GONE);
            lin_bottom.setOnClickListener(null);
        }
    }

    protected void onStop() {
        super.onStop();
        if (delPhoto != null) {
            delPhoto.stop(Urls.DelPhoto);
        }
    }

    private void delete() {
        delPhoto.sendPostRequest(Urls.DelPhoto, new Response.Listener<String>() {
            public void onResponse(String s) {
//                ShakeAlbumActivity.isRefresh3 = true;
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(LargeImagePageActivity.this, "图片已删除");
                        int size = largeImagePageInfoList.size();
                        if (size <= 1) {
                            baseFinish();
                        } else {
                            int i = 0;
                            for (; i < size; i++) {
                                LargeImagePageInfo largeImagePageInfo = largeImagePageInfoList.get(i);
                                if (largeImagePageInfo.getFi_id().equals(del_fi_id)) {
                                    largeImagePageInfoList.remove(i);
                                    break;
                                }
                            }
                            lin_viewpager.setAdapter(new MyViewPageAdapter(largeImagePageInfoList));
                            lin_viewpager.setCurrentItem(i);
                        }
                    } else {
                        Tools.showToast(LargeImagePageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImagePageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImagePageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private void autoAddTab(String[] tabInfos) {
        lin_teamSpeciality.removeAllViews();
        final int mar = (int) getResources().getDimension(R.dimen.searchhot_tab_marginLeftRight2) + 1;//最外层左右边距
        final int tabMar = (int) getResources().getDimension(R.dimen.searchhot_tab_marginLeftRight2) + 1;//标签外的右边距
        final int tabHeight = (int) getResources().getDimension(R.dimen.searchhot_tab_height2);//标签高度
        final int tabTextmarg = (int) getResources().getDimension(R.dimen.searchhot_tab_text_margLeftRight2) + 1;//标签内边距
        final int windowWidth = Tools.getScreeInfoWidth(this) - mar;
        int layoutwidth = 0;
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tabParams.topMargin = (int) getResources().getDimension(R.dimen.searchhot_tab_marginLeftRight) + 1;//标签上边距
        LinearLayout tempLayout = new LinearLayout(this);
        tempLayout.setOrientation(LinearLayout.HORIZONTAL);
        lin_teamSpeciality.addView(tempLayout, tabParams);
        boolean isAddMar;
        for (String temp : tabInfos) {
            int length = temp.length();
            //------start--------设置标签样式
            TextView tv = new TextView(this);
            tv.setTextSize(10);
            tv.setTextColor(Color.parseColor("#FFFFFFFF"));
            tv.setText(temp);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.dialog_upload8);
            tv.setTag(temp);
            tv.setOnClickListener(hotTabOnClickListener);
            //------end--------设置标签样式
            TextPaint paint = tv.getPaint();
            int minus = length - 4;
            int textWidth;
            if (minus <= 0) {
                textWidth = (int) (tv.getTextSize() * 7) + 1;
            } else {
                textWidth = (int) (paint.measureText(temp) + tabTextmarg * 2) + 1;
            }
            layoutwidth = layoutwidth + textWidth + tabMar;
            isAddMar = true;
            if (layoutwidth >= windowWidth) {
                layoutwidth = layoutwidth - tabMar;
                if (layoutwidth >= windowWidth) {
                    layoutwidth = textWidth + tabMar;
                    tempLayout = new LinearLayout(this);
                    tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                    lin_teamSpeciality.addView(tempLayout, tabParams);
                } else {
                    isAddMar = false;
                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, tabHeight);
            if (isAddMar) {
                params.rightMargin = tabMar;
            }
            tempLayout.addView(tv, params);
        }
    }

    private View.OnClickListener hotTabOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //search_main_edit.setText(((HotTabInfo) v.getTag()).name);
        }
    };

    @Override
    public void onBack() {
        baseFinish();
    }
}
