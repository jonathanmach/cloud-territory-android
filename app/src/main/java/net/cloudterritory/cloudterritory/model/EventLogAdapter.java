package net.cloudterritory.cloudterritory.model;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.cloudterritory.cloudterritory.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Jonathan on 27/5/2015.
 */
public class EventLogAdapter extends BaseAdapter {

    private String[] strings;
    public boolean getAll = false;
    private Context context;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); //To Converting the DateTime

    public EventLogAdapter(String[] string_array, Context context2) {

        strings = string_array;
        context = context2;

    }

    @Override
    public int getCount() {

        return strings.length;
    }

    @Override
    public String getItem(int position) {

        return strings[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.eventlog_row, parent, false);

        if(convertView==null)
        {
            LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater2.inflate(R.layout.eventlog_row, parent,false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView);

        textView.setText(Html.fromHtml(strings[position]));

        return convertView;

    }
}
