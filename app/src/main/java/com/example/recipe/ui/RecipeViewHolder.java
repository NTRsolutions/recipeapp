package com.example.recipe.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.R;
import com.example.recipe.data.RecipeDescription;
import com.example.recipe.utility.Config;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeViewHolder extends RecyclerView.ViewHolder {

    public interface RecipeViewHolderListener {
        void onViewHolderClicked(RecipeDescription recipeDescription);
    }

    private View mContent;
    protected ImageView mIcon;
    protected TextView mTitle;
    RecipeViewHolderListener mListener;

    public RecipeViewHolder(View view, final RecipeViewHolderListener lstr) {
        super(view);
        mIcon = (ImageView) view.findViewById(R.id.icon);
        mTitle = (TextView) view.findViewById(R.id.firstLine);
        mContent = view.findViewById(R.id.icon);
        mListener = lstr;

        ViewGroup.LayoutParams layoutParams = mContent.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * Config.MAX_CATEGORY_CARD_HEIGHT_PECENTAGE);
        mContent.setLayoutParams(layoutParams);
        mContent.requestLayout();
        Button downloadButton = (Button) view.findViewById(R.id.download);
        Button nextButton = (Button) view.findViewById(R.id.next);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String description = "this is the most yummlicious recipe";
                    RecipeDescription recipeDescription = RecipeDescription.getRecipeDescription();
                    mListener.onViewHolderClicked(recipeDescription);
                    Log.d("TAG","in view holder click" +description);

                }
            }
        });
    }
}
