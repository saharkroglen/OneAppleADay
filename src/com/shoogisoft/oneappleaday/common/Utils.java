package com.shoogisoft.oneappleaday.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import org.codehaus.jackson.map.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;
import com.shoogisoft.oneappleaday.receivers.SyncReceiver;
import com.shoogisoft.oneappleaday.R;
import com.splunk.mint.Mint;
//import android.R;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


public class Utils {
	
	public enum enShareButton
	{
		top,
		side
	}
	private static final String INTERNAL_MESSAGE_EXTRA = "internal_message_extra";
	private static final String DISABLED_CONTROL_COLOR = "#C0C0C0";
	private static ObjectMapper mMapper;
	public static final String FONT_OSWALD_REGULAR = "Oswald_Regular.ttf";
	public static String TAG = Constants.TAG;
	public static final String INTERNAL_MESSAGE_INTENT = "internal_message_intent";


	public static void initBugSense(Context c) {
		Mint.initAndStartSession(c, "731be9c5");
	}
	/**
	 * generates an average color out of a given bitmap. For example, google
	 * icon have mostly blue and some white colors. this method will return a
	 * light blue as return
	 * 
	 * @param bitmap
	 *            image to analyze
	 * @return Color representing the color average value of the given bitmap
	 */
	public static int getBitmapColorAverage(Bitmap bitmap) {
		int redBucket = 0;
		int greenBucket = 0;
		int blueBucket = 0;
		int pixelCount = 0;

		for (int y = 0; y < bitmap.getHeight(); y++) {
			for (int x = 0; x < bitmap.getWidth(); x++) {
				int c = bitmap.getPixel(x, y);

				pixelCount++;
				redBucket += Color.red(c);
				greenBucket += Color.green(c);
				blueBucket += Color.blue(c);
				// does alpha matter?
			}
		}

		int averageColor = Color.rgb(redBucket / pixelCount, greenBucket
				/ pixelCount, blueBucket / pixelCount);
		return averageColor;
	}
	
	public static int adjustColorAlpha(int color, int alpha) {
//	    int alpha = Math.round(Color.alpha(color) * factor);
	    int red = Color.red(color);
	    int green = Color.green(color);
	    int blue = Color.blue(color);
	    return Color.argb(alpha, red, green, blue);
	}
	
