package com.orange.oy.info;

public class NewCityInfo {

    /**
     * id : null
     * city : 唐山市
     * cityPinyin : tangshanshi
     * province : 河北省
     * provincePinyin : hebeisheng
     * county : 路南区
     * countyPinyin : lunanqu
     */

    private Object id;
    private String city;   //市
    private String cityPinyin; //
    private String province;  //省
    private String provincePinyin;
    private String county;   // 县
    private String countyPinyin;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityPinyin() {
        return cityPinyin;
    }

    public void setCityPinyin(String cityPinyin) {
        this.cityPinyin = cityPinyin;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvincePinyin() {
        return provincePinyin;
    }

    public void setProvincePinyin(String provincePinyin) {
        this.provincePinyin = provincePinyin;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyPinyin() {
        return countyPinyin;
    }

    public void setCountyPinyin(String countyPinyin) {
        this.countyPinyin = countyPinyin;
    }
}
