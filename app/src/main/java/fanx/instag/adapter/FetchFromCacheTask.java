package fanx.instag.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import fanx.instag.database.DatabaseManager;

public class FetchFromCacheTask extends AsyncTask<String,Void,Bitmap> {

	//private final WeakReference<ImageView> imageViewReference;
	ImageView imageView;
	private String position;
	private Context context;
	private ImageCache cache;
	boolean setimage;

	public FetchFromCacheTask(ImageView imageView, Context context, ImageCache cache) {

		this.imageView = imageView;
		this.context = context;
		this.cache = cache;
		position = "";
	}
	
	public FetchFromCacheTask(ImageView imageView, Context context, FragmentManager fm) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
		this.imageView = imageView;
		this.context = context;
		cache = ImageCache.getInstance(fm);
		position = "";
	}
	
	// Decode image in background.
	@Override
	protected Bitmap doInBackground(String... params)
	{
		position = params[0];
		synchronized(cache)
		{
			Bitmap bm = cache.getBitmapFromMemCache(position);
			return bm;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap){
		//		if (imageViewReference != null && bitmap != null) {
		//final ImageView imageView = imageViewReference.get();
		if (bitmap != null)	{
			imageView.setImageBitmap(bitmap);
		}
		else{
			DatabaseManager db = DatabaseManager.getInstance(context);
			byte[] array = db.getGridBitmapAsBytes(position);
			//byte[] array = DatabaseManager.getInstance(context).getGridBitmapAsBytes(position);
			if (array != null)	{
				System.out.println("Attempting to add bitmap to cache");
				synchronized(cache){
					if(!cache.addBitmapToMemoryCache(position, array)){
						System.out.println("Failed to write bitmap with identifier "+position+ " to cache");
					}
				}
				imageView.setImageBitmap(db.getGridBitmap(position));
			}
			else{
				System.out.println("Grid Image as bytes returned null, Could not update the image view");
			}
		}
		//		     Once complete, see if ImageView is still around and set bitmap
	}
		
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

}