package com.returntrue.genview;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.returntrue.R;


/**
 * Created by josephwang on 2017/4/25.
 */

public class GenText extends GenView
{
    private int line = 1;
    private int textSizeSp = 14;

    public GenText()
    {
        this(1, 14);
    }

    public GenText(int line, int textSizeSp)
    {
        this.line = line;
        this.textSizeSp = textSizeSp;
    }

    public GenText(int textSize)
    {
        this.textSizeSp = textSize;
    }

    @Override
    protected View genBody(Context context, View convertView, Object element, int position)
    {
        ViewHolder holder;
        if (convertView != null)
        {
            holder = (ViewHolder) convertView.getTag();
        }
        else
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.general_text, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }
        holder.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
        holder.text.setMaxLines(line);
        holder.text.setText(element.toString());
        return convertView;
    }

    private class ViewHolder
    {
        public TextView text;
    }
}