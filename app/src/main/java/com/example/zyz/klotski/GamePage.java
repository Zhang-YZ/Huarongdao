package com.example.zyz.klotski;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zyz.klotski.Assembly.KlotskiButton;
import com.example.zyz.klotski.Assembly.KlotskiLayout;

import java.util.Vector;

import static java.lang.Math.abs;


class Position {
    public int chessX, chessY;
    public float absX, absY;
    Position(float x, float y, boolean isAbs) {
        if (isAbs) {
            this.absX = x;
            this.absY = y;
        } else {
            this.chessX = (int)x;
            this.chessY = (int)y;
        }
    }

}

public class GamePage extends AppCompatActivity {
    public static final String LOG_MESSAGE = "GamePage";
    public static final int LENGTH_CHESS_X = 4, LENGTH_CHESS_Y = 5;
    private float screenWidth, screenHeight;
    private int unitLength, unitButtonLength;
    private float xSourcePoint, ySourcePoint;
    private int steps;
    private boolean hasPassed;


    private KlotskiLayout klotskiLayout;
    private KlotskiButton[] klotskiButtons;
    private TextView showName, showSteps;

    private float downPressX, downPressY;
    private float downViewX, downViewY;

    private Vector<int[][]> chessHistory;
    private int[][] buttonArray,tempButtonArray;
    private int buttonNum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        unitLength = (int) (this.screenWidth * 0.1);
        unitButtonLength = unitLength * 2;
        xSourcePoint = unitLength;
        ySourcePoint = (screenHeight-unitButtonLength*5)/3;
        steps=0;
        hasPassed = false;


        klotskiLayout = new KlotskiLayout(GamePage.this);
        KlotskiLayout.LayoutParams klotskiLayoutParams = new KlotskiLayout.LayoutParams(unitLength * 8, unitLength * 10);
//        klotskiLayout.setLayoutParams(klotskiLayoutParams);
        this.addContentView(klotskiLayout,klotskiLayoutParams);
        klotskiLayout.setX(xSourcePoint);
        klotskiLayout.setY(ySourcePoint);
        klotskiLayout.setBackgroundColor(Color.GRAY);


        buttonNum = 10;
        buttonArray = KlotskiButton.getInitArray(buttonNum);
        chessHistory.clear();
        chessHistory.add(buttonArray);
        klotskiButtons = new KlotskiButton[buttonNum];
        for (int i = 0; i < buttonNum; i++) {
            klotskiButtons[i] = new KlotskiButton(this, buttonArray[i], unitButtonLength);
            klotskiButtons[i].setOnTouchListener(new myListener());
            klotskiLayout.addView(klotskiButtons[i]);
        }
        klotskiLayout.setChess(klotskiButtons, true);


        showName = new TextView(this);
        showName.setText("横刀立马");
        RelativeLayout.LayoutParams nameParams= new RelativeLayout.LayoutParams(unitButtonLength*2,(int)(ySourcePoint*0.5));
        this.addContentView(showName,nameParams);
        showName.setX(xSourcePoint*3);
        showName.setY((float)(ySourcePoint*0.25));
        showName.setGravity(Gravity.CENTER);
        Typeface nameTypeface = Typeface.createFromAsset(getAssets(), "yan.ttf");
        showName.setTypeface(nameTypeface,Typeface.BOLD);
        showName.setTextSize(30);
        showName.setTextColor(Color.BLACK);


