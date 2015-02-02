package tud.kom.dss6.localsiri.localservice;

import tud.kom.dss6.localsiri.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;

	public ImageAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		return mImageIds.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			gridView = new View(mContext);

			gridView = inflater.inflate(R.layout.grid_view_items, null);

			ImageView imageView = (ImageView) gridView
					.findViewById(R.id.grid_image);
			TextView textView = (TextView) gridView
					.findViewById(R.id.grid_label);

			textView.setText(mLabelIds[position]);

			imageView.setImageResource(mImageIds[position]);
			// imageView.setLayoutParams(new GridView.LayoutParams(600, 600));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setPadding(8, 8, 8, 8);

		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	// references to our images
	private Integer[] mImageIds = { 
			R.drawable.ic_home_ask,
			R.drawable.ic_home_trace,
			R.drawable.ic_home_location,
			R.drawable.ic_home_setting, 
			R.drawable.ic_home_start,
			R.drawable.ic_home_stop };

	// references to our images
	private Integer[] mLabelIds = { 
			R.string.localsiri,
			R.string.trace,
			R.string.location, R.string.settings,
			R.string.start, R.string.stop };
}
