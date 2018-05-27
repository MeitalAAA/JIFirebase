package com.arye.meital.jifirebase.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arye.meital.jifirebase.R;

import java.util.ArrayList;

/**
 * Created by owner on 25/05/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<String> mDataset;
    private final AdapterInteraction mListener;

    public MyAdapter(AdapterInteraction adapterInteraction, ArrayList<String> myDataset) {
        mDataset = myDataset;
        mListener = adapterInteraction;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(mDataset.get(position));
        holder.remove.setTag(position);
        holder.edit.setTag(position);
        holder.remove.setOnClickListener(this);
        holder.edit.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onClick(View v) {
        //String name = ((TextView)v.findViewById(R.id.name)).getText().toString();
        int position = (int) v.getTag();
        String name = mDataset.get(position);
        switch (v.getId()) {
            case (R.id.edit):
                mListener.onEditItem(name, position);
                break;
            case (R.id.remove):
                mListener.onDeleteItem(name, position);
                //mDataset.remove(position);
                //notifyItemRemoved(position);
                //notifyItemRangeChanged(position, mDataset.size());
                break;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView itemName;
        public final Button edit;
        public final Button remove;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.itemName = view.findViewById(R.id.name);
            this.edit = view.findViewById(R.id.edit);
            this.remove = view.findViewById(R.id.remove);
        }
    }

    public interface AdapterInteraction {
        void onDeleteItem(String name, int pos);
        void onEditItem(String name, int pos);
    }
}
