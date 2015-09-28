package com.example.recipe.ui;

import android.content.res.Resources;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.MainActivity;
import com.example.recipe.R;
import com.example.recipe.data.DataUtility;
import com.example.recipe.data.DownloadFileFromURL;
import com.example.recipe.data.RecipeDescription;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.utility.Config;
import com.example.recipe.widgets.FlowLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    private View rootView;
    private CardView mCardView;
    private ImageView mReciepeImageView;
    private Uri mImageUri;
    private TextView mTitle;
    private RecipeViewHolderListener mListener;
    private ImageView mFavouriteImage;
    private RecipeInfo mRecipeInfo;
    private FlowLayout mFlowLayout;

    public interface RecipeViewHolderListener {
        void onViewHolderClicked(RecipeDescription recipeDescription);
    }

    public RecipeViewHolder(Context context, View view, final RecipeViewHolderListener lstr) {
        super(view);
        mContext = context;
        rootView = view.findViewById(R.id.rootView);
        mCardView = (CardView) view.findViewById(R.id.rootView);
        mReciepeImageView = (ImageView) view.findViewById(R.id.icon);
        mTitle = (TextView) view.findViewById(R.id.firstLine);
        mReciepeImageView = (ImageView) view.findViewById(R.id.icon);
        mFlowLayout = (FlowLayout) view.findViewById(R.id.tags);
        mFavouriteImage = (ImageView) view.findViewById(R.id.favourite);
        mListener = lstr;
        ViewGroup.LayoutParams layoutParams = mReciepeImageView.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * Config.MAX_CATEGORY_CARD_HEIGHT_PECENTAGE);
        mReciepeImageView.setLayoutParams(layoutParams);
        mReciepeImageView.requestLayout();
        mReciepeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String description = "this is the most yummlicious recipe";
                    //// TODO: 19/9/15  (rkumar) Debug code to remove later
                    String path = DataUtility.getInstance(mContext).getExternalFilesDirPath()
                            + "/" + "json" + "/" + mRecipeInfo.getRecipeinfoId() + ".json";
                    String json = DataUtility.getInstance(mContext).loadJSONFromFile(path);
                    RecipeDescription recipeDescription = RecipeDescription.getRecipeDescription(json);
                    recipeDescription.setImageUrl(mImageUri);
                    mListener.onViewHolderClicked(recipeDescription);
                    Log.d("TAG", "in view holder click" + description);

                }
            }
        });

        Button browse = (Button) view.findViewById(R.id.browse_button);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) v.getContext()).showDetailViewBrowseFragment();
            }
        });
    }

    private void setUpFavouriteImage(View rootView){
        Resources res = rootView.getContext().getResources();
        final int selectedColor = res.getColor(R.color.blue);
        final int unSelectedColor = res.getColor(R.color.orange);
        mFavouriteImage.setSelected(false);
        mFavouriteImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mFavouriteImage.setSelected(!mFavouriteImage.isSelected());
                if (mFavouriteImage.isSelected()) {
                    mFavouriteImage.setColorFilter(selectedColor);
                } else {
                    mFavouriteImage.setColorFilter(unSelectedColor);
                }

            }
        });

    }

    public void onBindView() {
        Resources resources = mContext.getResources();
        mCardView.setCardBackgroundColor(resources.getColor(R.color.white));
        mTitle.setText(mRecipeInfo.getTitle());
        mReciepeImageView.setImageResource(android.R.color.transparent);
        String localImagePath = DataUtility.getInstance(mContext)
                .getExternalFilesDirPath() + "/images/" + mRecipeInfo.getRecipeinfoId() + ".jpg";

        String cloudImagePath = Config.sRecipeStorageCloudBaseUrl + "/images/"
                + mRecipeInfo.getRecipeinfoId() + ".jpg";

        File imageFile = new File(localImagePath);
        if (imageFile.exists()) {
            mImageUri = Uri.fromFile(imageFile);
            Picasso.with(mContext).load(imageFile).into(mReciepeImageView);
//            mCardView.setCardBackgroundColor(resources.getColor(R.color.lightgreen));
        } else {
            Picasso.with(mContext).load(cloudImagePath).error(R.mipmap.ic_launcher).into(mReciepeImageView);
            downloadRecipeImage(cloudImagePath);
//            mCardView.setCardBackgroundColor(resources.getColor(R.color.lightred));
        }

        setUpFavouriteImage(rootView);
        pupulateCategoryTags();
    }

    public void downloadRecipeImage(String url) {
        String finalPath = DataUtility.getInstance(mContext)
                .getExternalFilesDirPath() + "/images/" + mRecipeInfo.getRecipeinfoId() + ".jpg";
        DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL(
                mContext, url, finalPath);
        downloadFileFromURL.execute("DownloadFileFromURL Task");
    }

    private void pupulateCategoryTags() {
        mFlowLayout.removeAllViews();
        String cateroty = mRecipeInfo.getCategory();

        if (cateroty == null || cateroty.equalsIgnoreCase("")) {
            return;
        }

        String[] categories = cateroty.split("\\|");

        mFlowLayout.setSpacing(5, 10);
        int numTags = categories.length;
        for (int i = 0; i < numTags; ++i) {
            String tagItem = "#" + categories[i];
            TextView tv = new TextView(mContext);
            tv.setText(tagItem);
//            tv.setOnClickListener(createSearchOnClickListener(searchTagItem));
            tv.setTextSize(15);
            tv.setPadding(3, 3, 3, 3);
            tv.setSingleLine();
            tv.setMaxLines(1);
            tv.setTextColor(mContext.getResources().getColor(R.color.blue));
            tv.setEllipsize(TextUtils.TruncateAt.END);
            mFlowLayout.addView(tv);
        }
    }

    public void setRecipeInfo(RecipeInfo mRecipeInfo) {
        this.mRecipeInfo = mRecipeInfo;
    }
}
