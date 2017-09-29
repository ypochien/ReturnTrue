package com.returntrue.framework;

import android.os.Bundle;

/**
 * Created by josephwang on 2017/3/21.
 */

public abstract class TabHostFragment extends JFragment implements ITabHostTransaction
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof ITabHostTransaction))
        {
            throw new IllegalArgumentException("The parent Activity must implements ITabHostTransaction !!!");
        }
    }

    @Override
    public void commitFragmentTransaction(JFragment fragment, FragmentAnimationType type)
    {
        ((ITabHostTransaction) getJActivity()).commitFragmentTransaction(fragment, type);
    }
}
