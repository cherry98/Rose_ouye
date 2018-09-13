package com.orange.oy.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.orange.oy.R;
import com.orange.oy.adapter.AlbumGridViewAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

public class AlbumActivity extends BaseActivity {

    private GridView gridView;
    private ArrayList<String> dataList = new ArrayList<String>();
    private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
    private ArrayList<String> selectedDataList = new ArrayList<String>();
    //    private String cameraDir = Environment.getExternalStorageDirectory()
//            .getAbsolutePath() + "/DCIM/";
    private ProgressBar progressBar;
    private AlbumGridViewAdapter gridImageAdapter;
    private LinearLayout selectedImageLayout;
    private Button okButton;
    private HorizontalScrollView scrollview;
    private int maxSize;
    private String projectid;
    private String storeid;
    private String packetid;
    private String taskid;
    private int onlyShow = 0;
    private SystemDBHelper systemDBHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
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
        if (onlyShow == 1) {
            appTitle.settingName("预览图片");
            findViewById(R.id.bottom_layout).setVisibility(View.GONE);
        } else {
            appTitle.settingName("选择图片");
        }
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.myGrid);
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList, selectedDataList);
        gridImageAdapter.setOnlyShow(onlyShow);
        gridView.setAdapter(gridImageAdapter);
        refreshData();
        selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
        okButton = (Button) findViewById(R.id.ok_button);
        scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);
        initSelectImage();
    }

    private ImageLoader imageLoader;

    private void initSelectImage() {
        if (selectedDataList == null)
            return;
        for (final String path : selectedDataList) {
            ImageView imageView = (ImageView) LayoutInflater.from(
                    AlbumActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
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
//        okButton.setText("完成(" + selectedDataList.size() + "/" + maxSize + ")");
        if (onlyShow != 1) {
            appTitle.settingExit("已选(" + selectedDataList.size() + ")");
        }
    }

    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
            public void onItemClick(final ToggleButton toggleButton, int position, final String path, boolean isChecked) {
                if (selectedDataList.size() >= maxSize) {
                    toggleButton.setChecked(false);
                    if (!removePath(path)) {
                        Tools.showToast(AlbumActivity.this, "只能选择" + maxSize + "张图片");
                    }
                    return;
                }
                if (isChecked) {
                    if (!hashMap.containsKey(path)) {
                        ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(
                                R.layout.choose_imageview, selectedImageLayout, false);
                        selectedImageLayout.addView(imageView);
                        imageView.postDelayed(new Runnable() {
                            public void run() {
                                int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
                                if (off > 0) {
                                    scrollview.smoothScrollTo(off, 0);
                                }
                            }
                        }, 100);
                        hashMap.put(path, imageView);
                        selectedDataList.add(path);
                        imageLoader.DisplayImage(path, imageView, R.mipmap.camera_default);
                        imageView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                toggleButton.setChecked(false);
                                removePath(path);
                            }
                        });
//                        okButton.setText("完成(" + selectedDataList.size() + "/" + maxSize + ")");
                        if (onlyShow != 1) {
                            appTitle.settingExit("已选(" + selectedDataList.size() + ")");
                        }
                        ;
                    }
                } else {
                    removePath(path);
                }

            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                // intent.putArrayListExtra("dataList", dataList);
                bundle.putStringArrayList("dataList", selectedDataList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private boolean removePath(String path) {
        if (hashMap.containsKey(path)) {
            selectedImageLayout.removeView(hashMap.get(path));
            hashMap.remove(path);
            removeOneData(selectedDataList, path);
//            okButton.setText("完成(" + selectedDataList.size() + "/" + maxSize + ")");
            if (onlyShow != 1) {
                appTitle.settingExit("已选(" + selectedDataList.size() + ")");
            }
            return true;
        } else {
            return false;
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
//                ArrayList<String> tmpList = new ArrayList<String>();
//                ArrayList<String> listDirlocal = listAlldir(new File(cameraDir));
                // ArrayList<String> listDiranjuke = new ArrayList<String>();
                // listDiranjuke.addAll(listDirlocal);
                //
                // for (int i = 0; i < listDiranjuke.size(); i++) {
                // listAllfile(new File(listDiranjuke.get(i)), tmpList);
                // }
                // return tmpList;
                ArrayList<String> list;
                if (TextUtils.isEmpty(taskid)) {
                    list = systemDBHelper.getPictureThumbnail(AppInfo.getName(AlbumActivity.this), projectid, storeid);
                } else {
                    list = systemDBHelper.getPictureThumbnail(AppInfo.getName(AlbumActivity.this), projectid,
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
                if (AlbumActivity.this == null || AlbumActivity.this.isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                dataList.clear();
                dataList.addAll(tmpList);
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
