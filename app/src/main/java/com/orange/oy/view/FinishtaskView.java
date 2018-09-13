package com.orange.oy.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.orange.oy.R;
import com.orange.oy.activity.TaskitemPhotographyActivity;
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.allinterface.FinishTaskProgressRefresh;
import com.orange.oy.base.BaseView;
import com.orange.oy.base.Tools;
import com.orange.oy.info.DetailsInfo;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.TaskFinishInfo;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

public class FinishtaskView extends LinearLayout implements View.OnClickListener, FinishTaskProgressRefresh, BaseView {
    public interface OnTitleClickListener {
        void titleClick(Object tag);
    }

    private OnTitleClickListener onTitleClickListener;

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }

    public FinishtaskView(Context context, TaskFinishInfo taskFinishInfo, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_task);
        init(isAgain);
        this.taskFinishInfo = taskFinishInfo;
    }

    public FinishtaskView(Context context, boolean isAgain) {
        super(context);
        Tools.loadLayout(this, R.layout.view_finishdt_task);
        init(isAgain);
        findViewById(R.id.viewfdt_photo_reset).setVisibility(GONE);
    }

    private TextView name, value;
    private ImageView right;
    private GridView gridView;
    private int itemWidth;
    private ArrayList<String> list = new ArrayList<>();
    private ImageLoader imageLoader;
    private MyAdapter adapter;
    private LinearLayout viewfdt_photo_layout;
    private TaskFinishInfo taskFinishInfo;
    private String task_type = "1";
    private View viewfdt_photo_note_layout;
    private TextView viewfdt_photo_progressvalue;
    private ProgressBar viewfdt_photo_progress;

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    private void init(boolean isAgain) {
        viewfdt_photo_progress = (ProgressBar) findViewById(R.id.viewfdt_photo_progress);
        viewfdt_photo_progressvalue = (TextView) findViewById(R.id.viewfdt_photo_progressvalue);
        name = (TextView) findViewById(R.id.viewfdt_photo_name);
        value = (TextView) findViewById(R.id.viewfdt_photo_value);
        right = (ImageView) findViewById(R.id.viewfdt_photo_right);
        viewfdt_photo_layout = (LinearLayout) findViewById(R.id.viewfdt_photo_layout);
        viewfdt_photo_note_layout = findViewById(R.id.viewfdt_photo_note_layout);
        gridView = (GridView) findViewById(R.id.viewfdt_photo_gridview);
        int mar = (int) getResources().getDimension(R.dimen.finishet_task_margin);
        int mar2 = (int) getResources().getDimension(R.dimen.taskphoto_gridview_mar2);
        itemWidth = (Tools.getScreeInfoWidth(getContext()) - 2 * mar - 4 * mar2) / 3;
        imageLoader = new ImageLoader(getContext());
        if (isAgain)
            findViewById(R.id.viewfdt_photo_reset).setOnClickListener(this);
        else
            findViewById(R.id.viewfdt_photo_reset).setVisibility(GONE);
        findViewById(R.id.viewfdt_task_layout).setOnClickListener(this);
//        onClick(right);
    }

    private boolean isProgress;

    public void setIsProgress(boolean isshow) {
        isProgress = isshow;
        if (isProgress) {
            findViewById(R.id.viewfdt_task_layout).setOnClickListener(null);
            viewfdt_photo_progress.setVisibility(VISIBLE);
            viewfdt_photo_progressvalue.setVisibility(VISIBLE);
        }
    }

    public void insertQuestion(View view) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        viewfdt_photo_layout.addView(view, lp);
    }

    private void insertView(ArrayList<String[]> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String[] temp = list.get(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_checkreqpgnext_add, null);
            imageLoader.DisplayImage(temp[0], (ImageView) view.findViewById(R.id
                    .view_checkreqpgnext_img));
            EditText editText = (EditText) view.findViewById(R.id.view_checkreqpgnext_edit);
            editText.setEnabled(false);
            editText.setHint("");
            if (TextUtils.isEmpty(temp[1]) || temp[1].equals("null")) {
                editText.setText("");
            } else {
                editText.setText(temp[1] + "");
            }
//            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//            lp.bottomMargin = 10;
//            viewfdt_photo_layout.addView(view, lp);
            viewfdt_photo_layout.addView(view);
        }
    }

    public void settingName(String name) {
        this.name.setText(name);
    }

    public void settingValue(String name, ArrayList<String[]> list) {//多备注
        this.name.setText(name);
        insertView(list);
    }

    public void settingValue(String name, ArrayList<String> list, String value) {//单备注
        largeImagePageInfos = null;
        this.name.setText(name);
        if (TextUtils.isEmpty(value) || value.equals("null")) {
            value = "";
        }
        this.value.setText(value);
        int size = list.size();
        int line = (int) Math.ceil(size / 3d);
        if (line > 0) {
            LinearLayout.LayoutParams lp = (LayoutParams) gridView.getLayoutParams();
            lp.height = itemWidth * line + (line - 1) * (int) getResources().getDimension(R.dimen.finishet_task_margin);
            gridView.setLayoutParams(lp);
        }
        this.list = new ArrayList<>();
        for (String str : list) {
            if (str.startsWith("http")) {
                str += "?x-oss-process=image/resize,l_350";
                this.list.add(str);
            }
        }
        if (adapter == null) {
            adapter = new MyAdapter();
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(gridviewOnItemClickListener);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void hideView() {
        viewfdt_photo_note_layout.setVisibility(GONE);
        gridView.setVisibility(View.GONE);
        viewfdt_photo_layout.setVisibility(View.GONE);
        right.setImageResource(R.mipmap.text_spread);
    }

    public void showView() {
        viewfdt_photo_note_layout.setVisibility(VISIBLE);
        gridView.setVisibility(View.VISIBLE);
        viewfdt_photo_layout.setVisibility(View.VISIBLE);
        right.setImageResource(R.mipmap.text_shrinkup);
    }

    public GridView getGridView() {
        return gridView;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewfdt_task_layout: {
                if (onTitleClickListener != null) {
                    onTitleClickListener.titleClick(getTag());
                }
                if (viewfdt_photo_note_layout.getVisibility() == View.VISIBLE) {
                    hideView();
                } else {
                    showView();
                }
            }
            break;
            case R.id.viewfdt_photo_reset: {
                Intent intent = new Intent(getContext(), TaskitemPhotographyActivity.class);
                intent.putExtra("task_pack_id", taskFinishInfo.getPid());
                intent.putExtra("task_type", task_type);
                intent.putExtra("task_id", taskFinishInfo.getTaskid());
                intent.putExtra("task_name", taskFinishInfo.getName());
                intent.putExtra("store_id", taskFinishInfo.getStoreid());
                intent.putExtra("photo_compression", taskFinishInfo.getCompression());
                intent.putExtra("category1", taskFinishInfo.getCategory1());
                intent.putExtra("category2", taskFinishInfo.getCategory2());
                intent.putExtra("category3", taskFinishInfo.getCategory3());
                intent.putExtra("project_id", taskFinishInfo.getProjectid());
                intent.putExtra("project_name", taskFinishInfo.getProjectname());
                intent.putExtra("task_pack_name", taskFinishInfo.getPackage_name());
                intent.putExtra("store_num", taskFinishInfo.getStorenum());
                intent.putExtra("store_name", taskFinishInfo.getStorename());
                intent.putExtra("is_watermark", taskFinishInfo.getIs_watermark());
                intent.putExtra("outlet_batch", taskFinishInfo.getOutlet_batch());
                intent.putExtra("p_batch", taskFinishInfo.getP_batch());
                getContext().startActivity(intent);
            }
            break;
        }
    }

    @Override
    public void setProgress(int progress) {
        if (isProgress) {
            viewfdt_photo_progress.setProgress(progress);
            if (progress < 100) {
                viewfdt_photo_progressvalue.setText(progress + "%");
            } else {
                viewfdt_photo_progress.setVisibility(GONE);
                viewfdt_photo_progressvalue.setVisibility(GONE);
            }
        }
    }

    @Override
    public Object getInfo() {
        return taskFinishInfo;
    }

    @Override
    public void onResume(Object object) {

    }

    @Override
    public void onPause(Object object) {

    }

    @Override
    public void onStop(Object object) {

    }

    @Override
    public void onDestory(Object object) {

    }

    @Override
    public Object getBaseData() {
        return taskFinishInfo;
    }

    class MyAdapter extends BaseAdapter {
        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(getContext());
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(itemWidth, itemWidth);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(lp);
            } else {
                imageView = (ImageView) convertView;
            }
            imageLoader.DisplayImage(list.get(position), imageView);
            return imageView;
        }
    }

    private ArrayList<LargeImagePageInfo> largeImagePageInfos;
    private AdapterView.OnItemClickListener gridviewOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (list != null && !list.isEmpty()) {
                if (largeImagePageInfos == null) {
                    largeImagePageInfos = new ArrayList<>();
                    for (String string : list) {
                        LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
                        largeImagePageInfo.setFile_url(string);
                        largeImagePageInfos.add(largeImagePageInfo);
                    }
                }
                Intent intent = new Intent(getContext(), LargeImagePageActivity.class);
                intent.putExtra("isList", true);
                intent.putExtra("list", largeImagePageInfos);
                intent.putExtra("position", position);
                intent.putExtra("state", 1);
                getContext().startActivity(intent);
            }
        }
    };
}
