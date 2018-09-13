package com.orange.oy.activity.shakephoto_318;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static com.orange.oy.R.id.edt_name;
import static com.orange.oy.R.id.edt_num;
import static com.orange.oy.R.id.lin_Nodata;
import static com.umeng.analytics.a.o;

/**
 * 设置大奖页面
 */
public class SetPrizeActivity extends BaseActivity implements AppTitle.OnBackClickForAppTitle {
    private void initTitle() {
        AppTitle appTitle = (AppTitle) findViewById(R.id.setprize_title);
        appTitle.settingName("设置大奖");
        appTitle.showBack(this);
    }

    private ListView listView;
    private ArrayList<SetPrizeInfo> list;
    private MyAdapter myAdapter;
    private int position;
    private boolean isHaveList = false;
    private String prize_info;
    private ImageLoader imageLoader;
    private String Echoprize_info; //回显的数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_prize);
        listView = (ListView) findViewById(R.id.mylistview);
        imageLoader = new ImageLoader(this);
        initTitle();
        list = new ArrayList<>();
        Echoprize_info = getIntent().getStringExtra("prize_info");
        if (!Tools.isEmpty(Echoprize_info)) {
            try {
                JSONObject jsonObject = new JSONObject(Echoprize_info);
                JSONArray jsonArray = jsonObject.getJSONArray("prize_info");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SetPrizeInfo setPrizeInfo = new SetPrizeInfo();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        setPrizeInfo.setPrize_image_url(jsonObject1.getString("prize_image_url"));
                        setPrizeInfo.setPrize_name(jsonObject1.getString("prize_name"));
                        setPrizeInfo.setPrize_type(jsonObject1.getInt("prize_type"));
                        setPrizeInfo.setPrize_num(jsonObject1.getString("prize_num"));
                        if (!TextUtils.isEmpty(setPrizeInfo.getPrize_image_url())) {
                            setPrizeInfo.setUpUrl(setPrizeInfo.getPrize_image_url());
                        }
                        list.add(setPrizeInfo);
                    }
                    if (list.size() == 1) {
                        list.add(new SetPrizeInfo());
                        list.add(new SetPrizeInfo());
                    }
                    if (list.size() == 2) {
                        list.add(new SetPrizeInfo());
                    }
                }
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            list.add(new SetPrizeInfo());
            list.add(new SetPrizeInfo());
            list.add(new SetPrizeInfo());
        }

        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);

        findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   for (int i = 0; i < list.size(); i++) {
                    SetPrizeInfo setPrizeInfo = list.get(i);
                    if (Tools.isEmpty(setPrizeInfo.getPrize_name())) {
                        Tools.showToast(SetPrizeActivity.this, "请输入第" + (i + 1) + "个奖励内容");
                        return;
                    }
                    if (Tools.isEmpty(setPrizeInfo.getPrize_num())) {
                        Tools.showToast(SetPrizeActivity.this, "请输入第" + (i + 1) + "个奖励数量");
                        return;
                    }
                    if (Tools.StringToInt(setPrizeInfo.getPrize_num()) <= 0) {
                        Tools.showToast(SetPrizeActivity.this, "第" + (i + 1) + "个奖励数量要大于0");
                        return;
                    }
                    if (Tools.isEmpty(setPrizeInfo.getUpUrl())) {
                        Tools.showToast(SetPrizeActivity.this, "请上传第" + (i + 1) + "个奖励图片");
                        return;
                    }
                }*/


                if (Tools.isEmpty(list.get(0).getPrize_name())) {
                    Tools.showToast(SetPrizeActivity.this, "请填写第一个奖励内容");
                    return;
                }
                if (!Tools.isEmpty(list.get(1).getPrize_name())) {
                    if (Tools.isEmpty(list.get(0).getPrize_name())) {
                        Tools.showToast(SetPrizeActivity.this, "请填写第一个奖励内容");
                        return;
                    }
                }
                if (!Tools.isEmpty(list.get(2).getPrize_name())) {
                    if (Tools.isEmpty(list.get(0).getPrize_name()) || Tools.isEmpty(list.get(1).getPrize_name())) {
                        Tools.showToast(SetPrizeActivity.this, "请填写奖励内容");
                        return;
                    }
                }


                if (Tools.isEmpty(list.get(0).getPrize_num())) {
                    Tools.showToast(SetPrizeActivity.this, "请填写第一个奖励数量");
                    return;
                }
                if (!Tools.isEmpty(list.get(1).getPrize_num())) {
                    if (Tools.isEmpty(list.get(0).getPrize_num())) {
                        Tools.showToast(SetPrizeActivity.this, "请填写第一个奖励数量");
                        return;
                    }
                }
                if (!Tools.isEmpty(list.get(2).getPrize_num())) {
                    if (Tools.isEmpty(list.get(0).getPrize_num()) || Tools.isEmpty(list.get(1).getPrize_num())) {
                        Tools.showToast(SetPrizeActivity.this, "请填写奖励数量");
                        return;
                    }
                }


                if (Tools.isEmpty(list.get(0).getUpUrl())) {
                    Tools.showToast(SetPrizeActivity.this, "请上传第一个奖励图片");
                    return;
                }
                if (!Tools.isEmpty(list.get(1).getUpUrl())) {
                    if (Tools.isEmpty(list.get(0).getUpUrl())) {
                        Tools.showToast(SetPrizeActivity.this, "请上传第一个奖励图片");
                        return;
                    }
                }
                if (!Tools.isEmpty(list.get(2).getUpUrl())) {
                    if (Tools.isEmpty(list.get(0).getUpUrl()) || Tools.isEmpty(list.get(1).getUpUrl())) {
                        Tools.showToast(SetPrizeActivity.this, "请上传奖励图片");
                        return;
                    }
                }

                if (!Tools.isEmpty(list.get(0).getPrize_num())) {
                    if (Tools.StringToInt(list.get(0).getPrize_num()) <= 0) {
                        Tools.showToast(SetPrizeActivity.this, "第1个奖励数量要大于0");
                        return;
                    }
                }
                if (!Tools.isEmpty(list.get(1).getPrize_num())) {
                    if (Tools.StringToInt(list.get(1).getPrize_num()) <= 0) {
                        Tools.showToast(SetPrizeActivity.this, "第2个奖励数量要大于0");
                        return;
                    }
                }
                if (!Tools.isEmpty(list.get(2).getPrize_num())) {
                    if (Tools.StringToInt(list.get(2).getPrize_num()) <= 0) {
                        Tools.showToast(SetPrizeActivity.this, "第3个奖励数量要大于0");
                        return;
                    }
                }

                //  取值转化为json
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                Iterator<SetPrizeInfo> it = list.iterator();
                while (it.hasNext()) {
                    SetPrizeInfo setPrizeInfo = it.next();
                    if (Tools.isEmpty(setPrizeInfo.getUpUrl())) {
                        it.remove();
                    }
                }

                try {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        SetPrizeInfo setPrizeInfo = list.get(i);
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("prize_type", setPrizeInfo.getPrize_type());
                        jsonObject1.put("prize_name", setPrizeInfo.getPrize_name());
                        jsonObject1.put("prize_num", setPrizeInfo.getPrize_num());
                        jsonObject1.put("prize_image_url", setPrizeInfo.getPrize_image_url());
                        jsonArray.put(jsonObject1);
                    }
                    jsonObject.put("prize_info", jsonArray);
                    prize_info = jsonObject.toString();
                } catch (JSONException e) {
                    Tools.showToast(SetPrizeActivity.this, "设置奖励失败");
                }
                Intent intent = new Intent();
                intent.putExtra("prize_info", prize_info);
                setResult(AppInfo.REQUEST_CODE_COLLECT, intent);
                baseFinish();
            }
        });
    }

    private int timetip = 0;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0: {//裁剪
                    if (data != null) {
                        timetip = Tools.getTimeSS();
                        Uri uri = data.getData();
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(uri, "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 3);
                        intent.putExtra("aspectY", 2);
                        intent.putExtra("outputX", 600);
                        intent.putExtra("outputY", 400);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(this)
                                .getPath() + "/" + "spImg.jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, 1);
                    }
                }
                break;
                case 1: {
                    String path = new File(FileCache.getDirForPhoto(this).getPath() + "/" + "spImg.jpg").getPath();
                    SetPrizeInfo setPrizeInfo = list.get(position);
                    setPrizeInfo.setPrize_image_url(path);
                    sendOSSData(path);
                }
                break;
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return 3;
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = Tools.loadLayout(SetPrizeActivity.this, R.layout.item_set_prize);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                viewHolder.edt_name = (EditText) convertView.findViewById(edt_name);
                viewHolder.edt_num = (EditText) convertView.findViewById(edt_num);
                viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.iv_pic.getLayoutParams();
                lp.height = Tools.getScreeInfoWidth(SetPrizeActivity.this) * 2 / 3;
                viewHolder.iv_pic.setLayoutParams(lp);
                viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);  //删除图片
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                viewHolder.iv_pic.setImageResource(R.mipmap.szdj_tu_scsdj1);
                viewHolder.tv_num.setText("一等奖数量");
                viewHolder.tv_name.setText("一等奖奖项");
            } else if (position == 1) {
                viewHolder.iv_pic.setImageResource(R.mipmap.szdj_tu_scsdj2);
                viewHolder.tv_num.setText("二等奖数量");
                viewHolder.tv_name.setText("二等奖奖项");
            } else if (position == 2) {
                viewHolder.tv_num.setText("三等奖数量");
                viewHolder.tv_name.setText("三等奖奖项");
                viewHolder.iv_pic.setImageResource(R.mipmap.szdj_tu_scsdj);
            }

            //===============数据回显================
            if (!Tools.isEmpty(Echoprize_info)) {
                SetPrizeInfo setPrizeInfo = list.get(position);
                String url = setPrizeInfo.getPrize_image_url();
                if (!Tools.isEmpty(setPrizeInfo.getPrize_image_url())) {
                    if (setPrizeInfo.getPrize_image_url().startsWith("http")) {
                        imageLoader.setShowWH(300).DisplayImage(url, viewHolder.iv_pic, -2);
                    } else {
                        imageLoader.setShowWH(300).DisplayImage(Urls.Endpoint3 + url, viewHolder.iv_pic, -2);
                    }
                    viewHolder.iv_delete.setVisibility(View.VISIBLE);
                }
                viewHolder.edt_name.setText(setPrizeInfo.getPrize_name());
                viewHolder.edt_num.setText(setPrizeInfo.getPrize_num());
            }

            //================================
            if (isHaveList) {
                SetPrizeInfo setPrizeInfo = list.get(position);
                String url = setPrizeInfo.getPrize_image_url();
                if (!Tools.isEmpty(setPrizeInfo.getPrize_image_url())) {
                    if (setPrizeInfo.getPrize_image_url().startsWith("http")) {
                        imageLoader.setShowWH(300).DisplayImage(url, viewHolder.iv_pic, -2);
                    } else {
                        imageLoader.setShowWH(300).DisplayImage(Urls.Endpoint3 + url, viewHolder.iv_pic, -2);
                    }
                    viewHolder.iv_delete.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iv_delete.setVisibility(View.GONE);
                    if (position == 0) {
                        viewHolder.iv_pic.setImageResource(R.mipmap.szdj_tu_scsdj1);
                    } else if (position == 1) {
                        viewHolder.iv_pic.setImageResource(R.mipmap.szdj_tu_scsdj2);
                    } else if (position == 2) {
                        viewHolder.iv_pic.setImageResource(R.mipmap.szdj_tu_scsdj);
                    }
                }
            }

            viewHolder.iv_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    //本地相册选择图片
