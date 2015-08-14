/*
 * Copyright (C) 2013 AChep@xda <artemchep@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.achep.header2actionbar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.shoogisoft.oneappleaday.common.ListViewScrollTracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Space;

/**
 * Little header fragment.
 * <p>
 * <b>Important</b>: Use {@link android.R.id#background} to specify background view and
 * {@link android.R.id#title} to specify view on top of the header
 * (for example: a shadow for {@code ActionBar}).
 * <p>
 * Created by AChep@xda <artemchep@gmail.com>
 */
//  let's        \'/
//  remember   -= * =-
//  happy        {.}
//  2013        {.-'}
//  year!      {`_.-'}
//  It was    {-` _.-'}
//  amazing!   `":=:"`
//              `---`
public abstract class HeaderFragment extends  Fragment {

    private static final String TAG = "HeaderFragment";

    public static final int HEADER_BACKGROUND_SCROLL_NORMAL = 0;
    public static final int HEADER_BACKGROUND_SCROLL_PARALLAX = 1;
    public static final int HEADER_BACKGROUND_SCROLL_STATIC = 2;

    private FrameLayout mFrameLayout;
    private View mContentOverlay;

    // header
    private View mHeader;
    private View mHeaderHeader;
    private View mHeaderBackground;
    private int mHeaderHeight;
    private int mHeaderScroll;

    private int mHeaderBackgroundScrollMode = HEADER_BACKGROUND_SCROLL_NORMAL;

    private Space mFakeHeader;
    private boolean isListViewEmpty;

    // listeners
    private AbsListView.OnScrollListener mOnScrollListener;
    private OnHeaderScrollChangedListener mOnHeaderScrollChangedListener;

    public interface OnHeaderScrollChangedListener {
        public void onHeaderScrollChanged(float progress, int height, int scroll);
    }

    public void setOnHeaderScrollChangedListener(OnHeaderScrollChangedListener listener) {
        mOnHeaderScrollChangedListener = listener;
    }

    public void setHeaderBackgroundScrollMode(int scrollMode) {
        mHeaderBackgroundScrollMode = scrollMode;
    }

    
    private static int mScrollPosition=0, mFirstVisibleItem=-1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Activity activity = getActivity();
        assert activity != null;
        mFrameLayout = new FrameLayout(activity);

        mHeader = onCreateHeaderView(inflater, mFrameLayout);
        mHeaderHeader = mHeader.findViewById(android.R.id.title);
        mHeaderBackground = mHeader.findViewById(android.R.id.background);
        assert mHeader.getLayoutParams() != null;
        mHeaderHeight = mHeader.getLayoutParams().height;

        mFakeHeader = new Space(activity);
        mFakeHeader.setLayoutParams(
                new ListView.LayoutParams(0, mHeaderHeight));

