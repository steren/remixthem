package fr.steren.remixthem.market;

import java.util.Date;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RemixThem extends Activity implements View.OnClickListener {

	/** Is the current application sold in the Market as the Full version ? */
	public static final boolean SOLD_FULL_VERSION = true;
	
	private static final String PREFS_NAME = "RemixThemPrefsFile";
	
	/** Store the values of the winner HashCodes */
	private HashSet<String> mCheatCodesSet;
	
	/** Is the current app in lite version state ? (user didn't buy it or didn't entered a promocode) The value is set at startup */
	public static boolean liteVersion;

	private AlertDialog mCheatcodeDialog;
	
	public RemixThem() {
		mCheatCodesSet = new HashSet<String>();
		mCheatCodesSet.add("hashkey1");
		mCheatCodesSet.add("hashkey2");
	}
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	// if the current app is not Full Version, then check the preferences for a special cheat code.
    	if(!SOLD_FULL_VERSION) {
    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            // Check if user has entered a cheat code to unlock the Full Version
            liteVersion = ! settings.getBoolean("cheatCodeOK", false);

            // TODO display or not the colored image
    		setContentView(R.layout.lite_main);
    	} else {
    		liteVersion = false;
    		setContentView(R.layout.main);
    	}
        
        View b = findViewById(R.id.remix_button);
        b.setOnClickListener(this);

        b = findViewById(R.id.mix_button);
        b.setOnClickListener(this);

        b = findViewById(R.id.gallery_button);
        b.setOnClickListener(this);

        if(!SOLD_FULL_VERSION) { // if the user did not pay, always display a Buy me button.
        	b = findViewById(R.id.buy_button);
        	b.setOnClickListener(this);
        }
    }

	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	if(liteVersion) { 
    		getMenuInflater().inflate(R.menu.menu_main, menu);
    	}
    	return true;
    }
    
    
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.remix_button:
            	intent = new Intent(this, RemixThemEditor.class);
            	//send info on which editor to start
            	intent.putExtra("Editor", 0);
                startActivity(intent);
                break;
            case R.id.mix_button:
            	intent = new Intent(this, RemixThemEditor.class);
            	//send info on which editor to start
            	intent.putExtra("Editor", 1);
            	startActivity(intent);
                break;
            case R.id.gallery_button:
            	if(liteVersion) {
                	Toast.makeText(this.getBaseContext(), R.string.BTN_buy_gallery , Toast.LENGTH_LONG).show();            		
            	} else {
            		intent = new Intent(this, RemixThemGrid.class);
            		startActivity(intent);
            	}
                break;
            case R.id.buy_button:
            	Intent goMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:fr.steren.remixthem.market"));
            	startActivity(goMarket);
                break;
        }
    }

    /* Handles item selections */
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.cheatcode:
        	// Create the cheat code dialog and display it
        	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        	View layout = inflater.inflate(R.layout.cheatcodedialog, (ViewGroup) findViewById(R.layout.main));

        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setView(layout);
        	builder.setTitle(R.string.entercheatcode);
        	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        });
	        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
            		handleCheatCode();
	            }
	        });
        	
	        mCheatcodeDialog = builder.create();
        	mCheatcodeDialog.show();
        }
		return false;
    }
    
    /**
     * Get the entered cheat code and check if valid.
     */
    private void handleCheatCode() {
    	boolean cheatCodeOk = false;

        EditText cheatCodeTextField = (EditText) mCheatcodeDialog.findViewById(R.id.cheatcodeinput);
    	
    	String cheatCode = cheatCodeTextField.getText().toString();
    	
		Toast.makeText(this, cheatCode,Toast.LENGTH_LONG).show();
    	
    	// depending on the date, check if the given code is valid
        Date currentDate = new Date();
        currentDate.getYear();
        currentDate.getMonth();
        currentDate.getDay();
        
    	if(cheatCodeOk) {
	        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	        SharedPreferences.Editor editor = settings.edit();
	        editor.putBoolean("checkCodeOK", true);
	        editor.commit();

	        // TODO restart activity or something
    	}
    	
    }
}
