package com.georgewilliam.speedforce.projectspeedforce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView textViewDistance = (TextView) findViewById(R.id.results_textview_distance_id);
        TextView textViewDuration = (TextView) findViewById(R.id.results_textview_duration_id);
        TextView textViewAverageBPM = (TextView) findViewById(R.id.results_textview_averagebpm_id);
        TextView textViewBurntCalories = (TextView) findViewById(R.id.results_textview_burntcalories_id);
        TextView textViewClimateCondition = (TextView) findViewById(R.id.results_textview_climatecondition_id);
        TextView textViewTemperature = (TextView) findViewById(R.id.results_textview_temperature_id);

        ImageView imageViewAverageBPM = (ImageView) findViewById(R.id.results_imageview_averagebpm_id);
        ImageView imageViewBurntCalories = (ImageView) findViewById(R.id.results_imageview_burntcalories_id);
        ImageView imageViewClimateCondition = (ImageView) findViewById(R.id.results_imageview_climatecondition_id);
        ImageView imageViewTemperature = (ImageView) findViewById(R.id.results_imageview_temperature_id);

        Bundle extras = getIntent().getExtras();
        String strPlaceHolder;
        double dblPlaceHolder;

        strPlaceHolder = Double.toString(extras.getDouble("Distance")) + " km";
        textViewDistance.setText(strPlaceHolder);

        strPlaceHolder = calculateDuration(extras.getLong("TimeElapsed"));
        textViewDuration.setText(strPlaceHolder);

        dblPlaceHolder = extras.getDouble("AverageBPM");
        if (dblPlaceHolder > 0) {
            strPlaceHolder = Double.toString(dblPlaceHolder) + " LPM";
            textViewAverageBPM.setText(strPlaceHolder);
        } else {
            imageViewAverageBPM.setImageResource(R.drawable.img_heart_dimmed);
        }

        if (dblPlaceHolder > 0) {
            dblPlaceHolder = extras.getDouble("BurntCalories");
            strPlaceHolder = Double.toString(dblPlaceHolder) + " Cal";
            textViewBurntCalories.setText(strPlaceHolder);
        } else {
            imageViewBurntCalories.setImageResource(R.drawable.img_fire_dimmed);
        }

        dblPlaceHolder = extras.getDouble("Temperature");
        if (dblPlaceHolder == -1000 ) {
            imageViewTemperature.setImageResource(R.drawable.img_thermometer_dimmed);
        } else {
            strPlaceHolder = Double.toString(dblPlaceHolder) + " °C";
            textViewTemperature.setText(strPlaceHolder);
        }

        strPlaceHolder = extras.getString("ClimateConditionID");
        if (strPlaceHolder != null) {
            textViewClimateCondition.setText(strPlaceHolder);
            switch(strPlaceHolder) {
                case "Despejado":
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_clear);
                    break;
                case "Nublado":
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_clouds);
                    break;
                case "Llovizna":
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_drizzle);
                    break;
                case "Lluvioso":
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_rain);
                    break;
                case "Tormenta Eléctrica":
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_storm);
                    break;
                case "Nevando":
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_snow);
                    break;
                case "Neblina":
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_wind);
                    break;
                default:
                    imageViewClimateCondition.setImageResource(R.drawable.img_weather_dimmed);
                    break;
            }
        }


        Button buttonAccept = (Button) findViewById(R.id.results_button_accept_id);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String calculateDuration(long timeElapsed) {
        int hours = (int) (timeElapsed / 3600000);
        int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
        int seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;
        String duration = Integer.toString(hours) + ":";
        if(minutes < 10) {
            duration += "0";
        }
        duration += Integer.toString(minutes) + ":";
        if(seconds < 10) {
            duration += "0";
        }
        duration += Integer.toString(seconds);
        return duration;
    }
}
