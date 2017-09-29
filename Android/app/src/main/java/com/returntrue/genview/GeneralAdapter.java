package com.returntrue.genview;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.returntrue.util.JUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author JosephWang
 */
public class GeneralAdapter extends BaseAdapter
{
    private boolean isCyclic = false;

    public boolean isCyclic()
    {
        return isCyclic;
    }

    public void setCyclic(boolean isCyclic)
    {
        this.isCyclic = isCyclic;
    }

    public enum DataType
    {
        ARRAYLIST,
        LINKEDHASHMAP,
        CURSOR,
        OBJECT_ARRAY,
        SPARSE_ARRAY,
        SPARSE_BOOLEAN_ARRAY,
        SPARSE_INT_ARRAY,
        VECTOR,
        LINKEDLIST,
        HASMAP,
        LIST,
        SET,
        NONE;
    }

    private DataType dataType = DataType.NONE;

    public DataType getDataType()
    {
        return dataType;
    }

    private Object data;
    private IGenView generator;

    public IGenView getGenerator()
    {
        return generator;
    }

    public void setGenerator(IGenView generator)
    {
        this.generator = generator;
    }

    private Context context;

    public GeneralAdapter(Context context, IGenView generator)
    {
        this.generator = generator;
        this.context = context;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(ArrayList<?> object)
    {
        dataType = DataType.ARRAYLIST;
        data = object;
    }

    public void setData(List<?> object)
    {
        dataType = DataType.LIST;
        data = object;
    }

    public void setData(Object[] object)
    {
        dataType = DataType.OBJECT_ARRAY;
        data = object;
    }

    public void setData(Object object) throws UnsupportedListException
    {
        // Multiple data list without header
        if (object instanceof ArrayList<?>)
        {
            dataType = DataType.ARRAYLIST;
        }
        else if (object instanceof LinkedHashMap<?, ?>)
        {
            dataType = DataType.LINKEDHASHMAP;
        }
        else if (object instanceof Cursor)
        {
            dataType = DataType.CURSOR;
        }
        else if (object instanceof Object[])
        {
            dataType = DataType.OBJECT_ARRAY;
        }
        else if (object instanceof SparseArray<?>)
        {
            dataType = DataType.SPARSE_ARRAY;
        }
        else if (object instanceof SparseBooleanArray)
        {
            dataType = DataType.SPARSE_BOOLEAN_ARRAY;
        }
        else if (object instanceof SparseIntArray)
        {
            dataType = DataType.SPARSE_INT_ARRAY;
        }
        else if (object instanceof Vector<?>)
        {
            dataType = DataType.VECTOR;
        }
        else if (object instanceof LinkedList<?>)
        {
            dataType = DataType.LINKEDLIST;
        }
        else if (object instanceof ConcurrentSkipListMap<?, ?>)
        {
            dataType = DataType.HASMAP;
        }
        else if (object instanceof ConcurrentSkipListSet<?>)
        {
            dataType = DataType.SET;
        }
        else if (object instanceof List<?>)
        {
            dataType = DataType.LIST;
        }
        else
        {
            throw new UnsupportedListException();
        }

        data = object;
    }

    public int getCountWithoutHeader()
    {
        int count = 0;
        if (data == null)
        {
            return 0;
        }
        if (isCyclic())
        {
            count = Integer.MAX_VALUE;
        }
        else
        {
            switch (dataType)
            {
                case ARRAYLIST:
                    count = ((ArrayList<?>) data).size();
                    break;
                case LINKEDHASHMAP:
                    Iterator<?> iterator = ((LinkedHashMap<?, ?>) data).entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
                        if (entry.getValue() instanceof List)
                        {
                            count = count + ((List<?>) entry.getValue()).size();
                        }
                    }
                    break;
                case CURSOR:
                    count = ((Cursor) data).getCount();
                    break;
                case OBJECT_ARRAY:
                    count = ((Object[]) data).length;
                    break;
                case SPARSE_ARRAY:
                    count = ((SparseArray<?>) data).size();
                    break;
                case SPARSE_BOOLEAN_ARRAY:
                    count = ((SparseBooleanArray) data).size();
                    break;
                case SPARSE_INT_ARRAY:
                    count = ((SparseIntArray) data).size();
                    break;
                case VECTOR:
                    count = ((Vector<?>) data).size();
                    break;
                case LINKEDLIST:
                    count = ((LinkedList<?>) data).size();
                    break;
                case HASMAP:
                    count = ((ConcurrentSkipListMap<?, ?>) data).size();
                    break;
                case SET:
                    count = ((ConcurrentSkipListSet<?>) data).size();
                    break;
                case LIST:
                    count = ((List<?>) data).size();
                    break;
                default:
                    break;
            }
        }
        return count;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void removeItem(int index) throws Exception
    {
        if (data != null)
        {
            if (isCyclic() && index >= realDataCount())
            {
                index = getIndexByReCycleMode(index);
            }
            switch (dataType)
            {
                case ARRAYLIST:
                    ((ArrayList<?>) data).remove(index);
                    break;
                case LINKEDHASHMAP:
                    ((LinkedHashMap<?, ?>) data).remove(index);
                    break;
                case SPARSE_ARRAY:
                    ((SparseArray<?>) data).remove(index);
                    break;
                case SPARSE_BOOLEAN_ARRAY:
                    ((SparseBooleanArray) data).delete(index);
                    break;
                case SPARSE_INT_ARRAY:
                    ((SparseIntArray) data).delete(index);
                    break;
                case VECTOR:
                    ((Vector<?>) data).remove(index);
                    break;
                case LINKEDLIST:
                    ((LinkedList<?>) data).remove(index);
                    break;
                case LIST:
                    ((List<?>) data).remove(index);
                    break;
                case HASMAP:
                    /*********轉Object Array 無法刪除***************/
                    CopyOnWriteArrayList list = new CopyOnWriteArrayList(((ConcurrentSkipListMap) data).values());
                    list.remove(index);
//                    ((HashMap<?, ?>) data).remove(index);
                    break;
                case SET:
                    CopyOnWriteArrayList listTwo = new CopyOnWriteArrayList(((ConcurrentSkipListSet<?>) data));
                    listTwo.remove(index);
                    break;
                default:
                    throw new AssertionError();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        int count = 0;
        if (data == null)
        {
            return 0;
        }
        if (isCyclic())
        {
            count = Integer.MAX_VALUE;
        }
        else
        {
            switch (dataType)
            {
                case ARRAYLIST:
                    count = ((ArrayList<?>) data).size();
                    break;
                case LINKEDHASHMAP:
                    Iterator<?> iterator = ((LinkedHashMap<?, ?>) data).entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
                        if (entry.getValue() instanceof List)
                        {
                            count = count + ((List<?>) entry.getValue()).size();
                        }
                    }
                    break;
                case CURSOR:
                    count = ((Cursor) data).getCount();
                    break;
                case OBJECT_ARRAY:
                    count = ((Object[]) data).length;
                    break;
                case SPARSE_ARRAY:
                    count = ((SparseArray<?>) data).size();
                    break;
                case SPARSE_BOOLEAN_ARRAY:
                    count = ((SparseBooleanArray) data).size();
                    break;
                case SPARSE_INT_ARRAY:
                    count = ((SparseIntArray) data).size();
                    break;
                case VECTOR:
                    count = ((Vector<?>) data).size();
                    break;
                case LINKEDLIST:
                    count = ((LinkedList<?>) data).size();
                    break;
                case HASMAP:
                    count = ((ConcurrentSkipListMap<?, ?>) data).size();
                    break;
                case SET:
                    count = ((ConcurrentSkipListSet<?>) data).size();
                    break;
                case LIST:
                    count = ((List<?>) data).size();
                    break;
                default:
                    throw new AssertionError();
            }
        }
        return count;
    }

    public int realDataCount()
    {
        int count = 0;
        if (data == null)
        {
            return 0;
        }
        switch (dataType)
        {
            case ARRAYLIST:
                count = ((ArrayList<?>) data).size();
                break;
            case LINKEDHASHMAP:
                Iterator<?> iterator = ((LinkedHashMap<?, ?>) data).entrySet().iterator();
                while (iterator.hasNext())
                {
                    Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
                    if (entry.getValue() instanceof List)
                    {
                        count = count + ((List<?>) entry.getValue()).size();
                    }
                }
                break;
            case CURSOR:
                count = ((Cursor) data).getCount();
                break;
            case OBJECT_ARRAY:
                count = ((Object[]) data).length;
                break;
            case SPARSE_ARRAY:
                count = ((SparseArray<?>) data).size();
                break;
            case SPARSE_BOOLEAN_ARRAY:
                count = ((SparseBooleanArray) data).size();
                break;
            case SPARSE_INT_ARRAY:
                count = ((SparseIntArray) data).size();
                break;
            case VECTOR:
                count = ((Vector<?>) data).size();
                break;
            case LINKEDLIST:
                count = ((LinkedList<?>) data).size();
                break;
            case HASMAP:
                count = ((ConcurrentSkipListMap<?, ?>) data).size();
                break;
            case SET:
                count = ((ConcurrentSkipListSet<?>) data).size();
                break;
            case LIST:
                count = ((List<?>) data).size();
                break;
            default:
                throw new AssertionError();
        }
        return count;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object getItem(int position)
    {
        if (data == null)
        {
            return null;
        }
        if (isCyclic() && position >= realDataCount())
        {
            position = getIndexByReCycleMode(position);
        }
        switch (dataType)
        {
            case ARRAYLIST:
            {
                ArrayList<?> list = ((ArrayList<?>) data);
                if (JUtil.notEmpty(list))
                {
                    return list.get(position);
                }
                else
                {
                    return null;
                }
            }
            case LINKEDHASHMAP:
                // Return value may be key or element
                LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) data;
                Iterator<?> iterator = map.entrySet().iterator();
                int index = position;
                while (iterator.hasNext())
                {
                    Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
                    if (entry.getValue() instanceof List)
                    {
                        if (index == 0)
                        {
                            return entry.getKey();
                        }
                        else if (index <= ((List<?>) entry.getValue()).size())
                        {
                            return ((List<?>) entry.getValue()).get(index - 1);
                        }
                        else
                        {
                            index = index - ((List<?>) entry.getValue()).size() - 1;
                        }
                    }
                }
                return null;
            case CURSOR:
                Cursor cursor = ((Cursor) data);
                cursor.moveToPosition(position);
                return cursor;
            case OBJECT_ARRAY:
                return ((Object[]) data)[position];
            case SPARSE_ARRAY:
                return ((SparseArray<?>) data).get(position);
            case SPARSE_BOOLEAN_ARRAY:
                return ((SparseBooleanArray) data).get(position);
            case SPARSE_INT_ARRAY:
                return ((SparseIntArray) data).get(position);
            case VECTOR:
                return ((Vector<?>) data).get(position);
            case LINKEDLIST:
                return ((LinkedList<?>) data).get(position);
            case HASMAP:
            {
                CopyOnWriteArrayList list = new CopyOnWriteArrayList(((ConcurrentSkipListMap) data).values());
                return list.get(position);
            }
            case SET:
                CopyOnWriteArrayList listTwo = new CopyOnWriteArrayList(((ConcurrentSkipListSet<?>) data));
                return listTwo.get(position);
            case LIST:
                return ((List<?>) data).get(position);
            default:
                throw new AssertionError();
        }
    }

    public int getIndexByReCycleMode(int index)
    {
        int currentIndex = index % realDataCount();
        if (currentIndex > realDataCount())
        {
            return getIndexByReCycleMode(currentIndex);
        }
        else
        {
            return currentIndex;
        }
    }

    @Override
    public long getItemId(int position)
    {
        if (data == null)
        {
            return 0;
        }
        if (isCyclic() && position >= realDataCount())
        {
            int recycleIndex = getIndexByReCycleMode(position);
            if (getItem(recycleIndex) != null)
            {
                return getItem(recycleIndex).hashCode();
            }
            else
            {
                return 0;
            }
        }
        else
        {
            if (getItem(position) != null)
            {
                return getItem(position).hashCode();
            }
            else
            {
                return 0;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (isCyclic() && position >= realDataCount())
        {
            int recycleIndex = getIndexByReCycleMode(position);
            if (notOutBound(recycleIndex))
            {
                return generator.setup(context, recycleIndex, convertView, parent, data);
            }
            else
            {
                return convertView;
            }
        }
        else
        {
            if (notOutBound(position))
            {
                return generator.setup(context, position, convertView, parent, data);
            }
            else
            {
                return convertView;
            }
        }
    }

    private boolean notOutBound(int index)
    {
        if (data instanceof Object[])
        {
            Object[] collection = ((Object[]) data);
            return collection.length > 0 && index < collection.length;
        }
        else if (data instanceof ArrayList<?>)
        {
            ArrayList<?> collection = (ArrayList<?>) data;
            return collection.size() > 0 && index < collection.size();
        }
        else if (data instanceof List<?>)
        {
            List<?> collection = (List<?>) data;
            return collection.size() > 0 && index < collection.size();
        }
        else if (data instanceof ConcurrentSkipListMap<?, ?>)
        {
            ConcurrentSkipListMap<?, ?> collection = (ConcurrentSkipListMap<?, ?>) data;
            return collection.size() > 0 && index < collection.size();
        }
        else if (data instanceof ConcurrentSkipListSet<?>)
        {
            ConcurrentSkipListSet<?> collection = (ConcurrentSkipListSet<?>) data;
            return collection.size() > 0 && index < collection.size();
        }
        else if (data instanceof LinkedHashMap<?, ?>)
        {
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) data;
            return map.size() > 0 && index < map.size();
        }
        else if (data instanceof Cursor)
        {
            Cursor collection = ((Cursor) data);
            return collection.getCount() > 0 && index < collection.getCount();
        }
        else
        {
            return false;
        }
    }
}