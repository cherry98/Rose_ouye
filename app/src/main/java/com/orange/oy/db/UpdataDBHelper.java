package com.orange.oy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.orange.oy.base.Tools;
import com.orange.oy.info.MyupdataInfo;
import com.orange.oy.info.MyupdataPackage;
import com.orange.oy.info.UpdataInfo;
import com.orange.oy.network.Urls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 上传信息数据库
 */
public class UpdataDBHelper extends SQLiteOpenHelper {
    private static final int VSERSION = 9;// 数据库版本号
    private static final String APPDBNAME = "orangeappdb_updata";//数据库名

    public UpdataDBHelper(Context context) {
        super(context, APPDBNAME, null, VSERSION);
    }

    private static final String UpdataTableName = "updatatablename";
    private static final String Updata_Url = "updata_url";//文件上传的接口
    private static final String Updata_file_key = "updata_file_key";//文件上传时的参数（多参数逗号分隔，应和路径数保持一致）
    private static final String Updata_filepath = "updata_filepath";//文件路径（多路径逗号分隔）
    private static final String Updata_file_success = "updata_file_success";//已上传成功的文件（存参数，根据参数判断去掉那个路径）注：没用到
    private static final String Updta_filetype = "updata_filetype";//文件类型
    private static final String Updata_UpdataByte = "updata_updatabyte";//已传的字节数（注：没用到！改成接口返回）
    private static final String Updata_parameter_key = "updata_parameter_key";//参数名，逗号分隔
    private static final String Updata_parameter_value = "updata_parameter_value";//参数值
    public static final String Updata_parameter_split = "8si,no8";//参数分隔符
    /***
     * Version 2
     ******/
    private static final String Updata_img_compression = "updata_img_compression";//压缩大小单位KB,默认值300
    /***
     * m,
     * Version 3
     ******/
    private static final String Updata_uniquely_num = "Updata_uniquely_num";//唯一可拼接标识码,默认值-1
    /***
     * Version 4
     ******/
    private static final String Updata_project_id = "Updata_project_id";//项目id
    private static final String Updata_project_name = "Updata_project_name";//项目名字
    private static final String Updata_store_id = "Updata_store_id";//店铺id
    private static final String Updata_store_name = "Updata_store_name";//店铺名字
    private static final String Updata_package_id = "Updata_package_id";//包id
    private static final String Updata_package_name = "Updata_package_name";//包名字
    private static final String Updata_task_id = "Updata_task_id";//任务id
    private static final String Updata_task_name = "Updata_task_name";//任务名字
    private static final String Updata_category1 = "Updata_category1";//分类1
    private static final String Updata_category2 = "Updata_category2";//分类2
    private static final String Updata_category3 = "Updata_category3";//分类3
    private static final String Updata_task_type = "Updata_task_type";//任务类型 0:关闭任务包
    /***
     * Version5
     ******/
    private static final String Updata_is_completed = "Updata_is_completed";//是否需要执行执行完成接口（int型），0:不需要,1:需要
    private static final String Updata_completed_url = "Updata_completed_url";//执行完成接口
    private static final String Updata_completed_parameter = "Updata_completed_parameter";//执行完成参数，拼接好的
    /***
     * Version6
     ******/
    private static final String Updata_username = "Updata_username";//账号
    /***
     * Version7
     *****/
    private static final String Updata_time = "Updata_time";//时间
    private static final String Updata_code = "updata_code";//网点编号
    private static final String Updata_brand = "updata_brand";//品牌
    /***
     * Version8
     *****/
    private static final String Updata_isUp = "Updata_isUp";//是否上传 int型(默认1），0：不上传，1：上传
    /**
     * version9
     */
    private static final String Updata_isBlack = "updata_isblack";//是否是暗访任务，1：是；0：否
    //文件类型
    public static final String Updata_file_type_video = "mp4";
    public static final String Updata_file_type_img = "png";

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UpdataTableName + "(id INTEGER PRIMARY KEY," + Updata_Url + " TEXT," +
                Updata_filepath + " TEXT," + Updata_file_key + " TEXT," + Updata_file_success + " TEXT," +
                Updata_UpdataByte + " TEXT," + Updata_parameter_key + " TEXT," + Updata_parameter_value + " TEXT," +
                Updata_img_compression + " TEXT," + Updata_project_id + " TEXT," + Updata_project_name + " TEXT," +
                Updata_store_id + " TEXT," + Updata_store_name + " TEXT," + Updata_package_id + " TEXT," +
                Updata_package_name + " TEXT," + Updata_task_id + " TEXT," + Updata_task_name + " TEXT," +
                Updata_category1 + " TEXT," + Updata_category2 + " TEXT," + Updata_category3 + " TEXT," +
                Updata_task_type + " TEXT," + Updata_uniquely_num + " TEXT," + Updata_is_completed + " INTEGER DEFAULT 0," +
                Updata_completed_parameter + " TEXT," + Updata_completed_url + " TEXT," + Updata_username + " TEXT," +
                Updta_filetype + " TEXT," + Updata_time + " TEXT," + Updata_code + " TEXT," + Updata_isUp + " INTEGER,"
                + Updata_isBlack + " INTEGER," + Updata_brand + " TEXT" + ")");
    }

    public synchronized boolean addUpdataTask(String username, String projectid, String projectname, String code, String brand,
                                              String storeid, String storename,
                                              String packageid, String packagename, String tasktype, String taskid,
                                              String taskname, String category1, String category2, String category3,
                                              String uniquelyNum, String url, String file_keys, String file_paths, String type,
                                              Map<String, String> map, String compression, boolean isCompleted,
                                              String completedurl, String completedparameter, boolean isOfflineTask) {
        return addUpdataTask(username, projectid, projectname, code, brand, storeid, storename, packageid, packagename, tasktype,
                taskid, taskname, category1, category2, category3, uniquelyNum, url, file_keys, file_paths, type, map,
                compression, isCompleted, completedurl, completedparameter, isOfflineTask, false);
    }

    /**
     * 添加数据
     *
     * @param uniquelyNum        唯一标识拼接方法：账号+项目id+店铺id+包id+分类1+分类2+分类3+任务id
     * @param projectname        项目名
     * @param storeid            店铺id
     * @param storename          店铺名字
     * @param packageid          包id
     * @param packagename        包名字
     * @param taskid             任务id
     * @param taskname           任务名字
     * @param category1          分类1
     * @param category2          分类2
     * @param category3          分类3
     * @param url                url
     * @param file_keys          文件参数名
     * @param file_paths         文件本地地址
     * @param type               文件类型
     * @param map                参数集合
     * @param compression        图片压缩比例
     * @param isCompleted        是否执行完成
     * @param completedurl       执行完成接口
     * @param completedparameter 执行完成必要的参数（拼接后的）
     */
    public synchronized boolean addUpdataTask(String username, String projectid, String projectname, String code, String brand,
                                              String storeid, String storename,
                                              String packageid, String packagename, String tasktype, String taskid,
                                              String taskname, String category1, String category2, String category3,
                                              String uniquelyNum, String url, String file_keys, String file_paths, String type,
                                              Map<String, String> map, String compression, boolean isCompleted,
                                              String completedurl, String completedparameter, boolean isOfflineTask,
                                              boolean isBlack) {
        int isUp = 1;
//        if (!isOfflineTask) {
//            isUp = 0;
//        }
        if (TextUtils.isEmpty(compression)) {
            compression = "500";
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
        if (TextUtils.isEmpty(packageid)) {
            packageid = "";
        }
        if (TextUtils.isEmpty(packagename)) {
            packagename = "";
        }
        if (TextUtils.isEmpty(taskid)) {
            taskid = "";
        }
        if (TextUtils.isEmpty(taskname)) {
            taskname = "";
        }
        if (TextUtils.isEmpty(category1)) {
            category1 = "";
        }
        if (TextUtils.isEmpty(category2)) {
            category2 = "";
        }
        if (TextUtils.isEmpty(category3)) {
            category3 = "";
        }
        if (TextUtils.isEmpty(tasktype)) {
            tasktype = "";
        }
        if (TextUtils.isEmpty(file_paths)) {
            file_paths = "";
        }
        if (TextUtils.isEmpty(code)) {
            code = "";
        }
        if (TextUtils.isEmpty(brand)) {
            brand = "";
        }
        if (!isCompleted) {
            if (TextUtils.isEmpty(completedurl)) {
                completedurl = "";
            }
            if (TextUtils.isEmpty(completedparameter)) {
                completedparameter = "";
            }
        } else {
            if (TextUtils.isEmpty(completedurl) || TextUtils.isEmpty(completedparameter)) {
                return false;
            }
        }
        uniquelyNum = projectid + uniquelyNum;
        Tools.d("file_paths>>>>>>>>>>>>>>" + file_paths);
        StringBuilder parameKey = new StringBuilder();
        StringBuilder parameName = new StringBuilder();
        if (map == null) {
            parameKey.append("");
            parameName.append("");
        } else {
            Iterator<String> iterator = map.keySet().iterator();
            String key, name;
            while (iterator.hasNext()) {
                key = iterator.next();
                name = map.get(key);
                if (TextUtils.isEmpty(name)) {
                    name = " ";
                }
                parameKey.append(key + Updata_parameter_split);
                parameName.append(name + Updata_parameter_split);
            }
        }
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName + " where " + Updata_uniquely_num + " = '" + uniquelyNum +
                "'";
//        String select = "select * from " + UpdataTableName + " where " + Updata_filepath + " = '" + file_paths + "'";
        Cursor cursor = db.rawQuery(select, null);
        ContentValues cv = new ContentValues();
        long in;
        if (cursor.moveToFirst()) {
            cv.put(Updata_Url, url);
            cv.put(Updata_filepath, file_paths);
            cv.put(Updata_UpdataByte, "0");
            cv.put(Updata_file_key, file_keys);
            cv.put(Updata_file_success, "");
            cv.put(Updata_parameter_key, parameKey.toString());
            cv.put(Updata_parameter_value, parameName.toString());
            cv.put(Updta_filetype, type);
            cv.put(Updata_img_compression, compression);
            cv.put(Updata_task_type, tasktype);
            cv.put(Updata_is_completed, (isCompleted) ? 1 : 0);
            cv.put(Updata_completed_url, completedurl);
            cv.put(Updata_completed_parameter, completedparameter);
            cv.put(Updata_code, code);
            cv.put(Updata_brand, brand);
            cv.put(Updata_time, Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss"));
            cv.put(Updata_isUp, isUp);
            in = db.update(UpdataTableName, cv, Updata_uniquely_num + " = ?", new String[]{uniquelyNum});
            Tools.d("insert task to update");
        } else {
            cv.put(Updata_username, username);
            cv.put(Updata_Url, url);
            cv.put(Updata_filepath, file_paths);
            cv.put(Updata_UpdataByte, "0");
            cv.put(Updata_file_key, file_keys);
            cv.put(Updata_file_success, "");
            cv.put(Updata_parameter_key, parameKey.toString());
            cv.put(Updata_parameter_value, parameName.toString());
            cv.put(Updta_filetype, type);
            cv.put(Updata_img_compression, compression);
            cv.put(Updata_uniquely_num, uniquelyNum);
            cv.put(Updata_project_id, projectid);
            cv.put(Updata_project_name, projectname);
            cv.put(Updata_store_id, storeid);
            cv.put(Updata_store_name, storename);
            cv.put(Updata_package_id, packageid);
            cv.put(Updata_package_name, packagename);
            cv.put(Updata_task_id, taskid);
            cv.put(Updata_task_name, taskname);
            cv.put(Updata_category1, category1);
            cv.put(Updata_category2, category2);
            cv.put(Updata_category3, category3);
            cv.put(Updata_task_type, tasktype);
            cv.put(Updata_is_completed, (isCompleted) ? 1 : 0);
            cv.put(Updata_completed_url, completedurl);
            cv.put(Updata_completed_parameter, completedparameter);
            cv.put(Updata_time, Tools.getTimeByPattern("yyyy-MM-dd-HH-mm-ss"));
            cv.put(Updata_code, code);
            cv.put(Updata_brand, brand);
            cv.put(Updata_isUp, isUp);
            cv.put(Updata_isBlack, (isBlack) ? 1 : 0);
            in = db.insert(UpdataTableName, null, cv);
            Tools.d("insert task " + in);
        }
        cursor.close();
        db.close();
        return in > 0;
    }

    /**
     * 删除数据库数据
     *
     * @param uniquely_num 唯一标识码
     * @param path         存储的文件路径名，用于分辨是否在上传个过程中进行过重做
     */
    public synchronized void removeTask(String uniquely_num, String path) {
        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("delete from " + UpdataTableName + " where " + Updata_uniquely_num + " = '" + uniquely_num + "' and " +
//                Updata_filepath + " = '" + path + "'");
        int r = db.delete(UpdataTableName, Updata_uniquely_num + " is ? and " + Updata_filepath + " is ?", new
                String[]{uniquely_num, path});
        db.close();
        Tools.d("removeTask:" + r + "uniquely_num:" + uniquely_num + ",path:" + path);
    }

    public synchronized void removeTask(String uniquely_num) {
        SQLiteDatabase db = getWritableDatabase();
        int r = db.delete(UpdataTableName, Updata_uniquely_num + " is ?", new
                String[]{uniquely_num});
        db.close();
        Tools.d("removeTask:" + r + "uniquely_num:" + uniquely_num);
    }

    public synchronized void removeTaskForClose01(String uniquely_num) {
        SQLiteDatabase db = getWritableDatabase();
        int r = db.delete(UpdataTableName, Updata_uniquely_num + " is ? and " + Updata_task_type + " is '01'", new
                String[]{uniquely_num});
        db.close();
        Tools.d("removeTask1:" + r);
    }

    /**
     * 删除数据
     */
    public synchronized void removeTask(String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        int r = db.delete(UpdataTableName, Updata_username + " is ? and " + Updata_project_id + " is ? and " +
                Updata_store_id + " is ?", new String[]{username, projectid, storeid});
        db.close();
        Tools.d("removeTask2:" + r);
    }

    public synchronized boolean isHave() {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName + " where " + Updata_isUp + " is 1";
        Cursor cursor = db.rawQuery(select, null);
        boolean isHave = cursor.moveToFirst();
        cursor.close();
        db.close();
        Tools.d("isHave is " + isHave);
        return isHave;
    }

    public synchronized boolean startUp(String username, String projectid, String storeid) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Updata_isUp, 1);
        long index = db.update(UpdataTableName, cv, Updata_username + " is ? and " + Updata_project_id + " is ? and " +
                Updata_store_id + " is ?", new String[]{username, projectid, storeid});
        db.close();
        return index > 0;
    }

    public synchronized void clearTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(UpdataTableName, null, null);
        db.close();
    }

    public synchronized void upisUpTo1() {
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName;
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            ArrayList<String> storeList = new ArrayList<>();
            do {
                String tasktype = cursor.getString(cursor.getColumnIndex(Updata_task_type));
                String storeid = cursor.getString(cursor.getColumnIndex(Updata_store_id));
                if ("-5".equals(tasktype) && !storeList.contains(storeid)) {
                    storeList.add(storeid);
                    String usrname = cursor.getString(cursor.getColumnIndex(Updata_username));
                    Map<String, String> map = new HashMap<>();
                    map.put("token", Tools.getToken());
                    map.put("storeid", storeid);
                    map.put("usermobile", usrname);
                    addUpdataTask(usrname,
                            cursor.getString(cursor.getColumnIndex(Updata_project_id)),
                            cursor.getString(cursor.getColumnIndex(Updata_project_name)), null, null, storeid,
                            cursor.getString(cursor.getColumnIndex(Updata_store_name)),
                            null, null, "-5", null, null, null, null, null, Tools.getTimeSS() + "-5",
                            Urls.Startupload,
                            null, null, UpdataDBHelper.Updata_file_type_video, map, null, false, null, null, true);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put(Updata_isUp, 1);
        int index = db.update(UpdataTableName, cv, null, null);
        Tools.d("upisUpTo1:" + index);
        db.close();
    }

    public synchronized ArrayList<UpdataInfo> getTask() {
        ArrayList<UpdataInfo> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName;
        Cursor cursor = db.rawQuery(select, null);
        String str;
        String[] strs1, strs2;
        if (cursor.moveToFirst()) {
            Tools.d("getTask start " + cursor.getColumnCount());
            do {
                if (cursor.getInt(cursor.getColumnIndex(Updata_isUp)) == 0) {//跳过不上传的
                    continue;
                }
                UpdataInfo updataInfo = new UpdataInfo();
                updataInfo.setUniquelyNum(cursor.getString(cursor.getColumnIndex(Updata_uniquely_num)));
                updataInfo.setTaskType(cursor.getString(cursor.getColumnIndex(Updata_task_type)));
                updataInfo.setBlack(1 == cursor.getInt(cursor.getColumnIndex(Updata_isBlack)));
                if ("-4".equals(updataInfo.getTaskType())) {//多余文件
                    updataInfo.setProjectid(cursor.getString(cursor.getColumnIndex(Updata_project_id)));
                    updataInfo.setPackageId(cursor.getString(cursor.getColumnIndex(Updata_package_id)));
                    updataInfo.setStroeid(cursor.getString(cursor.getColumnIndex(Updata_store_id)));
                    updataInfo.setPaths(cursor.getString(cursor.getColumnIndex(Updata_filepath)));
                    updataInfo.setUsername(cursor.getString(cursor.getColumnIndex(Updata_username)));
                    updataInfo.setTaskTime(cursor.getString(cursor.getColumnIndex(Updata_time)));
                    list.add(updataInfo);
                    continue;
                }
                updataInfo.setIs_completed(cursor.getInt(cursor.getColumnIndex(Updata_is_completed)));
                updataInfo.setCompleted_parameter(cursor.getString(cursor.getColumnIndex(Updata_completed_parameter)));
                updataInfo.setCompleted_url(cursor.getString(cursor.getColumnIndex(Updata_completed_url)));
                updataInfo.setUrl(cursor.getString(cursor.getColumnIndex(Updata_Url)));
                updataInfo.setProjectid(cursor.getString(cursor.getColumnIndex(Updata_project_id)));
                updataInfo.setProjecname(cursor.getString(cursor.getColumnIndex(Updata_project_name)));
                updataInfo.setStroeid(cursor.getString(cursor.getColumnIndex(Updata_store_id)));
                updataInfo.setStorename(cursor.getString(cursor.getColumnIndex(Updata_store_name)));
                updataInfo.setUsername(cursor.getString(cursor.getColumnIndex(Updata_username)));
                updataInfo.setPackageId(cursor.getString(cursor.getColumnIndex(Updata_package_id)));
                updataInfo.setPackageName(cursor.getString(cursor.getColumnIndex(Updata_package_name)));
                updataInfo.setTaskId(cursor.getString(cursor.getColumnIndex(Updata_task_id)));
                updataInfo.setTaskName(cursor.getString(cursor.getColumnIndex(Updata_task_name)));
                updataInfo.setCategory1(cursor.getString(cursor.getColumnIndex(Updata_category1)));
                updataInfo.setCategory2(cursor.getString(cursor.getColumnIndex(Updata_category2)));
                updataInfo.setCategory3(cursor.getString(cursor.getColumnIndex(Updata_category3)));
                updataInfo.setTaskTime(cursor.getString(cursor.getColumnIndex(Updata_time)));
                updataInfo.setFileType(cursor.getString(cursor.getColumnIndex(Updta_filetype)));
                updataInfo.setCode(cursor.getString(cursor.getColumnIndex(Updata_code)));
                updataInfo.setBrand(cursor.getString(cursor.getColumnIndex(Updata_brand)));
                if ("-5".equals(updataInfo.getTaskType()) || "01".equals(updataInfo.getTaskType()) ||
                        "3".equals(updataInfo.getTaskType()) || "3-3".equals(updataInfo.getTaskType()) ||
                        "-2".equals(updataInfo.getTaskType()) || "wx3".equals(updataInfo.getTaskType()) ||
                        "6".equals(updataInfo.getTaskType()) || "333".equals(updataInfo.getTaskType()) ||
                        "111".equals(updataInfo.getTaskType()) || "222".equals(updataInfo.getTaskType())) {//记录任务没有文件不需要后续判断
                    if ("3-3".equals(updataInfo.getTaskType()) || "3".equals(updataInfo.getTaskType()) ||
                            "01".equals(updataInfo.getTaskType()) || "-5".equals(updataInfo.getTaskType()) ||
                            "6".equals(updataInfo.getTaskType()) || "333".equals(updataInfo.getTaskType())
                            || "111".equals(updataInfo.getTaskType()) || "222".equals(updataInfo.getTaskType())) {
                        Map<String, String> map = new HashMap<>();
                        str = cursor.getString(cursor.getColumnIndex(Updata_parameter_key));
                        strs1 = str.split(Updata_parameter_split);
                        str = cursor.getString(cursor.getColumnIndex(Updata_parameter_value));
                        strs2 = str.split(Updata_parameter_split);
                        if (strs1.length != strs2.length) {
                            db.delete(UpdataTableName, Updata_uniquely_num + " = ?", new String[]{updataInfo.getUniquelyNum()});
                            continue;
                        } else {
                            for (int i = 0; i < strs1.length; i++) {
                                map.put(strs1[i], strs2[i]);
                            }
                        }
                        updataInfo.setParame(map);
                    }
                    list.add(updataInfo);
                    continue;
                }
                String path = cursor.getString(cursor.getColumnIndex(Updata_filepath));
                Tools.d("path:" + path);
                Map<String, String> map = new HashMap<>();
                str = cursor.getString(cursor.getColumnIndex(Updata_parameter_key));
                strs1 = str.split(Updata_parameter_split);
                str = cursor.getString(cursor.getColumnIndex(Updata_parameter_value));
                strs2 = str.split(Updata_parameter_split);
                if (strs1.length != strs2.length) {
                    db.delete(UpdataTableName, Updata_uniquely_num + " = ?", new String[]{updataInfo.getUniquelyNum()});
                    continue;
                } else {
                    for (int i = 0; i < strs1.length; i++) {
                        map.put(strs1[i], strs2[i]);
                    }
                }
                updataInfo.setParame(map);
                updataInfo.setPaths(path);
                String[] paths = path.split(",");
                String[] keys = cursor.getString(cursor.getColumnIndex(Updata_file_key)).split(",");
                String[] success = cursor.getString(cursor.getColumnIndex(Updata_file_success)).split(",");
                if (paths.length == keys.length) {
                    Map<String, String> fileMap = new HashMap<>();
                    for (int i = 0; i < paths.length; i++) {
//                        for (int j = 0; j < success.length; j++) {
//                            if (keys[i].equals(success[j])) {
//                                keys[i] = null;
//                            }
//                        }
                        if (keys[i] == null) {
                            continue;
                        }
                        fileMap.put(keys[i], paths[i]);
                    }
                    updataInfo.setFileParame(fileMap);
                    updataInfo.setFileNum(paths.length);
                } else {
                    continue;
                }
                updataInfo.setType(cursor.getString(cursor.getColumnIndex(Updta_filetype)));
                updataInfo.setCompression(cursor.getString(cursor.getColumnIndex(Updata_img_compression)));
                list.add(updataInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }


    /**
     * 获取项目列表
     *
     * @return 项目列表
     */
    public ArrayList<MyupdataInfo> getProjectList() {
        ArrayList<MyupdataInfo> returnList = new ArrayList<MyupdataInfo>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName + " where " + Updata_isUp + " is 1";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String project_id = cursor.getString(cursor.getColumnIndex(Updata_project_id));
                int si = searchStore(returnList, project_id);
                if (si == -1) {
                    MyupdataInfo myupdataInfo = new MyupdataInfo();
                    myupdataInfo.setStoreid(project_id);
                    myupdataInfo.setStorename(cursor.getString(cursor.getColumnIndex(Updata_project_name)));
                    returnList.add(myupdataInfo);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * 获取所有网点列表-3.3
     */

    public ArrayList<MyupdataInfo> getStoreList() {
        ArrayList<MyupdataInfo> returnList = new ArrayList<MyupdataInfo>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName + " where " + Updata_isUp + " is 1";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String storeid = cursor.getString(cursor.getColumnIndex(Updata_store_id));
                int si = searchStore(returnList, storeid);
                ArrayList<MyupdataPackage> list = getPackageList(cursor.getString(cursor.getColumnIndex(Updata_project_id)), storeid);
                if (si == -1 && list != null && !list.isEmpty()) {
                    MyupdataInfo myupdataInfo = new MyupdataInfo();
                    myupdataInfo.setStoreid(storeid);
                    myupdataInfo.setStorename(cursor.getString(cursor.getColumnIndex(Updata_store_name)));
                    myupdataInfo.setProjectid(cursor.getString(cursor.getColumnIndex(Updata_project_id)));
                    myupdataInfo.setProjectname(cursor.getString(cursor.getColumnIndex(Updata_project_name)));
                    myupdataInfo.setCode(cursor.getString(cursor.getColumnIndex(Updata_code)));
                    returnList.add(myupdataInfo);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * 获取网点列表
     *
     * @param projectid 项目id
     * @return 网点列表
     */
    public ArrayList<MyupdataInfo> getStoreList(String projectid) {
        ArrayList<MyupdataInfo> returnList = new ArrayList<MyupdataInfo>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName + " where " + Updata_project_id + " = '" + projectid + "' and " +
                Updata_isUp + " is 1";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String storeid = cursor.getString(cursor.getColumnIndex(Updata_store_id));
                int si = searchStore(returnList, storeid);
                if (si == -1) {
                    MyupdataInfo myupdataInfo = new MyupdataInfo();
                    myupdataInfo.setStoreid(storeid);
                    myupdataInfo.setStorename(cursor.getString(cursor.getColumnIndex(Updata_store_name)));
                    returnList.add(myupdataInfo);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * 获取包与任务列表
     *
     * @param projectid 包id
     * @param storeid   网点id
     * @return 包与任务列表
     */
    public ArrayList<MyupdataPackage> getPackageList(String projectid, String storeid) {
        ArrayList<MyupdataPackage> returnList = new ArrayList<MyupdataPackage>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName + " where " + Updata_store_id + " = '" + storeid + "' AND " +
                "" + Updata_project_id + " = '" + projectid + "' and " + Updata_isUp + " is 1";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(Updata_package_id));
                String un = cursor.getString(cursor.getColumnIndex(Updata_uniquely_num));
                String tasktype = cursor.getString(cursor.getColumnIndex(Updata_task_type));
                if (tasktype.equals("-2") || tasktype.equals("-3") || tasktype.equals("-4")) {
                    continue;
                }
                boolean isPackage;
                if (TextUtils.isEmpty(id)) {
                    isPackage = false;
                    id = cursor.getString(cursor.getColumnIndex(Updata_task_id));
                } else {
                    isPackage = true;
                }
                int si = searchPackage(returnList, id, isPackage);
                if (si == -1) {
                    MyupdataPackage myupdataPackage = new MyupdataPackage();
                    myupdataPackage.setId(id);
                    myupdataPackage.setIsPackage(isPackage);
                    myupdataPackage.setTasktype(tasktype);
                    if (isPackage) {
                        myupdataPackage.setName(cursor.getString(cursor.getColumnIndex(Updata_package_name)));
                    } else {
                        myupdataPackage.setName(cursor.getString(cursor.getColumnIndex(Updata_task_name)));
                        myupdataPackage.setUniquelyNum(un);
                    }
                    returnList.add(myupdataPackage);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * 根据包名获取列表
     *
     * @param projectid 包id
     * @param storeid   网点id
     * @param packageid 包id
     * @return 包与任务列表
     */
    public ArrayList<MyupdataPackage> getPackageList(String projectid, String storeid, String packageid) {
        ArrayList<MyupdataPackage> returnList = new ArrayList<MyupdataPackage>();
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + UpdataTableName + " where " + Updata_store_id + " = '" + storeid + "' AND " +
                "" + Updata_project_id + " = '" + projectid + "' AND " + Updata_package_id + " = '" + packageid + "' and " +
                Updata_isUp + " is 1";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(Updata_task_id));
                String un = cursor.getString(cursor.getColumnIndex(Updata_uniquely_num));
                String category1 = cursor.getString(cursor.getColumnIndex(Updata_category1));
                String category2 = cursor.getString(cursor.getColumnIndex(Updata_category2));
                String category3 = cursor.getString(cursor.getColumnIndex(Updata_category3));
                int si = searchTask(returnList, storeid, category1 + category2 + category3);
                if (si == -1) {
                    MyupdataPackage myupdataPackage = new MyupdataPackage();
                    myupdataPackage.setId(id);
                    myupdataPackage.setName(cursor.getString(cursor.getColumnIndex(Updata_task_name)));
                    myupdataPackage.setCategory1(category1);
                    myupdataPackage.setCategory2(category2);
                    myupdataPackage.setCategory3(category3);
                    myupdataPackage.setUniquelyNum(un);
                    myupdataPackage.setTasktype(cursor.getString(cursor.getColumnIndex(Updata_task_type)));
                    returnList.add(myupdataPackage);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    private int searchTask(ArrayList<MyupdataPackage> returnList, String taskid, String category) {
        if (!TextUtils.isEmpty(taskid)) {
            int size = returnList.size();
            for (int i = 0; i < size; i++) {
                MyupdataPackage myupdataInfo = returnList.get(i);
                if (taskid.equals(myupdataInfo.getId()) && (myupdataInfo.getCategory1() + myupdataInfo.getCategory2()
                        + myupdataInfo.getCategory3()).equals(category)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int searchPackage(ArrayList<MyupdataPackage> returnList, String id, boolean isPackage) {
        if (!TextUtils.isEmpty(id)) {
            int size = returnList.size();
            for (int i = 0; i < size; i++) {
                MyupdataPackage myupdataInfo = returnList.get(i);
                if (isPackage) {
                    if (myupdataInfo.isPackage() && id.equals(myupdataInfo.getId())) {
                        return i;
                    }
                } else {
                    if ((!myupdataInfo.isPackage()) && id.equals(myupdataInfo.getId())) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private int searchStore(ArrayList<MyupdataInfo> returnList, String updata_store_id) {
        if (!TextUtils.isEmpty(updata_store_id)) {
            int size = returnList.size();
            for (int i = 0; i < size; i++) {
                MyupdataInfo myupdataInfo = returnList.get(i);
                if (updata_store_id.equals(myupdataInfo.getStoreid())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public synchronized String[] getTaskFiles(String username, String projectid, String storeid, String packateid, String
            category1, String category2, String category3, String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String whereStr;
        String[] wheres;
        if (TextUtils.isEmpty(packateid)) {
            whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id + " is ? and " +
                    Updata_task_id + " is ?";
            wheres = new String[]{username, projectid, storeid, taskid};
        } else {
            if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2) && !TextUtils.isEmpty(category3)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_category3 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, category3, taskid};
            } else if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, taskid};
            } else if (!TextUtils.isEmpty(category1)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, taskid};
            } else {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, taskid};
            }
        }
        Cursor cursor = db.query(UpdataTableName, new String[]{Updata_filepath}, whereStr, wheres, null, null, null);
        String[] returnStr = null;
        if (cursor.moveToFirst()) {
            String paths = null;
            do {
                if (TextUtils.isEmpty(paths))
                    paths = cursor.getString(cursor.getColumnIndex(Updata_filepath));
                else
                    paths = paths + "," + cursor.getString(cursor.getColumnIndex(Updata_filepath));
            } while (cursor.moveToNext());
            if (!TextUtils.isEmpty(paths)) {
                returnStr = paths.split(",");
            }
        }
        cursor.close();
        db.close();
        if (returnStr != null) {
            for (int i = 0; i < returnStr.length; i++) {
                if (returnStr[i].endsWith(".ouye")) {
                    StringBuilder sb = new StringBuilder(returnStr[i]);
                    sb.insert(sb.lastIndexOf("."), "_2");
                    returnStr[i] = sb.toString();
                }
            }
        }
        return returnStr;
    }

    public synchronized String[] getTaskFiles2(String username, String projectid, String storeid, String packateid, String
            category1, String category2, String category3, String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String whereStr;
        String[] wheres;
        if (TextUtils.isEmpty(packateid)) {
            whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id + " is ? and " +
                    Updata_task_id + " is ?";
            wheres = new String[]{username, projectid, storeid, taskid};
        } else {
            if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2) && !TextUtils.isEmpty(category3)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_category3 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, category3, taskid};
            } else if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, taskid};
            } else if (!TextUtils.isEmpty(category1)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, taskid};
            } else {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, taskid};
            }
        }
        Cursor cursor = db.query(UpdataTableName, new String[]{Updata_filepath, Updata_parameter_key, Updata_parameter_value,
                Updata_uniquely_num}, whereStr, wheres, null, null, null);
        String[] returnStr = null;
        if (cursor.moveToFirst()) {
            String paths = null;
            String str;
            String[] strs1, strs2;
            str = cursor.getString(cursor.getColumnIndex(Updata_parameter_key));
            strs1 = str.split(Updata_parameter_split);
            str = cursor.getString(cursor.getColumnIndex(Updata_parameter_value));
            strs2 = str.split(Updata_parameter_split);
            do {
                if (TextUtils.isEmpty(paths))
                    paths = cursor.getString(cursor.getColumnIndex(Updata_filepath));
                else
                    paths = paths + "," + cursor.getString(cursor.getColumnIndex(Updata_filepath));
            } while (cursor.moveToNext());
            if (!TextUtils.isEmpty(paths)) {
                returnStr = paths.split(",");
            }
            if (returnStr != null) {
                for (int i = 0; i < returnStr.length; i++) {
                    if (TextUtils.isEmpty(returnStr[i])) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder(returnStr[i]);
                    sb.insert(sb.lastIndexOf("."), "_2");
                    returnStr[i] = sb.toString();
                }
                Map<String, String> map = new HashMap<>();
                if (strs1.length == strs2.length) {
                    for (int i = 0; i < strs1.length; i++) {
                        map.put(strs1[i], strs2[i]);
                    }
                }
                for (int i = 0; i < returnStr.length; i++) {
                    String temp = map.get("txt" + (i + 1));
                    if (!TextUtils.isEmpty(temp) && !TextUtils.isEmpty(temp.trim())) {
                        temp = "";
                    }
                    returnStr[i] = returnStr[i] + "&" + temp;
                }
            }
        }
        cursor.close();
        db.close();
        return returnStr;
    }

    public synchronized String getTaskFilesForClosetask(String username, String projectid, String storeid, String packateid,
                                                        String category1, String category2, String category3, String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String whereStr;
        String[] wheres;
        if (TextUtils.isEmpty(packateid)) {
            whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id + " is ? and " +
                    Updata_task_id + " is ?";
            wheres = new String[]{username, projectid, storeid, taskid};
        } else {
            if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2) && !TextUtils.isEmpty(category3)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_category3 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, category3, taskid};
            } else if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, taskid};
            } else if (!TextUtils.isEmpty(category1)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, taskid};
            } else {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, taskid};
            }
        }
        Cursor cursor = db.query(UpdataTableName, new String[]{Updata_filepath, Updata_parameter_key, Updata_parameter_value},
                whereStr, wheres, null, null, null);
        String returnStr = "";
        if (cursor.moveToFirst()) {
            Map<String, String> map = new HashMap<>();
            String str;
            String[] strs1, strs2;
            str = cursor.getString(cursor.getColumnIndex(Updata_parameter_key));
            strs1 = str.split(Updata_parameter_split);
            str = cursor.getString(cursor.getColumnIndex(Updata_parameter_value));
            strs2 = str.split(Updata_parameter_split);
            if (strs1.length == strs2.length) {
                for (int i = 0; i < strs1.length; i++) {
                    if ("note".equals(strs1[i])) {
                        returnStr = strs2[i];
                        break;
                    }
                }
            }
        }
        cursor.close();
        db.close();
        return returnStr;
    }

    public synchronized String getEditTextAnswers(String username, String projectid, String storeid, String packateid, String
            category1, String category2, String category3, String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String whereStr;
        String[] wheres;
        if (TextUtils.isEmpty(packateid)) {
            whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id + " is ? and " +
                    Updata_task_id + " is ?";
            wheres = new String[]{username, projectid, storeid, taskid};
        } else {
            if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2) && !TextUtils.isEmpty(category3)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_category3 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, category3, taskid};
            } else if (!TextUtils.isEmpty(category1) && !TextUtils.isEmpty(category2)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_category2 + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, category2, taskid};
            } else if (!TextUtils.isEmpty(category1)) {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_category1 + " is ? and " +
                        Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, category1, taskid};
            } else {
                whereStr = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id +
                        " is ? and " + Updata_package_id + " is ? and " + Updata_task_id + " is ?";
                wheres = new String[]{username, projectid, storeid, packateid, taskid};
            }
        }
        Cursor cursor = db.query(UpdataTableName, new String[]{Updata_completed_parameter},
                whereStr, wheres, null, null, null);
        String returnStr = null;
        if (cursor.moveToFirst()) {
            String[] ps = cursor.getString(cursor.getColumnIndex(Updata_completed_parameter)).split("&");
            for (String temp : ps) {
                if (temp.startsWith("answers=")) {
                    String[] temps = temp.split("=");
                    if (temps.length > 1) {
                        returnStr = temps[1];
                    }
                    break;
                }
            }
        }
        cursor.close();
        db.close();
        return returnStr;
    }

    public synchronized void deleteStoreTask(String updata_username, String updata_project_id, String updata_store_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(UpdataTableName, Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id + " is ?",
                new String[]{updata_username, updata_project_id, updata_store_id});
        db.close();
    }

    public synchronized void deleteTask(String username, String projectid, String storeid, String packageid, String taskid) {
        SQLiteDatabase db = getWritableDatabase();
        String where = Updata_username + " is ? and " + Updata_project_id + " is ? and " + Updata_store_id + " is ? and " +
                Updata_task_id + " is ?";
        String[] wheres;
        if (!TextUtils.isEmpty(packageid)) {
            where = where + " and " + Updata_package_id + " is ?";
            wheres = new String[]{username, projectid, storeid, taskid, packageid};
        } else {
            where = where + " and " + Updata_package_id + " is ''";
            wheres = new String[]{username, projectid, storeid, taskid};
        }
        db.delete(UpdataTableName, where, wheres);
        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            Version2(db);
            Version3(db);
            Version4(db);
            Version5(db);
            Version6(db);
            Version7(db);
            Version8(db);
            Version9(db);
        } else if (oldVersion == 2) {
            Version3(db);
            Version4(db);
            Version5(db);
            Version6(db);
            Version7(db);
            Version8(db);
            Version9(db);
        } else if (oldVersion == 3) {
            Version4(db);
            Version5(db);
            Version6(db);
            Version7(db);
            Version8(db);
            Version9(db);
        } else if (oldVersion == 4) {
            Version5(db);
            Version6(db);
            Version7(db);
            Version8(db);
            Version9(db);
        } else if (oldVersion == 5) {
            Version6(db);
            Version7(db);
            Version8(db);
            Version9(db);
        } else if (oldVersion == 6) {
            Version7(db);
            Version8(db);
            Version9(db);
        } else if (oldVersion == 7) {
            Version8(db);
            Version9(db);
        } else if (oldVersion == 8) {
            Version9(db);
        }
    }

    private void Version2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_img_compression + " TEXT default '300'");
    }

    private void Version3(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_uniquely_num + " TEXT default '-1'");
    }

    private void Version4(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_project_id + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_project_name + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_store_id + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_store_name + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_package_id + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_package_name + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_task_id + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_task_name + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_category1 + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_category2 + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_category3 + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_task_type + " TEXT");
    }

    private void Version5(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_is_completed + " INTEGER DEFAULT 0");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_completed_url + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_completed_parameter + " TEXT");
    }

    private void Version6(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_username + " TEXT");
    }

    private void Version7(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_time + " TEXT DEFAULT '0'");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_code + " TEXT");
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_brand + " TEXT");
    }

    private void Version8(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_isUp + " INTEGER DEFAULT 1");
    }

    private void Version9(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + UpdataTableName + " ADD " + Updata_isBlack + " INTEGER DEFAULT 0");
    }
}
