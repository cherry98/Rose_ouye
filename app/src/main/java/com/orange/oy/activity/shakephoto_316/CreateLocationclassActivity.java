package com.orange.oy.activity.shakephoto_316;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/13.
 * 创建位置的类型选择
 */

public class CreateLocationclassActivity extends BaseActivity {
    private NetworkConnection placeInfo;
    private ListView createlcl_listview;
    private ArrayList<PlaceInfo> placeInfos = new ArrayList<>();

    protected void onStop() {
        super.onStop();
        if (placeInfo != null) {
            placeInfo.stop(Urls.PlaceInfo);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createlocationclass);
        placeInfo = new NetworkConnection(this) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", Tools.getToken());
                params.put("usermobile", AppInfo.getName(CreateLocationclassActivity.this));
                return params;
            }
        };
        AppTitle createlcl_title = (AppTitle) findViewById(R.id.createlcl_title);
        createlcl_title.settingName("场景类型");
        createlcl_title.showBack(new AppTitle.OnBackClickForAppTitle() {
            public void onBack() {
                baseFinish();
            }
        });
        createlcl_listview = (ListView) findViewById(R.id.createlcl_listview);
        createlcl_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceInfo placeInfo = placeInfos.get(position);
                placeInfo.isSelect = !placeInfo.isSelect;
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }
//                Intent intent = new Intent();
//                intent.putExtra("cap_id", placeInfo.cap_id);
//                intent.putExtra("place_name", placeInfo.place_name);
//                setResult(RESULT_OK, intent);
//                baseFinish();
            }
        });
        getData();
        findViewById(R.id.createlcl_submit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String result1 = "";
                String result2 = "";
                for (PlaceInfo placeInfo : placeInfos) {
                    if (placeInfo.isSelect) {
                        if (TextUtils.isEmpty(result1)) {
                            result1 = placeInfo.cap_id;
                        } else {
                            result1 = result1 + "," + placeInfo.cap_id;
                        }
                        if (TextUtils.isEmpty(result2)) {
                            result2 = placeInfo.place_name;
                        } else {
                            result2 = result2 + "," + placeInfo.place_name;
                        }
                    }
                }
                if (TextUtils.isEmpty(result1) || TextUtils.isEmpty(result2)) {
                    Tools.showToast(CreateLocationclassActivity.this, "请选择类型");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("cap_id", result1);
                intent.putExtra("place_name", result2);
                setResult(RESULT_OK, intent);
                baseFinish();
            }
        });
    }

    private MyAdapter myAdapter;

    private void getData() {
        placeInfo.sendPostRequest(Urls.PlaceInfo, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (200 == jsonObject.getInt("code")) {
                        if (!jsonObject.isNull("data")) {
                            jsonObject = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.optJSONArray("list");
                            if (jsonArray != null) {
                                int length = jsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    PlaceInfo placeInfo = new PlaceInfo();
                                    placeInfo.cap_id = jsonObject1.getString("cap_id");
                                    placeInfo.place_name = jsonObject1.getString("place_name");
                                    placeInfos.add(placeInfo);
                                }
                                myAdapter = new MyAdapter();
                                createlcl_listview.setAdapter(myAdapter);
                            }
                        }
                    } else {
                        Tools.showToast(CreateLocationclassActivity.this, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(CreateLocationclassActivity.this, getResources().getString(R.string.network_error));
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                Tools.showToast(CreateLocationclassActivity.this, getResources().getString(R.string.network_volleyerror));
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        public int getCount() {
            return placeInfos.size();
        }

        public Object getItem(int position) {
            return placeInfos.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                viewHold = new ViewHold();
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        Tools.dipToPx(CreateLocationclassActivity.this, 45));
                LinearLayout linearLayout = new LinearLayout(CreateLocationclassActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER_VERTICAL);
                linearLayout.setPadding(Tools.dipToPx(CreateLocationclassActivity.this, 15), 0,
                        Tools.dipToPx(CreateLocationclassActivity.this, 15), 0);
                linearLayout.setLayoutParams(lp);
                convertView = linearLayout;
                viewHold.textView = new TextView(CreateLocationclassActivity.this);
                viewHold.textView.setTextSize(14);
                viewHold.textView.setTextColor(0xFF231916);
                viewHold.textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp1.weight = 1;
                linearLayout.addView(viewHold.textView, lp1);
                viewHold.imageView = new ImageView(CreateLocationclassActivity.this);
                viewHold.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(Tools.dipToPx(CreateLocationclassActivity.this, 22),
                        Tools.dipToPx(CreateLocationclassActivity.this, 22));
                linearLayout.addView(viewHold.imageView, lp2);

                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            PlaceInfo placeInfo = placeInfos.get(position);
            viewHold.textView.setText(placeInfo.place_name);
            if (placeInfo.isSelect) {
                viewHold.imageView.setImageResource(R.mipmap.round_selected);
            } else {
                viewHold.imageView.setImageResource(R.mipmap.round_notselect);
            }
            return convertView;
        }
    }

    private class ViewHold {
        TextView textView;
        ImageView imageView;
    }

    private class PlaceInfo {
        String cap_id;
        String place_name;
        boolean isSelect;
    }
}
