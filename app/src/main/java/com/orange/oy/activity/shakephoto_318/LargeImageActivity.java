package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.MyUMShareUtils;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.DiscussListDialog2;
import com.orange.oy.dialog.InformDialog;
import com.orange.oy.dialog.UMShareDialog;
import com.orange.oy.info.CommentListInfo;
import com.orange.oy.info.LargeImageInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.CustomViewPager;
import com.orange.oy.view.photoview.PhotoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;


/**
 * V3.18 查看大图 和广告大图
 */
public class LargeImageActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, View.OnClickListener, GestureDetector.OnGestureListener {

    private TextView tv_address, tv_theme;
    private TextView tv_username;
    private ImageLoader imageLoader;
    private ImageView iv_tips, iv_del;

    private NetworkConnection showAdvertisement, praisePhoto, shareSuccess, praiseAd, informPhoto, commentList, adCommentList;
    private CustomViewPager lin_viewpager;
    private AppTitle appTitle;


    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.mydetail_title);
        appTitle.settingName("查看大图");
        appTitle.showBack(this);
    }

    private String view_type, praise, inform_reason, inform_type;

    private void initNetworkConnection() {

        // 	图片评论信息页接口
        commentList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImageActivity.this));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("fi_id", fi_id);
                params.put("page", "1"); //页码，从1开始（每页15条）
                return params;
            }
        };

        //广告图片评论信息页面
        adCommentList = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImageActivity.this));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("sai_id", sai_id);  //sai_id	广告id【必传】
                params.put("page", "1"); //页码，从1开始（每页15条）
                return params;
            }
        };

        //举报投诉照片
        informPhoto = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImageActivity.this));
                params.put("fi_id", largeImageInfo.getFi_id());  //照片主键id
                params.put("type", "1"); // App传1，小程序传2【必传】
                params.put("inform_reason", inform_reason);//举报原因
                params.put("inform_type", inform_type);  //	举报类型，多个以英文逗号分隔
                return params;
            }
        };
        //广告查看接口
        showAdvertisement = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImageActivity.this));
                params.put("sai_id", largeImageInfo.getSai_id());  //广告的赞助信息id【必传】
                params.put("view_type", view_type); // （1：查看大图；2：查看详情）【必传】
                return params;
            }
        };
        // 图片点赞或取消赞接口
        praisePhoto = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImageActivity.this));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("fi_id", largeImageInfo.getFi_id()); //    fi_id	文件id【必传】
                params.put("praise", praise); //  是否是赞，1为赞，0为不赞
                return params;
            }
        };
        //广告图片点赞或取消赞接口
        praiseAd = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImageActivity.this));
                params.put("type", "1");  //App传1，小程序传2【必传】
                params.put("sai_id", largeImageInfo.getSai_id()); //   sai_id	广告id【必传】
                params.put("praise", praise); //  是否是赞，1为赞，0为不赞
                return params;
            }
        };
        //分享成功接口
        shareSuccess = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(LargeImageActivity.this));
                params.put("is_advertisement", is_advertisement);  //是否是广告图（1为是，0为否)
                if (!Tools.isEmpty(largeImageInfo.getSai_id())) {
                    params.put("sai_id", largeImageInfo.getSai_id()); //   广告的赞助信息id【当为广告图时必传】
                }
                if (!Tools.isEmpty(largeImageInfo.getFi_id())) {
                    params.put("fi_id", largeImageInfo.getFi_id()); //  图片id【当不是广告图时必传】
                }
                return params;
            }
        };

    }

    //分享成功
    private void sharesuccess() {
        shareSuccess.stop(Urls.ShareSuccess);
        shareSuccess.sendPostRequest(Urls.ShareSuccess, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //分享数字 +1
                        largeImagePageInfoList.get(position).setShare_num(Tools.StringToInt(share_nums) + 1 + " ");
                        share_num.setText(largeImagePageInfoList.get(position).getShare_num());

                        //刷新
                        //  Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(LargeImageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //图片点赞成功
    private void Oiliest() {
        praisePhoto.sendPostRequest(Urls.PraisePhoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //adapter.notifyDataSetChanged();
                        //刷新
                        // Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(LargeImageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //举报图片成功
    private void inform() {
        informPhoto.sendPostRequest(Urls.InformPhoto, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //adapter.notifyDataSetChanged();
                        //刷新
                        Tools.showToast(getBaseContext(), "举报成功~");
                    } else {
                        Tools.showToast(LargeImageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //广告图片点赞成功
    private void Oiliest2() {
        praiseAd.sendPostRequest(Urls.PraiseAd, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //adapter.notifyDataSetChanged();
                        //刷新
                        //  Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(LargeImageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    //广告查看接口
    private void ShowAdvertisement() {
        showAdvertisement.sendPostRequest(Urls.ShowAdvertisement, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.d("查看成功~");
                        //Tools.showToast(getBaseContext(), jsonObject.getString("msg"));
                    } else {
                        Tools.showToast(LargeImageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    private LinearLayout lin_all, lin_talk, lin_share, lin_bottom, lin_inform, lin_poplist;
    private CircularImageView iv_header;
    private ImageView iv_RedpeachHeart;
    private TextView tv_greatNumber, talk_num, share_num, tv_details;
    private ImageView iv_inform, iv_pic, iv_redlike;
    private View tv_dialog_layout;
    private TextView tv_dialog_txt;
    private boolean isLogin = false;
    private TextView tv_name, tv_time, tv_des, tv_red_num;
    private LinearLayout main;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        initTitle();
        //创建手势检测器
        detector = new GestureDetector(this, this);
        isLogin = (!TextUtils.isEmpty(AppInfo.getName(LargeImageActivity.this)));
        imageLoader = new ImageLoader(this);
        lin_viewpager = (CustomViewPager) findViewById(R.id.lin_viewpager);
        lin_inform = (LinearLayout) findViewById(R.id.lin_inform);
        lin_all = (LinearLayout) findViewById(R.id.lin_all);
        iv_inform = (ImageView) findViewById(R.id.iv_inform);
        iv_header = (CircularImageView) findViewById(R.id.iv_header);
        iv_RedpeachHeart = (ImageView) findViewById(R.id.iv_RedpeachHeart); //点赞红心
        tv_greatNumber = (TextView) findViewById(R.id.tv_greatNumber); //点赞数
        lin_talk = (LinearLayout) findViewById(R.id.lin_talk);
        talk_num = (TextView) findViewById(R.id.talk_num);
        lin_share = (LinearLayout) findViewById(R.id.lin_share);
        share_num = (TextView) findViewById(R.id.share_num);
        lin_bottom = (LinearLayout) findViewById(R.id.lin_bottom);
        tv_details = (TextView) findViewById(R.id.tv_details);
        iv_inform = (ImageView) findViewById(R.id.iv_inform);
        tv_dialog_layout = findViewById(R.id.tv_dialog_layout);
        tv_dialog_txt = (TextView) findViewById(R.id.tv_dialog_txt);
        main = (LinearLayout) findViewById(R.id.main);
        lin_poplist = (LinearLayout) findViewById(R.id.lin_poplist);
        iv_pic = (ImageView) findViewById(R.id.iv_pics);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_des = (TextView) findViewById(R.id.tv_des);
        tv_red_num = (TextView) findViewById(R.id.tv_red_num);
        iv_redlike = (ImageView) findViewById(R.id.iv_redlike);

        iv_header.setOnClickListener(this);
        iv_inform.setOnClickListener(this);
        iv_RedpeachHeart.setOnClickListener(this);
        lin_talk.setOnClickListener(this);
        lin_share.setOnClickListener(this);
        tv_details.setOnClickListener(this);
        lin_inform.setOnClickListener(this);
        iv_inform.setOnClickListener(this);
        lin_poplist.setOnClickListener(this);
        initNetworkConnection();
        init();


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
            LargeImageActivity.this.position = position;
            showDetail(largeImagePageInfoList.get(position));
        }

        public void onPageScrollStateChanged(int state) {
        }
    };

    private ArrayList<LargeImageInfo> largeImagePageInfoList = new ArrayList<>();
    private PhotoView photoView1;
    private PhotoView photoView2;
    private PhotoView photoView3;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_poplist: {
                DiscussListDialog2.showDialog(this, false, largeImageInfo.getFi_id(), largeImageInfo.getSai_id(), is_advertisement, new DiscussListDialog2.OnPrizeSettingListener() {
                    @Override
                    public void firstClick(int n) {
                        int num = n;
                        Tools.d("tag", num + "");
                        if (num != 0 && num > Tools.StringToInt(largeImagePageInfoList.get(position).getComment_num())) {
                            //显示
                            iv_header.setVisibility(View.VISIBLE);
                            largeImagePageInfoList.get(position).setComment_state("1");//评论过
                        } else {
                            String state = largeImagePageInfoList.get(position).getComment_state();
                            if (!Tools.isEmpty(largeImageInfo.getPraise_state()) && "1".equals(largeImageInfo.getPraise_state())
                                    || "1".equals(state)) {
                                iv_header.setVisibility(View.VISIBLE);
                            } else {
                                iv_header.setVisibility(View.GONE);
                            }
                        }
                        largeImagePageInfoList.get(position).setComment_num(num + "");
                        talk_num.setText(num + "");
                    }

                    @Override
                    public void secondClick() {

                    }
                });
            }
            break;
            case R.id.iv_header: {
                Intent intent = new Intent(this, RankingDetailActivity.class);
                intent.putExtra("join_usermobile", largeImageInfo.getUser_mobile());
                intent.putExtra("user_img", largeImageInfo.getUser_img());
                intent.putExtra("user_name", largeImageInfo.getUser_name());
                intent.putExtra("acname", largeImageInfo.getAcname());
                intent.putExtra("ai_id", largeImageInfo.getAi_id());
                startActivity(intent);
            }
            break;
            case R.id.iv_inform: {
                if (TextUtils.isEmpty(AppInfo.getKey(LargeImageActivity.this))) {
                    ConfirmDialog.showDialog(LargeImageActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    Intent intent = new Intent(LargeImageActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                InformDialog.showDialog(LargeImageActivity.this, "举报", false, new InformDialog.OnDataUploadClickListener() {
                    @Override
                    public void firstClick() {
                        InformDialog.dissmisDialog();
                    }

                    @Override
                    public void secondClick(String nums, String text) { //确定按钮
                        inform_reason = text;
                        inform_type = nums;
                        inform();
                    }
                });
            }
            break;
            //点赞
            case R.id.iv_RedpeachHeart: {
                if (TextUtils.isEmpty(AppInfo.getKey(LargeImageActivity.this))) {
                    ConfirmDialog.showDialog(LargeImageActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    Intent intent = new Intent(LargeImageActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }
                int num;
                if (largeImageInfo.getPraise_state().equals("0")) {
                    iv_header.setVisibility(View.VISIBLE);

                    praise = "1";
                    largeImagePageInfoList.get(position).setPraise_state("1");
                    largeImageInfo.setPraise_state("1");
                    if (is_advertisement.equals("0")) {//  是否是广告图片  1 是广告图
                        Oiliest();
                    } else {
                        Oiliest2();
                    }
                    int Num = Integer.parseInt(largeImagePageInfoList.get(position).getPraise_num());
                    num = Num + 1;
                    largeImagePageInfoList.get(position).setPraise_num(num + "");
                    tv_greatNumber.setText(num + "");
                    iv_RedpeachHeart.setImageResource(R.mipmap.ckdt_button_zanhong);
                } else if (largeImageInfo.getPraise_state().equals("1")) {
                    //取消赞，要判断是否评论
                    if (!Tools.isEmpty(largeImagePageInfoList.get(position).getComment_state()) && "1".equals(largeImagePageInfoList.get(position).getComment_state())) {
                        iv_header.setVisibility(View.VISIBLE);
                    } else {
                        iv_header.setVisibility(View.GONE);
                    }
                    praise = "0";
                    largeImagePageInfoList.get(position).setPraise_state("0");
                    largeImageInfo.setPraise_state("0");
                    if (is_advertisement.equals("0")) {//  是否是广告图片  1 是广告图
                        Oiliest();
                    } else {
                        Oiliest2();
                    }
                    int Num = Integer.parseInt(largeImagePageInfoList.get(position).getPraise_num());
                    num = Num - 1;
                    largeImagePageInfoList.get(position).setPraise_num(num + "");
                    tv_greatNumber.setText(num + "");
                    iv_RedpeachHeart.setImageResource(R.mipmap.ckdt_button_zanhui);
                }
            }
            break;
            // 评论
            case R.id.lin_talk: {
                if (TextUtils.isEmpty(AppInfo.getKey(LargeImageActivity.this))) {
                    ConfirmDialog.showDialog(LargeImageActivity.this, null, 2,
                            getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                }

                                public void rightClick(Object object) {
                                    Intent intent = new Intent(LargeImageActivity.this, IdentifycodeLoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    return;
                }

                DiscussListDialog2.showDialog(this, false, largeImageInfo.getFi_id(), largeImageInfo.getSai_id(), is_advertisement, new DiscussListDialog2.OnPrizeSettingListener() {
                    @Override
                    public void firstClick(int n) {
                        int num = n;
                        Tools.d("tag", num + "");
                        if (num != 0 && num > Tools.StringToInt(largeImagePageInfoList.get(position).getComment_num())) {
                            //显示
                            iv_header.setVisibility(View.VISIBLE);
                            largeImagePageInfoList.get(position).setComment_state("1");//评论过
                        } else {
                            String state = largeImagePageInfoList.get(position).getComment_state();
                            if (!Tools.isEmpty(largeImageInfo.getPraise_state()) && "1".equals(largeImageInfo.getPraise_state())
                                    || "1".equals(state)) {
                                iv_header.setVisibility(View.VISIBLE);
                            } else {
                                iv_header.setVisibility(View.GONE);
                            }
                        }
                        largeImagePageInfoList.get(position).setComment_num(num + "");
                        talk_num.setText(num + "");
                    }

                    @Override
                    public void secondClick() {

                    }
                });

               /* Intent intent = new Intent(this, DiscussListView.class);
                intent.putExtra("fi_id", largeImageInfo.getFi_id());
                intent.putExtra("sai_id", largeImageInfo.getSai_id());
                intent.putExtra("is_advertisement", is_advertisement); //1 是广告图片
                startActivityForResult(intent, 1);*/
            }
            break;
            //分享
            case R.id.lin_share: {
                UMShareDialog.showDialog(LargeImageActivity.this, false, new UMShareDialog.UMShareListener() {
                    public void shareOnclick(int type) {
                        sharesuccess();
                        MyUMShareUtils.umShare_shakephoto(LargeImageActivity.this, type, webUrl);
                    }
                }, true);
            }
            break;
            //点击查看详情 ---进入广告url
            case R.id.tv_details: {
                if (!Tools.isEmpty(ad_links)) {
                    view_type = "2";
                    ShowAdvertisement();
                    Uri uri = Uri.parse(ad_links);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
            break;

        }
    }

    /**
     * 读取GIF 文件头
     */
    boolean checkPicture(String path) {
        boolean result = false;
        FileInputStream is = null;
        try {
            is = new FileInputStream(path);
            String id = "";
            for (int i = 0; i < 6; i++) {
                id += (char) is.read();
            }
            result = id.toUpperCase().startsWith("GIF");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }


    private class MyViewPageAdapter extends PagerAdapter {
        ArrayList<LargeImageInfo> largeImagePageInfos = new ArrayList<>();

        public MyViewPageAdapter(ArrayList<LargeImageInfo> largeImagePageInfos) {
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
//            if (object instanceof PhotoView) {
//                imageLoader.cancelLoad((ImageView) object);
//            }
//            container.removeView((View) object);
        }

        private PhotoView getPhotoView() {
            PhotoView photoView = new PhotoView(LargeImageActivity.this);
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
                    url = Urls.Endpoint3 + url + "?x-oss-process=image/resize,l_1024";
                }
            }
            switch (index) {
                case 1: {
                    if (photoView1 != null) {
                        photoView1.setScale(1);
                        imageLoader.setGif(true).setShowWH(1024).DisplayImage(url, photoView1);
                    } else {
                        photoView1 = getPhotoView();
                        imageLoader.setGif(true).setShowWH(1024).DisplayImage(url, photoView1);
                    }
                }
                return photoView1;
                case 2: {
                    if (photoView2 != null) {
                        photoView2.setScale(1);
                        imageLoader.setGif(true).setShowWH(1024).DisplayImage(url, photoView2);
                    } else {
                        photoView2 = getPhotoView();
                        imageLoader.setGif(true).setShowWH(1024).DisplayImage(url, photoView2);
                    }
                }
                return photoView2;
                default: {
                    if (photoView3 != null) {
                        photoView3.setScale(1);
                        imageLoader.setGif(true).setShowWH(1024).DisplayImage(url, photoView3);
                    } else {
                        photoView3 = getPhotoView();
                        imageLoader.setGif(true).setShowWH(1024).DisplayImage(url, photoView3);
                    }
                }
                return photoView3;
            }
        }

    }

    private int position; //点击第几个

    private void init() {
        Intent data = getIntent();
        position = data.getIntExtra("position", 0);
        largeImagePageInfoList = (ArrayList<LargeImageInfo>) data.getSerializableExtra("list");

        lin_viewpager.setAdapter(new MyViewPageAdapter(largeImagePageInfoList));
        lin_viewpager.addOnPageChangeListener(onPageChangeListener);
        lin_viewpager.setCurrentItem(position);
        showDetail(largeImagePageInfoList.get(position));
    }

    private String webUrl; //分享的url
    private String ad_links, fi_id, sai_id;
    private int praise_num;
    private String share_nums;
    private String is_advertisement;  //是否是广告图（1为是，0为否)
    //    private String comment_state; // 是否评论过（0：未评论，1：已评论
    private LargeImageInfo largeImageInfo;

    /**
     * 设置当前图片信息 赋值
     *
     * @param info
     */
    private void showDetail(LargeImageInfo info) {
        this.largeImageInfo = info;
        imageLoader.DisplayImage(Urls.ImgIp + largeImageInfo.getUser_img(), iv_header, R.mipmap.grxx_icon_mrtx);

        if (!Tools.isEmpty(largeImageInfo.getPraise_state()) && "1".equals(largeImageInfo.getPraise_state()) ||
                !Tools.isEmpty(largeImageInfo.getComment_state()) && "1".equals(largeImageInfo.getComment_state())) {
            iv_header.setVisibility(View.VISIBLE);
        } else {
            iv_header.setVisibility(View.GONE);
        }

        if (!Tools.isEmpty(largeImageInfo.getPraise_num())) {
            praise_num = Integer.parseInt(largeImageInfo.getPraise_num());
        }
        is_advertisement = largeImageInfo.getIs_advertisement();
        fi_id = largeImageInfo.getFi_id();
        sai_id = largeImageInfo.getSai_id();


        //是否是广告图，1为是，0为否
        if ("1".equals(is_advertisement)) {
            appTitle.settingExit("", null);
            view_type = "1";
            ShowAdvertisement();
            lin_inform.setVisibility(View.INVISIBLE);
            lin_share.setVisibility(View.INVISIBLE);
            webUrl = Urls.ShareActivityIndex + "?&ai_id=" + largeImageInfo.getAi_id() + "&fi_id=" +
                    largeImageInfo.getFi_id() + "&sai_id=" + largeImageInfo.getSai_id() + "&type=3";
        } else {
            appTitle.settingExit("下载原图", new AppTitle.OnExitClickForAppTitle() {
                public void onExit() {
                    String url = LargeImageActivity.this.largeImageInfo.getFile_url();
                    if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                        if (!new File(url).exists()) {
                            url = Urls.Endpoint3 + url;
                        }
                    }
                    new DownImageAsynctask(url, largeImageInfo.getUser_name()).executeOnExecutor(Executors.newCachedThreadPool());
                }
            });
            lin_inform.setVisibility(View.VISIBLE);
            lin_share.setVisibility(View.VISIBLE);
            webUrl = Urls.ShareActivityIndex + "?&ai_id=" + largeImageInfo.getAi_id() + "&fi_id=" +
                    largeImageInfo.getFi_id() + "&sai_id=" + largeImageInfo.getSai_id() + "&type=4";
        }

        if (is_advertisement.equals("0") || Tools.isEmpty(largeImageInfo.getAd_links())) {
            tv_details.setVisibility(View.GONE);
        } else {
            if (!largeImageInfo.getAd_links().startsWith("http://") &&
                    !largeImageInfo.getAd_links().startsWith("https://")) {
                largeImageInfo.setAd_links("http://" + largeImageInfo.getAd_links());
            }
            tv_details.setVisibility(View.VISIBLE);
        }
        if (largeImageInfo.getPraise_state().equals("0")) {  // 是否点赞过（0：未点赞，1：已点赞）
            iv_RedpeachHeart.setImageResource(R.mipmap.ckdt_button_zanhui);
        } else if (largeImageInfo.getPraise_state().equals("1")) {
            iv_RedpeachHeart.setImageResource(R.mipmap.ckdt_button_zanhong);
        }
        if (!Tools.isEmpty(largeImageInfo.getPraise_num())) {
            tv_greatNumber.setText(largeImageInfo.getPraise_num());
        }
        if (!Tools.isEmpty(largeImageInfo.getComment_num())) {
            talk_num.setText(largeImageInfo.getComment_num());
        }
        share_nums = largeImageInfo.getShare_num();
        if (!Tools.isEmpty(share_nums)) {
            share_num.setText(share_nums);
        }
        if (!Tools.isEmpty(largeImageInfo.getAd_links())) {
            ad_links = largeImageInfo.getAd_links();
        }

        if (!Tools.isEmpty(is_advertisement)) {
            if (is_advertisement.equals("1")) {
                getData2();
            } else if (is_advertisement.equals("0")) {
                getData();
            }
        }

    }

    protected void onStop() {
        super.onStop();
        if (showAdvertisement != null) {
            showAdvertisement.stop(Urls.ShowAdvertisement);
        }
        if (praisePhoto != null) {
            praisePhoto.stop(Urls.PraisePhoto);
        }
        if (shareSuccess != null) {
            shareSuccess.stop(Urls.ShareSuccess);
        }
        if (praiseAd != null) {
            praiseAd.stop(Urls.PraiseAd);
        }

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("list", largeImagePageInfoList);
        setResult(RESULT_OK, intent);
        baseFinish();
    }

    public void onBack() {
        Intent intent = new Intent();
        intent.putExtra("list", largeImagePageInfoList);
        setResult(RESULT_OK, intent);
        baseFinish();
    }

    /**
     * 下载图片线程
     */
    private class DownImageAsynctask extends AsyncTask {
        private String url, name;

        DownImageAsynctask(String path, String name) {
            this.url = path;
            this.name = name;
        }

        protected void onPreExecute() {
            tv_dialog_layout.setVisibility(View.VISIBLE);
            tv_dialog_txt.setText("照片正在下载中，请稍候…");
        }

        protected Object doInBackground(Object[] objects) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                boolean isGif = false;
                int index = url.indexOf("?");
                if (index > -1) {
                    try {
                        isGif = url.substring(index - 4, index).equals(".gif");
                    } catch (Exception e1) {
                        isGif = false;
                    }
                } else {
                    isGif = url.endsWith(".gif");
                }
                Uri uri = null;
                if (isGif) {
                    uri = imageLoader.getUri(url);
                    isGif = checkPicture(uri.getPath());
                }
                if (isGif) {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".gif";
                    fis = new FileInputStream(uri.getPath());
                    fos = new FileOutputStream(path);
                    byte[] bytes = new byte[1024];
                    int ids = 0;
                    while ((ids = fis.read(bytes)) > -1) {
                        fos.write(bytes, 0, ids);
                    }
                    fos.flush();
                    IOUtils.safeClose(fos);
                    IOUtils.safeClose(fis);
                    return path;
                } else {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg";
                    Bitmap bitmap = imageLoader.setShowWH(1024).getBitmap(url);
                    if (bitmap != null) {
                        int dip30 = Tools.dipToPx(LargeImageActivity.this, 30);
                        Bitmap newBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
                        bitmap.recycle();
                        int height = newBitmap.getHeight();
                        int width = newBitmap.getWidth();
                        Canvas canvas = new Canvas(newBitmap);
                        Bitmap bottomBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.down_water);
                        bottomBitmap = zoomImg(bottomBitmap, width, width * bottomBitmap.getHeight() / bottomBitmap.getWidth());
                        canvas.drawBitmap(bottomBitmap, 0, height - bottomBitmap.getHeight(), new Paint());
                        //画ico
                        Bitmap loginbitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.login_icon);
//                    int loginWH = bottomBitmap.getHeight() - dip30;
//                    if (loginWH < dip30) {
//                        if (bottomBitmap.getHeight() > dip30) {
//                            loginWH = dip30;
//                        }
//                    }
                        int loginWH = bottomBitmap.getHeight() / 2;
                        loginbitmap = zoomImg(loginbitmap, loginWH, loginWH);
                        int loginbitmap_Top = height - (bottomBitmap.getHeight() - loginbitmap.getHeight()) / 2 - loginbitmap.getHeight();
                        canvas.drawBitmap(loginbitmap, Tools.dipToPx(LargeImageActivity.this, 14), loginbitmap_Top, new Paint());
                        //写字
                        Paint wPaint = new Paint();
                        wPaint.setAlpha(100);
                        wPaint.setColor(Color.WHITE);
                        wPaint.setDither(true);
                        wPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        wPaint.setStrokeJoin(Paint.Join.ROUND);
                        wPaint.setTextSize(bottomBitmap.getHeight() / 6);
                        int txtLeftM = Tools.dipToPx(LargeImageActivity.this, 14) + loginWH + Tools.dipToPx(LargeImageActivity.this, 8);
                        if (!TextUtils.isEmpty(name)) {
                            canvas.drawText("酔神@" + name, txtLeftM, loginbitmap_Top + loginWH, wPaint);
                        }
                        wPaint.setTextSize((bottomBitmap.getHeight() / 4));
                        canvas.drawText("偶业-酔吧", txtLeftM, loginbitmap_Top + wPaint.getTextSize(), wPaint);
                        wPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        canvas.drawText("发现极致乐趣", txtLeftM + wPaint.getTextSize() * 5 + dip30,
                                loginbitmap_Top + wPaint.getTextSize(), wPaint);
                        canvas.save(Canvas.ALL_SAVE_FLAG);
                        canvas.restore();
                        fos = new FileOutputStream(path);
                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        IOUtils.safeClose(fos);
                        try {
                            bottomBitmap.recycle();
                            loginbitmap.recycle();
                        } catch (Exception e) {
                            Tools.d(e.getMessage());
                        }
                        return path;
                    } else {
                        return null;
                    }
                }
            } catch (OutOfMemoryError e) {
                Tools.d(e.getMessage());
            } catch (Exception e) {
                Tools.d(e.getMessage());
            } finally {
                IOUtils.safeClose(fos);
                IOUtils.safeClose(fis);
            }
            return null;
        }

        Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
            // 获得图片的宽高
            int width = bm.getWidth();
            int height = bm.getHeight();
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            bm.recycle();
            return newbm;
        }

        protected void onPostExecute(Object o) {
            myHandler.sendEmptyMessageDelayed(0, 1000);
            if (o != null) {
                tv_dialog_txt.setText("下载完成\n可到您的手机相册查看");
                try {
                    File f = new File(o + "");
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            o + "", f.getName(), null);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + o)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                tv_dialog_txt.setText("下载失败\n请尝试重新下载");
            }
        }
    }

    private MyHandler myHandler = new MyHandler();

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            tv_dialog_layout.setVisibility(View.GONE);
        }
    }

    private ArrayList<CommentListInfo> listInfos; //评论列表

    //列表
    private void getData() {  //评论信息接口
        commentList.sendPostRequest(Urls.CommentList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (listInfos == null) {
                            listInfos = new ArrayList<CommentListInfo>();
                        } else {
                            listInfos.clear();
                        }
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.optJSONArray("comment_list");
                            if (jsonArray != null) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    CommentListInfo commentListInfo = new CommentListInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    commentListInfo.setComment_id(object.getString("comment_id"));
                                    commentListInfo.setContent(object.getString("content"));
                                    commentListInfo.setCreate_time(object.getString("create_time"));
                                    commentListInfo.setIs_praise(object.getString("is_praise")); // "is_praise ":"是否点过赞，1为点过，0为没点过"
                                    commentListInfo.setPraise_num(object.getInt("praise_num"));
                                    commentListInfo.setUser_img(object.getString("user_img"));
                                    commentListInfo.setUser_name(object.getString("user_name"));  //
                                    commentListInfo.setComment_username(object.getString("comment_username"));
                                    listInfos.add(commentListInfo);
                                }
                                //*************************************
                                if (listInfos.size() == 0) {
                                    lin_poplist.setVisibility(View.GONE);
                                } else {
                                    lin_poplist.setVisibility(View.VISIBLE);

                                    Glide.with(LargeImageActivity.this)
                                            .load(Urls.ImgIp + listInfos.get(0).getUser_img())
                                            .asBitmap()  //这句不能少，否则下面的方法会报错
                                            .centerCrop()
                                            .into(new BitmapImageViewTarget(iv_pic) {
                                                @Override
                                                protected void setResource(Bitmap resource) {
                                                    RoundedBitmapDrawable circularBitmapDrawable =
                                                            RoundedBitmapDrawableFactory.create(getBaseContext().getResources(), resource);
                                                    circularBitmapDrawable.setCircular(true);
                                                    iv_pic.setImageDrawable(circularBitmapDrawable);
                                                }
                                            });
                                    tv_time.setText(listInfos.get(0).getCreate_time());
                                    tv_name.setText(listInfos.get(0).getUser_name());
                                    tv_red_num.setText(listInfos.get(0).getPraise_num() + "");

                                    /*****  is_praise  : 是否点过赞，1为点过，0为没点过   **************/
                                    if (listInfos.get(0).getIs_praise().equals("0")) {
                                        iv_redlike.setImageResource(R.mipmap.ckdt_button_zanhui);
                                    } else if (listInfos.get(0).getIs_praise().equals("1")) {
                                        iv_redlike.setImageResource(R.mipmap.ckdt_button_zanhong);
                                    }
                                }


                            } else {
                                lin_poplist.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        Tools.showToast(LargeImageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    //列表
    private void getData2() {  //评论信息接口
        adCommentList.sendPostRequest(Urls.AdCommentList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        if (listInfos == null) {
                            listInfos = new ArrayList<CommentListInfo>();
                        } else {
                            listInfos.clear();
                        }
                        if (!jsonObject.isNull("data")) {
                            try {
                                jsonObject = jsonObject.getJSONObject("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JSONArray jsonArray = jsonObject.optJSONArray("comment_list");
                            if (jsonArray != null) {
                                lin_poplist.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    CommentListInfo commentListInfo = new CommentListInfo();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    commentListInfo.setComment_id(object.getString("comment_id"));
                                    commentListInfo.setContent(object.getString("content"));
                                    commentListInfo.setCreate_time(object.getString("create_time"));
                                    commentListInfo.setIs_praise(object.getString("is_praise")); // "is_praise ":"是否点过赞，1为点过，0为没点过"
                                    commentListInfo.setPraise_num(object.getInt("praise_num"));
                                    commentListInfo.setUser_img(object.getString("user_img"));
                                    commentListInfo.setUser_name(object.getString("user_name"));
                                    commentListInfo.setComment_username(object.getString("comment_username"));
                                    listInfos.add(commentListInfo);
                                }
                                if (listInfos.size() == 0) {
                                    lin_poplist.setVisibility(View.GONE);
                                } else {
                                    lin_poplist.setVisibility(View.VISIBLE);
                                    //*************************************
                                    Glide.with(LargeImageActivity.this)
                                            .load(Urls.ImgIp + listInfos.get(0).getUser_img())
                                            .asBitmap()  //这句不能少，否则下面的方法会报错
                                            .centerCrop()
                                            .into(new BitmapImageViewTarget(iv_pic) {
                                                @Override
                                                protected void setResource(Bitmap resource) {
                                                    RoundedBitmapDrawable circularBitmapDrawable =
                                                            RoundedBitmapDrawableFactory.create(getBaseContext().getResources(), resource);
                                                    circularBitmapDrawable.setCircular(true);
                                                    iv_pic.setImageDrawable(circularBitmapDrawable);
                                                }
                                            });
                                    tv_time.setText(listInfos.get(0).getCreate_time());
                                    tv_name.setText(listInfos.get(0).getUser_name());
                                    tv_red_num.setText(listInfos.get(0).getPraise_num() + "");

                                    /*****  is_praise  : 是否点过赞，1为点过，0为没点过   **************/
                                    if (listInfos.get(0).getIs_praise().equals("0")) {
                                        iv_redlike.setImageResource(R.mipmap.ckdt_button_zanhui);
                                    } else if (listInfos.get(0).getIs_praise().equals("1")) {
                                        iv_redlike.setImageResource(R.mipmap.ckdt_button_zanhong);
                                    }
                                }
                            } else {  //不显示评论view
                                lin_poplist.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        Tools.showToast(LargeImageActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(LargeImageActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }


    private GestureDetector detector;
    private static final int FLING_MIN_DISTANCE = 20;// 移动最小距离
    private static final int FLING_MIN_VELOCITY = 200;// 移动最大速度

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        //将该Activity上的触碰事件交给GesturDetector处理
        return detector.onTouchEvent(me);
    }

    /**
     * 如果触摸事件下有控件点击事件，则重写下面方法
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (detector.onTouchEvent(ev)) {
            return detector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // e1：第1个ACTION_DOWN MotionEvent
        // e2：最后一个ACTION_MOVE MotionEvent
        // velocityX：X轴上的移动速度（像素/秒）
        // velocityY：Y轴上的移动速度（像素/秒）

        // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒

        //向上
        if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            DiscussListDialog2.showDialog(this, false, largeImageInfo.getFi_id(), largeImageInfo.getSai_id(), is_advertisement, new DiscussListDialog2.OnPrizeSettingListener() {
                @Override
                public void firstClick(int n) {
                    int num = n;
                    Tools.d("tag", num + "");
                    if (num != 0 && num > Tools.StringToInt(largeImagePageInfoList.get(position).getComment_num())) {
                        //显示
                        iv_header.setVisibility(View.VISIBLE);
                        largeImagePageInfoList.get(position).setComment_state("1");//评论过
                    } else {
                        String state = largeImagePageInfoList.get(position).getComment_state();
                        if (!Tools.isEmpty(largeImageInfo.getPraise_state()) && "1".equals(largeImageInfo.getPraise_state())
                                || "1".equals(state)) {
                            iv_header.setVisibility(View.VISIBLE);
                        } else {
                            iv_header.setVisibility(View.GONE);
                        }
                    }
                    largeImagePageInfoList.get(position).setComment_num(num + "");
                    talk_num.setText(num + "");
                }

                @Override
                public void secondClick() {

                }
            });

        }
        return false;

    }

}



