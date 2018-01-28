package skot.sensormadness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by skot on 1/7/18.
 */

public class DataGraphView extends View {


    private float barWidth = 5.0f;       // width of each segment
    Rect drawMe, myRect;
    Paint paint;
    private byte [] data = null;

    public DataGraphView(Context context) {
        super(context);
        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
    }

    public DataGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
    }

    public DataGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
    }

    public void setData(byte [] data) {
        System.out.println(">>> DataGraphView.setData() NULL = " + (data == null));
        this.data = data;
    }

    @Override
    public void onDraw(Canvas canvas) {
        getDrawingRect(myRect);

        if (data == null)
            canvas.drawColor(Color.DKGRAY);

        else {
            canvas.drawColor(Color.WHITE);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            int interval = data.length / myRect.width();
            System.out.println(">>> DataGraphView interval = " + interval);


            for (int i = 0; i < myRect.width(); i++) {
                int value = 128 - Math.abs((int) data[(i * interval)]);
                System.out.println("value = " + value);
//                drawMe.set(i, myRect.centerY() - value, i+1, myRect.centerY());
//                drawMe.set(rando.nextInt(value), rando.nextInt(value), rando.nextInt(value), rando.nextInt(value));
                int valueHeight = myRect.bottom - value;
                drawMe.set(i, valueHeight, i+1, myRect.bottom);

                canvas.drawRect(drawMe, paint);
            }


        }


    }
}
