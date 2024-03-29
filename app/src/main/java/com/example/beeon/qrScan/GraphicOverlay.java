package com.example.beeon.qrScan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import info.androidhive.barcode.camera.CameraSource;

public  class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {
private final Object mLock = new Object();
private int mPreviewWidth;
private float mWidthScaleFactor = 1.0f;
private int mPreviewHeight;
private float mHeightScaleFactor = 1.0f;
private int mFacing = CameraSource.CAMERA_FACING_BACK;
private Set<T> mGraphics = new HashSet<>();



public static abstract class Graphic {
    private GraphicOverlay mOverlay;

    Graphic(GraphicOverlay overlay) {
        mOverlay = overlay;
    }



    public abstract void draw(Canvas canvas);



    float scaleX(float horizontal) {
        return horizontal * mOverlay.mWidthScaleFactor;
    }



    float scaleY(float vertical) {
        return vertical * mOverlay.mHeightScaleFactor;
    }



    float translateX(float x) {
        if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
            return mOverlay.getWidth() - scaleX(x);
        } else {
            return scaleX(x);
        }
    }



    float translateY(float y) {
        return scaleY(y);
    }

    void postInvalidate() {
        mOverlay.postInvalidate();
    }
}

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }


    public void add(T graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }


    public void remove(T graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }



    public List<T> getGraphics() {
        synchronized (mLock) {
            return new Vector(mGraphics);
        }
    }



    public float getWidthScaleFactor() {
        return mWidthScaleFactor;
    }



    public float getHeightScaleFactor() {
        return mHeightScaleFactor;
    }



    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (GraphicOverlay.Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        }
    }
}
