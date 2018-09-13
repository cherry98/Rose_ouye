package com.orange.oy.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.MainActivity;
import com.orange.oy.activity.RevisePasswordActivity;
import com.orange.oy.activity.SelectCityActivity;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.ConfirmDialog;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.util.FileCache;
import com.orange.oy.util.ImageLoader;
import com.orange.oy.view.AppTitle;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的信息页
 */
public class MyDetailFragment extends BaseFragment implements AppTitle.OnBackClickForAppTitle, View.OnClickListener,
        AppTitle.OnSearchClickForAppTitle {
    public interface OnSelectCityListener {
        void onSelectClick();
    }

    public void onBack() {
        if (onBackClickForMyDetailFragment != null) {
            onBackClickForMyDetailFragment.onBackForMyDetail();
        }
    }

    public void onStop() {
        super.onStop();
        if (sendData != null) {
            sendData.stop(Urls.UpateUser);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void setOnSelectCityListener(OnSelectCityListener listener) {
        onSelectCityListener = listener;
    }

    private View mView;
    private Bitmap bitmap;
    private OnSelectCityListener onSelectCityListener;
    private ImageView mydetail_item1_1;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mydetail_item2_layout: {
                startActivityForResult(new Intent(getContext(), RevisePasswordActivity.class), AppInfo
                        .RevisePasswordRequestCode);
            }
            break;
            case R.id.mydetail_item1_1: {
                if (mydetail_item1_1.getTag() != null) {
                    onSearch();
                    break;
                }
            }
            case R.id.mydetail_item1: {
                canEdit();
                mydetail_item1.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager) mydetail_item1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mydetail_item1, 0);
            }
            break;
            case R.id.mydetail_item5: {
                canEdit();
                startActivityForResult(new Intent(getContext(), SelectCityActivity.class), AppInfo
                        .SelectCityRequestCode);
            }
            break;
            case R.id.mydetail_img_layout: {
                canEdit();
                ConfirmDialog.showDialog(getContext(), "选择图片", "", "拍照", "相册", null, true, new ConfirmDialog
                        .OnSystemDialogClickListener() {
                    public void leftClick(Object object) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto
                                (getContext()).getPath() + "/myImg0.jpg")));
                        intent.putExtra("camerasensortype", 1);
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForTake);
                    }

                    public void rightClick(Object object) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 200);
                        intent.putExtra("outputY", 200);
                        intent.putExtra("return-data", false);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(getContext())
                                .getPath() + "/myImg.jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        intent.putExtra("noFaceDetection", true);
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForPick);
                    }
                });
            }
            break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case AppInfo.MyDetailRequestCodeForTake: {
                    String filePath = FileCache.getDirForPhoto(getContext()).getPath() + "/myImg0.jpg";
                    if (!new File(filePath).isFile()) {
                    } else {
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(Uri.fromFile(new File(FileCache.getDirForPhoto(getContext())
                                .getPath() + "/myImg0.jpg")), "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 200);
                        intent.putExtra("outputY", 200);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileCache.getDirForPhoto(getContext())
                                .getPath() + "/myImg.jpg")));
                        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                        startActivityForResult(intent, AppInfo.MyDetailRequestCodeForCut);
                    }
                }
                break;
                case AppInfo.MyDetailRequestCodeForPick: {
//                    Uri selectedImage = data.getData();
//                    String filePath = null;
//                    try {
//                        if (selectedImage.toString().startsWith("content")) {
//                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                            Cursor cursor = getContext().getContentResolver().query(selectedImage,
//                                    filePathColumn, null, null, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                                filePath = cursor.getString(columnIndex);
//                                cursor.close();
//                            }
//                        } else {
//                            filePath = selectedImage.getPath();
//                        }
//                    } catch (Exception e) {
//                    }
//                    if (TextUtils.isEmpty(filePath)) {
//                        Tools.showToast(getContext(), "图片读取失败");
//                        return;
//                    }
//                    settingImgPath(filePath);
                    String filePath = FileCache.getDirForPhoto(getContext()).getPath() + "/myImg.jpg";
                    settingImgPath(filePath);
                }
                break;
                case AppInfo.MyDetailRequestCodeForCut: {
                    String filePath = FileCache.getDirForPhoto(getContext()).getPath() + "/myImg.jpg";
                    settingImgPath(filePath);
                }
                break;
            }
        } else if (resultCode == AppInfo.SelectCityResultCode && data != null) {
            mydetail_item5.setText(data.getStringExtra("cityName"));
            onSearch();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void settingImgPath(String path) {
        if (!new File(path).isFile()) {
            Tools.showToast(getContext(), "拍照方式错误");
            return;
        }
        bitmap = imageZoom(Tools.getBitmap(path), 50);
        if (bitmap == null) {
            Tools.showToast(getContext(), "头像设置失败");
            return;
        }
        mydetail_img.setImageBitmap(bitmap);
        mydetail_img.setTag(path);
        onSearch();
    }

    public void onSearch() {
        if (mydetail_item1.getText().toString().trim().equals(AppInfo.getUserName(getContext())) && mydetail_item5.getText()
                .toString().trim().equals(AppInfo.getUserDistric(getContext())) && bitmap == null) {
            noEdit();
        } else {
            sendData();
        }
    }

    public interface OnBackClickForMyDetailFragment {
        void onBackForMyDetail();
    }

    private OnBackClickForMyDetailFragment onBackClickForMyDetailFragment;

    public void setOnBackClickForMyDetailFragment(OnBackClickForMyDetailFragment listener) {
        onBackClickForMyDetailFragment = listener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mydetail, container, false);
        return mView;
    }

    private void initNetworkConnection() {
        sendData = new NetworkConnection(getContext()) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                String username = mydetail_item1.getText().toString().trim();
//                String sex = mydetail_item4.getText().toString().trim();
                String address = mydetail_item5.getText().toString().trim();
                if (!username.equals(AppInfo.getUserName(getContext()))) {
                    params.put("username", username);
                }
//                if (!sex.equals(AppInfo.getUserSex(getContext()))) {
//                    if (sex.equals("男"))
//                        params.put("sex", "0");
//                    else
//                        params.put("sex", "1");
//                }
                if (!address.equals(AppInfo.getUserDistric(getContext()))) {
                    params.put("address", address);
                }
                if (bitmap != null) {
                    params.put("img", Tools.bitmapToBase64(bitmap));
                }
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(getContext()));
                return params;
            }
        };
        sendData.setIsShowDialog(true);
    }

    private TextView mydetail_item3, mydetail_item6, mydetail_item5,mydetail_item4;
    private EditText mydetail_item1;
    private ImageView mydetail_img;
    private ImageLoader imageLoader;
    private NetworkConnection sendData;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetworkConnection();
        imageLoader = new ImageLoader(getContext());
        mydetail_item1 = (EditText) mView.findViewById(R.id.mydetail_item1);
        mydetail_item3 = (TextView) mView.findViewById(R.id.mydetail_item3);
