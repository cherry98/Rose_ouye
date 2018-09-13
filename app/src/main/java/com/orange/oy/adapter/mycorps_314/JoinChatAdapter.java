package com.orange.oy.adapter.mycorps_314;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.allinterface.DiscussCallback;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.DiscussPopDialog;
import com.orange.oy.info.mycorps.ChatInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 2018/5/11.
 */

public class JoinChatAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChatInfo> list;

    public JoinChatAdapter(Context context, ArrayList<ChatInfo> list) {
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
            convertView = Tools.loadLayout(context, R.layout.item_chat);
            viewHolder.itemchat_content = (TextView) convertView.findViewById(R.id.itemchat_content);
            viewHolder.itemchat_type = (TextView) convertView.findViewById(R.id.itemchat_type);
            viewHolder.itemchat_reply = (TextView) convertView.findViewById(R.id.itemchat_reply);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ChatInfo chatInfo = list.get(position);
        if ("1".equals(chatInfo.getType())) {//队长回复我
            viewHolder.itemchat_type.setText(Html.fromHtml(convertView.getResources().getString(R.string.join_chat1)));
        } else {
            viewHolder.itemchat_type.setText(Html.fromHtml(convertView.getResources().getString(R.string.join_chat2)));
        }
        viewHolder.itemchat_content.setText(chatInfo.getText());
        if (position == list.size() - 1) {
            viewHolder.itemchat_reply.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemchat_reply.setVisibility(View.GONE);
        }
        viewHolder.itemchat_reply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new DiscussPopDialog((Activity) context, new DiscussCallback() {
                    @Override
                    public void onDiscuss(String discuss_content) {
                        reply(chatInfo.getTeam_id(), chatInfo.getApply_id(), discuss_content);
                    }
                });
                return false;
            }
        });
        return convertView;
    }

    private NetworkConnection reply;

    private void reply(final String team_id, final String apply_id, final String text) {
        reply = new NetworkConnection(context) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", AppInfo.getName(context));
                params.put("token", Tools.getToken());
                params.put("team_id", team_id);
                params.put("apply_id", apply_id);
                params.put("text", text);
                return params;
            }
        };
        reply.sendPostRequest(Urls.Reply, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        Tools.showToast(context, "回复成功");
                        onRefreshListener.onRefresh();
                    } else {
                        Tools.showToast(context, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(context, context.getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(context, context.getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    class ViewHolder {
        private TextView itemchat_type, itemchat_content, itemchat_reply;
    }

    private OnRefreshListener onRefreshListener;

    public interface OnRefreshListener {
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }
}
