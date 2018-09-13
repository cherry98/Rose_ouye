package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.activity.experience.CommentSelectActivity;
import com.orange.oy.base.Tools;
import com.orange.oy.info.QuestionListInfo;
import com.orange.oy.info.StoreInfo;
import com.orange.oy.view.MyGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xiedongyan on 2017/12/20.
 * 评选页面适配器~~
 */

public class CommentSelectAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QuestionListInfo> questionlists;
    private ArrayList<StoreInfo> storeinfos;

    public CommentSelectAdapter(Context context, ArrayList<QuestionListInfo> questionlists,
                                ArrayList<StoreInfo> storeinfos) {
        this.context = context;
        this.questionlists = questionlists;
        this.storeinfos = storeinfos;
    }

    public JSONArray getJsonArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        int size = questionlists.size();
        String storeidList = null,storenameList = null;
        for(StoreInfo temp : storeinfos){
            if(TextUtils.isEmpty(storeidList)){
                storeidList = temp.getStoreid();
            }else{
                storeidList = storeidList+","+temp.getStoreid();
            }
            if (TextUtils.isEmpty(storenameList)) {
                storenameList = temp.getStoreName();
            }else{
                storenameList = storenameList + "," + temp.getStoreName();
            }
        }
        ArrayList<StoreInfo> tempList;
        String tempStoreName = null,tempStoreId = null;
        for (int i=0;i<size;i++){
            JSONObject jsonObject = new JSONObject();
            tempList = adapters.get(i).getStoreinfos();
            for(StoreInfo temp : tempList){
                if(temp.isSelect){
                    tempStoreId = temp.getStoreid();
                    tempStoreName = temp.getStoreName();
                }
            }
            if(tempStoreId==null||tempStoreName==null){
                return null;
            }
            jsonObject.put("selectionId", questionlists.get(i).getSelectionId());
            jsonObject.put("storeName", tempStoreName);
            jsonObject.put("storeId", tempStoreId);
            jsonObject.put("storeidList", storeidList);
            jsonObject.put("storenameList", storenameList);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public int getCount() {
        return questionlists.size();
    }

    @Override
    public Object getItem(int position) {
        return questionlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private ArrayList<SelectDetailAdapter> adapters = new ArrayList<>();
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = Tools.loadLayout(context, R.layout.item_commentselect);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.itemcommentselect_title);
            viewHolder.gridView = (MyGridView) convertView.findViewById(R.id.itemcommentselect_gridview);
            SelectDetailAdapter selectDetailAdapter = new SelectDetailAdapter(context, questionlists.get(position).getStoreinfos());
            adapters.add(selectDetailAdapter);
            selectDetailAdapter.setIndex(position);
            viewHolder.gridView.setAdapter(selectDetailAdapter);
            Tools.d("+id:"+position);
            viewHolder.gridView.setId(position);
            viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Tools.d("id:"+parent.getId()+",position:"+position);
                    Tools.d("=====0" + adapters.get((int)parent.getId()).getSelectPosition() + "position:" + position);
                    adapters.get((int)parent.getId()).setSelectPosition(position);
                    adapters.get((int)parent.getId()).notifyDataSetChanged();
                    for(StoreInfo temp:adapters.get((int)parent.getId()).getStoreinfos()){
                        Tools.d("isSelect:"+temp.isSelect);
                    }
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        QuestionListInfo questionListInfo = questionlists.get(position);
        viewHolder.textView.setText(questionListInfo.getNum() + "、" + questionListInfo.getQuestion());
        return convertView;
    }

    class ViewHolder {
        private TextView textView;
        private MyGridView gridView;
    }
}
