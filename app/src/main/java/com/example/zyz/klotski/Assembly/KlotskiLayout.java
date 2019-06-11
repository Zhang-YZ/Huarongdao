package com.example.zyz.klotski.Assembly;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.zyz.klotski.GamePage;


public class KlotskiLayout extends RelativeLayout {
    private int[][] chess;

    public KlotskiLayout(Context context) {
        super(context);
    }
    public KlotskiLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(GamePage.LOG_MESSAGE, "=======================Layout_onMeasure");
        int width = KlotskiButton.measureLength(widthMeasureSpec, "layout");
        int height = KlotskiButton.measureLength(heightMeasureSpec, "layout");
        Log.d(GamePage.LOG_MESSAGE, "=======================Layout onMeasure mode " + width + " " + height);
    }

    // 当布局时调用
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            KlotskiButton tempButton = (KlotskiButton) getChildAt(i);
            int unitButtonLength = tempButton.getUnitButtonLength();
            int childLeft = tempButton.getMyLocX() * unitButtonLength + left;
            int childtop = tempButton.getMyLocY() * unitButtonLength + top;
            tempButton.layout(childLeft, childtop, childLeft + tempButton.getMeasuredWidth(), childtop + tempButton.getMeasuredHeight());
        }
        Log.d(GamePage.LOG_MESSAGE, "=======================Layout_onLayout " + changed + " " + left + " " + top + " " + right + " " + bottom);
    }

    public void setChess(KlotskiButton[] buttons, boolean init) {
        int chessLength = GamePage.LENGTH_CHESS_Y, chessWidth = GamePage.LENGTH_CHESS_X;
        if (init) {
            this.chess = new int[chessWidth][];
            for (int i = 0; i < chessWidth; i++) {
                this.chess[i] = new int[chessLength];
                for (int j = 0; j < chessLength; j++) {
                    this.chess[i][j] = 0;
                }
            }
        } else {
            for (int i = 0; i < chessWidth; i++) {
                for (int j = 0; j < chessLength; j++) {
                    this.chess[i][j] = 0;
                }
            }
        }
        KlotskiButton kButton;
        for (int i = 0; i < buttons.length; i++) {
            kButton = buttons[i];
            switch(kButton.getType()){
                case 1:
                    this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                    break;
                case 2:
                    this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                    this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()]=1;
                    break;
                case 3:
                    this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                    this.chess[kButton.getMyLocX()][kButton.getMyLocY()+1]=1;
                    break;
                case 4:
                    this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                    this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()]=1;
                    this.chess[kButton.getMyLocX()][kButton.getMyLocY()+1]=1;
                    this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()+1]=1;
                    break;
            }
        }
    }

    public void setChessOne(KlotskiButton kButton) {
        switch(kButton.getType()){
            case 1:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                break;
            case 2:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()]=1;
                break;
            case 3:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()+1]=1;
                break;
            case 4:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=1;
                this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()]=1;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()+1]=1;
                this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()+1]=1;
                break;
        }
    }

    public void setChessZero(KlotskiButton kButton) {
        switch(kButton.getType()){
            case 1:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=0;
                break;
            case 2:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=0;
                this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()]=0;
                break;
            case 3:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=0;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()+1]=0;
                break;
            case 4:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()]=0;
                this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()]=0;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()+1]=0;
                this.chess[kButton.getMyLocX()+1][kButton.getMyLocY()+1]=0;
                break;
        }
    }

    public boolean judgeEmpty(int x, int y){
        if(x>=GamePage.LENGTH_CHESS_X || y>=GamePage.LENGTH_CHESS_Y ||x<0||y<0)
            return false;
        else{
            return this.chess[x][y]==0;
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev){
//        return true;
//    }
}