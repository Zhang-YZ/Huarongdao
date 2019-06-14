package klotski.klotski.zyz.klotski.Assembly;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;


import java.io.Serializable;

import klotski.klotski.zyz.klotski.GamePage;


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
        int width = KlotskiButton.measureLength(widthMeasureSpec, "layout");
        int height = KlotskiButton.measureLength(heightMeasureSpec, "layout");
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
            tempButton.setX(childLeft);
            tempButton.setY(childtop);
            //tempButton.layout(childLeft, childtop, childLeft + tempButton.getMeasuredWidth(), childtop + tempButton.getMeasuredHeight());
        }
    }


    public void setChess(ChessButtonArray chessButtonArray, boolean init) {
        /**
         * 根据按钮快照设置棋盘数组
         */
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
        int[][] buttons = chessButtonArray.buttonArray;
        for (int i = 0; i < chessButtonArray.buttonNum; i++) {
            switch (buttons[i][1]) {
                case 1:
                    this.chess[buttons[i][2]][buttons[i][3]] = 1;
                    break;
                case 2:
                    this.chess[buttons[i][2]][buttons[i][3]] = 1;
                    this.chess[buttons[i][2] + 1][buttons[i][3]] = 1;
                    break;
                case 3:
                    this.chess[buttons[i][2]][buttons[i][3]] = 1;
                    this.chess[buttons[i][2]][buttons[i][3] + 1] = 1;
                    break;
                case 4:
                    this.chess[buttons[i][2]][buttons[i][3]] = 1;
                    this.chess[buttons[i][2] + 1][buttons[i][3]] = 1;
                    this.chess[buttons[i][2]][buttons[i][3] + 1] = 1;
                    this.chess[buttons[i][2] + 1][buttons[i][3] + 1] = 1;
                    break;
            }
        }

//        for(int i=0;i<4;i++){
//            for(int j=0;j<5;j++){
//                Log.d("testttttttt","ttttttttttttttttttttt"+String.valueOf(j));
//
//                Log.d("testttttttt","tttttttttttttt"+String.valueOf(this.chess[i][j]));
//            }
//        }
    }

    public void setChessOne(KlotskiButton kButton) {
        /**
         * 某个button放置
         */
        switch (kButton.getType()) {
            case 1:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 1;
                break;
            case 2:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 1;
                this.chess[kButton.getMyLocX() + 1][kButton.getMyLocY()] = 1;
                break;
            case 3:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 1;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY() + 1] = 1;
                break;
            case 4:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 1;
                this.chess[kButton.getMyLocX() + 1][kButton.getMyLocY()] = 1;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY() + 1] = 1;
                this.chess[kButton.getMyLocX() + 1][kButton.getMyLocY() + 1] = 1;
                break;
        }
    }

    public void setChessZero(KlotskiButton kButton) {
        /**
         * 某个button移动
         */
        switch (kButton.getType()) {
            case 1:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 0;
                break;
            case 2:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 0;
                this.chess[kButton.getMyLocX() + 1][kButton.getMyLocY()] = 0;
                break;
            case 3:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 0;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY() + 1] = 0;
                break;
            case 4:
                this.chess[kButton.getMyLocX()][kButton.getMyLocY()] = 0;
                this.chess[kButton.getMyLocX() + 1][kButton.getMyLocY()] = 0;
                this.chess[kButton.getMyLocX()][kButton.getMyLocY() + 1] = 0;
                this.chess[kButton.getMyLocX() + 1][kButton.getMyLocY() + 1] = 0;
                break;
        }
    }

    public boolean judgeEmpty(int x, int y) {
        if (x >= GamePage.LENGTH_CHESS_X || y >= GamePage.LENGTH_CHESS_Y || x < 0 || y < 0)
            return false;
        else {
            return this.chess[x][y] == 0;
        }
    }


    public static class ChessButtonArray implements Serializable {
        private int[][] buttonArray;
        private int buttonNum;

        public ChessButtonArray(int buttonNum, int[][] tempChess) {
            this.buttonNum = buttonNum;
            this.buttonArray = new int[buttonNum][];
            for (int i = 0; i < buttonNum; i++) {
                this.buttonArray[i] = new int[4];
            }
            this.init(tempChess);
        }

        public void init(int[][] tempChess) {
            try {
                for (int i = 0; i < this.buttonNum; i++) {
                    for (int j = 0; j < 4; j++) {
                        this.buttonArray[i][j] = tempChess[i][j];
                    }
                }
            } catch (Exception e) {
                Log.d("CHESS", String.valueOf(e));
            }
        }

        public int getButtonNum() {
            return buttonNum;
        }

        public int[][] getButtonArray() {
            return buttonArray;
        }

        public int getDiffIndex(ChessButtonArray another) {
            /**
             * 对比按钮快照
             */
            for (int i = 0; i < buttonNum; i++) {
                if (this.buttonArray[i][2] != another.buttonArray[i][2] || this.buttonArray[i][3] != another.buttonArray[i][3]) {
                    return i;
                }
            }
            return -1;
        }

        public void setOneButton(int index, int[] newButton) {
            /**
             * 重置某个按钮
             */
            for (int i = 0; i < 4; i++) {
                this.buttonArray[index][i] = newButton[i];
            }
        }
    }
}