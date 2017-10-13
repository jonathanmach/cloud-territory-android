package net.cloudterritory.cloudterritory.model;

import android.content.Context;
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
public class MessagesAdapter extends BaseAdapter {

    private ArrayList<MessageHistory> tst;
    public boolean getAll = false;
    private Context context;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); //To Converting the DateTime

    public MessagesAdapter(ArrayList<MessageHistory> arraylist, Context context2) {

        tst = arraylist;
        context = context2;

    }

    @Override
    public int getCount() {

        if (getAll)
        {
            return tst.size();

        }
        else {
            if (tst.size() > 3) {
                return 3;
            } else {
                return tst.size();
            }
        }

    }

    @Override
    public MessageHistory getItem(int position) {

        return tst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // ???? Entender os parametros que vao nesse inflate:
        View rowView = inflater.inflate(R.layout.history_row, parent, false);

        // ???? Estudar a necessidade desse if abaixo:

        if(convertView==null)
        {
            LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater2.inflate(R.layout.history_row, parent,false);
        }

        TextView textViewmessage = (TextView) convertView.findViewById(R.id.textViewMessage);
        TextView vDateTimeUser = (TextView) convertView.findViewById(R.id.textViewUserInfo);
        MessageHistory conc = tst.get(position);

        textViewmessage.setText(conc.message);
        String userInfoTime = conc.getFirstNameUserFK() +" on " +simpleDateFormat.format(conc.dateTime).toString() ;
        //TODO!!!: No debug, mesmo quando tinha apenas 2 msgs, o debug passou por aqui umas 6x

        vDateTimeUser.setText(userInfoTime);
//        txtContactName.setText(conc.name.toString());

        return convertView;

    }
}
