package fr.steren.remixthem;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class FacePart {

	private BitmapDrawable mDrawable;
	
	/** 
	 * the original size of the part, in eyedistance unit
	 */
	private float mWidth;
	private float mHeight; 

	/** We can't move too far the part from the original point */
	private float mMoveRestrictionFactorX;
	private float mMoveRestrictionFactorY;

	
	public FacePart(BitmapDrawable drawable, float width, float length,
			float moveRestrictionFactorX, float moveRestrictionFactorY) {
		super();
		mDrawable = drawable;
		mWidth = width;
		mHeight = length;
		mMoveRestrictionFactorX = moveRestrictionFactorX;
		mMoveRestrictionFactorY = moveRestrictionFactorY;
	}


	public Drawable getDrawable() {
		return mDrawable;
	}
	public float getWidth() {
		return mWidth;
	}
	public float getHeight() {
		return mHeight;
	}
	public float getMoveRestrictionFactorX() {
		return mMoveRestrictionFactorX;
	}
	public float getMoveRestrictionFactorY() {
		return mMoveRestrictionFactorY;
	}
	
}
