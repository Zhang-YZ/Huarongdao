package klotski.klotski.zyz.klotski;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import klotski.klotski.zyz.klotski.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import klotski.klotski.zyz.klotski.Assembly.KlotskiLayout;

public class MainActivity extends AppCompatActivity {
    public static final String FILE_PATH = "/data/data/klotski.klotski.zyz.klotski/userCache.txt";
    private KlotskiLayout.ChessButtonArray nowChessButtonArray;
    private String gameName;
    private String[] tempArray;
    private int buttonNum;
    private int[][] buttonArray;
    private ArrayList<GameInfo> gamesInfo;
    private ArrayList<OneGame> games;
    private Typeface myTypeface;
    private float screenHeight, screenWidth;
    private Button startButton, chooseButton, descripButton, aboutButton;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        myTypeface = Typeface.createFromAsset(getAssets(), "yan.ttf");
        int buttonHeight = (int) (screenHeight * 0.1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (screenWidth * 0.4), buttonHeight);
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams((int) (screenWidth), (int)(buttonHeight*1.2));


        title = new TextView(this);
        title.setText("极简华容道");
        this.addContentView(title, titleParams);
        title.setX((float) (screenWidth * 0));
        title.setY((float) (buttonHeight * 1.2));
        title.setGravity(Gravity.CENTER);
        title.setTypeface(myTypeface, Typeface.BOLD);
        title.setTextSize(70);
        title.setTextColor(Color.BLACK);


