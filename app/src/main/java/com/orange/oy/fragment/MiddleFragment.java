package com.orange.oy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.mycorps_314.IdentifycodeLoginActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.view.AppTitle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.InvocationTargetException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MiddleFragment extends BaseFragment implements View.OnClickListener {


    public MiddleFragment() {
        // Required empty public constructor
    }

    private boolean isHavpersonalTask = true;//是否有个人任务 默认有

    public void setHavpersonalTask(boolean havpersonalTask) {
        isHavpersonalTask = havpersonalTask;
    }

    private TextView middle_apply, middle_assign;
    private LinearLayout middle_fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction2;
    private Fragment mShowFragment;//显示当前的fragment
    private ImageView middle_del;
    private TextView tv_middle_del;
    private AppTitle middle_title2;
    private View middle_title1;
    private boolean isJoind = false;//是否加入或创建过战队
    private boolean bindidcard = false;//是否进行过身份认证
    private boolean isInitUI = false;

    public void setJoind(boolean joind, boolean bindidcard) {
        isJoind = joind;
        this.bindidcard = bindidcard;
        if (mShowFragment != null && mShowFragment instanceof CorpsTaskFragment) {
            ((CorpsTaskFragment) mShowFragment).setJoind(isJoind, bindidcard);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_middle, container, false);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // EventBus.getDefault().register(this);
        isInitUI = false;
        initView(view);
        return view;
    }

    private void initView(View view) {
        middle_title1 = view.findViewById(R.id.middle_title1);
        middle_title2 = (AppTitle) view.findViewById(R.id.middle_title2);
        middle_apply = (TextView) view.findViewById(R.id.middle_apply);
        middle_assign = (TextView) view.findViewById(R.id.middle_assign);
        middle_fragment = (LinearLayout) view.findViewById(R.id.middle_fragment);
        middle_del = (ImageView) view.findViewById(R.id.middle_del);
        tv_middle_del = (TextView) view.findViewById(R.id.tv_middle_del);
        middle_apply.setOnClickListener(this);
        middle_assign.setOnClickListener(this);
        fragmentManager = getFragmentManager();
        middle_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("1");
                middle_del.setVisibility(View.GONE);
                tv_middle_del.setVisibility(View.VISIBLE);

            }
        });
        tv_middle_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("2");
                middle_del.setVisibility(View.VISIBLE);
                tv_middle_del.setVisibility(View.GONE);
            }
        });
    }

    private boolean isShowRight = false;

    public void setShowRight(boolean showRight) {
        isShowRight = showRight;
    }

    public void onResume() {
        super.onResume();
        if (!isInitUI) {
            if (!isHavpersonalTask) {
                settingType();
                onClick(middle_assign);
            } else {
                if (isShowRight) {
                    onClick(middle_assign);
                } else {
                    onClick(middle_apply);
                }
            }
        }
    }

    /**
     * 设置只有战队任务title
     */
    private void settingType() {
        middle_title1.setVisibility(View.GONE);
        middle_title2.setVisibility(View.VISIBLE);
        middle_title2.settingName("战队任务");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String data) {
        if (data.equals("3")) {//战队任务
            middle_del.setVisibility(View.INVISIBLE);
            tv_middle_del.setVisibility(View.GONE);
        }

        if (data.equals("4")) {
            middle_del.setVisibility(View.VISIBLE);
            tv_middle_del.setVisibility(View.GONE);
        }
    }

    public void clickRight() {
        onClick(middle_assign);
    }

    public void onClick(View v) {
        if (TextUtils.isEmpty(AppInfo.getKey(getContext()))) {
            ConfirmDialog.showDialog(getContext(), null, 2,
                    getResources().getString(R.string.nologin), "取消", "登录", null, false, new ConfirmDialog.OnSystemDialogClickListener() {
                        @Override
                        public void leftClick(Object object) {
                        }

                        @Override
                        public void rightClick(Object object) {
                            Intent intent = new Intent(getContext(), IdentifycodeLoginActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    });
            return;
        }
        switch (v.getId()) {
            case R.id.middle_apply: {
                middle_del.setVisibility(View.VISIBLE);
                middle_apply.setTextColor(getResources().getColor(R.color.app_background2));
                middle_apply.setBackgroundResource(R.drawable.change_task2_1);
                middle_assign.setTextColor(getResources().getColor(R.color.myreward_two));
                middle_assign.setBackgroundResource(R.drawable.change_task3);
                showCurrentFragment(ApplyFragment.TAG, ApplyFragment.class);
            }
            break;
            case R.id.middle_assign: {
                EventBus.getDefault().post("2");
                middle_apply.setTextColor(getResources().getColor(R.color.myreward_two));
                middle_apply.setBackgroundResource(R.drawable.change_task3);
                middle_assign.setTextColor(getResources().getColor(R.color.app_background2));
                middle_assign.setBackgroundResource(R.drawable.change_task2_2);
                middle_del.setVisibility(View.INVISIBLE);
                tv_middle_del.setVisibility(View.GONE);
                showCurrentFragment(CorpsTaskFragment.TAG, CorpsTaskFragment.class);
            }
            break;
        }
    }

    private void showCurrentFragment(String tag, Class<? extends Fragment> className) {
        isInitUI = true;
        fragmentTransaction2 = fragmentManager.beginTransaction();
        if (mShowFragment != null) {
            fragmentTransaction2.hide(mShowFragment);
        }
        //通过标记进行查找
        mShowFragment = fragmentManager.findFragmentByTag(tag);
        if (mShowFragment != null) {
            fragmentTransaction2.show(mShowFragment);
        } else {
            try {
                mShowFragment = className.getConstructor().newInstance();
                fragmentTransaction2.add(R.id.middle_fragment, mShowFragment);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (mShowFragment != null && mShowFragment instanceof CorpsTaskFragment) {
            ((CorpsTaskFragment) mShowFragment).setJoind(isJoind, bindidcard);
        }
        fragmentTransaction2.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
