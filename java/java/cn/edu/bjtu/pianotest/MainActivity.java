package cn.edu.bjtu.pianotest;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    private Button button[];        // 按钮数组
    private PianoMusic utils;       // 工具类
    private View parent;            // 父视图
    private int buttonId[];         // 按钮id
    private boolean havePlayed[];   // 是否已经播放了声音，当手指在同一个按钮内滑动，且已经发声，就为true
/*    private View ParentKeys;        // 全部按钮的视图
    private View UpKeys;            // 上排白色按钮所在的视图
    private View DownKeys;          // 下排白色按钮所在的视图*/
    private int pressedkey[];       // 已按下的按键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        parent = (View) findViewById(R.id.ParentKeys);
        parent.setClickable(true);

        parent.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int temp;           // 暂存所点击的按钮对象
                int tempIndex;
                int pointercount;   // 同时按下的数目

                pointercount = event.getPointerCount();

                for (int count = 0; count < pointercount; count++) {
                    boolean moveflag = false;   // moveflag - 是否移动的标志
                    temp = isInAnyScale(event.getX(count), event.getY(count), button);
                    if (temp != -1) {
                        switch (event.getActionMasked()) {

                            case MotionEvent.ACTION_DOWN:   // 与pointerdown合并即可
                            case MotionEvent.ACTION_POINTER_DOWN:
                                Log.i("--", "count" + count); // 方便调试（输出为绿色）
                                pressedkey[count] = temp;
                                if (!havePlayed[temp]) {// 播放音乐；同时改变按键为已按下
                                    button[temp].setBackgroundResource(R.drawable.button_pressed);
                                    // 播放音阶
                                    utils.soundPlay(temp);
                                    Log.i("--", "sound" + temp);// 方便调试（输出为绿色）
                                    havePlayed[temp] = true;// 设置为已播放
                                }
                                break;

                            case MotionEvent.ACTION_MOVE:
                                temp = pressedkey[count];
                                for (int i = temp + 1; i >= temp - 1; i--) {
                                    // 当在两端的按钮时，会有一边越界
                                    if (i < 0 || i >= button.length) {
                                        continue;
                                    }
                                    if (isInScale(event.getX(count), event.getY(count), button[i])) {// 在某个按键内
                                        moveflag = true;
                                        if (i != temp) {// 在相邻按键内
                                            boolean laststill = false;
                                            boolean nextstill = false;
                                            // 假设手指已经从上一个位置抬起，但是没有真的抬起，所以不移位
                                            pressedkey[count] = -1;
                                            for (int j = 0; j < pointercount; j++) {
                                                if (pressedkey[j] == temp) {
                                                    laststill = true;
                                                }
                                                if (pressedkey[j] == i) {
                                                    nextstill = true;
                                                }
                                            }

                                            if (!nextstill) {// 移入的按键没有按下
                                                // 设置当前按键
                                                button[i].setBackgroundResource(R.drawable.button_pressed);
                                                // 发音
                                                utils.soundPlay(i);
                                                havePlayed[i] = true;
                                            }

                                            pressedkey[count] = i;

                                            if (!laststill) {// 没有手指按在上面
                                                // 设置上一个按键
                                                if(temp >= 28)
                                                    button[temp].setBackgroundResource(R.drawable.blackbutton);
                                                else
                                                    button[temp].setBackgroundResource(R.drawable.button);
                                                havePlayed[temp] = false;
                                            }

                                            break;
                                        }
                                    }
                                }
                                break;

                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_POINTER_UP:
                                // 事件与点对应
                                tempIndex = event.getActionIndex();
                                if (tempIndex == count) {
                                    Log.i("--", "index" + tempIndex);
                                    boolean still = false;
                                    // 当前点已抬起
                                    for (int t = count; t < 5; t++) {
                                        if (t != 4) {
                                            if (pressedkey[t + 1] >= 0) {
                                                pressedkey[t] = pressedkey[t + 1];
                                            } else {
                                                pressedkey[t] = -1;
                                            }
                                        } else {
                                            pressedkey[t] = -1;
                                        }

                                    }
                                    for (int i = 0; i < pressedkey.length; i++) {// 是否还有其他点
                                        if (pressedkey[i] == temp) {
                                            still = true;
                                            break;
                                        }
                                    }
                                    if (!still) {// 已经没有手指按在该键上
                                        if(temp >= 28)
                                            button[temp].setBackgroundResource(R.drawable.blackbutton);
                                        else
                                            button[temp].setBackgroundResource(R.drawable.button);
                                        havePlayed[temp] = false;
                                        Log.i("--", "button" + temp + "up");
                                    }
                                    break;
                                }
                        }
                    }
                    //
                    if (event.getActionMasked() == MotionEvent.ACTION_MOVE && !moveflag) {
                        if (pressedkey[count] != -1) {
                            if(pressedkey[count] >= 28)
                                button[pressedkey[count]].setBackgroundResource(R.drawable.blackbutton);
                            else
                                button[pressedkey[count]].setBackgroundResource(R.drawable.button);
                            havePlayed[pressedkey[count]] = false;
                        }
                    }
                }
                return false;
            }
        });

