package fr.steren.remixthem;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.Log;

public class BackgroundFace {
	
	private BitmapDrawable mDrawable;
	
	private boolean mFaceDetected;
		
	/** the detected eye position and distance */
	private PointF mEyesDetectedPosition;
	private float mEyesDetectedDistance;

	/** These are the intrinsic FaceParts, they can be null if the BackgroundFace doesn't have FaceParts */
    private ArrayList<FacePart> mFaceParts;

    public BackgroundFace(Context context, Bitmap faceBitmap) {
	    FaceDetector faceDetector;
        
        // Detect face
        faceDetector = new FaceDetector(faceBitmap.getWidth(),faceBitmap.getHeight(), 1);
        Face[] foundFaces = new Face[1];
        Face face = null;
        faceDetector.findFaces(faceBitmap, foundFaces);
        for (int i = 0; i < foundFaces.length; i++) {
        	if ( foundFaces[i] != null ) {
        		face = foundFaces[i];
        		Log.i("RemixThem", " FOUND ONE FACE");
        		break;
        	}
        	else {
        		Log.e("RemixThem", "no face found");
        	}
        }
        // Position the eyes
        if( face == null ) {
        	mFaceDetected = false;
        } else {
        	mFaceDetected = true;
        	
        	mEyesDetectedPosition = new PointF();
            face.getMidPoint(mEyesDetectedPosition);
            mEyesDetectedDistance = face.eyesDistance();
        
	        // load the picture of the face
	        mDrawable = new BitmapDrawable(faceBitmap);
	
	        //Create the eyes, nose and mouth FaceParts
	        BitmapFactory.Options alphaOptions = new BitmapFactory.Options();
	        alphaOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
	        mFaceParts = new ArrayList<FacePart>();
	        
	        Bitmap alphaMaskEyes = BitmapFactory.decodeResource(context.getResources(), R.drawable.alphamask_eyes, alphaOptions);
	        BitmapDrawable eyesDrawable = extractFacePart(faceBitmap, 2.2f, 1.2f, 0.f, -0.1f, alphaMaskEyes);
			mFaceParts.add( new FacePart(eyesDrawable, eyesDrawable.getIntrinsicWidth()/mEyesDetectedDistance , eyesDrawable.getIntrinsicHeight()/mEyesDetectedDistance, 0.05f, 0.15f) );
	
	        Bitmap alphaMaskNose = BitmapFactory.decodeResource(context.getResources(), R.drawable.alphamask_nose, alphaOptions);
	        BitmapDrawable noseDrawable = extractFacePart(faceBitmap, 1.1f, 1.3f, 0.f, 0.4f, alphaMaskNose);
			mFaceParts.add(  new FacePart(noseDrawable, noseDrawable.getIntrinsicWidth()/mEyesDetectedDistance , noseDrawable.getIntrinsicHeight()/mEyesDetectedDistance, 0.15f, 0.15f) );
	        
	        Bitmap alphaMaskMouth = BitmapFactory.decodeResource(context.getResources(), R.drawable.alphamask_mouth, alphaOptions);
	        BitmapDrawable mouthDrawable = extractFacePart(faceBitmap, 1.4f, 0.7f, 0.f, 1.1f, alphaMaskMouth);
	        mFaceParts.add(  new FacePart(mouthDrawable, mouthDrawable.getIntrinsicWidth()/mEyesDetectedDistance , mouthDrawable.getIntrinsicHeight()/mEyesDetectedDistance, 0.15f, 0.15f) );
        } 
    }
    
	public boolean isFaceDetected() {
		return mFaceDetected;
	}

    /**
     * extract a facePart from an image with a face
     * @param faceBitmap	: the Bitmap with the face on it
     * @param partSizeX
     * @param partSizeY
     * @param partSizePositionX
     * @param partSizePositionY
     * @param alphaMask	: the alpha mask Bitmap to be applied to the image
     * @return
     */
    private BitmapDrawable extractFacePart(Bitmap faceBitmap, float partSizeX, float partSizeY, float partSizePositionX, float partSizePositionY, Bitmap alphaMask) {
        int facePartStartX = (int)(mEyesDetectedPosition.x + mEyesDetectedDistance*(partSizePositionX - partSizeX/2) );
        int facePartStartY = (int)(mEyesDetectedPosition.y + mEyesDetectedDistance*(partSizePositionY - partSizeY/2) );
        int facePartPicW = (int)(mEyesDetectedDistance*(partSizeX));
        int facePartPicH = (int)(mEyesDetectedDistance*(partSizeY));
        int[] facePartPix = new int[facePartPicW * facePartPicH];
        faceBitmap.getPixels(facePartPix, 0, facePartPicW, facePartStartX, facePartStartY, facePartPicW, facePartPicH);

        int alphaMaskW = alphaMask.getWidth();
        int alphaMaskH = alphaMask.getHeight();
        int[] alphaMaskPix = new int[alphaMask.getWidth()*alphaMaskH];
        alphaMask.getPixels(alphaMaskPix, 0, alphaMaskW, 0, 0, alphaMaskW, alphaMaskH);
        for (int y = 0; y < facePartPicH; y++) {
        	   for (int x = 0; x < facePartPicW; x++) {
        	      int index = y * facePartPicW + x;
        	      
        	      int xMask = x * alphaMaskW  / facePartPicW;
        	      int yMask = y * alphaMaskH  / facePartPicH;
        	      int indexMask = yMask * alphaMaskW + xMask;
        	      
        	      int l = Color.red(alphaMaskPix[indexMask]);
        	      
        	      facePartPix[index] = Color.argb(l, Color.red(facePartPix[index]), Color.green(facePartPix[index]), Color.blue(facePartPix[index]));
        	   }
        }
        Bitmap eyesBitmap = Bitmap.createBitmap(facePartPix, facePartPicW, facePartPicH, Bitmap.Config.ARGB_8888);   
        
        BitmapDrawable facePartDrawable = new BitmapDrawable(eyesBitmap);
		return facePartDrawable;
    }
    
	public BitmapDrawable getDrawable() {
		return mDrawable;
	}

	public PointF getEyesDetectedPosition() {
		return mEyesDetectedPosition;
	}
	public float getEyesDetectedDistance() {
		return mEyesDetectedDistance;
	}
	public ArrayList<FacePart> getFaceParts() {
		return mFaceParts;
	}


}
