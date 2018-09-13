package com.orange.oy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.CommentListInfo;
import com.orange.oy.info.NewmessageInfo;
import com.orange.oy.network.Urls;

import java.util.ArrayList;

import static com.orange.oy.R.id.tv_des;
import static com.orange.oy.R.id.tv_name;
import static com.orange.oy.R.id.tv_red_num;


/**
 * 评论...
 */
public class DiscussListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CommentListInfo> list;

    public DiscussListAdapter(Context context, ArrayList<CommentListInfo> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_discuss_list);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pics);
            viewHolder.iv_like = (ImageView) convertView.findViewById(R.id.iv_redlike);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.tv_des = (TextView) convertView.findViewById(R.id.tv_des);
            viewHolder.tv_red_num = (TextView) convertView.findViewById(R.id.tv_red_num);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final CommentListInfo commentListInfo = list.get(position);

        //  Glide.with(context).load(Urls.ImgIp + commentListInfo.getUser_img()).into(viewHolder.iv_pic);
        Glide.with(context)
                .load(Urls.ImgIp + commentListInfo.getUser_img())
                .asBitmap()  //这句不能少，否则下面的方法会报错
                .centerCrop()
                .into(new BitmapImageViewTarget(viewHolder.iv_pic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        viewHolder.iv_pic.setImageDrawable(circularBitmapDrawable);
                    }
                });

        viewHolder.tv_time.setText(commentListInfo.getCreate_time());
        viewHolder.tv_name.setText(commentListInfo.getUser_name());
        viewHolder.tv_red_num.setText(commentListInfo.getPraise_num() + "");

        /*****  is_praise  : 是否点过赞，1为点过，0为没点过   **************/
        if (commentListInfo.getIs_praise().equals("0")) {
            viewHolder.iv_like.setImageResource(R.mipmap.ckdt_button_zanhui);
        } else if (commentListInfo.getIs_praise().equals("1")) {
            viewHolder.iv_like.setImageResource(R.mipmap.ckdt_button_zanhong);
        }

        if (!Tools.isEmpty(commentListInfo.getComment_username())) {
            String content = commentListInfo.getUser_name() + "@" + commentListInfo.getComment_username();
            String contents = content + commentListInfo.getContent();
            int start = contents.indexOf(content);
            int end = start + content.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(contents);
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.homepage_select)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tv_des.setText(builder);
        } else {
            viewHolder.tv_des.setText(commentListInfo.getContent());
        }

        viewHolder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orlikeClickListener.onlike(position);
                if (commentListInfo.getIs_praise().equals("0")) {
                    commentListInfo.setIs_praise("1");
                    viewHolder.iv_like.setImageResource(R.mipmap.ckdt_button_zanhong);
                } else if (commentListInfo.getIs_praise().equals("1")) {
                    commentListInfo.setIs_praise("0");
                    viewHolder.iv_like.setImageResource(R.mipmap.ckdt_button_zanhui);
                }
            }
        });
        this.notifyDataSetChanged();
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_pic, iv_like;
        private TextView tv_time, tv_name, tv_des, tv_red_num;
    }


    private OrlikeClickListener orlikeClickListener;

    public interface OrlikeClickListener {
        void onlike(int pos);
    }

    public void setOnOrlikeClickListener(OrlikeClickListener orlikeClickListener) {
        this.orlikeClickListener = orlikeClickListener;
    }
}