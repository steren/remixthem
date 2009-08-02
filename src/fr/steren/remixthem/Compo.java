package fr.steren.remixthem;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import java.lang.Math;

/**
 * A compo is the association of ONE background image and a various number of parts.
 */
public class Compo {
	public enum EditAction {NOTHING, MOVE, SCALE, ROTATE, ROTSCALE};

	/** Array of compoparts. The first one is eyes, second is nose, third is mouth */
    private ArrayList<CompoPart> mCompoParts;
    private BackgroundFace mBackgroundFace;
    
	public Compo(BackgroundFace backgroundFace) {
		super();
		mBackgroundFace = backgroundFace;
		
		mCompoParts = new ArrayList<CompoPart>();		
		CompoPart eyesCompoPart = new CompoPart(mBackgroundFace.getFaceParts().get(0), new CompoPartParams(new PointF(0.f , -0.1f)) );		
		mCompoParts.add(eyesCompoPart);
		CompoPart noseCompoPart = new CompoPart(mBackgroundFace.getFaceParts().get(1), new CompoPartParams(new PointF(0.f, 0.4f)) );		
		mCompoParts.add(noseCompoPart);
		CompoPart mouthCompoPart = new CompoPart(mBackgroundFace.getFaceParts().get(2), new CompoPartParams(new PointF(0.f , 1.1f)) );
		mCompoParts.add(mouthCompoPart);
	}

	public ArrayList<CompoPart> getCompoParts() {
		return mCompoParts;
	}
	public void addCompoPart(CompoPart compoPart){
		mCompoParts.add(compoPart);
	}
	
	public void setBackgroundFace(BackgroundFace backgroundFace) {
		mBackgroundFace = backgroundFace;
	}
	public BackgroundFace getBackgroundFace() {
		return mBackgroundFace;
	}
	
	/**
	 * Return the center position, relatively to the image dimension (in [0,1]²)
	 */
	public PointF computeCenterPosition(CompoPart compoPart) {
		PointF centerPosition = new PointF();
		float eyesDetectedDistance = getBackgroundFace().getEyesDetectedDistance();
		centerPosition.x = (getBackgroundFace().getEyesDetectedPosition().x + eyesDetectedDistance * compoPart.getParams().getCenterPosition().x) / getBackgroundFace().getDrawable().getIntrinsicWidth()  ;
		centerPosition.y = (getBackgroundFace().getEyesDetectedPosition().y + eyesDetectedDistance * compoPart.getParams().getCenterPosition().y) / getBackgroundFace().getDrawable().getIntrinsicHeight() ;
		return centerPosition;
	}
	
	/**
	 *  Set the center position of the compoPart
	 *  @param	point : coordinates of the new center relatively to the image (in [0,1]²)
	 */
	public void setCenterPosition(CompoPart compoPart, PointF point) {
		PointF centerRelativePosition = new PointF();
		float eyesDetectedDistance = getBackgroundFace().getEyesDetectedDistance();
		centerRelativePosition.x = ((point.x * getBackgroundFace().getDrawable().getIntrinsicWidth()	) - getBackgroundFace().getEyesDetectedPosition().x	)/eyesDetectedDistance ;
		centerRelativePosition.y = ((point.y * getBackgroundFace().getDrawable().getIntrinsicHeight()	) - getBackgroundFace().getEyesDetectedPosition().y	)/eyesDetectedDistance ;
		compoPart.getParams().setCenterPosition(centerRelativePosition);
	}
	
	/** 
	 * Return the CompoPart Bounds, relatively to the image dimension (in [0,1])
	 */
	public RectF computeBounds(CompoPart compoPart) {
		PointF centerPosition = computeCenterPosition(compoPart);
		float partWidth = computeWidth(compoPart);
		float partHeight = computeHeight(compoPart);
		RectF bounds = new RectF(centerPosition.x - partWidth/2, centerPosition.y - partHeight/2, centerPosition.x + partWidth/2, centerPosition.y + partHeight/2);
		return bounds;
	}
	
	/**
	 * Return the CompoPart Width, relatively to the image width (in [0,1])
	 */
	public float computeWidth(CompoPart compoPart) {
		float eyesDetectedDistance = getBackgroundFace().getEyesDetectedDistance();
		float partWidth = eyesDetectedDistance * compoPart.getFacePart().getWidth() * compoPart.getParams().getScale() / getBackgroundFace().getDrawable().getIntrinsicWidth()  ;
		return partWidth;
	}