/*        ParentKeys = (View) findViewById(R.id.ParentKeys);
        UpKeys = (View) findViewById(R.id.UpKeys);
        DownKeys = (View) findViewById(R.id.DownKeys);*/
    }

    private void init() {
        // 新建工具类
        utils = new PianoMusic(getApplicationContext());

        // 按钮资源Id
        buttonId = new int[48];
        buttonId[0] = R.id.PianoC;
        buttonId[1] = R.id.PianoD;
        buttonId[2] = R.id.PianoE;
        buttonId[3] = R.id.PianoF;
        buttonId[4] = R.id.PianoG;
        buttonId[5] = R.id.PianoA;
        buttonId[6] = R.id.PianoB;

        buttonId[7] = R.id.Pianoc;
        buttonId[8] = R.id.Pianod;
        buttonId[9] = R.id.Pianoe;
        buttonId[10] = R.id.Pianof;
        buttonId[11] = R.id.Pianog;
        buttonId[12] = R.id.Pianoa;
        buttonId[13] = R.id.Pianob;

        buttonId[14] = R.id.Pianoc1;
        buttonId[15] = R.id.Pianod1;
        buttonId[16] = R.id.Pianoe1;
        buttonId[17] = R.id.Pianof1;
        buttonId[18] = R.id.Pianog1;
        buttonId[19] = R.id.Pianoa1;
        buttonId[20] = R.id.Pianob1;

        buttonId[21] = R.id.Pianoc2;
        buttonId[22] = R.id.Pianod2;
        buttonId[23] = R.id.Pianoe2;
        buttonId[24] = R.id.Pianof2;
        buttonId[25] = R.id.Pianog2;
        buttonId[26] = R.id.Pianoa2;
        buttonId[27] = R.id.Pianob2;

        buttonId[28] = R.id.PianoCm;
        buttonId[29] = R.id.PianoDm;
        buttonId[30] = R.id.PianoFm;
        buttonId[31] = R.id.PianoGm;
        buttonId[32] = R.id.PianoAm;

        buttonId[33] = R.id.Pianocm;
        buttonId[34] = R.id.Pianodm;
        buttonId[35] = R.id.Pianofm;
        buttonId[36] = R.id.Pianogm;
        buttonId[37] = R.id.Pianoam;

        buttonId[38] = R.id.Pianoc1m;
        buttonId[39] = R.id.Pianod1m;
        buttonId[40] = R.id.Pianof1m;
        buttonId[41] = R.id.Pianog1m;
        buttonId[42] = R.id.Pianoa1m;

        buttonId[43] = R.id.Pianoc2m;
        buttonId[44] = R.id.Pianod2m;
        buttonId[45] = R.id.Pianof2m;
        buttonId[46] = R.id.Pianog2m;
        buttonId[47] = R.id.Pianoa2m;

        button = new Button[48];
        havePlayed = new boolean[48];

        // 获取按钮对象
        for (int i = 0; i < button.length; i++) {
            button[i] = (Button) findViewById(buttonId[i]);
            button[i].setClickable(false);
            havePlayed[i] = false;
        }

        pressedkey = new int[5];
        for (int j = 0; j < pressedkey.length; j++) {
            pressedkey[j] = -1;
        }

    }

    //判断某个点是否在某个按钮的范围内
    private boolean isInScale(float x, float y, Button button) {
        // tempParent.getTop()是获取按钮所在父视图相对其父视图的右上角纵坐标
        View tempParent;
        tempParent = (View)button.getParent();
        if (x > button.getLeft()
                && x < button.getRight()
                && y > button.getTop() + tempParent.getTop()
                && y < button.getBottom() + tempParent.getTop()
            ) {
            return true;
        } else {
            return false;
        }
    }

    //判断某个点是否在一个按钮集合中的某个按钮内
    private int isInAnyScale(float x, float y, Button[] button) {
        // tempParent.getTop()是获取按钮所在父视图相对其父视图的右上角纵坐标
        View tempParent;
        for (int i = button.length - 1; i >= 0; i--) {
            tempParent = (View)button[i].getParent();
            if (x > button[i].getLeft()
                    && x < button[i].getRight()
                    && y > button[i].getTop() + tempParent.getTop()
                    && y < button[i].getBottom() + tempParent.getTop()
                ){
                return i;
            }
        }
        return -1;
    }
}
