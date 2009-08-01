package fr.steren.remixthem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

public class RemixThemEditor extends Activity {
    public static final int REQUEST_CODE_TAKE_PICTURE   = 1;
    public static final int REQUEST_CODE_USE_IMAGE     	= 2;

    
    /** A handle to the View in which the RemixThem is running. */
    private RemixThemView mRemixThemView;    
    
    /** Called when the activity is first created. */ 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemixThemView = new RemixThemView(this);

        //DEBUG Uncomment this to auto-load pictures
//        BitmapFactory.Options bfo = new BitmapFactory.Options();
//        bfo.inPreferredConfig = Bitmap.Config.RGB_565;
//        Bitmap faceBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test_faces_cab , bfo);
//        mRemixThemView.addHead(this, faceBitmap);
//        Bitmap faceBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.test_faces_poly , bfo);
//        mRemixThemView.addHead(this, faceBitmap2);
        //End DEBUG

        setContentView(mRemixThemView);
        mRemixThemView.requestFocus();
               
        //Toast.makeText(this, R.string.takepicture, Toast.LENGTH_LONG).show(); 

        //small alert
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ok_icon);
	    builder.setTitle(R.string.start);
	    builder.setMessage(R.string.start_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	//take picture
                takePicture();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();	
        

        
        
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_remix, menu);
    	return true;
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
                Bitmap faceBitmap = (Bitmap) data.getParcelableExtra("data");

                if (faceBitmap == null) {
                	Log.e("RemixThem", "Take Photo Fail, bitmap null");
                	setTitle("Problemwith picture : Bitmap null");
                    return;
                }
                mRemixThemView.addHead(this, faceBitmap);
                setTitle("Picture received");
                break;

            case REQUEST_CODE_USE_IMAGE:
                setTitle("Image received");
                //addImage(data.getData());
                break;
            default:
                // TODO
                break;
        }
    }

    private void takePicture() {
    	Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(imageCaptureIntent, REQUEST_CODE_TAKE_PICTURE);
    	//TODO 
    	// mettre l'uri d'un bitmap dans les extras de l'intent
    }

    private void saveOnDisk() {
   	    //First create the directory if it doesn't exist
    	File directory = new File(Environment.getExternalStorageDirectory(), "RemixThem");
    	if(!directory.exists())
    	{  	
    		directory.mkdir();
    	}  	
    	
        File file1 ;
        FileOutputStream outputStream = null;
		try {
			file1 = new File(directory, computeFileName()+ ".jpg");
	        file1.createNewFile();
	        
			outputStream = new FileOutputStream(file1);
			mRemixThemView.getActiveCompo().saveAsBitmap().compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
			outputStream.close();
		
		} catch (FileNotFoundException e1) {
			Toast.makeText(this, "Error : Can't create the file",Toast.LENGTH_SHORT).show(); 
			e1.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "Error : Can't write in file",Toast.LENGTH_SHORT).show(); 
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
    }
    
    private String computeFileName() {
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        Date date = new Date();
	        return "remix_"+ dateFormat.format(date);
	    }
    
    private void send() {
    	Uri tempUri = null;
    	String fileName = "RemixThem.jpg";
    	try {
			File path = getFileStreamPath(fileName);
			path.delete();
    		
    		FileOutputStream filestream = this.openFileOutput(fileName, 0);
			mRemixThemView.getActiveCompo().saveAsBitmap().compress(Bitmap.CompressFormat.JPEG, 95, filestream);
			filestream.close();
			tempUri = Uri.fromFile(path);
			
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "Error : Can't create the file",Toast.LENGTH_SHORT).show(); 
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, "Error : Can't write in file",Toast.LENGTH_SHORT).show(); 
			e.printStackTrace();
		}

    	Intent email = new Intent(Intent.ACTION_SEND);
    	email.putExtra(Intent.EXTRA_STREAM, tempUri );
    	email.setType("image/*"); 
    	startActivity(email);  	
    
    }
    
}