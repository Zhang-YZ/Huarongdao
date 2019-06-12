package com.example.zyz.klotski;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zyz.klotski.Assembly.KlotskiButton;
import com.example.zyz.klotski.Assembly.KlotskiLayout;

import java.util.ArrayList;

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

    private RelativeLayout mainLayout;
    private KlotskiLayout klotskiLayout;
    private KlotskiButton[] klotskiButtons;
    private TextView showName, showSteps;
    private Button undoButton,refreshButton,changeCoverButton;
    private KlotskiLayout.ChessButtonArray initChessButtonArray;
    private Typeface myTypeface;

    private float downPressX, downPressY;
    private float downViewX, downViewY;

    private ArrayList<KlotskiLayout.ChessButtonArray> chessHistory;
    private int[][] buttonArray;
    private int buttonNum;

    boolean t = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        /**
         * 全局变量初始化
         */
        mainLayout = this.findViewById(R.id.main_layout);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        unitLength = (int) (this.screenWidth * 0.1);
        unitButtonLength = unitLength * 2;
        xSourcePoint = unitLength;
        ySourcePoint = (screenHeight-unitButtonLength*5)/5;
        steps=0;
        hasPassed = false;
        buttonNum = 10;
        chessHistory = new ArrayList<>();

        /**
         * layout初始化
         */
        klotskiLayout = new KlotskiLayout(GamePage.this);
        final KlotskiLayout.LayoutParams klotskiLayoutParams = new KlotskiLayout.LayoutParams(unitLength * 8, unitLength * 10);
        this.addContentView(klotskiLayout,klotskiLayoutParams);
        klotskiLayout.setX(xSourcePoint);
        klotskiLayout.setY(ySourcePoint*2);
        klotskiLayout.setBackgroundColor(Color.GRAY);
        klotskiLayout.setZ(10000);

        /**
         * chessButtonArray初始化，kbuttons初始化，chesshistory初始化，chess数组初始化
         */
        buttonArray = KlotskiButton.getInitArray(buttonNum);
        initChessButtonArray = new KlotskiLayout.ChessButtonArray(buttonNum,buttonArray);
        klotskiLayout.setChess(initChessButtonArray, true);
        chessHistory.add(initChessButtonArray);
        klotskiButtons = new KlotskiButton[buttonNum];
        for (int i = 0; i < buttonNum; i++) {
            klotskiButtons[i] = new KlotskiButton(this, buttonArray[i], unitButtonLength);
            klotskiButtons[i].setOnTouchListener(new myListener());
            klotskiLayout.addView(klotskiButtons[i]);
        };

        myTypeface = Typeface.createFromAsset(getAssets(), "yan.ttf");

        /**
         * 回退，重置，步数，时间，标题 初始化
         */
        showName = new TextView(this);
        showName.setText("横刀立马");
        RelativeLayout.LayoutParams wordParams= new RelativeLayout.LayoutParams(unitButtonLength*2,(int)(ySourcePoint*1.2));
        this.addContentView(showName,wordParams);
        showName.setX(xSourcePoint*3);
        showName.setY((float)(ySourcePoint*0.25));
        showName.setGravity(Gravity.CENTER);
        showName.setTypeface(myTypeface,Typeface.BOLD);
        showName.setTextSize(30);
        showName.setTextColor(Color.BLACK);


        showSteps = new TextView(this);
        showSteps.setText(getString(R.string.steps_num)+String.valueOf(steps));
        this.addContentView(showSteps,wordParams);
        showSteps.setX(xSourcePoint);
