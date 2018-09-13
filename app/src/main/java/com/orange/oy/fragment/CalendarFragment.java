package com.orange.oy.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.orange.oy.R;
import com.orange.oy.activity.TaskitemDetailActivity;
import com.orange.oy.adapter.CalendarAdapter;
import com.orange.oy.base.AppInfo;
import com.orange.oy.base.BaseFragment;
import com.orange.oy.base.SelecterDialog;
import com.orange.oy.base.Tools;
import com.orange.oy.dialog.CustomProgressDialog;
import com.orange.oy.dialog.MonPickerDialog;
import com.orange.oy.info.CalendarItem;
import com.orange.oy.info.CalendarSelectInfo;
import com.orange.oy.network.NetworkConnection;
import com.orange.oy.network.Urls;
import com.orange.oy.view.AppTitle;
import com.orange.oy.view.CalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 日历页
 */
public class CalendarFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        AppTitle.OnBackClickForAppTitle {
    public CalendarFragment() {
    }

    private Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    protected AppTitle title;

    private void initTitle() {
        title = (AppTitle) view.findViewById(R.id.calendar_title);
        title.settingName(getResources().getString(R.string.calendar));
        title.showBack(this);
    }

    public void onBack() {
        if (onBackClickForCalendarListener != null) {
            onBackClickForCalendarListener.backCalendar();
        }
    }

    public interface OnBackClickForCalendarListener {
        void backCalendar();
    }

    public void setOnBackClickForCalendarListener(OnBackClickForCalendarListener listener) {
        onBackClickForCalendarListener = listener;
    }

    private OnBackClickForCalendarListener onBackClickForCalendarListener;
    private static int year, month;
    private static String day;
    private static int nowDay, nowYear, nowMonth;
    private GridView gridView;
    private static CalendarItem[] list;
    private CalendarAdapter adapter;
    private View left, right;
    private TextView middle;
    private View view;
    public NetworkConnection networkConnection;
    public NetworkConnection networkConnection2;

