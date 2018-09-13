package com.orange.oy.info;

public class CityInfo {
    private String name;//city
    private String code;//provinceid
    private String sortLetters;
    private String province;
    private String county;
    private boolean isChecked;//是否选中Item

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCounty() {
        if (county == null) {
            county = "";
        }
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getProvince() {
        if (province == null) {
            province = "";
        }
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