//        showSteps.setY((float)(ySourcePoint*1.5+unitButtonLength*5));
        showSteps.setY((float)(ySourcePoint*1.3));

        showSteps.setGravity(Gravity.LEFT);
        showSteps.setTypeface(myTypeface, Typeface.BOLD);
        showSteps.setTextSize(20);
        showSteps.setTextColor(Color.BLACK);



        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams((int)(unitButtonLength*1.1),(int)(ySourcePoint*0.75));
        float buttonY = (float)(ySourcePoint*2.7+unitButtonLength*5);

        undoButton = new Button(this);
        undoButton.setText(getString(R.string.undo));
        this.addContentView(undoButton,buttonParams);
        undoButton.setX((float)(xSourcePoint*6.8));
        undoButton.setY(buttonY);
        undoButton.setGravity(Gravity.CENTER);
        undoButton.setTypeface(myTypeface,Typeface.BOLD);
        undoButton.setTextSize(20);
        undoButton.setTextColor(Color.BLACK);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chessHistory.size()>1){
                    int diffIndex = chessHistory.get(chessHistory.size()-1).getDiffIndex(chessHistory.get(chessHistory.size()-2));
                    chessHistory.remove(chessHistory.size()-1);
                    int[] lastPosition = chessHistory.get(chessHistory.size()-1).getButtonArray()[diffIndex];
                    klotskiButtons[diffIndex].resetByArray(lastPosition);
                    klotskiLayout.setChess(chessHistory.get(chessHistory.size()-1),false);
                    steps -=1;
                    showSteps.setText(getString(R.string.steps_num)+String.valueOf(steps));
                }
            }
        });

        refreshButton = new Button(this);
        refreshButton.setText(getString(R.string.refresh));
        this.addContentView(refreshButton,buttonParams);
        refreshButton.setX((float)(xSourcePoint*3.9));
        refreshButton.setY(buttonY);
        refreshButton.setGravity(Gravity.CENTER);
        refreshButton.setTypeface(myTypeface,Typeface.BOLD);
        refreshButton.setTextSize(20);
        refreshButton.setTextColor(Color.BLACK);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog();
            }
        });

        changeCoverButton = new Button(this);
        changeCoverButton.setText(getString(R.string.style));
        this.addContentView(changeCoverButton,buttonParams);
        changeCoverButton.setX(xSourcePoint);
        changeCoverButton.setY(buttonY);
        changeCoverButton.setGravity(Gravity.CENTER);
        changeCoverButton.setTypeface(myTypeface,Typeface.BOLD);
        changeCoverButton.setTextSize(20);
        changeCoverButton.setTextColor(Color.BLACK);

        changeCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(t){
                    setStyle(1);
                    t = false;
                }
                else{
                    setStyle(2);
                    t = true;
                }
            }
        });
        setStyle(0);

    }

    public void setStyle(int styleType){
        switch (styleType){
            case 0:
                for(int i=0;i<klotskiButtons.length;i++) {
                    int type = klotskiButtons[i].getType();
                    switch (type) {
                        case 1:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_1);
                            break;
                        case 2:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_1);
                            break;
                        case 3:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_1);
                            break;
                        case 4:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_2);
                            break;
                    }
//                    klotskiButtons[i].setText("");
                }
                klotskiLayout.setBackgroundColor(Color.parseColor("#999999"));
                mainLayout.setBackgroundColor(Color.parseColor("#C7D3E0"));
                undoButton.setBackgroundColor(Color.parseColor("#999999"));
                refreshButton.setBackgroundColor(Color.parseColor("#999999"));
                changeCoverButton.setBackgroundColor(Color.parseColor("#999999"));
                break;
            case 1:
                for(int i=0;i<klotskiButtons.length;i++) {
                    int type = klotskiButtons[i].getType();
                    switch (type) {
                        case 1:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_1);
                            break;
                        case 2:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_2);
                            break;
                        case 3:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_3);
                            break;
                        case 4:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_4);
                            break;
                    }
//                    klotskiButtons[i].setText("");
                }
                klotskiLayout.setBackgroundColor(Color.parseColor("#C1C064"));
                mainLayout.setBackgroundColor(Color.parseColor("#F2F0A4"));
                undoButton.setBackgroundColor(Color.parseColor("#C1C064"));
                refreshButton.setBackgroundColor(Color.parseColor("#C1C064"));
                changeCoverButton.setBackgroundColor(Color.parseColor("#C1C064"));
                break;
            case 2:
                for(int i=0;i<klotskiButtons.length;i++) {
                    int type = klotskiButtons[i].getType();
                    switch (type) {
                        case 1:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_1);
                            break;
                        case 2:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_1);
                            break;
                        case 3:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_1);
                            break;
                        case 4:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style0_2);
                            break;
                    }
