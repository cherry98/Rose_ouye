package com.orange.oy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.StoreInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/12/20.
 * 评选页面GridView内部适配器
 */

public class SelectDetailAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<StoreInfo> storeinfos = new ArrayList<>();
    private ImageLoader imageLoader;
    private int selectPosition;
    private int index ;

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    public SelectDetailAdapter(Context context, ArrayList<StoreInfo> storeinfos) {
        this.context = context;
        this.storeinfos.addAll(storeinfos);
        imageLoader = new ImageLoader(context);
        selectPosition = 0;
    }
    public ArrayList<StoreInfo> getStoreinfos(){
        return storeinfos;
    }
    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    @Override
    public int getCount() {
        return storeinfos.size();
    }

    @Override
    public Object getItem(int position) {
        return storeinfos.get(position);
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
            convertView = Tools.loadLayout(context, R.layout.item_selectdetail);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.itemselectdetail_name);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.itemselectdetail_img);
            viewHolder.itemselectdetail_select = (ImageView) convertView.findViewById(R.id.itemselectdetail_select);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        storeinfos.get(selectPosition).isSelect = true;
        Tools.d("selectPosition:"+selectPosition);
        Tools.d("storeinfos h:" + storeinfos.hashCode());
        int size = storeinfos.size();
        for(int i=0;i<size;i++){
            if(i!=selectPosition){
                Tools.d(i+":false");
                storeinfos.get(i).isSelect=false;}
        }
        if (selectPosition == position) {
            viewHolder.itemselectdetail_select.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemselectdetail_select.setVisibility(View.GONE);
        }
        StoreInfo storeInfo = storeinfos.get(position);
        imageLoader.DisplayImage(Urls.ImgIp + storeInfo.getPhotoUrl(), viewHolder.imageView);
        viewHolder.textView.setText(storeInfo.getStoreName());
        return convertView;
    }

    class ViewHolder {
        private ImageView imageView, itemselectdetail_select;
        private TextView textView;
    }
}
