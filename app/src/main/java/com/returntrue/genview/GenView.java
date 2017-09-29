package com.returntrue.genview;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author JosephWang
 */
public class GenView implements IGenView
{
	public int dataSize = 0;

	public boolean isHasToWork()
	{
		return hasToWork;
	}

	public void setHasToWork(boolean hasToWork)
	{
		this.hasToWork = hasToWork;
	}

	private boolean hasToWork = false;
	/**
	 * just for reference
	 */
	protected View genHeader(Context context, View convertView, Object element, int position)
	{
		return convertView;
	}
	/**
	 * just for reference
	 */
	protected View genBody(Context context, View convertView, Object element, int position)
	{
		return convertView;
	}

	protected View genFooter()
	{
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public View setup(Context context, int position, View convertView, ViewGroup parent, Object data)
	{
		if (data instanceof Object[])
		{
			Object[] collection = ((Object[]) data);
			dataSize = collection.length;
			return genBody(context, convertView, collection[position], position);
		}
		else if (data instanceof ArrayList<?>)
		{
			ArrayList<?> collection = (ArrayList<?>) data;
			dataSize = collection.size();
			return genBody(context, convertView, collection.get(position), position);
		}
		else if (data instanceof List<?>)
		{
			List<?> collection = (List<?>) data;
			dataSize = collection.size();
			return genBody(context, convertView, collection.get(position), position);
		}
		else if (data instanceof ConcurrentSkipListMap<?, ?>)
		{
			ConcurrentSkipListMap<?, ?> collection = (ConcurrentSkipListMap<?, ?>) data;
			dataSize = collection.size();

			CopyOnWriteArrayList<?> list = new CopyOnWriteArrayList(collection.values());
			return genBody(context, convertView, list.get(position), position);
		}
		else if (data instanceof ConcurrentSkipListSet<?>)
		{
			ConcurrentSkipListSet<?> collection = (ConcurrentSkipListSet<?>) data;
			dataSize = collection.size();
			CopyOnWriteArrayList<?> list = new CopyOnWriteArrayList(collection);
			return genBody(context, convertView, list.get(position), position);
		}
		else if (data instanceof LinkedHashMap<?, ?>)
		{
			LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) data;
			Iterator<?> iterator = map.entrySet().iterator();
			int index = position;
			dataSize = map.size();
			while (iterator.hasNext())
			{
				Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
				if (entry.getValue() instanceof List<?>)
				{
					if (index == 0)
					{
						return genHeader(context, convertView, entry.getKey(), position);
					}
					else if (index <= ((List<?>) entry.getValue()).size())
					{
						return genBody(context, convertView, ((List<?>) entry.getValue()).get(index - 1), position);
					}
					else
					{
						index = index - ((List<?>) entry.getValue()).size() - 1;
					}
				}
			}
			return null;
		}
		else if (data instanceof Cursor)
		{
			Cursor collection = ((Cursor) data);
			dataSize = collection.getCount();
			collection.moveToPosition(position);
			return genBody(context, convertView, data, position);
		}
		else
		{
			return convertView;
		}
	}
}
