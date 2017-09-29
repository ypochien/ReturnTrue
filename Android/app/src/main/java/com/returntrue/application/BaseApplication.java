package com.returntrue.application;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.returntrue.util.Const;
import com.returntrue.util.JLog;
import com.returntrue.util.JUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class BaseApplication extends MultiDexApplication
{
    protected static String TAG = "";

    private static Context mContext;
    private String loginToken = "";

    private SharedPreferences settings;
    public boolean isScreenON = true;

    public String getGcmToken()
    {
        String token = getStringFromPreference(Const.GCM_Token);
        JLog.d(TAG + " getGcmToken   " + token);
        return token;
    }

    public static ContentResolver getContentResolvers()
    {
        return BaseApplication.getContext().getContentResolver();
    }

    public static Context getContext()
    {
        return mContext;
    }

    public String getPictureDir()
    {
        String state = Environment.getExternalStorageState();
        File filesDir;
        // Make sure it's available
        if (Environment.MEDIA_MOUNTED.equals(state))
        { // We can read and write the media
            filesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        else
        {// Load another directory, probably local memory
            filesDir = getFilesDir();
        }
        if (filesDir != null)
        {
            return filesDir.getPath();
        }
        else
        {
            return "";
        }
    }

    public static BaseApplication getInstance()
    {
        return ((BaseApplication) mContext);
    }

    public String getLoginToken()
    {
        if (!TextUtils.isEmpty(loginToken))
        {
            JLog.d(TAG + " getLoginToken " + loginToken);
            return loginToken;
        }
        else
        {
            JLog.d(TAG + " getLoginToken " + getStringFromPreference(Const.ACCESS_TOKEN, "1"));
            return getStringFromPreference(Const.ACCESS_TOKEN, "1");
        }
    }

    public void setLoginToken(String loginToken)
    {
        this.loginToken = loginToken;
        if (!TextUtils.isEmpty(this.loginToken))
        {
            putStringToPreference(Const.ACCESS_TOKEN, this.loginToken);
        }
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        TAG = ((Object) this).getClass().getSimpleName();
        Log.d(TAG, "Application onCreate...");
        mContext = getApplicationContext();
        settings = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * 初始化App使用之API Url
     *
     * @return
     */
    protected String getApiUrl()
    {
        return "";
    }

    public Long getLongFromPreference(String key, long defaultValues)
    {
        return settings.getLong(key, defaultValues);
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的boolean值，若不存在則回傳defaultValues
     */
    public boolean getBooleanFromPreference(String key, boolean defaultValues)
    {
        return settings.getBoolean(key, defaultValues);
    }

    public String getStringFromPreference(String key, String defaultValues)
    {
        return settings.getString(key, defaultValues);
    }

    public String getStringFromPreference(String key)
    {
        return settings.getString(key, "");
    }

    public void putStringToPreference(String key, String value)
    {
        settings.edit().putString(key, value).apply();
    }

    public void putIntegerToPreference(String key, int value)
    {
        settings.edit().putInt(key, value).apply();
    }

    public void putIntegerToPreference(String key)
    {
        putIntegerToPreference(key, 0);
    }

    public void putLongToPreference(String key, long value)
    {
        settings.edit().putLong(key, value).apply();
    }

    public void putFloatToPreference(String key, float value)
    {
        settings.edit().putFloat(key, value).apply();
    }

    public void putFloatToPreference(String key)
    {
        putFloatToPreference(key, 0f);
    }

    public float getFloatFromPreference(String key, float defaultValues)
    {
        return settings.getFloat(key, defaultValues);
    }

    public float getFloatFromPreference(String key)
    {
        return getFloatFromPreference(key, 0f);
    }

    public void putBooleanToPreference(String key, boolean value)
    {
        settings.edit().putBoolean(key, value).apply();
    }

    public void putStringArrayToPreference(String key, ArrayList<String> list)
    {
        if (JUtil.notEmpty(list))
        {
            settings.edit().putString(key, convertToString(list)).apply();
        }
    }

    public ArrayList<String> getStringArrayFromPreference(String key)
    {
        String result = settings.getString(key, "");
        if (!TextUtils.isEmpty(result))
        {
            ArrayList<String> array = convertToArray(result);
            return array;
        }
        return null;
    }

    public ArrayList<String> convertToArray(String string)
    {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(",")));
        return list;
    }

    public int getIntegerFromPreference(String key, int defaultValues)
    {
        return settings.getInt(key, defaultValues);
    }

    public String convertToString(ArrayList<String> list)
    {
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : list)
        {
            sb.append(delim);
            sb.append(s);
            delim = ",";
        }
        return sb.toString();
    }
}
