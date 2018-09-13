package com.orange.oy.adapter.mycorps_314;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.shakephoto.ShakeAlbumInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/6.
 * 相册查看==我参与的活动(我参与的活动) V3.16
 */

public class ShakeAlbumAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShakeAlbumInfo> list;
    private boolean isClick1;
//    private boolean isClick2;
//    private boolean isClick3;
//    private boolean isClick4;
//    private boolean isClick5;
//    private ImageLoader imageLoader;
//    private boolean isJoin;

//    public ShakeAlbumAdapter(Context context, ArrayList<ShakeAlbumInfo> list, boolean isJoin) {
//        this.context = context;
//        this.list = list;
//        imageLoader = new ImageLoader(context);
//        this.isJoin = isJoin;
//    }

    public boolean isClick1() {
        return isClick1;
    }

    public void clearClick() {
        isClick1 = false;
    }

    public ShakeAlbumAdapter(Context context, ArrayList<ShakeAlbumInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_shakealbum);
            viewHolder.itemshake_img = (SimpleDraweeView) convertView.findViewById(R.id.itemshake_img);
            viewHolder.itemshake_shaked = (ImageView) convertView.findViewById(R.id.itemshake_shaked);
            viewHolder.itemshake_title = (TextView) convertView.findViewById(R.id.itemshake_title);
            viewHolder.itemshake_prize = (TextView) convertView.findViewById(R.id.itemshake_prize);
            viewHolder.itemshake_prize1 = (TextView) convertView.findViewById(R.id.itemshake_prize1);
            viewHolder.itemshake_desc = (TextView) convertView.findViewById(R.id.itemshake_desc);
            viewHolder.itemshake_money = (TextView) convertView.findViewById(R.id.itemshake_money);
            viewHolder.itemshake_redly = convertView.findViewById(R.id.itemshake_redly);
            int rw = Tools.getScreeInfoWidth(context) / 2;
            if (Tools.dipToPx((Activity) context, 180) > (rw)) {
                ViewGroup.LayoutParams lp = viewHolder.itemshake_img.getLayoutParams();
                lp.width = rw;
                lp.height = rw * 13 / 18;
                viewHolder.itemshake_img.setLayoutParams(lp);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ShakeAlbumInfo shakeAlbumInfo = list.get(position);

        if (position == 0) {
            viewHolder.itemshake_desc.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemshake_desc.setVisibility(View.GONE);
        }
        String url = Urls.Endpoint3 + shakeAlbumInfo.getPhoto_url() + "?x-oss-process=image/resize,l_250";
        Uri uri = Uri.parse(url);
        viewHolder.itemshake_img.setImageURI(uri);
        viewHolder.itemshake_title.setText(shakeAlbumInfo.getActivity_name());
        viewHolder.itemshake_money.setText(Tools.removePoint(shakeAlbumInfo.getSponsor_money()) + "元");

        String prize = shakeAlbumInfo.getPrize();
        if (!Tools.isEmpty(prize)) {
            viewHolder.itemshake_prize.setText("[" + prize + "]");
            viewHolder.itemshake_prize.setVisibility(View.VISIBLE);
            viewHolder.itemshake_prize1.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemshake_prize.setVisibility(View.GONE);
            viewHolder.itemshake_prize1.setVisibility(View.GONE);
        }

        String redpack_state = shakeAlbumInfo.getRedpack_state();
        if ("1".equals(redpack_state)) {
            viewHolder.itemshake_redly.setVisibility(View.VISIBLE);
            viewHolder.itemshake_redly.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    isClick1 = true;
                    return false;
                }
            });
        } else {
            viewHolder.itemshake_redly.setVisibility(View.GONE);
        }

        if ("1".equals(shakeAlbumInfo.getIs_join())) {
            viewHolder.itemshake_shaked.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemshake_shaked.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView itemshake_shaked;
        private SimpleDraweeView itemshake_img;
        private TextView itemshake_title, itemshake_prize, itemshake_prize1;
        private TextView itemshake_desc, itemshake_money;
        private View itemshake_redly;//拆红包

    }
}
