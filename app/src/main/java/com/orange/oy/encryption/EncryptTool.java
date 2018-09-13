package com.orange.oy.encryption;

import android.content.Context;
import android.text.TextUtils;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptTool {
    private String AESKEY = null;
    private Context context;

    public EncryptTool(Context context) {
        this.context = context;
        initAESKEY();
    }

    public void initAESKEY() {
        AESKEY = AESCrypt.getRandom16();
    }

    /**
     * AES加密
     *
     * @param content
     * @return
     * @throws NoSuchPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws AESException
     */
    public String AESencode(String content) throws NoSuchPaddingException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, AESException {
        if (TextUtils.isEmpty(AESKEY)) {
            throw new AESException("EncryptTool AESKEY is NULL!");
        }
        return AESCrypt.aesEncryptString(content, AESKEY);
    }

    public String AESdecode(String content) throws AESException, NoSuchPaddingException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException {
        if (TextUtils.isEmpty(AESKEY)) {
            throw new AESException("EncryptTool AESKEY is NULL!");
        }
        return AESCrypt.aesDecryptString(content, AESKEY);
    }

    /**
     * 获取aeskey
     */
    public String getAeskey() throws AESException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        if (TextUtils.isEmpty(AESKEY)) {
            throw new AESException("EncryptTool AESKEY is NULL!");
        }
        return Base64.encodeToString(RSAUtils.encryptByPublicKey(AESKEY, context.getResources().getString(R.string.RSA)), Base64.DEFAULT);
    }

}
