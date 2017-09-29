package com.returntrue.framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.util.ArrayMap;

import com.returntrue.util.JLog;

import java.util.List;

/**
 * @author JosephWang
 */
public class AppManager
{
    private static ArrayMap<String, JActivity> activityMap;

    public static void addActivity(JActivity activity)
    {
        if (activityMap == null)
        {
            activityMap = new ArrayMap<>();
        }
        activityMap.put(activity.getClass().getSimpleName(), activity);
    }

    public static void finishActivity(Class<? extends JActivity> name, boolean hasAnimation)
    {
        if (activityMap != null && activityMap.get(name.getSimpleName()) != null)
        {
            JActivity act = activityMap.get(name.getSimpleName());
            act.finishWithAnimation(hasAnimation);
            activityMap.remove(name.getSimpleName());
        }
    }

    public static boolean hasActivity(JActivity activity)
    {
        return activityMap.get(activity.getClass().getSimpleName()) != null;
    }

    public static JActivity getActivity(Class<? extends JActivity> name)
    {
        return activityMap.get(name.getSimpleName());
    }


    public static void removeRecord(JActivity activity)
    {
        if (activity != null)
        {
            activityMap.remove(activity.getClass().getSimpleName());
        }
    }

    public static void finishActivity(Class<? extends JActivity> name)
    {
        finishActivity(name, false);
    }

    public static void finishAllActivity()
    {
        for (JActivity act : activityMap.values())
        {
            act.finishWithAnimation(false);
        }
        activityMap.clear();
    }

    @SuppressWarnings("deprecation")
    public static void appExit()
    {
        JLog.d(JLog.TAG, "appExit...");
        activityMap.clear();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    public static boolean isLastOneActivity(Activity act)
    {
        if (act == null)
        {
            return false;
        }
        ActivityManager manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();
                if (tasks != null && !tasks.isEmpty() && tasks.size() > 0)
                {
                    if (tasks.get(0).processName.contains(act.getClass().getSimpleName()))
                    {
                        return true;
                    }
                }
            }
            else
            {
                List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
                if (tasks != null && !tasks.isEmpty())
                {
                    if (tasks.get(0).numActivities == 1)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static void clearAllInstance()
    {
        if (activityMap != null)
        {
            activityMap.clear();
        }
    }
}