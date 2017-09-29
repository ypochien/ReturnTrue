package com.returntrue.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.returntrue.application.BaseApplication;
import com.returntrue.framework.JActivity;
import com.returntrue.util.ui.JDialog;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author JosephWang
 */

/***
 * Code Name Version
 * Api level (no code name) 1.0
 * API level 1 (no code name) 1.1
 * API level 2 Cupcake 1.5
 * API level 3, NDK 1 Donut 1.6
 * API level 4, NDK 2 Eclair 2.0
 * API level 5 Eclair 2.0.1
 * API level 6 Eclair 2.1
 * API level 7, NDK 3 Froyo 2.2.x
 * API level 8, NDK 4 Gingerbread 2.3 - 2.3.2
 * API level 9, NDK 5 Gingerbread 2.3.3 - 2.3.7
 * API level 10 Honeycomb 3.0
 * API level 11 Honeycomb 3.1
 * API level 12,NDK 6 Honeycomb 3.2.x
 * API level 13 Ice Cream Sandwich 4.0.1 - 4.0.2
 * API level 14,NDK 7 Ice Cream Sandwich 4.0.3 - 4.0.4
 * API level 15,NDK 8 Jelly Bean 4.1.x
 * API level 16 Jelly Bean 4.2.x
 * API level 17 Jelly Bean 4.3.x
 * API level 18 KitKat 4.4 - 4.4.2
 * API level 19 KitKat (for wearable)4.4
 * API level 20 Lollipop 5.0
 * API level 21
 */


public class JUtil
{
    public static ThreadFactory threadFactory = Executors.defaultThreadFactory();

    public static double distance(double lat1, double lon1, double lat2, double lon2)
    {
        double R = 6371; // km (change this constant to get miles)
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000;
    }

    /**
     * @return result
     */
    public static int roundingFloatToInt(float input)
    {
        BigDecimal decimal = new BigDecimal(input);
        decimal.setScale(1, BigDecimal.ROUND_HALF_UP);
        return decimal.intValue();
    }

