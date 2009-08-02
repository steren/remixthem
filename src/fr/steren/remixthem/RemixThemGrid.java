package fr.steren.remixthem;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class RemixThemGrid extends Activity {

	private Bitmap[] mPictures;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setImageURIsForGallery();
        
        setContentView(R.layout.grid);

        GridView g = (GridView) findViewById(R.id.gallerygrid);
        g.setAdapter(new ImageAdapter(this));
    }

    private void setImageURIsForGallery() {
    	
        File dir = new File(Environment.getExternalStorageDirectory(), "RemixThem");

        // This filter only returns directories
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        File[] files = dir.listFiles(fileFilter);

        mPictures = new Bitmap[files.length];
        
        //get URIs
		for (int i = 0; i< files.length; i++) {
			mPictures[i] = BitmapFactory.decodeFile(files[i].getAbsolutePath());
		}
		
    }
    
    public class ImageAdapter extends BaseAdapter {
    	
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mPictures.length;
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
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(mPictures[position]);

            return imageView;
        }

        private Context mContext;

    }

}