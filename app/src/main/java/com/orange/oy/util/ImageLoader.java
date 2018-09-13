package com.orange.oy.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageLoader {

    private MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    private Context context;
    private int showWH = 300;
    private boolean isGif = false;

    public ImageLoader setGif(boolean gif) {
        isGif = gif;
        return this;
    }

    public ImageLoader setShowWH(int showWH) {
        this.showWH = showWH;
        return this;
    }

    public ImageLoader(Context context) {
        this.context = context;
        executorService = Executors.newFixedThreadPool(5);
    }

    private static int stub_id = -1;

//    public void DisplayImage(String url, ImageView imageView, int noImageId) {
//        DisplayImage(url, imageView, noImageId, 512, 512);
//    }

    /**
     * @param url       图片地址（网络/本地）
     * @param imageView
     * @param noImageId -2 不进行默认图片加载
     */
    public void DisplayImage(String url, ImageView imageView, int noImageId) {
        stub_id = noImageId;
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(stub_id);
            return;
        }
        url = url.replaceAll("\"", "");
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            url = "";
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            Tools.d(url);
            imageViews.put(imageView, url);
            Bitmap bitmap = memoryCache.get(url);
            if (bitmap != null && !bitmap.isRecycled())
                imageView.setImageBitmap(bitmap);
            else {
                queuePhoto(url, imageView);
                if (stub_id != -2) {
                    imageView.setImageResource(stub_id);
                }
            }
        } else {
            imageViews.put(imageView, url);
            queuePhoto(url, imageView);
            if (stub_id != -2) {
                imageView.setImageResource(stub_id);
            }
//            try {
//                File f = new File(url);
//                Bitmap b = getBitmap_rotate(f);
//                if (b != null) {
//                    imageView.setImageBitmap(b);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    class GetBitmapTask extends AsyncTask {
        protected Object doInBackground(Object[] params) {
            return null;
        }

        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

    }

    public void DisplayImage(String url, ImageView imageView) {
        DisplayImage(url, imageView, R.mipmap.dingwei);
    }
    // public void DisplayImage2(String url, ImageView imageView) {
    // imageViews.put(imageView, url);
    // queuePhoto2(url, imageView);
    // imageView.setImageResource(stub_id);
    //
    // }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private void queuePhoto2(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader2(p));
    }

    private Bitmap getBitmap_rotate(File f) {
        Bitmap b = null;
        if (f.exists()) {
            if (f.length() > 100 * 1024) {
                b = Tools.getBitmap(f.getPath(), showWH, showWH);
            } else {
                b = BitmapFactory.decodeFile(f.getPath());
            }
        }
        // from SD cache
        if (b != null) {
            b = rotateBitmap(f, b);
            return b;
        }
        return null;
    }

    public Uri getUri(String url) {
        File f = FileCache.getFile(context, url);
        if (f.exists() && f.isFile()) {
            return Uri.fromFile(f);
        }
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            if (f.exists() && f.isFile()) {
                return Uri.fromFile(f);
            } else {
                return null;
            }
        } catch (Exception e) {
            Tools.d(e.getMessage());
            return null;
        }
    }

    public Bitmap getBitmap(String url) {
        File f = FileCache.getFile(context, url);
        Bitmap b = getBitmap_rotate(f);
        if (b != null) {
            return b;
        }

        // from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            boolean result = Utils.CopyStream(is, os);
            os.close();
            if (result) {
                if (f.length() > 100 * 1024) {
                    bitmap = Tools.getBitmap(f.getPath(), showWH, showWH);
                } else {
                    bitmap = BitmapFactory.decodeFile(f.getPath());
                }
                if (f.exists()) {
                    Date curDate = new Date(System.currentTimeMillis());
                    f.setLastModified(curDate.getTime());
                    bitmap = rotateBitmap(f, bitmap);
                }
                return bitmap;
            } else {
                if (f.exists()) {
                    f.delete();
                }
                return null;
            }
        } catch (Exception e) {
            if (f.exists()) {
                f.delete();
            }
            Tools.d(e.getMessage());
            return null;
        }
    }

    private Bitmap getBitmapFromThread(String url) {
        File f = FileCache.getFile(context, url);
        Bitmap b = getBitmap_rotate(f);
        if (b != null) {
            return b;
        }
        // from web
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            is = conn.getInputStream();
            if (f.length() > 100 * 1024) {
                bitmap = Tools.getBitmap(f.getPath(), showWH, showWH);
            } else {
                bitmap = BitmapFactory.decodeFile(f.getPath());
            }
            if (f.exists()) {
                Date curDate = new Date(System.currentTimeMillis());
                f.setLastModified(curDate.getTime());
                bitmap = rotateBitmap(f, bitmap);
            }
            return bitmap;
        } catch (Exception e) {
            Tools.d(e.getMessage());
        } finally {
            if (conn != null)
                conn.disconnect();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static Bitmap rotateBitmap(File f, Bitmap origin) {
        int alpha = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(f.getPath());
            String or = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            alpha = Tools.StringToInt(or);
            if (alpha == -1) {
                alpha = 0;
            } else {
                switch (alpha) {
                    case 1: {
                        alpha = 0;
                    }
                    break;
                    case 6: {
                        alpha = 90;
                    }
                    break;
                    case 8: {
                        alpha = 270;
                    }
                    break;
                    case 3: {
                        alpha = 180;
                    }
                    break;
                    default: {
                        alpha = 0;
                    }
                    break;
                }
            }
        } catch (IOException e) {
            return origin;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Tools.d(e.getMessage());
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    /**
     * 读取GIF 文件头
     */
    boolean checkPicture(String path) {
        boolean result = false;
        FileInputStream is = null;
        try {
            is = new FileInputStream(path);
            String id = "";
            for (int i = 0; i < 6; i++) {
                id += (char) is.read();
            }
            result = id.toUpperCase().startsWith("GIF");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = null;
            Uri uri = null;
            Tools.d(photoToLoad.url);
            if (photoToLoad.url.startsWith("http")) {
                uri = getUri(photoToLoad.url);
                if (uri != null) {
                    if (isGif) {
                        isGif = checkPicture(uri.getPath());
                    }
                    if (isGif) {
                        uri = getUri(photoToLoad.url);
                    } else {
                        bmp = getBitmap(photoToLoad.url);
                        memoryCache.put(photoToLoad.url, bmp);
                        uri = null;
                    }
                } else {
                    bmp = getBitmap(photoToLoad.url);
                    memoryCache.put(photoToLoad.url, bmp);
                    uri = null;
                }
            } else {
                try {
                    File f = new File(photoToLoad.url);
                    if (f.length() > 100 * 1024) {
                        bmp = Tools.getBitmap(f.getPath(), showWH, showWH);
                    } else {
                        bmp = BitmapFactory.decodeFile(f.getPath());
                    }
                    bmp = rotateBitmap(f, bmp);
                } catch (Exception e) {
                    return;
                }
                memoryCache.put(photoToLoad.url, null);
            }
            if (imageViewReused(photoToLoad))
                return;
            if (uri != null) {
                BitmapDisplayerUri bd = new BitmapDisplayerUri(uri, photoToLoad);
                Activity a = (Activity) photoToLoad.imageView.getContext();
                a.runOnUiThread(bd);
            } else {
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                Activity a = (Activity) photoToLoad.imageView.getContext();
                a.runOnUiThread(bd);
            }
        }
    }

    private class PhotosLoader2 implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader2(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmapFromThread(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    public void cancelLoad(ImageView imageView) {
        imageViews.remove(imageView);
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    private class BitmapDisplayerUri implements Runnable {
        Uri uri;
        PhotoToLoad photoToLoad;

        public BitmapDisplayerUri(Uri u, PhotoToLoad p) {
            uri = u;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (uri != null)
                photoToLoad.imageView.setImageURI(uri);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache(Context context, long clearTime) {
        memoryCache.clear();
        FileCache.clear(context, clearTime);
    }


}