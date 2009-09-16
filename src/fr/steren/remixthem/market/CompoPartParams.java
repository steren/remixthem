package fr.steren.remixthem.market;

import android.graphics.PointF;

public class CompoPartParams {

	/**
	 * Center Position relatively to the eyecenter and in eyedistance unit !
	 */
	private PointF mCenterPosition;
	private float mRotation;
	private float mScale;
	
	/** extrema of the scale */
	private float mMinimumScale;
	private float mMaximumScale;	
	
	public CompoPartParams(PointF centerPosition, float rotation, float scale) {
		mCenterPosition = centerPosition;
		mRotation = rotation;
		mScale = scale;
		
		mMinimumScale = 0.7f;
		mMaximumScale = 2.0f;
	}

	public CompoPartParams(PointF centerPosition) {
		this(centerPosition, 0.f, 1.f);
	}
	
	public PointF getCenterPosition() {
		return mCenterPosition;
	}
	public void setCenterPosition(PointF centerPosition) {
		mCenterPosition = centerPosition;
	}
	public float getRotation() {
		return mRotation;
	}
	public void setRotation(float rotation) {
		mRotation = rotation;
	}
	public float getScale() {
		return mScale;
	}
	public void setScale(float scale) {
		if(scale > mMinimumScale && scale < mMaximumScale) {
			mScale = scale;
		}
	}
	
}
