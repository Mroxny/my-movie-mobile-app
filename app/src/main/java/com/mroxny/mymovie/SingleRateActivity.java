package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

public class SingleRateActivity extends AppCompatActivity {

    private Rate rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_rate);

        rate = (Rate) getIntent().getSerializableExtra("Rate");

        setInfo();
    }

    private void setInfo(){
        TextView tvBanner, tvAverage;
        String info1, info2;
        float avg;
        RatingBar picture, plot, cast, audio;

        tvAverage = findViewById(R.id.sr_average);
        tvBanner = findViewById(R.id.sr_banner);
        picture = findViewById(R.id.sr_ratePictures);
        plot = findViewById(R.id.sr_ratePlot);
        cast = findViewById(R.id.sr_rateCast);
        audio = findViewById(R.id.sr_rateAudio);

        info1 = rate.getUserName() + " " + getResources().getString(R.string.tag_rate_info1) + " " + '"' +rate.getMovieTitle()+
                '"'+ " " + getResources().getString(R.string.tag_rate_info2) + ":";
        tvBanner.setText(info1);
        picture.setRating(rate.getRatePictures());
        plot.setRating(rate.getRatePlot());
        cast.setRating(rate.getRateCast());
        audio.setRating(rate.getRateAudio());

        info2 =getResources().getString(R.string.tag_average) +": "+ rate.getAverageRate();
        tvAverage.setText(info2);



    }
}