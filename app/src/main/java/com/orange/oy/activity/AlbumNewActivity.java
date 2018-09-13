package com.orange.oy.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orange.oy.R;
import com.orange.oy.adapter.AlbumNewGridViewAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.iphotoviewer.PhotoViewActivity;
import com.orange.oy.view.photoview.PhotoMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/****
 *
 *   临时相册页面  (V3.1.1)
 */
public class AlbumNewActivity extends BaseActivity {

    private GridView gridView;
    private ArrayList<String> dataList = new ArrayList<String>();
    private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
    private ArrayList<String> selectedDataList = new ArrayList<String>();
    //    private String cameraDir = Environment.getExternalStorageDirectory()
//            .getAbsolutePath() + "/DCIM/";
    private ProgressBar progressBar;
    private AlbumNewGridViewAdapter gridImageAdapter;
    private LinearLayout selectedImageLayout;
    private Button okButton;
    private HorizontalScrollView scrollview;
    private int maxSize;
    private String projectid;
    private String storeid, storecode;
    private String packetid;
    private String taskid;
    private int onlyShow = 0;
    private SystemDBHelper systemDBHelper;
    private List<PhotoMode> datas = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_new);
        imageLoader = new ImageLoader(this);
        systemDBHelper = new SystemDBHelper(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        maxSize = bundle.getInt("maxsize");
        projectid = bundle.getString("projectid");
        storeid = bundle.getString("storeid");
        packetid = bundle.getString("packetid");
        taskid = bundle.getString("taskid");
        onlyShow = bundle.getInt("onlyShow", 0);
        storecode = bundle.getString("storecode");
        selectedDataList = new ArrayList<>();
        init();
        initListener();
    }

    private AppTitle appTitle;

    private void init() {
        appTitle = (AppTitle) findViewById(R.id.album_title);
        appTitle.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        appTitle.settingName("临时相册");
        findViewById(R.id.bottom_layout).setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.myGrid);

        dataList.add(0, "camera_default");
        gridImageAdapter = new AlbumNewGridViewAdapter(this, dataList, selectedDataList);
        gridImageAdapter.setOnlyShow(onlyShow);
        gridView.setAdapter(gridImageAdapter);
        refreshData();
        selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
        okButton = (Button) findViewById(R.id.ok_button);
        scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);
        //  initSelectImage();
    }

    private ImageLoader imageLoader;

    private void initSelectImage() {
        if (selectedDataList == null)
            return;
        for (final String path : selectedDataList) {
            ImageView imageView = (ImageView) LayoutInflater.from(
                    AlbumNewActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
            selectedImageLayout.addView(imageView);
            hashMap.put(path, imageView);
            imageLoader.DisplayImage(path, imageView, R.mipmap.camera_default);
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    removePath(path);
                    gridImageAdapter.notifyDataSetChanged();
                }
            });
        }
        if (onlyShow != 1) {
            appTitle.settingExit("已选(" + selectedDataList.size() + ")");
        }
    }

    private void initListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dataList.get(position).equals("camera_default")) {
                    Intent intent = new Intent(AlbumNewActivity.this, Camerase.class);
                    intent.putExtra("projectid", projectid);
                    intent.putExtra("storeid", storeid);
                    intent.putExtra("storecode", storecode);
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(AlbumNewActivity.this, PhotoViewActivity.class);
                    intent.putExtra("dataList", dataList);
                    intent.putExtra("currentPosition", position - 1);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean removePath(String path) {
        if (hashMap.containsKey(path)) {
            selectedImageLayout.removeView(hashMap.get(path));
            hashMap.remove(path);
            removeOneData(selectedDataList, path);
            if (onlyShow != 1) {
                appTitle.settingExit("已选(" + selectedDataList.size() + ")");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            refreshData();
        }
    }

    private void removeOneData(ArrayList<String> arrayList, String s) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals(s)) {
                arrayList.remove(i);
                return;
            }
        }
    }

    private void refreshData() {

        new AsyncTask<Void, Void, ArrayList<String>>() {

            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            protected ArrayList<String> doInBackground(Void... params) {
                ArrayList<String> list;
                if (TextUtils.isEmpty(taskid)) {
                    list = systemDBHelper.getPictureThumbnail(AppInfo.getName(AlbumNewActivity.this), projectid, storeid);
                } else {
                    list = systemDBHelper.getPictureThumbnail(AppInfo.getName(AlbumNewActivity.this), projectid,
                            storeid, packetid, taskid);
                }
                int size = list.size();
                String temp;
                for (int i = 0; i < size; i++) {
                    temp = list.get(i);
                    File file = new File(systemDBHelper.searchForOriginalpath(temp));
                    if (!file.exists() || !file.isFile()) {
                        systemDBHelper.deletePicture(temp);
                        file = new File(temp);
                        if (file.exists()) {
                            file.delete();
                        }
                        list.remove(i);
                        i--;
                        size--;
                    }
                }
                return list;
            }

            protected void onPostExecute(ArrayList<String> tmpList) {
                if (AlbumNewActivity.this == null || AlbumNewActivity.this.isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                dataList.clear();
                dataList.add(0, "camera_default");
                dataList.addAll(tmpList);
                appTitle.settingExit(dataList.size() - 1 + "张");
                gridImageAdapter.notifyDataSetChanged();
                return;

            }

            ;

        }.execute();

    }

    private ArrayList<String> listAlldir(File nowDir) {
        ArrayList<String> listDir = new ArrayList<String>();
        nowDir = new File(nowDir.getPath());
        if (!nowDir.isDirectory()) {
            return listDir;
        }
        // File[] files = nowDir.listFiles();
        //
        // for (int i = 0; i < files.length; i++) {
        // if (files[i].getName().substring(0, 1).equals(".")) {
        // continue;
        // }
        // File file = new File(files[i].getPath());
        // if (file.isDirectory()) {
        // listDir.add(files[i].getPath());
        // }
        // }
        boolean b = getFile(listDir);
        if (!b) {
            getFile(listDir, nowDir);
        }
        return listDir;
    }

    private void getFile(ArrayList<String> listDir, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals("OuYe")) {
                    continue;
                }
                getFile(listDir, files[i]);
            }
        } else {
            if (file.getPath().endsWith(".jpg") || file.getPath().endsWith(".png")) {
                BitmapFactory.Options opt1 = new BitmapFactory.Options();
                opt1.inSampleSize = 1;
                opt1.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getPath(), opt1);
                if (!TextUtils.isEmpty(opt1.outMimeType))
                    listDir.add(file.getPath());
            }
        }
    }

    private boolean getFile(ArrayList<String> listDir) {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                if (!TextUtils.isEmpty(data) && !data.contains("/OuYe/")
                        && (data.endsWith(".jpg") || data.endsWith(".png") || data.endsWith(".gif"))) {
                    File file = new File(data);
                    if (file.exists() && file.isFile()) {
                        BitmapFactory.Options opt1 = new BitmapFactory.Options();
                        opt1.inSampleSize = 1;
                        opt1.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(data, opt1);
                        if (!TextUtils.isEmpty(opt1.outMimeType)) {
                            Tools.d(data);
                            listDir.add(data);
                        }
                    }
                }
            }
            cursor.close();
        } else {
            return false;
        }
        return true;
    }

    public void onBackPressed() {
        finish();
        // super.onBackPressed();
    }

    public void finish() {
        super.finish();
        // ImageManager2.from(AlbumActivity.this).recycle(dataList);
    }

    protected void onDestroy() {

        super.onDestroy();
    }

}
