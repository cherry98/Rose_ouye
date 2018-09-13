package com.orange.oy.activity.createtask_321;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.db.SystemDBHelper;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.WebpageCommentlistDialog;
import com.orange.oy.info.WebpageComListInfo;
import com.orange.oy.info.WebpagetaskDBInfo;
import com.orange.oy.util.FileCache;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.MyWebView;
import com.orange.oy.view.WebpageCommentView;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/8/31.
 * 网上体验任务
 */

public class WebpageTaskActivity extends BaseActivity {
    private MyWebView tsmtt_webview;
    private AppTitle tsmtt_title;
    private WebpageCommentView webpagecommentview;
    private SystemDBHelper systemDBHelper;
    private WebpageCommentView.OnWebpageCommentViewListener onWebpageCommentViewListener = new WebpageCommentView.OnWebpageCommentViewListener() {
        public void submit(int state, String comment) {//提交
            WebpagetaskDBInfo webpagetaskDBInfo = new WebpagetaskDBInfo();
            webpagetaskDBInfo.setCommentTxt(comment);
            webpagetaskDBInfo.setCommentState(state + "");
            webpagetaskDBInfo.setPath(webpagecommentview.getPath());
            systemDBHelper.upWebpagephotoForComment(project_id, store_id, task_id, task_bath, AppInfo.getName(WebpageTaskActivity.this)
                    , webpagetaskDBInfo);
//            systemDBHelper.getWebpagephoto(project_id, store_id, task_id, task_bath, AppInfo.getName(WebpageTaskActivity.this));
            WebpageCommentlistDialog webpageCommentlistDialog =
                    WebpageCommentlistDialog.ShowWebpageCommentlistDialog(WebpageTaskActivity.this, nowUrl,
                            systemDBHelper.getWebpagephoto(project_id, store_id, task_id, task_bath, AppInfo.getName(WebpageTaskActivity.this)));
            webpageCommentlistDialog.setOnWebpageComDialogunOnlinePraiseListener(
                    new WebpageCommentlistDialog.OnWebpageComDialogunOnlinePraiseListener() {
                        public void clickPraise(WebpageComListInfo webpageComListInfo) {
                            WebpagetaskDBInfo webpagetaskDBInfo1 = new WebpagetaskDBInfo();
                            webpagetaskDBInfo1.setIspraise(webpageComListInfo.getIs_praise());
                            webpagetaskDBInfo1.setPath(webpageComListInfo.getLocalpath());
                            systemDBHelper.upWebpagephotoForPraise(project_id, store_id, task_id, task_bath,
                                    AppInfo.getName(WebpageTaskActivity.this), webpagetaskDBInfo1);
                        }
                    }
            );
        }

        public void screenshot() {//截图
            CustomProgressDialog.showProgressDialog(WebpageTaskActivity.this, "");
            Bitmap bmp = Bitmap.createBitmap(tsmtt_webview.getWidth(), tsmtt_webview.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bmp);
            tsmtt_webview.draw(canvas);
//
//            View view = getWindow().getDecorView();
//            view.setDrawingCacheEnabled(true);
//            view.buildDrawingCache();
//            Bitmap bmp = view.getDrawingCache();

//            Bitmap bmp = Bitmap.createBitmap(b1, (int) tsmtt_webview.getX(), (int) tsmtt_webview.getY(), tsmtt_webview.getWidth()
//                    , tsmtt_webview.getHeight());
//            if (bmp != b1 && b1 != null && !b1.isRecycled()) {
//                b1.recycle();
//            }

//            Bitmap bmp = Bitmap.createBitmap(tsmtt_webview.getWidth(), tsmtt_webview.getHeight(), Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(bmp);
//            tsmtt_webview.getX5WebViewExtension().snapshotWholePage(canvas, false, false);
            //
            try {
                String path = FileCache.getDirForPhoto(WebpageTaskActivity.this).getPath() + "/" +
                        Tools.getTimeSS() + "" + task_id + ".jpg";
//                String path = Environment.getExternalStorageDirectory().getPath() + "/test.jpg";
                saveBitmap(bmp, path);
                WebpagetaskDBInfo webpagetaskDBInfo = new WebpagetaskDBInfo();
                webpagetaskDBInfo.setPath(path);
                webpagetaskDBInfo.setWebUrl(nowUrl);
                webpagetaskDBInfo.setWebName(nowTitle);
                webpagetaskDBInfo.setCreatetime(Tools.getTimeByPattern("yyyy-MM-dd HH:mm:ss"));
                webpagetaskDBInfo.setIspraise("0");
                systemDBHelper.insertWebpagephoto(project_id, store_id, task_id, task_bath,
                        AppInfo.getName(WebpageTaskActivity.this), webpagetaskDBInfo);
                webpagecommentview.setPath(path);
                tsmtt_title.settingExit("查看截图" + systemDBHelper.getWebpagephotoCount(project_id, store_id,
                        task_id, task_bath, AppInfo.getName(WebpageTaskActivity.this)), onExitClickForAppTitle);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            }
            myHandler.sendEmptyMessageDelayed(1, 100);
//            Message msg = Message.obtain();
//            msg.what = 2;
//            msg.obj = bmp;
//            myHandler.sendMessageDelayed(msg, 5000);
        }
    };

    private AppTitle.OnExitClickForAppTitle onExitClickForAppTitle = new AppTitle.OnExitClickForAppTitle() {
        public void onExit() {
            Intent intent = new Intent(WebpageTaskActivity.this, ScreenshotActivity.class);
            intent.putExtra("task_id", task_id);
            intent.putExtra("storeid", store_id);
            intent.putExtra("taskbath", task_bath);
            intent.putExtra("projectid", project_id);
            intent.putExtra("p_batch", p_batch);
            intent.putExtra("outlet_batch", outlet_batch);
            intent.putExtra("store_num", store_num);
            intent.putExtra("pid", pid);
            intent.putExtra("which_page", "0");
            startActivity(intent);
        }
    };

