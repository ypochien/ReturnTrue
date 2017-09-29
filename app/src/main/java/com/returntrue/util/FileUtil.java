package com.returntrue.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.returntrue.application.BaseApplication;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by josephwang on 15/7/15.
 */
public class FileUtil
{
    public final static int BUFFER_SIZE = 4096;
    public final static int DEFAULT_BUFFER_SIZE = 1024;

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }

    public static String InputStreamTOString(InputStream in) throws Exception
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
        {
            outStream.write(data, 0, count);
        }
        data = null;
        return new String(outStream.toByteArray(), "UTF-8");
    }

    public static String InputStreamTOString(InputStream in, int size) throws Exception
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[size];
        int count = -1;
        while ((count = in.read(data, 0, size)) != -1)
        {
            outStream.write(data, 0, count);
        }
        // data = null;
        String result = new String(outStream.toByteArray(), "UTF-8");
        return result;
    }

    public static String getStringFromInputStream(InputStream is)
    {
        return getStringFromInputStream(is, "UTF-8");
    }

    public static String getStringFromInputStream(InputStream is, String uniCode)
    {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String line = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(is, uniCode));
            line = br.readLine();
            while (line != null)
            {
                sb.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                    is.close();
                    line = null;
                    System.gc();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString().trim();
    }

    /**
     * �?tring转�???nputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream StringTOInputStream(String in) throws Exception
    {
        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("UTF-8"));
        return is;
    }

    /**
     * �?nputStream转�???yte?��?
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }


    public static long folderSize(File directory)
    {
        long length = 0;
        for (File file : directory.listFiles())
        {
            if (file.isFile())
            {
                length += file.length();
            }
            else
            {
                length += folderSize(file);
            }
        }
        return length;
    }

    public static String getContent(InputStream input) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        String readLine;
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(input, Const.Encoding));
        while ((readLine = responseReader.readLine()) != null)
        {
            sb.append(readLine).append("\n");
        }
        responseReader.close();
        return sb.toString().trim();
    }

    public static InputStream StringToInputStream(String res) throws Exception
    {
        InputStream stream = new ByteArrayInputStream(res.getBytes("UTF-8"));
        return stream;
    }

    public static String convertStreamToString(InputStream is)
    {
        java.util.Scanner s = new java.util.Scanner(is, HTTP.UTF_8).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String getContent(HttpResponse response) throws IOException
    {
        return getContent(response.getEntity().getContent());
    }

    /**
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream byteTOInputStream(byte[] in) throws Exception
    {
        return new ByteArrayInputStream(in);
    }

    public static InputStream bitmapToInputStream(Bitmap bitmap)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
        return bs;
    }

    /**
     * @param in
     * @return
     * @throws Exception
     */
    public static String byteTOString(byte[] in) throws Exception
    {
        InputStream is = byteTOInputStream(in);
        return InputStreamTOString(is);
    }

    public static boolean copyFile(String sourcePath, String destPath)
    {
        return copyFile(new File(sourcePath),new File(destPath));
    }

    public static boolean copyFile(File source, File dest)
    {
        boolean successful = false;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try
        {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            successful = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            successful = false;
        }
        finally
        {
            if (inputChannel != null)
            {
                try
                {
                    inputChannel.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    successful = false;
                }
            }
            if (outputChannel != null)
            {
                try
                {
                    outputChannel.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    successful = false;
                }
            }
        }
        return successful;
    }

    public static boolean copyFileFromResource(int raw, String outputFileName)
    {
        // create files directory under /data/data/package name
        InputStream is;
        try
        {
            is = BaseApplication.getContext().getResources().openRawResource(raw);
            // copy ffmpeg file from assets to files dir
            OutputStream output = new BufferedOutputStream(new FileOutputStream(outputFileName));
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = is.read(buffer)))
            {
                output.write(buffer, 0, n);
            }
            close(output);
            close(is);
            return true;
        }
        catch (IOException e)
        {
            JLog.e("issue in coping binary from assets to data. ", e);
        }
        return false;
    }

    public static void close(InputStream inputStream)
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                // Do nothing
            }
        }
    }

    public static boolean fileExist(File file)
    {
        return fileExist(file.getPath());
    }

    public static boolean fileExist(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            File file = new File(path); //不可以判斷檔案長度file.length() > 0，因為非同步寫入時，檔案長度會是0
            return file.exists();
        }
        else
        {
            return false;
        }
    }

    public static void close(OutputStream outputStream)
    {
        if (outputStream != null)
        {
            try
            {
                outputStream.flush();
                outputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                // Do nothing
            }
        }
    }

    public static String getMimeType(String url)
    {
        String extension = "";
        try
        {
            Uri uri = Uri.parse(url);
            //Check uri format to avoid null
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))
            {
                //If scheme is a content
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(BaseApplication.getContext().getContentResolver().getType(uri));
            }
            else
            {
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return extension;
    }

    /**
     * Get text File From Assets
     * @param strFileName File Name
     * @return string
     */
    public static String getTextFormAssets(String strFileName)
    {
        String strContents = null;
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try
        {
            inputStream = BaseApplication.getContext().getAssets().open(strFileName);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[4096];

            int len;
            while ((len = inputStream.read(bytes)) > 0){
                byteArrayOutputStream.write(bytes, 0, len);
            }
            strContents = new String(byteArrayOutputStream.toByteArray(), "UTF8");

        }
        catch (IOException e)
        {
            e.printStackTrace();
            strContents = e.toString();
        } finally {
            try
            {
                if (inputStream != null )
                {
                    inputStream.close();
                }
                if (byteArrayOutputStream != null )
                {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                strContents = e.toString();
            }
        }

        return strContents;
    }
}
