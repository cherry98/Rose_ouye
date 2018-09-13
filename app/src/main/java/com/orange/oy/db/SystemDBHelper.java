package com.orange.oy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.orange.oy.base.Tools;
import com.orange.oy.info.SystemMessageInfo;
import com.orange.oy.info.WebpagetaskDBInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.orange.oy.R.id.code;

/**
 * 系统消息数据库
 */
public class SystemDBHelper extends SQLiteOpenHelper {
    private static final String SystemDBNAME = "orangesystemmesdb";//数据库名

    public SystemDBHelper(Context context) {
        super(context, SystemDBNAME, null, 7);
    }

    private static final String TABLENAME_SYSTEMMES = "tablename_systemmes";
    private static final String USER = "user_mobile";
    private static final String SYSTEM_CODE = "system_code";//2：分配任务、3：执行完成/资料已回收
    private static final String SYSTEM_TITLE = "system_title";
    private static final String SYSTEM_MESSAGE = "system_message";
    private static final String SYSTEM_MESSAGE2 = "system_message2";
    private static final String SYSTEM_TIME = "system_time";

    /****************************
     * 相册表
     ***********************************/
    private static final String TABLENAME_PICTUREINFO = "tablename_pictureinfo";//自定义相册
    private static final String Picture_OriginalPath = "picture_originalpath";//图片原文件路径
    private static final String Picture_ThumbnailPath = "picture_thumbnailpath";//缩略图路径
    private static final String Picture_Time = "picture_time";//拍摄时间
    private static final String Picture_Address = "picture_address";//拍摄地址
    private static final String Picture_StoreSum = "picture_storesum";//网点编号
    private static final String Picture_Username = "picture_username";//账号
    private static final String Picture_ProjectId = "picture_projectid";//项目id
    private static final String Picture_StoreId = "picture_storeid";//网点id
    private static final String Picture_PacketId = "picture_packetid";//任务包id
    private static final String Picture_TaskId = "picture_taskid";//任务id
    //version 4
    private static final String Picture_Iswater = "picture_iswater";//是否添加水印，1：是；0：否
    //照片状态，0：网点级别照片，1：私有照片（自动挂），2：私有照片（非自动挂）,3：已和上传任务关联照片（不可见）,4：补做的照片（重做将状态变更为3）
    private static final String Picture_State = "picture_state";
    //version 5
    private static final String Picture_FoucesPath = "picture_foucespath";//取证图路径
    //version 6
    private static final String Picture_Longitude = "picture_longitude";//拍摄经度
    private static final String Picture_Latitude = "picture_latitude";//拍摄纬度

    /****************************
     * 应用设置表
     ***********************************/
    private static final String TABLENAME_SYSTEMSETTING = "tablename_systemsetting";//应用设置
    private static final String SETTING_NETWORK = "setting_network";//1:wifi和234G，2：wifi，3：234G

