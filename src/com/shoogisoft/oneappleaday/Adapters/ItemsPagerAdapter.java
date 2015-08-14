package com.shoogisoft.oneappleaday.Adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.Adapters.ItemsListAdapter2ColumnsInRow.OnItemSelectedListener;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.Vote;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.common.VotePrefs;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.Share;
import com.shoogisoft.oneappleaday.parse.Items;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;
import com.shoogisoft.oneappleaday.controls.*;
import android.R.raw;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemsPagerAdapter extends PagerAdapter implements OnClickListener {

	 protected String[] mParties = new String[] {"ידעו", "לא ידעו"};
	private Activity mContext;
	private List<PersistentItem> mItemList;
	private TextView mItemTitle;
	private TextView mItemBody;
	private ImageView mImage;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ParallaxScrollView mParallaxScrollView;
	private Button mBtnKnew;
	private Button mBtnDidntKnow;
	private ViewGroup mVotingPanel;
	private HashMap<Integer,Integer> mPositionAverageColors = new HashMap<Integer, Integer>();
	private OnVoteListener callback;
	private PieChart mChart;
	private ViewGroup mChartPanel,mActionBar;
	private ImageView mBtnShare;
	private ImageView mBtnShareSideButton;
	
	
	
	public interface OnVoteListener {
		void onVote(boolean knewIt);
	}

	public void setOnVoteListener(OnVoteListener listener) {
		callback = listener;
	}

	public ItemsPagerAdapter(Activity context, List<PersistentItem> items) {
		this.mContext = context;
		
		mItemList = Utils.filterNewItems(items);
		
	}

	@Override
	public int getCount() {
		return mItemList.size();
	}

	
	@SuppressLint("NewApi")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		// int rightToLeftPosition = mItemList.size() - 1 - position;

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup layout = (ViewGroup) inflater.inflate(
				R.layout.item_single_pager_item, null);
		
		mBtnShareSideButton = (ImageView)layout.findViewById(R.id.btnShareSideButton);
		Animation slide = AnimationUtils.loadAnimation(mContext, R.anim.slide_from_right);
		mBtnShareSideButton.startAnimation(slide);
		mBtnShareSideButton.setVisibility(View.VISIBLE);
		
		mVotingPanel = (ViewGroup)layout.findViewById(R.id.votingPanel);
		mChartPanel = (ViewGroup)layout.findViewById(R.id.chartPanel);
		
		mActionBar = (ViewGroup)layout.findViewById(R.id.actionBarLayout);

		mParallaxScrollView = (ParallaxScrollView) layout
				.findViewById(R.id.parallaxScrollView);
		mItemTitle = (TextView) layout.findViewById(R.id.txtItemTitle);
		mItemBody = (TextView) layout.findViewById(R.id.txtItemBody);
//		mItemBody.setMovementMethod(LinkMovementMethod.getInstance());

		
		mBtnKnew = (Button) layout.findViewById(R.id.btnKnew);
		mBtnKnew.setOnClickListener(this);
		mBtnDidntKnow = (Button) layout.findViewById(R.id.btnDidntKnow);
		mBtnDidntKnow.setOnClickListener(this);
		
		mImage = (ImageView) layout.findViewById(R.id.imgTitle);

		PersistentItem itemInPosition = mItemList.get(position);

		mBtnDidntKnow.setTag(position);
		mBtnKnew.setTag(position);
		
		initChart(layout,position);
                		
		toggleVotingPanel(itemInPosition.ObjectID);

		String singleLineTitle = Utils.getSingleLineTitle(itemInPosition);
		mItemTitle.setText(singleLineTitle);
		
//		mBtnShare = (ImageView)layout.findViewById(R.id.btnShare);
//		mBtnShare.setTag(position);
//		mBtnShare.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {		
//				int position = Integer.valueOf(v.getTag().toString());
//				PersistentItem itemInPosition = mItemList.get(position);
//				String singleLineTitle = getSingleLineTitle(itemInPosition);
//				new ShareDialog(mContext, R.style.CustomAlertDialog,itemInPosition.ObjectID,singleLineTitle).show();
//			}
//		});

		mItemBody.setText(itemInPosition.Body);

		if (itemInPosition.ImageUrl != null) {

			Bitmap img = imageLoader.loadImageSync(itemInPosition.ImageUrl,
					Utils.getImageLoaderOptions());
			if (img != null ) {
				int bitmapColorAverage=0;
				if (itemInPosition.ItemColor != null)
				{
					bitmapColorAverage = Color.parseColor(itemInPosition.ItemColor);
					mPositionAverageColors.put(position, bitmapColorAverage);
				}
				else if (!mPositionAverageColors.containsKey(position))
				{
					bitmapColorAverage = Utils.getBitmapColorAverage(img);
					mPositionAverageColors.put(position, bitmapColorAverage);
				}
				else
					bitmapColorAverage = mPositionAverageColors.get(position);
				
				mImage.setImageBitmap(img);				
				mParallaxScrollView.setBackgroundColor(bitmapColorAverage);
				
				mActionBar.setBackgroundColor(bitmapColorAverage);
				
				
			}
		}

		container.addView(layout);
		return layout;

	}
	
	public PersistentItem getItemAtPosition(int position)
	{
		return mItemList.get(position);
	}
	public int getItemAverageColor(int position)
	{
		if (mPositionAverageColors != null && mPositionAverageColors.containsKey(position))
			return mPositionAverageColors.get(position);
		else
			return mContext.getResources().getColor(R.color.actionbar_bg);
	}

	private void initChart(ViewGroup layout, int position) {
		mChart = (PieChart) layout.findViewById(R.id.chart1);

        // change the color of the center-hole
        mChart.setHoleColor(Color.rgb(235, 235, 235));

//        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf");
//        mChart.setValueTypeface(tf);
//        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));

        mChart.setHoleRadius(40f);

        mChart.setDescription("");

        mChart.setDrawYValues(true);
        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);

        mChart.setRotationAngle(0);

        // draws the corresponding description value into the slice
        mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // display percentage values
        mChart.setUsePercentValues(true);
        // mChart.setUnit(" ג‚¬");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
