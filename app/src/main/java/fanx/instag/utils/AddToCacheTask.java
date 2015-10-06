package fanx.instag.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;

import fanx.instag.database.DatabaseManager;
import fanx.instag.adapter.ImageCache;

public class AddToCacheTask extends AsyncTask<String,Void,Boolean> {

	//private final WeakReference<ImageView> imageViewReference;
	private String position = "";
	private Context context;
	private ImageCache cache;
	//private int[] images;
	int reqWidth = 300;
	int reqHeight = 300;
	private Bitmap bitmap = null;

	public AddToCacheTask(Context context, ImageCache cache, int reqWidth, int reqHeight)
	{
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
		this.context = context;
		this.cache = cache;
	}
	
//	public AddToCacheTask(Context context, ImageCache cache)
//	{
//		// Use a WeakReference to ensure the ImageView can be garbage collected
//		//imageViewReference = new WeakReference<ImageView>(imageView);
//		this.reqWidth = reqWidth;
//		this.reqHeight = reqHeight;
//		this.context = context;
//		this.cache = cache;
//	}

	public AddToCacheTask(Context context, ImageCache cache, Bitmap bitmap)
	{
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
//		this.reqWidth = reqWidth;
//		this.reqHeight = reqHeight;
		this.context = context;
		this.cache = cache;
		this.bitmap = bitmap;
	}
	
//	public AddToCacheTask(Context context, ImageCache cache, int reqWidth, int reqHeight) 
//	{
//		// Use a WeakReference to ensure the ImageView can be garbage collected
//		//imageViewReference = new WeakReference<ImageView>(imageView);
//		this.reqWidth = reqWidth;
//		this.reqHeight = reqHeight;
//		this.context = context;
//		this.cache = cache;
//	}

	//	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	//	{
	//		// Raw height and width of image
	//		final int height = options.outHeight;
	//		final int width = options.outWidth;
	//		int inSampleSize = 1;
	//
	//		if (height > reqHeight || width > reqWidth) {
	//
	//			final int halfHeight = height / 2;
	//			final int halfWidth = width / 2;
	//
	//			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
	//			// height and width larger than the requested height and width.
	//			while ((halfHeight / inSampleSize) > reqHeight
	//					&& (halfWidth / inSampleSize) > reqWidth) {
	//				inSampleSize *= 2;
	//			}
	//		}
	//
	//		return inSampleSize;
	//	}
	//
	//	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	//			int reqWidth, int reqHeight) {
	//
	//		// First decode with inJustDecodeBounds=true to check dimensions
	//		final BitmapFactory.Options options = new BitmapFactory.Options();
	//		options.inJustDecodeBounds = true;
	//		BitmapFactory.decodeResource(res, resId, options);
	//
	//		// Calculate inSampleSize
	//		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	//
	//		// Decode bitmap with inSampleSize set
	//		options.inJustDecodeBounds = false;
	//		return BitmapFactory.decodeResource(res, resId, options);
	//	}

	// Decode image in background.
	@Override
	protected Boolean doInBackground(String... params)
	{
		position = params[0];
		//images = context.getResources().getIntArray(R.array.ImgRef);
		//data = params[0];
		//return decodeSampledBitmapFromResource(params[0].getResources(), data, 100, 100);
		//System.out.println("Decoding resource into bitmap");
		//Bitmap bm = decodeSampledBitmapFromResource(context.getResources(),position,300,300);
		//System.out.println("Scaled down the bitmap to 300, 300 and is now of size "+ (bm.getByteCount()/1024));
		//		ByteArrayOutputStream os = new ByteArrayOutputStream();
		//		if(bm.compress(Bitmap.CompressFormat.JPEG, 100, os))
		//		{
		//			System.out.println("Compressed the bitmap down to JPEG");
		//			//Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), images[i]);
		//			String string = ""+position;CompressFormat
		//			System.out.println("Attempting to add bitmap to cache");
		//			byte[] array = os.toByteArray();
		//			System.out.println("The size of the bitmap is now "+(array.length/1024));
		byte[] array = null;
		if( bitmap == null)
		{
			array = DatabaseManager.getInstance(context).getGridBitmapAsBytes(position);
		}
		else
		{
			int bytes = bitmap.getByteCount();
			//or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
			//int bytes = b.getWidth()*b.getHeight()*4;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
			//ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
			//bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
			//array = buffer.array();
			array = os.toByteArray();
		}
		
		if (array != null)
		{
			System.out.println("Attempting to add bitmap to cache");
			synchronized(cache)
			{
				if(!cache.addBitmapToMemoryCache(position, array))
				{
					System.out.println("Failed to write bitmap with identifier "+position+ " to cache");
					return false;
				}
				return true;
			}
		}
		else
		{
			System.out.println("Grid Bitmap Bytes returned from database is null");
			return false;
		}
	}

	//	    if (imageViewReference != null && bitmap != null) {
	//            final ImageView imageView = imageViewReference.get();
	//            if (imageView != null) {
	//                imageView.setImageBitmap(bitmap);
	//            }
	//        }
	//	     Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Boolean addedTocache)
	{
		
		if (addedTocache)
		{
			//added to cache, so do something
			System.out.println("Successfully added to cache");
		}
		else
		{
			//could not add to cache
			System.out.println("Failed to write object with key "+position+" to cache");
			//System.out.println("Failed to write bitmap with identifier "+images[position]+ " to cache");
		}
	}
}
