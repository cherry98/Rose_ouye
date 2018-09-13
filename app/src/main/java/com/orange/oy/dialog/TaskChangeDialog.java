package com.orange.oy.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orange.oy.R;
import com.orange.oy.adapter.MyTeamAdapter;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.util.CharacterParser;
import com.orange.oy.util.PinyinComparatorForMyteam;

import java.util.ArrayList;

/**
 * 更换访员弹出
 */
public class TaskChangeDialog extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener {
    //    private EditText taskchange_search;
    private ListView taskchange_listview;
    private MyTeamAdapter adapter;
    private ArrayList<MyteamNewfdInfo> list;
    private ArrayList<MyteamNewfdInfo> mainList;
    private CharacterParser characterParser;
    private PinyinComparatorForMyteam pinyinComparatorForMyteam;
    private boolean isSearch;
    private OnItemClickListener onItemClickListener;
//    private static Dialog dialog;

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(mainList.get(position));
        }
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
    }

    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.oneself();
        }
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MyteamNewfdInfo myteamNewfdInfo);

        void oneself();
    }

    public TaskChangeDialog(Context context, OnItemClickListener listener) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_taskchange);
        onItemClickListener = listener;
        characterParser = CharacterParser.getInstance();
        pinyinComparatorForMyteam = new PinyinComparatorForMyteam();
//        taskchange_search = (EditText) findViewById(R.id.taskchange_search);
        taskchange_listview = (ListView) findViewById(R.id.taskchange_listview);
        taskchange_listview.setOnItemClickListener(this);
        findViewById(R.id.taskchange_oneself).setOnClickListener(this);
        findViewById(R.id.taskchange_exit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        //根据输入框输入值的改变来过滤搜索
//        taskchange_search.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
//                filterData(s.toString());
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void afterTextChanged(Editable s) {
//            }
//        });
    }

    protected void setList(ArrayList<MyteamNewfdInfo> list) {
        this.list = list;
        mainList = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            mainList.add(list.get(i));
        }
        adapter = new MyTeamAdapter(getContext(), mainList);
        taskchange_listview.setAdapter(adapter);
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if (adapter == null) return;
        if (TextUtils.isEmpty(filterStr)) {
            if (mainList == null) {
                mainList = new ArrayList<>();
            } else {
                mainList.clear();
            }
            int size = list.size();
            for (int i = 0; i < size; i++) {
                mainList.add(list.get(i));
            }
            isSearch = false;
        } else {
            isSearch = true;
            mainList.clear();
            int size = list.size();
            MyteamNewfdInfo sortModel;
            for (int i = 0; i < size; i++) {
                sortModel = list.get(i);
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr
                        .toString())) {
                    mainList.add(sortModel);
                }
            }
        }
        adapter.updateListView(isSearch, mainList);
    }

    private static MyDialog myDialog;

    public static MyDialog showDialog(Context context, ArrayList<MyteamNewfdInfo> list, OnItemClickListener
            listener) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        TaskChangeDialog view = new TaskChangeDialog(context, listener);
        if (list != null) {
            view.setList(list);
        }
        myDialog = new MyDialog((BaseActivity) context, view, true);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        return myDialog;
    }

    public static boolean isOpen() {
        return myDialog != null && myDialog.isShowing();
    }
//    public static Dialog showDialog(Context context, ArrayList<MyteamNewfdInfo> list, OnItemClickListener
//            listener) {
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//        TaskChangeDialog view = new TaskChangeDialog(context, listener);
//        if (list != null) {
//            view.setList(list);
//        }
//        dialog = new Dialog(context);
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = dialog.getWindow();
//        window.getDecorView().setPadding(0, 0, 0, 0);
//        window.setGravity(Gravity.BOTTOM);
//        window.setWindowAnimations(R.style.selecterStyle);
//        dialog.show();
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        dialog.addContentView(view, params);
//        int[] screes = Tools.getScreeInfo(context);
//        dialog.getWindow().setLayout(screes[0],
//                (int) context.getResources().getDimension(R.dimen.dialog_selecter_height));
//        return dialog;
//    }
}
