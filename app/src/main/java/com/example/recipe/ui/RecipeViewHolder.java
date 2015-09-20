package com.example.recipe.ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.recipe.R;
import com.example.recipe.data.RecipeDescription;
import com.example.recipe.utility.Config;

import java.util.ArrayList;

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
    protected ToggleButton mToggleButton;
    RecipeViewHolderListener mListener;

    public RecipeViewHolder(final View view, final RecipeViewHolderListener lstr) {
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
        final Button downloadButton = (Button) view.findViewById(R.id.download);
        final Button nextButton = (Button) view.findViewById(R.id.next);
        final Button previousButton =(Button) view.findViewById(R.id.previous);
        Button categoryButton = (Button) view.findViewById(R.id.categoryButton);

        final LinearLayout addCategoryLayout = (LinearLayout) view.findViewById(R.id.category);
        final String[] categoryOptions = {"North Indian","South Indian","MilkShakes","Cakes","Chinese"};

        categoryButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Resources res = v.getContext().getResources();
                final int selectedColor = res.getColor(R.color.blue);
                final int unSelectedColor = res.getColor(R.color.grey);
                for(final String options: categoryOptions){
                    final Button addCategoryListButton = new Button(v.getContext());
                    addCategoryListButton.setText(options);
                    addCategoryListButton.setSelected(false);

                    if (addCategoryListButton.isSelected()) {
                        addCategoryListButton.setBackgroundColor(selectedColor);
                    } else {
                        addCategoryListButton.setBackgroundColor(unSelectedColor);
                    }

                    addCategoryLayout.addView(addCategoryListButton);
                    final StringBuffer addCategoryItem = new StringBuffer();
                    addCategoryListButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button btn = (Button) v;
                            btn.setSelected(!btn.isSelected());

                            if (btn.isSelected()) {
                                btn.setBackgroundColor(selectedColor);
                                addCategoryItem.append(addCategoryListButton.getText());
                            } else {
                                btn.setBackgroundColor(unSelectedColor);
                                while (addCategoryItem.indexOf(options) != -1 && options != null) {
                                    int pos = addCategoryItem.indexOf(options);
                                    if (pos < 0) break;
                                    addCategoryItem.delete(pos, pos + addCategoryItem.length());

                                }
                            }

                        }
                    });
                }
            }
        });

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

        previousButton.setOnClickListener(new View.OnClickListener(){

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