//        mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);

        mChart.setCenterText("סיכום");
        
        setData(1, 100,position);

        mChart.animateXY(1500, 1500);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();           	
        l.setPosition(LegendPosition.NONE);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(5f);
	}

	
	private void setData(int count, float range, int position) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
//        for (int i = 0; i < count + 1; i++) {
//            yVals1.add(new Entry(30, i));
//        }
        
        float knew =  mItemList.get(position).knew;
        float didntKnow =  mItemList.get(position).didntKnow;
        int normalizedKnew = normalizePercentage(knew,knew+didntKnow);
        int normalizedDidntKnow = normalizePercentage(didntKnow,knew+didntKnow);
        
        yVals1.add(new Entry(normalizedKnew,0));
        yVals1.add(new Entry(normalizedDidntKnow,0));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet set1 = new PieDataSet(yVals1, "Election Results");
        set1.setSliceSpace(3f);
        
        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//        
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
        
        
        colors.add(Color.parseColor("#64D313"));//green
        colors.add(Color.RED);

        set1.setColors(colors);

        PieData data = new PieData(xVals, set1);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }
	private int normalizePercentage(float knew, float total) {
		
		return Math.round(100 * knew / total);
	}

	

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((ViewGroup) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;

	}

	public void toggleVotingPanel(final String itemObjectID)
	{
		mContext.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (VotePrefs.didVoteAlready(itemObjectID))	
				{
					mVotingPanel.setVisibility(View.GONE);
					mChartPanel.setVisibility(View.VISIBLE);
					mChart.animateXY(1500, 1500);
				}
				else
				{
					mVotingPanel.setVisibility(View.VISIBLE);
					mChartPanel.setVisibility(View.GONE);
				}
			}
		});
		
	}
	@Override
	public void onClick(View v) {
		int position = Integer.valueOf(v.getTag().toString());
		PersistentItem itemInPosition = mItemList.get(position);		
		String itemObjectID = itemInPosition.ObjectID; 
		switch (v.getId()) {   
		case R.id.btnKnew:
			Items.iKnew(itemObjectID);
			if (callback != null)
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Vote.CATEGORY,Vote.VOTE_KNEW, String.valueOf(itemInPosition.ItemID));
				callback.onVote(true);
			break;
		case R.id.btnDidntKnow:
			Items.iDidntKnow(itemObjectID);
			if (callback != null)
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Vote.CATEGORY,Vote.VOTE_DIDNT_KNOW, String.valueOf(itemInPosition.ItemID));
				callback.onVote(false);
			break;

		}		
	}

}