        showSteps = new TextView(this);
        showSteps.setText("步数： "+String.valueOf(steps));
        RelativeLayout.LayoutParams stepParams= new RelativeLayout.LayoutParams(unitButtonLength*2,(int)(ySourcePoint*0.5));
        this.addContentView(showSteps,stepParams);
        showSteps.setX(xSourcePoint*3);
        showSteps.setY((float)(ySourcePoint*2+unitButtonLength*5));
        showSteps.setGravity(Gravity.CENTER);
        Typeface stepTypeface = Typeface.createFromAsset(getAssets(),"yan.ttf");
        showSteps.setTypeface(stepTypeface, Typeface.BOLD);
        showSteps.setTextSize(30);
        showSteps.setTextColor(Color.BLACK);


    }


    class myListener implements View.OnTouchListener {
        float lastViewX, lastViewY;
        KlotskiButton kButton;
        float left, top, right, bottom;
        boolean buttonDown=false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(hasPassed){
                return true;
            }

            int tempAction = event.getActionMasked();
            Log.d(LOG_MESSAGE, "===================================touch "+tempAction);
            kButton = (KlotskiButton) v;

            switch (tempAction) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(LOG_MESSAGE, "================down " + String.valueOf(event.getRawX()) + " " + String.valueOf(event.getRawY()));
                    downPressX = event.getRawX();
                    downPressY = event.getRawY();
                    downViewX = v.getX();
                    downViewY = v.getY();
                    lastViewX = downViewX;
                    lastViewY = downViewY;
                    klotskiLayout.setChessZero(kButton);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(LOG_MESSAGE, "================move view " + String.valueOf(v.getX()) + " " + String.valueOf(v.getY()));
                    float nowPressX = event.getRawX();
                    float nowPressY = event.getRawY();
                    float dx = nowPressX - downPressX;
                    float dy = nowPressY - downPressY;
                    Position absPos = moveButton(kButton, dx, dy);
                    left = absPos.absX;//布局内坐标
                    top = absPos.absY;
                    v.setX(left);
                    v.setY(top);
                    lastViewX = v.getX();
                    lastViewY = v.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float baisX, baisY;
                    baisX = v.getX() % (unitButtonLength);
                    baisY = v.getY() % (unitButtonLength);
                    if (baisX < unitLength) {
                        left = v.getX() - baisX;
                    } else {
                        left = v.getX() - baisX + unitButtonLength;
                    }
                    if (baisY < unitLength) {
                        top = v.getY() - baisY;
                    } else {
                        top = v.getY() - baisY + unitButtonLength;
                    }
                    kButton.updatePosition((int) left % (unitButtonLength), (int) top % (unitButtonLength));
                    Log.d(LOG_MESSAGE, "================move    ending " + String.valueOf(left) + "  " + String.valueOf(top) + "  " + String.valueOf(baisX) + "  " + String.valueOf(v.getX()));
                    v.setX(left);
                    v.setY(top);
                    kButton.updatePosition((int)left/unitButtonLength,(int)top/unitButtonLength);
                    klotskiLayout.setChessOne(kButton);
                    if(kButton.getType()==4 && kButton.getMyLocX()==1 && kButton.getMyLocY()==3){
                        hasPassed = true;
                        Toast.makeText(GamePage.this,"过关！！！",Toast.LENGTH_SHORT).show();
                        new Thread(){
                            @Override
                            public void run(){
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                GamePage.this.finish();
                            }
                        }.start();
                    }
                    steps+=1;
                    showSteps.setText(getString(R.string.steps_num)+String.valueOf(steps));
                    break;
            }
            return false;
        }

        Position transToChessPos(float x, float y) {
            int posX = (int) x / unitButtonLength;
            int posY = (int) y / unitButtonLength;
            return new Position(posX, posY,false);
        }

        Position moveButton(KlotskiButton kButton, float dx, float dy) {
            Position lastChessPos = transToChessPos(lastViewX, lastViewY);
            float movedViewX = downViewX + dx;
            float movedViewY = downViewY + dy;
            Position movedChessPos;
            int indexX, indexY;
            boolean flagX = true, flagY = true;
            Position changedXY = null;
            if(movedViewX<0){
                movedViewX=0;
            }
            if(movedViewY<0){
                movedViewY=0;
            }

            switch (kButton.getType()) {
                case 1:
                    if(movedViewX>unitLength*6){
                        movedViewX=unitLength*6;
                    }
                    if(movedViewY>unitLength*8){
                        movedViewY=unitLength*8;
                    }
                    movedChessPos = transToChessPos(movedViewX, movedViewY);
                    boolean xFirst = true;
//                    Log.d(LOG_MESSAGE, "================move begin "+String.valueOf(lastViewX)+"  "+String.valueOf(lastViewY));

                    if (lastViewX % (unitButtonLength) == 0 && lastViewY % (unitButtonLength) == 0) {
//                        Log.d(LOG_MESSAGE, "================move      begin");
                        if (abs(dx) < abs(dy)) {
                            xFirst = false;
                        }
                    }
                    else{
                        xFirst=false;
                    }
                    if (lastViewX % (unitButtonLength) != 0 || xFirst) {
                        if (movedChessPos.chessX>=lastChessPos.chessX) {
                            for (indexX = lastChessPos.chessX; indexX <= movedChessPos.chessX+1; indexX++) {
                                if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY)) {
                                    flagX = false;
                                    break;
                                }
                            }
                            if (!flagX) {
                                movedViewX = (indexX-1) * unitButtonLength;
                            }
//                            Log.d(LOG_MESSAGE, "================move                  dx>0 "+String.valueOf(movedViewX));
                        } else {
                            for (indexX = lastChessPos.chessX; indexX >= movedChessPos.chessX; indexX--) {
                                if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY)) {
                                    flagX = false;
                                    break;
                                }
                            }
                            if (!flagX) {
                                movedViewX = (indexX + 1) * unitButtonLength;
                            }
