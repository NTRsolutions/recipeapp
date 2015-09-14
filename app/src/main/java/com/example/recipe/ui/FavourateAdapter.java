package com.example.recipe.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.R;
import com.example.recipe.data.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajnish on 6/8/15.
 */
public class FavourateAdapter extends RecyclerView.Adapter<CatgoryViewHolder> {
    private List<Category> dataItems = new ArrayList<>();

    private Context context;


    public FavourateAdapter(Context context){
        this.context = context;
    }

    @Override
    public CatgoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_card_item,viewGroup, false);
        CatgoryViewHolder mh = new CatgoryViewHolder(v, new ClickResolver());
        return mh;

    }

    @Override
    public void onBindViewHolder(CatgoryViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public static class ClickResolver implements CatgoryViewHolder.CategoryViewHolderListener {

        public ClickResolver(){
        }

        @Override
        public String onViewHolderClicked(String s) {
            return s;
        }
    }



}
