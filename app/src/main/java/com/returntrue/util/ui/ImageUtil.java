package com.returntrue.util.ui;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.returntrue.application.BaseApplication;
import com.returntrue.framework.JActivity;
import com.returntrue.util.JLog;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author JosephWang
 */
public class ImageUtil
{
    public static int MAX_LENGTH = 1280;

    public static int getResourceByName(Context context, String drawableName)
    {
        String uri = "drawable/" + drawableName;
        int res = context.getResources().getIdentifier(uri, null, context.getPackageName());
        return res;
    }

    /**
     * @param context
     * @param drawableName
     * @return
     */
    public Drawable getDrawableByName(Context context, String drawableName)
    {
        int imageResource = getResourceByName(context, drawableName);
        if (imageResource != 0)
        {
            Drawable image = context.getResources().getDrawable(imageResource);
            return image;
        }
        else
        {
            return null;
        }
    }

    /**
     * @param uri
     * @return
     * @throws Exception
     */
    public Bitmap loadBitmap(String uri) throws Exception
    {
        if (uri.startsWith("http"))
        {
            return loadBitmap(new URL(uri));
        }
        else
        {
            return loadBitmap(new File(uri));
        }
    }

    /**
     * @param uri
     * @return
     * @throws Exception
     */
    public Bitmap loadBitmap(String uri, int maxLength) throws Exception
    {
        if (uri.startsWith("http"))
        {
            return loadBitmap(new URL(uri));
        }
        else
        {
            return loadBitmap(new File(uri), maxLength);
        }
    }

    /**
     * @param url
     * @return
     * @throws Exception
     */
    public Bitmap loadBitmap(URL url) throws Exception
    {
        return loadBitmap(url, true);
    }

    /**
     * @param url
     * @return
     * @throws Exception
     */
    public Bitmap loadBitmap(URL url, boolean sampling) throws Exception
    {
        if (sampling)
        {
            return sampling(url);
        }
        else
        {
            Bitmap bitmap = null;
            try
            {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(20000);
                conn.setInstanceFollowRedirects(true);
                conn.setDoInput(true);
                conn.setDoOutput(false);
                conn.connect();
                InputStream is = conn.getInputStream();

                bitmap = BitmapFactory.decodeStream(is);
                conn.disconnect();
                //bitmap = BitmapFactory.decodeStream(instream);
            }
            catch (Exception e)
            {
                Log.e("grandroid", null, e);
                throw e;
            }
            return bitmap;
        }
    }

    /**
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public Bitmap loadBitmap(File file) throws FileNotFoundException
    {
        if (file.exists())
        {
            return sampling(file);
            //return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        else
        {
            Log.e("grandroid", "can't load " + file.getAbsolutePath());
            throw new FileNotFoundException();
        }
    }

    /**
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public Bitmap loadBitmap(File file, int maxLength) throws FileNotFoundException
    {
        if (file.exists())
        {
            return sampling(file, maxLength);
            //return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        else
        {
            Log.e("grandroid", "can't load " + file.getAbsolutePath());
            throw new FileNotFoundException();
        }
    }

    /**
     * @return
     * @throws Exception
     */
    public Bitmap sampling(URL url) throws Exception
    {
        Bitmap bitmap = null;
        try
        {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, o);
            //is.close();
            //Find the correct scale value. It should be the power of 2.

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true)
            {
                if (width_tmp <= MAX_LENGTH && height_tmp <= MAX_LENGTH)
                {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            try
            {
                return BitmapFactory.decodeStream(is, null, o2);
            }
            catch (Exception ex)
            {
                return loadBitmap(url, false);
            }
            finally
            {
                is.close();
                conn.disconnect();
            }
            //bitmap = BitmapFactory.decodeStream(is);

            //bitmap = BitmapFactory.decodeStream(instream);
        }
        catch (Exception e)
        {
            Log.e("grandroid", null, e);
            throw e;
        }
    }