    protected void onDestroy() {
        if (tsmtt_webview != null) {
            tsmtt_webview.destroy();
        }
        super.onDestroy();
    }

    private String nowUrl = "";
    private String nowTitle;
    private String project_id = "", store_id = "", task_bath = "", task_id = "";
    private String p_batch = "", brand = "", code = "", index = "", outlet_batch = "", store_num = "", pid = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpagetask);
        p_batch = getIntent().getStringExtra("p_batch");
        project_id = getIntent().getStringExtra("project_id");
        store_id = getIntent().getStringExtra("store_id");
        task_bath = getIntent().getStringExtra("task_bath");
        task_id = getIntent().getStringExtra("task_id");
        brand = getIntent().getStringExtra("brand");
        code = getIntent().getStringExtra("code");
        index = getIntent().getStringExtra("index");
        pid = getIntent().getStringExtra("pid");
        outlet_batch = getIntent().getStringExtra("outlet_batch");
        nowUrl = getIntent().getStringExtra("online_store_url");
        store_num = getIntent().getStringExtra("store_num");
        systemDBHelper = new SystemDBHelper(this);
        systemDBHelper.deleteWebpagephoto(project_id, store_id, task_id, task_bath, AppInfo.getName(this));
        tsmtt_webview = (MyWebView) findViewById(R.id.tsmtt_webview);
        webpagecommentview = (WebpageCommentView) findViewById(R.id.webpagecommentview);
        webpagecommentview.setOnWebpageCommentViewListener(onWebpageCommentViewListener);
        tsmtt_title = (AppTitle) findViewById(R.id.tsmtt_title);
        tsmtt_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                if (tsmtt_webview != null && tsmtt_webview.canGoBack()) {
                    tsmtt_webview.goBack();
                } else {
                    ConfirmDialog.showDialog(WebpageTaskActivity.this, "提示", 1, "继续返回则会清空该任务下的全部截图",
                            "继续返回", "继续逛店", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                                public void leftClick(Object object) {
                                    systemDBHelper.deleteWebpagephoto(project_id, store_id, task_id, task_bath,
                                            AppInfo.getName(WebpageTaskActivity.this));
                                    baseFinish();
                                }

                                public void rightClick(Object object) {
                                }
                            });
                }
            }
        });
        if (index != null && "0".equals(index)) {
            tsmtt_title.settingName("网上体验(预览)");
            webpagecommentview.setVisibility(View.GONE);
        } else {
            tsmtt_title.settingName("网上体验");
            tsmtt_title.settingExit("查看截图" + systemDBHelper.getWebpagephotoCount(project_id, store_id,
                    task_id, task_bath, AppInfo.getName(WebpageTaskActivity.this)), onExitClickForAppTitle);
        }
        settingWebView(tsmtt_webview);
        tsmtt_webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                nowUrl = s;
                return super.shouldOverrideUrlLoading(webView, s);
            }
        });

        tsmtt_webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
            }

            public void onReceivedTitle(WebView webView, String s) {
                nowTitle = s;
                if (!(index != null && "0".equals(index))) {
                    tsmtt_title.settingName(nowTitle);
                }
            }

            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsAlert(webView, s, s1, jsResult);
            }

            public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsConfirm(webView, s, s1, jsResult);
            }

            public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
                return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
            }

            public boolean onJsBeforeUnload(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsBeforeUnload(webView, s, s1, jsResult);
            }

            public boolean onJsTimeout() {
                return super.onJsTimeout();
            }
        });

        tsmtt_webview.loadUrl(nowUrl);
        tsmtt_webview.setOnMyScrollChanged(new MyWebView.OnMyScrollChanged() {
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
//                Tools.d("l:" + l + ",t:" + t + ",oldl:" + oldl + ",oldt:" + oldt);
            }
        });
        tsmtt_webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                    }
                    break;
                }
                return false;
            }
        });
    }


    private void settingWebView(WebView webView) {
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存，直接用网络加载
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片
        webView.getSettings().setJavaScriptEnabled(true);// webView支持javascript
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 告诉js可以自动打开window
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (tsmtt_webview != null && tsmtt_webview.canGoBack()) {
            tsmtt_webview.goBack();
        } else {
            ConfirmDialog.showDialog(WebpageTaskActivity.this, "提示", 1, "继续返回则会清空该任务下的全部截图",
                    "继续返回", "继续逛店", null, true, new ConfirmDialog.OnSystemDialogClickListener() {
                        public void leftClick(Object object) {
                            systemDBHelper.deleteWebpagephoto(project_id, store_id, task_id, task_bath,
                                    AppInfo.getName(WebpageTaskActivity.this));
                            baseFinish();
                        }

                        public void rightClick(Object object) {
                        }
                    });
        }
    }

    private void clearWebView() {
        if (tsmtt_webview != null) {
            tsmtt_webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            tsmtt_webview.clearHistory();
            tsmtt_webview.clearCache(true);// 清除缓存

            ((ViewGroup) tsmtt_webview.getParent()).removeView(tsmtt_webview);
            tsmtt_webview.destroy();
            tsmtt_webview = null;
        }
    }

    private void saveBitmap(Bitmap picture, String savePath) throws IOException {
        FileOutputStream outputStream2 = null;
        outputStream2 = new FileOutputStream(savePath);
        picture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream2);
        IOUtils.safeClose(outputStream2);
    }

    private MyHandler myHandler = new MyHandler();

    private class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    CustomProgressDialog.Dissmiss();
                }
                break;
            }
        }
    }

}
