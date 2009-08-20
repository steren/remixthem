package fr.steren.remixthem;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class RemixThemGrid extends Activity {

	private File[] mPictureFiles;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setImageURIsForGallery();
        
        setContentView(R.layout.grid);

        GridView g = (GridView) findViewById(R.id.gallerygrid);
        g.setAdapter(new ImageAdapter(this));
        
        g.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent viewer = new Intent(Intent.ACTION_VIEW);
				viewer.setDataAndType( Uri.fromFile(mPictureFiles[position]), "image/png" );
	        	startActivity(viewer);  
			}
		}  );

        
    }

    private void setImageURIsForGallery() {
    	
        File dir = new File(Environment.getExternalStorageDirectory(), "RemixThem");

        // This filter only returns directories
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        mPictureFiles = dir.listFiles(fileFilter);

    }
        
    public class ImageAdapter extends BaseAdapter {
    	
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mPictureFiles.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setPadding(8, 8, 8, 8);
                //long clickable
                //imageView.setLongClickable(true);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap( BitmapFactory.decodeFile(mPictureFiles[position].getAbsolutePath()) );

            return imageView;
        }

        private Context mContext;

    }

}