//                    klotskiButtons[i].setText(getName(i));
//                    klotskiButtons[i].setTypeface(myTypeface,Typeface.BOLD);
//                    klotskiButtons[i].setTextSize(45);
//                    klotskiButtons[i].setTextColor(Color.BLACK);
                }
                klotskiLayout.setBackgroundColor(Color.parseColor("#999999"));
                mainLayout.setBackgroundColor(Color.parseColor("#C7D3E0"));
                undoButton.setBackgroundColor(Color.parseColor("#999999"));
                refreshButton.setBackgroundColor(Color.parseColor("#999999"));
                changeCoverButton.setBackgroundColor(Color.parseColor("#999999"));
                break;
            case 3:
                for(int i=0;i<klotskiButtons.length;i++) {
                    int type = klotskiButtons[i].getType();
                    switch (type) {
                        case 1:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_1);
                            break;
                        case 2:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_2);
                            break;
                        case 3:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_3);
                            break;
                        case 4:
                            klotskiButtons[i].setBackgroundResource(R.drawable.my_button_style1_4);
                            break;
                    }
                }
                klotskiLayout.setBackgroundColor(Color.parseColor("#C1C064"));
                mainLayout.setBackgroundColor(Color.parseColor("#F2F0A4"));
                undoButton.setBackgroundColor(Color.parseColor("#C1C064"));
                refreshButton.setBackgroundColor(Color.parseColor("#C1C064"));
                changeCoverButton.setBackgroundColor(Color.parseColor("#C1C064"));
                break;
        }

    }


    public String getName(int index){
        switch (index){
            case 0:
                return "曹操";
            case 1:
                return "张飞";
            case 2:
                return "赵云";
            case 3:
                return "马超";
            case 4:
                return "黄忠";
            case 5:
                return "关羽";
            default:
                return "卒";

        }
    }


    private void showNormalDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(GamePage.this,R.style.dialog);
        normalDialog.setMessage("确定要重来吗?");
        normalDialog.setPositiveButton("重来",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chessHistory.clear();
                        chessHistory.add(initChessButtonArray);
                        steps=0;
                        showSteps.setText(getString(R.string.steps_num)+String.valueOf(steps));
                        klotskiLayout.setChess(initChessButtonArray,false);
                        for(int i=0;i<buttonNum;i++){
                            klotskiButtons[i].resetByArray(initChessButtonArray.getButtonArray()[i]);
                        }
                        Toast.makeText(GamePage.this,"ok", Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //pass
                    }
                });
        normalDialog.show();
    }


    class myListener implements View.OnTouchListener {
        float lastViewX, lastViewY;
        KlotskiButton kButton;
        float left, top, right, bottom;

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
                    kButton.setAbsPosition(left,top);
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

                    kButton.setAbsPosition(left,top);
                    kButton.setLocPosition((int)left/unitButtonLength,(int)top/unitButtonLength);
                    klotskiLayout.setChessOne(kButton);

                    int tempIndex = kButton.getIndex();
                    KlotskiLayout.ChessButtonArray lastChess = chessHistory.get(chessHistory.size()-1);
                    if(! (lastChess.getButtonArray()[tempIndex][2] ==kButton.getMyLocX() && lastChess.getButtonArray()[tempIndex][3] == kButton.getMyLocY())){
                        Log.d(LOG_MESSAGE, "================move    ending  in");
                        KlotskiLayout.ChessButtonArray newChess= new KlotskiLayout.ChessButtonArray(lastChess.getButtonNum(),lastChess.getButtonArray());
                        int[] newButton = new int[]{tempIndex,kButton.getType(),kButton.getMyLocX(),kButton.getMyLocY()};
                        newChess.setOneButton(tempIndex,newButton);
                        chessHistory.add(newChess);
                        steps+=1;
                    }

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

