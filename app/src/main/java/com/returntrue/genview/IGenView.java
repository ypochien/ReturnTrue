package com.returntrue.genview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author JosephWang
 */
public interface IGenView
{
	public View setup(Context context, int position, View convertView, ViewGroup parent, Object data);
}

