package fr.steren.remixthem;

import android.graphics.PointF;

public class CompoPartParams {

	/**
	 * Center Position relatively to the eyecenter and in eyedistance unit !
	 */
	private PointF mCenterPosition;
	private float mRotation;
	private float mScale;
	
	public CompoPartParams(PointF centerPosition, float rotation, float scale) {
		mCenterPosition = centerPosition;
		mRotation = rotation;
		mScale = scale;
	}

	public CompoPartParams(PointF centerPosition) {
		mCenterPosition = centerPosition;
		mRotation = 0.f;
		mScale = 1.f;
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
		mScale = scale;
	}
	
	
}