//                    //从相册选取
                    SetPrizeActivity.this.position = position;
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 0);
                }
            });

            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != list.get(position).getUpUrl()) {
                        list.get(position).setChange(false);
                        list.get(position).setPrize_image_url("");
                        list.get(position).setUpUrl("");
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });
            viewHolder.edt_name.setTag(position);//存tag值
            viewHolder.edt_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null) {
                        Tools.d(position + "-" + s.toString());
                        list.get(position).setPrize_name(s.toString());
                    }
                }
            });

            viewHolder.edt_num.setTag(position);//存tag值
            viewHolder.edt_num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null) {
                        Tools.d(position + "-" + s.toString());
                        list.get(position).setPrize_num(s.toString());
                        list.get(position).setPrize_type(position + 1);
                    }
                }
            });

            // imageLoader.DisplayImage(Urls.ImgIp + seconderDesInfo.getPrize_image_url(), viewHolder.iv_pic);
            return convertView;
        }


        private class ViewHolder {
            TextView tv_name, tv_num;
            EditText edt_name, edt_num;
            ImageView iv_pic, iv_delete;

        }
    }


    class SetPrizeInfo {
        /**
         * prize_type : 2
         * prize_name : 奖项内容
         * prize_num : 奖项数量
         * prize_image_url : 图片地址url
         */

        private int prize_type;
        private String prize_name;
        private String prize_num;
        private String prize_image_url;
        private boolean isChange;  //是否上传
        private String upUrl;  //上传之后的url

        public String getUpUrl() {
            return upUrl;
        }

        public void setUpUrl(String upUrl) {
            this.upUrl = upUrl;
        }

        public boolean isChange() {
            return isChange;
        }

        public void setChange(boolean change) {
            isChange = change;
        }

        public int getPrize_type() {
            return prize_type;
        }

        public void setPrize_type(int prize_type) {
            this.prize_type = prize_type;
        }

        public String getPrize_name() {
            return prize_name;
        }

        public void setPrize_name(String prize_name) {
            this.prize_name = prize_name;
        }

        public String getPrize_num() {
            return prize_num;
        }

        public void setPrize_num(String prize_num) {
            this.prize_num = prize_num;
        }

        public String getPrize_image_url() {
            return prize_image_url;
        }

        public void setPrize_image_url(String prize_image_url) {
            this.prize_image_url = prize_image_url;
        }

    }

    @Override
    public void onBack() {
        baseFinish();
    }


    /**
     * OSS上传图片
     */
    private static final String bucketName = "ouye";
    private OSSAsyncTask task;
    private OSSCredentialProvider credentialProvider;
    private OSS oss;
    private boolean isUpdata = false;
    private String photourl;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    //继续上传
                    isHaveList = true;
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        SetPrizeInfo setPrizeInfo = list.get(i);
                        if (!setPrizeInfo.isChange() && !TextUtils.isEmpty(list.get(i).getPrize_image_url())) {
                            sendOSSData(list.get(i).getPrize_image_url());
                            break;
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }
                break;
                case 2: {//上传完成
                    //  Tools.showToast(SetPrizeActivity.this, "上传完成");
                    myAdapter.notifyDataSetChanged();
                    CustomProgressDialog.Dissmiss();
                    if (task != null) {
                        task.cancel();
                    }
                }
                break;
            }
            Tools.d("handler");
        }
    };

    public void sendOSSData(String s) {
        try {
            CustomProgressDialog.showProgressDialog(this, "正在上传");
            Tools.d(s);
            isUpdata = true;
            File file = new File(s);
            String objectKey = file.getName();
            objectKey = Tools.getTimeSS() + "_" + objectKey;
            objectKey = Urls.EndpointDir + "/" + objectKey;
            // String photourls = "http://ouye.oss-cn-hangzhou.aliyuncs.com/";

            // photourl = photourls + objectKey;
            photourl = objectKey;

            Tools.d("图片地址：" + photourl);
            if (credentialProvider == null)
                credentialProvider = new OSSPlainTextAKSKCredentialProvider("YiMYmCHzNNO8k2l8",
                        "oPDfExOB5wAgmT0LHRA35Ts1tGzfjH");
            if (oss == null)
                oss = new OSSClient(getApplicationContext(), Urls.Endpoint, credentialProvider);
            // 构造上传请求
            final PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, s);
            // 异步上传时可以设置进度回调
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                Log.i("-------", "图片上传中-----");
                    Tools.d("currentSize: " + currentSize + " totalSize: " + totalSize);
                }
            });
            task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Tools.d(result.getStatusCode() + "");
                    SetPrizeInfo setPrizeInfo = list.get(position);
                    new File(setPrizeInfo.getPrize_image_url()).delete();
                    setPrizeInfo.setPrize_image_url(photourl);
                    setPrizeInfo.setUpUrl(photourl);
                    setPrizeInfo.setChange(true);
                    if (null != list.get(0).getUpUrl() && null != list.get(1).getUpUrl() && null != list.get(2).getUpUrl()) {
                        //上传完成
                        handler.sendEmptyMessage(2);
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                    CustomProgressDialog.Dissmiss();
                }

                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    Tools.d("onFailure");
                    CustomProgressDialog.Dissmiss();
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        serviceException.printStackTrace();
                        Tools.d("-------图片上传失败");
                    }
                    task.cancel();
                    if (list != null) {
                        Tools.showToast(SetPrizeActivity.this, "上传失败");
                        SetPrizeInfo setPrizeInfo = list.get(position);
                        setPrizeInfo.setPrize_image_url(null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CustomProgressDialog.Dissmiss();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(1);
            handler.removeMessages(2);
        }
        if (list != null) {
            list.clear();
        }
        if (task != null) {
            task.cancel();
        }
    }

}
