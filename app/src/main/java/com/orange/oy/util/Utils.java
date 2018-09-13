package com.orange.oy.util;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static boolean CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    return true;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 检测摄像头设备是否可用 Check if this device has a camera
     *
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
        if (context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}