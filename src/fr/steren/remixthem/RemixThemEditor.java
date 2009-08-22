package fr.steren.remixthem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RemixThemEditor extends Activity {
	
    public static final int REQUEST_CODE_TAKE_PICTURE   	= 1;
    public static final int REQUEST_CODE_USE_IMAGE     		= 2;
    public static final int REQUEST_CODE_USE_CONTACT_IMAGE 	= 3;
    
    /** Which Editor are we using (mix = 0 or remix = 1) */
    private int mEditor;
    
    /** If the menu has been displayed */
    private boolean mMenuDisplayed;
    
    /** If photo(s) have been load, display the editor */
    private boolean mReadyToEdit;
    
    /** A handle to the View in which the RemixThem is running. */
    private RemixThemView mRemixThemView;    
    
    /** Called when the activity is first created. */ 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Get the Editor (mix = 0 or remix = 1)
        mEditor = getIntent().getExtras().getInt("Editor");
       
        mRemixThemView = new RemixThemView(this);
          
        setContentView(R.layout.editormenu);
        
        TextView hello_text = (TextView) findViewById(R.id.hello_editor_text);
        hello_text.setText(R.string.hello_editor);

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
            	//loadContactPictureGrid();
            	//Intent i = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
            	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            	intent.setType(People.CONTENT_ITEM_TYPE);
            	startActivityForResult(intent, 3); 
            }

        
        });
        
        mMenuDisplayed = false;
        mReadyToEdit = false;
        
    }
    
    private void takePicture() {
    	Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(imageCaptureIntent, REQUEST_CODE_TAKE_PICTURE);
    	//TODO 
    	// mettre l'uri d'un bitmap dans les extras de l'intent
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	if( mEditor == 1 ) {
    		inflater.inflate(R.menu.menu_mix, menu);
    	}else {
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
        	mRemixThemView.noActiveCompoPart();
        	Log.i("RemixThem", "ChangeMode");
            return true;
        case R.id.editpart:
        	mRemixThemView.setMode(RemixThemView.Mode.EDITPARTMODE);
            return true;
        case R.id.random:
        	mRemixThemView.noActiveCompoPart();
        	mRemixThemView.randomize();
            return true;
        case R.id.ugly:
        	mRemixThemView.noActiveCompoPart();
        	mRemixThemView.randomPreset();
            return true;
        case R.id.reset:
        	mRemixThemView.noActiveCompoPart();
        	mRemixThemView.resetParams();
        	return true;
        case R.id.send:
        	send();
        	return true;
        case R.id.save:
        	saveOnDisk();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_TAKE_PICTURE:
            	if(data !=null) {
            		Bitmap faceBitmap1 = (Bitmap) data.getParcelableExtra("data");
            		receiveBitmap(faceBitmap1);
            	}
                break;

            case REQUEST_CODE_USE_IMAGE:
                Uri uri = data.getData();
                if(uri != null) {
                	uri.getEncodedPath();
                	Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                	cursor.moveToFirst();
                	String imageFilePath = cursor.getString(0);
                	cursor.close(); 

                	//Prepare to load Bitmap
        	        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        	        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                	Bitmap originalBitmap = BitmapFactory.decodeFile(imageFilePath, bitmapOptions);
                	//Resize the Bitmap : 
                	int width = originalBitmap.getWidth();
                    int height = originalBitmap.getHeight();
                    int newHeight = 400;
                    int newWidth = (int)( (float)(newHeight * width) / (float)(height) );
                    // calculate the scale
                    float scaleWidth = ((float) newWidth) / ((float) width);
                    float scaleHeight = ((float) newHeight) / ((float) height);
                    // create a matrix for the manipulation
                    Matrix matrix = new Matrix();
                    // resize the bit map
                    matrix.postScale(scaleWidth, scaleHeight);
                    // create the new Bitmap
                    Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true); 

                    //Extract the face from this bitmap
            		receiveBitmap(resizedBitmap);
                }
                break;
            case REQUEST_CODE_USE_CONTACT_IMAGE:
            	Uri contactUri = data.getData();
            	
            	InputStream contactPictureStream = Contacts.People.openContactPhotoInputStream(getContentResolver(), contactUri);
            	
            	BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
    	        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            	Bitmap originalBitmap = BitmapFactory.decodeStream(contactPictureStream, null, bitmapOptions);
        		receiveBitmap(originalBitmap);
            	break;
            default:
                break;
        }
    }
    
    private void receiveBitmap(Bitmap faceBitmap) {
        if (faceBitmap == null) {
			Toast.makeText(this, R.string.ERROR_bitmap_null,Toast.LENGTH_LONG).show(); 
			return;
        }
        
        if (mRemixThemView.addHead(this, faceBitmap) == false)
        {
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setIcon(R.drawable.alert_icon);
		    builder.setTitle(R.string.nofacedetected);
		    builder.setMessage(R.string.nofacedetected_message);
	        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
	        
	        return; 
        }
        
        //if everything succeeded
        if(mEditor == 0) { //If we are in edit mode
        	//select a random preset
        	mRemixThemView.randomPreset();
        	setContentView(mRemixThemView);
        	mReadyToEdit = true;
        } else if(mEditor == 1 &&  mRemixThemView.getHeadNumber()==1) { //if we ask 2 pictures and have only one
            TextView hello_text = (TextView) findViewById(R.id.hello_editor_text);
            hello_text.setText(R.string.new_picture_editor);
        } else if ( mEditor == 1 && mRemixThemView.getHeadNumber() > 1) { //if we ask 2 pictures and have 2
        	//randomize the face
        	mRemixThemView.randomize();
        	setContentView(mRemixThemView);
        	mReadyToEdit = true;
        }
    }


    private void loadContactPictureGrid() {
    	Intent intent = new Intent(this, ContactGrid.class);
        startActivity(intent);
    }

    private Uri saveOnDisk() {
   	    //First create the directory if it doesn't exist
    	File directory = new File(Environment.getExternalStorageDirectory(), "RemixThem");
    	if(!directory.exists())
    	{  	
    		directory.mkdir();
    	}  	
    	
    	Uri returnUri = null;
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
		return returnUri;
    }
    
    private String computeFileName() {
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        Date date = new Date();
	        return "RemixThem_"+ dateFormat.format(date);
	    }
    
    private void send() {

    	Intent email = new Intent(Intent.ACTION_SEND);
    	email.putExtra(Intent.EXTRA_STREAM, saveOnDisk() );
    	email.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject); 
    	email.putExtra(Intent.EXTRA_TEXT, R.string.email_body );
    	email.setType("image/*"); 
    	startActivity(email);  	
    
    }
    
}