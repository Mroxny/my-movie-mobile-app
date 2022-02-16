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


public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private ArrayList<Movie> movieList;
    private OnItemClickListener mListener;
    private MovieViewHolder holder;
    private ArrayList<Bitmap> imgArr;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public TextView title, orgTitle, year, avgRate, rateNum;
        public ImageView img;
        private Drawable noImage;


        public MovieViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            orgTitle = itemView.findViewById(R.id.item_orgTitle);
            year = itemView.findViewById(R.id.item_year);
            avgRate = itemView.findViewById(R.id.item_avgRate);
            rateNum = itemView.findViewById(R.id.item_ratesNum);
            img = itemView.findViewById(R.id.item_image);
            noImage = img.getDrawable();


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

    public MovieListAdapter(ArrayList<Movie> list) {
        movieList = list;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        MovieViewHolder evh = new MovieViewHolder(v, mListener);

        return evh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        this.holder = holder;
        Movie currentItem = movieList.get(position);

        holder.title.setText(currentItem.getTitle());
        if(currentItem.getOriginalTitle()!=null && currentItem.getOriginalTitle().length()>0) holder.orgTitle.setText(currentItem.getOriginalTitle());
        else holder.orgTitle.setVisibility(View.GONE);
        holder.year.setText(Integer.toString(currentItem.getYear()));
        holder.avgRate.setText(String.format("%.2f",currentItem.getRateAverage()));
        holder.rateNum.setText(Integer.toString(currentItem.getRateNumber()));

        if(imgArr != null && imgArr.get(position) != null) holder.img.setImageBitmap(imgArr.get(position));
        else holder.img.setImageDrawable(holder.noImage);

    }


    @SuppressLint("NotifyDataSetChanged")
    public void setImages(ArrayList<Bitmap> images){
        imgArr = images;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Movie> filteredList) {
        movieList = filteredList;
        notifyDataSetChanged();
    }

}
