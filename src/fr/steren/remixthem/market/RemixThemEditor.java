package fr.steren.remixthem.market;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Contacts.People;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RemixThemEditor extends Activity {
	
    public static final int REQUEST_CODE_TAKE_PICTURE   	= 1;
    public static final int REQUEST_CODE_USE_IMAGE     		= 2;
    public static final int REQUEST_CODE_USE_CONTACT_IMAGE 	= 3;
    public static final int REQUEST_CODE_MANUAL_INPUT 		= 10;
    public static final int REQUEST_CODE_SEND	 			= 20;  
    
    public static final int DEFINE_HEIGHT = 400; 
    
    /** Which Editor are we using (remix = 0 / mix = 1) */
    private int mEditor = 0;
    
    /** If the menu has been displayed */
    private boolean mMenuDisplayed;
    
    /** If photo(s) have been loaded, display the editor */
    private boolean mReadyToEdit;
    
    /** The main remixthem view  */
    private RemixThemView mRemixThemView;    
    
    /** The Bitmap currently processed
     * is static because can be called from an other activity 
     */
    public static Bitmap mCurrentBitmap;
    
    /**
     * A temp URI where will be stored the picture taken with the camera.
     */
    private Uri mTempUri; 
    
    /** Called when the activity is first created. */ 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Get the Editor (remix = 0 / mix = 1)
        mEditor = getIntent().getExtras().getInt("Editor");
        
        mRemixThemView = new RemixThemView(this);

        if(mEditor == 1) {
        	setTitle(R.string.activity_mix);
        	setContentView(R.layout.editormenu_mix);
        }else {
        	setTitle(R.string.activity_remix);
        	setContentView(R.layout.editormenu);
        }

        Button button_take = (Button) findViewById(R.id.button_take);
        button_take.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	takePicture();
            }
        });

        Button button_load = (Button) findViewById(R.id.button_load);
        button_load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivityForResult( new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), REQUEST_CODE_USE_IMAGE); 
            }
        });

        Button button_contact = (Button) findViewById(R.id.button_contact);
        button_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	selectContact();
            }

        
        });
        
        mMenuDisplayed = false;
        mReadyToEdit = false;
        
    }
    
    private void takePicture() {
    	Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	
    	// Use an URI
    	// Save the name and description of an image in a ContentValues map.  
    	ContentValues values = new ContentValues(3);
    	values.put(Media.DISPLAY_NAME, "remixthem");
    	values.put(Media.DESCRIPTION, "Picture to be modify in remixthem");
    	values.put(Media.MIME_TYPE, "image/jpeg");
    	// Add a new record without the bitmap, but with the values just set.
    	// insert() returns the URI of the new record.
    	mTempUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
    	imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTempUri);
    	
    	startActivityForResult(imageCaptureIntent, REQUEST_CODE_TAKE_PICTURE);
    }
    
    private void selectContact() {
		Toast.makeText(this, R.string.contactnosearch, Toast.LENGTH_LONG).show();             	
    	Intent intent = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
    	startActivityForResult(intent, REQUEST_CODE_USE_CONTACT_IMAGE); 
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	if( mEditor == 1 ) {
    		inflater.inflate(R.menu.menu_mix, menu);
    	} else {
    		inflater.inflate(R.menu.menu_remix, menu);
    	}
    	displayMenu(menu, false);

    	return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	
    	if(!mMenuDisplayed) {
    		if(mReadyToEdit) {
    			displayMenu(menu, true);
    			mMenuDisplayed = true;
    		}
    	}
    	return true;
    }
    
    /**
     * Display or hide the menu
     * @param menu  : Menu to operate on
     * @param visible : display this menu or not
     */
    private void displayMenu(Menu menu, boolean visible) {
    	for( int i = 0; i < menu.size(); i++) {
    		menu.getItem(i).setVisible(visible);
    	}
    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.change:
        	mRemixThemView.setMode(RemixThemView.Mode.CHANGEMODE);
        	setTitle(R.string.TITLE_change);
        	mRemixThemView.setDisplayPointPart(true);
        	mRemixThemView.noActiveCompoPart();
            return true;
        case R.id.editpart:
        	mRemixThemView.setMode(RemixThemView.Mode.EDITPARTMODE);
        	setTitle(R.string.TITLE_edit);
        	mRemixThemView.setDisplayPointPart(true);
            return true;
        case R.id.random:
        	mRemixThemView.setMode(RemixThemView.Mode.NOINTERACTION);
        	setTitle(R.string.app_name);
        	mRemixThemView.noActiveCompoPart();
        	mRemixThemView.randomize();
            return true;
        case R.id.ugly:
        	mRemixThemView.setMode(RemixThemView.Mode.NOINTERACTION);
        	setTitle(R.string.app_name);
        	mRemixThemView.noActiveCompoPart();
        	mRemixThemView.randomPreset();
            return true;
        case R.id.reset:
        	mRemixThemView.setMode(RemixThemView.Mode.NOINTERACTION);
        	setTitle(R.string.app_name);
        	mRemixThemView.noActiveCompoPart();
        	mRemixThemView.resetParams();
        	return true;
        case R.id.send:
        	mRemixThemView.setMode(RemixThemView.Mode.NOINTERACTION);
        	setTitle(R.string.app_name);
        	mRemixThemView.noActiveCompoPart();
        	send();
        	return true;
        case R.id.save:
        	mRemixThemView.setMode(RemixThemView.Mode.NOINTERACTION);
        	setTitle(R.string.app_name);
        	mRemixThemView.noActiveCompoPart();
        	saveOnDisk();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == Activity.RESULT_OK)
    	{
	        switch(requestCode) {
	            case REQUEST_CODE_TAKE_PICTURE:
	            	// Old code to use if the URI is given (a small sized image is returned).
	            	// Bitmap sampledBitmapTake = (Bitmap) data.getParcelableExtra("data");
	            	
	            	Bitmap sampledBitmapTake = preSampleBitmap(mTempUri);
                	
                	// delete the image saved by the camera
                	getContentResolver().delete(mTempUri, null, null);
                	
                    //Extract the face from this bitmap
            		loadBitmap(sampledBitmapTake);
                break;
	                
	            case REQUEST_CODE_USE_IMAGE:
	                Uri uri = data.getData();
	                if(uri != null) {
	                	Bitmap sampledBitmapUse = preSampleBitmap(uri);
	                    //Extract the face from this bitmap
	            		loadBitmap(sampledBitmapUse);
	                }
	                break;
	            
	            case REQUEST_CODE_USE_CONTACT_IMAGE:
	            	Uri contactUri = data.getData();
	            	if(contactUri != null) {
	            		InputStream contactPictureStream = Contacts.People.openContactPhotoInputStream(getContentResolver(), contactUri);
	            		if(contactPictureStream != null) {
			            	BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			    	        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
			            	Bitmap contactBitmap = BitmapFactory.decodeStream(contactPictureStream, null, bitmapOptions);
			        		loadBitmap(contactBitmap);
	            		}else {
	            		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	            	        builder.setIcon(R.drawable.alert_icon);
	            		    builder.setTitle(R.string.nofacedetected);
	            		    builder.setMessage(R.string.nocontactfacedetected_message);
	            	        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            	            public void onClick(DialogInterface dialog, int whichButton) {
	            	            }
	            	        });
	            	        AlertDialog alert = builder.create();
	            	        alert.show();
	            		}
	            	}
	            	break;
	            	
	            case REQUEST_CODE_MANUAL_INPUT:
            		Bundle extras = data.getExtras();
            		float posX = extras.getFloat("EyePositionX", 10);
            		float posY = extras.getFloat("EyePositionY", 10);
            		float dist = extras.getFloat("EyeDistance", 10);
            		
	            	if( mRemixThemView.addHead(	this, mCurrentBitmap,new PointF(posX,posY), dist ) == true) {
	                    whatToDoAfterHeadAdded();	 		
	            	}
	            	break;
	            	
	            default:
	                break;
        }
        } else if (resultCode == Activity.RESULT_CANCELED) {
        	// This is strange but the SEND intent always returns RESULT_CANCELED as resultCode.
        	if(requestCode == REQUEST_CODE_SEND) {
            	// TODO delete the temp image but have to be sent first
            	// getContentResolver().delete(mTempUri, null, null);
        	} else {
    			Toast.makeText(this, "Operation cancelled by the user",Toast.LENGTH_LONG).show();
        	}
        	
        }
    }
    
    /**
     * Check the size of the bitmap and load a sub-sampled bitmap if necessary
     * @param uri : the URI of the bitmap.
     * @return : the bitmap loaded in memory at the right size.
     */
    private Bitmap preSampleBitmap(Uri uri) {
    	// get path from URI
    	Cursor cursorUse = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
    	cursorUse.moveToFirst();
    	String imageFilePath = cursorUse.getString(0);
    	cursorUse.close(); 
    	
    	//First get the bounds of the Bitmap
    	BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
    	boundsOptions.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(imageFilePath, boundsOptions);
    	
    	//Compute the sub-sample ratio :
    	int ratio = ( boundsOptions.outHeight / DEFINE_HEIGHT ) + 1;
    			                	
    	//Prepare to load Bitmap
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmapOptions.inSampleSize = ratio;
    	return BitmapFactory.decodeFile(imageFilePath, bitmapOptions);
    }
    
    /**
     * load bitmap into the editor (detect faces and all)
     * @param faceBitmap : the bitmap to load
     */
    private void loadBitmap(Bitmap faceBitmap) {
        if (faceBitmap == null) {
			Toast.makeText(this, R.string.ERROR_bitmap_null,Toast.LENGTH_LONG).show(); 
			return;
        }

    	int width = faceBitmap.getWidth();
    	int height = faceBitmap.getHeight();
    	
        //Make sure the bitmap height is under DEFINE_HEIGHT otherwise, resize it
    	if (height > DEFINE_HEIGHT) {
	    	float newHeight = DEFINE_HEIGHT;
	        float newWidth =  (float)(newHeight * width) / (float)(height);
	        // calculate the scale
	        float scaleWidth = newWidth / ((float) width);
	        float scaleHeight = newHeight / ((float) height);

	        // create a matrix for the manipulation
	        Matrix matrix = new Matrix();
	        // resize the bitmap
	        matrix.postScale(scaleWidth, scaleHeight);
	        // create the new Bitmap
	        faceBitmap = Bitmap.createBitmap(faceBitmap, 0, 0, width, height, matrix, true); 
    	}

    	//Try to detect face
        if (mRemixThemView.addHead(this, faceBitmap) == true) {
            whatToDoAfterHeadAdded();	
        } else {
        	//If no face detected, check if picture horizontal, if so turn it and try again.
        	if(width > height) {
        		//rotate
        		Matrix matrix = new Matrix();
    	        matrix.postRotate(+90f);
    	        faceBitmap = Bitmap.createBitmap(faceBitmap, 0, 0, faceBitmap.getWidth(), faceBitmap.getHeight(), matrix, true); 
    	        //try again
    	        loadBitmap(faceBitmap);
        	} else {
	        	mCurrentBitmap = faceBitmap;
	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setIcon(R.drawable.alert_icon);
			    builder.setTitle(R.string.nofacedetected);
			    builder.setMessage(R.string.nofacedetected_message);
		        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            }
		        });
		        builder.setPositiveButton(R.string.BTN_manual, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	startManualMode();
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();
        	}
        }
        return; 
    }

    /**
     * Call this to start the manual mode
     */
    public void startManualMode() {
    	Intent intent = new Intent(this, RemixThemManual.class);
    	startActivityForResult(intent , REQUEST_CODE_MANUAL_INPUT);
    }
    
    private void whatToDoAfterHeadAdded(){
        //if everything succeeded
        if(mEditor == 0) { //If we are in remix
        	//select a random preset
        	mRemixThemView.randomPreset();
        	setContentView(mRemixThemView);
        	setTitle(R.string.app_name);
        	mReadyToEdit = true;
			Toast.makeText(this, R.string.press_menu_for_option, Toast.LENGTH_LONG).show();
        	return;
        } else if(mEditor == 1 &&  mRemixThemView.getHeadNumber()==1) { //if we ask 2 pictures and have only one
        	//change the text
        	TextView hello_text = (TextView) findViewById(R.id.hello_editor_text);
            hello_text.setText(R.string.new_picture_editor);
            //Update the first portrait
            ImageView portrait1 = (ImageView) findViewById(R.id.portrait1);
            portrait1.setBackgroundDrawable(mRemixThemView.getActiveCompo().getBackgroundFace().getDrawable());
            portrait1.setImageDrawable( getResources().getDrawable(R.drawable.img_portrait_frame));
            return;
        } else if ( mEditor == 1 && mRemixThemView.getHeadNumber() > 1) { //if we ask 2 pictures and have 2
        	//randomize the face
        	mRemixThemView.randomize();
        	setContentView(mRemixThemView);
        	setTitle(R.string.app_name);
        	mReadyToEdit = true;
        	return;
        }
    }

    private Uri saveOnDisk() {
    	Uri returnUri = null;

    	//Check if SDCard
    	if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
	   	    //First create the directory if it doesn't exist
	    	File directory = new File(Environment.getExternalStorageDirectory(), "RemixThem");
	    	if(!directory.exists())
	    	{  	
	    		directory.mkdir();
	    	}  	

	    	File file1;
	        FileOutputStream outputStream = null;
			try {
				file1 = new File(directory, computeFileName()+ ".jpg");
		        file1.createNewFile();
		        
				outputStream = new FileOutputStream(file1);
				mRemixThemView.getActiveCompo().saveAsBitmap().compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
				outputStream.close();
			
				returnUri = Uri.fromFile(file1);
				
				Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show(); 
				
			} catch (FileNotFoundException e1) {
				Toast.makeText(this, "Error : Can't create the file",Toast.LENGTH_LONG).show(); 
				e1.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(this, "Error : Can't write in file",Toast.LENGTH_LONG).show(); 
				e.printStackTrace();
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException ex) {
						// ignore exception
					}
				}
			}
    	}else {
			Toast.makeText(this, "Error : No SD-Card found",Toast.LENGTH_LONG).show(); 
    	}
		return returnUri;
    }
    
    private String computeFileName() {
	        DateFormat dateFormat = new SimpleDateFormat("MM-dd_HH-mm-ss");
	        Date date = new Date();
	        return "RemixThem_"+ dateFormat.format(date);
	}
    
    private void send() {

    	// Save the name and description of an image in a ContentValues map.  
    	ContentValues values = new ContentValues(3);
    	values.put(Media.DISPLAY_NAME, "RemixThem.jpg");
    	values.put(Media.DESCRIPTION, "Picture sent with RemixThem");
    	values.put(Media.MIME_TYPE, "image/jpeg");
    	// Add a new record without the bitmap, but with the values just set.
    	// insert() returns the URI of the new record.
    	mTempUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);

    	// Now get a handle to the file for that record, and save the data into it.
    	// Here, sourceBitmap is a Bitmap object representing the file to save to the database.
    	try {
    	    OutputStream outStream = getContentResolver().openOutputStream(mTempUri);
    	    mRemixThemView.getActiveCompo().saveAsBitmap().compress(Bitmap.CompressFormat.JPEG, 95, outStream);
    	    outStream.close();
    	} catch (Exception e) {
			Toast.makeText(this, "Error : Can't write in image",Toast.LENGTH_LONG).show(); 
    	}
    	
    	Intent sendIntent = new Intent(Intent.ACTION_SEND);
    	sendIntent.putExtra(Intent.EXTRA_STREAM, mTempUri );
    	sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject) ); 
    	sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.email_body) ); 
    	sendIntent.setType("image/*"); 
    	startActivityForResult(sendIntent, REQUEST_CODE_SEND);
    	
    }
    
}