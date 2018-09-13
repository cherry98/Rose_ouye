package com.orange.oy.info;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/8/28.
 */

public class MyGiftDetailInfo implements Serializable {

    private static final long serialVersionUID = 8787887346885823606L;
    private String express_company;
    private String express_number;
    private String official_phone;
    private String gift_name;
    private String express_type;

    public String getExpress_type() {
        return express_type;
    }

    public void setExpress_type(String express_type) {
        this.express_type = express_type;
    }

    public String getExpress_company() {
        return express_company;
    }

    public void setExpress_company(String express_company) {
        this.express_company = express_company;
    }

    public String getExpress_number() {
        return express_number;
    }

    public void setExpress_number(String express_number) {
        this.express_number = express_number;
    }

    public String getOfficial_phone() {
        return official_phone;
    }

    public void setOfficial_phone(String official_phone) {
        this.official_phone = official_phone;
    }

    public String getGift_name() {
        return gift_name;
    }

    public void setGift_name(String gift_name) {
        this.gift_name = gift_name;
    }
}