	@SuppressLint("NewApi")
	public static void setNotificationBarColor(int bitmapColorAverage,Activity act) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {					
			Window window = act.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(Utils.adjustColorAlpha(bitmapColorAverage,200));
		}
	}

	public static String getSingleLineTitle(PersistentItem itemInPosition) {
		return itemInPosition.Title.replace("\n", " ");
	}
	public static DisplayImageOptions getImageLoaderOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.no_media)
		.showImageOnFail(0)
		.showImageOnLoading(R.drawable.no_media_loading)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.cacheInMemory(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(800))
		.build();
		return options;
	}
	 /**
     * Returns the number of days between two dates. The time part of the
     * days is ignored in this calculation, so 2007-01-01 13:00 and 2007-01-02 05:00
     * have one day inbetween.
     */
    public static long daysBetween(Date date1, Date date2) {
    	 try {
             
             long diff = Math.abs(date1.getTime() - date2.getTime());
             long diffDays = diff / (24 * 60 * 60 * 1000); 
             return diffDays ;

         } catch (Exception e1) {
             System.out.println("exception " + e1); 
             return -1;
         }
    }

	public static ArrayList<PersistentItem> filterNewItems(List<PersistentItem> items) {	
		if (items == null)
			return null;
		ArrayList<PersistentItem> filteredList = new ArrayList<PersistentItem>();
		
		for(PersistentItem item: items)
		{
			if (!item.IsNew ){ 
				filteredList.add(item);
			}	
			
		}
//		Utils.sortList(filteredList, false) ;
		return filteredList;
	}
	
	/**
	 * Get a specific share preference object.
	 * @param prefsName
	 * @return
	 */
	public static SharedPreferences getSharedPrefs(String prefsName)
	{
		if (prefsName == null)
			prefsName = Constants.SHARED_PREFS_GENERAL;
		return MyApplication.getContext().getSharedPreferences(prefsName,Context.MODE_PRIVATE);
	}
	
	/**
	 * get the default shared preferences object
	 * @return
	 */
	public static SharedPreferences getSharedPrefs()
	{
		return getSharedPrefs(Constants.SHARED_PREFS_GENERAL);
	}

	public static ArrayList<PersistentItem> sortList(
			ArrayList<PersistentItem> persistentItemList, final boolean ascending) {

		Collections.sort(persistentItemList, new Comparator<PersistentItem>() {
			// Overriding the compare method to sort the age
			public int compare(PersistentItem a, PersistentItem b) {
				if (ascending)
					return (a.ItemID - b.ItemID);
				else
					return (b.ItemID - a.ItemID);
			}
		});
		return persistentItemList;
	}


	public static boolean isFacebookAppInstalled()
	{
		try{
		    ApplicationInfo info = MyApplication.getContext().getPackageManager().
		            getApplicationInfo("com.facebook.katana", 0 );
		    return true;
		} catch( PackageManager.NameNotFoundException e ){
		    return false;
		}
	}
	
	public static void launchPackage(Context c,String packageName, String name) {
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		if (packageName != null && name != null)
			intent.setComponent(new ComponentName(packageName, name));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		c.startActivity(intent);
	}
	
	/**
	 * sends a message that stops activity sniffer therefore disables home blocker enforcement
	 * Rationale: don't enforce home blocker when you are sure that this activity is done by admin (Parent) and not user (Child)
	 */

	
	public static String getPackageName() {
		return MyApplication.getContext().getApplicationContext().getPackageName();
	}
	public static void startApplication(String packageName) {
		try {
			Intent intent = new Intent("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");

			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			List<ResolveInfo> resolveInfoList = MyApplication.getContext().getPackageManager()
					.queryIntentActivities(intent, 0);

			for (ResolveInfo info : resolveInfoList)
				if (info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
					launchComponent(info.activityInfo.packageName,
							info.activityInfo.name);
					return;
				}

			// No match, so application is not installed
			showInMarket(packageName);
		} catch (Exception e) {
			showInMarket(packageName);
		}
	}
	private static void showInMarket(String packageName) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id=" + packageName));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MyApplication.getContext().startActivity(intent);
	}
	private static void launchComponent(String packageName, String name) {
		Utils.launchPackage(MyApplication.getContext(), packageName, name);
	}
	public static void showToast(final Context c, final String message) {
		if (c instanceof Activity) {
			((Activity) c).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(c, message, Toast.LENGTH_LONG).show();
				}
			});
		}
	}
	public static void showToast(final Context c, int resid) {
		showToast(c, c.getString(resid));
	}

	public static void enableDisableView(View view, boolean enabled) {
		view.setEnabled(enabled);
		setEnablingTextViewStyle(view, enabled);

		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;

			for (int idx = 0; idx < group.getChildCount(); idx++) {
				enableDisableView(group.getChildAt(idx), enabled);
			}
		}
	}

	private static void setEnablingTextViewStyle(View view, boolean enabled) {
		if (view instanceof TextView) {
			TextView textViewToEnforceEnablingStyle = ((TextView) view);
			if (enabled && view.getTag() != null) {
				int colorToRestore = Integer
						.parseInt(textViewToEnforceEnablingStyle.getTag()
								.toString());
				textViewToEnforceEnablingStyle.setTextColor(colorToRestore);
			} else if ((!enabled && view.getTag() == null)
					|| (!enabled && view.getTag() != null && textViewToEnforceEnablingStyle
							.getCurrentTextColor() != Color
							.parseColor(DISABLED_CONTROL_COLOR))) {
				textViewToEnforceEnablingStyle
						.setTag(textViewToEnforceEnablingStyle
								.getCurrentTextColor()); // save current color
															// so we know how to
															// restore when
															// enabling
				textViewToEnforceEnablingStyle.setTextColor(Color
						.parseColor(DISABLED_CONTROL_COLOR));
			}
		}
	}

	public static boolean saveBitmapToFile(Bitmap bmp, String filename) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			bmp.compress(Bitmap.CompressFormat.PNG, 50, out);
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;
		}

	}
	
	public static File createImageFile(Context c) throws IOException {
		// Create an image file name
		if (!Utils.isExternalStorageWritable())
		{
			Log.e(Constants.TAG,"External storage is not writable, abort image file creation");
			return null;
		}
		
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "PNG_" + timeStamp + "_";
		//File storageDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + Utils.getMyAppName(this).replace(" ","")); //getExternalFilesDir(Environment.DIRECTORY_PICTURES);// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File storageDir = Utils.getAppImageStoragePath(c);
		if (!storageDir.exists())
			storageDir.mkdirs();
		File image = File.createTempFile(imageFileName, /* prefix */
				".png", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
//		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}
	public static File getAppImageStoragePath(Context c) {
		return new File(String.format("%s/.%s", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),Utils.getMyAppName(c).replace(" ","")));
	}
	
	class SavePhotoByteArrayTask extends AsyncTask<byte[], Void, Void> {
		private String mPath;
		public SavePhotoByteArrayTask(String filePath)
		{
			mPath = filePath;
		}
	    @Override
	    protected Void doInBackground(byte[]... byteArray) {
	      File photo=new File(mPath);

	      if (photo.exists()) {
	            photo.delete();
	      }

	      try {
	        FileOutputStream fos=new FileOutputStream(photo.getPath());

	        fos.write(byteArray[0]);
	        fos.close();
	      }
	      catch (java.io.IOException e) {
	        Log.e("PictureDemo", "Exception in photoCallback", e);
	      }

	      return(null);
	    }
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	public static Bitmap getBitmapFromAsset(Context context, String strName) {
		AssetManager assetManager = context.getAssets();

		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(strName);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			return null;
		}

		return bitmap;
	}


	public static String GetProfileExternalDirectory(String profileName,
			String subDirectory) {
		String profileDirectoryPath;
		if (subDirectory == null)
			profileDirectoryPath = String.format("%s%s%s",
					Constants.EXTERNAL_APP_DIRECTORY_FULL_PATH, File.separator,
					profileName);
		else
			profileDirectoryPath = String.format("%s%s%s%s%s",
					Constants.EXTERNAL_APP_DIRECTORY_FULL_PATH, File.separator,
					profileName, File.separator, subDirectory);
		File f = new File(profileDirectoryPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		return profileDirectoryPath;
	}

	public static String GetFileName(String fullPath,boolean withExtension) {
		String[] pathSeparation = splitPath(fullPath);
		String fileName = pathSeparation[pathSeparation.length - 1];
		if (withExtension)
			return fileName;
		else
		{
			int indexOfFileExtension = fileName.lastIndexOf('.');
			if (indexOfFileExtension != -1) {
				return fileName.substring(0, indexOfFileExtension);
			}
			return fileName;
		}
	}

	private static String[] splitPath(String fullPath) {
		String[] pathSeparation = fullPath.split(File.separator);
		return pathSeparation;
	}

	public static String GetContainingDirectoryName(String fullPath) {
		String[] pathSeparation = splitPath(fullPath);

		if (pathSeparation.length > 1)
			return pathSeparation[pathSeparation.length - 2];
		else
			return "N/A";
	}

	public static String GetContainingFolderShortenPath(String fullPath) {
		String[] pathSeparation = splitPath(fullPath);

		if (pathSeparation.length > 1)
			return String.format("%s%s%s",
					pathSeparation[pathSeparation.length - 2], File.separator,
					pathSeparation[pathSeparation.length - 1]);
		else
			return "N/A";
	}

	public static String GetFilePath(String fullFilePath) {
		int indexOfFileSeperator = fullFilePath.lastIndexOf(File.separator);
		if (indexOfFileSeperator != -1) {
			return fullFilePath.substring(0, indexOfFileSeperator);
		}
		return "N/A";
	}


	public static boolean CreateDirectory(String path) {
		boolean result = false;
		try {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static void writeToFile(Context c, final String content,
			String filePath) {

		try {
			// String s = c.getFilesDir().getPath();
			FileWriter out = new FileWriter(new File(filePath));
			out.write(content);
			out.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	// public static void serializeTofile(Context c, byte[] content, String
	// filePath) {
	public static void serializeTofile(Context c, Object obj, String filePath) {

		try {
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(obj);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> T deserializeObjectFromFile(Context c, String filePath,
			Class<T> type) {

		T simpleClass = null;
		try {
			FileInputStream fis = new FileInputStream(new File(filePath));
			ObjectInputStream is = new ObjectInputStream(fis);
			simpleClass = type.cast(is.readObject());
			is.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return simpleClass;
	}

	@SuppressWarnings("resource")
	public static String readFromFile(Context c, String filePath) {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(new File(filePath)));
			while ((line = in.readLine()) != null)
				stringBuilder.append(line);

		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return stringBuilder.toString();
	}
	public static Bitmap safeBitmapCopy(Bitmap sourceBitmap, boolean rotate,
			int angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		Bitmap copiedBitmap = null;

		int compressionPercent = 100;

		while ((copiedBitmap == null) && compressionPercent > 20) {
			Log.i(Utils.TAG, "Fixing compression rate:"
					+ compressionPercent);
			try {
				if (compressionPercent != 100) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					sourceBitmap.compress(CompressFormat.JPEG,
							compressionPercent, bos);

					Bitmap oldBitmap = sourceBitmap;

					sourceBitmap = BitmapFactory
							.decodeStream(new ByteArrayInputStream(bos
									.toByteArray()));

					oldBitmap.recycle();
					System.gc();
				}

				if (rotate) {
					copiedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
							sourceBitmap.getWidth(),
							sourceBitmap.getHeight(), matrix, false);
				} else {
					copiedBitmap = sourceBitmap.copy(Config.ARGB_8888,
							false);
				}
			} catch (OutOfMemoryError e) {
				Log.e(Utils.TAG,
						"Out of memory exception creating compressed picture");
				compressionPercent -= 5;
			}
		}

		return copiedBitmap;
	}


	public static void HideKeyboard(Activity activity, View control) {
		// InputMethodManager imm = (InputMethodManager) c
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(control.getWindowToken(), 0);

		activity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && control != null) {
			imm.hideSoftInputFromWindow(control.getWindowToken(), 0);
		}
	}

	public static Bitmap getCircularBitmap(Bitmap bitmap) {
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final Bitmap outputBitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		final Canvas canvas = new Canvas(outputBitmap);
		final int color = 0xffff0000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);

		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) 4);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return outputBitmap;
	}

	public static Bitmap replaceToSingleColorPreserveTransparency(Bitmap src,
			int targetColor) {
		if (src == null) {
			return null;
		}

		int width = src.getWidth();
		int height = src.getHeight();
		int[] pixels = new int[width * height];
		int A;
		src.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int x = 0; x < pixels.length; ++x) {
			int pixel = pixels[x];
			A = Color.alpha(pixel);

			pixels[x] = Color.argb(A, Color.red(targetColor),
					Color.green(targetColor), Color.blue(targetColor));
		}

		Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		// src.recycle();
		return result;
	}

	// public static Bitmap fillColorBetweenBlackLines(Point touchPoint,
	// Bitmap src, int targetColor) {
	// if (src == null) {
	// return null;
	// }
	//
	// int width = src.getWidth();
	// int height = src.getHeight();
	// Bitmap result = Bitmap.createBitmap(width, height,
	// Bitmap.Config.ARGB_8888);
	// HashSet<String> visitedPoints = new HashSet<String>();
	// // int[] pixels = new int[width * height];
	// BlockingQueue<Point> pointsToCheck = new LinkedBlockingDeque<Point>();
	//
	// pointsToCheck.add(touchPoint);
	// visitedPoints.add(touchPoint.toString());
	//
	// Point p = null;
	// while (!pointsToCheck.isEmpty())
	// {
	//
	// try {
	// p = pointsToCheck.take();
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// int pixel = src.getPixel(p.x, p.y);
	// int red = Color.red(pixel);
	// int green = Color.green(pixel);
	// int blue = Color.blue(pixel);
	//
	// float Y = (float) ((0.2126*red + 0.7152*green + 0.0722*blue) / 255);
	// boolean white = (Y > .95);
	// boolean black = (Y < .05);
	// // if (!black && !white)
	// // {
	// // boolean isColor = true;
	// // break;
	// // }
	// if (white)
	// {
	// result.setPixel(p.x,p.y,targetColor);
	// addToPotentialPoints(visitedPoints, pointsToCheck,new Point(p.x-1,p.y));
	// addToPotentialPoints(visitedPoints, pointsToCheck,new
	// Point(p.x-1,p.y-1));
	// addToPotentialPoints(visitedPoints, pointsToCheck,new
	// Point(p.x-1,p.y+1));
	// addToPotentialPoints(visitedPoints, pointsToCheck,new
	// Point(p.x+1,p.y-1));
	// addToPotentialPoints(visitedPoints, pointsToCheck,new Point(p.x+1,p.y));
	// addToPotentialPoints(visitedPoints, pointsToCheck,new
	// Point(p.x+1,p.y+1));
	// addToPotentialPoints(visitedPoints, pointsToCheck,new Point(p.x,p.y-1));
	// addToPotentialPoints(visitedPoints, pointsToCheck,new Point(p.x,p.y+1));
	// }
	// if (black)
	// Log.v(Constants.TAG,String.format("black point found : %s",p.toString()));
	//
	// }
	// int k = 0;
	// // for (int j = 0; j < src.getHeight(); j++) {
	// // for (int i = 0; i < src.getWidth(); i++, k++) {
	//
	// int pixel = src.getPixel(i, j);
	// int red = Color.red(pixel);
	// int green = Color.green(pixel);
	// int blue = Color.blue(pixel);
	//
	// float Y = (float) ((0.2126*red + 0.7152*green + 0.0722*blue) / 255);
	// boolean white = (Y > .95);
	// boolean black = (Y < .05);
	// // if (!black && !white)
	// // {
	// // boolean isColor = true;
	// // break;
	// // }
	// if (white)
	// {
	// result.setPixel(i,j,targetColor);
	// }
	//
	// // }
	// // }

	// result.setPixels(pixels, 0, width, 0, 0, width, height);
	// src.recycle();
	// return result;
	// }

	public static Bitmap floodFill(Bitmap imageSrc, Point node,
			int replacementColor, boolean cancelIfGotToImageBound,
			boolean ignoreBlackDots) {
		Bitmap image = imageSrc.copy(Bitmap.Config.ARGB_8888, true);
		int width = image.getWidth();
		int height = image.getHeight();
		if (node.x > width || node.y > height) {
			Log.w(Constants.TAG,
					"Can't fill shape, touch event is beyond image bounds");
			return null;
		}
		int tappedPixel = image.getPixel(node.x, node.y);

		if (ignoreBlackDots && isBlackColor(tappedPixel)) {
			Log.v(TAG, "Ignore tap on black pixel");
			return null;
		}
		int replacement = replacementColor;
		if (tappedPixel != replacement) {
			Queue<Point> queue = new LinkedList<Point>();
			do {
				int x = node.x;
				int y = node.y;
				if (cancelIfGotToImageBound) {
					if (x == 0 || y == 0 || x == image.getWidth()
							|| y == image.getHeight())
						return null;

				}
				while (x > 0 && image.getPixel(x - 1, y) == tappedPixel) {
					x--;
				}
				boolean spanUp = false;
				boolean spanDown = false;
				while (x < width && image.getPixel(x, y) == tappedPixel) {
					image.setPixel(x, y, replacement);
					if (!spanUp && y > 0
							&& image.getPixel(x, y - 1) == tappedPixel) {
						queue.add(new Point(x, y - 1));
						spanUp = true;
					} else if (spanUp && y > 0
							&& image.getPixel(x, y - 1) != tappedPixel) {
						spanUp = false;
					}
					if (!spanDown && y < height - 1
							&& image.getPixel(x, y + 1) == tappedPixel) {
						queue.add(new Point(x, y + 1));
						spanDown = true;
					} else if (spanDown && y < height - 1
							&& image.getPixel(x, y + 1) != tappedPixel) {
						spanDown = false;
					}
					x++;
				}
			} while ((node = queue.poll()) != null);
		}
		return image;
	}

	public static boolean isBlackColor(int color) {
		if (color == Color.TRANSPARENT)
			return false;
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);

		float Y = (float) ((0.2126 * red + 0.7152 * green + 0.0722 * blue) / 255);
		boolean isWhite = (Y > .95);
		boolean isBlack = (Y < .05);
		return isBlack;
	}

	// private static void addToPotentialPoints(HashSet<String> visitedPoints,
	// BlockingQueue<Point> pointsToCheck, Point p) {
	//
	// if (visitedPoints.contains(p.toString()))
	// {
	// Log.v(Constants.TAG,String.format("point already visited : %s",p.toString()));
	// }
	// else
	// {
	// visitedPoints.add(p.toString());
	// pointsToCheck.add(p);
	// Log.v(Constants.TAG,String.format("num of points to check : %s",pointsToCheck.size()));
	// }
	// }

	public static Bitmap replaceColor(Bitmap src, int fromColor, int targetColor) {
		if (src == null) {
			return null;
		}
		// Source image size
		int width = src.getWidth();
		int height = src.getHeight();
		int[] pixels = new int[width * height];
		int A, R, G, B;
		int fromA, fromR, fromG, fromB;
		// get pixels
		src.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int x = 0; x < pixels.length; ++x) {
			int pixel = pixels[x];
			A = Color.alpha(pixel);
			R = Color.red(pixel);
			G = Color.green(pixel);
			B = Color.blue(pixel);

			fromA = A;
			fromR = Color.red(fromColor);
			fromG = Color.green(fromColor);
			fromB = Color.blue(fromColor);
			if (Color.argb(A, R, G, B) == Color.argb(A, fromG, fromG, fromB))
				pixels[x] = Color.argb(A, Color.red(targetColor),
						Color.green(targetColor), Color.blue(targetColor));

		}
		// create result bitmap output
		Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
		// set pixels
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		src.recycle();
		return result;
	}

	/*
	 * Built this method according to following article guidelines and hints
	 * http
	 * ://developer.samsung.com/android/technical-docs/How-to-retrieve-the-Device
	 * -Unique-ID-from-android-device
	 */
	public static String getUniqueID(Context context) {
		String uniqueID = null;

		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			String serialID = (String) (get.invoke(c, "ro.serialno", "unknown"));

			if (!serialID.equals("unknown")) {
				uniqueID = String.format("si_%s", serialID); // si stands for
																// android
																// serial id
			} else // try another method if failed
			{
				String androidID = Settings.Secure.getString(
						context.getContentResolver(),
						Settings.Secure.ANDROID_ID);
				if (androidID == null)
					throw new Exception(
							"all unique id methods failed on this device therefore generating random id");
				else
					uniqueID = String.format("ai_%s", androidID); // ai stands
																	// for
																	// android
																	// id
			}
		} catch (Exception ignored) // generate random id if all others failed
		{
			uniqueID = String.format("ri_%s", UUID.randomUUID());// ri stands
																	// for
																	// random id
		}
		return uniqueID;
	}

	private static ActivityManager mActivityManager = null;

	public static String getTopMostActivityPackageName(Context c) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) c
					.getSystemService(Context.ACTIVITY_SERVICE);
		}
		List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager
				.getRunningTasks(1);
		String topMostActivityPackageName = taskInfo.get(0).topActivity
				.getPackageName();
		return topMostActivityPackageName;
	}

	public static byte[] serializeObject(Object o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(o);
			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();

			return buf;
		} catch (IOException ioe) {
			Log.e("serializeObject", "error", ioe);

			return null;
		}
	}

	public static Object deserializeObject(byte[] b) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(b));
			Object object = in.readObject();
			in.close();

			return object;
		} catch (ClassNotFoundException cnfe) {
			Log.e("deserializeObject", "class not found error", cnfe);

			return null;
		} catch (IOException ioe) {
			Log.e("deserializeObject", "io error", ioe);

			return null;
		}
	}

	