//                            Log.d(LOG_MESSAGE, "================move                  dx<0 "+String.valueOf(movedViewX));

                        }
                        movedChessPos = transToChessPos(movedViewX, movedViewY);
                        if (movedViewX % (unitButtonLength) == 0) {
                            if (movedChessPos.chessY>=lastChessPos.chessY) {
                                for (indexY = lastChessPos.chessY; indexY <= movedChessPos.chessY+1; indexY++) {
                                    if (!klotskiLayout.judgeEmpty(movedChessPos.chessX, indexY)) {
                                        flagY = false;
                                        break;
                                    }
                                }
                                if (!flagY) {
                                    movedViewY = (indexY - 1) * unitButtonLength;
                                }
//                                Log.d(LOG_MESSAGE, "================move                  dx  dy>0 "+String.valueOf(indexY) +" "+ String.valueOf(movedViewY));

                            } else {
                                for (indexY = lastChessPos.chessY; indexY >= movedChessPos.chessY; indexY--) {
                                    if (!klotskiLayout.judgeEmpty(movedChessPos.chessX, indexY)) {
                                        flagY = false;
                                        break;
                                    }
                                }
                                if (!flagY) {
                                    movedViewY = (indexY + 1) * unitButtonLength;
                                }

//                                Log.d(LOG_MESSAGE, "================move                  dx  dy<0 "+String.valueOf(movedViewY));

                            }
                        } else {
                            movedViewY = lastViewY;
//                            Log.d(LOG_MESSAGE, "================move                  dx  dy "+String.valueOf(movedViewY));

                        }
                    } else {
                        if (movedChessPos.chessY>=lastChessPos.chessY) {
//                            Log.d(LOG_MESSAGE, "================move                  error dy>0 "+String.valueOf(movedViewY)+" "+String.valueOf(dy));

                            for (indexY = lastChessPos.chessY; indexY <= movedChessPos.chessY+1; indexY++) {
                                if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY)) {
                                    flagY = false;
                                    break;
                                }
                            }
                            if (!flagY) {
                                movedViewY = (indexY - 1) * unitButtonLength;
                            }
//                            Log.d(LOG_MESSAGE, "================move                  dy>0 "+String.valueOf(movedViewY));

                        } else {
                            for (indexY = lastChessPos.chessY; indexY >= movedChessPos.chessY; indexY--) {
                                if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY)) {
                                    flagY = false;
                                    break;
                                }
                            }
                            if (!flagY) {
                                movedViewY = (indexY + 1) * unitButtonLength;
                            }