    private void initNetworkConnection() {
        networkConnection = new NetworkConnection(context) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> map = new HashMap<>();
                map.put("usermobile", AppInfo.getName(getContext()));
                map.put("token", Tools.getToken());
                map.put("year", year + "");
                map.put("month", month + "");
                return map;
            }
        };
        networkConnection.setIsShowDialog(true);
        networkConnection2 = new NetworkConnection(context) {
            public Map<String, String> getNetworkParams() {
                Map<String, String> map = new HashMap<>();
                map.put("usermobile", AppInfo.getName(getContext()));
                map.put("token", Tools.getToken());
                map.put("year", year + "");
                map.put("month", month + "");
                map.put("day", day);
                return map;
            }
        };
        networkConnection2.setIsShowDialog(true);
    }

    public void onStop() {
        super.onStop();
        if (networkConnection != null) {
            networkConnection.stop(Urls.Scheduleindex);
        }
        if (networkConnection2 != null) {
            networkConnection2.stop(Urls.Scheduledetail);
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if (view == null)
            return;
        context = getActivity();
        initTitle();
        list = new CalendarItem[42];
        right = view.findViewById(R.id.calendar_right);
        left = view.findViewById(R.id.calendar_left);
        middle = (TextView) view.findViewById(R.id.calendar_middle);
        gridView = (GridView) view.findViewById(R.id.calendar_gridview);
        gridView.setOnItemClickListener(this);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) gridView.getLayoutParams();
        lp.height = getHeight(context);
        gridView.setLayoutParams(lp);
        adapter = new CalendarAdapter(context, list);
        gridView.setAdapter(adapter);
        settingList(year = Tools.getYear(), month = Tools.getMonth());
        day = Tools.getCurrentMonthDay() + "";
        right.setOnClickListener(this);
        left.setOnClickListener(this);
        middle.setOnClickListener(this);
        middle.setText(year + "年 " + month + "月");
        initNetworkConnection();
        getData();
    }

    private void getData() {
        networkConnection.sendPostRequest(Urls.Scheduleindex, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                try {
                    JSONObject responseJSONObject = new JSONObject(s);
                    if (responseJSONObject.getInt("code") == 200) {
                        JSONArray jsonArray = responseJSONObject.getJSONArray("datas");
                        int count = jsonArray.length();
                        for (int i = 0, j = getListMinSize(); i < count; i++, j++) {
                            if (list[j] != null) {
                                JSONObject ob = jsonArray.getJSONObject(i);
                                int[] temp = new int[CalendarItem.scheduleLenght];
                                temp[0] = ob.getInt("state");
                                list[j].setSchedule(temp);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Tools.showToast(context, responseJSONObject.getString("msg") + "");
                    }
                } catch (JSONException e) {
                    Tools.d(e.getMessage());
                    Tools.showToast(context, getResources().getString(R.string.network_error));
                } finally {
                    CustomProgressDialog.Dissmiss();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(context, getResources().getString(R.string.network_volleyerror));
            }
        }, getResources().getString(R.string.calendar_dialog_message));
    }

    private int getListMinSize() {
        for (int i = 0; i < 7; i++) {
            if (list[i] != null) {
                return i;
            }
        }
        return 0;
    }

    private void settingList(int year, int month) {
        if (list == null) {
            list = new CalendarItem[42];
        }
        if (nowDay == 0) {
            nowDay = Tools.getCurrentMonthDay();
        }
        if (nowYear == 0) {
            nowYear = Tools.getYear();
        }
        if (nowMonth == 0) {
            nowMonth = Tools.getMonth();
        }
        int week = Tools.getWeekFirstday(year, month);
        int days = Tools.getMonthDays(year, month);
        week--;
        if (week == 0) {
            week = 7;
        }
        for (int i = 0; i < week - 1; i++) {
            list[i] = null;
        }
        for (int i = 0, index = week - 1; i < days; i++, index++) {
            CalendarItem item = new CalendarItem();
            item.setYear(year + "");
            item.setMonth(month + "");
            item.setDay(i + 1 + "");
            item.setWeek(Tools.getWeekDay(year, month, i + 1));
            item.setIsSelect(nowDay == i + 1 && nowYear == year && nowMonth == month);
            list[index] = item;
        }
        int result = 42 - (days + week - 1);
        for (int i = 0, index = 42 - result; i < result; i++, index++) {
            list[index] = null;
        }
    }

    private int getHeight(Context context) {
        int calendar_margin = (int) getResources().getDimension(R.dimen.calendar_margin);
        return CalendarView.getHeight(context) * 6 + 5 * calendar_margin;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_left: {
                if (year == 0 || month == 0) {
                    settingList(year = Tools.getYear(), month = Tools.getMonth());
                } else {
                    if (month == 1) {
                        month = 12;
                        year--;
                    } else {
                        month--;
                    }
                    settingList(year, month);
                }
                middle.setText(year + "年 " + month + "月");
                adapter.notifyDataSetChanged();
                getData();
            }
            break;
            case R.id.calendar_right: {
                if (year == 0 || month == 0) {
                    settingList(year = Tools.getYear(), month = Tools.getMonth());
                } else {
                    if (month == 12) {
                        month = 1;
                        year++;
                    } else {
                        month++;
                    }
                    settingList(year, month);
                }
                middle.setText(year + "年 " + month + "月");
                adapter.notifyDataSetChanged();
                getData();
            }
            break;
            case R.id.calendar_middle: {
                showMonPicker();
            }
            break;
        }
    }

    private SelecterDialog.OnSelecterClickListener onSelecterClickListener = new SelecterDialog
            .OnSelecterClickListener() {
        public void onClickLeft() {
        }

        public void onClickRight() {
            SelecterDialog.dismiss();
        }

        public void onItemClick(int position) {
            CalendarSelectInfo calendarSelectInfo = temids.get(position);
            if (calendarSelectInfo.getFlag() == 1) {
                SelecterDialog.dismiss();
                Intent intent = new Intent(getContext(), TaskitemDetailActivity.class);
                intent.putExtra("id", calendarSelectInfo.getChildId());
                intent.putExtra("projectname", calendarSelectInfo.getParentName());
                intent.putExtra("store_name", calendarSelectInfo.getChildNAme());
                intent.putExtra("store_num", calendarSelectInfo.getChildNum());
                intent.putExtra("province", calendarSelectInfo.getProvince());
                intent.putExtra("city", calendarSelectInfo.getCity());
                intent.putExtra("project_id", calendarSelectInfo.getParentId());
                startActivity(intent);
            }
        }
    };
    private ArrayList<CalendarSelectInfo> temids = new ArrayList<>();

    /**
     * 弹出任务列表 TODO
     */
    private void showTaskList() {
        networkConnection2.sendPostRequest(Urls.Scheduledetail, new Response.Listener<String>() {
            public void onResponse(String s) {
                Tools.d(s);
                if (temids == null) {
                    temids = new ArrayList<CalendarSelectInfo>();
                }
                temids.clear();
                try {
                    JSONObject responseJson = new JSONObject(s);
                    if (responseJson.getInt("code") == 200) {
                        JSONArray jsonArray = responseJson.getJSONArray("datas");
                        int size = jsonArray.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            String projectId = ob.getString("projectid");
                            CalendarSelectInfo csi = new CalendarSelectInfo();
                            csi.setFlag(0);
                            csi.setParentName(ob.getString("projectName"));
                            temids.add(csi);
                            JSONArray ja = ob.getJSONArray("datas");
                            int l = ja.length();
                            for (int j = 0; j < l; j++) {
                                ob = ja.getJSONObject(j);
                                CalendarSelectInfo temp = new CalendarSelectInfo();
                                temp.setFlag(1);
                                temp.setParentId(projectId);
                                temp.setParentName(csi.getParentName());
                                temp.setChildNAme(ob.getString("storeName"));
                                temp.setChildId(ob.getString("storeid"));
                                temp.setChildNum(ob.getString("storeNum"));
                                temp.setCity(ob.getString("city"));
                                temp.setProvince(ob.getString("province"));
                                temp.setChildDetail(temp.getProvince() + temp.getCity() + ob.getString
                                        ("address"));
                                temids.add(temp);
                            }
                        }
                        SelecterDialog.showSelecterForCalendar(context, null, year + "年" + month + "月" + day + "日",
                                temids, true, onSelecterClickListener);
                    } else {
                        Tools.showToast(context, responseJson.getString("msg"));
                    }
                } catch (JSONException e) {
                    Tools.showToast(context, getResources().getString(R.string.network_error));
                }
                CustomProgressDialog.Dissmiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                CustomProgressDialog.Dissmiss();
                Tools.showToast(context, getResources().getString(R.string.network_volleyerror));
            }
        }, getResources().getString(R.string.calendar_dialog_message2));
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list[position] != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] != null) {
                    if (i == position) {
                        day = list[i].getDay();
                        list[i].setIsSelect(true);
                    } else {
                        list[i].setIsSelect(false);
                    }
                }
            }
            if (list[position].getSchedule() != null) {
                int[] schedule = list[position].getSchedule();
                for (int i = 0; i < CalendarItem.scheduleLenght; i++) {
                    if (schedule[i] == 1) {
                        showTaskList();
                        break;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void showMonPicker() {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(Tools.strToDate("yyyy-MM", year + "-" + ((month < 10) ? ("0" + month) : month)));
        new MonPickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                CalendarFragment.year = year;
                CalendarFragment.month = monthOfYear + 1;
                middle.setText(year + "年 " + month + "月");
                settingList(year, month);
                adapter.notifyDataSetChanged();
                getData();
            }
        }, localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar
                .DAY_OF_MONTH)).show();
    }

}
