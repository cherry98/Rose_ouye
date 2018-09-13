package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.alipay.OuMiDetailActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.info.NewCommentInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CircularImageView;
import com.orange.oy.view.MyListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.orange.oy.R.id.iv_pic;

/**
 * 评论详细回复
 */
public class CommentDesActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {

    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.title);
        appTitle.settingName(commentList.get(0).getActivity_name());
        appTitle.showBack(this);
    }

    private String fi_id, comment_id, file_url, user_img, user_name, create_time, comment, content;
    private ImageView iv_bigimg;
    private TextView tv_submit;
    private EditText ed_des;
    private NetworkConnection sendComment;
    private ArrayList<NewCommentInfo.CommentsBean> commentList = new ArrayList<>();
    private ImageLoader imageLoader;
    private MyListView lv_listview;
    private String Username, Userimg;
    private MyAdapter adapter;

    @Override
    protected void onStop() {
        super.onStop();
        if (sendComment != null) {
            sendComment.stop(Urls.SendComment);
        }
    }

    private void initNetworkConnection() {
        sendComment = new NetworkConnection(this) {
            @Override
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CommentDesActivity.this));
                params.put("type", "1");
                params.put("fi_id", fi_id);
                params.put("comment_id", comment_id);
                params.put("content", content);//回复内容
                return params;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_des);
        imageLoader = new ImageLoader(this);
        Username = AppInfo.getUserName(this);
        Userimg = AppInfo.getUserImagurl(this);
        Intent data = getIntent();
        commentList = (ArrayList<NewCommentInfo.CommentsBean>) data.getSerializableExtra("commentList");

        initTitle();
        initNetworkConnection();
        lv_listview = (MyListView) findViewById(R.id.lv_listview);
        iv_bigimg = (ImageView) findViewById(R.id.iv_bigimg);
        ed_des = (EditText) findViewById(R.id.ed_des);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        String url = commentList.get(0).getFile_url();
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = Urls.Endpoint3 + url;
        }
        imageLoader.setShowWH(500).DisplayImage(url, iv_bigimg, -2);
        imageLoader.setShowWH(200).DisplayImage(Urls.Endpoint3 + commentList.get(0).getFile_url(), iv_bigimg, -2);
        adapter = new MyAdapter();
        lv_listview.setAdapter(adapter);
        submit();
    }

    private void submit() {
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.isEmpty(ed_des.getText().toString()) || "".equals(ed_des.getText().toString())) {
                    Tools.showToast(CommentDesActivity.this, "请填写评论~");
                    return;
                }
                content = ed_des.getText().toString();
                fi_id = commentList.get(0).getFi_id();
                comment_id = commentList.get(0).getComment_id();
                Comment();
            }
        });
    }

    //评论提交
    private void Comment() {
        sendComment.sendPostRequest(Urls.SendComment, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getInt("code") == 200) {
                        //加一个list
                        NewCommentInfo.CommentsBean commentsBean = new NewCommentInfo.CommentsBean();
                        commentsBean.setCreate_time(Tools.getNowDate());
                        commentsBean.setUser_name(commentList.get(0).getUser_name());
                        commentsBean.setUser_img(Userimg);
                        commentsBean.setComment(content);
                        commentList.add(commentsBean);
                        adapter.notifyDataSetChanged();
                        NewCommentActivity.IsRefresh = true;
                        content = "";
                        ed_des.setText("");
                        lv_listview.setSelection(lv_listview.getBottom());
                    } else {
                        Tools.showToast(CommentDesActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CommentDesActivity.this, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CommentDesActivity.this, getResources().getString(R.string.network_volleyerror));
                CustomProgressDialog.Dissmiss();
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentList.get(position);
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
                convertView = Tools.loadLayout(CommentDesActivity.this, R.layout.item_commentdes);
                viewHolder.iv_pic = (CircularImageView) convertView.findViewById(iv_pic);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_des = (TextView) convertView.findViewById(R.id.tv_des);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            NewCommentInfo.CommentsBean commentsBean = commentList.get(position);

            viewHolder.tv_time.setText(commentsBean.getCreate_time());
            if (position == 0) {
                viewHolder.tv_name.setText(commentsBean.getUser_name());
                imageLoader.setShowWH(200).DisplayImage(Urls.ImgIp + commentsBean.getUser_img(), viewHolder.iv_pic, -2);
                viewHolder.tv_des.setText(commentsBean.getComment());
            } else {
                viewHolder.tv_name.setText(Username);
                imageLoader.setShowWH(200).DisplayImage(commentsBean.getUser_img(), viewHolder.iv_pic, -2);
                String contents = Username + "@" + commentsBean.getUser_name() + commentsBean.getComment();
                int end = contents.length() - commentsBean.getComment().length();
                SpannableStringBuilder builder = new SpannableStringBuilder(contents);
                builder.setSpan(new ForegroundColorSpan(getBaseContext().getResources().getColor(R.color.homepage_select)),
                        0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.tv_des.setText(builder);
            }
            return convertView;
        }

        class ViewHolder {
            TextView tv_name, tv_time, tv_des, tv_sure;
            CircularImageView iv_pic;
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("commentList", commentList);
        setResult(RESULT_OK, intent);
        baseFinish();
    }

    public void onBack() {
        Intent intent = new Intent();
        intent.putExtra("commentList", commentList);
        setResult(RESULT_OK, intent);
        baseFinish();
    }
}
