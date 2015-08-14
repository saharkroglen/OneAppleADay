package com.shoogisoft.oneappleaday.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shoogisoft.oneappleaday.MainActivity;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.General;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.ItemView;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;
import com.shoogisoft.oneappleaday.receivers.SyncReceiver;
import com.shoogisoft.oneappleaday.R;

public class NotificationHelper {

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public void newItemArrived(PersistentItem item) {
		
		new CreateNotification(item).execute();
		
	}

	public void newItemArrived1(Context c, String longContent,
			String shortContent, String title, String body, String imageUrl) {

		Intent openIntent = new Intent(c, MainActivity.class);
		openIntent.setAction(Constants.ACTION_OPEN_FIELD);
		openIntent.putExtra(Constants.EXTRA_FIELD_ID,
				SyncReceiver.FieldObjectID);

		Intent snoozeIntent = new Intent(c, MainActivity.class);
		snoozeIntent.setAction(Constants.ACTION_SNOOZE_ITEM);
		snoozeIntent.putExtra(Constants.EXTRA_ITEM_TITLE, title);
		snoozeIntent.putExtra(Constants.EXTRA_ITEM_BODY, body);

		PendingIntent openPendingIntent = PendingIntent.getActivity(c, 0,
				openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		PendingIntent snoozePendingIntent = PendingIntent.getActivity(c, 0,
				snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Constructs the Builder object.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(c)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(
						c.getResources().getString(R.string.notification_title))
				.setContentText(shortContent)
				.setDefaults(Notification.DEFAULT_ALL)
				// requires VIBRATE permission

				// Sets the big view "big text" style and supplies
				.setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(longContent))
				.addAction(0/* R.drawable.open */,
						c.getResources().getString(R.string.btn_open),
						openPendingIntent)
				.addAction(0/* R.drawable.snooze */,
						c.getResources().getString(R.string.btn_snooze),
						snoozePendingIntent);

		RemoteViews mContentView = new RemoteViews(c.getPackageName(),
				R.layout.notification);
		mContentView.setImageViewUri(R.id.notifimage, Uri.parse(imageUrl));
		// mContentView.setImageViewResource(R.id.notifimage, R.drawable.logo);
		mContentView.setTextViewText(R.id.notiftitle, "Custom notification");
		mContentView.setTextViewText(R.id.notiftext, "This is a custom layout");
		builder.setContent(mContentView);

		builder.setContentIntent(openPendingIntent);
		NotificationManager notificationManager = (NotificationManager) c
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(
				Constants.ITEM_ARRIVED_NOTIFICATION_MESSAGE_ID, notification);
	}

	/**
	 * Notification AsyncTask to create and return the requested notification.
	 *
	 * @see CreateNotification#CreateNotification(int)
	 */
	public class CreateNotification extends AsyncTask<Void, Void, Void> {

		
		private Context mContext;
		private String mLongContent;
		private String mShortContent;
		private String mTitle;
		private String mBody;
		private String mImageUrl;
		private int mItemID;

		/**
		 * Main constructor for AsyncTask that accepts the parameters below.
		 *
		 * @param style
		 *            {@link #NORMAL}, {@link #BIG_TEXT_STYLE},
		 *            {@link #BIG_PICTURE_STYLE}, {@link #INBOX_STYLE}
		 * @see #doInBackground
		 */
		public CreateNotification(PersistentItem item) {
			mContext = MyApplication.getContext();
			mLongContent = String.format("%s\n%s", item.Title, item.Body); 
			mShortContent = item.Title;
			mTitle = item.Title;
			mBody = item.Body;
			mImageUrl = item.ImageUrl;
			mItemID = item.ItemID;
			GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, ItemView.CATEGORY, ItemView.PUSH_NOTIFICATION,String.valueOf(mItemID));
		}

		/**
		 * Creates the notification object.
		 *
		 * @see #setNormalNotification
		 * @see #setBigTextStyleNotification
		 * @see #setBigPictureStyleNotification
		 * @see #setInboxStyleNotification
		 */
		

		@Override
		protected Void doInBackground(Void... arg0) {
			//Notification noti = setBigPictureNotificationView();
			Notification noti = setCustomViewNotification();
			
			noti.defaults |= Notification.DEFAULT_LIGHTS;
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;

            noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            NotificationManager notificationManager = (NotificationManager) mContext
    				.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, noti);
			return null;
		}

		private Notification setCustomViewNotification() {

			Bitmap remote_picture = imageLoader.loadImageSync(mImageUrl,
					Utils.getImageLoaderOptions());
	        // Creates an explicit intent for an ResultActivity to receive.
	        Intent resultIntent = new Intent(mContext, MainActivity.class);
	        resultIntent.putExtra(Constants.EXTRA_NOTIFICATION_ITEM_ID,mItemID);

	        // This ensures that the back button follows the recommended convention for the back key.
	        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

	        // Adds the back stack for the Intent (but not the Intent itself)
	        stackBuilder.addParentStack(MainActivity.class);

	        // Adds the Intent that starts the Activity to the top of the stack.
	        stackBuilder.addNextIntent(resultIntent);
	        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

	        // Create remote view and set bigContentView.
	        RemoteViews expandedView = new RemoteViews(mContext.getPackageName(), R.layout.notification_custom_remote);
	        expandedView.setTextViewText(R.id.txtItemTitle, mTitle);
	        expandedView.setImageViewBitmap(R.id.imgNotificationImage, remote_picture);

	        Notification notification = new NotificationCompat.Builder(mContext)
			        .setSmallIcon(R.drawable.ic_launcher)
					.setAutoCancel(true)
	                .setContentIntent(resultPendingIntent)
	                .setContentTitle(mContext.getResources().getString(R.string.notification_title))
					.setContentText(mTitle).build();

	        notification.bigContentView = expandedView;

	        return notification;
	    }
		
		private Notification setBigPictureNotificationView() {
	//			newItemArrived(mContext,mLongContent,mShortContent,mTitle,mBody,mImageUrl);
				Bitmap remote_picture = null;
	
				// Create the style object with BigPictureStyle subclass.
				NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
				notiStyle.setBigContentTitle(mContext.getResources().getString(R.string.notification_title));
				notiStyle.setSummaryText(mTitle);
	
				try {
					remote_picture = imageLoader.loadImageSync(mImageUrl,
							Utils.getImageLoaderOptions());
					// remote_picture = BitmapFactory.decodeStream((InputStream) new
					// URL(imageUrl).getContent());
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				// Add the big picture to the style.
				notiStyle.bigPicture(remote_picture);
	
				// Creates an explicit intent for an ResultActivity to receive.
				Intent resultIntent = new Intent(mContext, MainActivity.class);
				
				resultIntent.putExtra(Constants.EXTRA_NOTIFICATION_ITEM_ID,mItemID);
	
				// This ensures that the back button follows the recommended convention
				// for the back key.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
	
				// Adds the back stack for the Intent (but not the Intent itself).
				stackBuilder.addParentStack(MainActivity.class);
	
				// Adds the Intent that starts the Activity to the top of the stack.
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
						PendingIntent.FLAG_UPDATE_CURRENT);
	
				Notification noti = new NotificationCompat.Builder(mContext)
						.setSmallIcon(R.drawable.ic_launcher)
						.setAutoCancel(true)
	//					.setLargeIcon(remote_picture)
						.setContentIntent(resultPendingIntent)
	//					.addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
	//					.addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
	//					.addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
						.setContentTitle(mContext.getResources().getString(R.string.notification_title))
						.setContentText(mTitle)
						.setStyle(notiStyle).build();
			return noti;
		}
	}
}
