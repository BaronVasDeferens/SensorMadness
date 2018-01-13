package skot.sensormadness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;


/**
 * Created by skot on 1/7/18.
 */

public class GraphView extends View {

    ArrayList<Float> values;
    private float barWidth = 5.0f;       // width of each segment
    Rect drawMe, myRect;
    Paint paint;

    public GraphView(Context context) {
        super(context);
        values = new ArrayList<>();
        paint = new Paint();
        myRect = new Rect();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        values = new ArrayList<>();
        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        values = new ArrayList<>();
        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
    }

    public void addPoint(float data) {
        values.add(data);
    }

    @Override
    public void onDraw(Canvas canvas) {

        getDrawingRect(myRect);

        // Reset the display once we reach the end
        if (values.size() * barWidth > myRect.width()) {
            values.clear();
        }

        canvas.drawColor(Color.DKGRAY);

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        float offset = 0.0f;

        for (float i : values) {
            drawMe.set((int) offset, (int) myRect.height() / 2, (int) (offset + barWidth), (int) (myRect.height() / 2 - i));
            canvas.drawRect(drawMe, paint);
            offset += barWidth;
        }
    }
}