    @SuppressWarnings("deprecation")
    public static void setTextEllipsizeToEnd(final TextView textView)
    {
        if (textView != null && textView.getText() != null && textView.getText().toString() != null && textView.getText().toString().length() > 0)
        {
            ViewTreeObserver vto = textView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    textView.setVisibility(View.INVISIBLE);
                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    CharSequence charSequence = TextUtils.ellipsize(textView.getText(), textView.getPaint(), textView.getMeasuredWidth() - 5, TextUtils.TruncateAt.END);
                    textView.setText(charSequence);
                    textView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public static RejectedExecutionHandler executionHandler = new RejectedExecutionHandler()
    {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
        {
            try
            {
                executor.remove(r);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public static void allowNetWorkRunOnUI()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static boolean checkInternet(Context context)
    {
        if (context == null)
        {
            return true;
        }
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * corePoolSize : the number of threads to keep in the pool, even if they
     * are idle, unless allowCoreThreadTimeOut is set .</br> maximumPoolSize :
     * the maximum number of threads to allow in the pool.</br> keepAliveTime :
     * when the number of threads is greater than the core, this is the maximum
     * time that excess idle threads will wait for new tasks before terminating.
     * </br> unit : the time unit for the keepAliveTime argument workQueue : the
     * queue to use for holding tasks before they are executed. This queue will
     * hold only the Runnable tasks submitted by the execute method.
     * threadFactory : the factory to use when the executor creates a new thread
     * handler : the handler to use when execution is blocked because the thread
     * bounds and queue capacities are reached
     *
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getNewThreadPoolExecutor(int corePoolSize, int maximumPoolSize, int lockingQueueSize)
    {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 300, TimeUnit.NANOSECONDS, new ArrayBlockingQueue<Runnable>(lockingQueueSize), threadFactory, executionHandler);
    }

    public static byte[] convertIntToByteArray(int res)
    {
        return ByteBuffer.allocate(4).putInt(res).array();
    }


    public static String[] listToArray(List<String> data)
    {
        String[] result = new String[data.size()];
        for (int i = 0; i < data.size(); i++)
        {
            result[i] = data.get(i);
        }
        return result;
    }

    public static boolean hasInternet()
    {
        return hasInternet(BaseApplication.getContext());
    }

    public boolean hasInternetAndShowAlert(JActivity activity)
    {
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable())
        {
            activity.cancelLoading();
            JDialog.showMessage(activity,
                    Const.Message_Title,
                    "無網路連線",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
            return false;
        }
        else
        {
            return true;
        }
    }


    public static boolean hasInternet(Context context)
    {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected() || !info.isAvailable())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static int dip2px(float dipValue)
    {
        return dip2px(BaseApplication.getContext(), dipValue);
    }

    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static float dip2pxFloat(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public static float dip2pxFloat(float dipValue)
    {
        final float scale = BaseApplication.getContext().getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue)
    {
        return px2dip(BaseApplication.getContext(), pxValue);
    }

    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * **** android.permission.GET_TASKS 已被depreciatin 需要移除 *************
     */
    public static boolean isLastOneActivity(Class<? extends FragmentActivity> act)
    {
        return isLastOneActivity(BaseApplication.getContext(), act);
    }

    public static boolean isLastOneActivity(Context ctx, Class<? extends FragmentActivity> act)
    {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 21)
        {
            List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();
            if (!tasks.isEmpty() && tasks.size() > 0)
            {
                if (tasks.get(0).processName.contains(act.getSimpleName()))
                {
                    return true;
                }
            }
        }
        else
        {
            List<RunningTaskInfo> tasks = manager.getRunningTasks(1);
            if (!tasks.isEmpty())
            {
                if (tasks.get(0).numActivities == 1)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void performTouchDown(View view)
    {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = view.getLeft() + 1.1f;
        float y = view.getTop() + 2.2f;
        // List of meta states found here:
        // developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState);
        view.dispatchTouchEvent(motionEvent);
    }

    public static void performTouchUp(View view)
    {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.1f;
        float y = 0.2f;
        // List of meta states found here:
        // developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, metaState);
        view.dispatchTouchEvent(motionEvent);
    }

    public static void performTouchMove(View view)
    {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = view.getLeft() + 1.1f;
        float y = view.getTop() + 2.2f;
        // List of meta states found here:
        // developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, metaState);
        view.dispatchTouchEvent(motionEvent);
    }

    public static IntentFilter getIntentFilter(String... action)
    {
        IntentFilter filter = new IntentFilter();
        for (String each : action)
        {
            filter.addAction(each);
        }
        return filter;
    }

    public static IntentFilter getIntentFilter(List<String> action)
    {
        IntentFilter filter = new IntentFilter();
        for (String each : action)
        {
            filter.addAction(each);
        }
        return filter;
    }

    public static boolean notEmpty(File file)
    {
        return (file != null && file.isFile() && file.exists() && file.length() > 0);
    }

    public static boolean notEmpty(ViewPager list)
    {
        return (list != null && list.getAdapter() != null && list.getAdapter().getCount() > 0);
    }

    public static boolean notEmpty(float[] list)
    {
        return (list != null && list.length > 0);
    }

    public static boolean notEmpty(int[] list)
    {
        return (list != null && list.length > 0);
    }

    public static boolean notEmpty(AdapterView<?> list)
    {
        if (list != null && list.getAdapter() != null && list.getAdapter().getCount() > 0)
        {
            return true;
        }
        return false;
    }

    public static boolean notEmpty(AdapterView<?> list, int position)
    {
        if (list != null && list.getAdapter() != null && list.getAdapter().getCount() > 0
                && position < list.getAdapter().getCount() && list.getAdapter().getItem(position) != null)
        {
            return true;
        }
        return false;
    }

    public static boolean notEmpty(Collection<?> list)
    {
        if (list != null && list.size() > 0)
        {
            return true;
        }
        return false;
    }

    public static boolean notEmpty(Map<?, ?> map)
    {
        return (map != null && map.size() > 0);
    }

    public static boolean notEmpty(Object[] data)
    {
        return (data != null && data.length > 0);
    }

    public static boolean notEmpty(Set<?> set)
    {
        return (set != null && set.size() > 0);
    }

    public static void startClearTopIntent(FragmentActivity act, Intent intent)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            act.startActivity(intent);
        }
        else
        {
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            act.startActivity(mainIntent);
        }
        act.finish();
    }

    public static void startClearTopIntent(JActivity act, Intent intent)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            act.startActivity(intent);
        }
        else
        {
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            act.startActivity(mainIntent);
        }
        act.finish();
    }

    public static Parcelable getParcelableInIntent(JActivity ctx, String tag)
    {
        if (ctx.getIntent() != null &&
                ctx.getIntent().getExtras() != null &&
                ctx.getIntent().getExtras().getParcelable(tag) != null)
        {
            return ctx.getIntent().getExtras().getParcelable(tag);
        }
        return null;
    }

    public static String getStringInIntent(JActivity ctx, String tag)
    {
        if (ctx.getIntent() != null && ctx.getIntent().getExtras() != null && !TextUtils.isEmpty(ctx.getIntent().getExtras().getString(tag)))
        {
            return ctx.getIntent().getExtras().getString(tag);
        }
        return "";
    }

    public static boolean hasStringInIntentData(JActivity ctx, String tag)
    {
        if (ctx.getIntent() != null && ctx.getIntent().getExtras() != null)
        {
            return TextUtils.isEmpty(ctx.getIntent().getExtras().getString(tag));
        }
        return false;
    }

    public static boolean hasStringInIntentData(Intent data, String tag)
    {
        if (data != null && data.getExtras() != null)
        {
            return TextUtils.isEmpty(data.getExtras().getString(tag));
        }
        return false;
    }

    public static boolean hasIntentExtras(Intent intent)
    {
        if (intent != null && intent.getExtras() != null)
        {
            return true;
        }
        return false;
    }

    public static boolean hasIntentData(Intent intent)
    {
        if (intent != null && intent.getData() != null)
        {
            return true;
        }
        return false;
    }

    public static boolean hasScheme(JActivity act, String scheme)
    {
        return hasScheme(act.getIntent(), scheme);
    }

    public static boolean hasScheme(Intent intent, String scheme)
    {
        if (intent != null && intent.getData() != null && !TextUtils.isEmpty(intent.getScheme()) && intent.getScheme().equals(scheme))
        {
            return true;
        }
        return false;
    }

    public static boolean hasIntentExtras(Activity act)
    {
        return hasIntentExtras(act.getIntent());
    }

    public static String getStringFromIntent(FragmentActivity act, String tag)
    {
        if (act.getIntent() != null && act.getIntent().getExtras() != null)
        {
            return act.getIntent().getExtras().getString(tag);
        }
        return "";
    }

    public static boolean getBooleanFromIntent(Activity act, String tag)
    {
        if (act.getIntent() != null && act.getIntent().getExtras() != null)
        {
            return act.getIntent().getExtras().getBoolean(tag);
        }
        return false;
    }

    public static boolean getBooleanFromIntent(Fragment fragment, String tag)
    {
        return getBooleanFromIntent(fragment.getActivity(), tag);
    }

    public static String getStringFromIntent(JActivity act, String tag)
    {
        return getStringFromIntent(act, tag, "");
    }

    public static String getStringFromIntent(JActivity act, String tag, String defaultStr)
    {
        if (act.getIntent() != null && act.getIntent().getExtras() != null)
        {
            return act.getIntent().getExtras().getString(tag);
        }
        return defaultStr;
    }

    public static int getIntFromIntent(Activity act, String tag, int defaultStr)
    {
        if (act.getIntent() != null && act.getIntent().getExtras() != null)
        {
            return act.getIntent().getExtras().getInt(tag);
        }
        return defaultStr;
    }

    public static int getIntFromIntent(Activity act, String tag)
    {
        return getIntFromIntent(act, tag, -1);
    }

    @SuppressWarnings("deprecation")
    public static void setBackGround(View v, int drawable)
    {
        v.setBackgroundDrawable(BaseApplication.getContext().getResources().getDrawable(drawable));
    }

    public static void openBrowser(Activity start, String url)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            start.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(BaseApplication.getContext(), "尚未安裝瀏覽器App", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static Comparator<Integer> getIntegerComparator()
    {
        return new Comparator<Integer>()
        {
            @Override
            public int compare(Integer lhs, Integer rhs)
            {
                return lhs.compareTo(rhs);
            }
        };
    }

    public static boolean isAPPInstall(String packageName)
    {
        return isAPPInstall(BaseApplication.getContext(), packageName);
    }

    public static boolean isAPPInstall(Context context, String packageName)
    {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null)
        {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static boolean isAppOnForeground()
    {
        return isAppOnForeground(BaseApplication.getContext());
    }

    public static boolean isAppOnForeground(Context context)
    {
        ActivityManager mActivityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        String mPackageName = context.getPackageName();
        List<RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0)
        {
            if (mPackageName.equals(tasksInfo.get(0).topActivity.getPackageName()))
            {
                return true;
            }
        }
        return false;
    }

    public static void objectArrayToArryList(ArrayList list, Object[] resource)
    {
        if (notEmpty(resource))
        {
            for (Object each : resource)
            {
                list.add(each);
            }
        }
    }

    public static boolean smallThanTens(int res)
    {
        if (res < 10 && res > 0)
        {
            return true;
        }
        return false;
    }

    public static boolean smallThanTens(String res)
    {
        if (TextUtils.isDigitsOnly(res) && Integer.parseInt(res) < 10 && Integer.parseInt(res) > 0)
        {
            return true;
        }
        return false;
    }

    public static String getString(@StringRes int stringId)
    {
        return BaseApplication.getContext().getResources().getString(stringId);
    }

    public static int getColor(@ColorRes int colorId)
    {
        return BaseApplication.getContext().getResources().getColor(colorId);
    }

    public static int getInt(@IntegerRes int intId)
    {
        return BaseApplication.getContext().getResources().getInteger(intId);
    }

    public static String[] getStringArray(@ArrayRes int stringId)
    {
        return BaseApplication.getContext().getResources().getStringArray(stringId);
    }

    public static int[] getIntArray(int stringId)
    {
        return BaseApplication.getContext().getResources().getIntArray(stringId);
    }

    public static boolean isRightInputs(String input, String pattern)
    {
        Pattern resular =
                Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = resular.matcher(input);
        return matcher.find();
    }

    public static String getJSonStringByTag(JsonElement element, String tag)
    {
        if (element.getAsJsonObject().get(tag) != null)
        {
            String result = element.getAsJsonObject().get(tag).toString();
            if (!TextUtils.isEmpty(result))
            {
                result = result.replace("\"", "");
            }
            return result;
        }
        return null;
    }

    public static boolean hasJsonArrayByTag(JsonObject object, String tag)
    {
        if (object.get(tag).getAsJsonArray() != null &&
                object.get(tag).getAsJsonArray().size() > 0)
        {
            return true;
        }
        return false;
    }

    public static JsonArray getJsonArrayByTag(JsonObject object, String tag)
    {
        if (object.get(tag).getAsJsonArray() != null &&
                object.get(tag).getAsJsonArray().size() > 0)
        {
            return object.get(tag).getAsJsonArray();
        }
        return null;
    }


    public static boolean hasJSonStringByTag(JsonElement element, String tag)
    {
        return !TextUtils.isEmpty(element.getAsJsonObject().get(tag).toString());
    }


    public static void adjustViewParams(ViewGroup group, int dip)
    {
        ViewGroup.LayoutParams params = group.getLayoutParams();
        params.width = dip2px(dip);
        group.setLayoutParams(params);
        group.requestLayout();
    }

    public static void printVolleyError(VolleyError error)
    {
        try
        {
            if (error != null && !TextUtils.isEmpty(error.getMessage()))
            {
                JLog.d("VolleyError", " imageError " + error.getMessage());
                if (error.networkResponse != null && !TextUtils.isEmpty(error.networkResponse.toString()))
                {
                    JLog.d("VolleyError", " onErrorResponse: " + error.networkResponse.toString());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void shareMessageToLine(JActivity act, String text)
    {
        if (isAPPInstall("jp.naver.line.android"))
        {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_TEXT, text);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            StringBuffer buffer = new StringBuffer();
            buffer.append("line://msg/text/" + text);
            intent.setData(Uri.parse(buffer.toString()));

            act.startActivity(intent);
        }
        else
        {
            invokeInstallAppAction(act, "jp.naver.line.android");
        }
    }

    public static void invokeInstallAppAction(Activity act, String packageName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try
        {
            intent.setData(Uri.parse("market://details?id=" + packageName));
            act.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
            intent.setData(Uri.parse("https://market.android.com/details?id=" + packageName));
            act.startActivity(intent);
        }
    }

    public static void sharePhotoToline(Activity act, String path)
    {
        sharePhotoToline(act, new File(path));
    }

    public static void sharePhotoToline(Activity act, File file)
    {
        if (isAPPInstall("jp.naver.line.android"))
        {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            act.startActivity(intent);
        }
        else
        {
            invokeInstallAppAction(act, "jp.naver.line.android");
        }
    }

    public static void shareVideoToline(Activity act, String path)
    {
        shareVideoToline(act, new File(path));
    }

    public static void shareVideoToline(Activity act, File file)
    {
        if (isAPPInstall("jp.naver.line.android"))
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setClassName("jp.naver.line.android", "jp.naver.line.android.activity.selectchat.SelectChatActivity");
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            act.startActivity(intent);
        }
        else
        {
            invokeInstallAppAction(act, "jp.naver.line.android");
        }
    }


    /**
     * 將資料集合分成某個數值組的小集合
     *
     * @param data
     * @return
     */
    public static void sortStringDataByCountMutiple(ArrayList<List<String>> deviceID, List<String> data, int count)
    {
        if (count > 0 && data != null && data.size() > 0)
        {
            int mutiple = (data.size() % count == 0) ? data.size() / count : data.size() / count + 1;// 6的除數，不是5的倍數，要多一組String_array
            for (int index = 0; index < mutiple; index++)
            {
                if (mutiple > 1)
                {
                    if (index + 1 < mutiple && index > 0 && index < mutiple - 1)
                    {// 其他部分
                        deviceID.add(data.subList(index * count, (index + 1) * count));
                    }
                    else if (index == 0)
                    {// 第一組
                        deviceID.add(data.subList(0, (1) * (count - 1) + 1));
                    }
                    else if (index == mutiple - 1)
                    {// 最後一組
                        deviceID.add(data.subList((count * index), data.size()));
                    }
                }
                else
                {// 不足5個
                    deviceID.add(data.subList(0, data.size()));
                }
            }
        }
    }

    public static boolean isTablet()
    {
        return (BaseApplication.getContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void updateShaOne(Context ctx)
    {
        try
        {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    public static void uninstallApp(Context ctx, String packageName)
    {
        Uri packageUri = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        ctx.startActivity(uninstallIntent);
    }

    public static void openInternetSetting(Activity act, int requestCode)
    {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivityForResult(intent, requestCode);
    }

    public static void updateHashKey(Activity act)
    {
        updateHashKey(act, DeviceUtil.getAndroidId(act));
    }

    public static void updateHashKey(Activity act, String appId)
    {
        PackageInfo info;
        try
        {
            info = act.getPackageManager().getPackageInfo(appId, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        }
        catch (PackageManager.NameNotFoundException e1)
        {
            Log.e("name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e)
        {
            Log.e("no such an algorithm", e.toString());
        }
        catch (Exception e)
        {
            Log.e("exception", e.toString());
        }
    }

    private static MathContext halfMathContext(int position)
    {
        MathContext mc = new MathContext(position, RoundingMode.HALF_UP);
        return mc;
    }

    public static float subtract(float v1, float v2)
    {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        b1.setScale(5, RoundingMode.HALF_UP);
        b2.setScale(5, RoundingMode.HALF_UP);
        return b1.subtract(b2, halfMathContext(5)).floatValue();
    }

    public static float multiply(float v1, float v2)
    {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        b1.setScale(5, RoundingMode.HALF_UP);
        b2.setScale(5, RoundingMode.HALF_UP);
        return b1.multiply(b2, halfMathContext(5)).floatValue();
    }

    public static float divide(float v1, float v2)
    {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        b1.setScale(5, RoundingMode.HALF_UP);
        b2.setScale(5, RoundingMode.HALF_UP);
        return b1.divide(b2, halfMathContext(5)).floatValue();
    }

    public static float divide(int v1, int v2)
    {
        return divide(v1, v2, 5);
    }

    public static float divide(int v1, int v2, int position)
    {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        return b1.divide(b2, halfMathContext(position)).floatValue();
    }

    public static float add(float v1, float v2)
    {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        b1.setScale(5, RoundingMode.HALF_UP);
        b2.setScale(5, RoundingMode.HALF_UP);
        return b1.add(b2, halfMathContext(5)).floatValue();
    }

    public static long multiply(long v1, long v2)
    {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        return b1.multiply(b2, halfMathContext(5)).longValue();
    }

    public static long divide(long v1, long v2)
    {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        return b1.divide(b2, halfMathContext(5)).longValue();
    }

    public static Activity getCurrentActivity() throws Exception
    {
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        HashMap activities = (HashMap) activitiesField.get(activityThread);
        for (Object activityRecord : activities.values())
        {
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord))
            {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }
        return null;
    }

    public static boolean notReclaimActivity(FragmentActivity act)
    {
        return (act != null && !act.isFinishing());
    }

    public static boolean notReclaimActivity(Activity act)
    {
        return (act != null && !act.isFinishing());
    }

    public static boolean notReclaimActivity(Context act)
    {
        if (act != null)
        {
            if (act instanceof FragmentActivity)
            {
                return notReclaimActivity((FragmentActivity) act);
            }
            else if (act instanceof Activity)
            {
                return notReclaimActivity((Activity) act);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public static <T extends Object> T[] mapToList(Map<T, T> map, Class<T[]> inClass)
    {
        T[] arrays = null;
        try
        {
            arrays = map.values().toArray(inClass.newInstance());
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return arrays;
    }

    public static <T extends Object> ArrayList<T> mapToArryList(Map<T, T> map)
    {
        return new ArrayList<T>(map.values());
    }

    public static String md5(String string) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // md5.update(s.getBytes(), 0, s.length());
            md5.reset();
            md5.update(string.getBytes(Charset.forName("UTF-8")));
            // String signature = new BigInteger(1, md5.digest()).toString(16);
            // // JLog.d(JLog.JosephWang, "Signature original " + s + " md5 " +
            // // signature);
            //
            // return signature;
            String result = toHexString(md5.digest());
            JLog.d(JLog.JosephWang, "md5 " + result.toUpperCase());
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 將字符串中的每個字符轉換為十六進制
    private static String toHexString(byte[] bytes) {

        StringBuilder hexstring = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexstring.append('0');
            }
            hexstring.append(hex);

        }

        return hexstring.toString();
    }
}