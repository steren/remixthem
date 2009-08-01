package fr.steren.remixthem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import fr.steren.remixthem.Compo.EditAction;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class RemixThemView extends View {
	public enum Mode {CHANGEMODE, EDITPARTMODE};

	private Mode mMode= Mode.CHANGEMODE;
	private EditAction mEditAction = EditAction.NOTHING;
	
    /** The face touched by an Action event. */
    private CompoPart mActiveCompoPart;
    /** The compo that is currently being modified */
    private Compo mActiveCompo;
    
    /** The position of the compo */
    private int mCompoPositionLeft;
    private int mCompoPositionTop;
    private int mCompoPositionRight;
    private int mCompoPositionBottom;
	
    /** All the "heads" are stored here */
    private ArrayList<BackgroundFace> mHeads;

    public Compo getActiveCompo() {
		return mActiveCompo;
	}

	/** All the presets are stored here */
    private ArrayList<Preset> mPresets;
    
	/**
	 * The difference between the touched point and the center of the considered part
	 */
    private float mDeltaTouchX;	
    private float mDeltaTouchY;
    /**
     * temporary store the initial rotation and scale 
     */
    private float mTempRotation;
    private float mTempScale;
    
   
    
	public RemixThemView(Context context) {
		super(context);
		Log.i("RemixThem", "HELLO WORLD");
		
		//Depending the screen size, draw the Compo
		//TODO
		mCompoPositionLeft = 0;
	    mCompoPositionTop = 0;
	    mCompoPositionRight = 300;
	    mCompoPositionBottom = 400;
		
	    //Store the background faces
	    mHeads = new ArrayList<BackgroundFace>();
	    
	    //load the presets
	    mPresets = new ArrayList<Preset>();
	    loadPresets(context);
	}

	@Override protected void onDraw(Canvas canvas) {
		if(mActiveCompo != null) {
	        mActiveCompo.getBackgroundFace().getDrawable().setBounds(mCompoPositionLeft, mCompoPositionTop, mCompoPositionRight, mCompoPositionBottom);
	        mActiveCompo.getBackgroundFace().getDrawable().draw(canvas);
	    	
	        for( CompoPart compoPart : mActiveCompo.getCompoParts()) {
	        	Rect bounds = computeCompoPartBoundsOnScreen(compoPart);
				Point center = convertCompoImagePointToScreenPoint(mActiveCompo.computeCenterPosition(compoPart));

				compoPart.getFacePart().getDrawable().setBounds(bounds);
	        	canvas.save();
	        	canvas.rotate(compoPart.getParams().getRotation(), center.x, center.y);
	        	compoPart.getFacePart().getDrawable().draw(canvas);
	        	canvas.restore();	        	
	        }
	        
	        if( mActiveCompoPart != null) {
		        drawUILines(canvas, mActiveCompoPart);	        	
	        }
		}
	}

	/**
	 * Draw the edition Gizmo around the given CompoPart 
	 * @param canvas	: the canvas where to draw
	 * @param compoPart	: the compoPart around which to draw the gizmo
	 */
	private void drawUILines(Canvas canvas, CompoPart compoPart) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
    	paint.setARGB(220, 255, 255, 255);
    	
    	Rect bounds = computeCompoPartBoundsOnScreen(compoPart);
		Point center = convertCompoImagePointToScreenPoint(mActiveCompo.computeCenterPosition(compoPart));
    	
    	canvas.save();
    	canvas.rotate(compoPart.getParams().getRotation(), center.x, center.y);
    	canvas.drawLine(bounds.exactCenterX(), bounds.top, bounds.right, bounds.top, paint);
	    canvas.drawLine(bounds.right, bounds.top, bounds.right, bounds.exactCenterY(), paint);
	    canvas.drawLine(bounds.left, bounds.bottom, bounds.left, bounds.exactCenterY(), paint);
	    canvas.drawLine(bounds.exactCenterX(), bounds.bottom, bounds.left, bounds.bottom, paint);
    	canvas.restore();
    	
	    canvas.drawCircle(center.x, center.y, 3, paint);
	}
	
	
    @Override public boolean onTouchEvent(MotionEvent event) {
		if(mActiveCompo != null) {
	    	int action = event.getAction();
	    	Point touchedPoint = new Point( (int)event.getX(), (int)event.getY());
	    	PointF touchedPointInImage = convertScreenPointToCompoImagePoint(touchedPoint);
	    	
	    	if(mMode == Mode.EDITPARTMODE){
	            if(action == MotionEvent.ACTION_DOWN) {
	            	//Check if the user points on a widget
	            	if(mActiveCompoPart != null) {
	            		mEditAction = mActiveCompo.whereIsCompoPartTouched(mActiveCompoPart, touchedPointInImage );
	            		
	            		if(mEditAction != EditAction.NOTHING) {
	            			PointF compoPartCenter = mActiveCompo.computeCenterPosition(mActiveCompoPart);
	            			mDeltaTouchX = touchedPointInImage.x - compoPartCenter.x;
	            			mDeltaTouchY = touchedPointInImage.y - compoPartCenter.y;
	            			mTempRotation = mActiveCompoPart.getParams().getRotation();
	            			mTempScale = mActiveCompoPart.getParams().getScale();
	
	            			return true;
	            		}
	            	}
	            	
	            	//if no action has been started or no active CompoPart, check if the user points to a compoPart.
	            	for( CompoPart compoPart : mActiveCompo.getCompoParts()) {
	                	if ( mActiveCompo.isCompoPartTouched(compoPart, touchedPointInImage )) {
	                    	mActiveCompoPart = compoPart;
	                    	invalidate();
	                    	return true;
	                	}else {
	                    	mActiveCompoPart = null;
	                    	invalidate();
	                	}
	                }
	            } else  if(action == MotionEvent.ACTION_MOVE) {
	            	if(mEditAction == EditAction.MOVE) {
	        			PointF newCenterPosition = new PointF(touchedPointInImage.x - mDeltaTouchX, touchedPointInImage.y - mDeltaTouchY);
	        			mActiveCompo.setCenterPosition(mActiveCompoPart, newCenterPosition);
	                	invalidate();
	        			return true;
	        		}else if(mEditAction == EditAction.ROTSCALE) {
	        			PointF compoPartCenter = mActiveCompo.computeCenterPosition(mActiveCompoPart);
	
	        			PointF oldDelta = new PointF(mDeltaTouchX, mDeltaTouchY);
	        			PointF newDelta = new PointF(touchedPointInImage.x - compoPartCenter.x , touchedPointInImage.y - compoPartCenter.y);
	
	        			//Rotation
	        			double angle1 = Math.atan2((double) mDeltaTouchX, (double) mDeltaTouchY);
	        			double angle2 = Math.atan2((double) newDelta.x, (double) newDelta.y);
	        			double anglediff = angle2 - angle1;
	        			mActiveCompoPart.getParams().setRotation(mTempRotation - (float) Math.toDegrees(anglediff));
	        			
	        			//Scale
	        			float factor = newDelta.length()/oldDelta.length();
	        			mActiveCompoPart.getParams().setScale(mTempScale * factor);
	        			
	        			invalidate();
	        			return true;
	        		}
	            	
	            	return true;
	            }
	            return false;
	            
	    	} else if (mMode == Mode.CHANGEMODE) {
	            if(action == MotionEvent.ACTION_DOWN) {
	            	int compoPartIndex =0;
	                for( CompoPart compoPart : mActiveCompo.getCompoParts()) {
	                	if ( mActiveCompo.isCompoPartTouched(compoPart, touchedPointInImage )) {
	                		int[] indexes = getFacePartIndex(compoPart.getFacePart());
	                		indexes[0] = (indexes[0] + 1) % mHeads.size();
	                		compoPart.setFacePart(mHeads.get(indexes[0]).getFaceParts().get(indexes[1]));
	                		invalidate();
	                    	return true;
	                	}
	                	compoPartIndex++;
	                }
	                //if no facePart has been touched, than, change the background
	                int index = getHeadIndex(mActiveCompo.getBackgroundFace());
	        		index = (index + 1) % mHeads.size();
	        		mActiveCompo.setBackgroundFace(mHeads.get(index));
	        		invalidate();
	        		return true;
	            }
	    	}
	    	
	    	mActiveCompoPart = null;
			return false;
		}
		return false;
    }
    
	public void setMode(Mode mode) {
		mMode = mode;
		invalidate();
	}
	
	/**
	 * Find a given head in the mHeads vector member and return its index.
	 * @param the head to look for
	 * @return the index of the head, starting at 0. -1 if the head is not listed (=problem)
	 */
	private int getHeadIndex(BackgroundFace head) {
		for( int i =0; i < mHeads.size(); ++i) {
			if(mHeads.get(i) == head) {
				return i;	
			}
		}
		return -1;
	}

	/**
	 * Find a given facePart in all the available faceParts
	 * @param facePart
	 * @return an array of 2 indexes : the first one is the index of the Backgroundface containing the facepart, the second is the index of the facePart in the mFaceParts array of this backgroundface 
	 */
	private int[] getFacePartIndex(FacePart facePart) {
		int[] indexes = new int[2];
		indexes[0] = -1;
		indexes[1] = -1;
		
		for( int i=0; i<mHeads.size(); ++i ) {
			for( int j=0; j<mHeads.get(i).getFaceParts().size(); ++j) {
				
				if(mHeads.get(i).getFaceParts().get(j) == facePart) {
					indexes[0] = i;
					indexes[1] = j;
					return indexes;
				}
			}
		}
		return indexes;
	}
	
	public void noActiveCompoPart() {
		mActiveCompoPart = null;
	}
	
	/**
	 * Randomize the Compo : its BackgroundFace and its parts
	 */
	public void randomize() {
		if(mActiveCompo != null) {
			Random rand = new Random();
			//generate a random number
			int index = rand.nextInt( mHeads.size() );
			//change the background image
			mActiveCompo.setBackgroundFace(mHeads.get(index));
			//change parts
			for(int i = 0; i < mActiveCompo.getCompoParts().size(); ++i) {
				int index2 = rand.nextInt( mHeads.size() );
				mActiveCompo.getCompoParts().get(i).setFacePart(mHeads.get(index2).getFaceParts().get(i));
			}
			invalidate();  
		}
	}

	/**
	 * Select a random preset and apply it to the active compo
	 */
	public void randomPreset() {
		if(mActiveCompo != null && mPresets.size() != 0) {
			Random rand = new Random();
			//generate a random number
			int index = rand.nextInt( mPresets.size() );
			//load the preset
			mActiveCompo.loadPreset(mPresets.get(index));
			invalidate();  
		}
	}

	private Rect computeCompoPartBoundsOnScreen(CompoPart compoPart){
		RectF rect = mActiveCompo.computeBounds(compoPart);
		
		Rect bounds = new Rect( (int) ( mCompoPositionLeft 	+  rect.left 	* (mCompoPositionRight-mCompoPositionLeft) ),
								(int) ( mCompoPositionTop 	+  rect.top 	* (mCompoPositionBottom-mCompoPositionTop) ),
								(int) ( mCompoPositionLeft 	+  rect.right 	* (mCompoPositionRight-mCompoPositionLeft) ),
								(int) ( mCompoPositionTop 	+  rect.bottom 	* (mCompoPositionBottom-mCompoPositionTop) )  );
		return bounds;
	}

	/**
	 * Takes an point in [0,1]² and return its coordinates on the screen
	 */
	private Point convertCompoImagePointToScreenPoint(PointF imagePoint){
		Point point = new Point( (int) ( mCompoPositionLeft + imagePoint.x  * (mCompoPositionRight-mCompoPositionLeft) ),
								 (int) ( mCompoPositionTop + imagePoint.y * (mCompoPositionBottom-mCompoPositionTop) )  );
		return point;
	}

	/**
	 * Takes an point in screen coordinates and return its coordinates in [0,1]²
	 */
	private PointF convertScreenPointToCompoImagePoint(Point point) {
		PointF imagePoint = new PointF(	(float) (point.x - mCompoPositionLeft) / (float) (mCompoPositionRight-mCompoPositionLeft) ,
										(float) (point.y - mCompoPositionTop) / (float) (mCompoPositionBottom-mCompoPositionTop) );
		return imagePoint;
	}
	
	/**
	 * add a new face to the Heads vector
	 * @param context	: context of the activity
	 * @param faceBitmap : the Bitmap of the new face
	 */
	public void addHead(Context context, Bitmap faceBitmap) {
	    BackgroundFace backgroundface = new BackgroundFace(context, faceBitmap);
	    
	    //TODO use exceptions ?
	    if(backgroundface.isFaceDetected()) {
		    if(mHeads.isEmpty()) {
		    	mActiveCompo = new Compo(backgroundface);
		    }
		    mHeads.add(backgroundface);	    	
	    }else{
		    AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setIcon(R.drawable.alert_icon);
		    builder.setTitle(R.string.nofacedetected);
		    builder.setMessage(R.string.nofacedetected_message);
		    //TODO use string
	        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                // do some stuff?
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();	    	
	    }
	}
	
	/**
	 * Load the presets configuration file
	 * @param context
	 */
	private void loadPresets(Context context) {
		try {
			XmlResourceParser presetsXML = context.getAssets().openXmlResourceParser("presets.xml");
		} catch (IOException e) {
		}

		//TODO load from XML
		
		ArrayList<CompoPartParams> paramlist = new ArrayList<CompoPartParams>();
		CompoPartParams eyesParams = new CompoPartParams(new PointF((float) 0.0, (float) -0.1) );
		paramlist.add(eyesParams);
		CompoPartParams noseParams = new CompoPartParams(new PointF((float) 0.0, (float) 0.4) );
		paramlist.add(noseParams);
		CompoPartParams mouthParams = new CompoPartParams(new PointF((float) 0.0, (float) 1.1) );
		paramlist.add(mouthParams);
		Preset preset = new Preset(paramlist);

		ArrayList<CompoPartParams> paramlist2 = new ArrayList<CompoPartParams>();
		CompoPartParams eyesParams2 = new CompoPartParams(new PointF((float) 0.0, (float) -0.1), (float)10.0, (float)1.5);
		paramlist2.add(eyesParams2);
		CompoPartParams noseParams2 = new CompoPartParams(new PointF((float) 1.0, (float) 0.4) );
		paramlist2.add(noseParams2);
		CompoPartParams mouthParams2 = new CompoPartParams(new PointF((float) 0.0, (float) 1.1), (float)0.0, (float)0.5 );
		paramlist2.add(mouthParams2);
		Preset preset2 = new Preset(paramlist2);

		mPresets.add(preset);
		mPresets.add(preset2);

	}

	
}
