package com.orange.oy.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.db.AppDBHelper;
import com.orange.oy.view.MyImageView;

public class GridImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> dataList;
    private DisplayMetrics dm;
    private AppDBHelper appDBHelper;
    private String rate, thumbnailPath;

    public GridImageAdapter(Context c, ArrayList<String> dataList) {
        mContext = c;
        appDBHelper = new AppDBHelper(c);
        this.dataList = dataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }


    public void setRateData(String rate, String thumbnailPath) {
        this.rate = rate;
        this.thumbnailPath = thumbnailPath;
    }

    public int getCount() {
        return dataList.size();
    }


    public Object getItem(int position) {
        return dataList.get(position);
    }


    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        MyImageView imageView;
        if (convertView == null) {
            imageView = new MyImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    GridView.LayoutParams.MATCH_PARENT, (Tools.getScreeInfoWidth(mContext) - dipToPx(60)) / 3));
            imageView.setAdjustViewBounds();
            imageView.setScaleType2();
        } else {
            imageView = (MyImageView) convertView;
        }
        String path;
        if (dataList != null && position < dataList.size())
            path = dataList.get(position);
        else
            path = "camera_default";
        if (path.equals("camera_default")) {
            imageView.setImageResource(R.mipmap.pzp_button_tjzp);
            imageView.setText("");
            imageView.setAlpha(1f);
        } else {
            if (path.equals(thumbnailPath)) {
                if ("0".equals(rate)) {
                    imageView.setText(rate + "%" + "\n等待上传");
                    imageView.setAlpha(0.4f);
                } else if ("100".equals(rate)) {
                    imageView.setText(rate + "%" + "\n上传成功");
                    imageView.setAlpha(1f);
                } else {
                    imageView.setText(rate + "%" + "\n正在上传");
                    imageView.setAlpha(0.4f);
                }
            }
            imageView.setImageBitmap(path);
        }
        return imageView;
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }

}