//        mydetail_item4 = (TextView) mView.findViewById(R.id.mydetail_item4);
        mydetail_item5 = (TextView) mView.findViewById(R.id.mydetail_item5);
        mydetail_item6 = (TextView) mView.findViewById(R.id.mydetail_item6);
        mydetail_img = (ImageView) mView.findViewById(R.id.mydetail_img);
        mydetail_item1_1 = (ImageView) mView.findViewById(R.id.mydetail_item1_1);
        mydetail_img.setImageResource(R.mipmap.my_nologin);
        String name = AppInfo.getName(getContext());
        if (Tools.isMobile(name)) {
            mydetail_item3.setText(name);
            mView.findViewById(R.id.mydetail_item2_layout).setOnClickListener(this);
        } else {
            mydetail_item3.setText("");
            mView.findViewById(R.id.mydetail_item2_layout).setOnClickListener(null);
        }
//        mydetail_item4.setText(AppInfo.getUserSex(getContext()));
        mydetail_item5.setText(AppInfo.getUserDistric(getContext()));
        mydetail_item6.setText(AppInfo.getInviteCode(getContext()));
        imageLoader.DisplayImage(AppInfo.getUserImagurl(getContext()), mydetail_img, R.mipmap.my_nologin);
        noEdit();
        mView.findViewById(R.id.mydetail_img_layout).setOnClickListener(this);
        mView.findViewById(R.id.mydetail_item1_1).setOnClickListener(this);
        mydetail_item1.setOnClickListener(this);
//        mydetail_item4.setOnClickListener(this);
        mydetail_item5.setOnClickListener(this);
        if (!TextUtils.isEmpty(MainActivity.getCityNameState)) {
            mydetail_item5.setText(MainActivity.getCityNameState);
            canEdit();
            MainActivity.getCityNameState = null;
        }
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mydetail_item1.setText(AppInfo.getUserName(getContext()));
    }

    private void sendData() {
        sendData.sendPostRequest(Urls.UpateUser, new Response.Listener<String>() {
            public void onResponse(String s) {
                try {
                    JSONObject job = new JSONObject(s);
                    int code = job.getInt("code");
                    if (code == 200) {
                        Tools.showToast(getContext(), job.getString("msg"));
//                        AppInfo.setUserinfo2(getContext(), mydetail_item1.getText().toString().trim(), mydetail_item5.getText().toString().trim(),
//                                null, null);
                        Object ob = mydetail_img.getTag();
                        String filePath = "";
                        if (ob != null) {
                            filePath = ob.toString();
                            AppInfo.setUserImg(getContext(), filePath);
                        }
                        noEdit();
                    } else {
                        Tools.showToast(getContext(), job.getString("msg"));
                    }
                } catch (Exception e) {
                    Tools.showToast(getContext(), getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(getContext(), getResources().getString(R.string.network_volleyerror));
            }
        }, "正在修改...");
    }

    private void noEdit() {
        mydetail_item1_1.setTag(null);
        mydetail_item1_1.setImageResource(R.mipmap.mydetail_name);
        mydetail_item1.setFocusable(false);
        mydetail_item1.setFocusableInTouchMode(false);
    }

    private void canEdit() {
        mydetail_item1_1.setTag("1");
        mydetail_item1_1.setImageResource(R.mipmap.mydetail_name2);
        mydetail_item1.setFocusable(true);
        mydetail_item1.setFocusableInTouchMode(true);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private Bitmap imageZoom(Bitmap bitMap, double maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        double mid = b.length / 1024;
        if (mid > maxSize) {
            double i = mid / maxSize;
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }

    private Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
    }
}
