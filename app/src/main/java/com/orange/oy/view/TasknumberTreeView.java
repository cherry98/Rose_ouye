package com.orange.oy.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by Administrator on 2018/5/23.
 * 任务列表的状态数量树
 */

public class TasknumberTreeView extends RelativeLayout {
    public TasknumberTreeView(Context context) {
        this(context, null, 0);
    }

    public TasknumberTreeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TasknumberTreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private TextView viewtasknumbertree_sum_num;//总量
    private TextView point_1_num, point_1_txt;
    private ImageView point_1;
    private TextView point_2_num, point_2_txt;
    private ImageView point_2;
    private TextView point_3_num, point_3_txt;
    private ImageView point_3;
    private TextView point_4_num, point_4_txt;
    private ImageView point_4;
    private ImageView point_5;//已通过
    private TextView viewtasknumbertree_right1, viewtasknumbertree_right1_num;
    private ImageView point_6;//未通过
    private TextView viewtasknumbertree_right2, viewtasknumbertree_right2_num;

    private void initView() {
        Tools.loadLayout(this, R.layout.view_tasknumbertree);
        viewtasknumbertree_sum_num = (TextView) findViewById(R.id.viewtasknumbertree_sum_num);
        point_1_num = (TextView) findViewById(R.id.point_1_num);
        point_1_txt = (TextView) findViewById(R.id.point_1_txt);
        point_1 = (ImageView) findViewById(R.id.point_1);
        point_2_num = (TextView) findViewById(R.id.point_2_num);
        point_2_txt = (TextView) findViewById(R.id.point_2_txt);
        point_2 = (ImageView) findViewById(R.id.point_2);
        point_3_num = (TextView) findViewById(R.id.point_3_num);
        point_3_txt = (TextView) findViewById(R.id.point_3_txt);
        point_3 = (ImageView) findViewById(R.id.point_3);
        point_4_num = (TextView) findViewById(R.id.point_4_num);
        point_4_txt = (TextView) findViewById(R.id.point_4_txt);
        point_4 = (ImageView) findViewById(R.id.point_4);
        point_5 = (ImageView) findViewById(R.id.point_5);
        viewtasknumbertree_right1 = (TextView) findViewById(R.id.viewtasknumbertree_right1);
        viewtasknumbertree_right1_num = (TextView) findViewById(R.id.viewtasknumbertree_right1_num);
        point_6 = (ImageView) findViewById(R.id.point_6);
        viewtasknumbertree_right2 = (TextView) findViewById(R.id.viewtasknumbertree_right2);
        viewtasknumbertree_right2_num = (TextView) findViewById(R.id.viewtasknumbertree_right2_num);
    }

    /**
     * @param type 1   等待执行列表用
     *             2   查看状态列表用
     * @param sum  总量
     * @param n1   待分配
     * @param n2   待执行
     * @param n3   执行中
     * @param n4   审核中
     * @param n5   已通过
     * @param n6   未通过
     */
    public void setting(int type, String sum, String n1, String n2, String n3, String n4, String n5, String n6) {
        setType(type);
        settingNum(sum, n1, n2, n3, n4, n5, n6);
    }

    public void setType(int type) {
        String redColor = "#F65D57";
        String grey = "#A0A0A0";
        switch (type) {
            case 1: {
                point_1_num.setTextColor(Color.parseColor(redColor));
                point_1_txt.setTextColor(Color.parseColor(redColor));
                point_2_num.setTextColor(Color.parseColor(redColor));
                point_2_txt.setTextColor(Color.parseColor(redColor));
                point_3_num.setTextColor(Color.parseColor(redColor));
                point_3_txt.setTextColor(Color.parseColor(redColor));
                point_1.setImageResource(R.mipmap.view_tasknumbertree_2);
                point_2.setImageResource(R.mipmap.view_tasknumbertree_2);
                point_3.setImageResource(R.mipmap.view_tasknumbertree_2);
                //初始化
                point_4_num.setTextColor(Color.parseColor(grey));
                point_4_txt.setTextColor(Color.parseColor(grey));
                viewtasknumbertree_right1.setTextColor(Color.parseColor(grey));
                viewtasknumbertree_right1_num.setTextColor(Color.parseColor(grey));
                viewtasknumbertree_right2.setTextColor(Color.parseColor(grey));
                viewtasknumbertree_right2_num.setTextColor(Color.parseColor(grey));
                point_4.setImageResource(R.mipmap.view_tasknumbertree_3);
                point_5.setImageResource(R.mipmap.view_tasknumbertree_3);
                point_6.setImageResource(R.mipmap.view_tasknumbertree_3);
            }
            break;
            case 2: {
                point_4_num.setTextColor(Color.parseColor(redColor));
                point_4_txt.setTextColor(Color.parseColor(redColor));
                viewtasknumbertree_right1.setTextColor(Color.parseColor(redColor));
                viewtasknumbertree_right1_num.setTextColor(Color.parseColor(redColor));
                viewtasknumbertree_right2.setTextColor(Color.parseColor(redColor));
                viewtasknumbertree_right2_num.setTextColor(Color.parseColor(redColor));
                point_4.setImageResource(R.mipmap.view_tasknumbertree_2);
                point_5.setImageResource(R.mipmap.view_tasknumbertree_2);
                point_6.setImageResource(R.mipmap.view_tasknumbertree_2);
                //初始化
                point_1_num.setTextColor(Color.parseColor(grey));
                point_1_txt.setTextColor(Color.parseColor(grey));
                point_2_num.setTextColor(Color.parseColor(grey));
                point_2_txt.setTextColor(Color.parseColor(grey));
                point_3_num.setTextColor(Color.parseColor(grey));
                point_3_txt.setTextColor(Color.parseColor(grey));
                point_1.setImageResource(R.mipmap.view_tasknumbertree_3);
                point_2.setImageResource(R.mipmap.view_tasknumbertree_3);
                point_3.setImageResource(R.mipmap.view_tasknumbertree_3);
            }
            break;
        }
    }

    public void settingNum(String sum, String n1, String n2, String n3, String n4, String n5, String n6) {
        viewtasknumbertree_sum_num.setText(checkStr(sum));
        point_1_num.setText(checkStr(n1));
        point_2_num.setText(checkStr(n2));
        point_3_num.setText(checkStr(n3));
        point_4_num.setText(checkStr(n4));
        viewtasknumbertree_right1_num.setText(checkStr(n5));
        viewtasknumbertree_right2_num.setText(checkStr(n6));
    }

    private String checkStr(String str) {
        try {
            return Tools.StringToInt(str) == -1 ? "0" : str;
        } catch (Exception e) {
            return "0";
        }
    }
}
