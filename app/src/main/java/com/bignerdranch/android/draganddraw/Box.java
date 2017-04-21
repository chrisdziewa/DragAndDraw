package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris on 4/20/2017.
 */

public class Box implements Parcelable {

    private PointF mOrigin;
    private PointF mCurrent;

    private float mBoxAngle;
    private PointF mCenterOfRotation;


    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

    protected Box(Parcel in) {
        mOrigin = (PointF) in.readValue(PointF.class.getClassLoader());
        mCurrent = (PointF) in.readValue(PointF.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mOrigin);
        dest.writeValue(mCurrent);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Box> CREATOR = new Parcelable.Creator<Box>() {
        @Override
        public Box createFromParcel(Parcel in) {
            return new Box(in);
        }

        @Override
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };

    public float getBoxAngle() {
        return mBoxAngle;
    }

    public void setBoxAngle(float boxAngle) {
        mBoxAngle = boxAngle;
    }

    public PointF getCenterOfRotation() {
        return mCenterOfRotation;
    }

    public void setCenterOfRotation(PointF centerOfRotation) {
        mCenterOfRotation = centerOfRotation;
    }
}
