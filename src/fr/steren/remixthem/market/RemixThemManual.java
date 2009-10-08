package fr.steren.remixthem.market;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class RemixThemManual extends Activity {

    /** Manual input view */
    private RemixThemManualView mRemixThemManualInputView;   

	public RemixThemManual() {
		super();
	}

    /** Called when the activity is first created. */ 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRemixThemManualInputView = new RemixThemManualView(this, RemixThemEditor.mCurrentBitmap);
        setContentView(mRemixThemManualInputView);
    }
	
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
   		inflater.inflate(R.menu.menu_manual, menu);    		
    	for( int i = 0; i < menu.size(); i++) {
    		menu.getItem(i).setVisible(true);
    	}
    	
    	return true;
    }
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.manual_ok:
        	grabManualInputInfo();
        	return true;
        case R.id.manual_cancel:
        	setResult(RESULT_CANCELED);
        	finish();
        	return true;
        }        
        return false;
    }

    
    private void grabManualInputInfo() {
        	if (mRemixThemManualInputView.isReady() ) {
            	setTitle(R.string.app_name);

            	Intent data = new Intent();
            	data.putExtra("EyePositionX", mRemixThemManualInputView.getEyePosition().x	);
            	data.putExtra("EyePositionY", mRemixThemManualInputView.getEyePosition().y	);
            	data.putExtra("EyeDistance"	, mRemixThemManualInputView.getEyeDistance()	);
            	setResult(RESULT_OK, data);
            	finish();
        	} else {
        		Toast.makeText(this, R.string.please_2_eyes,Toast.LENGTH_LONG).show();
        	}
    }
}