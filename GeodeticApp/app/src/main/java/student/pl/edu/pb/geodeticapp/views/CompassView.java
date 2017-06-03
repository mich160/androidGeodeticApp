package student.pl.edu.pb.geodeticapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import student.pl.edu.pb.geodeticapp.R;

public class CompassView extends View {
    private Context context;

    private Paint needleNPaint;
    private Paint needleEWPaint;
    private Paint needleSPaint;
    private int compassBackgroundColor;
    private boolean transparent;

    private double rotationAngle;
    private double needleAxisX;
    private double needleAxisY;
    private double needleLength;
    private double needleWidth;

    private double[] needlePointsBuffer;
    private Path needlePath;
    private Matrix pathTransformMatrix;

    public CompassView(Context context) {
        super(context);
        init(context);
    }

    public CompassView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CompassView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawNeedle(canvas, 0.0 + rotationAngle, needleNPaint);
        drawNeedle(canvas, 90 + rotationAngle, needleEWPaint);
        drawNeedle(canvas, 180 + rotationAngle, needleSPaint);
        drawNeedle(canvas, 270 + rotationAngle, needleEWPaint);
        drawDetails(canvas);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        needleAxisX = w / 2.0;
        needleAxisY = 0.9 * h;
        needleLength = h * 0.8;
        needleWidth = needleLength / 3.0;
    }

    public void update(double rotationAngle) {
        this.rotationAngle = rotationAngle;
        invalidate();
    }

    private void init(Context context) {
        this.context = context;
        transparent = false;
        needleNPaint = new Paint();
        needleEWPaint = new Paint();
        needleSPaint = new Paint();
        needleNPaint.setColor(ContextCompat.getColor(context, R.color.compassNeedleN));
        needleNPaint.setStyle(Paint.Style.FILL);
        needleEWPaint.setColor(ContextCompat.getColor(context, R.color.compassNeedleEW));
        needleEWPaint.setStyle(Paint.Style.FILL);
        needleSPaint.setColor(ContextCompat.getColor(context, R.color.compassNeedleS));
        needleSPaint.setStyle(Paint.Style.FILL);
        compassBackgroundColor = ContextCompat.getColor(context, R.color.compassBackground);
        needlePointsBuffer = new double[6];
        rotationAngle = 0.0;
        needlePath = new Path();
        pathTransformMatrix = new Matrix();
    }

    private void drawNeedle(Canvas canvas, double rotationOffset, Paint paint) {
        needlePointsBuffer[0] = needleAxisX;
        needlePointsBuffer[1] = needleAxisY - needleLength;
        needlePointsBuffer[2] = needleAxisX - needleWidth / 2.0;
        needlePointsBuffer[3] = needleAxisY;
        needlePointsBuffer[4] = needleAxisX + needleWidth / 2.0;
        needlePointsBuffer[5] = needleAxisY;
        needlePath.moveTo((float) needlePointsBuffer[0], (float) needlePointsBuffer[1]);
        needlePath.lineTo((float) needlePointsBuffer[2], (float) needlePointsBuffer[3]);
        needlePath.lineTo((float) needlePointsBuffer[4], (float) needlePointsBuffer[5]);
        needlePath.lineTo((float) needlePointsBuffer[0], (float) needlePointsBuffer[1]);
        needlePath.close();
        pathTransformMatrix.setRotate((float) (-rotationOffset), (float) needleAxisX, (float) needleAxisY);
        needlePath.transform(pathTransformMatrix);
        canvas.drawPath(needlePath, paint);
        needlePath.reset();
        pathTransformMatrix.reset();
    }

    private void drawBackground(Canvas canvas) {
        if (transparent) {
            canvas.drawColor(Color.TRANSPARENT);
        } else {
            canvas.drawColor(compassBackgroundColor);
        }

    }

    private void drawDetails(Canvas canvas) {
        canvas.drawCircle((float) needleAxisX, (float) needleAxisY, (float) (needleWidth * 0.6), needleEWPaint);
    }

    public int getNeedleNColor() {
        return needleNPaint.getColor();
    }

    public void setNeedleNColor(int needleNColor) {
        this.needleNPaint.setColor(needleNColor);
    }

    public Paint getNeedleEWPaint() {
        return needleEWPaint;
    }

    public void setNeedleEWPaint(Paint needleEWPaint) {
        this.needleEWPaint = needleEWPaint;
    }

    public Paint getNeedleSPaint() {
        return needleSPaint;
    }

    public void setNeedleSPaint(Paint needleSPaint) {
        this.needleSPaint = needleSPaint;
    }

    public int getCompassBackgroundColor() {
        return compassBackgroundColor;
    }

    public void setCompassBackgroundColor(int compassBackgroundColor) {
        this.compassBackgroundColor = compassBackgroundColor;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }
}
