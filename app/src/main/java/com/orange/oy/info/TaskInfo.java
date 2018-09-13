package com.orange.oy.info;

import android.text.TextUtils;

public class TaskInfo {
    private String name;
    private String id;
    private String code;
    private String is_record;
    private String photo_compression;//照片大小限制 1:100kb;2:300kb;3:500kb
    private String is_download;//是否支持离线，1为是，0为否
    private String is_watermark;//是否要水印 值为1时添加照片水印，值为0时不添加照片水印
    private String codeStr;//代号
    private String brand;//品牌
    /**
     * 完成进度
     */
    private String item1Num;
    private String item2Num;
    private String item3Num;
    private String finishTime;

    private String is_takephoto;//是否允许连续拍照,1时允许连续拍照，0为不允许连续拍照

    private String type;//值为1时为普通的检查项目，值为2为神秘客项目

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIs_takephoto() {
        return is_takephoto;
    }

    public void setIs_takephoto(String is_takephoto) {
        this.is_takephoto = is_takephoto;
    }

    public String getCodeStr() {
        return codeStr;
    }

    public String getBrand() {
        return brand;
    }

    public void setCodeStr(String codeStr) {
        this.codeStr = codeStr;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIs_watermark() {
        return is_watermark;
    }

    public void setIs_watermark(String is_watermark) {
        this.is_watermark = is_watermark;
    }

    public String getIs_download() {
        return is_download;
    }

    public void setIs_download(String is_download) {
        this.is_download = is_download;
    }

    public String getIs_record() {
        return is_record;
    }

    public void setIs_record(String is_record) {
        this.is_record = is_record;
    }

    public String getPhoto_compression() {
        return photo_compression;
    }

    public void setPhoto_compression(String photo_compression) {
        if (photo_compression.equals("1")) {
            this.photo_compression = "300";
        } else if (photo_compression.equals("2")) {
            this.photo_compression = "500";
        } else if (photo_compression.equals("3")) {
            this.photo_compression = "1024";
        } else if (photo_compression.equals("4")) {
            this.photo_compression = "-1";
        } else {
            this.photo_compression = "500";
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getItem3Num() {
        if (TextUtils.isEmpty(item3Num) || item3Num.equals("null")) {
            return "0";
        }
        return item3Num;
    }

    public void setItem3Num(String item3Num) {
        this.item3Num = item3Num;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getItem1Num() {
        if (TextUtils.isEmpty(item1Num) || item1Num.equals("null")) {
            return "0";
        }
        return item1Num;
    }

    public void setItem1Num(String item1Num) {
        this.item1Num = item1Num;
    }

    public String getItem2Num() {
        if (TextUtils.isEmpty(item2Num) || item2Num.equals("null")) {
            return "0";
        }
        return item2Num;
    }

    public void setItem2Num(String item2Num) {
        this.item2Num = item2Num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
