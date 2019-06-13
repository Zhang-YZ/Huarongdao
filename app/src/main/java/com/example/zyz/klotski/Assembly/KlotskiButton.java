package com.example.zyz.klotski.Assembly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.example.zyz.klotski.GamePage;
import com.example.zyz.klotski.R;

import java.io.Serializable;

@SuppressLint("AppCompatCustomView")
public class KlotskiButton extends Button {

    public KlotskiButton(Context context) {
        super(context);
    }

    public KlotskiButton(Context context, int[] initArray, int unit) {
        super(context);
        this.init(initArray);
        this.unitButtonLength = unit;
    }

    private int index;
    private int type;
    private int myLocX = 0;//左上各自的位置
    private int myLocY = 0;
    private int myWidth;
    private int myHeight;

    private int unitButtonLength;


    public static int[][] getInitArray(int buttonNum) {
        int[][] allButton = new int[buttonNum][];
        allButton[0] = new int[]{0, 4, 1, 2};
        allButton[1] = new int[]{1, 3, 0, 0};
        allButton[2] = new int[]{2, 3, 3, 0};
        allButton[3] = new int[]{3, 3, 0, 2};
        allButton[4] = new int[]{4, 3, 3, 2};
        allButton[5] = new int[]{5, 2, 1, 1};
        allButton[6] = new int[]{6, 1, 0, 4};
        allButton[7] = new int[]{7, 1, 1, 0};
        allButton[8] = new int[]{8, 1, 2, 0};
        allButton[9] = new int[]{9, 1, 3, 4};
        return allButton;
    }

    /**
     * init 0:index 1:type 2:myX 3:myY
     */
    public void init(int[] initArray) {
        this.index = initArray[0];
        this.type = initArray[1];
        this.myLocX = initArray[2];
        this.myLocY = initArray[3];
        switch (type) {
            case 1:
                this.myWidth = 1;
                this.myHeight = 1;
                break;
            case 2:
                this.myWidth = 2;
                this.myHeight = 1;
                break;
            case 3:
                this.myWidth = 1;
                this.myHeight = 2;
                break;
            case 4:
                this.myWidth = 2;
                this.myHeight = 2;
                break;
        }
    }

    public void resetByArray(int[] tempArray) {
        this.myLocX = tempArray[2];
        this.myLocY = tempArray[3];
        this.setAbsPositionByLoc();
    }

    public void setLocPosition(int x, int y) {
        this.myLocX = x;
        this.myLocY = y;
    }

    public void setAbsPosition(float left, float top) {
        this.setX(left);
        this.setY(top);
    }

    public void setAbsPositionByLoc() {
        this.setX(this.myLocX * unitButtonLength);
        this.setY(this.myLocY * unitButtonLength);
    }

    public int getMyLocX() {
        return myLocX;
    }

    public int getMyLocY() {
        return myLocY;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMyWidth() {
        return myWidth;
    }

    public int getMyHeight() {
        return myHeight;
    }

    public int getUnitButtonLength() {
        return unitButtonLength;
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.myWidth * this.unitButtonLength, this.myHeight * this.unitButtonLength);
    }

    public static int measureLength(int measureSpec, String str) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        // Default size if no limits are specified.
        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

}