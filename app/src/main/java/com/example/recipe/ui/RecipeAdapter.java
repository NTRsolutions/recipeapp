package com.example.recipe.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.R;
import com.example.recipe.data.RecipeDataStore;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.utility.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    private List<RecipeInfo> dataItems = new ArrayList<>();
    private Context context;
    final private BaseFragment.AdapterListener adapterListener;

    public RecipeAdapter(Context context, BaseFragment.AdapterListener listener){
        this.context = context;
        this.adapterListener = listener;
        RecipeDataStore.fetchAllInfoData(new RecipeDataStore.RecipeDataStoreListener() {
            @Override
            public void onDataFetchComplete(List<RecipeInfo> list) {
                dataItems = list;
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recipe_card_item,viewGroup, false);
        RecipeViewHolder mh = new RecipeViewHolder(v, new ClickResolver(adapterListener));
        return mh;

    }

    @Override
    public void onBindViewHolder(RecipeViewHolder myViewHolder, int i) {

        RecipeInfo dataItem = dataItems.get(i);
        myViewHolder.mTitle.setText(dataItem.getTitle());
        Picasso.with(context).load(dataItem.getImageUrl())
                .resize(Config.SCREEN_SIZE.x, Config.SCREEN_SIZE.x)
                .centerCrop().into(myViewHolder.mIcon);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public static class ClickResolver implements RecipeViewHolder.ViewHolderListener {
        BaseFragment.AdapterListener mListener;

        public ClickResolver( BaseFragment.AdapterListener listener){
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
