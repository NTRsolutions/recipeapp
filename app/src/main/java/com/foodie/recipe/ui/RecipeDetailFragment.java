package com.foodie.recipe.ui;


import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.foodie.recipe.R;
import com.foodie.recipe.data.AnalyticsHandler;
import com.foodie.recipe.data.DataUtility;
import com.foodie.recipe.data.DownloadDescFromUrl;
import com.foodie.recipe.data.RecipeDataStore;
import com.foodie.recipe.data.RecipeInfo;
import com.foodie.recipe.data.ShoppingListDataStore;
import com.foodie.recipe.data.TextToSpeechDesc;
import com.foodie.recipe.data.UserInfo;
import com.foodie.recipe.ui.audio.DragToSeekTouchListener;
import com.foodie.recipe.utility.Config;
import com.foodie.recipe.utility.SpringOnTouchListener;
import com.foodie.recipe.utility.Utility;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeDetailFragment extends Fragment {
    public static final String TAG = "RecipeDetailFragment";

    public interface TaskCompletion {
        void onTaskCompletionResult(boolean status, String path);
    }

    public static final String RECIPE_DETAIL_KEY = "RECIPE_DETAIL_KEY";
    private static final int UPDATE_TIME = 1;
    private MessageHandler mMessageHandler = new MessageHandler();
    View mRootView;
    public static float MAX_CARD_HEIGHT_PECENTAGE = 0.35f;
    RecipeInfo mRecipeInfo;
    Boolean mItemUpdated;
    ImageView mRecipeImageView;
    ProgressBar mProgressBar;
    RelativeLayout mContent;
    ImageView mSpeechButton;
    RelativeLayout mOverlay;
    SeekBar mSeekBar;
    RelativeLayout mSoundOverlay;
    ImageView mAudioControls;
    private String mAudioFilePathName;
    private int mAudioFileLength;
    private MediaPlayer mMediaPlayer;
    private boolean mAudioCreated;
    private boolean mPlayRequested;
    DownloadDescFromUrl mDownloadDescFromUrlTask;
    TaskCompletion mTaskCompletionListener;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AnalyticsHandler.getInstance(getActivity()).sendScreenName(this.getClass().getSimpleName());
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_recipe2, null, false);
        int recipeInfoId = getArguments().getInt(RECIPE_DETAIL_KEY, -1);
        mRecipeInfo = RecipeDataStore.getsInstance(getActivity()).getRecipeInfo(recipeInfoId);
        mRecipeInfo.updateLastViewedTime();
        UserInfo.getInstance(getActivity()).logRecipeViewedInfo(mRecipeInfo);
        mRecipeImageView = (ImageView) mRootView.findViewById(R.id.recipe_image);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mContent = (RelativeLayout) mRootView.findViewById(R.id.content);
        mOverlay = (RelativeLayout) mRootView.findViewById(R.id.overlay);
        mSeekBar = (SeekBar) mRootView.findViewById(R.id.audioSeekBar);
        mSoundOverlay = (RelativeLayout) mRootView.findViewById(R.id.soundOverlay);
        mAudioControls = (ImageView) mRootView.findViewById(R.id.audioControls);
        mAudioControls.setColorFilter(getResources().getColor(R.color.colorAccent));

        mOverlay.setVisibility(View.INVISIBLE);
        mContent.setVisibility(View.INVISIBLE);
        setUpBannerSize(mRootView);
        populateImageView();
        showRecipeDetail();

        final DragToSeekTouchListener dragToSeekTouchListener =
                new DragToSeekTouchListener(new DragToSeekUpdateListenerImp());

        mContent.setClickable(true);
        mContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (dragToSeekTouchListener.onTouch(v, event)) {
                    return true;
                }
                return false;
            }
        });

        return mRootView;
    }

    private void showRecipeDetail() {
        String localdescriptionPath = DataUtility.getInstance(getActivity()).getExternalFilesDirPath()
                + "/" + "json" + "/" + mRecipeInfo.getRecipeinfoId() + ".json";
        File decriptionFile = new File(localdescriptionPath);

        if (decriptionFile.exists()) {
            String json = DataUtility.getInstance(getActivity()).loadJSONFromFile(localdescriptionPath);
            RecipeInfo recipeInfo = RecipeInfo.getRecipeDescription(json);

            // fill missing data in json
            recipeInfo.setRecipeinfoId(mRecipeInfo.getRecipeinfoId());
            recipeInfo.setCategory(mRecipeInfo.getCategory());
            mRecipeInfo = recipeInfo;
            populateUI();
        } else {
            String cloudDescriptionPath = Config.sRecipeStorageCloudBaseUrl + "/json/"
                    + mRecipeInfo.getRecipeinfoId() + ".json";
            downloadRecipeDescription(cloudDescriptionPath);
        }
    }

    private void populateImageView() {
        String localImagePath = DataUtility.getInstance(getActivity())
                .getExternalFilesDirPath() + "/images/" + mRecipeInfo.getRecipeinfoId() + ".jpg";
        File imageFile = new File(localImagePath);

        if (imageFile.exists()) {
            Picasso.with(getActivity()).load(imageFile).into(mRecipeImageView);
        } else {
            String cloudImagePath = Config.sRecipeStorageCloudBaseUrl + "/images/"
                    + mRecipeInfo.getRecipeinfoId() + ".jpg";

            Picasso.with(getActivity()).load(cloudImagePath).error(R.mipmap.ic_launcher)
                    .into(mRecipeImageView);
        }
    }

    public void downloadRecipeDescription(String url) {
        String descriptionPath = DataUtility.getInstance(getActivity()).getExternalFilesDirPath()
                + "/json/" + mRecipeInfo.getRecipeinfoId() + ".json";
        mTaskCompletionListener = new TaskCompletionImpl();
        mDownloadDescFromUrlTask = new DownloadDescFromUrl(mTaskCompletionListener,
                url, descriptionPath);
        mDownloadDescFromUrlTask.execute("DownloadFileFromURL Task");
    }

    private class TaskCompletionImpl implements TaskCompletion {
        @Override
        public void onTaskCompletionResult(boolean status, String path) {
            if (status && isAdded()) {
                showRecipeDetail();
            }
        }
    }

    private void populateUI() {
        setupShareRecipe(mRootView);
        setUpFavouriteRecipe(mRootView);
        setUpTitle(mRootView);
        setUpIngredientView(mRootView);
        setUpDirection(mRootView);
        setUpServesTxt(mRootView);
        setUpPrepTimetxt(mRootView);
        setUpNutritionView(mRootView);
        setUpSpeechButton(mRootView);

        mProgressBar.setVisibility(View.INVISIBLE);
        mContent.setVisibility(View.VISIBLE);
    }

    private void createAudio() {
        TextToSpeechDesc texttoSpeech = new TextToSpeechDesc();
        String text = texttoSpeech.convertTextToSpeechDescription(mRecipeInfo);
        HashMap<String, String> myHashRender = new HashMap();
        mAudioFileLength = text.length();
        mAudioFilePathName = DataUtility.getInstance(getActivity())
                .getExternalFilesDirPath() + "/" + "audio.wav";

        File audioFile = new File(mAudioFilePathName);
        if (audioFile.exists()) {
            audioFile.delete();
        }

        if (Utility.getInstance(getActivity()).getTextToSpeech() == null) {
            return;
        }

        Utility.getInstance(getActivity()).getTextToSpeech().setOnUtteranceProgressListener(
                new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.d(TAG, "UtteranceProgressListener : onStart : " + utteranceId);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.d(TAG, "UtteranceProgressListener : onDone : " + utteranceId);
                        mAudioCreated = true;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playMediaPlayer();
                            }
                        });
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.d(TAG, "UtteranceProgressListener : onError : " + utteranceId);
                    }
                });

        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mRecipeInfo.getDocId());
        Utility.getInstance(getActivity()).getTextToSpeech().synthesizeToFile(
                text, myHashRender, mAudioFilePathName);
        Log.d(TAG, "synthesizeToFile complete");
    }

    private void setUpSpeechButton(View rootView) {
        mSpeechButton = (ImageView) rootView.findViewById(R.id.speech);
        mSpeechButton.setVisibility(View.VISIBLE);
        mSpeechButton.setColorFilter(getResources().getColor(R.color.colorAccent));
        createAudio();

        mSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPlayRequested = true;
                    playMediaPlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void playMediaPlayer() {

        if (!mAudioCreated && mPlayRequested) {
            mOverlay.setVisibility(View.VISIBLE);
            Snackbar.make(mOverlay, "Preparing speech for you recipe", Snackbar.LENGTH_LONG)
                    .setAction("show", null)
                    .show();

        }
        if (!mAudioCreated || !mPlayRequested) {
            return;
        }

        mOverlay.setVisibility(View.INVISIBLE);
        try {
            mMediaPlayer = MediaPlayer.create(getActivity(), Uri.fromFile(new File(mAudioFilePathName)));
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateRemainingTime();
        mSoundOverlay.setVisibility(View.VISIBLE);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged : " + fromUser + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch : ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    Log.d(TAG, "onStopTrackingTouch : ");
                    mMediaPlayer.seekTo((int) ((seekBar.getProgress() * 1.0f / seekBar.getMax()) * mMediaPlayer.getDuration()));
                    if (!mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                        updateRemainingTime();
                    }
                }
            }
        });

        mAudioControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioControls.setSelected(!mAudioControls.isSelected());

                if (mAudioControls.isSelected()) {
                    mAudioControls.setImageResource(R.drawable.ic_play_arrow);
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                } else {
                    mAudioControls.setImageResource(R.drawable.pause_volume);
                    if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                        updateRemainingTime();
                    }
                }
            }
        });
    }

    public void setUpPrepTimetxt(View rootView) {
        TextView preparationTitle = (TextView) rootView.findViewById(R.id.preparation_title);
        preparationTitle.setText("Preparation Time");
        TextView preparationTime = (TextView) rootView.findViewById(R.id.preparationtime);
        preparationTime.setText(mRecipeInfo.getPreparationTime());
        preparationTime.setTextSize(Config.TEXT_SIZE_CONTENT);
    }

    public void setUpServesTxt(View rootView) {
        TextView servesTitle = (TextView) rootView.findViewById(R.id.serves_title);
        servesTitle.setText("Serves");
        TextView serves = (TextView) rootView.findViewById(R.id.serves);
        serves.setText(mRecipeInfo.getmServing());
        serves.setTextSize(Config.TEXT_SIZE_CONTENT);
    }

    public void setUpDirection(View rootView) {
        LinearLayout linearLayoutDirection = (LinearLayout) rootView
                .findViewById(R.id.direction_list);
        List<String> listDirection = mRecipeInfo.getDirections();
        for (String direction : listDirection) {
            TextView tvDirection = new TextView(getActivity());
            tvDirection.setText(direction);
            tvDirection.setTextSize(Config.TEXT_SIZE_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    linearLayoutDirection.getLayoutParams());
            params.setMargins(10, 10, 10, 10);
            linearLayoutDirection.addView(tvDirection, params);
        }
    }

    public void setUpNutritionView(View rootView) {
        LinearLayout linearLayoutDirection = (LinearLayout) rootView
                .findViewById(R.id.nutrition_list);
        List<String> listNutrition = mRecipeInfo.getNutritionList();

        if (listNutrition == null) {
            rootView.findViewById(R.id.nutrition).setVisibility(View.GONE);
            return;
        }

        for (String direction : listNutrition) {
            TextView tvNutrition = new TextView(getActivity());
            tvNutrition.setText(direction);
            tvNutrition.setTextSize(Config.TEXT_SIZE_CONTENT);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    linearLayoutDirection.getLayoutParams());
            params.setMargins(10, 10, 10, 10);
            linearLayoutDirection.addView(tvNutrition, params);
        }
    }

    public void setUpTitle(View rootView) {
        TextView recipe = (TextView) rootView.findViewById(R.id.recipeId);
        recipe.setText(mRecipeInfo.getTitle());
    }

    public void setUpBannerSize(View rootView) {
        RelativeLayout banner = (RelativeLayout) rootView.findViewById(R.id.banner);
        ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
        layoutParams.height = (int) (Config.SCREEN_SIZE.y
                * MAX_CARD_HEIGHT_PECENTAGE);
        banner.setLayoutParams(layoutParams);
        banner.requestLayout();
    }

    public void setUpIngredientView(final View rootView) {
        LinearLayout ingredientListLayout = (LinearLayout) rootView.findViewById(R.id.ingredient_list);
        List<String> list = mRecipeInfo.getIngredients();
        Resources resources = getResources();
        for (final String ingredient : list) {
            final LinearLayout innerLineaarLayout = new LinearLayout(getActivity());
            innerLineaarLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLineaarLayout.setGravity(Gravity.CENTER);

            final ImageView addImage = new ImageView(getActivity());
            addImage.setImageResource(R.drawable.addbutton);
            addImage.setColorFilter(resources.getColor(R.color.colorAccent));
            ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(50, 50);
            addImage.setLayoutParams(imageParams);
            addImage.setPadding(0, 3, 10, 3);
            if (ShoppingListDataStore.checkIfItemPresent(mRecipeInfo, ingredient)) {
                addImage.setImageResource(R.drawable.checkbutton);
                innerLineaarLayout.addView(addImage);
                innerLineaarLayout.setSelected(true);
            } else {
                innerLineaarLayout.addView(addImage);
                innerLineaarLayout.setSelected(false);
            }

            TextView tv = new TextView(getActivity());
            tv.setText(ingredient);
            tv.setTextSize(Config.TEXT_SIZE_CONTENT);
            innerLineaarLayout.addView(tv);

            innerLineaarLayout.setOnClickListener(new View.OnClickListener() {
                RelativeLayout parentView = (RelativeLayout) rootView.findViewById(R.id.parentView);

                @Override
                public void onClick(View v) {
                    innerLineaarLayout.setSelected(!innerLineaarLayout.isSelected());
                    boolean isSelected = innerLineaarLayout.isSelected();
                    if (isSelected) {
                        mItemUpdated = ShoppingListDataStore.updateShoppingList(mRecipeInfo, ingredient);
                        Snackbar.make(parentView, "Item is added to shopping cart", Snackbar.LENGTH_LONG)
                                .setAction("show", null)
                                .show();
                        addImage.setImageResource(R.drawable.checkbutton);
                    } else {
                        mItemUpdated = ShoppingListDataStore.deleteShoppingIngredientItem(mRecipeInfo, ingredient);
                        Snackbar.make(parentView, "Item is removed from shopping cart", Snackbar.LENGTH_LONG)
                                .setAction("show", null)
                                .show();
                        addImage.setImageResource(R.drawable.addbutton);

                    }
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ingredientListLayout.getLayoutParams());
            params.setMargins(5, 9, 5, 9);
            ingredientListLayout.addView(innerLineaarLayout, params);
        }
    }

    public void setUpFavouriteRecipe(View rootView) {
        final ImageView favouriteRecipe = (ImageView) rootView.findViewById(R.id.favourite);
        Resources res = rootView.getContext().getResources();
        final int selectedColor = res.getColor(R.color.colorAccent);
        final int unSelectedColor = res.getColor(R.color.grey);

        if (RecipeDataStore.getsInstance(getActivity()).isFavouriteTextTag(mRecipeInfo)) {
            favouriteRecipe.setColorFilter(selectedColor);
            favouriteRecipe.setSelected(true);
        } else {
            favouriteRecipe.setColorFilter(unSelectedColor);
            favouriteRecipe.setSelected(false);
        }

        favouriteRecipe.setOnTouchListener(new SpringOnTouchListener(favouriteRecipe) {
            @Override
            protected void onClick(View view) {
                favouriteRecipe.setSelected(!favouriteRecipe.isSelected());
                if (favouriteRecipe.isSelected()) {
                    favouriteRecipe.setColorFilter(selectedColor);
                    RecipeDataStore.getsInstance(getActivity()).addFavouriteTextTags(
                            mRecipeInfo);
                } else {
                    favouriteRecipe.setColorFilter(unSelectedColor);
                    RecipeDataStore.getsInstance(getActivity()).removeFavouriteTextTags(
                            mRecipeInfo);
                }
            }
        });
    }
    public void setupShareRecipe(View rootView) {
        final ImageView shareRecipe = (ImageView) rootView.findViewById(R.id.shareicon);
        Resources res = rootView.getContext().getResources();
        final int selectedColor = res.getColor(R.color.colorAccent);
        shareRecipe.setColorFilter(selectedColor);
        shareRecipe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, "Recipe @ your finger tips");
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.pickart.android&hl=en");

                startActivity(Intent.createChooser(share, "Share link!"));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        Utility.getInstance(getActivity()).getTextToSpeech().stop();
        if (mAudioFilePathName != null) {
            File audioFile = new File(mAudioFilePathName);
            if (audioFile.exists()) {
                audioFile.delete();
            }
        }

        if (mDownloadDescFromUrlTask != null) {
            mDownloadDescFromUrlTask.updateListener(null);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private class DragToSeekUpdateListenerImp implements
            DragToSeekTouchListener.DragToSeekUpdateListener {

        @Override
        public void onSeekStarted() {
            Log.d(TAG, "onSeekStarted");
//            Utility.getInstance(getActivity()).getTextToSpeech().stop();
//            Utility.getInstance(getActivity()).getTextToSpeech().shutdown();
        }

        @Override
        public void onSeekUpdate(float index) {
            Log.d(TAG, "onSeekUpdate : " + index);
        }

        @Override
        public void onSeekCompleted(float seekedToPercent) {
//            TextToSpeechDesc texttoSpeech = new TextToSpeechDesc();
//            String text = texttoSpeech.convertTextToSpeechDescription(mRecipeInfo);
//
//            String subText = text.substring((int)((seekedToPercent/100) * text.length()));
//            Utility.getInstance(getActivity()).getTextToSpeech().speak(subText, TextToSpeech.QUEUE_FLUSH,
//                    null);
            Log.d(TAG, "onSeekCompleted");
        }
    }

    private void updateRemainingTime() {
        if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) {
            mMessageHandler.removeMessages(UPDATE_TIME);
            return;
        }

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            RecipeDetailFragment.this.mSeekBar.setProgress((int)
                    (mMediaPlayer.getCurrentPosition() * 100.0f/ mMediaPlayer.getDuration()));

            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMessageHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
            }
        }
    }


    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TIME: {
                    updateRemainingTime();
                    break;
                }
            }
        }
    }

}
