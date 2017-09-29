package com.returntrue.framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.returntrue.R;
import com.returntrue.application.BaseApplication;
import com.returntrue.util.JLog;
import com.returntrue.util.JUtil;
import com.returntrue.util.ui.JDialog;
import com.returntrue.util.ui.UIAdjuster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class JFragment extends Fragment implements View.OnKeyListener
{
    public String TAG;
    protected Bundle savedInstanceState;
    protected ProgressDialog loading = null;
    protected final Handler handler = new Handler();
    public Handler getHandler()
    {
        return handler;
    }

    public JFragment()
    {
        super();
        TAG = ((Object) this).getClass().getSimpleName();
    }

    public <T extends JActivity> T getJActivity()
    {
        return (T) getActivity();
    }

    public void putIntegerToPreference(String key, int value)
    {
        BaseApplication.getInstance().putIntegerToPreference(key, value);
    }

    public void putIntegerToPreference(String key)
    {
        putIntegerToPreference(key, 0);
    }

    public void putLongToPreference(String key, long value)
    {
        BaseApplication.getInstance().putLongToPreference(key, value);
    }

    public void putStringToPreference(String key, String value)
    {
        BaseApplication.getInstance().putStringToPreference(key, value);
    }

    public void putBooleanToPreference(String key, boolean value)
    {
        BaseApplication.getInstance().putBooleanToPreference(key, value);
    }

    public void putStringArrayToPreference(String key, ArrayList<String> list)
    {
        BaseApplication.getInstance().putStringArrayToPreference(key, list);
    }

    public ArrayList<String> getStringArrayFromPreference(String key)
    {
        return BaseApplication.getInstance().getStringArrayFromPreference(key);
    }

    private ArrayList<String> convertToArray(String string)
    {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(",")));
        return list;
    }

    public String getStringFromPreference(String key, String defaultValues)
    {
        return BaseApplication.getInstance().getStringFromPreference(key, defaultValues);
    }

    public int getIntegerFromPreference(String key, int defaultValues)
    {
        return BaseApplication.getInstance().getIntegerFromPreference(key, defaultValues);
    }

    public int getIntegerFromPreference(String key)
    {
        return getIntegerFromPreference(key, 0);
    }

    public Long getLongFromPreference(String key)
    {
        return getLongFromPreference(key, 0);
    }

    public Long getLongFromPreference(String key, long defaultValues)
    {
        return BaseApplication.getInstance().getLongFromPreference(key, defaultValues);
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的boolean值，若不存在則回傳defaultValues
     */
    public boolean getBooleanFromPreference(String key, boolean defaultValues)
    {
        return BaseApplication.getInstance().getBooleanFromPreference(key, defaultValues);
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的值，若不存在則回傳空字串(非null)
     */
    public String getStringFromPreference(String key)
    {
        return  BaseApplication.getInstance().getStringFromPreference(key, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        JLog.d(JLog.JosephWang, TAG + " onCreate");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            this.savedInstanceState = savedInstanceState;
        }

        // singleErrorDialog = new SingleDialog(getActivity());
        UIAdjuster.closeKeyBoard(getActivity());
        // // 註冊監聽
    }

    @Override
    public void onResume()
    {
        JLog.d(JLog.JosephWang, TAG + " onResume");
        super.onResume();

        // if (bundledReceiver != null)
        // bundledReceiver.registerAllEvent(getActivity());
        UIAdjuster.closeKeyBoard(getActivity());
    }

    @Override
    public void onDestroy()
    {
        JLog.d(JLog.JosephWang, TAG + " onDestroy");
        cancelLoading();
        UIAdjuster.closeKeyBoard(getActivity());
        handler.removeCallbacks(null);
        super.onDestroy();
    }

    /**
     * showLoading ,check network state and set Timer of timeout
     *
     * @return true
     */
    public boolean showLoading()
    {
        return showLoading("資料交換中\n請稍候");
    }

    public boolean showLoading(String text)
    {
        if (loading == null && getActivity() != null)
        {
            loading = JDialog.showProgressDialog(getActivity(), "" + text, false);
        }
        return true;
    }

    protected void setProgressInLoading(String progress)
    {
        if (loading != null && getActivity() != null)
        {
            ((TextView) loading.findViewById(R.id.progressTitle)).setText("" + progress);
        }
    }

    public boolean isReclaim()
    {
        return getActivity() == null ||
                getActivity().isFinishing();
    }

    /**
     * dismiss ProgressDialog
     */
    public void cancelLoading()
    {
        if (!isReclaim() &&
                loading != null && loading.isShowing())
        {
            loading.dismiss();
            loading = null;
        }
    }

    /**
     *
     */
    @Override
    public void onPause()
    {
        JLog.d(JLog.JosephWang, TAG + " onPause");
        super.onPause();
        cancelLoading();
        // unregisterAllBundledAction();
    }

    public boolean isLastOneActivity()
    {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty())
        {
            if (tasks.get(0).numActivities == 1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    public void onFinish()
    {
        JLog.d("josephWang", "onFinish " + getClass().getSimpleName());
    }

    @Override
    public void onAttach(Activity activity)
    {
        JLog.d(JLog.JosephWang, TAG + " onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        JLog.d(JLog.JosephWang, TAG + " onDetach");
        super.onDetach();
        cancelLoading();
    }

    public void finish()
    {
        onFinish();
        cancelLoading();
        if (notEmpyFragments())
        {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        JLog.d(JLog.JosephWang, TAG + " onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        JLog.d(JLog.JosephWang, TAG + " onStart");
        super.onStart();
    }

    @Override
    public void onStop()
    {
        JLog.d(JLog.JosephWang, TAG + " onStop");
        super.onStop();
    }

    public <T extends View> T getView(@IdRes int id)
    {
        return (T) getJActivity().getView(id);
    }

    @Override
    public void onDestroyView()
    {
        handler.removeCallbacks(null);
        JLog.d(JLog.JosephWang, TAG + " onDestroyView");
        cancelLoading();
        super.onDestroyView();
    }

    public Bitmap takeScreenShot()
    {
        return takeScreenShot(getActivity().findViewById(android.R.id.content).getRootView());
    }

    public Bitmap takeScreenShot(View rootView)
    {
        rootView.destroyDrawingCache();
        rootView.buildDrawingCache();
        rootView.invalidate();
        rootView.setDrawingCacheEnabled(true);
        final Bitmap map = rootView.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
        rootView.destroyDrawingCache();
        return map;
    }

    public void commitFragment(int replaceId, Fragment fragment)
    {
        commitFragment(replaceId, fragment, FragmentAnimationType.None);
    }

    public void commitFragment(int replaceId, Fragment fragment, FragmentAnimationType type)
    {
        commitFragment(replaceId, fragment, fragment.getClass().getSimpleName(), type);
    }

    public void commitFragment(int replaceId, Fragment fragment, String tag, FragmentAnimationType type)
    {
        if (getJActivity() != null && !getJActivity().isFinishing() && getJActivity().isAlive() && getChildFragmentManager() != null)
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            /***Test***/
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            /***Test***/
            switch (type)
            {
                case LeftIn:
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case RightIn:
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    break;
                case TopDown:
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                    break;
                case BottomRise:
                    transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
                    break;
                case None:
                    break;
            }
            if (hasSameFragment(fragment))
            {
                Fragment historyFragment = getHistoryFragment(fragment.getClass());
                transaction.replace(replaceId, historyFragment, tag);
            }
            else
            {
                transaction.replace(replaceId, fragment, tag);
            }

            transaction.addToBackStack(null);
            // transaction.commit();
            /*******
             * 修正Bug java.lang.IllegalStateException: Can not perform this action
             * after onSaveInstanceState
             ********/
            transaction.commitAllowingStateLoss();
        }
    }

    public void commitNewFragment(int replaceId, Fragment fragment)
    {
        if (!getJActivity().isFinishing() && getChildFragmentManager() != null)
        {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(replaceId, fragment, fragment.getClass().getSimpleName());

            transaction.addToBackStack(null);
            // transaction.commit();
            /*******
             * 修正Bug java.lang.IllegalStateException: Can not perform this action
             * after onSaveInstanceState
             ********/
            transaction.commitAllowingStateLoss();
        }
    }

    public <T extends Fragment> T getHistoryFragment(Class<T> fragment)
    {
        return getHistoryFragment(fragment.getSimpleName());
    }

    public <T extends Fragment> T getHistoryFragment(String tag)
    {
        if (getChildFragmentManager() != null)
        {
            T current = (T) getChildFragmentManager().findFragmentByTag(tag);
            if (current != null)
            {
                return current;
            }
        }
        return null;
    }

    public void clearFragment(Class<? extends Fragment> fragment)
    {
        if (getChildFragmentManager() != null)
        {
            Fragment current = getChildFragmentManager().findFragmentByTag(fragment.getSimpleName());
            if (current != null)
            {
                JLog.d(TAG + " clearFragment Fragment " + current.getClass().getSimpleName());
                current.onDestroyView();
            }
        }
    }

    public boolean hasSameFragment(Fragment fragment)
    {
        if (getChildFragmentManager() != null)
        {
            Fragment current = getChildFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasSameFragment(Class<? extends Fragment> fragment)
    {
        if (getChildFragmentManager() != null)
        {
            Fragment current = getChildFragmentManager().findFragmentByTag(fragment.getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public void removeFragment(Class<? extends Fragment> fragments, boolean clear)
    {
        if (notEmpyFragments())
        {
            Fragment fragment = getChildFragmentManager().findFragmentByTag(fragments.getSimpleName());
            JLog.d(JLog.JosephWang, TAG + " removeFragment " + (fragment != null));
            if (fragment != null)
            {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }

            if (clear)
            {
                getChildFragmentManager().getFragments().clear();
            }
        }
    }

    public void removeFragment(Class<? extends Fragment> fragments)
    {
        removeFragment(fragments, true);
    }

    public void clearAllFragment()
    {
        if (notEmpyFragments())
        {
            getChildFragmentManager().getFragments().clear();
        }
    }

    public boolean isCurrentFragmentVisible(Fragment fragment)
    {
        if (notEmpyFragments())
        {
            Fragment current = getChildFragmentManager().getFragments().get(0);
            if (current != null && current.getClass().getSimpleName().equals(fragment.getClass().getSimpleName()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    public boolean isCurrentFragmentVisible(Class<? extends Fragment> fragment)
    {
        if (notEmpyFragments())
        {
            Fragment current = getChildFragmentManager().getFragments().get(0);
            if (current != null && current.getClass().getSimpleName().equals(fragment.getSimpleName()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    public boolean notEmpyFragments()
    {
        return (getChildFragmentManager() != null &&
                JUtil.notEmpty(getChildFragmentManager().getFragments()));
    }

    public boolean isLastOneFragmentVisible(Class<? extends Fragment> fragment)
    {
        if (notEmpyFragments())
        {
            Fragment current = getChildFragmentManager().getFragments().get(getChildFragmentManager().getFragments().size() - 1);
            if (current != null && current.getClass().getSimpleName().equals(fragment.getSimpleName()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    public boolean isCurrentFragmentExist(Fragment fragment)
    {
        if (getChildFragmentManager() != null)
        {
            Fragment current = getChildFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentFragmentExist(Class<? extends Fragment> fragment)
    {
        if (getChildFragmentManager() != null)
        {
            Fragment current = getChildFragmentManager().findFragmentByTag(fragment.getSimpleName());
            if (current != null)
            {
                return true;
            }
        }
        return false;
    }

    public <T extends View> T getChildView(@IdRes int id)
    {
        return (T) getActivity().getWindow().findViewById(id);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK ||
            event.getAction() == KeyEvent.KEYCODE_BACK)
        {
            backAction();
            return true;
        }
        return onKey(v, keyCode, event);
    }

    public void backAction()
    {
        if (getChildFragmentManager().getBackStackEntryCount() > 0)
        {
            getChildFragmentManager().popBackStack();
        }
    }
}