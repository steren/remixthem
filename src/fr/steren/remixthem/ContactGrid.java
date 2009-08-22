package fr.steren.remixthem;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class ContactGrid extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.grid);

        // Get a cursor with all people
        Cursor c = getContentResolver().query(People.CONTENT_URI, null, null, null, null);
        startManagingCursor(c);
        
        ListAdapter adapter = new SimpleCursorAdapter(this,
        // Use a template that displays a text view
                android.R.layout.simple_gallery_item,
                // Give the cursor to the list adatper
                c,
                // Map the NAME column in the people database to...
                new String[] {People.NAME},
                // The "text1" view defined in the XML template
                new int[] { android.R.id.text1 });
        
        
        GridView g = (GridView) findViewById(R.id.gallerygrid);
        g.setAdapter(adapter);
        
        /*g.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent viewer = new Intent(Intent.ACTION_VIEW);
				viewer.setDataAndType( Uri.fromFile(mPictureFiles[position]), "image/png" );
	        	startActivity(viewer);  
			}
		}  );*/

        
    }

}