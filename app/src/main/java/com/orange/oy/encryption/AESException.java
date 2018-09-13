package com.orange.oy.encryption;

/**
 * Created by Administrator on 2018/1/24.
 */

public class AESException extends Exception {
    public AESException() {
        super();
    }

    public AESException(String detailMessage) {
        super(detailMessage);
    }

    public AESException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AESException(Throwable throwable) {
        super(throwable);
    }

}
