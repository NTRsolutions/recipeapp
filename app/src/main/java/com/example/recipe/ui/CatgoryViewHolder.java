package com.example.recipe.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.recipe.R;
import com.example.recipe.utility.Config;

/**
 * Created by rajnish on 6/8/15.
 */
public class CatgoryViewHolder extends RecyclerView.ViewHolder {

    public interface CategoryViewHolderListener {
        String onViewHolderClicked(String s);
    }

    private View mContent;
    protected ImageView mIcon;
    protected TextView mTitle;
    CategoryViewHolderListener listener;

    public CatgoryViewHolder(View view, final CategoryViewHolderListener lstr) {
        super(view);
        mIcon = (ImageView) view.findViewById(R.id.icon);
        mTitle = (TextView) view.findViewById(R.id.firstLine);
        mContent = (RelativeLayout) view.findViewById(R.id.content);
        listener = lstr;

        ViewGroup.LayoutParams layoutParams = mContent.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * Config.MAX_CATEGORY_CARD_HEIGHT_PECENTAGE);
        mContent.setLayoutParams(layoutParams);
        mContent.requestLayout();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onViewHolderClicked("category clicked");
                    Log.d("TAG","in view holder click");

                }
            }
        });

    }

}
