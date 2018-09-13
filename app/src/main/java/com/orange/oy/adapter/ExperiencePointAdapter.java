package com.orange.oy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.ExperienceCommentInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.FlowLayoutView;
import com.orange.oy.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/11/22.
 */

public class ExperiencePointAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ExperienceCommentInfo> list;
    private ImageLoader imageLoader;

    public ExperiencePointAdapter(Context context, ArrayList<ExperienceCommentInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_experiencepoint);
            viewHolder.itemepoint_img = (CircularImageView) convertView.findViewById(R.id.itemepoint_img);
            viewHolder.itemepoint_ratingbar = (RatingBar) convertView.findViewById(R.id.itemepoint_ratingbar);
            viewHolder.itemepoint_content = (TextView) convertView.findViewById(R.id.itemepoint_content);
            viewHolder.itemepoint_score = (TextView) convertView.findViewById(R.id.itemepoint_score);
            viewHolder.itemepoint_time = (TextView) convertView.findViewById(R.id.itemepoint_time);
            viewHolder.itemepoint_gridview = (MyGridView) convertView.findViewById(R.id.itemepoint_gridview);
            viewHolder.itemepoint_flowlayout = (FlowLayoutView) convertView.findViewById(R.id.itemepoint_flowlayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ExperienceCommentInfo experienceCommentInfo = list.get(position);
        imageLoader.DisplayImage(Urls.ImgIp + experienceCommentInfo.getImgurl(), viewHolder.itemepoint_img);
        viewHolder.itemepoint_score.setText(experienceCommentInfo.getScore());
        viewHolder.itemepoint_content.setText(experienceCommentInfo.getComment());
        ArrayList<String> multiselect = new ArrayList<>();
        ArrayList<String> photourl = new ArrayList<>();
        try {
            JSONArray jsonArray = experienceCommentInfo.getMultiselect();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    multiselect.add(jsonArray.getString(i));
                }
            }
            JSONArray jsonArray1 = experienceCommentInfo.getPhotourl();
            if (jsonArray1 != null) {
                for (int i = 0; i < jsonArray1.length(); i++) {
                    if ("0".equals(experienceCommentInfo.getType())) {
                        photourl.add(Urls.ImgIp + jsonArray1.getString(i));
                    } else {
                        photourl.add(jsonArray1.getString(i));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < multiselect.size(); i++) {
            final TextView textView = new TextView(context);
            textView.setText(multiselect.get(i));
            textView.setPadding(10, 5, 10, 10);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(12);
            textView.setTextColor(context.getResources().getColor(R.color.experience_notselect));
            textView.setBackgroundResource(R.drawable.shape_item);
            viewHolder.itemepoint_flowlayout.addView(textView);
        }
        viewHolder.itemepoint_time.setText(experienceCommentInfo.getDate());
        viewHolder.itemepoint_ratingbar.setRating(Float.parseFloat(experienceCommentInfo.getScore()));
        TaskitemReqPgAdapter taskitemReqPgAdapter = new TaskitemReqPgAdapter(context, photourl, true);
        viewHolder.itemepoint_gridview.setAdapter(taskitemReqPgAdapter);
        int scroeHeight = 0;
        try {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.rating_notselect);
            scroeHeight = bmp.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scroeHeight != 0) {
            RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) viewHolder.itemepoint_ratingbar.getLayoutParams();
            llp.width = -2;// 包裹内容
            llp.height = scroeHeight;
            viewHolder.itemepoint_ratingbar.setLayoutParams(llp);
        }
        return convertView;
    }

    class ViewHolder {
        private CircularImageView itemepoint_img;
        private RatingBar itemepoint_ratingbar;
        private TextView itemepoint_score, itemepoint_content, itemepoint_time;
        private MyGridView itemepoint_gridview;
        private FlowLayoutView itemepoint_flowlayout;
    }
}
