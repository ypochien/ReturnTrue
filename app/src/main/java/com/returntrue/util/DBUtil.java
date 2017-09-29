package com.returntrue.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;

import com.returntrue.application.BaseApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by josephwang on 15/7/15.
 */
public class DBUtil
{
    public static boolean isDBExist(Context ctx, String dbName)
    {
        SQLiteDatabase checkDB = null;
        try
        {
            String myPath = "/data/data/" + ctx.getPackageName() + "/databases/" + dbName;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }

        if (checkDB != null)
        {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * 關閉Cursor
     *
     * @param cursor
     */
    public static void closeCursor(Cursor cursor)
    {
        if (cursor != null && !cursor.isClosed())
        {
            cursor.close();
            cursor = null;
        }
    }

    public static boolean notEmptyCursorAndMoveToFirst(Cursor cursor)
    {
        if (cursor != null && cursor.getCount() > 0 && !cursor.isClosed() && cursor.moveToFirst())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean notEmptyCursor(Cursor cursor)
    {
        if (cursor != null && cursor.getCount() > 0 && !cursor.isClosed())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isEmptyCursor(Cursor cursor)
    {
        return !notEmptyCursor(cursor);
    }

    /**
     * 印出Cursor的內容，開發Debug時使用
     *
     * @param cursor
     * @return
     */
    public static String printCursor(Cursor cursor)
    {
        StringBuilder retval = new StringBuilder();

        retval.append("|");
        final int numcolumns = cursor.getColumnCount();
        for (int column = 0; column < numcolumns; column++)
        {
            String columnName = cursor.getColumnName(column);
            retval.append(String.format("%-20s |", columnName.substring(0, Math.min(20, columnName.length()))));
        }
        retval.append("\n|");
        for (int column = 0; column < numcolumns; column++)
        {
            for (int i = 0; i < 21; i++)
            {
                retval.append("-");
            }
            retval.append("+");
        }
        retval.append("\n|");

        while (cursor.moveToNext())
        {
            for (int column = 0; column < numcolumns; column++)
            {
                String columnValue = cursor.getString(column);
                if (columnValue != null)
                {
                    columnValue = columnValue.substring(0, Math.min(20, columnValue.length()));
                }
                retval.append(String.format("%-20s |", columnValue));
            }
            retval.append("\n");
        }

        return retval.toString();
    }


    public static boolean backup(String dbName)
    {
        boolean isRight = false;
        boolean writeable = isSDCardWriteable();
        if (writeable)
        {
            String packageName = BaseApplication.getContext().getPackageName();
            File file = new File(Environment.getDataDirectory() + "/data/" + packageName + "/databases/" + dbName);
            File fileBackupDir = new File(Environment.getExternalStorageDirectory(), packageName + "/backup");
            if (!fileBackupDir.exists())
            {
                fileBackupDir.mkdirs();
            }

            if (file.exists())
            {

                File fileBackup = new File(fileBackupDir, dbName);
                try
                {
                    if (fileBackup.exists())
                    {
                        fileBackup.delete();
                    }
                    else
                    {
                        fileBackup.createNewFile();
                    }
                    copyFile(file, fileBackup);
                    isRight = true;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return isRight;
    }

    private static boolean writeSharePreference()
    {
        boolean isRight = false;
        boolean writeable = isSDCardWriteable();
        if (writeable)
        {
            String packageName = BaseApplication.getContext().getPackageName();
            File file = new File(Environment.getDataDirectory() + "/data/" + packageName + "/shared_prefs");
            File fileBackupDir = new File(Environment.getExternalStorageDirectory(), packageName + "/backup");
            if (!fileBackupDir.exists())
            {
                fileBackupDir.mkdirs();
            }

            if (file.exists())
            {
                File fileBackup = new File(fileBackupDir, "shared_prefs.txt");
                try
                {
                    fileBackup.createNewFile();
                    copyFile(file, fileBackup);
                    isRight = true;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return isRight;
    }


    public static void copyFile(File source, File dest)
    {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try
        {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (inputChannel != null)
                {
                    inputChannel.close();
                }
                if (outputChannel != null)
                {
                    outputChannel.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public static int getNextPage(Cursor cursor)
    {
        int page = 1;
        if (DBUtil.notEmptyCursor(cursor))
        {
            int currentPage = cursor.getCount() % 100;
            page = page + currentPage;
        }
        return page;
    }

    public static boolean isSDCardWriteable()
    {
        boolean isRight = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            isRight = true;
        }
        return isRight;
    }

}
