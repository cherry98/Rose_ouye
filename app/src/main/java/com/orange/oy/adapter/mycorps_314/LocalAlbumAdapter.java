package com.orange.oy.adapter.mycorps_314;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.shakephoto.LocalPhotoInfo;
import com.orange.oy.view.MyGridView;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2018/6/6.
 * 相册查看==我参与的活动(本地相册) V3.16
 */

public class LocalAlbumAdapter extends BaseAdapter implements ImageSelectAdadpter.OnItemCheckListener {
    private Context context;
    private ArrayList<LocalPhotoInfo> list;
    private ArrayList<LargeImagePageInfo> largeImagePageInfos;
    private boolean isShow;
    private boolean isClick;

    public LocalAlbumAdapter(Context context, ArrayList<LocalPhotoInfo> list) {
        this.context = context;
        this.list = list;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getCount() {
        return list.size();
    }

    public void setShow(boolean show) {
        isShow = show;
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
            convertView = Tools.loadLayout(context, R.layout.item_localalbum);
            viewHolder.itemlocal_addr = (TextView) convertView.findViewById(R.id.itemlocal_addr);
            viewHolder.itemlocal_num = (TextView) convertView.findViewById(R.id.itemlocal_num);
            viewHolder.itemlocal_info = (TextView) convertView.findViewById(R.id.itemlocal_info);
            viewHolder.itemlocal_map = (TextView) convertView.findViewById(R.id.itemlocal_map);
            viewHolder.itemlocal_mapnum = (TextView) convertView.findViewById(R.id.itemlocal_mapnum);
            viewHolder.itemlocal_gridview = (MyGridView) convertView.findViewById(R.id.itemlocal_gridview);
            viewHolder.itemlocal_gridview.setOnItemClickListener(localGridviewOnitemClickListener);
            ArrayList<LocalPhotoInfo.PhotoListBean> list_photo = new ArrayList<>();
            ImageSelectAdadpter imageSelectAdadpter = new ImageSelectAdadpter(context, list_photo);
            imageSelectAdadpter.setOnItemCheckListener(this);
            viewHolder.itemlocal_gridview.setAdapter(imageSelectAdadpter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final LocalPhotoInfo localPhotoInfo = list.get(position);
        if (position == 0 && localPhotoInfo.isShowMap()) {
            viewHolder.itemlocal_map.setVisibility(View.VISIBLE);
            viewHolder.itemlocal_mapnum.setVisibility(View.VISIBLE);
            viewHolder.itemlocal_mapnum.setText(localPhotoInfo.getTotal_photo_num());
            viewHolder.itemlocal_map.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isClick = true;
                    return false;
                }
            });
        } else {
            viewHolder.itemlocal_map.setVisibility(View.GONE);
            viewHolder.itemlocal_mapnum.setVisibility(View.GONE);
        }
        viewHolder.itemlocal_addr.setText(localPhotoInfo.getArea());
        viewHolder.itemlocal_num.setText(localPhotoInfo.getPhoto_num() + "张");
        ArrayList<LocalPhotoInfo.PhotoListBean> list_photo = localPhotoInfo.getPhoto_list();
        if (!list_photo.isEmpty()) {
            String province = list_photo.get(0).getProvince();
            String city = list_photo.get(0).getCity();
            String country = list_photo.get(0).getCounty();
            String district = city;
            if (!Tools.isEmpty(province)) {
                if (!city.equals(province)) {
                    district = province + "," + district;
                }
            }
            if (!Tools.isEmpty(country)) {
                if (!city.equals(country)) {
                    district = district + "," + country;
                }
            }
            viewHolder.itemlocal_info.setText(localPhotoInfo.getCreate_time() + "   " + district);
            ImageSelectAdadpter imageSelectAdadpter = (ImageSelectAdadpter) viewHolder.itemlocal_gridview.getAdapter();
            imageSelectAdadpter.setList(list_photo);
            imageSelectAdadpter.setParPosition(position);
            imageSelectAdadpter.notifyDataSetChanged();
            if (isShow) {
                for (int i = 0; i < list_photo.size(); i++) {
                    LocalPhotoInfo.PhotoListBean photoListBean = list_photo.get(i);
                    photoListBean.setShow(true);
                }
            } else {
                for (int i = 0; i < list_photo.size(); i++) {
                    LocalPhotoInfo.PhotoListBean photoListBean = list_photo.get(i);
                    photoListBean.setShow(false);
                }
            }
            imageSelectAdadpter.notifyDataSetChanged();
        }
        return convertView;
    }

    private AdapterView.OnItemClickListener localGridviewOnitemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int parPosition = 0;
            int nowPasition = 0;
            if (view.getTag() instanceof ImageSelectAdapterViewhold) {
                parPosition = ((ImageSelectAdapterViewhold) view.getTag()).parPosition;
            }
            if (list != null && !list.isEmpty()) {
                if (largeImagePageInfos == null) {
                    largeImagePageInfos = new ArrayList<>();
                    int size = list.size();
                    boolean isParPosition = false;
                    for (int i = 0; i < size; i++) {
                        LocalPhotoInfo shakePhotoInfo = list.get(i);
                        isParPosition = i == parPosition;
                        int length = shakePhotoInfo.getPhoto_list().size();
                        for (int j = 0; j < length; j++) {
                            LocalPhotoInfo.PhotoListBean photoListBean = shakePhotoInfo.getPhoto_list().get(j);
                            LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
                            largeImagePageInfo.setKey_concent(photoListBean.getKey_concent());
                            largeImagePageInfo.setFile_url(photoListBean.getFile_url());
                            largeImagePageInfo.setAddress(photoListBean.getAddress());
                            largeImagePageInfo.setShow_address("1");
                            largeImagePageInfo.setCreate_time(photoListBean.getCreate_time());
                            largeImagePageInfo.setAitivity_name(photoListBean.getAitivity_name());
                            largeImagePageInfo.setIsHaveDelete("2");
                            largeImagePageInfo.setFi_id(photoListBean.getFi_id());
                            largeImagePageInfos.add(largeImagePageInfo);
                            if (isParPosition && position == j) {
                                nowPasition = largeImagePageInfos.size() - 1;
                            }
                        }
                    }
                } else {
                    int size = list.size();
                    boolean isParPosition = false;
                    int index = 0;
                    for (int i = 0; i < size; i++) {
                        LocalPhotoInfo shakePhotoInfo = list.get(i);
                        isParPosition = i == parPosition;
                        int length = shakePhotoInfo.getPhoto_list().size();
                        for (int j = 0; j < length; j++) {
                            index++;
                            if (isParPosition && position == j) {
                                nowPasition = index - 1;
                                break;
                            }
                        }
                        if (isParPosition) {
                            break;
                        }
                    }
                }
                Intent intent = new Intent(context, LargeImagePageActivity.class);
                intent.putExtra("isList", true);
                intent.putExtra("list", largeImagePageInfos);
                intent.putExtra("position", nowPasition);
                context.startActivity(intent);
            }
        }
    };

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        largeImagePageInfos = null;
    }

    public void onItemCheck(LocalPhotoInfo.PhotoListBean photoListBean) {
        onItemCheckListener.onItemCheck(photoListBean);
    }

    class ViewHolder {
        private TextView itemlocal_addr, itemlocal_num, itemlocal_info, itemlocal_map, itemlocal_mapnum;
        private MyGridView itemlocal_gridview;
    }

    private OnItemCheckListener onItemCheckListener;

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public interface OnItemCheckListener {
        void onItemCheck(LocalPhotoInfo.PhotoListBean photoListBean);
    }
}
