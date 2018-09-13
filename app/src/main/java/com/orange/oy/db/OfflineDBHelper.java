package com.orange.oy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.orange.oy.base.MyApplication;
import com.orange.oy.base.Tools;
import com.orange.oy.info.DownloadDataInfo;
import com.orange.oy.info.TaskDetailLeftInfo;
import com.orange.oy.info.TaskitemDetailNewInfo;
import com.orange.oy.info.TaskitemListInfo;
import com.orange.oy.info.TrafficInfo;
import com.orange.oy.util.FileCache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 离线数据库（离线数据表&离线示例文件表&流量统计表）
 */
public class OfflineDBHelper extends SQLiteOpenHelper {
    private static final String OfflineDBName = "orangeofflinedbname";//数据库名

    public OfflineDBHelper(Context context) {
        super(context, OfflineDBName, null, 4);
    }

    private static final String TABLENAME_OFFLINE = "offline_tablename";//离线数据表表名
    private static final String OFFLINE_USERNAME = "offline_username";
    private static final String OFFLINE_PROJECTID = "offline_projectid";
    private static final String OFFLINE_PROJECTNAME = "offline_projectname";
    private static final String OFFLINE_STOREID = "offline_storeid";
    private static final String OFFLINE_STORENAME = "offline_stroename";
    private static final String OFFLINE_STORENUM = "offline_storenum";
    private static final String OFFILNE_STORETIME = "offline_storetime";
    private static final String OFFLINE_STOREADDRESS = "offline_storeaddress";
    private static final String OFFLINE_ACCESSEDNUM = "offline_accessed_num";
    private static final String OFFLINE_PACKAGEID = "offline_packageid";
    private static final String OFFLINE_PACKAGENAME = "offline_packagename";
    private static final String OFFLINE_PHOTO_COMPRESSION = "offline_photo_compression";
    private static final String OFFLINE_CATEGORY1_NAME = "offline_category1_name";
    private static final String OFFLINE_CATEGORY2_NAME = "offline_category2_name";
    private static final String OFFLINE_CATEGORY3_NAME = "offline_category3_name";
    private static final String OFFLINE_CATEGORY1_CONTENT = "offline_category1_content";
    private static final String OFFLINE_CATEGORY2_CONTENT = "offline_category2_content";
    private static final String OFFLINE_CATEGORY3_CONTENT = "offline_category3_content";
    private static final String OFFLINE_INVALID = "offline_invalid";//int型，是否能关闭，0：不能关闭；1：可以关闭
    private static final String OFFLINE_ATTRIBUTE = "offline_attribute";//int型，是否有属性分类，0：没有；1：有
    private static final String OFFLINE_IS_PACKAGE = "offline_is_package";//int型，是否是任务包
    private static final String OFFLINE_IS_RECORD = "offline_is_record";//int型，是否全程录音，1：是；0：否
    private static final String OFFLINE_IS_CLOSE = "offline_is_close";//int型(默认为1),1任务包可以执行，2任务包关闭
    private static final String OFFLINE_TASKID = "offline_taskid";
    private static final String OFFLINE_TASKNAME = "offline_taskname";
    private static final String OFFLINE_TASKTYPE = "offline_tasktype";
    private static final String OFFLINE_TASKDETAIL = "offline_taskdetail";//任务详情json
    private static final String OFFLINE_TASKCONTENT = "offline_taskcontent";//任务内容json
    private static final String OFFLINE_IS_COMPLETED = "offline_is_completed";//int型，任务或任务包是否完成，0：没完成；1：完成了
    //version 2
    private static final String OFFLINE_IS_WATERMARK = "offline_is_watermark";//int型，是否有水印，0：没有；1：有
    private static final String OFFLINE_CODE = "offline_code";//代号名
    private static final String OFFLINE_BRAND = "offline_brand";//品牌名
    //version 3
    private static final String OFFLINE_OUTLET_BATCH = "offline_outlet_batch";//网点批次
    private static final String OFFLINE_P_BATCH = "offline_p_batch";//任务包批次
    //version 4
    private static final String OFFLINE_IS_PACKAGE_TASK = "offline_is_package_task";//是否是任务包任务 int 型，0：不是；1是
    private static final String OFFLINE_INVALID_TYPE = "offline_invalid_type";//关闭任务包需要执行的任务，0：之前任务调用之前接口，1：仅备注，2：拍照任务，视频任务
    private static final String OFFLINE_IS_TASKPHOTO = "offline_is_taskphoto";//是否允许连续拍照
    //*******************************************************************************
    private static final String TABLENAME_OFFLINEOUTLETNOTE = "offlineoutletnote_tablename";//离线网点数据
    private static final String OFFLINE_OUTLETNOTE = "offline_outletnote";//网点数据
    //*******************************************************************************
    private static final String TABLENAME_OFFLINEDOWNLOAD = "offlinedownload_tablename";//离线数据示例文件下载表名
    private static final String OFFLINEDOWNLOAD_URL = "offlinedownload_url";
    private static final String OFFLINEDOWNLOAD_PATH = "offlinedownload_path";
    private static final String OFFLINEDOWNLOAD_IS_DOWN = "offlinedownload_is_down";//int型，0：未下载；1：已下载
    //*******************************************************************************
    private static final String TABLENAME_OFFLINECOMPLETED = "offlinecompleted_tablename";//离线完成的包任务记录，用于检查包是否可以完成
    private static final String OFFLINECOMPLETED_CATEGORY1 = "offlinecompleted_category1";
    private static final String OFFLINECOMPLETED_CATEGORY2 = "offlinecompleted_category2";
    private static final String OFFLINECOMPLETED_CATEGORY3 = "offlinecompleted_category3";
    //*******************************************************************************
    private static final String TABLENAME_TRAFFICSUM = "trafficsum_tablename";//流量统计表
    private static final String TRAFFICSUM_SUM = "trafficsum_sum";

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLENAME_OFFLINE + "(id INTEGER PRIMARY KEY," + OFFLINE_USERNAME + " TEXT," +
                OFFLINE_PROJECTID + " TEXT," + OFFLINE_PROJECTNAME + " TEXT," + OFFLINE_STOREID + " TEXT," +
                OFFLINE_STORENAME + " TEXT," + OFFILNE_STORETIME + " TEXT," + OFFLINE_PACKAGEID + " TEXT," +
                OFFLINE_PACKAGENAME + " TEXT," + OFFLINE_IS_CLOSE + " INTEGER DEFAULT 1," + OFFLINE_PHOTO_COMPRESSION + " TEXT," +
                OFFLINE_TASKID + " TEXT," + OFFLINE_STORENUM + " TEXT," + OFFLINE_STOREADDRESS + " TEXT," +
                OFFLINE_ACCESSEDNUM + " TEXT," + OFFLINE_TASKNAME + " TEXT," + OFFLINE_TASKTYPE + " TEXT," +
                OFFLINE_TASKDETAIL + " TEXT," + OFFLINE_CATEGORY1_NAME + " TEXT," + OFFLINE_CATEGORY2_NAME +
                " TEXT," + OFFLINE_CATEGORY3_NAME + " TEXT," + OFFLINE_CATEGORY1_CONTENT + " TEXT," +
                OFFLINE_CATEGORY2_CONTENT + " TEXT," + OFFLINE_CATEGORY3_CONTENT + " TEXT," +
                OFFLINE_INVALID + " INTEGER," + OFFLINE_IS_RECORD + " INTEGER," +
                OFFLINE_ATTRIBUTE + " INTEGER," + OFFLINE_IS_PACKAGE + " INTEGER," + OFFLINE_IS_PACKAGE_TASK + " INTEGER,"
                + OFFLINE_IS_COMPLETED + " INTEGER," + OFFLINE_INVALID_TYPE + " TEXT," + OFFLINE_IS_TASKPHOTO + " TEXT," +
                OFFLINE_IS_WATERMARK + " INTEGER," + OFFLINE_CODE + " TEXT," + OFFLINE_BRAND + " TEXT," +
                OFFLINE_OUTLET_BATCH + " TEXT," + OFFLINE_P_BATCH + " TEXT," + OFFLINE_TASKCONTENT + " TEXT" + ")");
        db.execSQL("create table if not exists " + TABLENAME_OFFLINEDOWNLOAD + "(id INTEGER PRIMARY KEY," +
                OFFLINE_USERNAME + " TEXT," + OFFLINE_PROJECTID + " TEXT," + OFFLINE_STOREID + " TEXT," +
                OFFLINE_PACKAGEID + " TEXT," + OFFLINE_TASKID + " TEXT," + OFFLINEDOWNLOAD_PATH + " TEXT," +
                OFFLINEDOWNLOAD_IS_DOWN + " INTEGER," + OFFLINEDOWNLOAD_URL + " TEXT" + ")");
        db.execSQL("create table if not exists " + TABLENAME_OFFLINECOMPLETED + "(id INTEGER PRIMARY KEY," +
                OFFLINE_USERNAME + " TEXT," + OFFLINE_PROJECTID + " TEXT," + OFFLINE_STOREID + " TEXT," +
                OFFLINE_PACKAGEID + " TEXT," + OFFLINE_TASKID + " TEXT," + OFFLINECOMPLETED_CATEGORY1 + " TEXT," +
                OFFLINECOMPLETED_CATEGORY2 + " TEXT," + OFFLINECOMPLETED_CATEGORY3 + " TEXT" + ")");
        db.execSQL("create table if not exists " + TABLENAME_TRAFFICSUM + "(id INTEGER PRIMARY KEY," +
                OFFLINE_USERNAME + " TEXT," + OFFLINE_PROJECTID + " TEXT," +
                OFFLINE_PROJECTNAME + " TEXT," + OFFLINE_STOREID + " TEXT," + OFFLINE_STORENAME + " TEXT," +
                TRAFFICSUM_SUM + " TEXT" + ")");
        db.execSQL("create table if not exists " + TABLENAME_OFFLINEOUTLETNOTE + "(id INTEGER PRIMARY KEY," +
                OFFLINE_USERNAME + " TEXT," + OFFLINE_PROJECTID + " TEXT," + OFFLINE_STOREID + " TEXT," +
                OFFLINE_OUTLETNOTE + " TEXT" + ")");
    }

    public synchronized void clearCache() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_OFFLINE, null, null);
        db.delete(TABLENAME_OFFLINECOMPLETED, null, null);
        db.delete(TABLENAME_OFFLINEDOWNLOAD, null, null);
        db.delete(TABLENAME_OFFLINEOUTLETNOTE, null, null);
        db.close();
    }

    public synchronized void deleteOfflineForRedo(String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_OFFLINE, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINECOMPLETED, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINEDOWNLOAD, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINEOUTLETNOTE, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ?",
                new String[]{username, projectid});
        db.close();
    }

    private String myformat(String str) {
        if (TextUtils.isEmpty(str) || "null".equals(str)) {
            return "";
        } else {
            return str;
        }
    }

    public synchronized boolean insertOfflinedata(String username, String projectid, String projectname, String code,
                                                  String brand, String storeid, String storename, String storetime,
                                                  String storenum, String storeaddress, String accessednum,
                                                  String photo_compression, boolean is_watermark, boolean is_record,
                                                  String packageid, String packagename, String category1_name,
                                                  String category2_name, String category3_name, String category1_content,
                                                  String category2_content, String category3_content, boolean is_invalid,
                                                  boolean is_attribute, boolean is_package, boolean is_package_task,
                                                  String invalidtype, String is_taskphoto, String taskid, String taskname,
                                                  String tasktype, String outlet_batch, String p_batch, String taskdetail,
                                                  String taskcontent) {
        username = myformat(username);
        projectid = myformat(projectid);
        projectname = myformat(projectname);
        storeid = myformat(storeid);
        storename = myformat(storename);
        storetime = myformat(storetime);
        storenum = myformat(storenum);
        storeaddress = myformat(storeaddress);
        accessednum = myformat(accessednum);
        photo_compression = myformat(photo_compression);
        packageid = myformat(packageid);
        packagename = myformat(packagename);
        category1_name = myformat(category1_name);
        category2_name = myformat(category2_name);
        category3_name = myformat(category3_name);
        category1_content = myformat(category1_content);
        category2_content = myformat(category2_content);
        category3_content = myformat(category3_content);
        taskid = myformat(taskid);
        tasktype = myformat(tasktype);
        taskname = myformat(taskname);
        taskdetail = myformat(taskdetail);
        taskcontent = myformat(taskcontent);
        code = myformat(code);
        brand = myformat(brand);
        if (TextUtils.isEmpty(outlet_batch) || "null".equals(outlet_batch)) {
            outlet_batch = "1";
        }
        if (TextUtils.isEmpty(p_batch) || "null".equals(p_batch)) {
            p_batch = "1";
        }
        if (TextUtils.isEmpty(invalidtype) || "null".equals(invalidtype)) {
            invalidtype = "1";
        }
        if (TextUtils.isEmpty(is_taskphoto) || "null".equals(is_taskphoto)) {
            is_taskphoto = "1";
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OFFLINE_USERNAME, username);
        cv.put(OFFLINE_PROJECTID, projectid);
        cv.put(OFFLINE_PROJECTNAME, projectname);
        cv.put(OFFLINE_STOREID, storeid);
        cv.put(OFFLINE_STORENAME, storename);
        cv.put(OFFILNE_STORETIME, storetime);
        cv.put(OFFLINE_STORENUM, storenum);
        cv.put(OFFLINE_STOREADDRESS, storeaddress);
        cv.put(OFFLINE_ACCESSEDNUM, accessednum);
        cv.put(OFFLINE_PHOTO_COMPRESSION, photo_compression);
        cv.put(OFFLINE_PACKAGEID, packageid);
        cv.put(OFFLINE_PACKAGENAME, packagename);
        cv.put(OFFLINE_CATEGORY1_NAME, category1_name);
        cv.put(OFFLINE_CATEGORY2_NAME, category2_name);
        cv.put(OFFLINE_CATEGORY3_NAME, category3_name);
        cv.put(OFFLINE_CATEGORY1_CONTENT, category1_content);
        cv.put(OFFLINE_CATEGORY2_CONTENT, category2_content);
        cv.put(OFFLINE_CATEGORY3_CONTENT, category3_content);
        cv.put(OFFLINE_INVALID, (is_invalid) ? 1 : 0);
        cv.put(OFFLINE_ATTRIBUTE, (is_attribute) ? 1 : 0);
        cv.put(OFFLINE_IS_PACKAGE, (is_package) ? 1 : 0);
        cv.put(OFFLINE_IS_RECORD, (is_record) ? 1 : 0);
        cv.put(OFFLINE_IS_WATERMARK, (is_watermark) ? 1 : 0);
        cv.put(OFFLINE_IS_PACKAGE_TASK, (is_package_task) ? 1 : 0);
        cv.put(OFFLINE_INVALID_TYPE, invalidtype);
        cv.put(OFFLINE_IS_TASKPHOTO, is_taskphoto);
        cv.put(OFFLINE_CODE, code);
        cv.put(OFFLINE_BRAND, brand);
        cv.put(OFFLINE_TASKID, taskid);
        cv.put(OFFLINE_TASKNAME, taskname);
        cv.put(OFFLINE_TASKTYPE, tasktype);
        cv.put(OFFLINE_TASKDETAIL, taskdetail);
        cv.put(OFFLINE_TASKCONTENT, taskcontent);
        cv.put(OFFLINE_IS_COMPLETED, 0);
        cv.put(OFFLINE_OUTLET_BATCH, outlet_batch);
        cv.put(OFFLINE_P_BATCH, p_batch);
        long index = db.insert(TABLENAME_OFFLINE, null, cv);
        db.close();
        return index != -1;
    }

    /**
     * 任务包完成
     *
     * @param username  账号
     * @param projectid 项目id
     * @param storeid   店铺id
     * @param packageid 包id
     */
    public synchronized void completedPackage(String username, String projectid, String storeid, String packageid) {
        if (TextUtils.isEmpty(packageid)) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OFFLINE_IS_COMPLETED, 1);
        long index = db.update(TABLENAME_OFFLINE, cv, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ?",
                new String[]{username, projectid, storeid, packageid});
        Tools.d("TABLENAME_OFFLINE completedPackage index:" + index);
    }

    /**
     * 任务完成
     *
     * @param username  账号
     * @param projectid 项目id
     * @param storeid   店铺id
     * @param taskid    任务id
     */
    public synchronized void completedTask(String username, String projectid, String storeid, String taskid) {
        if (TextUtils.isEmpty(taskid)) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OFFLINE_IS_COMPLETED, 1);
        long index = db.update(TABLENAME_OFFLINE, cv, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_TASKID + " is ? and " + OFFLINE_IS_PACKAGE + " is 0",
                new String[]{username, projectid, storeid, taskid});
        Tools.d("TABLENAME_OFFLINE completedPackage index:" + index);
    }

    /**
     * 获取关闭任务包任务
     *
     * @param username  账号
     * @param projectid 项目id
     * @param storeid   网点id
     * @param packageid 包id
     * @return 任务数据
     */
    public synchronized TaskitemListInfo getClosePackageTask(String username, String projectid, String storeid, String
            packageid) {
        TaskitemListInfo taskitemListInfo = new TaskitemListInfo();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                        OFFLINE_IS_COMPLETED + " is 0 and " + OFFLINE_IS_PACKAGE_TASK + " is 1"
                , new String[]{username, projectid, storeid, packageid}, null, null, null);
        if (cursor.moveToFirst()) {
            taskitemListInfo.setType(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKTYPE)));
            taskitemListInfo.setTaskname(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKNAME)));
            taskitemListInfo.setP_id(packageid);
            taskitemListInfo.setTask_id(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKID)));
            taskitemListInfo.setStoreid(storeid);
        }
        cursor.close();
        db.close();
        return taskitemListInfo;
    }

    /**
     * 关闭任务包
     *
     * @param username  账号
     * @param projectid 项目id
     * @param storeid   店铺id
     * @param packageid 包id
     */
    public synchronized void closePackage(String username, String projectid, String storeid, String packageid) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OFFLINE_IS_COMPLETED, 1);
        cv.put(OFFLINE_IS_CLOSE, 2);
        long index = db.update(TABLENAME_OFFLINE, cv, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and (" + OFFLINE_IS_PACKAGE + " is 1 or "
                        + OFFLINE_IS_PACKAGE_TASK + " is 1)",
                new String[]{username, projectid, storeid, packageid});
        Tools.d("TABLENAME_OFFLINE completedPackage index:" + index);
        db.close();
    }

    /**
     * 检测店铺之前是否插如果数据，如果有就清除
     *
     * @param username  账号
     * @param projectid 项目id
     * @param storeid   店铺id
     */
    public synchronized void checkClear(String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_OFFLINE, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINECOMPLETED, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINEDOWNLOAD, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINEOUTLETNOTE, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ?",
                new String[]{username, projectid});
        db.close();
    }

    public synchronized ArrayList<TaskDetailLeftInfo> getProjectList(String username) {
        ArrayList<TaskDetailLeftInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, new String[]{OFFLINE_PROJECTID, OFFLINE_PROJECTNAME},
                OFFLINE_USERNAME + " is ? and " + OFFLINE_IS_PACKAGE_TASK + " is 0", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String projectid = cursor.getString(cursor.getColumnIndex(OFFLINE_PROJECTID));
                boolean isHad = false;
                for (TaskDetailLeftInfo temp : list) {
                    if (temp.getId().equals(projectid)) {
                        isHad = true;
                        break;
                    }
                }
                if (!isHad) {
                    TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                    taskDetailLeftInfo.setId(projectid);
                    taskDetailLeftInfo.setName(cursor.getString(cursor.getColumnIndex(OFFLINE_PROJECTNAME)));
                    taskDetailLeftInfo.setIs_taskphoto(cursor.getString(cursor.getColumnIndex(OFFLINE_IS_TASKPHOTO)));
                    list.add(taskDetailLeftInfo);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized ArrayList<TaskDetailLeftInfo> getStoreList(String username, String projectid, String searchStr) {
        ArrayList<TaskDetailLeftInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, new String[]{OFFLINE_STOREID, OFFLINE_STORENAME, OFFLINE_STORENUM,
                        OFFLINE_STOREADDRESS, OFFILNE_STORETIME, OFFLINE_PROJECTNAME,
                        OFFLINE_PHOTO_COMPRESSION, OFFLINE_IS_RECORD, OFFLINE_IS_WATERMARK, OFFLINE_CODE, OFFLINE_BRAND,
                        OFFLINE_IS_TASKPHOTO},
                OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and ("
                        + OFFLINE_STORENAME + " like '%" + searchStr + "%' or " + OFFLINE_STORENUM + " like '%" + searchStr +
                        "%') and " + OFFLINE_IS_PACKAGE_TASK + " is 0", new String[]{username, projectid}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String storeid = cursor.getString(cursor.getColumnIndex(OFFLINE_STOREID));
                boolean isHad = false;
                for (TaskDetailLeftInfo temp : list) {
                    if (temp.getId().equals(storeid)) {
                        isHad = true;
                        break;
                    }
                }
                if (!isHad) {
                    TaskDetailLeftInfo taskDetailLeftInfo = new TaskDetailLeftInfo();
                    taskDetailLeftInfo.setId(storeid);
                    taskDetailLeftInfo.setName(cursor.getString(cursor.getColumnIndex(OFFLINE_STORENAME)));
                    taskDetailLeftInfo.setCode(cursor.getString(cursor.getColumnIndex(OFFLINE_STORENUM)));
                    taskDetailLeftInfo.setTimedetail(cursor.getString(cursor.getColumnIndex(OFFILNE_STORETIME)));
                    taskDetailLeftInfo.setCity3(cursor.getString(cursor.getColumnIndex(OFFLINE_STOREADDRESS)));
                    taskDetailLeftInfo.setProjectid(projectid);
                    taskDetailLeftInfo.setProjectname(cursor.getString(cursor.getColumnIndex(OFFLINE_PROJECTNAME)));
                    taskDetailLeftInfo.setPhoto_compression(cursor.getString(cursor.getColumnIndex(OFFLINE_PHOTO_COMPRESSION)));
                    taskDetailLeftInfo.setIs_record(cursor.getInt(cursor.getColumnIndex(OFFLINE_IS_RECORD)));
                    taskDetailLeftInfo.setIs_watermark(cursor.getInt(cursor.getColumnIndex(OFFLINE_IS_WATERMARK)));
                    taskDetailLeftInfo.setCodeStr(cursor.getString(cursor.getColumnIndex(OFFLINE_CODE)));
                    taskDetailLeftInfo.setBrand(cursor.getString(cursor.getColumnIndex(OFFLINE_BRAND)));
                    taskDetailLeftInfo.setIs_taskphoto(cursor.getString(cursor.getColumnIndex(OFFLINE_IS_TASKPHOTO)));
                    list.add(taskDetailLeftInfo);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized void deleteStore(String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_OFFLINE,
                OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ?",
                new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINECOMPLETED,
                OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ?",
                new String[]{username, projectid, storeid});
        db.delete(TABLENAME_OFFLINEDOWNLOAD,
                OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ?",
                new String[]{username, projectid, storeid});
        db.close();
    }

    public synchronized ArrayList<TaskitemDetailNewInfo> getTaskPackage(String username, String projectid, String storeid,
                                                                        String search) {
        if (TextUtils.isEmpty(search)) {
            search = "";
        }
        ArrayList<TaskitemDetailNewInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, new String[]{OFFLINE_STORENAME, OFFLINE_PROJECTNAME, OFFLINE_STORENUM,
                        OFFLINE_IS_PACKAGE, OFFLINE_PACKAGEID, OFFLINE_PACKAGENAME, OFFLINE_ATTRIBUTE, OFFLINE_INVALID,
                        OFFLINE_IS_CLOSE, OFFLINE_TASKID, OFFLINE_TASKNAME, OFFLINE_TASKTYPE, OFFLINE_OUTLET_BATCH,
                        OFFLINE_P_BATCH, OFFLINE_INVALID_TYPE},
                OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " +
                        OFFLINE_IS_COMPLETED + " is 0 and (" + OFFLINE_TASKNAME + " like '%" + search + "%' or " +
                        OFFLINE_PACKAGENAME + " like '%" + search + "%') and " + OFFLINE_IS_PACKAGE_TASK + " is 0",
                new String[]{username, projectid, storeid}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                TaskitemDetailNewInfo taskitemDetailNewInfo = new TaskitemDetailNewInfo();
                taskitemDetailNewInfo.setStoreid(storeid);
                taskitemDetailNewInfo.setStorename(cursor.getString(cursor.getColumnIndex(OFFLINE_STORENAME)));
                taskitemDetailNewInfo.setProjectid(projectid);
                taskitemDetailNewInfo.setProjectname(cursor.getString(cursor.getColumnIndex(OFFLINE_PROJECTNAME)));
                taskitemDetailNewInfo.setStoreNum(cursor.getString(cursor.getColumnIndex(OFFLINE_STORENUM)));
                taskitemDetailNewInfo.setOutlet_batch(cursor.getString(cursor.getColumnIndex(OFFLINE_OUTLET_BATCH)));
                taskitemDetailNewInfo.setP_batch(cursor.getString(cursor.getColumnIndex(OFFLINE_P_BATCH)));
                if (cursor.getInt(cursor.getColumnIndex(OFFLINE_IS_PACKAGE)) == 1) {//是任务包
                    String packageid = cursor.getString(cursor.getColumnIndex(OFFLINE_PACKAGEID));
                    boolean isHad = false;
                    for (TaskitemDetailNewInfo temp : list) {
                        if (temp.getIsPackage().equals("1") && temp.getId().equals(packageid)) {
                            isHad = true;
                            break;
                        }
                    }
                    if (!isHad) {
                        taskitemDetailNewInfo.setId(cursor.getString(cursor.getColumnIndex(OFFLINE_PACKAGEID)));
                        taskitemDetailNewInfo.setName(cursor.getString(cursor.getColumnIndex(OFFLINE_PACKAGENAME)));
                        taskitemDetailNewInfo.setIsCategory(cursor.getInt(cursor.getColumnIndex(OFFLINE_ATTRIBUTE)) == 1);
                        taskitemDetailNewInfo.setIs_invalid(cursor.getInt(cursor.getColumnIndex(OFFLINE_INVALID)) + "");
                        taskitemDetailNewInfo.setIsClose(cursor.getInt(cursor.getColumnIndex(OFFLINE_IS_CLOSE)) + "");
                        taskitemDetailNewInfo.setCloseInvalidtype(cursor.getString(cursor.getColumnIndex(OFFLINE_INVALID_TYPE)));
                        taskitemDetailNewInfo.setIsPackage("1");
//                        String select = "select * from " + TABLENAME_OFFLINE + " where " + OFFLINE_USERNAME + " is '" +
//                                username + "' and " +
//                                OFFLINE_PROJECTID + " is '" + projectid + "' and " + OFFLINE_STOREID + " is '" + storeid +
//                                "' and " + OFFLINE_PACKAGEID + " is '" + packageid + "' and " +
//                                OFFLINE_IS_COMPLETED + " = 0 and " + OFFLINE_IS_PACKAGE_TASK + " = 1";
                        Cursor cursor1 = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " is ? and " +
                                        OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " +
                                        OFFLINE_PACKAGEID + " is ? and " + OFFLINE_IS_COMPLETED + " = 0 and " +
                                        OFFLINE_IS_PACKAGE_TASK + " = 1"
                                , new String[]{username, projectid, storeid, packageid}, null, null, null);
//                        Cursor cursor1 = db.rawQuery(select, null);
                        if (cursor1.moveToFirst()) {
                            taskitemDetailNewInfo.setCloseTasktype(cursor1.getString(cursor1.getColumnIndex(OFFLINE_TASKTYPE)));
                            taskitemDetailNewInfo.setCloseTaskname(cursor1.getString(cursor1.getColumnIndex(OFFLINE_TASKNAME)));
                            taskitemDetailNewInfo.setCloseTaskid(cursor1.getString(cursor1.getColumnIndex(OFFLINE_TASKID)));
                        }
                        cursor1.close();
                        list.add(taskitemDetailNewInfo);
                    }
                } else {
                    taskitemDetailNewInfo.setId(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKID)));
                    taskitemDetailNewInfo.setName(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKNAME)));
                    taskitemDetailNewInfo.setTask_type(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKTYPE)));
                    taskitemDetailNewInfo.setIsPackage("0");
                    list.add(taskitemDetailNewInfo);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized ArrayList<TaskitemListInfo> getTaskList(String username, String projectid, String storeid,
                                                                String packageid) {
        ArrayList<TaskitemListInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                        OFFLINE_IS_COMPLETED + " is 0 and " + OFFLINE_IS_PACKAGE_TASK + " is 0",
                new String[]{username, projectid, storeid, packageid}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                TaskitemListInfo taskitemListInfo = new TaskitemListInfo();
                taskitemListInfo.setType(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKTYPE)));
                taskitemListInfo.setTaskname(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKNAME)));
                taskitemListInfo.setP_id(packageid);
                taskitemListInfo.setTask_id(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKID)));
                taskitemListInfo.setStoreid(storeid);
                list.add(taskitemListInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized ArrayList<TaskitemListInfo> getTaskList2(String username, String projectid, String storeid,
                                                                 String packageid) {
        ArrayList<TaskitemListInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                        OFFLINE_IS_COMPLETED + " is 0 and " + OFFLINE_IS_PACKAGE_TASK + " is 0",
                new String[]{username, projectid, storeid, packageid}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String task_id = cursor.getString(cursor.getColumnIndex(OFFLINE_TASKID));
                Cursor cursor1 = db.query(TABLENAME_OFFLINECOMPLETED, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID +
                                " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                                OFFLINE_TASKID + " is ?", new String[]{username, projectid, storeid, packageid, task_id}, null,
                        null, null);
                if (!cursor1.moveToFirst()) {
                    TaskitemListInfo taskitemListInfo = new TaskitemListInfo();
                    taskitemListInfo.setType(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKTYPE)));
                    taskitemListInfo.setTaskname(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKNAME)));
                    taskitemListInfo.setP_id(packageid);
                    taskitemListInfo.setTask_id(task_id);
                    taskitemListInfo.setStoreid(storeid);
                    list.add(taskitemListInfo);
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(OFFLINE_IS_COMPLETED, 1);
                    db.update(TABLENAME_OFFLINE, cv, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                                    OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " + OFFLINE_TASKID + " is ?",
                            new String[]{username, projectid, storeid, packageid, task_id});
                }
                cursor1.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized ArrayList<String> getCategorys(String username, String projectid, String storeid, String packageid) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " + OFFLINE_IS_PACKAGE_TASK + " is 0",
                new String[]{username, projectid, storeid, packageid}, null, null, null, "1");
        if (cursor.moveToFirst()) {
            String categoryname1 = cursor.getString(cursor.getColumnIndex(OFFLINE_CATEGORY1_NAME));
            String categoryname2 = cursor.getString(cursor.getColumnIndex(OFFLINE_CATEGORY2_NAME));
            String categoryname3 = cursor.getString(cursor.getColumnIndex(OFFLINE_CATEGORY3_NAME));
            if (!TextUtils.isEmpty(categoryname1)) {
                list.add(categoryname1 + "," + cursor.getString(cursor.getColumnIndex(OFFLINE_CATEGORY1_CONTENT)));
            }
            if (!TextUtils.isEmpty(categoryname2)) {
                list.add(categoryname2 + "," + cursor.getString(cursor.getColumnIndex(OFFLINE_CATEGORY2_CONTENT)));
            }
            if (!TextUtils.isEmpty(categoryname3)) {
                list.add(categoryname3 + "," + cursor.getString(cursor.getColumnIndex(OFFLINE_CATEGORY3_CONTENT)));
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized String getTaskDetail(String username, String projectid, String storeid, String packageid, String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, new String[]{OFFLINE_TASKDETAIL}, OFFLINE_USERNAME + " is ? and " +
                OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                OFFLINE_TASKID + " is ?", new String[]{username, projectid, storeid, packageid, taskid}, null, null, null);
        String result = "";
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OFFLINE_TASKDETAIL));
        }
        cursor.close();
        db.close();
        return result;
    }

    public synchronized String getTaskConetnt(String username, String projectid, String storeid, String packageid, String
            taskid) {
        if (packageid == null) {
            packageid = "";
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, new String[]{OFFLINE_TASKCONTENT}, OFFLINE_USERNAME + " is ? and " +
                OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                OFFLINE_TASKID + " is ?", new String[]{username, projectid, storeid, packageid, taskid}, null, null, null);
        String result = "";
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OFFLINE_TASKCONTENT));
        }
        cursor.close();
        db.close();
        return result;
    }

    public synchronized String getTaskOutletBatch(String username, String projectid, String storeid, String packageid, String
            taskid) {
        if (packageid == null) {
            packageid = "";
        }
        String where = OFFLINE_USERNAME + " is ? and " +
                OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ?";
        String[] strs;
        if (taskid != null) {
            where = where + " and " + OFFLINE_TASKID + " is ?";
            strs = new String[]{username, projectid, storeid, packageid, taskid};
        } else {
            strs = new String[]{username, projectid, storeid, packageid};
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, new String[]{OFFLINE_OUTLET_BATCH}, where, strs, null, null, null);
        String result = "1";
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OFFLINE_OUTLET_BATCH));
        }
        cursor.close();
        db.close();
        return result;
    }

    public synchronized String getTaskPBatch(String username, String projectid, String storeid, String packageid, String
            taskid) {
        if (packageid == null) {
            packageid = "";
        }
        String where = OFFLINE_USERNAME + " is ? and " +
                OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ?";
        String[] strs;
        if (taskid != null) {
            where = where + " and " + OFFLINE_TASKID + " is ?";
            strs = new String[]{username, projectid, storeid, packageid, taskid};
        } else {
            strs = new String[]{username, projectid, storeid, packageid};
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, new String[]{OFFLINE_P_BATCH}, where, strs, null, null, null);
        String result = "1";
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OFFLINE_P_BATCH));
        }
        cursor.close();
        db.close();
        return result;
    }

    public synchronized void insertOutletNote(String username, String projectid, String storeid, String outletnote) {
        if (TextUtils.isEmpty(outletnote) || "null".equals(outletnote)) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OFFLINE_USERNAME, username);
        cv.put(OFFLINE_PROJECTID, projectid);
        cv.put(OFFLINE_STOREID, storeid);
        cv.put(OFFLINE_OUTLETNOTE, outletnote);
        db.insert(TABLENAME_OFFLINEOUTLETNOTE, null, cv);
        db.close();
    }

    public synchronized void settingOutletNote(String username, ArrayList<TaskDetailLeftInfo> list) {
        SQLiteDatabase db = getWritableDatabase();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            TaskDetailLeftInfo temp = list.get(i);
            String projectid = temp.getProjectid();
            String storeid = temp.getId();
            Cursor cursor = db.query(TABLENAME_OFFLINEOUTLETNOTE, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID +
                    " is ? and " + OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid}, null, null, null);
            if (cursor.moveToFirst()) {
                list.get(i).setOutletnote(cursor.getString(cursor.getColumnIndex(OFFLINE_OUTLETNOTE)));
            }
            cursor.close();
        }
        db.close();
    }

    public synchronized void insertOfflineCompleted(String username, String projectid, String storeid, String packageid,
                                                    String taskid, String category1, String category2, String category3) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINECOMPLETED, null, OFFLINE_USERNAME + " is ? and " +
                        OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                        OFFLINE_TASKID + " is ? and " + OFFLINECOMPLETED_CATEGORY1 + " is ? and " + OFFLINECOMPLETED_CATEGORY2 +
                        " is ? and " + OFFLINECOMPLETED_CATEGORY3 + " is ?",
                new String[]{username, projectid, storeid, packageid, taskid, category1, category2, category3}, null, null, null);
        boolean bl = cursor.moveToFirst();
        cursor.close();
        if (!bl) {
            ContentValues cv = new ContentValues();
            cv.put(OFFLINE_USERNAME, username);
            cv.put(OFFLINE_PROJECTID, projectid);
            cv.put(OFFLINE_STOREID, storeid);
            cv.put(OFFLINE_PACKAGEID, packageid);
            cv.put(OFFLINE_TASKID, taskid);
            cv.put(OFFLINECOMPLETED_CATEGORY1, category1);
            cv.put(OFFLINECOMPLETED_CATEGORY2, category2);
            cv.put(OFFLINECOMPLETED_CATEGORY3, category3);
            long index = db.insert(TABLENAME_OFFLINECOMPLETED, null, cv);
            Tools.d("TABLENAME_OFFLINECOMPLETED insert:" + index);
        }
    }

    /**
     * 查询单个分类组合是否做完过
     *
     * @param category1 分类1
     * @param category2 分类2
     * @param category3 分类3
     * @param size      包里的任务总数
     * @return true/false
     */
    public synchronized boolean isCompletedForCategory(String username, String projectid, String storeid, String packageid,
                                                       String category1, String category2, String category3, int size) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINECOMPLETED, new String[]{OFFLINE_TASKID}, OFFLINE_USERNAME + " is ? and " +
                        OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                        OFFLINECOMPLETED_CATEGORY1 + " is ? and " + OFFLINECOMPLETED_CATEGORY2 + " is ? and " +
                        OFFLINECOMPLETED_CATEGORY3 + " is ?",
                new String[]{username, projectid, storeid, packageid, category1, category2, category3}, null, null, null);
        boolean r = cursor.getCount() == size;
        cursor.close();
        db.close();
        return r;
    }

    /**
     * 判断是否可以点击完成
     *
     * @param username  账号
     * @param projectid 项目id
     * @param storeid   网点id
     * @param packageid 包id
     * @return true/false
     */
    public synchronized boolean canCompleted(String username, String projectid, String storeid, String packageid) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " + OFFLINE_IS_PACKAGE_TASK + " is 0"
                , new String[]{username, projectid, storeid, packageid}, null, null, null);
        Map<String, Integer> map = new HashMap<>();
        int size = 0;
        if (cursor.moveToFirst()) {
            do {
                String taskid = cursor.getString(cursor.getColumnIndex(OFFLINE_TASKID));
                Cursor cursor1 = db.query(TABLENAME_OFFLINECOMPLETED, null, OFFLINE_USERNAME + " is ? and " +
                                OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID +
                                " is ? and " + OFFLINE_TASKID + " is ?",
                        new String[]{username, projectid, storeid, packageid, taskid},
                        null, null, null);
                if (cursor1.moveToFirst()) {
                    do {
                        String key = cursor1.getString(cursor1.getColumnIndex(OFFLINECOMPLETED_CATEGORY1)) +
                                cursor1.getString(cursor1.getColumnIndex(OFFLINECOMPLETED_CATEGORY2)) +
                                cursor1.getString(cursor1.getColumnIndex(OFFLINECOMPLETED_CATEGORY3));
                        if (map.containsKey(key)) {
                            int value = map.get(key);
                            map.put(key, value + 1);
                        } else {
                            map.put(key, 1);
                        }
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
                size++;
            } while (cursor.moveToNext());
        }
        Tools.d("task num:" + size);
        cursor.close();
        db.close();
        for (String s : map.keySet()) {
            if (map.get(s) == size) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断店铺列表是否完成
     *
     * @param username  账号
     * @param projectid 项目id
     * @param list      店铺列表
     */
    public synchronized void isCompletedForStore(String username, String projectid, ArrayList<TaskDetailLeftInfo> list) {
        int size = list.size();
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < size; i++) {
            TaskDetailLeftInfo taskDetailLeftInfo = list.get(i);
            if (taskDetailLeftInfo.getIsEdit() == 0) {
                Cursor cursor = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " is ? and " +
                                OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and "
                                + OFFLINE_IS_PACKAGE_TASK + " is 0",
                        new String[]{username, projectid, taskDetailLeftInfo.getId()}, null, null, null);
                if (cursor.moveToFirst()) {
                    Tools.d("离线了");
                    taskDetailLeftInfo.setIsOffline(1);
                    taskDetailLeftInfo.setIsCompleted(1);
                    taskDetailLeftInfo.setIs_record(cursor.getInt(cursor.getColumnIndex(OFFLINE_IS_RECORD)));
                    do {
                        if (cursor.getInt(cursor.getColumnIndex(OFFLINE_IS_COMPLETED)) == 0) {
                            taskDetailLeftInfo.setIsCompleted(0);
                            break;
                        }
                    } while (cursor.moveToNext());
                } else {
                    taskDetailLeftInfo.setIsOffline(0);
                    taskDetailLeftInfo.setIsCompleted(0);
                }
                cursor.close();
                taskDetailLeftInfo.setIsEdit(1);
            }
        }
        db.close();
    }

    /**
     * 判断店铺是否存在
     *
     * @param username  账号
     * @param projectid 项目id
     * @param list      店铺列表
     */
    public synchronized void isCompletedForOfflineStore(String username, String projectid, ArrayList<TaskDetailLeftInfo> list) {
        int size = list.size();
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < size; i++) {
            TaskDetailLeftInfo taskDetailLeftInfo = list.get(i);
            if (taskDetailLeftInfo.getIsEdit() == 0) {
                Cursor cursor = db.query(TABLENAME_OFFLINE, null, OFFLINE_USERNAME + " = ? and " +
                                OFFLINE_PROJECTID + " = ? and " + OFFLINE_STOREID + " = ?",
                        new String[]{username, projectid, taskDetailLeftInfo.getId()}, null, null, null);
                if (cursor.moveToFirst()) {
                    list.remove(i);
                    i--;
                    size--;
                }
                cursor.close();
                taskDetailLeftInfo.setIsEdit(1);
            }
        }
        db.close();
    }

    public synchronized boolean insertOfflinedownload(String username, String projectid, String storeid, String packageid,
                                                      String taskid, String url) {
        if (TextUtils.isEmpty(packageid) || "null".equals(packageid)) {
            packageid = "";
        }
        File downfile = FileCache.getDirForDownload(MyApplication.getInstance(), username + "/" +
                projectid + storeid + packageid + taskid);
        int regionStart = url.lastIndexOf("/");
        if (regionStart != -1) {
            File sf = new File(downfile, url.substring(regionStart, url.length()));
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(OFFLINE_USERNAME, username);
            cv.put(OFFLINE_PROJECTID, projectid);
            cv.put(OFFLINE_STOREID, storeid);
            cv.put(OFFLINE_PACKAGEID, packageid);
            cv.put(OFFLINE_TASKID, taskid);
            cv.put(OFFLINEDOWNLOAD_IS_DOWN, 0);
            cv.put(OFFLINEDOWNLOAD_URL, url);
            cv.put(OFFLINEDOWNLOAD_PATH, sf.getPath());
            long index = db.insert(TABLENAME_OFFLINEDOWNLOAD, null, cv);
            db.close();
            return index != -1;
        }
        return false;
    }

    public synchronized boolean isHadDownList() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_OFFLINEDOWNLOAD, null, OFFLINEDOWNLOAD_IS_DOWN + " is 0", null, null, null, null);
        boolean r = cursor.moveToFirst();
        cursor.close();
        db.close();
        return r;
    }

    public synchronized ArrayList<DownloadDataInfo> getDownloadList() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<DownloadDataInfo> list = new ArrayList<>();
        Cursor cursor = db.query(TABLENAME_OFFLINEDOWNLOAD, null, OFFLINEDOWNLOAD_IS_DOWN + " is 0", null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                DownloadDataInfo downloadDataInfo = new DownloadDataInfo();
                downloadDataInfo.setUsername(cursor.getString(cursor.getColumnIndex(OFFLINE_USERNAME)));
                downloadDataInfo.setProjectid(cursor.getString(cursor.getColumnIndex(OFFLINE_PROJECTID)));
                downloadDataInfo.setStoreid(cursor.getString(cursor.getColumnIndex(OFFLINE_STOREID)));
                downloadDataInfo.setPackageid(cursor.getString(cursor.getColumnIndex(OFFLINE_PACKAGEID)));
                downloadDataInfo.setTaskid(cursor.getString(cursor.getColumnIndex(OFFLINE_TASKID)));
                downloadDataInfo.setUrl(cursor.getString(cursor.getColumnIndex(OFFLINEDOWNLOAD_URL)));
                downloadDataInfo.setPath(cursor.getString(cursor.getColumnIndex(OFFLINEDOWNLOAD_PATH)));
                list.add(downloadDataInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized String getDownPath(String name, String projectid, String storeid, String packageid, String taskid,
                                           String url) {
        SQLiteDatabase db = getWritableDatabase();
        String path = "";
        Cursor cursor = db.query(TABLENAME_OFFLINEDOWNLOAD, new String[]{OFFLINEDOWNLOAD_PATH}, OFFLINE_USERNAME + " is ? and " +
                OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ? and " +
                OFFLINE_PACKAGEID + " is ? and " + OFFLINE_TASKID + " is ? and " + OFFLINEDOWNLOAD_URL + " is ?", new
                String[]{name,
                projectid, storeid, packageid, taskid, url}, null, null, null);
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(OFFLINEDOWNLOAD_PATH));
        }
        cursor.close();
        db.close();
        return path;
    }

    public synchronized void removeDownloadData(String username, String projectid, String storeid, String packageid,
                                                String taskid, String url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(OFFLINEDOWNLOAD_IS_DOWN, 1);
        db.update(TABLENAME_OFFLINEDOWNLOAD, cv, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                        OFFLINE_STOREID + " is ? and " + OFFLINE_PACKAGEID + " is ? and " +
                        OFFLINE_TASKID + " is ? and " + OFFLINEDOWNLOAD_URL + " is ?",
                new String[]{username, projectid, storeid, packageid, taskid, url});
        db.close();
    }

    public synchronized void deleteTraffic(String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_TRAFFICSUM, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{username, projectid, storeid});
        db.close();
    }

    public synchronized void upTrafficSum(String name, String projectid, String projectname, String storeid, String storename,
                                          long size) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_TRAFFICSUM, null, OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ? and " +
                OFFLINE_STOREID + " is ?", new String[]{name, projectid, storeid}, null, null, null);
        long oldSize = 0;
        if (cursor.moveToFirst()) {
            oldSize = StringToLong(cursor.getString(cursor.getColumnIndex(TRAFFICSUM_SUM)));
        }
        cursor.close();
        if (TextUtils.isEmpty(name)) {
            name = "";
        }
        if (TextUtils.isEmpty(projectid)) {
            projectid = "";
        }
        if (TextUtils.isEmpty(projectname)) {
            projectname = "";
        }
        if (TextUtils.isEmpty(storeid)) {
            storeid = "";
        }
        if (TextUtils.isEmpty(storename)) {
            storename = "";
        }
        ContentValues cv = new ContentValues();
        cv.put(OFFLINE_USERNAME, name);
        cv.put(OFFLINE_PROJECTID, projectid);
        cv.put(OFFLINE_PROJECTNAME, projectname);
        cv.put(OFFLINE_STOREID, storeid);
        cv.put(OFFLINE_STORENAME, storename);
        cv.put(TRAFFICSUM_SUM, oldSize + size + "");
        db.insert(TABLENAME_TRAFFICSUM, null, cv);
        db.close();
    }

    public synchronized ArrayList<TrafficInfo> getTrafficForProject(String username) {
        ArrayList<TrafficInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_TRAFFICSUM, new String[]{OFFLINE_PROJECTID, OFFLINE_PROJECTNAME, TRAFFICSUM_SUM},
                OFFLINE_USERNAME + " is ?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String projectid = cursor.getString(cursor.getColumnIndex(OFFLINE_PROJECTID));
                TrafficInfo trafficInfo = null;
                boolean isContinue = false;
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    trafficInfo = list.get(i);
                    if (trafficInfo.getId().equals(projectid)) {
                        isContinue = true;
                        break;
                    }
                }
                if (!isContinue) {
                    trafficInfo = new TrafficInfo();
                }
