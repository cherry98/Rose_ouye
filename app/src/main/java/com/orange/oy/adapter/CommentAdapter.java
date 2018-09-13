package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.NewCommentInfo;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;
import com.orange.oy.view.MyGridView;
import com.orange.oy.view.MyListView;

import java.util.ArrayList;

/**
 *
 *
 */

public class CommentAdapter extends BaseAdapter implements ItemCommentAdadpter.OnItemCheckListener {
    private Context context;
    private ArrayList<NewCommentInfo> list;
    private ArrayList<NewCommentInfo.CommentsBean> commentsBeanArrayList = new ArrayList<>();

    public CommentAdapter(Context context, ArrayList<NewCommentInfo> list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_comment);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.item_mylistview = (MyListView) convertView.findViewById(R.id.item_mylistview);
            viewHolder.item_mylistview.setOnItemClickListener(localGridviewOnitemClickListener);

            ItemCommentAdadpter imageSelectAdadpter = new ItemCommentAdadpter(context, commentsBeanArrayList);
            imageSelectAdadpter.setOnItemCheckListener(this);
            viewHolder.item_mylistview.setAdapter(imageSelectAdadpter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewCommentInfo commentInfo = list.get(position);
        if (!Tools.isEmpty(commentInfo.getActivity_name())) {
            viewHolder.tv_name.setText(commentInfo.getActivity_name());
        } else {
            viewHolder.tv_name.setText("");
        }
        commentsBeanArrayList = commentInfo.getComments();
        ItemCommentAdadpter imageSelectAdadpter = (ItemCommentAdadpter) viewHolder.item_mylistview.getAdapter();
        if (!commentsBeanArrayList.isEmpty()) {
            imageSelectAdadpter.setList(commentsBeanArrayList);
            imageSelectAdadpter.setParPosition(position);
        } else {
            imageSelectAdadpter.clearDeviceList();
        }
        imageSelectAdadpter.notifyDataSetChanged();
        return convertView;
    }

    private AdapterView.OnItemClickListener localGridviewOnitemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public void onItemCheck(NewCommentInfo.CommentsBean commentsBean) {
        onItemCheckListener.onItemReply(commentsBean);
    }


    class ViewHolder {
        private TextView tv_name;
        private MyListView item_mylistview;
    }

    private OnItemCheckListener onItemCheckListener;

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public interface OnItemCheckListener {
        void onItemReply(NewCommentInfo.CommentsBean commentsBean);
    }
}
