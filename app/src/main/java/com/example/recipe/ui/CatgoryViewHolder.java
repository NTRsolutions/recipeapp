package com.example.recipe.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.R;
import com.example.recipe.utility.Config;

/**
 * Created by rajnish on 6/8/15.
 */
public class CatgoryViewHolder extends RecyclerView.ViewHolder {

    public interface  ViewHolderListener{
        String onViewHolderClicked(String s);
    }

    protected ImageView mIcon;
    protected TextView mTitle;
    protected TextView mDescription;
    ViewHolderListener listener;

    public CatgoryViewHolder(View view, final ViewHolderListener lstr) {
        super(view);
        this.mIcon = (ImageView) view.findViewById(R.id.icon);
        this.mTitle = (TextView) view.findViewById(R.id.firstLine);
        this.mDescription =   (TextView) view.findViewById(R.id.secondLine);
        this.listener = lstr;

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * Config.MAX_CATEGORY_CARD_HEIGHT_PECENTAGE);
        view.setLayoutParams(layoutParams);
        view.requestLayout();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String description = "this is the most yummlicious recipe";
                    listener.onViewHolderClicked(description);
                    Log.d("TAG","in view holder click" +description);

                }
            }
        });

    }

}
