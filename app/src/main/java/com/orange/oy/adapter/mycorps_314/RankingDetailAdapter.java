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

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2018/7/17.
 * 排名照片详情 V3.18
 */

public class RankingDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RankingListInfo> list;

    public RankingDetailAdapter(Context context, ArrayList<RankingListInfo> list) {
        this.context = context;
        this.list = list;
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
            convertView = Tools.loadLayout(context, R.layout.item_rankingdetail);
            viewHolder.itemrankingdetail_text1 = (TextView) convertView.findViewById(R.id.itemrankingdetail_text1);
            viewHolder.itemrankingdetail_text2 = (TextView) convertView.findViewById(R.id.itemrankingdetail_text2);
            viewHolder.itemrankingdetail_text3 = (TextView) convertView.findViewById(R.id.itemrankingdetail_text3);
            viewHolder.itemrankingdetail_img = (ImageView) convertView.findViewById(R.id.itemrankingdetail_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RankingListInfo rankingListInfo = list.get(position);
        viewHolder.itemrankingdetail_text1.setText(rankingListInfo.getRanking());
        viewHolder.itemrankingdetail_text2.setText(rankingListInfo.getPraise_num());
        viewHolder.itemrankingdetail_text3.setText(rankingListInfo.getComment_num());
        ImageLoader imageLoader = new ImageLoader(context);
        String url = rankingListInfo.getFile_url();
        if (Tools.isEmpty(url) && url.startsWith("http://")) {
            imageLoader.DisplayImage(url, viewHolder.itemrankingdetail_img);
        } else {
            imageLoader.DisplayImage(Urls.Endpoint3 + url, viewHolder.itemrankingdetail_img);
        }
        return convertView;
    }

    class ViewHolder {
        private TextView itemrankingdetail_text1, itemrankingdetail_text2, itemrankingdetail_text3;
        private ImageView itemrankingdetail_img;
    }
}
