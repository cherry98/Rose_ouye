package com.orange.oy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.orange.oy.base.Tools;
import com.orange.oy.info.MyteamNewfdInfo;
import com.orange.oy.info.ShakephotoUpdataInfo;
import com.orange.oy.info.shakephoto.LocalPhotoInfo;
import com.orange.oy.info.shakephoto.ShakePhotoInfo;
import com.orange.oy.info.shakephoto.ShakePhotoInfo2;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * 应用基本信息记录数据库工具类
 */
public class AppDBHelper extends SQLiteOpenHelper {
    public static final String APPDBNAME = "orangeappdb";//数据库名

    public AppDBHelper(Context context) {
        super(context, APPDBNAME, null, 7);
    }

    public void onCreate(SQLiteDatabase db) {//在数据库第一次生产的时候会调用这个方法
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_LOGIN_NUMBER + "(id INTEGER PRIMARY KEY," +
                LOGIN_NUMBER_NAME + " TEXT," + LOGIN_NUMBER_TIME + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_MYTEAM_NEWFRIENDS + "(id INTEGER PRIMARY KEY," +
                MYTEAM_NEWFD_MYNUM + " TEXT," + MYTEAM_NEWFD_NAME + " TEXT," + MYTEAM_NEWFD_ID + " TEXT," +
                MYTEAM_NEWFD__ID + " TEXT," + MYTEAM_NEWFD_IMG + " TEXT," + MYTEAM_NEWFD_STATE + " INTEGER," +
                MYTEAM_NEWFD_TIME + " INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_PROJECT_ISSHOW + "(id INTEGER PRIMARY KEY," +
                ISSHOW_PROJECT_ID + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_PHOTOURL + "(id INTEGER PRIMARY KEY," +
                PHOTOURL_FILEPATH + " TEXT," + PHOTOURL_FILEPATH2 + " TEXT," + PHOTOURL_FILEURL + " TEXT," + PHOTOURL_PROJECTID + " TEXT,"
                + PHOTOURL_FILENUM + " TEXT," + PHOTOURL_STOREID + " TEXT," + PHOTOURL_FILESIZE + " TEXT," +
                PHOTOURL_USERNAME + " TEXT," + PHOTOURL_TASKID + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_DATAUPLOAD + "(id INTEGER PRIMARY KEY," +
                DATAUPLOAD_STOREID + " TEXT," + DATAUPLOAD_MODE + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_SHAKEPHOTO + "(id INTEGER PRIMARY KEY," + SHAKEPHOTO_FILEPATH
                + " TEXT," + SHAKEPHOTO_FILEPATH2 + " TEXT," + SHAKEPHOTO_AREA + " TEXT," + SHAKEPHOTO_TIME + " TEXT," +
                SHAKEPHOTO_PROVINCE + " TEXT," + SHAKEPHOTO_CITY + " TEXT," + SHAKEPHOTO_COUNTY + " TEXT," + SHAKEPHOTO_ADDRESS + " TEXT," +
                SHAKEPHOTO_LONGITUDE + " TEXT," + SHAKEPHOTO_LATITUDE + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME_SHAKEPHOTOUPDATA + "(id INTEGER PRIMARY KEY," + SSHAKEPHOTOUPDATA_PATH
                + " TEXT," + SHAKEPHOTOUPDATA_PARAMETER + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //当数据库需要升级的时候，android系统会主动的调用这个方法。当数据库需要升级的时候，android系统会主动的调用这个方法。
        Tools.d("oldVersion:" + oldVersion + "newVersion:" + newVersion);
        if (oldVersion < 4) {
            onCreate(db);
        } else if (oldVersion == 4) {
            db.execSQL("ALTER TABLE " + TABLENAME_PHOTOURL + " ADD " + PHOTOURL_USERNAME + " TEXT DEFAULT 'default'");
            db.execSQL("ALTER TABLE " + TABLENAME_PHOTOURL + " ADD " + PHOTOURL_FILESIZE + " TEXT ''");
            onCreate(db);
        } else if (oldVersion == 5) {
            onCreate(db);
        } else if (oldVersion == 6) {
            onCreate(db);
        }
    }

    /**********************************************
     * ************记录是否显示任务页面***************
     **********************************************/
    public static final String TABLENAME_PROJECT_ISSHOW = "projectishow";//记录是否显示任务介绍页面表
    public static final String ISSHOW_PROJECT_ID = "projectid";//记录用来区分的id

    public synchronized void addIsShow(String projectid) {//添加是否点击不再显示
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PROJECT_ISSHOW + " where " + ISSHOW_PROJECT_ID +
                " = '" + projectid + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            db.update(TABLENAME_PROJECT_ISSHOW, cv, ISSHOW_PROJECT_ID + " = ?", new String[]{projectid});
        } else {
            cv.put(ISSHOW_PROJECT_ID, projectid);
            db.insert(TABLENAME_PROJECT_ISSHOW, null, cv);
        }
        cursor.close();
        db.close();
    }

