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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    public static final String TAG = "RecipeAdapter";
    private List<RecipeInfo> mDataItems = new ArrayList<>();
    private Context mContext;
    private RecipeAdapterListener mRecipeAdapterListener;

    public interface RecipeAdapterListener {
        void onRecipeAdapterListener(RecipeInfo recipeInfo);
    }

    public RecipeAdapter(Context context, RecipeAdapterListener recipeAdapterListener){
        this.mContext = context;
        this.mRecipeAdapterListener = recipeAdapterListener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recipe_card_item,viewGroup, false);
        RecipeViewHolder mh = new RecipeViewHolder(mContext, v, new ClickResolver(this));
        return mh;

    }

    @Override
    public void onBindViewHolder(RecipeViewHolder myViewHolder, int i) {
        Log.d(TAG, "onBindViewHolder ");
        RecipeInfo dataItem = mDataItems.get(i);
        myViewHolder.setRecipeInfo(dataItem);
        myViewHolder.onBindView();
    }

    @Override
    public int getItemCount() {
        return mDataItems.size();
    }

    public void updateDataList(List<RecipeInfo> dataItems) {
        mDataItems = dataItems;
    }

    public static class ClickResolver implements RecipeViewHolder.RecipeViewHolderListener {
        RecipeAdapter recipeAdapter;

        public ClickResolver(RecipeAdapter adapter) {
            recipeAdapter = adapter;
        }

        @Override
        public void onViewHolderClicked(RecipeInfo recipeInfo) {
            if (recipeAdapter != null && recipeAdapter.mRecipeAdapterListener != null) {
                recipeAdapter.mRecipeAdapterListener.onRecipeAdapterListener(recipeInfo);
            }
            return ;
        }
    }



}
