package com.example.recipe.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipe.R;

/**
 * Created by rajnish on 6/8/15.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    public interface  ViewHolderListener{
        String onViewHolderClicked(String s);
    }

    protected ImageView icon;
    protected TextView firstLine;
    protected TextView secondLine;
    ViewHolderListener listener;

    public MyViewHolder(View view, final ViewHolderListener lstr) {
        super(view);
        this.icon = (ImageView) view.findViewById(R.id.icon);
        this.firstLine = (TextView) view.findViewById(R.id.firstLine);
        this.secondLine =   (TextView) view.findViewById(R.id.secondLine);
        this.listener = lstr;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String description = "this is the most yummlicious recipe";
                    listener.onViewHolderClicked(description);
                    Log.d("TAG","in view holder click" +description);

                }
            }
        });

    }

}
