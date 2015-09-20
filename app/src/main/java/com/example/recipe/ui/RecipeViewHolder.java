package com.example.recipe.ui;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.R;
import com.example.recipe.data.DataUtility;
import com.example.recipe.data.DownloadFileFromURL;
import com.example.recipe.data.RecipeDescription;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.utility.Config;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rajnish on 6/8/15.
 */
public class RecipeViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    private CardView mCardView;
    private ImageView mReciepeImageView;
    private Uri mImageUri;
    private TextView mTitle;
    private RecipeViewHolderListener mListener;
    private RecipeInfo mRecipeInfo;

    public interface RecipeViewHolderListener {
        void onViewHolderClicked(RecipeDescription recipeDescription);
    }

    public RecipeViewHolder(Context context, View view, final RecipeViewHolderListener lstr) {
        super(view);
        mContext = context;
        mCardView = (CardView) view.findViewById(R.id.rootView);
        mReciepeImageView = (ImageView) view.findViewById(R.id.icon);
        mTitle = (TextView) view.findViewById(R.id.firstLine);
        mReciepeImageView = (ImageView) view.findViewById(R.id.icon);
        mListener = lstr;

        ViewGroup.LayoutParams layoutParams = mReciepeImageView.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * Config.MAX_CATEGORY_CARD_HEIGHT_PECENTAGE);
        mReciepeImageView.setLayoutParams(layoutParams);
        mReciepeImageView.requestLayout();

        Button downloadButton = (Button) view.findViewById(R.id.download);
        Button nextButton = (Button) view.findViewById(R.id.next);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadRecipeImage();
            }

        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipeImage();
            }
        });

        mListener = lstr;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String description = "this is the most yummlicious recipe";
                    //// TODO: 19/9/15  (rkumar) Debug code to remove later
                    String path = DataUtility.getInstance(mContext).getExternalFilesDirPath()
                            + "/" + "json" + "/" + mRecipeInfo.getId() + ".json";
                    String json = DataUtility.getInstance(mContext).loadJSONFromFile(path);
                    RecipeDescription recipeDescription = RecipeDescription.getRecipeDescription(json);
                    recipeDescription.setImageUrl(mImageUri);
                    mListener.onViewHolderClicked(recipeDescription);
                    Log.d("TAG", "in view holder click" + description);

                }
            }
        });
    }

    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    int mCurrentImageCount = 0;
    public void updateRecipeImage() {
        if (mReciepeImageView == null) {
            return;
        }

        ArrayList<String> list = mRecipeInfo.getImageUrlList();
        mCurrentImageCount++;
        if (mCurrentImageCount == list.size()) {
            mCurrentImageCount = 0;
        }

        String path = list.get(mCurrentImageCount);
        Picasso.with(mContext).load(path).into(mReciepeImageView);
    }

    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    public void downloadRecipeImage() {
        ArrayList<String> list = mRecipeInfo.getImageUrlList();
        String url = list.get(mCurrentImageCount);
        String finalPath = DataUtility.getInstance(mContext)
                .getExternalFilesDirPath() + "/images/" + mRecipeInfo.getId() + ".jpg";

        DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL(
                mContext, url, finalPath);
        downloadFileFromURL.execute("DownloadFileFromURL Task");
    }

    public void onBindView() {
        Resources resources = mContext.getResources();
        mCardView.setCardBackgroundColor(resources.getColor(R.color.white));
        mCurrentImageCount = 0;
        mTitle.setText(mRecipeInfo.getTitle());
        mReciepeImageView.setImageResource(android.R.color.transparent);
        String localImagePath = DataUtility.getInstance(mContext)
                .getExternalFilesDirPath() + "/images/" + mRecipeInfo.getId() + ".jpg";
        File imageFile = new File(localImagePath);
        if (imageFile.exists()) {
            mImageUri = Uri.fromFile(imageFile);
            Picasso.with(mContext).load(imageFile).into(mReciepeImageView);
            mCardView.setCardBackgroundColor(resources.getColor(R.color.lightgreen));
        } else {
            //// TODO: 19/9/15  (rkumar) Debug code to remove later
            DataUtility dataUtility = DataUtility.getInstance(mContext);
            HashMap<String, ArrayList<String>> map = dataUtility.getImageUrlMapList();
            ArrayList<String> imageUrlList = map.get(mRecipeInfo.getId()+"");

            if (imageUrlList == null) {
                dataUtility.networkImageRequest(Integer.toString(
                        mRecipeInfo.getId()), mRecipeInfo.getTitle() + " reciepe");
            } else {
                mRecipeInfo.setImageUrl(imageUrlList);
                mImageUri = Uri.parse(imageUrlList.get(0));
                Picasso.with(mContext).load(mImageUri).into(mReciepeImageView);
                mCardView.setCardBackgroundColor(resources.getColor(R.color.lightred));
            }
        }
    }

    public void setRecipeInfo(RecipeInfo mRecipeInfo) {
        this.mRecipeInfo = mRecipeInfo;
    }
}