//
//	public static void sortGallerySelectedAreTopSecondSortByModifiedDate(
//			ArrayList<CustomGallery> galleryList) {
//		// show newest photo at beginning of the list
//		Collections.sort(galleryList, new Comparator<CustomGallery>() {
//
//			// Overriding the compare method to sort the age
//			public int compare(CustomGallery a, CustomGallery b) {
//				if (a == null || b== null)
//					return 0;
//				Boolean b1 = a.isSeleted;
//				Boolean b2 = b.isSeleted;
//				if (b1 && !b2) {
//					return -1;
//				}
//				if (!b1 && b2) {
//					return +1;
//				}
//				return (b.modifiedDate.compareTo(a.modifiedDate)); //second sorting by modified date
//			}
//		});
//	}

//	public static void sortGalleryByName(ArrayList<CustomGallery> galleryList) {
//		// show newest photo at beginning of the list
//		Collections.sort(galleryList, new Comparator<CustomGallery>() {
//
//			// Overriding the compare method to sort the age
//			public int compare(CustomGallery a, CustomGallery b) {
//				return (Utils.GetFileName(a.sdcardPath).compareTo(Utils
//						.GetFileName(b.sdcardPath)));
//			}
//		});
//	}

		/*
	 * public static interface IButtonClick{ void onClick(); }
	 */

	private static Animation animClickImageViewButton = null;

	

	public static String FormatTimeFromMillis(long timeInMillis) {
		long timeInSec = timeInMillis / 1000;
		long minutes = timeInSec / 60;
		long seconds = timeInSec % 60;
		return String.format("%d:%02d", minutes, seconds);

	}

	public static boolean isPackageExist(String packageName)
	{
		ApplicationInfo ai;
		PackageManager pm = MyApplication.getContext().getPackageManager();
		try {
		    ai = pm.getApplicationInfo(packageName, 0);
		    return true;
		} catch (final NameNotFoundException e) {
		    return false;
		}
	}
	
	public static String getMyAppName(Context c)
	{		
		return getAppNameByPackage(c.getPackageName());
	}
	public static String getAppNameByPackage(String packageName)
	{
		ApplicationInfo ai;
		PackageManager pm = MyApplication.getContext().getPackageManager();
		try {
		    ai = pm.getApplicationInfo(packageName, 0);
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : null);
		return applicationName;
	}


	public static void setCustomFont(TextView textView)
	{
		Context c =  MyApplication.getContext();
		Locale deviceLocale = c.getResources().getConfiguration().locale;
		if (deviceLocale.getCountry().toLowerCase().equals("il") || deviceLocale.getLanguage().toLowerCase().equals("iw"))
			Utils.setCustomFontToTextView(c, textView,"hebfont.ttf");
		else
			Utils.setCustomFontToTextView(c, textView,"LuckiestGuy.ttf");
	}
	
