package com.returntrue.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.returntrue.application.BaseApplication;
import com.returntrue.framework.JActivity;
import com.returntrue.util.ui.JDialog;
import com.returntrue.util.ui.UIAdjuster;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by josephwang on 2016/9/4.
 */
public class IntentUtil
{
    public static final String COLOR_DICTIONARY_SEARCH_ACTION = "colordict.intent.action.SEARCH";
    public static final String COLOR_DICTIONARY_EXTRA_QUERY = "EXTRA_QUERY";
    public static final String COLOR_DICTIONARY_EXTRA_FULLSCREEN = "EXTRA_FULLSCREEN";
    public static final String COLOR_DICTIONARY_EXTRA_HEIGHT = "EXTRA_HEIGHT";
    public static final String COLOR_DICTIONARY_EXTRA_WIDTH = "EXTRA_WIDTH";
    public static final String COLOR_DICTIONARY_EXTRA_GRAVITY = "EXTRA_GRAVITY";
    public static final String COLOR_DICTIONARY_EXTRA_MARGIN_LEFT = "EXTRA_MARGIN_LEFT";
    public static final String COLOR_DICTIONARY_EXTRA_MARGIN_TOP = "EXTRA_MARGIN_TOP";
    public static final String COLOR_DICTIONARY_EXTRA_MARGIN_BOTTOM = "EXTRA_MARGIN_BOTTOM";
    public static final String COLOR_DICTIONARY_EXTRA_MARGIN_RIGHT = "EXTRA_MARGIN_RIGHT";
    public static final String COLOR_DICTIONARY_ID = "com.socialnmobile.colordict";

    /**
     * 撥打電話
     * @param context
     * @param no 電話號碼
     * @return string
     */
    public static void makeCall(final Context context, final String no)
    {
        makeCall(context, Const.Message_Title, no);
    }

    public static void makeCall(final Context context,final String title ,final String no)
    {
        JDialog.showDialog(context, title,  "撥打電話聯絡：" + no + " ?",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index)
                    {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        if (no.contains("#"))
                        {
                            String[] list = no.split("#");
                            StringBuilder result = new StringBuilder();
                            for (int i = 0; i < list.length; i++)
                            {
                                result.append(list[i]);
                                if (i != list.length - 1)
                                {
                                    result.append(",");
                                }
                            }
                            intent.setData(Uri.parse("tel:" + result.toString()));
                        }
                        else
                        {
                            intent.setData(Uri.parse("tel:" + no));
                        }
                        context.startActivity(intent);
                    }
                });
    }

    public static void callColorDictionary(Activity act, String textToTranslate)
    {
        if (isAPPInstall(act, COLOR_DICTIONARY_ID))
        {
            Intent intent = new Intent(COLOR_DICTIONARY_SEARCH_ACTION);
            intent.putExtra(COLOR_DICTIONARY_EXTRA_QUERY, textToTranslate); //Search Query
            intent.putExtra(COLOR_DICTIONARY_EXTRA_FULLSCREEN, false); //
            intent.putExtra(COLOR_DICTIONARY_EXTRA_HEIGHT, UIAdjuster.dip2px(300)); //400pixel, if you don't specify, fill_parent"
            intent.putExtra(COLOR_DICTIONARY_EXTRA_GRAVITY, Gravity.BOTTOM);
            intent.putExtra(COLOR_DICTIONARY_EXTRA_MARGIN_LEFT, 0);
            act.startActivity(intent);
        }
        else
        {
            invokeInstallAppAction(act, COLOR_DICTIONARY_ID);
        }
    }

    public static void invokeOthersAppLauncher(String packageName)
    {
        PackageManager pm = BaseApplication.getContext().getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        BaseApplication.getContext().startActivity(intent);
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

    public static Parcelable getParcelable(Activity ctx, String tag)
    {
        if (ctx.getIntent() != null &&
                ctx.getIntent().getExtras() != null &&
                ctx.getIntent().getExtras().getParcelable(tag) != null)
        {
            return ctx.getIntent().getExtras().getParcelable(tag);
        }
        return null;
    }

    public static <T extends Parcelable> T[] getParelableArray(Activity ctx, Class<? extends T[]> genericArray, String tag)
    {
        Parcelable[] arrays = getParcelableArrayInIntent(ctx, tag);
        if (arrays != null)
        {
            return Arrays.copyOf(arrays, arrays.length, genericArray);
        }
        return null;
    }

    public static <T extends Parcelable> ArrayList<T> getParelableArrayList(Activity ctx, String tag)
    {
        if (ctx.getIntent() != null &&
                ctx.getIntent().getExtras() != null &&
                ctx.getIntent().getExtras().getParcelableArrayList(tag) != null)
        {
            return (ArrayList<T>) ctx.getIntent().getExtras().getParcelableArrayList(tag);
        }
        return null;
    }

    public static Parcelable[] getParcelableArrayInIntent(Activity ctx, String tag)
    {
        if (ctx.getIntent() != null &&
                ctx.getIntent().getExtras() != null &&
                ctx.getIntent().getExtras().getParcelableArray(tag) != null)
        {
            return ctx.getIntent().getExtras().getParcelableArray(tag);
        }
        return null;
    }

    public static String getStringInIntent(JActivity ctx, String tag)
    {
        if (ctx.getIntent() != null &&
                ctx.getIntent().getExtras() != null &&
                !TextUtils.isEmpty(ctx.getIntent().getExtras().getString(tag)))
        {
            return ctx.getIntent().getExtras().getString(tag);
        }
        return "";
    }

    public static boolean hasStringInIntentData(JActivity ctx, String tag)
    {
        if (ctx.getIntent() != null &&
                ctx.getIntent().getExtras() != null)
        {
            return TextUtils.isEmpty(ctx.getIntent().getExtras().getString(tag));
        }
        return false;
    }

    public static boolean hasStringInIntentData(Intent data, String tag)
    {
        if (data != null &&
                data.getExtras() != null)
        {
            return TextUtils.isEmpty(data.getExtras().getString(tag));
        }
        return false;
    }

    public static boolean hasIntentExtras(Intent intent)
    {
        if (intent != null &&
                intent.getExtras() != null)
        {
            return true;
        }
        return false;
    }

    public static boolean hasIntentData(Intent intent)
    {
        if (intent != null &&
                intent.getData() != null)
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
        if (intent != null &&
                intent.getData() != null &&
                !TextUtils.isEmpty(intent.getScheme()) &&
                intent.getScheme().equals(scheme))
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

    public static String getStringFromIntent(Activity act, String tag)
    {
        return getStringFromIntent(act, tag, "");
    }

    public static String getStringFromIntent(Activity act, String tag, String defaultStr)
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

    public static <T extends Parcelable> T getParcelable(JActivity ctx, String tag)
    {
        if (ctx.getIntent() != null &&
                ctx.getIntent().getExtras() != null &&
                ctx.getIntent().getExtras().getParcelable(tag) != null)
        {
            return ctx.getIntent().getExtras().getParcelable(tag);
        }
        return null;
    }

    public static void shareMessageToLine(JActivity act, String text)
    {
        if (JUtil.isAPPInstall("jp.naver.line.android"))
        {
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
        if (JUtil.isAPPInstall("jp.naver.line.android"))
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
        if (JUtil.isAPPInstall("jp.naver.line.android"))
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

    public static void openBrowser(Fragment fragment, String url)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            fragment.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(BaseApplication.getContext(), "尚未安裝瀏覽器App", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
