package com.example.beeon.qrScan;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import info.androidhive.barcode.camera.CameraSource;

public class ScannerOverLay extends ViewGroup implements BarcodeReader.BarcodeReaderListener {

    private float left, top, endY;
    private int rectWidth, rectHeight;
    private int frames;
    private boolean revAnimation;
    private int lineColor, lineWidth;
    private CameraSource mCameraSource;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private SurfaceView mSurfaceView;
    private GraphicOverlay mOverlay;

    public ScannerOverLay(Context context) {
        super(context);
    }

    public ScannerOverLay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScannerOverLay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                info.androidhive.barcode.R.styleable.ScannerOverlay,
                0, 0);
        rectWidth = a.getInteger(info.androidhive.barcode.R.styleable.ScannerOverlay_square_width, getResources().getInteger(info.androidhive.barcode.R.integer.scanner_rect_width));
        rectHeight = a.getInteger(info.androidhive.barcode.R.styleable.ScannerOverlay_square_height, getResources().getInteger(info.androidhive.barcode.R.integer.scanner_rect_height));
        lineColor = a.getColor(info.androidhive.barcode.R.styleable.ScannerOverlay_line_color, ContextCompat.getColor(context, info.androidhive.barcode.R.color.scanner_line));
        lineWidth = a.getInteger(info.androidhive.barcode.R.styleable.ScannerOverlay_line_width, getResources().getInteger(info.androidhive.barcode.R.integer.line_width));
        frames = a.getInteger(info.androidhive.barcode.R.styleable.ScannerOverlay_line_speed, getResources().getInteger(info.androidhive.barcode.R.integer.line_width));
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = getWidth();
        int height = getHeight();

        if (mCameraSource != null) {
          Size size = mCameraSource.getPreviewSize();
            if (size != null) {
                width = size.getWidth();
                height = size.getHeight();

                    int tmp = width;
                    width = height;
                    height = tmp;

            }
        }

        int layoutWidth = right - left;
        int layoutHeight = bottom - top;


        float ratio =  ((float) Math.max(layoutHeight, layoutWidth))/(float) Math.min(width, height);
        int childWidth = (int) ((float) width * ratio);
        int childHeight = (int) ((float) height * ratio);
        int childLeft = (childWidth>layoutWidth)?(childWidth-layoutWidth)/-2:0;
        int childTop = (childHeight>layoutHeight)?(childHeight-layoutHeight)/-2:0;


        for (int i = 0; i < getChildCount(); ++i) {
            getChildAt(i).layout(childLeft, childTop, childWidth, childHeight);
        }

        try {
            startIfReady();
        } catch (SecurityException se) {
        } catch (IOException e) {
        }
    }

    private void startIfReady() throws IOException, SecurityException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {


                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
                requestLayout();
            }
            mStartRequested = false;
        }
    }

    private boolean isPortraitMode() {
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        left = (w - dpToPx(rectWidth)) / 2;
        top = (h - dpToPx(rectHeight)) / 2;
        endY = top;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cornerRadius = 0;
        Paint eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


        RectF rect = new RectF(left, top, dpToPx(rectWidth) + left, dpToPx(rectHeight) + top);
        canvas.drawRoundRect(rect, (float) cornerRadius, (float) cornerRadius, eraser);


        Paint line = new Paint();
        line.setColor(lineColor);
        line.setStrokeWidth(Float.valueOf(lineWidth));


        if (endY >= top + dpToPx(rectHeight) + frames) {
            revAnimation = true;
        } else if (endY == top + frames) {
            revAnimation = false;
        }


        if (revAnimation) {
            endY -= frames;
        } else {
            endY += frames;
        }
        canvas.drawLine(left, endY, left + dpToPx(rectWidth), endY, line);
        invalidate();
    }

    @Override
    public void onScanned(Barcode barcode) {

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }
}