    public synchronized boolean getIsShow(String projectid) {//是否显示不再显示
        boolean isFirst = true;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PROJECT_ISSHOW + " where " + ISSHOW_PROJECT_ID +
                " = '" + projectid + "'";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            isFirst = false;
        }
        cursor.close();
        db.close();
        return isFirst;
    }

    /****************************
     * 根据storeid查询资料上传模式
     ***********************************/
    public static final String TABLENAME_DATAUPLOAD = "tablename_dataupload";
    public static final String DATAUPLOAD_STOREID = "storeid";//同一个网点下的storeid相同（唯一）
    public static final String DATAUPLOAD_MODE = "mode";//上传模式（1==继续上传，2==仅此任务，3==全部任务）

    public synchronized void addDataUploadRecord(String storeid, String mode) {//根据storeid存储上传模式
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_DATAUPLOAD + " where " + DATAUPLOAD_STOREID + " = '" + storeid + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            cv.put(DATAUPLOAD_MODE, mode);
            db.update(TABLENAME_DATAUPLOAD, cv, DATAUPLOAD_STOREID + " = ?", new String[]{storeid});
        } else {
            cv.put(DATAUPLOAD_STOREID, storeid);
            cv.put(DATAUPLOAD_MODE, mode);
            db.insert(TABLENAME_DATAUPLOAD, null, cv);
        }
        cursor.close();
        db.close();
    }

    public String getDataUploadMode(String storeid) {//根据storeid判断上传模式
        String mode = null;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_DATAUPLOAD + " where " + DATAUPLOAD_STOREID + " = '" + storeid + "'";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            mode = cursor.getString(cursor.getColumnIndex(DATAUPLOAD_MODE));
        }
        if (mode == null) {
            mode = "1";
        }
        cursor.close();
        db.close();
        return mode;
    }


    /****************************
     * 存储拍照任务URL表
     ***********************************/
    private static final String TABLENAME_PHOTOURL = "tablename_photourl";//拍照任务url记录表
    private static final String PHOTOURL_FILEURL = "fileurl";//文件url
    private static final String PHOTOURL_FILEPATH = "filepath";//文件本地地址（主键）
    private static final String PHOTOURL_FILEPATH2 = "filePath2";//文件本地地址（缩略图路径）
    private static final String PHOTOURL_FILENUM = "filenum";//文件数量
    private static final String PHOTOURL_PROJECTID = "projectid";
    private static final String PHOTOURL_STOREID = "storeid";
    private static final String PHOTOURL_TASKID = "taskid";
    /**
     * verstion 5
     */
    private static final String PHOTOURL_USERNAME = "file_username";
    private static final String PHOTOURL_FILESIZE = "file_size";//文件大小

    public synchronized boolean havPhotoUrlRecord(String username, String projectid, String storeid, String taskid,
                                                  String filePath, String filePath2) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_FILEPATH + " = '" + filePath + "'";
        Cursor cursor = db.rawQuery(select, null);
        boolean ishav = cursor.moveToFirst();
        cursor.close();
        return ishav;
    }

    public synchronized boolean addPhotoUrlRecord(String username, String projectid, String storeid, String taskid,
                                                  String filePath, String filePath2) {
        long in;
        Tools.d("filePath==:" + filePath);
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_FILEPATH + " = '" + filePath + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            cv.put(PHOTOURL_USERNAME, username);
            cv.put(PHOTOURL_PROJECTID, projectid);
            cv.put(PHOTOURL_STOREID, storeid);
            cv.put(PHOTOURL_TASKID, taskid);
            cv.put(PHOTOURL_FILEPATH2, filePath2);
            in = db.update(TABLENAME_PHOTOURL, cv, PHOTOURL_FILEPATH + " = ?", new String[]{filePath});
            Tools.d("update task...");
        } else {
            cv.put(PHOTOURL_USERNAME, username);
            cv.put(PHOTOURL_FILEPATH, filePath);
            cv.put(PHOTOURL_PROJECTID, projectid);
            cv.put(PHOTOURL_STOREID, storeid);
            cv.put(PHOTOURL_TASKID, taskid);
            cv.put(PHOTOURL_FILEPATH2, filePath2);
            cv.put(PHOTOURL_FILESIZE, new File(filePath).length() + "");
            in = db.insert(TABLENAME_PHOTOURL, null, cv);
            Tools.d("insert task ..." + in);
        }
        cursor.close();
        db.close();
        return in > 0;
    }

    public synchronized void setFileNum(String filePath, String fileNum) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_FILEPATH + " = '" + filePath +
                "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            cv.put(PHOTOURL_FILENUM, fileNum);
            db.update(TABLENAME_PHOTOURL, cv, PHOTOURL_FILEPATH + " = ?", new String[]{filePath});
        } else {
            cv.put(PHOTOURL_FILENUM, fileNum);
            db.insert(TABLENAME_PHOTOURL, null, cv);
        }
        cursor.close();
        db.close();
    }

    public synchronized void setPhotoUrl(String fileUrl, String filePath) {//上传完毕的url地址
        Tools.d("appdbupdata:" + fileUrl);
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_FILEPATH + " = '" + filePath + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            cv.put(PHOTOURL_FILEURL, fileUrl);
            db.update(TABLENAME_PHOTOURL, cv, PHOTOURL_FILEPATH + " = ?", new String[]{filePath});
        } else {
            cv.put(PHOTOURL_FILEURL, fileUrl);
            db.insert(TABLENAME_PHOTOURL, null, cv);
        }
        cursor.close();
        db.close();
    }

    public synchronized void deletePhotoUrl(String projectid, String storeid, String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        int r = db.delete(TABLENAME_PHOTOURL, PHOTOURL_PROJECTID + " is ? and " + PHOTOURL_STOREID + " is ? and " +
                PHOTOURL_TASKID + " is ?", new String[]{projectid, storeid, taskid});
        db.close();
        Tools.d("remove photourl:" + r);
    }

    public synchronized void deletePhotoFromPath2(String path) {
        SQLiteDatabase db = getWritableDatabase();
        int r = db.delete(TABLENAME_PHOTOURL, PHOTOURL_FILEPATH2 + " is ?", new String[]{path});
        db.close();
        Tools.d("remove 单张照片记录：" + r);
    }

    /**
     * @param username
     * @param projectid
     * @param storeid
     * @param taskid
     * @return 如果返回值为null，则数据库为空，如果返回值为""，则数据库不为空
     */
    public synchronized String getAllPhotoUrl(String username, String projectid, String storeid, String taskid) {//同一个任务的fileurl拼接
        String photoUrls = null;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_PROJECTID + " = '" + projectid +
                "' and " + PHOTOURL_STOREID + " = '" + storeid + "' and " + PHOTOURL_TASKID + " = '" + taskid + "' and (" +
                PHOTOURL_USERNAME + " = '" + username + "' or " + PHOTOURL_USERNAME + " = 'default')";
        Cursor cursor = db.rawQuery(select, null);
        Tools.d("行数：" + cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            photoUrls = "";
            do {
                String url = cursor.getString(cursor.getColumnIndex(PHOTOURL_FILEURL));
                if (url != null && !"null".equals(url)) {
                    if (TextUtils.isEmpty(photoUrls)) {
                        photoUrls = url;
                    } else {
                        photoUrls = photoUrls + "&&" + url;
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return photoUrls;
    }

    public synchronized int getAllRecordNumber(String username, String projectid, String storeid, String taskid) {//同一个任务下有几条数据
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_PROJECTID + " = '" + projectid +
                "' and " + PHOTOURL_STOREID + " = '" + storeid + "' and " + PHOTOURL_TASKID + " = '" + taskid + "' and (" +
                PHOTOURL_USERNAME + " = '" + username + "' or " + PHOTOURL_USERNAME + " = 'default')";
        Cursor cursor = db.rawQuery(select, null);
        Tools.d("记录条数：" + cursor.getCount() + "");
        cursor.close();
        db.close();
        return cursor.getCount();
    }

    public synchronized ArrayList<String> getTapePath(String username, String projectid, String storeid, String taskid) {//查找录音路径
        ArrayList<String> list = new ArrayList<>();
        String photoUrls = null;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_PROJECTID + " = '" + projectid +
                "' and " + PHOTOURL_STOREID + " = '" + storeid + "' and " + PHOTOURL_TASKID + " = '" + taskid + "' and (" +
                PHOTOURL_USERNAME + " = '" + username + "' or " + PHOTOURL_USERNAME + " = 'default')";
        Cursor cursor = db.rawQuery(select, null);
        Tools.d("行数：" + cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(cursor.getColumnIndex(PHOTOURL_FILEPATH));
                list.add(url);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    //查找同一个任务下的所有url
    public synchronized boolean getPhotoUrlIsCompete(String fileUrl, String projectid, String storeid, String taskid) {
        String[] paths = fileUrl.split("&&");
        Tools.d("新增拍照or视频数量：" + paths.length);
        int filenum = 0;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_PROJECTID + " = '" + projectid +
                "' and " + PHOTOURL_STOREID + " = '" + storeid + "' and " + PHOTOURL_TASKID + " = '" + taskid + "'";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                Tools.d("hhhhhh :" + cursor.getString(cursor.getColumnIndex(PHOTOURL_FILENUM)));
                filenum = Tools.StringToInt(cursor.getString(cursor.getColumnIndex(PHOTOURL_FILENUM)));
                if (filenum < Tools.StringToInt(cursor.getString(cursor.getColumnIndex(PHOTOURL_FILENUM)))) {
                    filenum = Tools.StringToInt(cursor.getString(cursor.getColumnIndex(PHOTOURL_FILENUM)));
                }
            } while (cursor.moveToNext());
        }
        Tools.d("新增拍照or视频数量2：" + filenum);
        cursor.close();
        db.close();
        return (paths.length + "").equals(filenum + "");
    }

    public boolean getPhotoIsComplete(String path) {//通过缩略图路径查找是否上传成功(照片)
        boolean isComplete = false;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_FILEPATH2 + " = '" + path + "'";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            String fileUrl = cursor.getString(cursor.getColumnIndex(PHOTOURL_FILEURL));
            if (fileUrl == null) {
                isComplete = false;
            } else {
                isComplete = true;
            }
        }
        cursor.close();
        db.close();
        return isComplete;
    }

    public boolean getViedoIsComplete(String path) {//通过路径查找是否上传成功(照片)
        boolean isComplete = false;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_FILEPATH + " = '" + path + "'";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            String fileUrl = cursor.getString(cursor.getColumnIndex(PHOTOURL_FILEURL));
            if (fileUrl == null) {
                isComplete = false;
            } else {
                isComplete = true;
            }
        }
        cursor.close();
        db.close();
        return isComplete;
    }

    /**
     * 获取任务下所有本地文件总大小
     *
     * @param username
     * @param projectid
     * @param storeid
     * @param taskid
     * @return
     */
    public synchronized long[] getAllFilesumsize(String username, String projectid, String storeid, String taskid) {
        long[] result = new long[2];
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_PHOTOURL + " where " + PHOTOURL_PROJECTID + " = '" + projectid +
                "' and " + PHOTOURL_STOREID + " = '" + storeid + "' and " + PHOTOURL_TASKID + " = '" + taskid + "' and (" +
                PHOTOURL_USERNAME + " = '" + username + "' or " + PHOTOURL_USERNAME + " = 'default')";
        Cursor cursor = db.rawQuery(select, null);
        Tools.d("行数：" + cursor.getCount() + "");
        if (cursor.moveToFirst()) {
            do {
                String size = cursor.getString(cursor.getColumnIndex(PHOTOURL_FILESIZE));
                String url = cursor.getString(cursor.getColumnIndex(PHOTOURL_FILEPATH));
                if (url != null && !"null".equals(url)) {
                    try {
                        result[0] += Long.parseLong(size);
                        if (new File(url).length() == 0) {
                            result[1] += Long.parseLong(size);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    /************************************************
     * 记录账号
     ************************************************/
    public static final String TABLENAME_LOGIN_NUMBER = "loginumber";//登录的账号记录表
    public static final String LOGIN_NUMBER_NAME = "name";//账号
    public static final String LOGIN_NUMBER_TIME = "time";//更新时间，以后排序用

    public synchronized void addLoginNumber(String number) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_LOGIN_NUMBER + " where " + LOGIN_NUMBER_NAME + " = '" + number +
                "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            cv.put(LOGIN_NUMBER_TIME, Tools.getTimeSS());
            db.update(TABLENAME_LOGIN_NUMBER, cv, LOGIN_NUMBER_NAME + " = ?", new String[]{number});
        } else {
            cv.put(LOGIN_NUMBER_NAME, number);
            cv.put(LOGIN_NUMBER_TIME, Tools.getTimeSS());
            db.insert(TABLENAME_LOGIN_NUMBER, null, cv);
        }
        cursor.close();
        db.close();
    }

    public synchronized boolean getLoginnumber(String number) {//查询用户是否是第一次登陆
        boolean isFrist = true;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_LOGIN_NUMBER + " where " + LOGIN_NUMBER_NAME + " = '" + number +
                "'";
        Cursor cursor = db.rawQuery(select, null);
//        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            isFrist = false;
        }
        cursor.close();
        db.close();
        return isFrist;
    }

    public synchronized ArrayList<String> getAllLoginnumber() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_LOGIN_NUMBER + " order by " + LOGIN_NUMBER_TIME + " desc";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(LOGIN_NUMBER_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**********************************************
     * 记录新的好友
     *************************************************/
    private static final String TABLENAME_MYTEAM_NEWFRIENDS = "myteam_newfriends";//记录新的好友表
    private static final String MYTEAM_NEWFD_MYNUM = "mynum";
    private static final String MYTEAM_NEWFD_NAME = "name";//名字
    private static final String MYTEAM_NEWFD_ID = "newfd_id";//账号
    private static final String MYTEAM_NEWFD__ID = "newfd__id";//id
    private static final String MYTEAM_NEWFD_IMG = "img";//头像
    private static final String MYTEAM_NEWFD_STATE = "state";//当前状态 0:未添加;1:已添加
    private static final String MYTEAM_NEWFD_TIME = "time";//更新时间，以后排序用

    public synchronized int addNewfriendsData(String mynum, String name, String id, String _id, String img) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_MYTEAM_NEWFRIENDS + " where " + MYTEAM_NEWFD_ID + " = '" + id +
                "' and " + MYTEAM_NEWFD_MYNUM + " = '" + mynum + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        int result = 0;
        if (cursor.moveToFirst()) {//防止脏数据
            cv.put(MYTEAM_NEWFD_TIME, Tools.getTimeSS());
            db.update(TABLENAME_MYTEAM_NEWFRIENDS, cv, MYTEAM_NEWFD_ID + " = ?", new String[]{id});
        } else {
            result = 1;
            cv.put(MYTEAM_NEWFD_MYNUM, mynum);
            cv.put(MYTEAM_NEWFD_NAME, name);
            cv.put(MYTEAM_NEWFD_ID, id);
            cv.put(MYTEAM_NEWFD__ID, _id);
            cv.put(MYTEAM_NEWFD_IMG, img);
            cv.put(MYTEAM_NEWFD_STATE, 0);
            cv.put(MYTEAM_NEWFD_TIME, Tools.getTimeSS());
            db.insert(TABLENAME_MYTEAM_NEWFRIENDS, null, cv);
        }
        cursor.close();
        db.close();
        return result;
    }

    public synchronized void upState(String mynum, String id) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_MYTEAM_NEWFRIENDS + " where " + MYTEAM_NEWFD_ID + " = '" + id +
                "' and " + MYTEAM_NEWFD_MYNUM + " = '" + mynum + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            cv.put(MYTEAM_NEWFD_STATE, 1);
            db.update(TABLENAME_MYTEAM_NEWFRIENDS, cv, MYTEAM_NEWFD_ID + " = ?", new String[]{id});
        }
        cursor.close();
        db.close();
    }

    public synchronized ArrayList<MyteamNewfdInfo> getAllNewfriends(String mynum) {
        ArrayList<MyteamNewfdInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_MYTEAM_NEWFRIENDS + " where " + MYTEAM_NEWFD_MYNUM + " = '" +
                mynum + "' order by " + MYTEAM_NEWFD_TIME + " desc";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                MyteamNewfdInfo myteamNewfdInfo = new MyteamNewfdInfo();
                myteamNewfdInfo.setId(cursor.getString(cursor.getColumnIndex(MYTEAM_NEWFD_ID)));
                myteamNewfdInfo.set_id(cursor.getString(cursor.getColumnIndex(MYTEAM_NEWFD__ID)));
                myteamNewfdInfo.setImg(cursor.getString(cursor.getColumnIndex(MYTEAM_NEWFD_IMG)));
                myteamNewfdInfo.setName(cursor.getString(cursor.getColumnIndex(MYTEAM_NEWFD_NAME)));
                myteamNewfdInfo.setState(cursor.getInt(cursor.getColumnIndex(MYTEAM_NEWFD_STATE)));
                list.add(myteamNewfdInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**********************************************
     * 记录自由拍的信息 version 6
     *************************************************/
    private static final String TABLENAME_SHAKEPHOTO = "table_shakephoto";//表名
    private static final String SHAKEPHOTO_FILEPATH = "filepath";//原图路径
    private static final String SHAKEPHOTO_FILEPATH2 = "filepath2";//缩略图路径  注：没用到（zhangpengfei）
    private static final String SHAKEPHOTO_TIME = "time";//时间
    private static final String SHAKEPHOTO_PROVINCE = "province";//省份
    private static final String SHAKEPHOTO_CITY = "city";//城市
    private static final String SHAKEPHOTO_COUNTY = "county";//区/县
    private static final String SHAKEPHOTO_ADDRESS = "address";//精确位置
    private static final String SHAKEPHOTO_AREA = "area";//不精确位置（用来图片分类）
    private static final String SHAKEPHOTO_LONGITUDE = "longitude";//经度
    private static final String SHAKEPHOTO_LATITUDE = "latitude";//纬度

    //自由拍图片信息存储
    public synchronized boolean addShakePhoto(String path, String path2, String area, String time, String longitude, String latitude
            , String address, String province, String city, String county) {
        Tools.d("自由拍-path：" + path);
        long in;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_SHAKEPHOTO + " where " + SHAKEPHOTO_AREA + " = '" + area +
                "' and " + SHAKEPHOTO_TIME + " = '" + time + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {
            path = path + "," + cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_FILEPATH));
            cv.put(SHAKEPHOTO_FILEPATH, path);
            path2 = path2 + "," + cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_FILEPATH2));
            cv.put(SHAKEPHOTO_FILEPATH2, path2);
            cv.put(SHAKEPHOTO_TIME, time);
            cv.put(SHAKEPHOTO_AREA, area);
            cv.put(SHAKEPHOTO_LATITUDE, latitude);
            cv.put(SHAKEPHOTO_LONGITUDE, longitude);
            cv.put(SHAKEPHOTO_PROVINCE, province);
            cv.put(SHAKEPHOTO_CITY, city);
            cv.put(SHAKEPHOTO_COUNTY, county);
            cv.put(SHAKEPHOTO_ADDRESS, address);
            in = db.update(TABLENAME_SHAKEPHOTO, cv, SHAKEPHOTO_AREA + " is ? and " + SHAKEPHOTO_TIME + " is ?", new String[]{area, time});
            Tools.d("自由拍-update：......." + in);
        } else {
            cv.put(SHAKEPHOTO_FILEPATH, path);
            cv.put(SHAKEPHOTO_FILEPATH2, path2);
            cv.put(SHAKEPHOTO_TIME, time);
            cv.put(SHAKEPHOTO_AREA, area);
            cv.put(SHAKEPHOTO_LATITUDE, latitude);
            cv.put(SHAKEPHOTO_LONGITUDE, longitude);
            cv.put(SHAKEPHOTO_PROVINCE, province);
            cv.put(SHAKEPHOTO_CITY, city);
            cv.put(SHAKEPHOTO_COUNTY, county);
            cv.put(SHAKEPHOTO_ADDRESS, address);
            in = db.insert(TABLENAME_SHAKEPHOTO, null, cv);
            Tools.d("自由拍-insert：......." + in);
        }
        db.close();
        cursor.close();
        return in > 0;
    }

    //查询所有图片信息
    public synchronized ArrayList<ShakePhotoInfo2> getShakePhoto() {
        ArrayList<ShakePhotoInfo2> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_SHAKEPHOTO;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_FILEPATH));
                String path2 = cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_FILEPATH2));
                if (!Tools.isEmpty(path) && !Tools.isEmpty(path2)) {
                    ShakePhotoInfo2 shakePhotoInfo = new ShakePhotoInfo2();
                    shakePhotoInfo.setFile_url(path);
                    shakePhotoInfo.setFile_url2(path2);
                    shakePhotoInfo.setProvince(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_PROVINCE)));
                    shakePhotoInfo.setCity(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_CITY)));
                    shakePhotoInfo.setCounty(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_COUNTY)));
                    shakePhotoInfo.setAddress(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_ADDRESS)));
                    shakePhotoInfo.setLatitude(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_LATITUDE)));
                    shakePhotoInfo.setLongitude(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_LONGITUDE)));
                    shakePhotoInfo.setArea(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_AREA)));
                    shakePhotoInfo.setTime(cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_TIME)));
                    list.add(shakePhotoInfo);
                }
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        Collections.sort(list, new Comparator<ShakePhotoInfo2>() {
            public int compare(ShakePhotoInfo2 lhs, ShakePhotoInfo2 rhs) {
                Date date1 = stringToDate(lhs.getTime());
                Date date2 = stringToDate(rhs.getTime());
                // 对日期字段进行降序
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });
        return list;
    }

    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    public synchronized void clearNullData() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_SHAKEPHOTO, SHAKEPHOTO_FILEPATH + " = ''", null);
        db.close();
    }

    //图片上传完成后根据路径，地点时间删除照片
    public synchronized void deleteShakePhoto(String path) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_SHAKEPHOTO;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String fileUrl = cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_FILEPATH));
                String fileUrl2 = cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_FILEPATH2));
                if (fileUrl.contains(path)) {
                    String area = cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_AREA));
                    String time = cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_TIME));
                    if (fileUrl.contains(path + ",")) {//不是最后一位
                        fileUrl = fileUrl.replaceAll(path + ",", "");
                    } else if (fileUrl.contains(path)) {
                        fileUrl = fileUrl.replaceAll(path, "");
                    }