//                Cursor cursor1 = db.query(TABLENAME_TRAFFICSUM, new String[]{TRAFFICSUM_SUM},
//                        OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ?", new String[]{username, projectid}, null,
//                        null, null);
//                long sum = 0;
//                if (cursor1.moveToFirst()) {
//                    do {
//                        sum += StringToLong(cursor1.getString(cursor1.getColumnIndex(TRAFFICSUM_SUM)));
//                        Tools.d("project sum:" + sum);
//                    } while (cursor1.moveToNext());
//                }
//                cursor1.close();
                long sum = StringToLong(cursor.getString(cursor.getColumnIndex(TRAFFICSUM_SUM)));
                if (!isContinue) {
                    trafficInfo.setId(projectid);
                    trafficInfo.setName(cursor.getString(cursor.getColumnIndex(OFFLINE_PROJECTNAME)));
                    trafficInfo.setSize(sum);
                    list.add(trafficInfo);
                } else {
                    sum += trafficInfo.getSize();
                    trafficInfo.setSize(sum);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized ArrayList<TrafficInfo> getTrafficForStore(String username, String projectid) {
        if (projectid == null) {
            projectid = "";
        }
        ArrayList<TrafficInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_TRAFFICSUM, new String[]{OFFLINE_STOREID, OFFLINE_STORENAME, TRAFFICSUM_SUM},
                OFFLINE_USERNAME + " is ? and " + OFFLINE_PROJECTID + " is ?", new String[]{username, projectid}, null, null,
                null);
        if (cursor.moveToFirst()) {
            do {
                String storeid = cursor.getString(cursor.getColumnIndex(OFFLINE_STOREID));
                TrafficInfo trafficInfo = null;
                boolean isContinue = false;
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    trafficInfo = list.get(i);
                    if (trafficInfo.getId().equals(storeid)) {
                        isContinue = true;
                        break;
                    }
                }
                if (!isContinue) {
                    trafficInfo = new TrafficInfo();
                }
//                Cursor cursor1 = db.query(TABLENAME_TRAFFICSUM, new String[]{TRAFFICSUM_SUM}, OFFLINE_USERNAME + " is ? and " +
//                        OFFLINE_PROJECTID + " is ? and " + OFFLINE_STOREID + " is ?", new String[]{username, projectid,
//                        storeid}, null, null, null);
//                if (cursor1.moveToFirst()) {
//                    do {
//                        sum += StringToLong(cursor1.getString(cursor1.getColumnIndex(TRAFFICSUM_SUM)));
//                        Tools.d("store sum:" + sum);
//                    } while (cursor1.moveToNext());
//                }
//                cursor1.close();
                long sum = StringToLong(cursor.getString(cursor.getColumnIndex(TRAFFICSUM_SUM)));
                if (!isContinue) {
                    trafficInfo.setId(storeid);
                    trafficInfo.setName(cursor.getString(cursor.getColumnIndex(OFFLINE_STORENAME)));
                    trafficInfo.setSize(sum);
                    list.add(trafficInfo);
                } else {
                    sum += trafficInfo.getSize();
                    trafficInfo.setSize(sum);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    private long StringToLong(String string) {
        if (TextUtils.isEmpty(string)) return 0;
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            return 0;
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            Version2(db);
            Version3(db);
            Version4(db);
        } else if (oldVersion == 2) {
            Version3(db);
            Version4(db);
        } else if (oldVersion == 3) {
            Version4(db);
        }
    }

    private void Version2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_IS_WATERMARK + " INTEGER default 0");
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_CODE + " TEXT");
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_BRAND + " TEXT");
    }

    private void Version3(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_OUTLET_BATCH + " TEXT default '1'");
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_P_BATCH + " TEXT default '1'");
    }

    private void Version4(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_IS_PACKAGE_TASK + " INTEGER default 0");
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_INVALID_TYPE + " TEXT default '1'");
        db.execSQL("ALTER TABLE " + TABLENAME_OFFLINE + " ADD " + OFFLINE_IS_TASKPHOTO + " TEXT default '1'");
        db.execSQL("create table if not exists " + TABLENAME_OFFLINEOUTLETNOTE + "(id INTEGER PRIMARY KEY," +
                OFFLINE_USERNAME + " TEXT," + OFFLINE_PROJECTID + " TEXT," + OFFLINE_STOREID + " TEXT," +
                OFFLINE_OUTLETNOTE + " TEXT" + ")");
    }
}
