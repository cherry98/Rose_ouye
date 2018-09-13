package com.orange.oy.activity.black;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.IType;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.Message1;
import com.orange.oy.info.Message2;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlackBackDropFragment extends Fragment implements View.OnClickListener {


    public void initNetworkConnection() {
        inputNote = new NetworkConnection(getActivity()) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getActivity()));
                params.put("storeid", storeid);
                params.put("taskid", taskid);
                if (type.equals("2")) {
                    params.put("questionid", questionid);
                }
                params.put("type", type);
                params.put("note", note);
                return params;
            }
        };
    }

    private String storeid, taskid, questionid, type, note;
    private ImageLoader imageLoader;

    public void setData(String storeid, String taskid, String questionid) {
        this.storeid = storeid;
        this.taskid = taskid;
        this.questionid = questionid;
    }

    private View mView;
    private ArrayList<IType> list = new ArrayList<>();
    private TextView blackbackdrop_time;
    private ListView blackbackdrop_listview;
    private MyAdapter myAdapter;
    private TextView blackbackdrop_button1, blackbackdrop_button2;
    private BlackBackListener blackBackListener;
    private LinearLayout blackbackdrop_layout;//假输入框
    private EditText blackbackdrop_edittext;//真输入框
    private TextView blackbackdrop_send;//发送按钮
    private NetworkConnection inputNote;

    public interface BlackBackListener {
        void selectContinue();

        void selectComplete();
    }

    public void setBlackBackListener(BlackBackListener blackBackListener) {
        this.blackBackListener = blackBackListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Tools.d("onCreateView");
        mView = inflater.inflate(R.layout.fragment_black_back_drop, container, false);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.d("onCreate");
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Tools.d("onActivityCreated");
        initNetworkConnection();
        imageLoader = new ImageLoader(getActivity());
        blackbackdrop_time = (TextView) mView.findViewById(R.id.blackbackdrop_time);
        blackbackdrop_button1 = (TextView) mView.findViewById(R.id.blackbackdrop_button1);
        blackbackdrop_button2 = (TextView) mView.findViewById(R.id.blackbackdrop_button2);
        blackbackdrop_listview = (ListView) mView.findViewById(R.id.blackbackdrop_listview);
        blackbackdrop_layout = (LinearLayout) mView.findViewById(R.id.blackbackdrop_layout);
        blackbackdrop_edittext = (EditText) mView.findViewById(R.id.blackbackdrop_edittext);
        blackbackdrop_send = (TextView) mView.findViewById(R.id.blackbackdrop_send);
        myAdapter = new MyAdapter();
        blackbackdrop_listview.setAdapter(myAdapter);
    }

    public void setMessage(ArrayList<IType> content2, boolean isShow) {//解屏显示界面
        blackbackdrop_time.setText(Tools.getTimeByPattern("HH:mm"));
        this.list = content2;
        if (myAdapter == null) {
            myAdapter = new MyAdapter();
            blackbackdrop_listview.setAdapter(myAdapter);
        }
        myAdapter.notifyDataSetChanged();
        if (isShow) {
            blackbackdrop_button1.setText("继续体验");
            blackbackdrop_button2.setText("体验完成");
            blackbackdrop_button1.setVisibility(View.VISIBLE);
            blackbackdrop_button2.setVisibility(View.VISIBLE);
            blackbackdrop_button1.setOnClickListener(this);
            blackbackdrop_button2.setOnClickListener(this);
            blackbackdrop_layout.setVisibility(View.VISIBLE);
            blackbackdrop_edittext.setVisibility(View.GONE);
        }
        blackbackdrop_send.setBackgroundResource(R.mipmap.fwx_3);
        blackbackdrop_send.setText("");
        blackbackdrop_send.setOnClickListener(null);
        blackbackdrop_edittext.setText("");
//        blackbackdrop_listview.setStackFromBottom(false);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        }
    }


    public void setMessage(ArrayList<IType> content1, String type) {
        blackbackdrop_time.setText(Tools.getTimeByPattern("HH:mm"));//录屏显示界面
        this.list = content1;
        this.type = type;
        if (myAdapter == null) {
            myAdapter = new MyAdapter();
            blackbackdrop_listview.setAdapter(myAdapter);
        }
        myAdapter.notifyDataSetChanged();
        blackbackdrop_button1.setText("");
        blackbackdrop_button2.setText("");
        blackbackdrop_button1.setVisibility(View.VISIBLE);
        blackbackdrop_button2.setVisibility(View.GONE);
        blackbackdrop_button1.setOnClickListener(null);
        blackbackdrop_button2.setOnClickListener(null);
        blackbackdrop_layout.setVisibility(View.GONE);
        blackbackdrop_edittext.setVisibility(View.VISIBLE);
//        blackbackdrop_listview.setStackFromBottom(false);
        blackbackdrop_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after == 0) {
                    blackbackdrop_send.setText("");
                    blackbackdrop_send.setBackgroundResource(R.mipmap.fwx_3);
                    blackbackdrop_send.setOnClickListener(null);
                    Tools.d("beforeTextChanged" + "start:" + start + "count:" + count + "after:" + after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    blackbackdrop_send.setText("");
                    blackbackdrop_send.setBackgroundResource(R.mipmap.fwx_3);
                    blackbackdrop_send.setOnClickListener(null);
                    Tools.d("onTextChanged" + "start:" + start + "before:" + before + "count:" + count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Tools.d("afterTextChanged" + s);
                if (!s.equals("") && !TextUtils.isEmpty(s)) {
                    blackbackdrop_send.setBackgroundResource(R.drawable.black_send);
                    blackbackdrop_send.setText("发送");
                    blackbackdrop_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            blackbackdrop_listview.setStackFromBottom(true);
                            blackbackdrop_listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                            String message = blackbackdrop_edittext.getText().toString();
                            note = message;
                            if (!message.trim().equals("")) {
                                Message2 message2 = new Message2();
                                message2.setContent(message);
                                list.add(message2);
                                blackbackdrop_edittext.setText("");
                                myAdapter.notifyDataSetChanged();
                                sendData();
                            }
                        }
                    });
                }
            }
        });
    }

    public void sendData() {
        inputNote.sendPostRequest(Urls.InputNote, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
//                        Tools.showToast(getActivity(), "发送成功");
                        Tools.d("发送成功");
                    } else {
                        Tools.d(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blackbackdrop_button1:
                blackBackListener.selectContinue();
                break;
            case R.id.blackbackdrop_button2:
                blackBackListener.selectComplete();
                break;
        }
    }


    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            ImageView blackchat_img = null;
            String imgUrl = AppInfo.getUserImagurl(getContext());
            int type = getItemViewType(position);
            if (convertView == null) {
                if (type == 0) {//左边
                    convertView = Tools.loadLayout(getContext(), R.layout.view_blackchat);
                    textView = (TextView) convertView.findViewById(R.id.blackchat_content);
                } else if (type == 1) {
                    convertView = Tools.loadLayout(getContext(), R.layout.view_blackchat2);
                    textView = (TextView) convertView.findViewById(R.id.blackchat_content2);
                    blackchat_img = (ImageView) convertView.findViewById(R.id.blackchat_img2);
                }
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            if (type == 0) {
                textView.setText(((Message1) list.get(position)).getContent());
            } else if (type == 1) {
                textView.setText(((Message2) list.get(position)).getContent());
                if (!TextUtils.isEmpty(imgUrl)) {
                    if (blackchat_img == null) {
                        blackchat_img = (ImageView) convertView.findViewById(R.id.blackchat_img2);
                    }
                    imageLoader.DisplayImage(imgUrl, blackchat_img, R.mipmap.grxx_icon_mrtx);
                }
            }
            blackbackdrop_listview.setSelection(blackbackdrop_listview.getCount() - 1);
            return convertView;
        }
    }

}
