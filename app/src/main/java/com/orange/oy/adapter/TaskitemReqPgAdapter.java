package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

public class TaskitemReqPgAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> urlList;
    private ImageLoader imageLoader;
    private boolean isCheck;

    public TaskitemReqPgAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.urlList = list;
        imageLoader = new ImageLoader(context);
    }

    public TaskitemReqPgAdapter(Context context, ArrayList<String> list, boolean isCheck) {
        this.context = context;
        this.urlList = list;
        imageLoader = new ImageLoader(context);
        this.isCheck = isCheck;
    }

    private boolean isOffline = false;

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    public int getCount() {
        return urlList.size();
    }

    public Object getItem(int position) {
        return urlList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView item_ckrg_check = null;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.item_gridview_checkreqpg);
            imageView = (ImageView) convertView.findViewById(R.id.item_ckrg_img);
            item_ckrg_check = (TextView) convertView.findViewById(R.id.item_ckrg_check);
//            int temp = (int) ((Tools.getScreeInfoWidth(context) - context.getResources().getDimension(R.dimen
//                    .taskphoto_gridview_mar) * 2 - context.getResources().getDimension(R.dimen.taskphoto_gridview_item_mar) *
//                    2) / 3);
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
//            lp.width = temp;
//            lp.height = temp;
//            imageView.setLayoutParams(lp);
            convertView.setTag(imageView);
        } else {
            imageView = (ImageView) convertView.getTag();
            item_ckrg_check = (TextView) convertView.findViewById(R.id.item_ckrg_check);
        }
        if (isCheck) {
            item_ckrg_check.setVisibility(View.GONE);
        } else {
            item_ckrg_check.setVisibility(View.VISIBLE);
        }
        if (isOffline) {
            try {
                imageView.setImageBitmap(Tools.getBitmap(urlList.get(position)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imageLoader.DisplayImage(urlList.get(position), imageView);
        }
        return convertView;
    }
}