//                    if (fileUrl2.contains(path2 + ",")) {//不是最后一位
//                        fileUrl2 = fileUrl2.replaceAll(path2 + ",", "");
//                    } else if (fileUrl2.contains(path2)) {
//                        fileUrl2 = fileUrl2.replaceAll(path, "");
//                    }
                    updataShakePhoto(fileUrl, fileUrl2, area, time);
                    break;
                }
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
    }

    /**
     * 图片是否存在甩吧相册里
     *
     * @param path
     * @return
     */
    public synchronized boolean havPhotoInShakeTable(String path) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_SHAKEPHOTO;
        Cursor cursor = db.rawQuery(select, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            do {
                String fileUrl = cursor.getString(cursor.getColumnIndex(SHAKEPHOTO_FILEPATH));
                if (fileUrl.contains(path)) {
                    result = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return result;
    }

    //删除照片后更新数据库
    private synchronized void updataShakePhoto(String paths, String paths2, String area, String time) {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLENAME_SHAKEPHOTO + " where " + SHAKEPHOTO_AREA + " = '" + area +
                "' and " + SHAKEPHOTO_TIME + " = '" + time + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {//一定有数据
            cv.put(SHAKEPHOTO_FILEPATH, paths);
            cv.put(SHAKEPHOTO_FILEPATH2, paths2);
            db.update(TABLENAME_SHAKEPHOTO, cv, SHAKEPHOTO_AREA + " is ? and " + SHAKEPHOTO_TIME + " is ?", new String[]{area, time});
        }
        db.close();
        cursor.close();
    }

    /**********************************************
     * 甩图上传队列 version 7
     *************************************************/
    private static final String TABLENAME_SHAKEPHOTOUPDATA = "table_shakephotoupdata";//甩图上传队列表
    private static final String SSHAKEPHOTOUPDATA_PATH = "shakephotoupdata_path";//路径
    private static final String SHAKEPHOTOUPDATA_PARAMETER = "shakephotoupdata_parameter";//参数

    public synchronized boolean addShakePhotoUpdata(String path, String parameter) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        long in;
        cv.put(SSHAKEPHOTOUPDATA_PATH, path);
        cv.put(SHAKEPHOTOUPDATA_PARAMETER, parameter);
        in = db.insert(TABLENAME_SHAKEPHOTOUPDATA, null, cv);
        db.close();
        return in > 0;
    }

    public synchronized ArrayList<ShakephotoUpdataInfo> getShakePhotoUpdataList() {
        ArrayList<ShakephotoUpdataInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLENAME_SHAKEPHOTOUPDATA, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ShakephotoUpdataInfo shakephotoUpdataInfo = new ShakephotoUpdataInfo();
                shakephotoUpdataInfo.setPath(cursor.getString(cursor.getColumnIndex(SSHAKEPHOTOUPDATA_PATH)));
                shakephotoUpdataInfo.setParameter(cursor.getString(cursor.getColumnIndex(SHAKEPHOTOUPDATA_PARAMETER)));
                list.add(shakephotoUpdataInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public synchronized void removeShakePhotoUpdataItem(String path) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLENAME_SHAKEPHOTOUPDATA, SSHAKEPHOTOUPDATA_PATH + " = '" + path + "'", null);
        db.close();
    }
}
