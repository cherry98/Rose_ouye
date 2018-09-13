package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.soleagencysdk.util.LogUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.adapter.LeftAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;
import com.orange.oy.info.shakephoto.ShakeThemeInfo;
import com.orange.oy.view.AppTitle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;


import static com.orange.oy.info.shakephoto.ShakePhotoInfo2.*;


/***
 * V3.18 甩吧相册
 */
public class LeftActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, LeftAdapter.OnItemCheckListener {

    private AppTitle appTitle;
    private AppDBHelper appDBHelper;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.left_title);
        appTitle.settingName("甩吧相册");
        appTitle.showBack(this);
        if (shakeThemeInfo == null) {
            appTitle.settingExit("上传", onExitClickForAppTitle1);
        }
    }

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle1 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            if (adapter != null) {
                appTitle.settingExit("完成", onExitClickForAppTitle2);
                findViewById(R.id.tv_select).setVisibility(View.VISIBLE);
                lin_selectPhoto.setVisibility(View.GONE);
                findViewById(R.id.tv_select).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //从相册选取
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 5);
                    }
                });
                adapter.setShow(true);
                adapter.notifyDataSetChanged();
            }
        }
    };
    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle2 = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            appTitle.settingExit("上传", onExitClickForAppTitle1);
            findViewById(R.id.tv_select).setVisibility(View.GONE);
            lin_selectPhoto.setVisibility(View.VISIBLE);
            if (adapter != null) {
                adapter.setShow(false);
                adapter.notifyDataSetChanged();
                if (!photoList.isEmpty()) {
                    Tools.d("zpf", photoList.toString());
                    Intent intent = new Intent(LeftActivity.this, UploadPicturesActivity.class);
                    intent.putExtra("photoList", photoList);
                    startActivity(intent);
                    finish();
                }
            }
            lin_selectPhoto.setVisibility(View.VISIBLE);
        }
    };

    protected void onResume() {
        super.onResume();
//        if (adapter != null) {
//            adapter.setShow(false);
//            adapter.notifyDataSetChanged();
//        }
        if (loadDataAsyncTask == null) {
            new LoadDataAsyncTask().executeOnExecutor(Executors.newCachedThreadPool());
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        loadDataAsyncTask = null;
    }

    private PullToRefreshListView left_listview;
    private LeftAdapter adapter;
    private ArrayList<ShakePhotoInfo2> list = new ArrayList<>();
    private String dai_id;
    private LinearLayout lin_selectPhoto;
    private ShakeThemeInfo shakeThemeInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left);
        dai_id = getIntent().getStringExtra("dai_id");
        shakeThemeInfo = (ShakeThemeInfo) getIntent().getSerializableExtra("shakeThemeInfo");
        appDBHelper = new AppDBHelper(this);
        left_listview = (PullToRefreshListView) findViewById(R.id.left_listview);
        left_listview.setMode(PullToRefreshBase.Mode.DISABLED);
        lin_selectPhoto = (LinearLayout) findViewById(R.id.lin_selectPhoto);
        initTitle();
        lin_selectPhoto.setVisibility(View.VISIBLE);
        lin_selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //自由拍的甩吧
                Intent intent = new Intent(LeftActivity.this, ShakephotoActivity.class);
                startActivity(intent);
            }
        });
    }

    private static LoadDataAsyncTask loadDataAsyncTask;

    private class LoadDataAsyncTask extends AsyncTask {
        protected void onPreExecute() {
            loadDataAsyncTask = this;
            CustomProgressDialog.showProgressDialog(LeftActivity.this, "");
        }

        protected Object doInBackground(Object[] params) {
            appDBHelper.clearNullData();
            list = appDBHelper.getShakePhoto();
            for (int i = 0; i < list.size(); i++) {
                ShakePhotoInfo2 shakePhotoInfo2 = list.get(i);
                ArrayList<PhotoListBean> list1 = new ArrayList<>();
                String[] str = list.get(i).getFile_url().split(",");
                String[] str2 = list.get(i).getFile_url2().split(",");
                String province = list.get(i).getProvince();
                String city = list.get(i).getCity();
                String county = list.get(i).getCounty();
                String address = list.get(i).getAddress();
                String area = list.get(i).getArea();
                String latitude = list.get(i).getLatitude();
                String longitude = list.get(i).getLongitude();
                String create_time = list.get(i).getTime();
                // for (String aStr : str) {
                for (int j = 0; j < str.length; j++) {
                    PhotoListBean photoListBean = new PhotoListBean();
                    photoListBean.setFile_url(str[j]);
                    photoListBean.setFile_url2(str2[j]);
                    photoListBean.setCheck(false);
                    photoListBean.setShow(false);
                    photoListBean.setProvince(province);
                    photoListBean.setCity(city);
                    photoListBean.setDai_id(dai_id);
                    photoListBean.setCounty(county);
                    photoListBean.setLongitude(longitude);
                    photoListBean.setLatitude(latitude);
                    photoListBean.setArea(area);
                    photoListBean.setAddress(address);
                    photoListBean.setCreate_time(create_time);
                    list1.add(photoListBean);
                }
                shakePhotoInfo2.setList(list1);
                list.set(i, shakePhotoInfo2);
            }
            return null;
        }

        protected void onPostExecute(Object object) {
            loadDataAsyncTask = null;
            CustomProgressDialog.Dissmiss();
            if (list != null && left_listview != null) {
                adapter = new LeftAdapter(LeftActivity.this, list, shakeThemeInfo);
                adapter.setOnItemCheckListener(LeftActivity.this);
                left_listview.setAdapter(adapter);
            }
        }
    }

    public void onBack() {
        baseFinish();
    }


    /**
     * 选择上传 的 图片集合
     *
     * @param photoListBean
     * "province":"省份",
     * "city":"城市",
     * "county":"区域",
     * "address":"详细地址",
     * "longitude":"经度",
     * "latitude":"纬度"
     */
    private ArrayList<PhotoListBean> photoList = new ArrayList<>();

    @Override
    public void onItemCheck(PhotoListBean photoListBean) {
        if (photoListBean.isCheck()) {
            if (!photoList.contains(photoListBean)) {
                photoList.add(photoListBean);
            }
        } else {
            if (photoList.contains(photoListBean)) {
                photoList.remove(photoListBean);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 5: {
                    try {
                        Uri uri = data.getData();
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                            cursor.close();
                            //从本地相册中获取经纬度等，如果获取不到，不加入bean

                            ExifInterface exif = new ExifInterface(path);
                            String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                            String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);

                            if (!Tools.isEmpty(longitude) && !Tools.isEmpty(latitude)) {
                                PhotoListBean photoListBean = new PhotoListBean();
                                photoListBean.setFile_url(path);
                                photoListBean.setLatitude(Tools.score2dimensionality(latitude) + "");
                                photoListBean.setLongitude(Tools.score2dimensionality(longitude) + "");
                                photoList.add(photoListBean);
                                adapter.notifyDataSetChanged();
                            } else {
                                Tools.showToast(this, "此照片中不含经纬度信息，不能上传哦~");
                            }


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}
