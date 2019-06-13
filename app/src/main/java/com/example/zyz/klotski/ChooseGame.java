package com.example.zyz.klotski;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.service.chooser.ChooserTargetService;
import android.support.constraint.solver.widgets.ConstraintHorizontalLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChooseGame extends AppCompatActivity {
    private ArrayList<MainActivity.GameInfo> gamesInfo;
    private Button[] buttons;
    private float screenHeight, screenWidth;
    private RelativeLayout relativeLayout;
    private Typeface myTypeface;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        myTypeface = Typeface.createFromAsset(getAssets(), "yan.ttf");
        int buttonHeight = (int) (screenHeight * 0.15);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (screenWidth * 0.8), buttonHeight);
        relativeLayout = findViewById(R.id.relative_layout);
//        relativeLayout.setBackgroundColor(Color.parseColor("#999999"));

        title = new TextView(this);
        title.setText(getString(R.string.choose_game_page));
        relativeLayout.addView(title, params);
        title.setX((float) (screenWidth * 0.1));
        title.setY((float) (0));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(myTypeface, Typeface.BOLD);
        title.setTextSize(30);
        title.setTextColor(Color.BLACK);

        Intent intent = getIntent();
        int gameNumber = intent.getIntExtra("gameNumber", 10);
        checkFile(gameNumber);


        buttons = new Button[gameNumber];
        for (int i = 0; i < gameNumber; i++) {
            buttons[i] = new Button(this);
            MainActivity.GameInfo temp = gamesInfo.get(i);
            relativeLayout.addView(buttons[i], params);
            buttons[i].setX((float) (screenWidth * 0.1));
            buttons[i].setY((float) (buttonHeight * 1.2 * i + screenHeight * 0.15));
            buttons[i].setText(temp.getGameName() + "\r\n\r\n" + "时间：" + temp.getBestTime() + "  步数：" + temp.getBestStep());
            buttons[i].setGravity(Gravity.CENTER);
            buttons[i].setTypeface(myTypeface, Typeface.BOLD);
            buttons[i].setTextSize(23);
//            buttons[i].setBackgroundColor(Color.parseColor("#D9DADB"));
            buttons[i].setTextColor(Color.BLACK);
            buttons[i].setOnClickListener(new myClickLister(temp.getGameName()));
        }
        relativeLayout.setMinimumHeight((int) (buttonHeight * 1.2 * gameNumber + screenHeight * 0.2));


    }

    class myClickLister implements View.OnClickListener {
        private String gameName;

        myClickLister(String name) {
            this.gameName = name;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ChooseGame.this, MainActivity.class);
            intent.putExtra("name", this.gameName);
            setResult(2001, intent);
            ChooseGame.this.finish();
        }
    }

    public void returnBack(String gameName) {
        Intent intent = new Intent(ChooseGame.this, MainActivity.class);
        intent.putExtra("name", gameName);
        setResult(2, intent);
        ChooseGame.this.finish();
    }

    public boolean checkFile(int gameNumber) {
        try {
            File f = new File(MainActivity.FILE_PATH);
            if (!f.exists()) {
                return false;
            }
            InputStream input = new FileInputStream(f);
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader buffReader = new BufferedReader(inputReader);
            int checkfile = Integer.parseInt(buffReader.readLine().replace("\r\n", ""));
            if (checkfile != gameNumber) {
                return false;
            }
            gamesInfo = new ArrayList<>();
            for (int i = 0; i < gameNumber; i++) {
                MainActivity.GameInfo tempGameInfo = new MainActivity.GameInfo(buffReader.readLine().replace("\r\n", ""));
                tempGameInfo.setBestTime(buffReader.readLine().replace("\r\n", ""));
                tempGameInfo.setBestStep(buffReader.readLine().replace("\r\n", ""));
                gamesInfo.add(tempGameInfo);
            }
            buffReader.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
