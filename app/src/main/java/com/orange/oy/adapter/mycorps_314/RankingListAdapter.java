package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.RankingListInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/7/16.
 * 参与的活动相册排行榜 V3.18
 */

public class RankingListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RankingListInfo> list;
    private int length;
    private ImageLoader imageLoader;

    public RankingListAdapter(Context context, ArrayList<RankingListInfo> list) {
        this.context = context;
        this.list = list;
        imageLoader = new ImageLoader(context);
    }

    public void setLength(int length) {
        this.length = length;
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
            convertView = Tools.loadLayout(context, R.layout.item_rankinglist);
            viewHolder = new ViewHolder();
            viewHolder.itemranking_title = (TextView) convertView.findViewById(R.id.itemranking_title);
            viewHolder.itemranking_ranking = (TextView) convertView.findViewById(R.id.itemranking_ranking);
            viewHolder.itemranking_name = (TextView) convertView.findViewById(R.id.itemranking_name);
            viewHolder.itemranking_record = (TextView) convertView.findViewById(R.id.itemranking_record);
            viewHolder.itemranking_img = (CircularImageView) convertView.findViewById(R.id.itemranking_img);
            viewHolder.itemranking_img2 = (ImageView) convertView.findViewById(R.id.itemranking_img2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (length > 0) {
            if (position == 0) {
                viewHolder.itemranking_title.setVisibility(View.VISIBLE);
                viewHolder.itemranking_title.setText("我的排名");
            } else if (position == length) {
                viewHolder.itemranking_title.setVisibility(View.VISIBLE);
                viewHolder.itemranking_title.setText("全部排名");
            } else {
                viewHolder.itemranking_title.setVisibility(View.GONE);
            }
        } else if (length == 0) {
            if (position == 0) {
                viewHolder.itemranking_title.setVisibility(View.VISIBLE);
                viewHolder.itemranking_title.setText("全部排名");
            } else {
                viewHolder.itemranking_title.setVisibility(View.GONE);
            }
        }
        RankingListInfo rankingListInfo = list.get(position);
        viewHolder.itemranking_ranking.setText(rankingListInfo.getRanking());
        String img_url = rankingListInfo.getUser_img();
        if (!Tools.isEmpty(img_url) && img_url.startsWith("http://")) {
            imageLoader.DisplayImage(img_url, viewHolder.itemranking_img, R.mipmap.grxx_icon_mrtx);
        } else {
            imageLoader.DisplayImage(Urls.ImgIp + img_url, viewHolder.itemranking_img, R.mipmap.grxx_icon_mrtx);
        }
        viewHolder.itemranking_name.setText(rankingListInfo.getUser_name());
        viewHolder.itemranking_record.setText(rankingListInfo.getPraise_num() + "赞 " + rankingListInfo.getComment_num() + "评论");
        String file_url = rankingListInfo.getFile_url();
        if (!Tools.isEmpty(file_url) && file_url.startsWith("http://")) {
            imageLoader.DisplayImage(file_url, viewHolder.itemranking_img2);
        } else {
            imageLoader.DisplayImage(Urls.Endpoint3 + file_url, viewHolder.itemranking_img2);
        }
        return convertView;
    }

    class ViewHolder {
        TextView itemranking_title, itemranking_ranking, itemranking_name, itemranking_record;
        CircularImageView itemranking_img;
        ImageView itemranking_img2;
    }
}
