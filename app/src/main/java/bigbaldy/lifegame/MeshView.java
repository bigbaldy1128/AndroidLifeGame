package bigbaldy.lifegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wangjinzhao on 2016/8/3.
 */
public class MeshView extends View {
    private boolean[][] struArr;
    private boolean[][] struArr2;
    private int width;
    private int height;
    private final int interval = 10;
    private final int area = 20;
    private int oldx, oldy;
    private boolean canRun = false;
    private int backgroundColor;
    Paint pBlock = new Paint();
    Context context;

    private void touch(MotionEvent event) {
        int xPos, yPos;
        int x = (int) event.getX();
        int y = (int) event.getY();
        xPos = x / area;
        yPos = y / area;
        if (xPos == oldx && yPos == oldy)
            return;
        if (xPos > width - 1 || xPos < 0 || yPos > height - 1 || yPos < 0)
            return;
        oldx = xPos;
        oldy = yPos;
        struArr[xPos][yPos] = !struArr[xPos][yPos];
        struArr2[xPos][yPos] = struArr[xPos][yPos];
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touch(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            touch(event);
        }

        return true;
    }

    public MeshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        width = (right - left) / area;
        height = (bottom - top) / area;
        backgroundColor = ContextCompat.getColor(this.context, R.color.colorSilver);
        pBlock.setStyle(Paint.Style.FILL);
        struArr = new boolean[width][height];
        struArr2 = new boolean[width][height];
        Init();
        super.onLayout(changed, left, top, right, bottom);
    }

    private void Init() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                struArr2[i][j] = false;
            }
        }
    }

    public void stop() {
        canRun = false;
    }

    public void start() {
        canRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (canRun) {
                    for (int i = 0; i < width; i++)
                        for (int j = 0; j < height; j++)
                            struArr2[i][j] = struArr[i][j];

                    for (int i = 0; i < width; i++)
                        for (int j = 0; j < height; j++) {
                            int count = 0;
                            if (struArr[i - 1 == -1 ? width - 1 : i - 1][j - 1 == -1 ? height - 1 : j - 1]) {
                                count++;
                            }
                            if (struArr[i - 1 == -1 ? width - 1 : i - 1][j]) {
                                count++;
                            }
                            if (struArr[i - 1 == -1 ? width - 1 : i - 1][j + 1 == height ? 0 : j + 1]) {
                                count++;
                            }
                            if (struArr[i][j - 1 == -1 ? height - 1 : j - 1]) {
                                count++;
                            }
                            if (struArr[i][j + 1 == height ? 0 : j + 1]) {
                                count++;
                            }
                            if (struArr[i + 1 == width ? 0 : i + 1][j - 1 == -1 ? height - 1 : j - 1]) {
                                count++;
                            }
                            if (struArr[i + 1 == width ? 0 : i + 1][j]) {
                                count++;
                            }
                            if (struArr[i + 1 == width ? 0 : i + 1][j + 1 == height ? 0 : j + 1]) {
                                count++;
                            }
                            switch (count) {
                                case 3:
                                    struArr2[i][j] = true;
                                    break;
                                case 2:
                                    if (struArr[i][j]) {
                                        struArr2[i][j] = true;
                                    }
                                    break;
                                default:
                                    struArr2[i][j] = false;
                                    break;
                            }
                        }
                    postInvalidate();
                    for (int i = 0; i < width; i++)
                        for (int j = 0; j < height; j++)
                            struArr[i][j] = struArr2[i][j];
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(backgroundColor);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pBlock.setColor(struArr2[i][j] ? Color.BLACK : Color.WHITE);
                canvas.drawRect(area * i + 1, area * j + 1, area * (i + 1) - 1, area * (j + 1) - 1, pBlock);
            }
        }
        super.onDraw(canvas);
    }


}
