package com.example.recipe.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.recipe.MainActivity;
import com.example.recipe.R;
import com.example.recipe.data.Category;
import com.example.recipe.utility.Config;
import com.squareup.picasso.Picasso;

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
    private Category mCategory;

    public CatgoryViewHolder(View view, final CategoryViewHolderListener lstr) {
        super(view);
        mIcon = (ImageView) view.findViewById(R.id.icon);
        mTitle = (TextView) view.findViewById(R.id.firstLine);
        mContent = view.findViewById(R.id.content);
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
                    ((MainActivity) v.getContext()).showDetailViewBrowseFragment(
                            mCategory.getCategory());
//                    listener.onViewHolderClicked("category clicked");
                    Log.d("TAG","in view holder click");

                }
            }
        });
    }

    public void onBind(Category category) {
        mCategory = category;
        mTitle.setText(category.getCategory());
        Picasso.with(mContent.getContext()).load(category.getUrl())
                .resize(Config.SCREEN_SIZE.x, Config.SCREEN_SIZE.x)
                .centerCrop().into(mIcon);
    }
}
