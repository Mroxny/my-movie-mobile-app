package com.mroxny.mymovie;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RateListAdapter extends RecyclerView.Adapter<RateListAdapter.RateViewHolder> {
    private ArrayList<Rate> rateList;
    private ArrayList<Bitmap> imgArr;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class RateViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, rate;
        public ImageView userProfile;
        private Drawable noImage;


        public RateViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            userName = itemView.findViewById(R.id.rateItem_userName);
            userProfile = itemView.findViewById(R.id.rateItem_profile);
            rate = itemView.findViewById(R.id.rateItem_rate);
            noImage = userProfile.getDrawable();



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public RateListAdapter(ArrayList<Rate> exampleList) {
        rateList = exampleList;
    }

    @Override
    public RateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_item, parent, false);
        RateViewHolder evh = new RateViewHolder(v, mListener);
        return evh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RateViewHolder holder, int position) {
        Rate currentItem = rateList.get(position);

        holder.userName.setText(currentItem.getUserName());
        holder.rate.setText(Float.toString(currentItem.getAverageRate()));

        if(imgArr != null && imgArr.get(position) != null) holder.userProfile.setImageBitmap(imgArr.get(position));
        else holder.userProfile.setImageDrawable(holder.noImage);

    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setImages(ArrayList<Bitmap> images){
        imgArr = images;
        notifyDataSetChanged();
    }

}
