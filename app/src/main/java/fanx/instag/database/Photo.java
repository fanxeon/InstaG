package fanx.instag.database;

import android.graphics.Bitmap;

public class Photo {

	public static final String PHOTO_ID = "photoid";
	public static final String ALBUM = "album";
	public static final String BITMAP_DATA = "bitmapdata";
	public static final String GRID_BITMAP_DATA = "gridbitmapdata";
	public static final String DESCRIPTION = "description";
	
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String NONE = "none";
	
	//private String name;
	//A photo ID is the same as the timestamp.
	//A date taken for the photo in String, and the value of this attribute has to be in a unified 
	//format e.g yyyyMMdd_HHmmss. For consistency, the value has to be from the photo manager.
	private String photoID;
	
	//A photo decription
	private String description;
	
	//An image data for a photo
	private Bitmap bitmap;
	
	private Bitmap gridbitmap;
	//Album name where a photo belongs to, if applicable. Otherwise, null
	private String album = NONE;
	
	private boolean isUploadedToServer = false;
	
	public Photo() {
	}	
	
	public Photo(String photoID, String description, Bitmap bitmap, Bitmap gridBitmap, boolean uploadedToServer) {
		//super();
		//this.name = name;
		this.description = description;
		this.photoID = photoID;
		this.bitmap = bitmap;
		this.isUploadedToServer = uploadedToServer;
		this.gridbitmap = gridBitmap;

	}

	public Photo(String photoID, String description, Bitmap bitmap, Bitmap gridBitmap, String album,
				 boolean uploadedToServer) {
		//super();
		//this.name = name;
		this.description = description;
		this.photoID = photoID;
		this.bitmap = bitmap;
		this.album = album;
		this.gridbitmap = gridBitmap;
		this.isUploadedToServer = uploadedToServer;
	}

	public String getPhotoID() {
		return photoID;
	}
	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public boolean isUploadedToServer() {
		return isUploadedToServer;
	}
	public String isUploadedToServerAsYesNO() {
		if(isUploadedToServer)
			return YES;
		
		return NO;
	}
	public void setUploadedToServer(boolean isUploadedToServer) {
		this.isUploadedToServer = isUploadedToServer;
	}
	
	public void setUploadedToServer(String isUploadedToServerStr) {
		if(isUploadedToServerStr.equalsIgnoreCase(YES))	{
			this.isUploadedToServer = true;
		}
		else {
			this.isUploadedToServer = false;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Bitmap getGridBitmap() {
		return gridbitmap;
	}

	public void setGridBitmap(Bitmap gridbitmap) {
		this.gridbitmap = gridbitmap;
	}

}
