package net.cloudterritory.cloudterritory.model;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.cloudterritory.cloudterritory.R;

import java.util.ArrayList;
import java.util.List;

public class AddressRecyclerViewAdapter
        extends RecyclerView.Adapter<AddressRecyclerViewAdapter.ContactViewHolder>
         {

    private List<Contact> contacts;
     private ClickListener clickListener;
     private LongClickListener longClickListener;


    public AddressRecyclerViewAdapter(ArrayList<Contact> contactList) {
        this.contacts = contactList;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder territoryviewholder, int i) {
        Contact contact = contacts.get(i);

        if (!TextUtils.isEmpty(contact.additional_info))
        {
            territoryviewholder.vAddress.setVisibility(View.VISIBLE);
            territoryviewholder.vAddress.setText(contact.additional_info);
        }
        territoryviewholder.vName.setText(contact.address);

    }

     public void setClickListener (ClickListener clickListener){
         this.clickListener = clickListener;
     }
     public void setLongClickListener (LongClickListener longClickListener){
         this.longClickListener = longClickListener;
     }

    public Contact getItem(int position) {

        return contacts.get(position);
    }
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_row_address, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        protected TextView vName;
        protected TextView vAddress;


        public ContactViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

            vName = (TextView) v.findViewById(R.id.vTerritoyName);
            vAddress = (TextView) v.findViewById(R.id.vCityZone);

        }

        @Override
        public void onClick(View v) {
            if (clickListener!=null)
            {
                clickListener.itemClicked(v,getPosition());}

        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener!=null)
            {
                longClickListener.itemLongClicked(v,getPosition());}
            return true;
        }
    }
     public interface ClickListener{
         public void itemClicked (View view, int position);
         public void itemLongClicked (View view, int position);

     }
     public interface LongClickListener{
         public void itemLongClicked (View view, int position);

     }
}