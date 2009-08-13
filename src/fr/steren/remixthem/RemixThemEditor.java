package fr.steren.remixthem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RemixThemEditor extends Activity {
	
    public static final int REQUEST_CODE_TAKE_PICTURE   = 1;
    public static final int REQUEST_CODE_USE_IMAGE     	= 2;

    private int mEditor; 
    
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
        
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	if( mEditor == 1 ) {
    		inflater.inflate(R.menu.menu_mix, menu);
    	}else {
    		inflater.inflate(R.menu.menu_remix, menu);
    	}
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
                Bitmap faceBitmap = (Bitmap) data.getParcelableExtra("data");

                if (faceBitmap == null) {
                	Log.e("RemixThem", "Take Photo Fail, bitmap null");
        			Toast.makeText(this, "Error : Bitmap null",Toast.LENGTH_LONG).show(); 
                            			
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
                } else if(mEditor == 1 &&  mRemixThemView.getHeadNumber()==1) { //if we ask 2 pictures and have only one
                    TextView hello_text = (TextView) findViewById(R.id.hello_editor_text);
                    hello_text.setText(R.string.new_picture_editor);
                } else if ( mEditor == 1 && mRemixThemView.getHeadNumber() > 1) { //if we ask 2 pictures and have 2
                	//randomize the face
                	mRemixThemView.randomize();
                	setContentView(mRemixThemView);
                }
        
                break;

            case REQUEST_CODE_USE_IMAGE:
                setTitle("Image received");
                break;
            default:
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
    }
    
    private String computeFileName() {
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        Date date = new Date();
	        return "RemixThem_"+ dateFormat.format(date);
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