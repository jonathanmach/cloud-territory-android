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

public class TerritoryRecyViewAdapter
        extends RecyclerView.Adapter<TerritoryRecyViewAdapter.TerritoryViewHolder> {

    private List<Territory> territories;
    private ClickListener clickListener;
    private LongClickListener longClickListener;

    public TerritoryRecyViewAdapter(ArrayList<Territory> territoryArrayList) {
        this.territories = territoryArrayList;
    }

    @Override
    public int getItemCount() {
        return territories.size();
    }

    @Override
    public void onBindViewHolder(TerritoryViewHolder territoryviewholder, int i) {
        Territory ci = territories.get(i);
        territoryviewholder.currentItem = territories.get(i);
        territoryviewholder.vName.setText(ci.name);
        territoryviewholder.vCityZone.setText(ci.city.name);
        territoryviewholder.vIDterritory.setText(Integer.toString(ci._id));
        territoryviewholder.vPublisherName.setText(ci.assigned_user.userFullName);

//TODO: bug ao rolar a RecyclerView: esta zerando todos o Text vPublisherName
        if (!TextUtils.isEmpty(ci.assigned_user.userFullName))
        { territoryviewholder.vPublisherName.setVisibility(View.VISIBLE);
          territoryviewholder.vPublisherName.setText(ci.assigned_user.userFullName); }
        else
        { territoryviewholder.vPublisherName.setVisibility(View.GONE); }
    }

    public void setClickListener (ClickListener clickListener){
        this.clickListener = clickListener;
    }
    public void setLongClickListener (LongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }
    public Territory getItem(int position) {

        return territories.get(position);
    }
    @Override
    public TerritoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_row_territory, viewGroup, false);


        return new TerritoryViewHolder(itemView);
    }


    class TerritoryViewHolder extends RecyclerView.ViewHolder
                                            implements View.OnClickListener, View.OnLongClickListener{
        protected TextView vName;
        protected TextView vIDterritory;
        protected TextView vCityZone;
        protected TextView vPublisherName;
        public Territory currentItem;


        public TerritoryViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);

            vName = (TextView) v.findViewById(R.id.vTerritoyName);
            vIDterritory = (TextView) v.findViewById(R.id.vIDterritory);
            vCityZone = (TextView) v.findViewById(R.id.vCityZone);
            vPublisherName = (TextView) v.findViewById(R.id.txtvPublisherName);

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

    public void delete(int position)
    {
        territories.remove(position);
        notifyItemRemoved(position);

    }
    public interface ClickListener{
        public void itemClicked (View view, int position);
        public void itemLongClicked (View view, int position);

    }
    public interface LongClickListener{
        public void itemLongClicked (View view, int position);

    }

}