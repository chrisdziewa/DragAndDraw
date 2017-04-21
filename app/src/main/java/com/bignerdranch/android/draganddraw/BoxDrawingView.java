package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Chris on 4/20/2017.
 * Allows user to drag finger to create a new box
 */

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private ArrayList<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;
    private PointF mCenterOfRotation;

    // Tags for saving state
    private static final String SAVED_BOXEN = "SavedBoxen";
    private static final String SAVED_VIEW_STATE = "SavedViewState";

    // Used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    // Used when inflating the view from XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    // Saving on orientation changes
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable viewState = super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_VIEW_STATE, viewState);
        bundle.putParcelableArrayList(SAVED_BOXEN, mBoxen);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // https://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes/3542895#3542895
        // Thanks to user Kobor42  for explaining that parcelable can be converted to a bundle for ease of access
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            super.onRestoreInstanceState(bundle.getParcelable(SAVED_VIEW_STATE));
            mBoxen = bundle.getParcelableArrayList(SAVED_BOXEN);
            return;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Fill the background
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            if (box.getBoxAngle() != 0) {
                canvas.save();
                canvas.rotate(box.getBoxAngle(), box.getCenterOfRotation().x, box.getCenterOfRotation().y);
                canvas.drawRect(left, top, right, bottom, mBoxPaint);
                canvas.restore();
            } else {
                canvas.drawRect(left, top, right, bottom, mBoxPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());

        String action = "";

        Log.i(TAG, "pointer count = " + event.getPointerCount());

        // TODO: 4/21/2017 build rotation functionality
        /* 1. get pointer count
           2. if pointer count is equal to 2
              track movement for 2nd pointer (index 1)
           3. rotate points based on angle from start
           4. invalidate to draw again
           5. rotate canvas?
        */
        int pointerIndex = event.getActionIndex();
        Log.i(TAG, "onTouchEvent: pointerIndex = " + pointerIndex);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // Reset drawing state
                if (pointerIndex == 0) {
                    mCurrentBox = new Box(current);
                    mBoxen.add(mCurrentBox);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";

                if (mCurrentBox != null) {
                    if (event.getPointerCount() == 1) {
                        mCurrentBox.setCurrent(current);
                    } else if (event.getPointerCount() == 2) {
                        // find angle of rotation and reset canvas
                        PointF movePoint = new PointF(event.getX(1), event.getY(1));
                        mCurrentBox.setBoxAngle(calculateAngle(movePoint, mCurrentBox.getCurrent()));
                        Log.i(TAG, "rotation: " + mCurrentBox.getBoxAngle());

                        PointF origin = mCurrentBox.getOrigin();
                        float centerX = (current.x + origin.x) / 2;
                        float centerY = (current.y + origin.y) / 2;
                        mCurrentBox.setCenterOfRotation(new PointF(centerX, centerY));
                    } else {
                        return false;
                    }
                    invalidate();
                    break;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;

        }
        Log.i(TAG, action + " at x = " + current.x + ", y = " + current.y);
        return true;
    }

    private float calculateAngle(PointF movePoint, PointF origin) {
        Log.i(TAG, "calculateAngle: movePoint = " + movePoint.toString() + " , and origin = " + origin.toString());

        // The following image rotation code is from here:
        // https://judepereira.com/blog/multi-touch-in-android-translate-scale-and-rotate/
        double delta_x = (origin.x - movePoint.x);
        double delta_y = (origin.y - movePoint.y);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}
