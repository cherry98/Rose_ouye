package com.orange.oy.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.orange.oy.R;
import com.orange.oy.adapter.AlbumGridViewAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 调用系统相册
 */
public class SystemAlbumActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private AppTitle appTitle;

    private void initTitle() {
        appTitle = (AppTitle) findViewById(R.id.systemalbum_title);
        appTitle.settingName("选择图片");
        appTitle.showBack(this);
        appTitle.settingExit("0/" + maxSize);
    }

    private ProgressBar progressBar;
    private AlbumGridViewAdapter gridViewAdapter;
    private GridView gridView;
    private int maxSize;
    private ArrayList<String> dataList = new ArrayList<String>();
    private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
    private ArrayList<String> selectedDataList = new ArrayList<String>();
    private Button systemok_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_album);
        maxSize = getIntent().getIntExtra("num", 0);
        initTitle();
        gridView = (GridView) findViewById(R.id.system_myGrid);
        progressBar = (ProgressBar) findViewById(R.id.system_progressbar);
        systemok_button = (Button) findViewById(R.id.systemok_button);
        refreshData();
        progressBar.setVisibility(View.GONE);
        gridViewAdapter = new AlbumGridViewAdapter(this, dataList, selectedDataList);
        gridView.setAdapter(gridViewAdapter);
        onItemClickListener();
    }

    private void onItemClickListener() {
        gridViewAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ToggleButton view, int position, String path, boolean isChecked) {
                if (selectedDataList.size() >= maxSize) {
                    view.setChecked(false);
                    Tools.showToast(SystemAlbumActivity.this, "您只能选择" + maxSize + "张图片");
                    removePath(path);
                    return;
                }
                if (isChecked) {
                    selectedDataList.add(path);
                    appTitle.settingExit(selectedDataList.size() + "/" + maxSize);
                } else {
                    removePath(path);
                }
            }
        });
        systemok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("dataList", selectedDataList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                baseFinish();
            }
        });
    }

    private void removePath(String path) {
        removeOneData(selectedDataList, path);
        appTitle.settingExit(selectedDataList.size() + "/" + maxSize);
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
                ArrayList<String> list = new ArrayList<String>();
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = SystemAlbumActivity.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    list.add(path);
                }
                return list;
            }

            protected void onPostExecute(ArrayList<String> tmpList) {
                if (SystemAlbumActivity.this == null || SystemAlbumActivity.this.isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                dataList.clear();
                dataList.addAll(tmpList);
                gridViewAdapter.notifyDataSetChanged();
                return;
            }
        }.execute();
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
