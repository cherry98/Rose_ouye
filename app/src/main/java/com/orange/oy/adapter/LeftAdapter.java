package com.orange.oy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.shakephoto_316.LargeImagePageActivity;
import com.orange.oy.activity.shakephoto_318.LeftActivity;
import com.orange.oy.activity.shakephoto_318.ShakephotoActivity;
import com.orange.oy.activity.shakephoto_318.ThemeDetailActivity;
import com.orange.oy.base.ScreenManager;
import com.orange.oy.base.Tools;
import com.orange.oy.info.LargeImagePageInfo;
import com.orange.oy.info.shakephoto.PhotoListBean;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;
import com.orange.oy.info.shakephoto.ShakeThemeInfo;
import com.orange.oy.view.MyGridView;

import java.util.ArrayList;

/**
 * 甩图相册adapter  V3.18
 */

public class LeftAdapter extends BaseAdapter implements ImageSelect2Adadpter.OnItemCheckListener {
    private Context context;
    private ArrayList<ShakePhotoInfo2> list;
    private ArrayList<LargeImagePageInfo> largeImagePageInfos;
    private boolean isShow;
    private boolean isClick;
    private ArrayList<PhotoListBean> photoList = new ArrayList<>();
    private ShakeThemeInfo shakeThemeInfo;

    public LeftAdapter(Context context, ArrayList<ShakePhotoInfo2> list, ShakeThemeInfo shakeThemeInfo) {
        this.context = context;
        this.shakeThemeInfo = shakeThemeInfo;
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
            convertView = Tools.loadLayout(context, R.layout.item_localalbum2);
            viewHolder.itemlocal2_addr = (TextView) convertView.findViewById(R.id.itemlocal2_addr);
            viewHolder.itemlocal2_time = (TextView) convertView.findViewById(R.id.itemlocal2_time);
            viewHolder.itemlocal_gridview = (MyGridView) convertView.findViewById(R.id.grid_gridview);
            viewHolder.itemlocal_gridview.setOnItemClickListener(localGridviewOnitemClickListener);

            ImageSelect2Adadpter imageSelectAdadpter = new ImageSelect2Adadpter(context, photoList);
            imageSelectAdadpter.setOnItemCheckListener(this);
            viewHolder.itemlocal_gridview.setAdapter(imageSelectAdadpter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ShakePhotoInfo2 shakeInfo = list.get(position);
        // ShakePhotoInfo
        viewHolder.itemlocal2_addr.setText(shakeInfo.getArea());
        viewHolder.itemlocal2_time.setText(shakeInfo.getTime());

        photoList = shakeInfo.getList();
        if (!photoList.isEmpty()) {
            String province = shakeInfo.getProvince();
            String city = shakeInfo.getCity();
            String country = shakeInfo.getCounty();
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
            ImageSelect2Adadpter imageSelectAdadpter = (ImageSelect2Adadpter) viewHolder.itemlocal_gridview.getAdapter();
            imageSelectAdadpter.setList(photoList);
            imageSelectAdadpter.setParPosition(position);
            imageSelectAdadpter.notifyDataSetChanged();

            if (isShow) {  //显示与否
                for (int i = 0; i < photoList.size(); i++) {
                    PhotoListBean photoListBean = photoList.get(i);
                    photoListBean.setShow(true);
                }
            } else {
                for (int i = 0; i < photoList.size(); i++) {
                    PhotoListBean photoListBean = photoList.get(i);
                    photoListBean.setShow(false);
                }
            }
            imageSelectAdadpter.notifyDataSetChanged();
        }
        return convertView;
    }

    private AdapterView.OnItemClickListener localGridviewOnitemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (shakeThemeInfo != null) {
                if (view.getTag() instanceof ImageSelect2Adadpter.ImageSelectAdapterViewhold) {
                    Intent intent = new Intent(context, ShakephotoActivity.class);
                    intent.putExtra("shakeThemeInfo", shakeThemeInfo);
                    intent.putExtra("isuppic", true);
                    intent.putExtra("mPath", ((ImageSelect2Adadpter.ImageSelectAdapterViewhold) view.getTag()).mPath);
                    context.startActivity(intent);
                    ScreenManager.getScreenManager().finishActivity(LeftActivity.class);
                }
            } else {
                int parPosition = 0;
                int nowPasition = 0;
                if (view.getTag() instanceof ImageSelect2Adadpter.ImageSelectAdapterViewhold) {
                    parPosition = ((ImageSelect2Adadpter.ImageSelectAdapterViewhold) view.getTag()).parPosition;
                }
                if (list != null && !list.isEmpty()) {
                    if (largeImagePageInfos == null) {
                        largeImagePageInfos = new ArrayList<>();
                        int size = list.size();
                        boolean isParPosition = false;
                        for (int i = 0; i < size; i++) {
                            ShakePhotoInfo2 shakePhotoInfo = list.get(i);
                            isParPosition = i == parPosition;
                            int length = shakePhotoInfo.getList().size();
                            for (int j = 0; j < length; j++) {
                                PhotoListBean photoListBean = shakePhotoInfo.getList().get(j);
                                LargeImagePageInfo largeImagePageInfo = new LargeImagePageInfo();
                                largeImagePageInfo.setFile_url(photoListBean.getFile_url());

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
                            ShakePhotoInfo2 shakePhotoInfo = list.get(i);
                            isParPosition = i == parPosition;
                            int length = shakePhotoInfo.getList().size();
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
                    intent.putExtra("state", 1);
                    intent.putExtra("photoList", photoList);
                    intent.putExtra("isRight", 1); //查看大图，如果有值的话，右上角有上传
                    context.startActivity(intent);
                }
            }
        }
    };

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        largeImagePageInfos = null;
    }

    public void onItemCheck(PhotoListBean photoListBean) {
        onItemCheckListener.onItemCheck(photoListBean);
    }


    class ViewHolder {
        private TextView itemlocal2_time, itemlocal2_addr;
        private MyGridView itemlocal_gridview;
    }

    private OnItemCheckListener onItemCheckListener;

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public interface OnItemCheckListener {
        void onItemCheck(PhotoListBean photoListBean);


    }

    public class ImageSelectAdapterViewhold {
        ImageView itemimage_img;
        CheckBox itemimage_check;
        int parPosition;
    }
}
