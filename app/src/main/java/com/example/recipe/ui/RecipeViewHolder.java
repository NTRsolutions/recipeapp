package com.example.recipe.ui;

import android.content.res.Resources;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.recipe.R;
import com.example.recipe.data.DataUtility;
import com.example.recipe.data.DownloadFileFromURL;
import com.example.recipe.data.RecipeDescription;
import com.example.recipe.data.RecipeInfo;
import com.example.recipe.utility.Config;
import com.example.recipe.widgets.FlowLayout;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.ArrayList;

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
    private RecipeInfo mRecipeInfo;
    final String[] categoryList = {"North Indian","South Indian","MilkShakes","Cakes","Chinese",
            "Bengali", "Tandoor", "snacks", "Thai", "French", "Italian", "Punjabi", "Salad",
            "Pasta", "Kids", "breakfast", "lunch", "dinner"};

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
        mListener = lstr;
        ViewGroup.LayoutParams layoutParams = mReciepeImageView.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * Config.MAX_CATEGORY_CARD_HEIGHT_PECENTAGE);
        mReciepeImageView.setLayoutParams(layoutParams);
        mReciepeImageView.requestLayout();
        final Button downloadButton = (Button) view.findViewById(R.id.download);
        final Button nextButton = (Button) view.findViewById(R.id.next);
        final Button previousButton =(Button) view.findViewById(R.id.previous);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadRecipeImage();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextRecipeImage();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPreviousRecipeImage();
            }
        });



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
    private void setUpCategoryButton(View rootView) {
        Button categoryButton = (Button) rootView.findViewById(R.id.categoryButton);
        categoryButton.setText("Select Category");
        final FlowLayout addCategoryContainer = (FlowLayout) rootView.findViewById(R.id.category);
        addCategoryContainer.removeAllViews();
        addCategoryContainer.setSpacing(10, 10);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button)v;
                if(btn.isSelected()) {
                    // fetch selected list
                    btn.setText("Select Categories");
                    addCategoryContainer.removeAllViews();
                    btn.setSelected(!btn.isSelected());
                    return;
                }

                btn.setSelected(!btn.isSelected());
                btn.setText("Submit Selected Categories");

                // categoryButton not selected add children
                Resources res = v.getContext().getResources();
                final int selectedColor = res.getColor(R.color.blue);
                final int unSelectedColor = res.getColor(R.color.greylight);
                for(final String options: categoryList){
                    final TextView suggestedTextView = new TextView(v.getContext());
                    suggestedTextView.setPadding(5, 5, 5, 5);
                    suggestedTextView.setText(options);
                    suggestedTextView.setSelected(false);

                    if (suggestedTextView.isSelected()) {
                        suggestedTextView.setBackgroundColor(selectedColor);
                    } else {
                        suggestedTextView.setBackgroundColor(unSelectedColor);
                    }
                    addCategoryContainer.addView(suggestedTextView);
                    setSeggestedTextClickListener(suggestedTextView);

                }
            }
        });
    }

    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    public void setSeggestedTextClickListener(final TextView suggestedCategoryView) {
        Resources res = suggestedCategoryView.getContext().getResources();
        final int selectedColor = res.getColor(R.color.blue);
        final int unSelectedColor = res.getColor(R.color.greylight);
        suggestedCategoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                btn.setSelected(!btn.isSelected());
                if (btn.isSelected()) {
                    btn.setBackgroundColor(selectedColor);
                } else {
                    btn.setBackgroundColor(unSelectedColor);
                }
            }
        });
    }

    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    int mCurrentImageCount = 0;

    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    public void showNextRecipeImage() {
        if (mReciepeImageView == null) {
            return;
        }

        ArrayList<String> list = mRecipeInfo.getImageUrlList();

        // When next Clicked firt time trigger all downloads for preview to save time
        if (mCurrentImageCount == 0) {
            for (String url : list) {
                Picasso.with(mContext).load(url);
            }
        }

        mCurrentImageCount++;
        if (mCurrentImageCount == list.size()) {
            mCurrentImageCount = 0;
        }

        String path = list.get(mCurrentImageCount);
        Picasso.with(mContext).load(path).into(mReciepeImageView);

    }

    //// TODO: 19/9/15  (rkumar) Debug code to remove later
    public void showPreviousRecipeImage() {
        if (mReciepeImageView == null) {
            return;
        }

        ArrayList<String> list = mRecipeInfo.getImageUrlList();
        mCurrentImageCount--;
        if (mCurrentImageCount < 0) {
            mCurrentImageCount = list.size() - 1;
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

        setUpCategoryButton(rootView);
    }

    public void setRecipeInfo(RecipeInfo mRecipeInfo) {
        this.mRecipeInfo = mRecipeInfo;
    }
}
