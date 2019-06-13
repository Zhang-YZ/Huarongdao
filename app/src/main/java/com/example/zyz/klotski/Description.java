package com.example.zyz.klotski;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Description extends AppCompatActivity {
    private TextView title,content;
    private float screenHeight, screenWidth;
    private Typeface myTypeface;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        relativeLayout = findViewById(R.id.relative_layout_des);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        myTypeface = Typeface.createFromAsset(getAssets(), "yan.ttf");
        int buttonHeight = (int) (screenHeight * 0.15);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (screenWidth*0.8), (int)screenHeight);

        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((int) (screenWidth*0.8), buttonHeight);

        title = new TextView(this);
        title.setText(getString(R.string.game_descrip));
        relativeLayout.addView(title, titleParams);
        title.setX((float) (screenWidth * 0.1));
        title.setY((float) (0));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(myTypeface, Typeface.BOLD);
        title.setTextSize(35);
        title.setTextColor(Color.BLACK);

        content = new TextView(this);
        content.setText(getString(R.string.description_content));
        relativeLayout.addView(content, params);
        content.setX((float) (screenWidth * 0.1));
        content.setY((float) (buttonHeight*0.9));
        content.setGravity(Gravity.LEFT);
        content.setTypeface(myTypeface, Typeface.BOLD);
        content.setTextSize(25);
        content.setTextColor(Color.BLACK);
        relativeLayout.setMinimumHeight((int) (buttonHeight+screenHeight));
    }
}
