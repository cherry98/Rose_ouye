package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.TeamSpecialtyInfo;

import java.util.ArrayList;


/**
 * 添加 Tags标签
 */

public class TagsView extends LinearLayout implements View.OnClickListener {
    public interface OnOtherClickListener {
        void clickOther();
    }

    public interface OnRemoveClickListener {
        void remove();
    }

    private int teamspecialty_label_height, teamspecialty_label_marginright, teamspecialty_label_text_margin,
            teamspecialty_label_del_height, teamspecialty_label_del_marginright, teamspecialty_label_margintop,
            teamspecialty_label_margin;
    private LinearLayout teamspecialty_select_layout;
    private RelativeLayout teamspecialty_other_layout;
    private ArrayList<TeamSpecialtyInfo> teamSpecialtyDefaultLabels;
    private View teamspecialty_other_button;
    private EditText teamspecialty_other_edit;
    private OnOtherClickListener onOtherClickListener;
    private String hinttext;
    private int addTagsNum;
    private OnClick onClick;
    private boolean isShow;//可以回显数据
    private FlowLayoutView teamspecialty_flowlayout;

    /**
     * 获取选择的标签
     * [0]:选择系统的
     * [1]:自定义的
     *
     * @return
     */
    public String[] getSelectLabelForNet() {
        String[] strings = new String[]{"", ""};
        for (TeamSpecialtyInfo teamSpecialtyInfo : teamSpecialtyDefaultLabels) {
            if (teamSpecialtyInfo.isSelect()) {
                if (teamSpecialtyInfo.isCustom()) {
                    if (TextUtils.isEmpty(strings[1])) {
                        strings[1] = teamSpecialtyInfo.getName();
                    } else {
                        strings[1] = strings[1] + "," + teamSpecialtyInfo.getName();
                    }
                } else {
                    if (TextUtils.isEmpty(strings[0])) {
                        strings[0] = teamSpecialtyInfo.getId();
                    } else {
                        strings[0] = strings[0] + "," + teamSpecialtyInfo.getId();
                    }
                }
            }
        }
        return strings;
    }

    /**
     * 设置默认标签
     *
     * @param teamSpecialtyDefaultLabels
     */
    public void setTeamSpecialtyDefaultLabels(ArrayList<TeamSpecialtyInfo> teamSpecialtyDefaultLabels) {
        this.teamSpecialtyDefaultLabels = teamSpecialtyDefaultLabels;
    }

    public TagsView(Context context) {
        this(context, null);
    }