    /**
     * @param context
     * @param uri
     * @return
     * @throws Exception
     */
    public Bitmap sampling(Context context, Uri uri) throws Exception
    {
        InputStream imageStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, o);
        imageStream.close();
        //Find the correct scale value. It should be the power of 2.

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true)
        {
            if (width_tmp <= MAX_LENGTH && height_tmp <= MAX_LENGTH)
            {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        //decode with inSampleSize
        imageStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try
        {
            return BitmapFactory.decodeStream(imageStream, null, o2);
        }
        finally
        {
            imageStream.close();
        }
    }

    /**
     * @param f
     * @return
     * @throws FileNotFoundException
     */
    public Bitmap sampling(File f) throws FileNotFoundException
    {
        //InputStream instream
        //decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

        //Find the correct scale value. It should be the power of 2.

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true)
        {
            if (width_tmp <= MAX_LENGTH &&
                    height_tmp <= MAX_LENGTH)
            {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        //decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        final boolean isGingerBread = Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1;
        if (isGingerBread)
        {
            o2.inSampleSize = 4;
        }
        else
        {
            o2.inSampleSize = scale;
        }
        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    }

    public Bitmap sampling(File f, int maxLength) throws FileNotFoundException
    {//TODO
        //InputStream instream
        //decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

        //Find the correct scale value. It should be the power of 2.

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true)
        {
            if (width_tmp <= maxLength &&
                    height_tmp <= maxLength)
            {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        JLog.d(JLog.JosephWang, "sampling scale " + scale);
        JLog.d(JLog.JosephWang, "sampling width_tmp " + width_tmp);
        JLog.d(JLog.JosephWang, "sampling height_tmp " + height_tmp);

        //decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        final boolean isGingerBread = Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1;
        if (isGingerBread)
        {
            o2.inSampleSize = 4;
        }
        else
        {
            o2.inSampleSize = scale;
        }
        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    }

    public Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter)
    {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }


    /**
     * @param url
     * @param path
     * @param filename
     * @return
     * @throws Exception
     */
    public Bitmap downloadAndLoad(URL url, String path, String filename) throws Exception
    {
        /*
         * 資料夾不在就先建立
         */
        File f = new File(Environment.getExternalStorageDirectory(), path);

        if (!f.exists())
        {
            f.mkdirs();
        }
        /*
         * 儲存相片檔
         */
        File n = new File(f, filename);
        return downloadAndLoad(url, n);
    }

    /**
     * @param url
     * @param file
     * @return
     * @throws Exception
     */
    public Bitmap downloadAndLoad(URL url, File file) throws Exception
    {

        Bitmap bmp = sampling(url);
        if (bmp != null)
        {
            saveBitmap(bmp, file, !url.getPath().endsWith("png"), 100);
            return bmp;
        }
        else
        {
            /*
             * Open a connection to that URL.
             */
            URLConnection ucon = url.openConnection();
            ucon.setConnectTimeout(10000);

            /*
             * Define InputStreams to read from the URLConnection.
             */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            /*
             * Read bytes to the Buffer until there is nothing more to read(-1).
             */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1)
            {
                baf.append((byte) current);
            }

            /*
             * Convert the Bytes read to a String.
             */
            FileOutputStream fos = new FileOutputStream(file);
            byte[] imageByteArray = baf.toByteArray();
            fos.write(imageByteArray);
            fos.close();
            return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        }

    }

    /**
     * @param bitmap
     * @param path
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String path)
    {
        long cnt = System.currentTimeMillis();
        return saveBitmap(bitmap, path, cnt + ".jpg", true, 100);
    }

    /**
     * @param bitmap
     * @param path
     * @param fileNamePrefix
     * @param fileNameSuffix
     * @param saveAsJPEG
     * @param quality
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String path, String fileNamePrefix, String fileNameSuffix, boolean saveAsJPEG, int quality)
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            try
            {
                /*
                 * 資料夾不在就先建立
                 */
                File f = new File(Environment.getExternalStorageDirectory(), path);
                if (!f.exists())
                {
                    f.mkdir();
                }
                NumberFormat nf = new DecimalFormat("0000");
                for (int fileIndex = 1; fileIndex <= 9999; fileIndex++)
                {
                    String fileName = fileNamePrefix + nf.format(fileIndex) + fileNameSuffix;
                    File n = new File(f, fileName);
                    if (!n.exists())
                    {
                        return saveBitmap(bitmap, path, fileName, saveAsJPEG, quality);
                    }
                }
            }
            catch (Exception e)
            {
                Log.e("grandroid", null, e);
            }
        }
        return null;
    }

    /**
     * @param bitmap
     * @param path
     * @param fileName
     * @param saveAsJPEG
     * @param quality
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String path, String fileName, boolean saveAsJPEG, int quality)
    {
        /*
         * 儲存檔案
         */
        if (bitmap != null)
        {
            /*
             * 檢視SDCard是否存在
             */
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            {
                /*
                 * SD卡不存在，顯示Toast訊息
                 */
            }
            else
            {
                try
                {
                    /*
                     * 資料夾不在就先建立
                     */
                    File f = new File(
                            Environment.getExternalStorageDirectory(), path);

                    if (!f.exists())
                    {
                        f.mkdir();
                    }

                    /*
                     * 儲存相片檔
                     */
                    File n = new File(f, fileName);
                    FileOutputStream bos =
                            new FileOutputStream(n.getAbsolutePath());
                    /*
                     * 檔案轉換
                     */
                    if (saveAsJPEG)
                    {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                    }
                    else
                    {
                        bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
                    }
                    /*
                     * 呼叫flush()方法，更新BufferStream
                     */
                    bos.flush();
                    /*
                     * 結束OutputStream
                     */
                    bos.close();
                    return n.getAbsolutePath();
                }
                catch (Exception e)
                {
                    Log.e("grandroid", null, e);
                }
            }
        }
        return null;
    }

    public static String saveBitmap(Bitmap bitmap, File file, boolean saveAsJPEG, int quality) throws Exception
    {
        /*
         * 儲存檔案
         */
        if (bitmap != null)
        {
            /*
             * 檢視SDCard是否存在
             */
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            {
                /*
                 * SD卡不存在，顯示Toast訊息
                 */
            }
            else
            {
                FileOutputStream bos =
                        new FileOutputStream(file.getAbsolutePath());
                /*
                 * 檔案轉換
                 */
                if (saveAsJPEG)
                {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                }
                else
                {
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
                }
                /*
                 * 呼叫flush()方法，更新BufferStream
                 */
                bos.flush();
                /*
                 * 結束OutputStream
                 */
                bos.close();
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * @param bitmap
     * @param scale
     * @return
     */
    public Bitmap resizeBitmap(Bitmap bitmap, float scale)
    {
        return resizeBitmap(bitmap, scale, false);
    }

    public Bitmap resizeBitmap(Bitmap bitmap, float scale, boolean recycleOldBitmap)
    {
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scale, scale);
        if (recycleOldBitmap)
        {
            int w = (int) (scale * bitmap.getWidth());
            int h = (int) (scale * bitmap.getHeight());
            Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(newBitmap);
            c.drawBitmap(bitmap, matrix, new Paint());
            return newBitmap;
        }
        else
        {
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return newBitmap;
        }
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int angle)
    {
        return rotateBitmap(bitmap, angle, false);
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int angle, boolean recycleOldBitmap)
    {
        Matrix mtx = new Matrix();
        mtx.postRotate(angle);
        try
        {
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, false);
        }
        finally
        {
            if (recycleOldBitmap)
            {
                bitmap.recycle();
            }
        }
    }

    public Bitmap resizeBitmapMaxWidth(Bitmap bitmap, int width, boolean recycleOldBitmap)
    {
        if (bitmap.getWidth() > width)
        {
            return resizeBitmap(bitmap, width / (float) bitmap.getWidth(), recycleOldBitmap);
        }
        else
        {
            return bitmap;
        }
    }

    public Bitmap resizeBitmapFitWidth(Bitmap bitmap, int width, boolean recycleOldBitmap)
    {
        return resizeBitmap(bitmap, width / (float) bitmap.getWidth(), recycleOldBitmap);
    }

    /**
     * @param bitmap
     * @param width
     * @return
     */
    public Bitmap resizeBitmapMaxWidth(Bitmap bitmap, int width)
    {
        if (bitmap.getWidth() > width)
        {
            return resizeBitmap(bitmap, width / (float) bitmap.getWidth());
        }
        else
        {
            return bitmap;
        }
    }

    public Bitmap resizeBitmapMaxHeight(Bitmap bitmap, int height, boolean recycleOldBitmap)
    {
        if (bitmap.getHeight() > height)
        {
            return resizeBitmap(bitmap, height / (float) bitmap.getHeight(), recycleOldBitmap);
        }
        else
        {
            return bitmap;
        }
    }

    public Bitmap resizeBitmapFitHeight(Bitmap bitmap, int height, boolean recycleOldBitmap)
    {
        return resizeBitmap(bitmap, height / (float) bitmap.getHeight(), recycleOldBitmap);
    }

    /**
     * @param bitmap
     * @param height
     * @return
     */
    public Bitmap resizeBitmapMaxHeight(Bitmap bitmap, int height)
    {
        if (bitmap.getHeight() > height)
        {
            return resizeBitmap(bitmap, height / (float) bitmap.getHeight());
        }
        else
        {
            return bitmap;
        }
    }

    /**
     * @param bmp1
     * @param bmp2
     * @param left
     * @param top
     * @return
     */
    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2, float left, float top)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, left, top, null);
        return bmOverlay;
    }

    /**
     * @param bmp
     * @return
     */
    public Bitmap cut(Bitmap bmp)
    {
        return cut(bmp, false);
    }

    public Bitmap cut(Bitmap bmp, boolean recycleOldBitmap)
    {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        if (w > h)
        {
            return cut(bmp, (w - h) / 2f, 0, recycleOldBitmap);
        }
        else if (h > w)
        {
            return cut(bmp, 0, (h - w) / 2f, recycleOldBitmap);
        }
        else
        {
            return bmp;
        }
    }

    /**
     * @param bmp
     * @param cutMarginX
     * @param cutMarginY
     * @return
     */
    public Bitmap cut(Bitmap bmp, float cutMarginX, float cutMarginY)
    {
        return cut(bmp, cutMarginX, cutMarginY, false);
    }

    public Bitmap cut(Bitmap bmp, float cutMarginX, float cutMarginY, boolean recycleOldBitmap)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth() - Math.round(2 * cutMarginX), bmp.getHeight() - Math.round(2 * cutMarginY), bmp.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp, -cutMarginX, -cutMarginY, null);
        if (recycleOldBitmap)
        {
            bmp.recycle();
        }
        Log.d("grandroid", "after cut, bitmap is recycled? " + bmOverlay.isRecycled());
        return bmOverlay;
    }

    /**
     * @param bmp
     * @param cutLeft
     * @param cutTop
     * @param cutRight
     * @param cutBottom
     * @return
     */
    public Bitmap cut(Bitmap bmp, int cutLeft, int cutTop, int cutRight, int cutBottom)
    {
        return cut(bmp, cutLeft, cutTop, cutRight, cutBottom, false);
    }

    public static Bitmap cut(Bitmap bmp, int cutLeft, int cutTop, int cutRight, int cutBottom, boolean recycleOldBitmap)
    {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth() - (cutLeft + cutRight), bmp.getHeight() - (cutTop + cutBottom), bmp.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp, -cutLeft, -cutTop, null);
        bmp.recycle();
        return bmOverlay;
    }

    /**
     * @param bitmap
     * @param edge
     * @return
     */
    public Bitmap square(Bitmap bitmap, float edge)
    {
        return square(bitmap, edge, false);
    }

    public Bitmap square(Bitmap bitmap, float edge, boolean recycleOldBitmap)
    {
        Matrix matrix = new Matrix();
        // resize the Bitmap
        try
        {
            if (bitmap.getWidth() > bitmap.getHeight())
            {
                matrix.postScale(edge / bitmap.getHeight(), edge / bitmap.getHeight());
                return Bitmap.createBitmap(bitmap, (bitmap.getWidth() - bitmap.getHeight()) / 2, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
            }
            else
            {
                matrix.postScale(edge / bitmap.getWidth(), edge / bitmap.getWidth());
                return Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - bitmap.getWidth()) / 2, bitmap.getWidth(), bitmap.getWidth(), matrix, true);
            }
        }
        finally
        {
            if (recycleOldBitmap)
            {
                bitmap.recycle();
            }
        }
    }

    public Bitmap round(Bitmap bitmap)
    {
        return round(bitmap, 12);
    }

    public Bitmap round(Bitmap bitmap, int roundPx)
    {
        return round(bitmap, bitmap.getWidth(), roundPx);
    }

    public Bitmap round(Bitmap bitmap, int edge, int roundPx)
    {
        Bitmap output = Bitmap.createBitmap(edge, edge, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, edge, edge);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (edge != bitmap.getWidth())
        {
            int minLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
            Matrix m = new Matrix();
            if (bitmap.getWidth() > bitmap.getHeight())
            {
                m.setTranslate(-(bitmap.getWidth() - minLength) / 2, 0);
            }
            else
            {
                m.setTranslate(0, -(bitmap.getHeight() - minLength) / 2);
            }
            m.setScale(edge / (float) minLength, edge / (float) minLength);
            canvas.drawBitmap(bitmap, m, paint);
        }
        else
        {
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }
        bitmap.recycle();
        return output;
    }

    public Bitmap circle(Bitmap bitmapimg)
    {
        int length = Math.min(bitmapimg.getWidth(), bitmapimg.getHeight());
        Bitmap output = Bitmap.createBitmap(length,
                length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, length,
                length);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(length / 2,
                length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        bitmapimg.recycle();
        return output;
    }

    public static byte[] toBytes(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }

    public static String toPngBase64String(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
    }

    public Bitmap toGrayscale(Bitmap bmp)
    {
        return toGrayscale(bmp, false);
    }

    public Bitmap getScaleBitMap(int deviceWidth, Bitmap origin)
    {
        int height = deviceWidth * origin.getHeight() / origin.getWidth();
        Bitmap bitmap = Bitmap.createScaledBitmap(origin, deviceWidth, height, true);
        return bitmap;
    }

    public Bitmap toGrayscale(Bitmap bmp, boolean recycleOldBitmap)
    {
        Bitmap bmpGrayscale = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmp, 0, 0, paint);
        if (recycleOldBitmap)
        {
            bmp.recycle();
        }
        return bmpGrayscale;
    }


    public static byte[] drawableToByte(Drawable d)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (d instanceof BitmapDrawable)
        {
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }
        else
        {
            return new byte[]{};
        }
    }


    @SuppressWarnings("deprecation")
    public Drawable decodeImage(InputStream inputStream)
    {
        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        int count = 0;
        try
        {
            while ((len = inputStream.read(buffer)) > -1)
            {
                if (len != 0)
                {
                    if (count + len > byteArr.length)
                    {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }
                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }
            return new BitmapDrawable(BitmapFactory.decodeByteArray(byteArr, 0, count));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public Drawable decodeImage(InputStream inputStream, int reqWidth, int reqHeight)
    {
        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        int count = 0;
        try
        {
            while ((len = inputStream.read(buffer)) > -1)
            {
                if (len != 0)
                {
                    if (count + len > byteArr.length)
                    {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }
                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            JLog.d(JLog.JosephWang, "decodeImage byteArr.length " + byteArr.length);
            return new BitmapDrawable(BitmapFactory.decodeByteArray(byteArr, 0, count, options));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0)
        {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        else
        {
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public static interface SavePhotoListener
    {
        public void savePhotoEvent(boolean successful);
    }

    public static void storeJpegInSdCard(final JActivity act, final String fileName, final byte[] imageData, final int contentLength, final SavePhotoListener listener)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (imageData)
                {
                    boolean isSuccessful = true;
                    try
                    {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
                        JLog.d(JLog.JosephWang, " storeJpegInApp path " + path);
                        File file = new File(path, fileName + ".jpg");
                        OutputStream fOut = new FileOutputStream(file);
                        fOut.write(imageData);
                        fOut.flush();
                        fOut.close();
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                        values.put(MediaStore.Images.Media.DESCRIPTION, fileName);
                        values.put(MediaStore.MediaColumns.DATE_ADDED, fileName);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        values.put(MediaStore.Images.Media.SIZE, imageData.length);
                        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                        act.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        isSuccessful = true;
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                        isSuccessful = false;
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        isSuccessful = false;
                    }
                    finally
                    {
                        if (act != null && listener != null && isSuccessful)
                        {
                            listener.savePhotoEvent(true);
                        }
                        else
                        {
                            listener.savePhotoEvent(false);
                        }
                    }
                }
            }
        });
    }

//    public static void savePhotoToAlbum(String imageFilePath)
//    {
//        savePhotoToAlbum(BaseApplication.getContext(), imageFilePath);
//    }
//
//    public static void savePhotoToAlbum(Context ctx, String imageFilePath)
//    {
//        MediaScannerConnection.scanFile(ctx, new String[]{imageFilePath},
//                new String[]{"image/jpeg"}, null);
//    }

    public static String saveImageToGalleryReturnPath(Bitmap photo, String title, String description)
    {
        return saveImageToGalleryReturnPath(BaseApplication.getContext(), photo, title, description);
    }

    public static String saveImageToGalleryReturnPath(Context ctx, Bitmap photo, String title, String description)
    {
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), photo, title, description);
        return path;
    }

    public static void replaceColorInBitmap(Bitmap resource, int color)
    {
        int width = resource.getWidth();
        int height = resource.getHeight();
        int[] allpixels = new int[width * height];

        resource.getPixels(allpixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < allpixels.length; i++)
        {
            if (allpixels[i] == Color.WHITE)
            {
                allpixels[i] = color ;
            }
        }
        resource.setPixels(allpixels, 0, width, 0, 0, width, height);
    }


    public static void changeBitmapColor(Bitmap sourceBitmap, ImageView image, int color)
    {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        image.setImageBitmap(resultBitmap);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
    }


    public static void recycleBitmap(Bitmap map)
    {
        if (map != null && !map.isRecycled())
        {
            map.recycle();
        }
    }

    public static Bitmap replaceColorInBitmap(Bitmap myBitmap, int targetColor, int replaceColor)
    {
        int width = myBitmap.getWidth();
        int height = myBitmap.getHeight();
        int[] allpixels = new int[width * height];

        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myBitmap.getPixels(allpixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < allpixels.length; i++)
        {
            if (allpixels[i] == targetColor)
            {
                allpixels[i] = replaceColor;
            }
        }
        resultBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);
        return resultBitmap;
    }
}
