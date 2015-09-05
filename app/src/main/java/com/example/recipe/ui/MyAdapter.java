package com.example.recipe.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.data.DataStore;
import com.example.recipe.R;

import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    public interface AdapterListener{
        String onAdapterClickListener(String s);
    }

    private List<DataStore.DataItem> dataItems;

    private Context context;

    final private AdapterListener adapterListener;

    public MyAdapter(Context context,List<DataStore.DataItem> dataItems,AdapterListener listener){
        this.context = context;
        this.dataItems =  dataItems;
        this.adapterListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_card_item,viewGroup, false);
        MyViewHolder mh = new MyViewHolder(v, new ClickResolver(adapterListener));
        return mh;

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {

        DataStore.DataItem dataItem = dataItems.get(i);
        myViewHolder.firstLine.setText(dataItem.getItem());
        myViewHolder.secondLine.setText(dataItem.getItemDescription());
        myViewHolder.icon.setImageResource(dataItem.getImageResource());
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public static class ClickResolver implements MyViewHolder.ViewHolderListener {
        AdapterListener mListener;

        public ClickResolver( AdapterListener listener){
            mListener = listener;
        }

        @Override
        public String onViewHolderClicked(String s) {
            if(mListener != null) {
                mListener.onAdapterClickListener(s);
                Log.d("TAG", "in adapter click" + s);
            }
            return s;
        }
    }



}
