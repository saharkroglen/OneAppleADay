package com.shoogisoft.oneappleaday.fragments;

import java.util.List;
import com.achep.header2actionbar.HeaderFragment;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.shoogisoft.oneappleaday.MainActivity;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.Adapters.FieldListAdapter2ColumnsInRow;
import com.shoogisoft.oneappleaday.Adapters.FieldListAdapter2ColumnsInRow.OnFieldSelectedListener;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.common.Utils.spinnerType;
import com.shoogisoft.oneappleaday.parse.Fields;
import com.shoogisoft.oneappleaday.parse.Fields.OnQueryDone;
import com.shoogisoft.oneappleaday.receivers.SyncReceiver;

public class HomeFragment extends HeaderFragment implements OnClickListener {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_PROFILE_NAME = "profile_name";
	FrameLayout mContentOverlay;
	private ListView mItemsGrid;
	private TextView mHeaderTitle;
	private FieldListAdapter2ColumnsInRow mFieldsAdapter;

	public HomeFragment() {

	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_PARALLAX);
		setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
			@Override
			public void onHeaderScrollChanged(float progress, int height,
					int scroll) {
				if (getActivity() == null)
					return;
				height -= getActivity().getActionBar().getHeight();

				progress = (float) scroll / height;
				if (progress > 1f)
					progress = 1f;

				progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;

				((MainActivity) getActivity()).getFadingActionBarHelper()
						.setActionBarAlpha((int) (255 * progress));
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tileShare:
			// ((MainActivity) mContext).openPost();
			break;
		}
	}

	@Override
	public View onCreateContentOverlayView(LayoutInflater inflater,
			ViewGroup container) {
		ProgressBar progressBar = new ProgressBar(getActivity());
		mContentOverlay = new FrameLayout(getActivity());
		mContentOverlay.addView(progressBar, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		// if (mLoaded) mContentOverlay.setVisibility(View.GONE);
		mContentOverlay.setVisibility(View.GONE);
		return mContentOverlay;
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		
		mItemsGrid = (ListView) rootView.findViewById(R.id.itemsGrid);
	
		Fields.getFields(new OnQueryDone() {

			@Override
			public void onFieldsQueryResult(List<Fields> fieldList) {
				Utils.showSpinner(MyApplication.getContext(), false,
						spinnerType.largeWithoutBackground);
				mFieldsAdapter = new FieldListAdapter2ColumnsInRow(
						getActivity(), fieldList);
				mFieldsAdapter
						.setOnFieldSelectedListener(new OnFieldSelectedListener() {

							@Override
							public void onFieldSelected(Fields field) {

							}
						});
				//mItemsGrid.setAdapter(mFieldsAdapter);
			}
		});
		
		return rootView;

	}

	@Override
	public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
		View headerRoot = inflater.inflate(R.layout.fragment_header, container,
				false);
//		mHeaderTitle = (TextView) headerRoot.findViewById(R.id.txtHeaderTitle);
//		mHeaderTitle.setText(getResources().getString(R.string.title_home));
		return headerRoot;
	}

	@Override
	public void onSetAdapter() {
		mItemsGrid.setAdapter(mFieldsAdapter);		
	}

}
