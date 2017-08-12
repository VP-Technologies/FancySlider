package com.vptechnologies.fancyslider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vptechnologies.vpfancyslider.FancySlider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FancySlider slider = (FancySlider) findViewById(R.id.sliderView);
        final TextView text = (TextView) findViewById(R.id.valueTextView);
        final Button scrollButton = (Button) findViewById(R.id.button);

        slider.setOnValueChangedListener(new FancySlider.OnValueChangedListener() {
            @Override
            public void onValueChanged(float value) {
                text.setText(Float.toString(value));
            }
        });

        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slider.scrollToValue(50, true);
            }
        });

    }
}
