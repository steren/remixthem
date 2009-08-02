package fr.steren.remixthem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class RemixThem extends Activity implements View.OnClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	
        View b = (View) findViewById(R.id.remix_button);
        b.setOnClickListener(this);

        b = (View) findViewById(R.id.mix_button);
        b.setOnClickListener(this);

        b = (View) findViewById(R.id.gallery_button);
        b.setOnClickListener(this);

    	
    	
    }

	
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_main, menu);
    	return true;
    }
    
    
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.remix_button:
            	intent = new Intent(this, RemixThemEditor.class);
                startActivity(intent);
                break;
            case R.id.mix_button:
            	intent = new Intent(this, RemixThemEditor.class);
                startActivity(intent);
                break;
            case R.id.gallery_button:
            	intent = new Intent(this, RemixThemGrid.class);
                startActivity(intent);
                break;
        }
    }
	
}
