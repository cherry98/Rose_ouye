package com.orange.oy.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.info.WebpageComListInfo;
import com.orange.oy.info.WebpagetaskDBInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.WebpageCommentView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/11.
 * 体验任务评论列表
 */

public class WebpageCommentlistDialog extends LinearLayout implements View.OnClickListener {
    private static MyDialog myDialog;
    private static WebpageCommentlistDialog webpageCommentlistDialog;
    private PullToRefreshListView dwpgcl_listview;
    private NetworkConnection PageCommentList, PraisePageComment;
    private int page = 1;
    private String online_store_url = "";
    private TextView dwpgcl_il1, dwpgcl_il2, dwpgcl_il3, dwpgcl_il4, dwpgcl_il5;
    private ArrayList<WebpageComListInfo> list = new ArrayList<>();
    private ImageLoader imageLoader;
    private Myadapter myadapter;
    private View dwpgcl_layout;
    private ArrayList<WebpagetaskDBInfo> webpagetaskDBInfos;

    public void setWebpagetaskDBInfos(ArrayList<WebpagetaskDBInfo> webpagetaskDBInfos) {
        this.webpagetaskDBInfos = webpagetaskDBInfos;
    }

    public static WebpageCommentlistDialog ShowWebpageCommentlistDialog(Context context, String storeurl,
                                                                        ArrayList<WebpagetaskDBInfo> webpagetaskDBInfos) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
        }
        webpageCommentlistDialog = new WebpageCommentlistDialog(context);
        webpageCommentlistDialog.setOnline_store_url(storeurl);
        webpageCommentlistDialog.setWebpagetaskDBInfos(webpagetaskDBInfos);
        myDialog = new MyDialog((BaseActivity) context, webpageCommentlistDialog, false);
        myDialog.showAtLocation(((BaseActivity) context).findViewById(R.id.main), Gravity.BOTTOM | Gravity
                .CENTER_HORIZONTAL, 0, 0);
        webpageCommentlistDialog.getData();
        PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                webpageCommentlistDialog.stopNetWork();
                webpageCommentlistDialog = null;
                myDialog.backgroundAlpha(1f);
            }
        };
        myDialog.setOnDismissListener(onDismissListener);
        return webpageCommentlistDialog;
    }

    public void setOnline_store_url(String online_store_url) {
        this.online_store_url = online_store_url;
    }

    public WebpageCommentlistDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_webpagecommentlist);
        PageCommentList = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                params.put("online_store_url", online_store_url);
                params.put("page", page + "");
                return params;
            }
        };
        View dwpgcl_dismiss = findViewById(R.id.dwpgcl_dismiss);
        dwpgcl_listview = (PullToRefreshListView) findViewById(R.id.dwpgcl_listview);
        dwpgcl_il1 = (TextView) findViewById(R.id.dwpgcl_il1);
        dwpgcl_il2 = (TextView) findViewById(R.id.dwpgcl_il2);
        dwpgcl_il3 = (TextView) findViewById(R.id.dwpgcl_il3);
        dwpgcl_il4 = (TextView) findViewById(R.id.dwpgcl_il4);
        dwpgcl_il5 = (TextView) findViewById(R.id.dwpgcl_il5);
        dwpgcl_layout = findViewById(R.id.dwpgcl_layout);
        LayoutParams lp = (LayoutParams) dwpgcl_layout.getLayoutParams();
        lp.height = Tools.getScreeInfoHeight(context) / 3 * 2;
        dwpgcl_layout.setLayoutParams(lp);
        dwpgcl_dismiss.setOnClickListener(this);
        imageLoader = new ImageLoader(context);
        dwpgcl_listview.setMode(PullToRefreshBase.Mode.BOTH);
        dwpgcl_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                getData();
            }

            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
            }
        });
        page = 1;
    }

    public void stopNetWork() {
        if (PageCommentList != null) {
            PageCommentList.stop(Urls.PageCommentList);
        }
    }

    private void getData() {
        PageCommentList.sendPostRequest(Urls.PageCommentList, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.getInt("code")) {
                        jsonObject = jsonObject.optJSONObject("data");
                        if (jsonObject != null) {
                            dwpgcl_il1.setText(jsonObject.optString("one_num"));
                            dwpgcl_il2.setText(jsonObject.optString("two_num"));
                            dwpgcl_il3.setText(jsonObject.optString("three_num"));
                            dwpgcl_il4.setText(jsonObject.optString("four_num"));
                            dwpgcl_il5.setText(jsonObject.optString("five_num"));
                            if (page == 1) {
                                list.clear();
                                if (myadapter == null) {
                                    myadapter = new Myadapter();
                                    dwpgcl_listview.setAdapter(myadapter);
                                } else {
                                    myadapter.notifyDataSetChanged();
                                }
                                if (webpagetaskDBInfos != null) {
                                    String imgurl = AppInfo.getUserImagurl(getContext());
                                    String name = AppInfo.getUserName(getContext());
                                    int one_num = 0, two_num = 0, three_num = 0, four_num = 0, five_num = 0;
                                    for (WebpagetaskDBInfo webpagetaskDBInfo : webpagetaskDBInfos) {
                                        if (TextUtils.isEmpty(webpagetaskDBInfo.getCommentState())) {
                                            continue;
                                        }
                                        WebpageComListInfo webpageComListInfo = new WebpageComListInfo();
                                        webpageComListInfo.setContent(webpagetaskDBInfo.getCommentTxt());
                                        webpageComListInfo.setCreate_time(webpagetaskDBInfo.getCreatetime());
                                        webpageComListInfo.setIs_praise(webpagetaskDBInfo.getIspraise());
                                        webpageComListInfo.setLocalpath(webpagetaskDBInfo.getPath());
                                        if ("1".equals(webpagetaskDBInfo.getIspraise())) {
                                            webpageComListInfo.setPraise_num("1");
                                        } else {
                                            webpageComListInfo.setPraise_num("0");
                                        }
                                        webpageComListInfo.setUser_img(imgurl);
                                        webpageComListInfo.setUser_name(name);
                                        list.add(webpageComListInfo);
                                        switch (webpagetaskDBInfo.getCommentState()) {
                                            case "1": {
                                                one_num++;
                                            }
                                            break;
                                            case "2": {
                                                two_num++;
                                            }
                                            break;
                                            case "3": {
                                                three_num++;
                                            }
                                            break;
                                            case "4": {
                                                four_num++;
                                            }
                                            break;
                                            case "5": {
                                                five_num++;
                                            }
                                            break;
                                        }
                                    }
                                    dwpgcl_il1.setText((Tools.StringToIntFrozero(dwpgcl_il1.getText().toString()) + one_num) + "");
                                    dwpgcl_il2.setText((Tools.StringToIntFrozero(dwpgcl_il2.getText().toString()) + two_num) + "");
                                    dwpgcl_il3.setText((Tools.StringToIntFrozero(dwpgcl_il3.getText().toString()) + three_num) + "");
                                    dwpgcl_il4.setText((Tools.StringToIntFrozero(dwpgcl_il4.getText().toString()) + four_num) + "");
                                    dwpgcl_il5.setText((Tools.StringToIntFrozero(dwpgcl_il5.getText().toString()) + five_num) + "");
                                }
                            }
                            JSONArray comment_list = jsonObject.optJSONArray("comment_list");
                            int length = comment_list.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonObject1 = comment_list.getJSONObject(i);
                                WebpageComListInfo webpageComListInfo = new WebpageComListInfo();
                                webpageComListInfo.setComment_id(jsonObject1.optString("comment_id"));
                                webpageComListInfo.setContent(jsonObject1.optString("content"));
                                if ("null".equals(webpageComListInfo.getContent())) {
                                    webpageComListInfo.setContent("");
                                }
                                webpageComListInfo.setCreate_time(jsonObject1.optString("create_time"));
                                webpageComListInfo.setIs_praise(jsonObject1.optString("is_praise"));
                                webpageComListInfo.setPraise_num(jsonObject1.optString("praise_num"));
                                webpageComListInfo.setUser_img(jsonObject1.optString("user_img"));
                                webpageComListInfo.setUser_name(jsonObject1.optString("user_name"));
                                list.add(webpageComListInfo);
                            }
                            dwpgcl_listview.onRefreshComplete();
                            if (length < 15) {
                                dwpgcl_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            } else {
                                dwpgcl_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            if (myadapter == null) {
                                myadapter = new Myadapter();
                                dwpgcl_listview.setAdapter(myadapter);
                            }
                            myadapter.notifyDataSetChanged();
                        }
                    } else {
                        Tools.showToast(getContext(), jsonObject.optString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                dwpgcl_listview.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                dwpgcl_listview.onRefreshComplete();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dwpgcl_dismiss: {
                if (myDialog != null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
            break;
        }
    }

    public interface OnWebpageComDialogunOnlinePraiseListener {
        void clickPraise(WebpageComListInfo webpageComListInfo);
    }

    private OnWebpageComDialogunOnlinePraiseListener onWebpageComDialogunOnlinePraiseListener;

    public void setOnWebpageComDialogunOnlinePraiseListener(OnWebpageComDialogunOnlinePraiseListener onWebpageComDialogunOnlinePraiseListener) {
        this.onWebpageComDialogunOnlinePraiseListener = onWebpageComDialogunOnlinePraiseListener;
    }

    private OnClickListener zClickListener = new OnClickListener() {
        public void onClick(View v) {
            final int position = (Integer) v.getTag();
            WebpageComListInfo webpageComListInfo = list.get(position);
            String praise;
            if ("1".equals(webpageComListInfo.getIs_praise())) {
                praise = "0";
            } else {
                praise = "1";
            }
            if (TextUtils.isEmpty(webpageComListInfo.getComment_id())) {
                if ("1".equals(webpageComListInfo.getIs_praise())) {
                    int num = Tools.StringToIntFrozero(webpageComListInfo.getPraise_num());
                    webpageComListInfo.setPraise_num((num - 1) + "");
                    webpageComListInfo.setIs_praise("0");
                } else {
                    int num = Tools.StringToIntFrozero(webpageComListInfo.getPraise_num());
                    webpageComListInfo.setPraise_num((num + 1) + "");
                    webpageComListInfo.setIs_praise("1");
                }
                if (onWebpageComDialogunOnlinePraiseListener != null) {
                    onWebpageComDialogunOnlinePraiseListener.clickPraise(webpageComListInfo);
                }
                if (myadapter != null)
                    myadapter.notifyDataSetChanged();
                return;
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("token", Tools.getToken());
            params.put("usermobile", AppInfo.getName(getContext()));
            params.put("comment_id", webpageComListInfo.getComment_id());
            params.put("praise", praise);
            if (PraisePageComment == null) {
                PraisePageComment = new NetworkConnection(getContext()) {
                    public Map<String, String> getNetworkParams() {
                        return null;
                    }
                };
            }
            PraisePageComment.setMapParams(params);
            PraisePageComment.sendPostRequest(Urls.PraisePageComment, new Response.Listener<String>() {
                public void onResponse(String s) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(s);
                        if (200 == jsonObject.getInt("code") && list != null && list.size() > position) {
                            WebpageComListInfo webpageComListInfo = list.get(position);
                            if ("1".equals(webpageComListInfo.getIs_praise())) {
                                int num = Tools.StringToInt(webpageComListInfo.getPraise_num());
                                webpageComListInfo.setPraise_num((num - 1) + "");
                                webpageComListInfo.setIs_praise("0");
                            } else {
                                int num = Tools.StringToInt(webpageComListInfo.getPraise_num());
                                webpageComListInfo.setPraise_num((num + 1) + "");
                                webpageComListInfo.setIs_praise("1");
                            }
                            if (myadapter != null)
                                myadapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError volleyError) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
                }
            });
        }
    };

    private class Myadapter extends BaseAdapter {
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
                convertView = Tools.loadLayout(getContext(), R.layout.item_webpagecomlistdialog);
                viewHolder.itemwpcld_content = (TextView) convertView.findViewById(R.id.itemwpcld_content);
                viewHolder.itemwpcld_img = (CircularImageView) convertView.findViewById(R.id.itemwpcld_img);
                viewHolder.itemwpcld_name = (TextView) convertView.findViewById(R.id.itemwpcld_name);
                viewHolder.itemwpcld_time = (TextView) convertView.findViewById(R.id.itemwpcld_time);
                viewHolder.itemwpcld_z = (ImageView) convertView.findViewById(R.id.itemwpcld_z);//是否点过赞，1为点过，0为没点过
                viewHolder.itemwpcld_znum = (TextView) convertView.findViewById(R.id.itemwpcld_znum);
                viewHolder.itemwpcld_zlayout = convertView.findViewById(R.id.itemwpcld_zlayout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            WebpageComListInfo webpageComListInfo = list.get(position);
            viewHolder.itemwpcld_zlayout.setTag(position);
            String url = webpageComListInfo.getUser_img();
            if (!url.startsWith("http")) {
                url = Urls.ImgIp + url;
            }
            imageLoader.setShowWH(500).DisplayImage(url, viewHolder.itemwpcld_img);
            viewHolder.itemwpcld_content.setText(webpageComListInfo.getContent());
            viewHolder.itemwpcld_name.setText(webpageComListInfo.getUser_name());
            viewHolder.itemwpcld_time.setText(webpageComListInfo.getCreate_time());
            viewHolder.itemwpcld_znum.setText(webpageComListInfo.getPraise_num());
            if ("1".equals(webpageComListInfo.getIs_praise())) {
                viewHolder.itemwpcld_znum.setTextColor(0xFFFF5F5F);
                viewHolder.itemwpcld_z.setImageResource(R.mipmap.webpagecomlist_z2);
            } else {
                viewHolder.itemwpcld_znum.setTextColor(0xFFA0A0A0);
                viewHolder.itemwpcld_z.setImageResource(R.mipmap.webpagecomlist_z1);
            }
            viewHolder.itemwpcld_zlayout.setOnClickListener(zClickListener);
            return convertView;
        }

        class ViewHolder {
            CircularImageView itemwpcld_img;
            TextView itemwpcld_name, itemwpcld_time, itemwpcld_content, itemwpcld_znum;
            ImageView itemwpcld_z;
            View itemwpcld_zlayout;
        }
    }

}
