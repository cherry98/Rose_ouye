package com.orange.oy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.info.DetailsInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ItemViewHolder> {


    private ArrayList<DetailsInfo> list = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private ImageLoader imageLoader;
    List<String> datas;
    Context context;

    public DetailsAdapter(ArrayList<DetailsInfo> list, Context context) {
        this.context = context;
        imageLoader = new ImageLoader(context);
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
    }

    public ArrayList<DetailsInfo> getList() {
        return list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setList(ArrayList<DetailsInfo> list) {
        if (null == this.list) {
            return;
        }
        this.list.addAll(list);
    }

    public void clear() {
        if (null == list) {
            return;
        }
        this.list.clear();
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(View.inflate(parent.getContext(), R.layout.item_details_pic, null));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        if (list != null && list.size() != 0) {
            DetailsInfo detailsInfo = list.get(position);
            if (!TextUtils.isEmpty(detailsInfo.getFile_url())) {
                holder.item_pic.setVisibility(View.VISIBLE);
                imageLoader.DisplayImage(Urls.Endpoint3 + detailsInfo.getFile_url() + "?x-oss-process=image/resize,m_fill,h_100,w_100", holder.item_pic);
            } else {
                holder.item_pic.setVisibility(View.GONE);
            }


            holder.item_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView item_pic;


        ItemViewHolder(View itemView) {
            super(itemView);
            item_pic = (ImageView) itemView.findViewById(R.id.item_pic);
        }
    }
}