        startButton = new Button(this);
        startButton.setText(getString(R.string.start_game));
        this.addContentView(startButton, params);
        startButton.setX((float) (screenWidth * 0.3));
        startButton.setY((float) (buttonHeight * 3.5));
        startButton.setGravity(Gravity.CENTER);
        startButton.setTypeface(myTypeface, Typeface.BOLD);
        startButton.setTextSize(30);
        startButton.setTextColor(Color.BLACK);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame(v);
            }
        });

        chooseButton = new Button(this);
        chooseButton.setText(getString(R.string.choose_game_button));
        this.addContentView(chooseButton, params);
        chooseButton.setX((float) (screenWidth * 0.3));
        chooseButton.setY((float) (buttonHeight * 4.8));
        chooseButton.setGravity(Gravity.CENTER);
        chooseButton.setTypeface(myTypeface, Typeface.BOLD);
        chooseButton.setTextSize(30);
        chooseButton.setTextColor(Color.BLACK);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseGame(v);
            }
        });

        descripButton = new Button(this);
        descripButton.setText(getString(R.string.description_button));
        this.addContentView(descripButton, params);
        descripButton.setX((float) (screenWidth * 0.3));
        descripButton.setY((float) (buttonHeight * 6.1));
        descripButton.setGravity(Gravity.CENTER);
        descripButton.setTypeface(myTypeface, Typeface.BOLD);
        descripButton.setTextSize(30);
        descripButton.setTextColor(Color.BLACK);
        descripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tempIntent = new Intent(MainActivity.this,Description.class);
                startActivity(tempIntent);
            }
        });

        aboutButton = new Button(this);
        aboutButton.setText(getString(R.string.about));
        this.addContentView(aboutButton, params);
        aboutButton.setX((float) (screenWidth * 0.3));
        aboutButton.setY((float) (buttonHeight * 7.4));
        aboutButton.setGravity(Gravity.CENTER);
        aboutButton.setTypeface(myTypeface, Typeface.BOLD);
        aboutButton.setTextSize(30);
        aboutButton.setTextColor(Color.BLACK);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tempIntent = new Intent(MainActivity.this,About.class);
                startActivity(tempIntent);
            }
        });


        nowChessButtonArray = null;
        games = new ArrayList<>();
        try {
            InputStream input = getAssets().open("map.txt");
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader buffReader = new BufferedReader(inputReader);
            String line = buffReader.readLine().replace("\r\n", "");
            while (!line.equals("&&")) {
                gameName = line;
                line = buffReader.readLine().replace("\r\n", "");
                buttonNum = Integer.parseInt(line);
                buttonArray = new int[buttonNum][];
                for (int i = 0; i < buttonNum; i++) {
                    line = buffReader.readLine().replace("\r\n", "");
                    tempArray = line.split(" ");
                    buttonArray[i] = new int[4];
                    for (int j = 0; j < 4; j++) {
                        buttonArray[i][j] = Integer.parseInt(tempArray[j]);
                    }
                }
                KlotskiLayout.ChessButtonArray tempChessButtonArray = new KlotskiLayout.ChessButtonArray(buttonNum, buttonArray);
                OneGame tempGame = new OneGame(tempChessButtonArray, gameName);
                games.add(tempGame);
                line = buffReader.readLine().replace("\r\n", "");
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean check = checkFile(games.size());
        if (!check) {
            writeFile(games.size(), true);
        }
    }

    public void playGame(View view) {
        if (nowChessButtonArray == null) {
            nowChessButtonArray = games.get(0).getChessButtonArray();
            gameName = games.get(0).getGameName();
        }
        Intent intent = new Intent(MainActivity.this, GamePage.class);
        intent.putExtra("nowChessButtonArray", nowChessButtonArray);
        intent.putExtra("gameName", gameName);
        startActivityForResult(intent, 1000);

    }

    public void chooseGame(View view) {
        Intent intent = new Intent(MainActivity.this, ChooseGame.class);
        intent.putExtra("gameNumber", games.size());
        startActivityForResult(intent, 2000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("test", "++++++++++ 1");
        if (requestCode == 2000 && resultCode == 2001) {
            Log.d("test", "++++++++++ 2");

            if (data == null) {
                Log.d("test", "++++++++++ 3");

                return;
            }
            gameName = data.getStringExtra("name");
            if (gameName.equals("") || gameName == null) {
                Log.d("test", "++++++++++ 4");

                return;
            }
            for (int i = 0; i < buttonNum; i++) {
                if (games.get(i).isThisName(gameName)) {
                    nowChessButtonArray = games.get(i).getChessButtonArray();
                    break;
                }
            }
            Log.d("test", "++++++++++ 5");

            playGame(new View(this));

        }
        if (requestCode == 1000 && resultCode == 1001) {
            if (data == null) {
                return;
            }
            gameName = data.getStringExtra("gameName");
            String newTime = data.getStringExtra("time");
            String newStep = data.getStringExtra("steps");
            if (gameName == null || newTime == null || newStep == null) {
                return;
            }

            for (int i = 0; i < games.size(); i++) {
                if (gamesInfo.get(i).getGameName().equals(gameName)) {
                    if (gamesInfo.get(i).getBestStep().equals("---") || Integer.parseInt(newStep) < Integer.parseInt(gamesInfo.get(i).getBestStep())) {
                        gamesInfo.get(i).setBestStep(newStep);
                    }
                    if (gamesInfo.get(i).getBestTime().equals("---")) {
                        gamesInfo.get(i).setBestTime(newTime);
                        break;
                    }
                    String[] timeSplits = newTime.split(":");
                    int time1 = 0, time2 = 0;
                    if (timeSplits.length == 3) {
                        time1 = Integer.parseInt(timeSplits[0]) * 60 * 60
                                + Integer.parseInt(timeSplits[1]) * 60 + Integer.parseInt(timeSplits[2]);
                    } else if (timeSplits.length == 2) {
                        time1 = Integer.parseInt(timeSplits[0]) * 60 + Integer.parseInt(timeSplits[1]);
                    } else {
                        time1 = Integer.parseInt(timeSplits[0]);
                    }
                    timeSplits = gamesInfo.get(i).getBestTime().split(":");
                    if (timeSplits.length == 3) {
                        time2 = Integer.parseInt(timeSplits[0]) * 60 * 60
                                + Integer.parseInt(timeSplits[1]) * 60 + Integer.parseInt(timeSplits[2]);
                    } else if (timeSplits.length == 2) {
                        time2 = Integer.parseInt(timeSplits[0]) * 60 + Integer.parseInt(timeSplits[1]);
                    } else {
                        time2 = Integer.parseInt(timeSplits[0]);
                    }
                    if (time1 < time2) {
                        gamesInfo.get(i).setBestTime(newTime);
                    }
                    break;
                }
            }
            writeFile(games.size(), false);

        }
    }

    public boolean checkFile(int gameNumber) {
        try {
            File f = new File(FILE_PATH);
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
                GameInfo tempGameInfo = new GameInfo(buffReader.readLine().replace("\r\n", ""));
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

    public void writeFile(int gameNumber, boolean init) {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(FILE_PATH));
            buffWriter.write(String.valueOf(gameNumber) + "\r\n");
            if (init) {
                gamesInfo = new ArrayList<>();
                for (int i = 0; i < gameNumber; i++) {
                    GameInfo tempGameInfo = new GameInfo(games.get(i).getGameName());
                    tempGameInfo.setBestTime("---");
                    tempGameInfo.setBestStep("---");
                    gamesInfo.add(tempGameInfo);
                    buffWriter.write(games.get(i).getGameName() + "\r\n");
                    buffWriter.write("---\r\n");
                    buffWriter.write("---\r\n");
                }
            } else {
                for (int i = 0; i < gameNumber; i++) {
                    buffWriter.write(gamesInfo.get(i).getGameName() + "\r\n");
                    buffWriter.write(gamesInfo.get(i).getBestTime() + "\r\n");
                    buffWriter.write(gamesInfo.get(i).getBestStep() + "\r\n");
                }
            }
            buffWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class OneGame implements Serializable {
        private KlotskiLayout.ChessButtonArray chessButtonArray;
        private String gameName;

        OneGame(KlotskiLayout.ChessButtonArray chessButtonArray, String gameName) {
            this.chessButtonArray = chessButtonArray;
            this.gameName = gameName;
        }

        public KlotskiLayout.ChessButtonArray getChessButtonArray() {
            return chessButtonArray;
        }

        public String getGameName() {
            return gameName;
        }

        public boolean isThisName(String gameName) {
            return this.gameName.equals(gameName);
        }
    }

    public static class GameInfo implements Serializable {
        private String gameName, bestTime, bestStep;

        GameInfo(String gameName) {
            this.gameName = gameName;
        }

        public String getBestStep() {
            return bestStep;
        }

        public void setBestStep(String bestStep) {
            this.bestStep = bestStep;
        }

        public String getBestTime() {
            return bestTime;
        }

        public void setBestTime(String bestTime) {
            this.bestTime = bestTime;
        }

        public String getGameName() {
            return gameName;
        }
    }
}
