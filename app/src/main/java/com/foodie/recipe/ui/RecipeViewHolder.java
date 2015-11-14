package com.foodie.recipe.ui;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foodie.recipe.MainActivity;
import com.foodie.recipe.R;
import com.foodie.recipe.data.AnalyticsHandler;
import com.foodie.recipe.data.DataUtility;
import com.foodie.recipe.data.DownloadFileFromURL;
import com.foodie.recipe.data.RecipeInfo;
import com.foodie.recipe.utility.Config;
import com.foodie.recipe.utility.Utility;
import com.foodie.recipe.widgets.FlowLayout;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = RecipeViewHolder.class.getSimpleName();
    Context mContext;
    private View rootView;
    private CardView mCardView;
    private ImageView mReciepeImageView;
    private Uri mImageUri;
    private TextView mTitle;
    private RecipeViewHolderListener mListener;
    private RecipeInfo mRecipeInfo;
    private FlowLayout mFlowLayout;

    public interface RecipeViewHolderListener {
        void onViewHolderClicked(int recipeInfoId);
        void onTagClicked(String tag);
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
        ProgressBar spinner = (ProgressBar)view.findViewById(R.id.progressBar);
        spinner.getIndeterminateDrawable().setColorFilter(0XFFCDDC39, android.graphics.PorterDuff.Mode.MULTIPLY);
        mListener = lstr;
        ViewGroup.LayoutParams layoutParams = view.findViewById(R.id.image_container).getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * Config.MAX_CATEGORY_CARD_HEIGHT_PECENTAGE);
        mReciepeImageView.setLayoutParams(layoutParams);
        mReciepeImageView.requestLayout();
        mReciepeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onViewHolderClicked(mRecipeInfo.getRecipeinfoId());
                }
            }
        });
    }

    private void setUpFavouriteImage(View rootView){
        Resources res = rootView.getContext().getResources();
        final int selectedColor = res.getColor(R.color.blue);
        final int unSelectedColor = res.getColor(R.color.orange);
    }

    public void onBindView(RecipeInfo dataItem) {
        mRecipeInfo = dataItem;
        Resources resources = mContext.getResources();
        mCardView.setCardBackgroundColor(resources.getColor(R.color.white));
        mTitle.setText(mRecipeInfo.getTitle());
        mReciepeImageView.setImageResource(android.R.color.transparent);
        String localImagePath = DataUtility.getInstance(mContext)
                .getExternalFilesDirPath() + "/images/" + mRecipeInfo.getRecipeinfoId() + ".jpg";
        File imageFile = new File(localImagePath);
        mImageUri  = null;
        if (imageFile.exists()) {
            mImageUri = Uri.fromFile(imageFile);
            Picasso.with(mContext).load(imageFile).into(mReciepeImageView);
//            mCardView.setCardBackgroundColor(resources.getColor(R.color.lightgreen));
        } else {
            String cloudImagePath = Config.sRecipeStorageCloudBaseUrl + "/images/"
                    + mRecipeInfo.getRecipeinfoId() + ".jpg";

            Picasso.with(mContext).load(cloudImagePath).into(mReciepeImageView);
            downloadRecipeImage(cloudImagePath);
//            mCardView.setCardBackgroundColor(resources.getColor(R.color.lightred));
        }

        setUpFavouriteImage(rootView);
        populateCategoryTags();

    }

    public void unBind() {
    }

    public void downloadRecipeImage(String url) {
        String finalPath = DataUtility.getInstance(mContext)
                .getExternalFilesDirPath() + "/images/" + mRecipeInfo.getRecipeinfoId() + ".jpg";
        DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL(
                mContext, url, finalPath);
        downloadFileFromURL.execute("DownloadFileFromURL Task");
    }

    private void populateCategoryTags() {
        mFlowLayout.removeAllViews();

        String[] categories = Utility.getCategories(mRecipeInfo);
        if (categories == null) {
            return;
        }

        mFlowLayout.setSpacing(5, 10);
        int numTags = categories.length;
        for (int i = 0; i < numTags; ++i) {
            String tagItem = "#" + categories[i];
            TextView tv = new TextView(mContext);
            tv.setText(tagItem);
            tv.setOnClickListener(new TagsClickListenerImpl(tagItem));
            tv.setTextSize(15);
            tv.setPadding(10, 10, 10, 10);
            tv.setSingleLine();
            tv.setMaxLines(1);
            tv.setTextColor(mContext.getResources().getColor(R.color.tags_color));
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setBackgroundResource(R.drawable.round_corner_tags);
            mFlowLayout.addView(tv);
        }
    }

    public void setRecipeInfo(RecipeInfo mRecipeInfo) {
        this.mRecipeInfo = mRecipeInfo;
    }

    public class TagsClickListenerImpl implements View.OnClickListener {
        String mSerachSrting;

        TagsClickListenerImpl(String searchString) {
            mSerachSrting = searchString;
        }

        @Override
        public void onClick(View v) {
            mSerachSrting = mSerachSrting.replace("#", "");
            AnalyticsHandler.getInstance(mContext).logAppEvent(
                    AnalyticsHandler.CATEGORY_TAG_STR, "Clicked", mSerachSrting);
            ((MainActivity) v.getContext()).showDetailViewBrowseFragment(mSerachSrting);
        }
    }
}