    public TagsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        teamspecialty_label_height = (int) getResources().getDimension(R.dimen.teamspecialty_label_height);
        teamspecialty_label_marginright = (int) getResources().getDimension(R.dimen.teamspecialty_label_marginright);
        teamspecialty_label_text_margin = (int) getResources().getDimension(R.dimen.teamspecialty_label_text_margin);
        teamspecialty_label_del_height = (int) getResources().getDimension(R.dimen.teamspecialty_label_del_height);
        teamspecialty_label_del_marginright = (int) getResources().getDimension(R.dimen.teamspecialty_label_del_marginright);
        teamspecialty_label_margintop = (int) getResources().getDimension(R.dimen.teamspecialty_label_margintop);
        teamspecialty_label_margin = (int) getResources().getDimension(R.dimen.teamspecialty_label_margin);
        init();
    }

    private void init() {
        Tools.loadLayout(this, R.layout.view_tags);
        teamspecialty_other_layout = (RelativeLayout) findViewById(R.id.teamspecialty_other_layout);
        teamspecialty_select_layout = (LinearLayout) findViewById(R.id.teamspecialty_select_layout);
        teamspecialty_other_edit = (EditText) findViewById(R.id.teamspecialty_other_edit);
        teamspecialty_flowlayout = (FlowLayoutView) findViewById(R.id.teamspecialty_flowlayout);
        teamspecialty_other_edit.setHint(hinttext);
        teamspecialty_other_edit.setHintTextColor(Color.parseColor("#FFA0A0A0"));
        teamspecialty_other_edit.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(",")) {
                    teamspecialty_other_edit.setText(s.toString().replaceAll(",", ""));
                    teamspecialty_other_edit.setSelection(teamspecialty_other_edit.getText().length());
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        teamspecialty_other_button = findViewById(R.id.teamspecialty_other_button);
        teamspecialty_other_button.setOnClickListener(this);
    }

    public void setOnOtherClickListener(OnOtherClickListener onOtherClickListener, String hinttext, int addTagsNum) {
        this.onOtherClickListener = onOtherClickListener;
        this.hinttext = hinttext;
        this.addTagsNum = addTagsNum;
        if (teamspecialty_other_edit != null)
            teamspecialty_other_edit.setHint(hinttext);
    }

    public void setOnOtherClickListener(OnOtherClickListener onOtherClickListener, String hinttext, int addTagsNum, int textNum, boolean isShow) {
        this.onOtherClickListener = onOtherClickListener;
        this.hinttext = hinttext;
        this.addTagsNum = addTagsNum;
        this.isShow = isShow;
        //限制输入字数
        teamspecialty_other_edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(textNum)});
        if (teamspecialty_other_edit != null)
            teamspecialty_other_edit.setHint(hinttext);
    }

    private boolean isNotifyDataSetChanged = false;

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isNotifyDataSetChanged) {
            new MyHandler().sendEmptyMessageDelayed(0, 100);
            isNotifyDataSetChanged = false;
        }
    }

    private void removeParent(View view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            }
        }
    }

    public void showData() {
        //默认标签布局刷新
        if (teamSpecialtyDefaultLabels != null && !teamSpecialtyDefaultLabels.isEmpty()) {
            teamspecialty_flowlayout.setVisibility(VISIBLE);
            for (int i = 0; i < teamSpecialtyDefaultLabels.size(); i++) {
                TextView textView = new TextView(getContext());
                textView.setText(teamSpecialtyDefaultLabels.get(i).getName());
                textView.setPadding(15, 5, 15, 5);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(12);
                textView.setTextColor(getResources().getColor(R.color.homepage_select));
                textView.setBackgroundResource(R.drawable.teamspecialty_label_bg);
                teamspecialty_flowlayout.addView(textView);
            }
        } else {
            teamspecialty_flowlayout.setVisibility(GONE);
        }
    }

    public void notifyDataSetChanged() {
        if (getWidth() == 0) {
            isNotifyDataSetChanged = true;
            return;
        }
        if (isShow) {
            showData();
        } else {
            //已选择标签布局
            teamspecialty_select_layout.removeAllViews();
            if (teamSpecialtyDefaultLabels != null && !teamSpecialtyDefaultLabels.isEmpty()) {
                int layoutwidth = 0;
                LayoutParams tabParams = new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                boolean isAddMar;
                LinearLayout tempLayout = new LinearLayout(getContext());
                tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                teamspecialty_select_layout.addView(tempLayout, tabParams);
                int size = teamSpecialtyDefaultLabels.size();
                for (int i = 0; i < size; i++) {
                    TeamSpecialtyInfo teamSpecialtyInfo = teamSpecialtyDefaultLabels.get(i);
                    if (teamSpecialtyInfo.isSelect()) {
                        if (teamSpecialtyInfo.isCustom()) {//自定义标签
                            MyTextView myTextView;
                            if (teamSpecialtyInfo.getView() == null) {
                                myTextView = new MyTextView(getContext());
                                myTextView.setText(teamSpecialtyInfo.getName());
                                myTextView.setOnClickListener(selectLabelListener);
                                myTextView.setTag(teamSpecialtyInfo);
                                teamSpecialtyInfo.setView(myTextView);
                            } else {
                                myTextView = (MyTextView) teamSpecialtyInfo.getView();
                            }
                            int textWidth = myTextView.getLabelWidth();
                            layoutwidth = layoutwidth + textWidth + teamspecialty_label_margin;
                            isAddMar = true;
                            if (layoutwidth >= getWidth()) {
                                layoutwidth = layoutwidth - teamspecialty_label_margin;
                                if (layoutwidth >= getWidth()) {
                                    layoutwidth = textWidth + teamspecialty_label_margin;
                                    tempLayout = new LinearLayout(getContext());
                                    tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    tabParams.topMargin = teamspecialty_label_margintop;
                                    teamspecialty_select_layout.addView(tempLayout, tabParams);
                                } else {
                                    isAddMar = false;
                                }
                            }
                            LayoutParams params = new LayoutParams(textWidth, teamspecialty_label_height);
                            if (isAddMar) {
                                params.rightMargin = teamspecialty_label_margin;
                            }
                            removeParent(myTextView);
                            tempLayout.addView(myTextView, params);
                        } else {//原始标签
                            TextView textView;
                            if (teamSpecialtyInfo.getView() == null) {
                                textView = new TextView(getContext());
                                textView.setTextSize(12);
                                textView.setGravity(Gravity.CENTER);
                                textView.setText(teamSpecialtyInfo.getName());
                                textView.setOnClickListener(selectLabelListener);
                                textView.setTag(teamSpecialtyInfo.getId());
                                teamSpecialtyInfo.setView(textView);
                            } else {
                                textView = (TextView) teamSpecialtyInfo.getView();
                            }
                            textView.setId(i);
                            textView.setTextColor(Color.parseColor("#F65D57"));
                            textView.setBackgroundResource(R.drawable.teamspecialty_label_bg);
                            int length = teamSpecialtyInfo.getName().length();
                            TextPaint paint = textView.getPaint();
                            int minus = length - 4;
                            int textWidth;
                            if (minus <= 0) {
                                textWidth = (int) (paint.measureText("神秘顾客") + teamspecialty_label_text_margin * 2) + 1;
                            } else {
                                textWidth = (int) (paint.measureText(teamSpecialtyInfo.getName()) + teamspecialty_label_text_margin * 2) + 1;
                            }
                            layoutwidth = layoutwidth + textWidth + teamspecialty_label_margin;
                            isAddMar = true;
                            if (layoutwidth >= getWidth()) {
                                layoutwidth = layoutwidth - teamspecialty_label_margin;
                                if (layoutwidth >= getWidth()) {
                                    layoutwidth = textWidth + teamspecialty_label_margin;
                                    tempLayout = new LinearLayout(getContext());
                                    tempLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    tabParams.topMargin = teamspecialty_label_margintop;
                                    teamspecialty_select_layout.addView(tempLayout, tabParams);
                                } else {
                                    isAddMar = false;
                                }
                            }
                            LayoutParams params = new LayoutParams(textWidth, teamspecialty_label_height);
                            if (isAddMar) {
                                params.rightMargin = teamspecialty_label_margin;
                            }
                            removeParent(textView);
                            tempLayout.addView(textView, params);
                        }
                    }
                }
            }
        }
    }

    private OnClickListener selectLabelListener = new OnClickListener() {
        public void onClick(View v) {
            if (v instanceof TextView) {
                TeamSpecialtyInfo teamSpecialtyInfo = teamSpecialtyDefaultLabels.get(v.getId());
                if (teamSpecialtyInfo.getName().equals("其他")) {
                    if (onOtherClickListener != null) {
                        onOtherClickListener.clickOther();
                    }
                } else {
                    teamSpecialtyInfo.setSelect(!teamSpecialtyInfo.isSelect());
                    notifyDataSetChanged();
                }
            } else if (v instanceof MyTextView) {
                teamSpecialtyDefaultLabels.remove(v.getTag());
                onClick.clickMinus();
                notifyDataSetChanged();
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.teamspecialty_other_button: {//添  加
                String str = teamspecialty_other_edit.getText().toString().trim();
                if (!TextUtils.isEmpty(str)) {
                    if (teamSpecialtyDefaultLabels.size() >= addTagsNum) {
                        Tools.showToast(getContext(), "最多可添加" + addTagsNum + "个哦~");
                        return;
                    }
                    str.replaceAll(",", "");
                    for (TeamSpecialtyInfo teamSpecialtyInfo : teamSpecialtyDefaultLabels) {
                        if (!TextUtils.isEmpty(teamSpecialtyInfo.getName())) {
                            if (teamSpecialtyInfo.getName().equals(str)) {
                                Tools.showToast(getContext(), "这个特长已经有了哦~");
                                return;
                            }
                        }

                    }
                    TeamSpecialtyInfo teamSpecialtyInfo = new TeamSpecialtyInfo();
                    teamSpecialtyInfo.setSelect(true);
                    teamSpecialtyInfo.setCustom(true);
                    teamSpecialtyInfo.setName(str);
                    teamSpecialtyDefaultLabels.add(teamSpecialtyInfo);
                    teamspecialty_other_edit.setText("");
                    onClick.clickPlus();
                    isShow = false;
                    notifyDataSetChanged();
                }
            }
            break;
        }
    }

    private class MyTextView extends LinearLayout {
        private TextView textView;
        private ImageView imageView;

        public MyTextView(@NonNull Context context) {
            super(context);
            setBackgroundResource(R.drawable.teamspecialty_label_bg);
            setGravity(Gravity.CENTER_VERTICAL);
            setOrientation(LinearLayout.HORIZONTAL);
            textView = new TextView(context);
            textView.setTextSize(12);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.parseColor("#F65D57"));
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, teamspecialty_label_height, 1);
            lp.rightMargin = teamspecialty_label_marginright;
            lp.leftMargin = teamspecialty_label_text_margin;
            addView(textView, lp);
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(R.mipmap.teamspecialty_del);
            LayoutParams lp2 = new LayoutParams(teamspecialty_label_del_height, teamspecialty_label_del_height);
            lp2.rightMargin = teamspecialty_label_del_marginright;
            addView(imageView, lp2);
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public TextView getTextView() {
            return textView;
        }

        public int getLabelWidth() {
            TextPaint paint = textView.getPaint();
            return (int) (paint.measureText(textView.getText().toString()) + teamspecialty_label_text_margin
                    + teamspecialty_label_marginright + teamspecialty_label_del_height + teamspecialty_label_del_marginright) + 1;
        }
    }

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            notifyDataSetChanged();
            super.handleMessage(msg);
        }
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public interface OnClick {
        void clickPlus();

        void clickMinus();
    }
}
