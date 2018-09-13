package com.orange.oy.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

/**
 * 任务页-未登录状态
 */
public class TaskNologinFragment extends BaseFragment implements View.OnClickListener, RadioGroup
        .OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    private View mView;
    private ImageLoader imageLoader;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tasknologin_new, container, false);
        return mView;
    }

    public interface OnLoginChangeInTaskListener {
        void loginChange();
    }

    public void setOnLoginChangeInTaskListener(OnLoginChangeInTaskListener listener) {
        onLoginChangeInTaskListener = listener;
    }

    private ArrayList<View> pageList;
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private OnLoginChangeInTaskListener onLoginChangeInTaskListener;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            RelativeLayout title_layout = (RelativeLayout) mView.findViewById(R.id.title_layout);
            int height = (int) getResources().getDimension(R.dimen.apptitle_height);
            if (title_layout.getHeight() != height) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title_layout.getLayoutParams();
                lp.height = height;
                title_layout.setLayoutParams(lp);
                title_layout.setPadding(0, 0, 0, 0);
            }
        }

        imageLoader = new ImageLoader(getContext());
//        imageView1 = new ImageView(getContext());
//        imageView2 = new ImageView(getContext());
//        imageView3 = new ImageView(getContext());
//        imageView4 = new ImageView(getContext());
//        imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView4.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView1.setImageResource(R.mipmap.loading1);
//        imageView2.setImageResource(R.mipmap.loading2);
//        imageView3.setImageResource(R.mipmap.loading3);
//        imageView4.setImageResource(R.mipmap.loading4);
//        pageList = new ArrayList<>();
//        pageList.add(imageView1);
//        pageList.add(imageView2);
//        pageList.add(imageView3);
//        pageList.add(imageView4);
        mView.findViewById(R.id.tasklg_login).setOnClickListener(this);
    }

    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(AppInfo.getKey(getContext())) && onLoginChangeInTaskListener != null) {
            onLoginChangeInTaskListener.loginChange();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tasklg_login: {
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                intent.putExtra("nologin", "1");
//                startActivity(intent);
//
//            }
//            break;
        }
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
    }

    public void onPageScrollStateChanged(int state) {
    }
}