    /****************************
     * 体验任务截图数据库
     ***********************************/
    private static final String TABLENAME_WEBPAGETASKPHOTO = "tablename_webpagetaskphoto";
    private static final String WEBPAGETASK_PROJECTID = "webpagetask_projectid";
    private static final String WEBPAGETASK_STOREID = "webpagetask_storeid";//网点id
    private static final String WEBPAGETASK_TASKID = "webpagetask_taskid";//任务id
    private static final String WEBPAGETASK_TASKBATH = "webpagetask_taskbath";//任务批次
    private static final String WEBPAGETASK_USERNAME = "webpagetask_username";//账号
    private static final String WEBPAGETASK_COMMENT = "webpagetask_comment";//评论
    private static final String WEBPAGETASK_STATE = "webpagetask_state";//评论状态
    private static final String WEBPAGETASK_WEBNAME = "webpagetask_webname";//网页名称
    private static final String WEBPAGETASK_WEBURL = "webpagetask_weburl";//网页地址
    private static final String WEBPAGETASK_PATH = "webpagetask_path";//图片本地地址
    private static final String WEBPAGETASK_CREATETIME = "webpagetask_createtime";//创建时间
    private static final String WEBPAGETASK_ISPRAISE = "webpagetask_ispraise";//是否赞过

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_SYSTEMMES + "(id INTEGER PRIMARY KEY," + SYSTEM_TITLE +
                " TEXT," + SYSTEM_CODE + " TEXT," + USER + " TEXT," + SYSTEM_MESSAGE2 + " TEXT," + SYSTEM_TIME + " " +
                "TEXT," + SYSTEM_MESSAGE + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_PICTUREINFO + "(id INTEGER PRIMARY KEY," + Picture_OriginalPath +
                " TEXT," + Picture_ThumbnailPath + " TEXT," + Picture_Time + " TEXT," +
                Picture_StoreSum + " TEXT," + Picture_Username + " TEXT," + Picture_ProjectId + " TEXT," +
                Picture_StoreId + " TEXT," + Picture_PacketId + " TEXT DEFAULT ''," +
                Picture_TaskId + " TEXT DEFAULT ''," + Picture_Address + " TEXT," + Picture_Longitude + " TEXT," + Picture_Latitude + " TEXT," + Picture_State + " INTEGER DEFAULT 0,"
                + Picture_Iswater + " INTEGER DEFAULT 0," + Picture_FoucesPath + " TEXT" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_SYSTEMSETTING + "(id INTEGER PRIMARY KEY," +
                "_id INTEGER DEFAULT 1," + SETTING_NETWORK + " INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_WEBPAGETASKPHOTO + "(id INTEGER PRIMARY KEY," +
                WEBPAGETASK_PROJECTID + " TEXT," + WEBPAGETASK_STOREID + " TEXT," + WEBPAGETASK_TASKID + " TEXT," +
                WEBPAGETASK_TASKBATH + " TEXT," + WEBPAGETASK_USERNAME + " TEXT," + WEBPAGETASK_COMMENT + " TEXT," +
                WEBPAGETASK_STATE + " TEXT," + WEBPAGETASK_WEBNAME + " TEXT," + WEBPAGETASK_PATH + " TEXT," +
                WEBPAGETASK_CREATETIME + " TEXT," + WEBPAGETASK_ISPRAISE + " TEXT," + WEBPAGETASK_WEBURL + " TEXT" + ")");
    }

    /**
     * 插入体验任务截图数据
     */
    public synchronized boolean insertWebpagephoto(String projectid, String storeid, String taskid, String taskbath,
                                                   String username, WebpagetaskDBInfo webpagetaskDBInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(WEBPAGETASK_PROJECTID, projectid);
        cv.put(WEBPAGETASK_STOREID, storeid);
        cv.put(WEBPAGETASK_TASKID, taskid);
        cv.put(WEBPAGETASK_TASKBATH, taskbath);
        cv.put(WEBPAGETASK_USERNAME, username);
        cv.put(WEBPAGETASK_WEBNAME, webpagetaskDBInfo.getWebName());
        cv.put(WEBPAGETASK_WEBURL, webpagetaskDBInfo.getWebUrl());
        cv.put(WEBPAGETASK_COMMENT, webpagetaskDBInfo.getCommentTxt());
        cv.put(WEBPAGETASK_STATE, webpagetaskDBInfo.getCommentState());
        cv.put(WEBPAGETASK_PATH, webpagetaskDBInfo.getPath());
        cv.put(WEBPAGETASK_CREATETIME, webpagetaskDBInfo.getCreatetime());
        cv.put(WEBPAGETASK_ISPRAISE, webpagetaskDBInfo.getIspraise());
        long index = db.insert(TABLENAME_WEBPAGETASKPHOTO, null, cv);
        db.close();
        return index > -1;
    }

    /**
     * 获取网页截图对应的评论与评论类型
     */
    public synchronized WebpagetaskDBInfo getWebpageInfo(String projectid, String storeid, String taskid, String taskbath,
                                                         String username, String path) {
        WebpagetaskDBInfo webpagetaskDBInfo = new WebpagetaskDBInfo();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_WEBPAGETASKPHOTO,
                null
                , WEBPAGETASK_PROJECTID + " is ? and " + WEBPAGETASK_STOREID + " is ? and " + WEBPAGETASK_TASKID + " is ? and " +
                        WEBPAGETASK_TASKBATH + " is ? and " + WEBPAGETASK_USERNAME + " is ? and " +
                        WEBPAGETASK_PATH + " is ?",
                new String[]{projectid, storeid, taskid, taskbath, username, path}, null, null, null);
        if (cursor.moveToFirst()) {
            webpagetaskDBInfo.setWebName(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_WEBNAME)));
            webpagetaskDBInfo.setWebUrl(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_WEBURL)));
            webpagetaskDBInfo.setCommentState(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_STATE)));
            webpagetaskDBInfo.setCommentTxt(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_COMMENT)));
            webpagetaskDBInfo.setCreatetime(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_CREATETIME)));
            webpagetaskDBInfo.setIspraise(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_ISPRAISE)));
        }
        cursor.close();
        db.close();
        return webpagetaskDBInfo;
    }

    /**
     * 根据projectid...等和本地路径修改评论与评论类型
     */
    public synchronized boolean upWebpagephotoForComment(String projectid, String storeid, String taskid, String taskbath,
                                                         String username, WebpagetaskDBInfo webpagetaskDBInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(WEBPAGETASK_COMMENT, webpagetaskDBInfo.getCommentTxt());
        cv.put(WEBPAGETASK_STATE, webpagetaskDBInfo.getCommentState());
        long index = db.update(TABLENAME_WEBPAGETASKPHOTO, cv, WEBPAGETASK_PROJECTID + " is ? and " +
                        WEBPAGETASK_STOREID + " is ? and " + WEBPAGETASK_TASKID + " is ? and " + WEBPAGETASK_TASKBATH + " is ? and " +
                        WEBPAGETASK_USERNAME + " is ? and " + WEBPAGETASK_PATH + " is ?",
                new String[]{projectid, storeid, taskid, taskbath, username, webpagetaskDBInfo.getPath()});
        db.close();
        return index > -1;
    }

    /**
     * 根据projectid...等和本地路径修改评论赞状态
     */
    public synchronized boolean upWebpagephotoForPraise(String projectid, String storeid, String taskid, String taskbath,
                                                        String username, WebpagetaskDBInfo webpagetaskDBInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(WEBPAGETASK_ISPRAISE, webpagetaskDBInfo.getIspraise());
        long index = db.update(TABLENAME_WEBPAGETASKPHOTO, cv, WEBPAGETASK_PROJECTID + " is ? and " +
                        WEBPAGETASK_STOREID + " is ? and " + WEBPAGETASK_TASKID + " is ? and " + WEBPAGETASK_TASKBATH + " is ? and " +
                        WEBPAGETASK_USERNAME + " is ? and " + WEBPAGETASK_PATH + " is ?",
                new String[]{projectid, storeid, taskid, taskbath, username, webpagetaskDBInfo.getPath()});
        db.close();
        return index > -1;
    }

    /**
     * 获取指定体验任务的截图列表
     */
    public synchronized ArrayList<WebpagetaskDBInfo> getWebpagephoto(String projectid, String storeid, String taskid,
                                                                     String taskbath, String username) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<WebpagetaskDBInfo> list = new ArrayList<>();
        Cursor cursor = db.query(TABLENAME_WEBPAGETASKPHOTO,
                null
                , WEBPAGETASK_PROJECTID + " is ? and " + WEBPAGETASK_STOREID + " is ? and " + WEBPAGETASK_TASKID + " is ? and " +
                        WEBPAGETASK_TASKBATH + " is ? and " + WEBPAGETASK_USERNAME + " is ?",
                new String[]{projectid, storeid, taskid, taskbath, username}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                WebpagetaskDBInfo webpagetaskDBInfo = new WebpagetaskDBInfo();
                webpagetaskDBInfo.setWebName(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_WEBNAME)));
                webpagetaskDBInfo.setCommentState(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_STATE)));
                webpagetaskDBInfo.setCommentTxt(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_COMMENT)));
                webpagetaskDBInfo.setPath(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_PATH)));
                webpagetaskDBInfo.setWebUrl(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_WEBURL)));
                webpagetaskDBInfo.setCreatetime(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_CREATETIME)));
                webpagetaskDBInfo.setIspraise(cursor.getString(cursor.getColumnIndex(WEBPAGETASK_ISPRAISE)));
                list.add(webpagetaskDBInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 获取指定体验任务的截图数量
     */
    public synchronized int getWebpagephotoCount(String projectid, String storeid, String taskid,
                                                 String taskbath, String username) {
        SQLiteDatabase db = getWritableDatabase();
        int value = 0;
        Cursor cursor = db.query(TABLENAME_WEBPAGETASKPHOTO,
                new String[]{WEBPAGETASK_PATH}
                , WEBPAGETASK_PROJECTID + " is ? and " + WEBPAGETASK_STOREID + " is ? and " + WEBPAGETASK_TASKID + " is ? and " +
                        WEBPAGETASK_TASKBATH + " is ? and " + WEBPAGETASK_USERNAME + " is ?",
                new String[]{projectid, storeid, taskid, taskbath, username}, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getCount();
        }
        cursor.close();
        db.close();
        return value;
    }

    /**
     * 删除网页体验截图数据
     */
    public synchronized void deleteWebpagephoto(String projectid, String storeid, String taskid, String taskbath,
                                                String username, String path) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_WEBPAGETASKPHOTO, WEBPAGETASK_PROJECTID + " = ? and " + WEBPAGETASK_STOREID + " = ? and " +
                        WEBPAGETASK_TASKID + " = ? and " + WEBPAGETASK_TASKBATH + " = ? and " +
                        WEBPAGETASK_USERNAME + " = ? and " + WEBPAGETASK_PATH + " = ?",
                new String[]{projectid, storeid, taskid, taskbath, username, path});
        db.close();
    }

    public synchronized void deleteWebpagephoto(String projectid, String storeid, String taskid, String taskbath,
                                                String username) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_WEBPAGETASKPHOTO, WEBPAGETASK_PROJECTID + " = ? and " + WEBPAGETASK_STOREID + " = ? and " +
                        WEBPAGETASK_TASKID + " = ? and " + WEBPAGETASK_TASKBATH + " = ? and " +
                        WEBPAGETASK_USERNAME + " = ?",
                new String[]{projectid, storeid, taskid, taskbath, username});
        db.close();
    }

    public synchronized ArrayList<SystemMessageInfo> getAll(String usermobile) {
        ArrayList<SystemMessageInfo> list = new ArrayList<>();
        if (TextUtils.isEmpty(usermobile)) return list;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLENAME_SYSTEMMES + " where " + USER + " = '" + usermobile +
                "' order by id desc", null);
        if (cursor.moveToFirst()) {
            do {
                SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                systemMessageInfo.setId(cursor.getInt(cursor.getColumnIndex("id")) + "");
                systemMessageInfo.setCode(cursor.getString(cursor.getColumnIndex(SYSTEM_CODE)));
                systemMessageInfo.setTitle(cursor.getString(cursor.getColumnIndex(SYSTEM_TITLE)));
                systemMessageInfo.setMessage(cursor.getString(cursor.getColumnIndex(SYSTEM_MESSAGE)));
                systemMessageInfo.setMessage2(cursor.getString(cursor.getColumnIndex(SYSTEM_MESSAGE2)));
                systemMessageInfo.setTime(cursor.getString(cursor.getColumnIndex(SYSTEM_TIME)));
                list.add(systemMessageInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }


    public synchronized ArrayList<SystemMessageInfo> getAllCode(String usermobile, String code) {
        ArrayList<SystemMessageInfo> list = new ArrayList<>();
        if (TextUtils.isEmpty(usermobile)) return list;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLENAME_SYSTEMMES + " where " + USER + " = '" + usermobile + "' and " + SYSTEM_CODE + " = '" + code +
                "' order by id desc", null);
        if (cursor.moveToFirst()) {
            do {
                SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                systemMessageInfo.setId(cursor.getInt(cursor.getColumnIndex("id")) + "");
                systemMessageInfo.setCode(cursor.getString(cursor.getColumnIndex(SYSTEM_CODE)));
                systemMessageInfo.setTitle(cursor.getString(cursor.getColumnIndex(SYSTEM_TITLE)));
                systemMessageInfo.setMessage(cursor.getString(cursor.getColumnIndex(SYSTEM_MESSAGE)));
                systemMessageInfo.setMessage2(cursor.getString(cursor.getColumnIndex(SYSTEM_MESSAGE2)));
                systemMessageInfo.setTime(cursor.getString(cursor.getColumnIndex(SYSTEM_TIME)));
                list.add(systemMessageInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized void deleteMessage(String id) {
        Tools.d(id + "");
        SQLiteDatabase db = getWritableDatabase();
        long index = db.delete(TABLENAME_SYSTEMMES, "id = ?", new String[]{id});
        Tools.d(id + "-----------------" + index);
        db.close();
    }

    public synchronized void addSystemMessage(SystemMessageInfo systemMessageInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SYSTEM_CODE, systemMessageInfo.getCode());
        cv.put(SYSTEM_TITLE, systemMessageInfo.getTitle());
        cv.put(USER, systemMessageInfo.getUsermobile());
        cv.put(SYSTEM_MESSAGE, systemMessageInfo.getMessage());
        cv.put(SYSTEM_MESSAGE2, systemMessageInfo.getMessage2());
        cv.put(SYSTEM_TIME, systemMessageInfo.getTime());
        db.insert(TABLENAME_SYSTEMMES, null, cv);
        db.close();
    }

    public synchronized ArrayList<String> getPictureThumbnail(String username, String project, String storeid, String packetid,
                                                              String taskid) {
        ArrayList<String> list = new ArrayList<>();
        if (taskid == null) {
            return list;
        }
        SQLiteDatabase db = getWritableDatabase();
        //查询网点级别照片
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath}, Picture_Username + " is ? and " +
                        Picture_ProjectId + " is ? and " + Picture_StoreId + " is ? and " + Picture_State + " is 0",
                new String[]{username, project, storeid}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(Picture_ThumbnailPath)));
            } while (cursor.moveToNext());
        }
        //查询私有照片
        String where = Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " + Picture_StoreId + " is ? and " +
                Picture_TaskId + " is ?";
        String[] wheres;
        if (!TextUtils.isEmpty(packetid)) {
            where = where + " and " + Picture_PacketId + " is ?";
            wheres = new String[]{username, project, storeid, taskid, packetid};
        } else {
            where = where + " and " + Picture_PacketId + " is ''";
            wheres = new String[]{username, project, storeid, taskid};
        }
        cursor.close();
        cursor = null;
        cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath}, where + " and " + Picture_State + " is 2"
                , wheres, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(Picture_ThumbnailPath)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 把照片状态更新为2
     *
     * @param thumbnailPath 缩略图路径
     */
    public synchronized void updataStateTo2(String[] thumbnailPath) {
        if (thumbnailPath == null) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        for (String temp : thumbnailPath) {
            if (temp == null) {
                continue;
            }
            ContentValues cv = new ContentValues();
            cv.put(Picture_State, 2);
            db.update(TABLENAME_PICTUREINFO, cv, Picture_ThumbnailPath + " is ?", new String[]{temp});
        }
        db.close();
    }

    /**
     * 把照片状态更新为1
     *
     * @param thumbnailPath 缩略图路径
     */
    public synchronized void updataStateTo1(String[] thumbnailPath) {
        if (thumbnailPath == null) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        for (String temp : thumbnailPath) {
            if (temp == null) {
                continue;
            }
            ContentValues cv = new ContentValues();
            cv.put(Picture_State, 1);
            db.update(TABLENAME_PICTUREINFO, cv, Picture_ThumbnailPath + " is ?", new String[]{temp});
        }
        db.close();
    }

    /**
     * 把照片状态更新为3
     *
     * @param thumbnailPath 缩略图路径
     */
    public synchronized void updataStateTo3(String[] thumbnailPath) {
        if (thumbnailPath == null) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        for (String temp : thumbnailPath) {
            if (temp == null) {
                continue;
            }
            ContentValues cv = new ContentValues();
            cv.put(Picture_State, 3);
            db.update(TABLENAME_PICTUREINFO, cv, Picture_ThumbnailPath + " is ?", new String[]{temp});
        }
        db.close();
    }

    /**
     * 把照片状态更新为3
     *
     * @param oPath 原图路径
     */
    public synchronized void updataStateOPathTo3(String[] oPath) {
        if (oPath == null) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        for (String temp : oPath) {
            if (temp == null) {
                continue;
            }
            ContentValues cv = new ContentValues();
            cv.put(Picture_State, 3);
            db.update(TABLENAME_PICTUREINFO, cv, Picture_ThumbnailPath + " is ?", new String[]{temp});
        }
        db.close();
    }

    /**
     * 把照片状态更新为3
     *
     * @param oPath 原图路径
     */
    public synchronized void updataStateOPathTo3_2(String oPath) {
        if (oPath == null) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Picture_State, 3);
        db.update(TABLENAME_PICTUREINFO, cv, Picture_ThumbnailPath + " is ?", new String[]{oPath});
        db.close();
    }

    /**
     * 将非网点级照片状态变更为3
     */
    public synchronized void updataAllstateTo3(String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Picture_State, 3);
        db.update(TABLENAME_PICTUREINFO, cv, Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " +
                Picture_StoreId + " is ? ", new String[]{username, projectid, storeid});
        db.close();
    }

    /**
     * 将属于指定任务的图片状态更改为3
     *
     * @param username
     * @param projectid
     * @param storeid
     * @param packageid
     * @param taskid
     */
    public synchronized void updataTaskstateTo3(String username, String projectid, String storeid, String packageid,
                                                String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String where = Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " + Picture_StoreId + " is ? and " +
                Picture_TaskId + " is ?";
        String[] wheres;
        if (TextUtils.isEmpty(packageid)) {
            where = where + " and " + Picture_PacketId + " is ''";
            wheres = new String[]{username, projectid, storeid, taskid};
        } else {
            where = where + " and " + Picture_PacketId + " is ?";
            wheres = new String[]{username, projectid, storeid, taskid, packageid};
        }
        ContentValues cv = new ContentValues();
        cv.put(Picture_State, 3);
        db.update(TABLENAME_PICTUREINFO, cv, where, wheres);
        db.close();
    }

    /**
     * 获取指定任务图片的补拍图(state:4)数量
     *
     * @param username
     * @param projectid
     * @param storeid
     * @param packageid
     * @param taskid
     * @return
     */
    public synchronized int getPhotoNumFortaskstate4(String username, String projectid, String storeid, String packageid,
                                                     String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String where = Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " + Picture_StoreId + " is ? and " +
                Picture_TaskId + " is ?";
        String[] wheres;
        if (TextUtils.isEmpty(packageid)) {
            where = where + " and " + Picture_PacketId + " is ''";
            wheres = new String[]{username, projectid, storeid, taskid};
        } else {
            where = where + " and " + Picture_PacketId + " is ?";
            wheres = new String[]{username, projectid, storeid, taskid, packageid};
        }
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath}, where + " and " +
                Picture_State + " is 4", wheres, null, null, null);
        int index = cursor.getCount();
        cursor.close();
        db.close();
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    /**
     * 任务页直接挂照片用
     *
     * @param username
     * @param project
     * @param storeid
     * @param packetid
     * @param taskid
     * @return
     */
    public synchronized ArrayList<String> getPictureThumbnailForTask(String username, String project, String storeid,
                                                                     String packetid, String taskid) {
        ArrayList<String> list = new ArrayList<>();
        if (taskid == null) {
            return list;
        }
        SQLiteDatabase db = getWritableDatabase();
        String where = Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " + Picture_StoreId + " is ? and " +
                Picture_TaskId + " is ?";
        String[] wheres;
        if (!TextUtils.isEmpty(packetid)) {
            where = where + " and " + Picture_PacketId + " is ?";
            wheres = new String[]{username, project, storeid, taskid, packetid};
        } else {
            where = where + " and " + Picture_PacketId + " is ''";
            wheres = new String[]{username, project, storeid, taskid};
        }
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath}, where + " and " + Picture_State +
                        " is 1",
                wheres, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(Picture_ThumbnailPath)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized int getPictureNumForprivate(String username, String project, String storeid, String packetid,
                                                    String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String where = Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " + Picture_StoreId + " is ? and " +
                Picture_TaskId + " is ?";
        String[] wheres;
        if (!TextUtils.isEmpty(packetid)) {
            where = where + " and " + Picture_PacketId + " is ?";
            wheres = new String[]{username, project, storeid, taskid, packetid};
        } else {
            where = where + " and " + Picture_PacketId + " is ''";
            wheres = new String[]{username, project, storeid, taskid};
        }
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath}, where + " and " + Picture_State +
                        " is 1",
                wheres, null, null, null);
        int size = cursor.getCount();
        cursor.close();
        db.close();
        return size;
    }

    public synchronized ArrayList<String> getPictureThumbnail(String username, String project, String storeid) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String where = Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " + Picture_StoreId + " is ? and " +
                Picture_State + " is 0";
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath}, where,
                new String[]{username, project, storeid}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(Picture_ThumbnailPath)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized boolean insertPicture(String username, String projectid, String storeid, String storecode,
                                              String packageid, String taskid, String path, String path2, String time,
                                              String picture_Address, String longitude, String latitude) {
        return insertPicture(username, projectid, storeid, storecode, packageid, taskid, path, path2, time, picture_Address, longitude, latitude, 0);
    }

    public synchronized boolean insertPicture(String username, String projectid, String storeid, String storecode,
                                              String packageid, String taskid, String path, String path2, String time,
                                              String picture_Address, String longitude, String latitude, int state) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Picture_Username, username);
        cv.put(Picture_ProjectId, projectid);
        cv.put(Picture_StoreId, storeid);
        cv.put(Picture_StoreSum, storecode);
        if (!TextUtils.isEmpty(packageid)) {
            cv.put(Picture_PacketId, packageid);
        }
        if (!TextUtils.isEmpty(taskid)) {
            cv.put(Picture_TaskId, taskid);
        }
        cv.put(Picture_OriginalPath, path);
        cv.put(Picture_ThumbnailPath, path2);
        cv.put(Picture_Time, time);
        cv.put(Picture_Address, picture_Address);
        cv.put(Picture_Longitude, longitude);
        cv.put(Picture_Latitude, latitude);
        cv.put(Picture_State, state);
        long index = db.insert(TABLENAME_PICTUREINFO, null, cv);
        db.close();
        Tools.d("photo insert index:" + index + ",state:" + state);
        return index > 0;
    }

    /**
     * 用缩略图找原图
     *
     * @param thumbnailpath
     * @return
     */
    public synchronized String searchForOriginalpath(String thumbnailpath) {
        if (thumbnailpath == null) {
            return "";
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_OriginalPath},
                Picture_ThumbnailPath + " is ?", new String[]{thumbnailpath}, null, null, null);
        String path = "";
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(Picture_OriginalPath));
        }
        cursor.close();
        db.close();
        return path;
    }

    /**
     * 用原图找缩略图
     *
     * @param originalpath
     * @return
     */
    public synchronized String searchForThumbnailPath(String originalpath) {
        if (originalpath == null) {
            return "";
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath},
                Picture_OriginalPath + " is ?", new String[]{originalpath}, null, null, null);
        String path = "";
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(Picture_ThumbnailPath));
        }
        cursor.close();
        db.close();
        return path;
    }


    /**
     * 用原图找取证图
     *
     * @param original
     * @return
     */
    public synchronized String searchForFoucesPicturepath(String original) {
        if (original == null) {
            return "";
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_FoucesPath},
                Picture_OriginalPath + " is ?", new String[]{original}, null, null, null);
        String path = "";
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(Picture_FoucesPath));
        }
        cursor.close();
        db.close();
        return path;
    }

    /**
     * 在原图list item后自动拼接取证图，分隔符为&
     *
     * @return
     * @paramoriginal
     */
    public synchronized void searchForFoucesPicturepath(List<String> originals) {
        if (originals == null || originals.isEmpty()) {
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        int size = originals.size();
        String temp = "";
        Cursor cursor = null;
        for (int i = 0; i < size; i++) {
            temp = originals.get(i);
            cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_FoucesPath},
                    Picture_OriginalPath + " is ?", new String[]{temp}, null, null, null);
            String path = "";
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(Picture_FoucesPath));
            }
            cursor.close();
            if (!TextUtils.isEmpty(path)) {
                originals.remove(i);
                originals.add(i, temp + "&" + path);
            }
        }
        db.close();
    }

    public synchronized String searchForWatermark(String oPath) {
        if (oPath == null) {
            return "";
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_Time, Picture_Address, Picture_Longitude, Picture_Latitude},
                Picture_OriginalPath + " is ?", new String[]{oPath}, null, null, null);
        String watermark = "";
        if (cursor.moveToFirst()) {
            watermark = cursor.getString(cursor.getColumnIndex(Picture_Time)) + "\n"
                    + cursor.getString(cursor.getColumnIndex(Picture_Address)) + "\n"
                    + "经度：" + cursor.getString(cursor.getColumnIndex(Picture_Longitude)) + "纬度："
                    + cursor.getString(cursor.getColumnIndex(Picture_Latitude));
        }
        cursor.close();
        db.close();
        return watermark;
    }

    public synchronized boolean bindTaskForPicture(String oPath, String packetid, String taskid) {
        if (TextUtils.isEmpty(taskid)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        boolean isException = false;
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_PacketId, Picture_TaskId, Picture_State},
                Picture_OriginalPath + " is ?", new String[]{oPath}, null, null, null, null);
        String task_id;
        int state;
        if (cursor.moveToFirst()) {
            state = cursor.getInt(cursor.getColumnIndex(Picture_State));
            String packet_id = cursor.getString(cursor.getColumnIndex(Picture_PacketId));
            task_id = cursor.getString(cursor.getColumnIndex(Picture_TaskId));
            if (!TextUtils.isEmpty(packetid) && !TextUtils.isEmpty(packet_id) && !packet_id.equals(packetid)) {
                isException = true;
            } else if (TextUtils.isEmpty(packetid) && !TextUtils.isEmpty(packet_id)) {
                isException = true;
            }
            if (isException) {
                cursor.close();
                db.close();
                return false;
            } else {
                if (TextUtils.isEmpty(taskid)) {
                    isException = true;
                } else if (!TextUtils.isEmpty(task_id) && !taskid.equals(task_id)) {
                    isException = true;
                } else if (!TextUtils.isEmpty(task_id) && task_id.equals(taskid) && !(packet_id + "").equals(packetid)) {
                    isException = true;
                }
            }
            if (isException) {
                cursor.close();
                db.close();
                return false;
            }
            cursor.close();
        } else {
            return false;
        }
        if ((state == 1 || state == 4) && taskid.equals(task_id)) {
            db.close();
            return true;
        }
        if (!TextUtils.isEmpty(packetid)) {
            cv.put(Picture_PacketId, packetid);
        }
        if (!taskid.equals(task_id)) {
            cv.put(Picture_TaskId, taskid);
        }
        if (state != 1 && state != 4) {
            cv.put(Picture_State, 1);
        }
        long index = db.update(TABLENAME_PICTUREINFO, cv, Picture_OriginalPath + " is ?", new String[]{oPath});
        db.close();
        return index > 0;
    }

    public synchronized boolean isBindForPicture(String picture_OriginalPath) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_Iswater}, Picture_OriginalPath + " is ?", new
                String[]{picture_OriginalPath}, null, null, null, null);
        boolean result = cursor.moveToFirst();
        if (result) {
            int iswater = cursor.getInt(cursor.getColumnIndex(Picture_Iswater));
            if (iswater == 0) {
                result = false;
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public synchronized void updataIswater(String picture_OriginalPath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Picture_Iswater, 1);
        db.update(TABLENAME_PICTUREINFO, cv, Picture_OriginalPath + " is ?", new String[]{picture_OriginalPath});
        db.close();
    }

    /**
     * 将取证图片更新进图片表
     * picture_OriginalPath:原图路径
     */
    public synchronized void updataFoucePicture(String picture_OriginalPath, String foucesPicture) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Picture_FoucesPath, foucesPicture);
        db.update(TABLENAME_PICTUREINFO, cv, Picture_OriginalPath + " is ?", new String[]{picture_OriginalPath});
        db.close();
    }

    public synchronized void deletePicture(String path) {
        SQLiteDatabase db = getWritableDatabase();
        String tPath = null;
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_ThumbnailPath}, Picture_OriginalPath + " is ?",
                new String[]{path}, null, null, null);
        if (cursor.moveToFirst()) {
            tPath = cursor.getString(cursor.getColumnIndex(Picture_ThumbnailPath));
        }
        cursor.close();
        if (tPath != null) {
            File file = new File(tPath);
            if (file.exists()) {
                file.delete();
            }
        }
        db.delete(TABLENAME_PICTUREINFO, Picture_OriginalPath + " is ?", new String[]{path});
        db.close();
    }

    public synchronized void deletePictureForThum(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        SQLiteDatabase db = getWritableDatabase();
        String tPath = null;
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_OriginalPath}, Picture_ThumbnailPath + " is ?",
                new String[]{path}, null, null, null);
        if (cursor.moveToFirst()) {
            tPath = cursor.getString(cursor.getColumnIndex(Picture_OriginalPath));
        }
        cursor.close();
        if (tPath != null) {
            file = new File(tPath);
            if (file.exists()) {
                file.delete();
            }
        }
        db.delete(TABLENAME_PICTUREINFO, Picture_OriginalPath + " is ?", new String[]{path});
        db.close();
    }

    public synchronized void packPhotoUpload(Context context, String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        String oList = null;
        Cursor cursor = db.query(TABLENAME_PICTUREINFO, new String[]{Picture_OriginalPath, Picture_TaskId},
                Picture_Username + " is ? and " + Picture_ProjectId + " is ? and " + Picture_StoreId + " is ?"
                , new String[]{username, projectid, storeid}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(Picture_TaskId)))) {
                    String path = cursor.getString(cursor.getColumnIndex(Picture_OriginalPath));
                    if (isLegal(path)) {
                        if (TextUtils.isEmpty(oList)) {
                            oList = path;
                        } else {
                            oList = oList + "," + path;
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (!TextUtils.isEmpty(oList)) {
            UpdataDBHelper updataDBHelper = new UpdataDBHelper(context);
            updataDBHelper.addUpdataTask(username, projectid, null, null, null, storeid, null, null, null, "-4", null, null,
                    null, null, null, "-4-4" + projectid + storeid, null, null, oList, null, null, null, false, null,
                    null, true);
        }
    }

    private boolean isLegal(String path) {
        File file = new File(path);
        return file.exists() && file.length() > 51200;
    }

    public synchronized void insertSetting(int network) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_SYSTEMSETTING, new String[]{SETTING_NETWORK}, "_id is ?", new String[]{"1"}, null,
                null, null);
        ContentValues cv = new ContentValues();
        cv.put(SETTING_NETWORK, network);
        if (cursor.moveToFirst()) {
            long index = db.update(TABLENAME_SYSTEMSETTING, cv, "_id is ?", new String[]{"1"});
            Tools.d("network update--" + index);
        } else {
            long index = db.insert(TABLENAME_SYSTEMSETTING, null, cv);
            Tools.d("network insert--" + index);
        }
        cursor.close();
        db.close();
    }

    public synchronized int getNetworkSetting() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_SYSTEMSETTING, new String[]{SETTING_NETWORK}, "_id is ?", new String[]{"1"}, null,
                null, null);
        int flag = 1;
        if (cursor.moveToFirst()) {
            flag = cursor.getInt(cursor.getColumnIndex(SETTING_NETWORK));
        }
        cursor.close();
        db.close();
        return flag;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            Version2(db);
            Version3(db);
            Version4(db);
            Version5(db);
            Version6(db);
        } else if (oldVersion == 2) {
            Version3(db);
            Version4(db);
            Version5(db);
            Version6(db);
        } else if (oldVersion == 3) {
            Version4(db);
            Version5(db);
            Version6(db);
        } else if (oldVersion == 4) {
            Version5(db);
            Version6(db);
        } else if (oldVersion == 5) {
            Version6(db);
        } else if (oldVersion == 6) {
            onCreate(db);
        }
    }

    private void Version2(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_PICTUREINFO + "(id INTEGER PRIMARY KEY," + Picture_OriginalPath +
                " TEXT," + Picture_ThumbnailPath + " TEXT," + Picture_Time + " TEXT," +
                Picture_StoreSum + " TEXT," + Picture_Username + " TEXT," + Picture_ProjectId + " TEXT," +
                Picture_StoreId + " TEXT," + Picture_PacketId + " TEXT DEFAULT ''," +
                Picture_TaskId + " TEXT DEFAULT ''," + Picture_Address + " TEXT)");
    }

    private void Version3(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_SYSTEMSETTING + "(id INTEGER PRIMARY KEY," +
                "_id INTEGER DEFAULT 1," + SETTING_NETWORK + " INTEGER DEFAULT 1)");
    }

    private void Version4(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLENAME_PICTUREINFO + " ADD " + Picture_State + " INTEGER DEFAULT 0");
        db.execSQL("ALTER TABLE " + TABLENAME_PICTUREINFO + " ADD " + Picture_Iswater + " INTEGER DEFAULT 0");
    }

    private void Version5(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLENAME_PICTUREINFO + " ADD " + Picture_FoucesPath + " TEXT");
    }

    private void Version6(SQLiteDatabase db) {
        if (!checkColumnExist(db, TABLENAME_PICTUREINFO, Picture_Latitude)) {
            db.execSQL("ALTER TABLE " + TABLENAME_PICTUREINFO + " ADD " + Picture_Latitude + " TEXT");
            db.execSQL("ALTER TABLE " + TABLENAME_PICTUREINFO + " ADD " + Picture_Longitude + " TEXT");
        }
    }

    /**
     * 检查某表列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExist(SQLiteDatabase db, String tableName
            , String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0"
                    , null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }
}