//                            Log.d(LOG_MESSAGE, "================move                  dy<0 "+String.valueOf(movedViewY));

                        }
                        movedChessPos = transToChessPos(movedViewX, movedViewY);
                        if (movedViewY % (unitButtonLength) == 0) {
                            if (movedChessPos.chessX>=lastChessPos.chessX) {
                                for (indexX = lastChessPos.chessX; indexX <= movedChessPos.chessX+1; indexX++) {
                                    if (!klotskiLayout.judgeEmpty(indexX, movedChessPos.chessY)) {
                                        flagX = false;
                                        break;
                                    }
                                }
                                if (!flagX) {
                                    movedViewX = (indexX - 1) * unitButtonLength;
                                }
//                                Log.d(LOG_MESSAGE, "================move                  dy  dx>0 "+String.valueOf(movedViewX));

                            } else {
                                for (indexX = lastChessPos.chessX; indexX >= movedChessPos.chessX; indexX--) {
                                    if (!klotskiLayout.judgeEmpty(indexX, movedChessPos.chessY)) {
                                        flagX = false;
                                        break;
                                    }
                                }
                                if (!flagX) {
                                    movedViewX = (indexX + 1) * unitButtonLength;
                                }
//                                Log.d(LOG_MESSAGE, "================move                  dy  dx<0 "+String.valueOf(movedViewX));

                            }
                        } else {
                            movedViewX = lastViewX;
//                            Log.d(LOG_MESSAGE, "================move                  dy  dx "+String.valueOf(movedViewX));

                        }
                    }
                    changedXY = new Position(movedViewX, movedViewY, true);
                    break;
                case 2:
                    if(movedViewX>unitLength*4){
                        movedViewX=unitLength*4;
                    }
                    if(movedViewY>unitLength*8){
                        movedViewY=unitLength*8;
                    }
                    movedChessPos = transToChessPos(movedViewX, movedViewY);

                    if (movedChessPos.chessX>=lastChessPos.chessX) {
                        for (indexX = lastChessPos.chessX + 1; indexX <= movedChessPos.chessX + 2; indexX++) {
                            if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY)) {
                                flagX = false;
                                break;
                            }
                        }
                        if (!flagX) {
                            movedViewX = (indexX - 2) * unitButtonLength;
                        }
                    } else {
                        for (indexX = lastChessPos.chessX; indexX >= movedChessPos.chessX; indexX--) {
                            if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY)) {
                                flagX = false;
                                break;
                            }
                        }
                        if (!flagX) {
                            movedViewX = (indexX + 1) * unitButtonLength;
                        }
                    }
                    if (movedChessPos.chessY>=lastChessPos.chessY) {
                        for (indexY = lastChessPos.chessY; indexY <= movedChessPos.chessY+1; indexY++) {
                            if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY) || !klotskiLayout.judgeEmpty(lastChessPos.chessX + 1, indexY)) {
                                flagY = false;
                                break;
                            }

                        }
                        if (!flagY) {
                            movedViewY = (indexY - 1) * unitButtonLength;
                        }
                    } else {
                        for (indexY = lastChessPos.chessY; indexY >= movedChessPos.chessY; indexY--) {
                            if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY) || !klotskiLayout.judgeEmpty(lastChessPos.chessX + 1, indexY)) {
                                flagY = false;
                                break;
                            }
                        }
                        if (!flagY) {
                            movedViewY = (indexY + 1) * unitButtonLength;
                        }
                    }
                    changedXY = new Position(movedViewX, movedViewY, true);
                    break;
                case 3:
                    if(movedViewX>unitLength*6){
                        movedViewX=unitLength*6;
                    }
                    if(movedViewY>unitLength*6){
                        movedViewY=unitLength*6;
                    }
                    movedChessPos = transToChessPos(movedViewX, movedViewY);
                    if (movedChessPos.chessX>=lastChessPos.chessX) {
                        for (indexX = lastChessPos.chessX; indexX <= movedChessPos.chessX+1; indexX++) {
                            if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY) || !klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY + 1)) {
                                flagX = false;
                                break;
                            }
                        }
                        if (!flagX) {
                            movedViewX = (indexX - 1) * unitButtonLength;
                        }
                    } else {
                        for (indexX = lastChessPos.chessX; indexX >= movedChessPos.chessX; indexX--) {
                            if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY) || !klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY + 1)) {
                                flagX = false;
                                break;
                            }
                        }
                        if (!flagX) {
                            movedViewX = (indexX + 1) * unitButtonLength;
                        }
                    }
                    if (movedChessPos.chessY>=lastChessPos.chessY) {
                        for (indexY = lastChessPos.chessY + 1; indexY <= movedChessPos.chessY + 2; indexY++) {
                            if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY)) {
                                flagY = false;
                                break;
                            }

                        }
                        if (!flagY) {
                            movedViewY = (indexY - 2) * unitButtonLength;
                        }
                    } else {
                        for (indexY = lastChessPos.chessY; indexY >= movedChessPos.chessY; indexY--) {
                            if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY)) {
                                flagY = false;
                                break;
                            }
                        }
                        if (!flagY) {
                            movedViewY = (indexY + 1) * unitButtonLength;
                        }
                    }
                    changedXY = new Position(movedViewX, movedViewY, true);
                    break;
                case 4:
                    if(movedViewX>unitLength*4){
                        movedViewX=unitLength*4;
                    }
                    if(movedViewY>unitLength*6){
                        movedViewY=unitLength*6;
                    }
                    movedChessPos = transToChessPos(movedViewX, movedViewY);
                    if (movedChessPos.chessX>=lastChessPos.chessX) {
                        for (indexX = lastChessPos.chessX + 1; indexX <= movedChessPos.chessX + 2; indexX++) {
                            if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY) || !klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY + 1)) {
                                flagX = false;
                                break;
                            }
                        }
                        if (!flagX) {
                            movedViewX = (indexX - 2) * unitButtonLength;
                        }
                    } else {
                        for (indexX = lastChessPos.chessX; indexX >= movedChessPos.chessX; indexX--) {
                            if (!klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY) || !klotskiLayout.judgeEmpty(indexX, lastChessPos.chessY + 1)) {
                                flagX = false;
                                break;
                            }
                        }
                        if (!flagX) {
                            movedViewX = (indexX + 1) * unitButtonLength;
                        }
                    }
                    if (movedChessPos.chessY>=lastChessPos.chessY) {
                        for (indexY = lastChessPos.chessY + 1; indexY <= movedChessPos.chessY + 2; indexY++) {
                            if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY) || !klotskiLayout.judgeEmpty(lastChessPos.chessX + 1, indexY)) {
                                flagY = false;
                                break;
                            }

                        }
                        if (!flagY) {
                            movedViewY = (indexY - 2) * unitButtonLength;
                        }
                    } else {
                        for (indexY = lastChessPos.chessY; indexY >= movedChessPos.chessY; indexY--) {
                            if (!klotskiLayout.judgeEmpty(lastChessPos.chessX, indexY) || !klotskiLayout.judgeEmpty(lastChessPos.chessX + 1, indexY)) {
                                flagY = false;
                                break;
                            }
                        }
                        if (!flagY) {
                            movedViewY = (indexY + 1) * unitButtonLength;
                        }
                    }
                    changedXY = new Position(movedViewX, movedViewY, true);
                    break;
            }
            return changedXY;
        }
    }

}