        View content = onCreateContentView(inflater, mFrameLayout);
        if (content instanceof ListView) {
            isListViewEmpty = true;

            final ListView listView = (ListView) content;
            listView.addHeaderView(mFakeHeader);
            onSetAdapter();
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                    if (mOnScrollListener != null) {
                        mOnScrollListener.onScrollStateChanged(absListView, scrollState);
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    if (mOnScrollListener != null) {
                        mOnScrollListener.onScroll(
                                absListView, firstVisibleItem, 
                                visibleItemCount, totalItemCount);
                    }
                    
                    isListViewEmpty = listView.getAdapter() == null;
                    if (isListViewEmpty) {
                        scrollHeaderTo(0);
                    } else {
                        final View child = absListView.getChildAt(0);
                        assert child != null;
                        scrollHeaderTo(child == mFakeHeader ? child.getTop() : -mHeaderHeight);
//                        Log.v("header height",String.format("mHeaderHeight:%s",mHeaderHeight));
                    }
                }
            });
        } 
        else if (content instanceof GridView) {
            isListViewEmpty = true;

           
            final GridView grid = (GridView) content;
            //isListViewEmpty = grid.getAdapter() == null;
//            grid.addHeaderView(mFakeHeader);
            
         // Merge fake header view and content view.
            final LinearLayout view = new LinearLayout(activity);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            view.setOrientation(LinearLayout.VERTICAL);
            view.addView(mFakeHeader);
            view.addView(grid);
                        
            grid.setOnScrollListener(new AbsListView.OnScrollListener() {

                private ListViewScrollTracker mScrollTracker; ;

				@Override
                public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                    if (mOnScrollListener != null) {
                        mOnScrollListener.onScrollStateChanged(absListView, scrollState);
                    }
                }

                public int getGridScrollY(GridView grid)
                {
                	
                	Method privateStringMethod = null;
                	Integer scrollOffset = null;
                	Integer scrollExtent  = null;
                	Integer scrollRange = null;
					try {
						privateStringMethod = GridView.class.getDeclaredMethod("computeVerticalScrollOffset", null);
						privateStringMethod.setAccessible(true);
						scrollOffset = (Integer)privateStringMethod.invoke(grid, null);
						
						privateStringMethod = GridView.class.getDeclaredMethod("computeVerticalScrollExtent", null);
						privateStringMethod.setAccessible(true);
						scrollExtent = (Integer)privateStringMethod.invoke(grid, null);
						
						privateStringMethod = GridView.class.getDeclaredMethod("computeVerticalScrollRange", null);
						privateStringMethod.setAccessible(true);
						scrollRange = (Integer)privateStringMethod.invoke(grid, null);
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	return scrollRange;//scrollExtent*scrollOffset;  

                }
                
                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                	if (mScrollTracker == null)
                		mScrollTracker = new ListViewScrollTracker(absListView);
                	mScrollPosition = mScrollTracker.calculateIncrementalOffset(firstVisibleItem, visibleItemCount);
                	Log.v("header","scrollPosition:"+mScrollPosition);
                    if (mOnScrollListener != null) {
                        mOnScrollListener.onScroll(
                                absListView, firstVisibleItem, 
                                visibleItemCount, totalItemCount);
                    }                   
                   
                    final View child = absListView.getChildAt(0);
                    if (child != null)
                    {
                    	int gridScrollY =  mScrollPosition;//getGridScrollY((GridView) absListView);
                    	scrollHeaderTo(-gridScrollY);
                    	
                    	//change the size of the fake header on scrolling                     	
						int currentHeight = mFakeHeader.getLayoutParams().height;						
                    	Integer fakeHeaderHeight = (int) (mHeaderHeight-(gridScrollY*2)) ;                    	
						mFakeHeader.setLayoutParams(new android.widget.LinearLayout.LayoutParams(mFakeHeader.getWidth(), fakeHeaderHeight));
//						setViewTranslationY(mFrameLayout, -1*(gridScrollY));
                    }
                    else
                    	scrollHeaderTo(0); 
                }
            });
            content = view;
        }else {

            // Merge fake header view and content view.
            final LinearLayout view = new LinearLayout(activity);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            view.setOrientation(LinearLayout.VERTICAL);
            view.addView(mFakeHeader);
            view.addView(content);

            // Put merged content to ScrollView
            final NotifyingScrollView scrollView = new NotifyingScrollView(activity);
            scrollView.addView(view);
            scrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
                @Override
                public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                    scrollHeaderTo(-t);
                }
            });
            content = scrollView;
        }

        mFrameLayout.addView(content);
        mFrameLayout.addView(mHeader);

        // Content overlay view always shows at the top of content.
        if ((mContentOverlay = onCreateContentOverlayView(inflater, mFrameLayout)) != null) {
            mFrameLayout.addView(mContentOverlay, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }

        // Post initial scroll
        mFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                scrollHeaderTo(0, true);
            }
        });

        return mFrameLayout;
    }

    private int scrollHeaderTo(int scrollTo) {
        return scrollHeaderTo(scrollTo, false);
    }

    private int scrollHeaderTo(int scrollTo, boolean forceChange) {
        scrollTo = Math.min(Math.max(scrollTo, -mHeaderHeight), 0);
        if (mHeaderScroll == (mHeaderScroll = scrollTo) & !forceChange) return 0;

        setViewTranslationY(mHeader, scrollTo);
        setViewTranslationY(mHeaderHeader, -scrollTo);

        switch (mHeaderBackgroundScrollMode) {
            case HEADER_BACKGROUND_SCROLL_NORMAL:
                setViewTranslationY(mHeaderBackground, 0);
                break;
            case HEADER_BACKGROUND_SCROLL_PARALLAX:
                setViewTranslationY(mHeaderBackground, -scrollTo / 1.6f);
                break;
            case HEADER_BACKGROUND_SCROLL_STATIC:
                setViewTranslationY(mHeaderBackground, -scrollTo);
                break;
        }

        int delta = 0;
		if (mContentOverlay != null) {
            final ViewGroup.LayoutParams lp = mContentOverlay.getLayoutParams();
            delta = mHeaderHeight + scrollTo;
            lp.height = mFrameLayout.getHeight() - delta;
            mContentOverlay.setLayoutParams(lp);
            mContentOverlay.setTranslationY(delta);
        }

        notifyOnHeaderScrollChangeListener(
                (float) -scrollTo / mHeaderHeight,
                mHeaderHeight,
                -scrollTo);
        return scrollTo;
    }

    private void setViewTranslationY(View view, float translationY) {
        if (view != null) view.setTranslationY(translationY);
    }

    private void notifyOnHeaderScrollChangeListener(float progress, int height, int scroll) {
        if (mOnHeaderScrollChangedListener != null) {
            mOnHeaderScrollChangedListener.onHeaderScrollChanged(progress, height, scroll);
        }
    }

    public abstract View onCreateHeaderView(LayoutInflater inflater, ViewGroup container);
    
    public abstract void onSetAdapter();

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container);

    public abstract View onCreateContentOverlayView(LayoutInflater inflater, ViewGroup container);

    public void setListViewAdapter(ListView listView, ListAdapter adapter) {
        isListViewEmpty = adapter == null;
        listView.setAdapter(null);
        listView.removeHeaderView(mFakeHeader);
        listView.addHeaderView(mFakeHeader);
        listView.setAdapter(adapter);
    }

    /**
     * {@inheritDoc AbsListView#setOnScrollChangedListener}
     */
    public void setListViewOnScrollChangedListener(AbsListView.OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    // //////////////////////////////////////////
    // //////////// -- GETTERS -- ///////////////
    // //////////////////////////////////////////

    public View getHeaderView() {
        return mHeader;
    }

    public View getHeaderHeaderView() {
        return mHeaderHeader;
    }

    public View getHeaderBackgroundView() {
        return mHeaderBackground;
    }

    public int getHeaderBackgroundScrollMode() {
        return mHeaderBackgroundScrollMode;
    }

}