	/**
	 * Return the CompoPart Width, relatively to the image width (in [0,1])
	 */
	public float computeHeight(CompoPart compoPart) {
		float eyesDetectedDistance = getBackgroundFace().getEyesDetectedDistance();
		float partHeight = eyesDetectedDistance * compoPart.getFacePart().getHeight() * compoPart.getParams().getScale() / getBackgroundFace().getDrawable().getIntrinsicHeight()  ;
		return partHeight;
	}

	
	/**
	 * Return if the CompoPart is touched
	 */
	public boolean isCompoPartTouched(CompoPart compoPart, PointF imageTouchedPoint) {
		float TOUCHEFACTOR = 0.8f;
		PointF center =  computeCenterPosition(compoPart);
		PointF touchedPoint = new PointF(imageTouchedPoint.x - center.x, imageTouchedPoint.y - center.y);
		PointF point = inFacePartAxis(compoPart,touchedPoint);
		
		if ( ( Math.abs(point.x) < (computeWidth(compoPart)/2)*TOUCHEFACTOR ) && ( Math.abs(point.y) < (computeHeight(compoPart)/2)*TOUCHEFACTOR ) ) {
			return true;
		}
		
		return false;
	}
	
	public EditAction whereIsCompoPartTouched(CompoPart compoPart, PointF imageTouchedPoint) {
		float MOVEFACTOR = 0.5f;
		float ROTSCALEFACTOR = 1.0f;
		
		PointF center =  computeCenterPosition(compoPart);
		PointF touchedPoint = new PointF(imageTouchedPoint.x - center.x, imageTouchedPoint.y - center.y);
		PointF point = inFacePartAxis(compoPart,touchedPoint);

		if ( ( Math.abs(point.x)< (computeWidth(compoPart)/2)*MOVEFACTOR ) && ( Math.abs(point.y) < (computeHeight(compoPart)/2)*MOVEFACTOR )) {
			return EditAction.MOVE;
		}
		
		if ( ( Math.abs(point.x)< (computeWidth(compoPart)/2)*ROTSCALEFACTOR ) && ( Math.abs(point.y) < (computeHeight(compoPart)/2)*ROTSCALEFACTOR )) {
			return EditAction.ROTSCALE;
		}

		return EditAction.NOTHING;
	}
	
	/**
	 * return the point in the CompoPart axis (using the rotation of the axis)
	 * @param 	point : the touched point relatively to the part center position
	 */
	private PointF inFacePartAxis(CompoPart compoPart, PointF point) {
		PointF pointInPart = new PointF( 	point.x * ((float) Math.cos( Math.toRadians((double) compoPart.getParams().getRotation())) ) + point.y * ((float) Math.sin( Math.toRadians((double) compoPart.getParams().getRotation())) ) ,
											point.y * ((float) Math.cos( Math.toRadians((double) compoPart.getParams().getRotation())) ) - point.x * ((float) Math.sin( Math.toRadians((double) compoPart.getParams().getRotation())) ) );
		return pointInPart;
	}
	
	/**
	 * Load a preset
	 */
	public void loadPreset(Preset preset) {
        for( int i = 0; i < preset.getParamList().size(); i++) {
        	mCompoParts.get(i).setParams(preset.getParamList().get(i));
        }
	}
	
	/**
	 * Save the compo as a Bitmap
	 */
	public Bitmap saveAsBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(mBackgroundFace.getBitmap());
		Canvas canvas = new Canvas(bitmap);
		
        for( CompoPart compoPart : getCompoParts()) {
        	RectF boundsF = computeBounds(compoPart);
        	Rect bounds = new Rect(	(int) (boundsF.left * mBackgroundFace.getBitmap().getWidth()),
        							(int) (boundsF.top * mBackgroundFace.getBitmap().getHeight()), 
        							(int) (boundsF.right * mBackgroundFace.getBitmap().getWidth()), 
        							(int) (boundsF.bottom * mBackgroundFace.getBitmap().getHeight()) );

        	PointF center = computeCenterPosition(compoPart);
        	center.x = center.x * mBackgroundFace.getBitmap().getWidth();
        	center.y = center.y * mBackgroundFace.getBitmap().getHeight();
        	
			compoPart.getFacePart().getDrawable().setBounds(bounds);
        	canvas.save();
        	canvas.rotate(compoPart.getParams().getRotation(), center.x, center.y);
        	compoPart.getFacePart().getDrawable().draw(canvas);
        	canvas.restore();	        	
        }
        
		return bitmap;
	}
}
