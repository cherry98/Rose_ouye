package com.orange.oy.activity.calltask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.view.AppTitle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 查找电话录音
 */
public class CallfindrecodeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle, AdapterView.OnItemClickListener {
    private ListView listView;
    private ArrayList<HashMap<String, String>> showList = new ArrayList<>();

    public void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.callfindrecode_title);
        appTitle.settingName("音频");
        appTitle.showBack(this);
        appTitle.settingExit("我没找到");
        appTitle.settingExitColor(Color.parseColor("#FFF65D57"));
        appTitle.showExit(new AppTitle.OnExitClickForAppTitle() {
            @Override
            public void onExit() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                String path = getRealFilePath(this, uri);
                if (TextUtils.isEmpty(path)) {
                    Tools.showToast(this, "文件无效，请在文件管理器中选择");
                } else if (!new File(path).exists()) {
                    Tools.showToast(this, "文件已经没有了...");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("path", path);
                    setResult(RESULT_OK, intent);
                    baseFinish();
                }
            }
        }
    }

    public static String getRealFilePath(Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.AudioColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_findrecode);
        initTitle();
        listView = (ListView) findViewById(R.id.call_findrecode_listview);
        listView.setOnItemClickListener(this);
        new SystemAudioSearch().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = showList.get(position);
        String path = map.get(MediaStore.Audio.Media.DATA);
        if (new File(path).exists()) {
            Intent intent = new Intent();
            intent.putExtra("path", path);
            setResult(RESULT_OK, intent);
            baseFinish();
        } else {
            Tools.showToast(this, "文件已经没有了...");
        }
    }

    /**
     * 获取音频列表
     */
    void getAudio() {
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE};
        String orderBy = MediaStore.Audio.Media.DATE_MODIFIED;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        getContentProvider(uri, projection, orderBy);
    }

    /**
     * 获取ContentProvider
     *
     * @param projection
     * @param orderBy
     */
    public void getContentProvider(Uri uri, String[] projection, String orderBy) {
        Cursor cursor = getContentResolver().query(uri, projection, null,
                null, orderBy + " desc");
        if (null == cursor) {
            return;
        }
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < projection.length; i++) {
                map.put(projection[i], cursor.getString(i));
                System.out.println(projection[i] + ":" + cursor.getString(i));
            }
            String path = map.get(MediaStore.Audio.Media.DATA);
            if (new File(path).exists()) {
                showList.add(map);
            }
        }
        cursor.close();
    }

    /**
     * 所搜系统录音文件
     */
    private class SystemAudioSearch extends AsyncTask {

        protected Object doInBackground(Object[] params) {
            getAudio();
            return null;
        }

        protected void onPostExecute(Object o) {
            if (listView == null) return;
            MyAdapter adapter = new MyAdapter();
            listView.setAdapter(adapter);
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showList.size();
        }

        @Override
        public Object getItem(int position) {
            return showList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item item = null;
            if (convertView == null) {
                convertView = Tools.loadLayout(CallfindrecodeActivity.this, R.layout.item_call_findrecode);
                item = new Item();
                item.name = (TextView) convertView.findViewById(R.id.call_findrecode_name);
                item.time = (TextView) convertView.findViewById(R.id.call_findrecode_time);
                item.size = (TextView) convertView.findViewById(R.id.call_findrecode_size);
                convertView.setTag(item);
            } else {
                item = (Item) convertView.getTag();
            }
            HashMap<String, String> map = showList.get(position);
            item.name.setText(map.get(MediaStore.Audio.Media.DISPLAY_NAME));
            item.time.setText(map.get(MediaStore.Audio.Media.DATE_MODIFIED));
            item.size.setText(map.get(MediaStore.Audio.Media.SIZE));
            return convertView;
        }

        private class Item {
            TextView name, time, size;
        }
    }

    @Override
    public void onBack() {
        baseFinish();
    }
}
