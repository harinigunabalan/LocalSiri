package tud.kom.dss6.localsiri;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AskSiri extends Activity {

	protected ImageButton relTopics;
	protected ImageButton query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask_siri);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

		relTopics = (ImageButton) findViewById(R.id.RelTopics);
		query = (ImageButton) findViewById(R.id.Query);

		relTopics.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchRelTopics();
			}
		});

		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchQuery();
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void launchRelTopics() {
		startActivity(new Intent(this, RelTopics.class));
	}

	protected void launchQuery() {
		startActivity(new Intent(this, askQuery.class));
	}

}