//	public static void setCustomFontToViewGroup(View view, String fontName) {
//		if (view instanceof ViewGroup) {
//			ViewGroup group = (ViewGroup) view;
//
//			for (int idx = 0; idx < group.getChildCount(); idx++) {
//				setCustomFontToViewGroup(group.getChildAt(idx), fontName) ;
//			}
//		}
//		else if (view instanceof TextView) {
//			Utils.setCustomFontToTextView(view.getContext(),(TextView) view,fontName);
//		}
//	}
	public static void setCustomFontToTextView(Context c, TextView v,
			String fontName) {
		// Font path
		String fontPath = String.format("fonts/%s", fontName);
		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(c.getAssets(), fontPath.toLowerCase());
		v.setTypeface(tf);
	}
	
	
	public static void showSpinner(Context c, boolean show,spinnerType type) {
		Utils.showSpinner(c,show,null,type);
	}
	public enum spinnerType
	{
		smallWithTextAndBackground,
		largeWithoutBackground
	}
	public static void showSpinner(Context c, boolean show,String spinnerText,spinnerType type) {
		switch (type)
		{
		case largeWithoutBackground:
			sendMessage(c,new InternalMessage(InternalMessage.MESSAGE_SHOW_SPINNER_NO_BACKGROUND,String.valueOf(show),spinnerText));
			break;
		case smallWithTextAndBackground:
			sendMessage(c,new InternalMessage(InternalMessage.MESSAGE_SHOW_SPINNER_WITH_TEXT,String.valueOf(show),spinnerText));	
			break;
		}
		
	}
	
	public static void sendMessage(Context c, InternalMessage msg) {
//		Log.d(Constants.TAG, "Broadcasting message");
		Intent intent = new Intent(INTERNAL_MESSAGE_INTENT);
		// You can also include some extra data.
		intent.putExtra(INTERNAL_MESSAGE_EXTRA, msg);
		LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
	}
	public static InternalMessage getMessageFromIntent(Intent i) {		
		return i.getExtras().getParcelable(INTERNAL_MESSAGE_EXTRA);		
	}
	
	public static void dismissNotification(Context ctx, int notifyId) {
	    String ns = Context.NOTIFICATION_SERVICE;
	    NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
	    nMgr.cancel(notifyId);
	}
	public static ObjectMapper getMapper() {
		if (mMapper == null)
		{
			mMapper = new ObjectMapper();			
		}
		return mMapper;
	}
	
	// To animate view slide out from left to right
	public static void slideToRight(View view, int visibility, int durationMillis,boolean fillAfter,float fromXDelta,float  toXDelta) {
		if (visibility == View.VISIBLE)
			view.setVisibility(View.VISIBLE);
		TranslateAnimation animate = new TranslateAnimation(fromXDelta, toXDelta,0, 0);
		animate.setDuration(durationMillis);
		animate.setFillAfter(fillAfter);
		view.startAnimation(animate);
		if (visibility != View.VISIBLE)
			view.setVisibility(visibility);
	}

	// To animate view slide out from right to left
	public static void slideToLeft(View view, int visibility, int durationMillis,boolean fillAfter,float fromXDelta,float  toXDelta) {
//		if (visibility == View.VISIBLE)
//			view.setVisibility(View.VISIBLE);
		TranslateAnimation animate = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
		animate.setDuration(durationMillis);
		animate.setFillAfter(fillAfter);
		view.startAnimation(animate);
//		if (visibility != View.VISIBLE)
			view.setVisibility(visibility);
	}

	// To animate view slide out from top to bottom
	public static void slideToBottom(View view, int visibility, int durationMillis,boolean fillAfter,float  fromYDelta,float  toYDelta) {
		if (visibility == View.VISIBLE)
			view.setVisibility(View.VISIBLE);
		TranslateAnimation animate = new TranslateAnimation(0, 0, fromYDelta,  toYDelta);
		animate.setDuration(durationMillis);
		animate.setFillAfter(fillAfter);
		view.startAnimation(animate);
		if (visibility != View.VISIBLE)
			view.setVisibility(visibility);
	}

	// To animate view slide out from bottom to top
	public static void slideToTop(View view, int visibility, int durationMillis,boolean fillAfter,float  fromYDelta,float  toYDelta) {
		if (visibility == View.VISIBLE)
			view.setVisibility(View.VISIBLE);
		TranslateAnimation animate = new TranslateAnimation(0, 0, fromYDelta,  toYDelta);
		animate.setDuration(durationMillis);
		animate.setFillAfter(fillAfter);
		view.startAnimation(animate);
		if (visibility != View.VISIBLE)
			view.setVisibility(visibility);
	}
	
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
