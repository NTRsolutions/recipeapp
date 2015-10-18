package com.example.recipe.ui;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.MainActivity;
import com.example.recipe.R;
import com.example.recipe.data.DataUtility;
import com.example.recipe.data.DownloadFileFromURL;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.utility.Config;
import com.example.recipe.utility.Utility;
import com.example.recipe.widgets.FlowLayout;
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
    private ImageView mFavouriteImage;
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
                    mListener.onViewHolderClicked(mRecipeInfo.getRecipeinfoId());
                }
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

            Picasso.with(mContext).load(cloudImagePath).error(R.mipmap.ic_launcher).into(mReciepeImageView);
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
        String categoryStr = mRecipeInfo.getCategory();

        if (categoryStr == null || categoryStr.equalsIgnoreCase("")) {
            return;
        }

        String[] categories = Utility.getCategories(categoryStr);

        mFlowLayout.setSpacing(5, 10);
        int numTags = categories.length;
        for (int i = 0; i < numTags; ++i) {
            String tagItem = "#" + categories[i];
            TextView tv = new TextView(mContext);
            tv.setText(tagItem);
            tv.setOnClickListener(new TagsClickListenerImpl(tagItem));
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

    public class TagsClickListenerImpl implements View.OnClickListener {
        String mSerachSrting;

        TagsClickListenerImpl(String searchString) {
            mSerachSrting = searchString;
        }

        @Override
        public void onClick(View v) {
            mSerachSrting = mSerachSrting.replace("#", "");
            ((MainActivity) v.getContext()).showDetailViewBrowseFragment(mSerachSrting);
        }
    }
}
