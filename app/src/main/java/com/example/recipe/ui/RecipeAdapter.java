package com.example.recipe.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.R;
import com.example.recipe.data.RecipeDataStore;
import com.example.recipe.data.RecipeDescription;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.utility.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    public interface RecipeAdapterListener {
        void onRecipeAdapterListener(RecipeDescription recipeDescription);
    }

    private List<RecipeInfo> mDataItems = new ArrayList<>();
    private Context mContext;
    private RecipeAdapterListener mRecipeAdapterListener;

    public RecipeAdapter(Context context, RecipeAdapterListener recipeAdapterListener){
        this.mContext = context;
        this.mRecipeAdapterListener = recipeAdapterListener;
        RecipeDataStore.fetchAllInfoData(new RecipeDataStore.RecipeDataStoreListener() {
            @Override
            public void onDataFetchComplete(List<RecipeInfo> list) {
                mDataItems = list;
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recipe_card_item,viewGroup, false);
        RecipeViewHolder mh = new RecipeViewHolder(v, new ClickResolver(this));
        return mh;

    }

    @Override
    public void onBindViewHolder(RecipeViewHolder myViewHolder, int i) {

        RecipeInfo dataItem = mDataItems.get(i);
        myViewHolder.mTitle.setText(dataItem.getTitle());
        Picasso.with(mContext).load(dataItem.getImageUrl())
                .resize(Config.SCREEN_SIZE.x, Config.SCREEN_SIZE.x)
                .centerCrop().into(myViewHolder.mIcon);
    }

    @Override
    public int getItemCount() {
        return mDataItems.size();
    }

    public static class ClickResolver implements RecipeViewHolder.RecipeViewHolderListener {
        RecipeAdapter recipeAdapter;

        public ClickResolver(RecipeAdapter adapter) {
            recipeAdapter = adapter;
        }

        @Override
        public void onViewHolderClicked(RecipeDescription recipeDescription) {
            if (recipeAdapter != null && recipeAdapter.mRecipeAdapterListener != null) {
                recipeAdapter.mRecipeAdapterListener.onRecipeAdapterListener(recipeDescription);
            }
            return ;
        }
    }



}
