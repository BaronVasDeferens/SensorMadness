package skot.sensormadness;

import android.content.Context;
import android.graphics.Bitmap;
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

    Rect drawMe, myRect;
    Canvas renderedCanvas = null;
    Bitmap renderedBitmap = null;
    Paint paint;
    private byte[] data = null;
    private int markerPosition = 0;
    private final int markerWidth = 2;

    public DataGraphView(Context context) {
        super(context);
        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
        this.setDrawingCacheEnabled(true);
    }

    public DataGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
        this.setDrawingCacheEnabled(true);
    }

    public DataGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        drawMe = new Rect();
        myRect = new Rect();
        this.setDrawingCacheEnabled(true);
    }

    public void setData(byte[] data) {
        System.out.println(">>> DataGraphView.setData() NULL = " + (data == null));
        this.data = data;
        renderedBitmap = null;
        renderedCanvas = null;
    }

    public void setMarkerPosition(final int markerPosition) {

        if (data == null)
            return;

        if (markerPosition >= 0 && markerPosition < data.length) {
            this.markerPosition = (int)((markerPosition / 100f) *  myRect.width());
        } else if (markerPosition == 100) {
            this.markerPosition = myRect.width()-(markerWidth * 2);
        }

    }


    private Bitmap getBitmap() {
        return this.getDrawingCache();
    }

    private void renderData(final Canvas canvas) {

        Bitmap tmpBitmap = getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Canvas tempCanvas = new Canvas(tmpBitmap);

        getDrawingRect(myRect);

        if (renderedBitmap == null) {

            tempCanvas.drawColor(Color.WHITE);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            int interval = data.length / myRect.width();

            for (int i = 0; i < myRect.width(); i++) {
                int value = 128 - Math.abs((int) data[(i * interval)]);
                int valueHeight = myRect.bottom - value;
                drawMe.set(i, valueHeight, i + 1, myRect.bottom);
                tempCanvas.drawRect(drawMe, paint);
            }

            renderedBitmap = tmpBitmap;
        }

        canvas.drawBitmap(renderedBitmap, null, myRect, null);

        paint.setColor(Color.BLACK);
        drawMe.set(markerPosition, 0, markerPosition + markerWidth, myRect.bottom);
        canvas.drawRect(drawMe, paint);

    }

    @Override
    public void onDraw(Canvas canvas) {

        if (data == null)
            canvas.drawColor(Color.DKGRAY);

        else {
            renderData(canvas);
        }


    }
}
