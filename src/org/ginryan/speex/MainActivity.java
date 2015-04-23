package org.ginryan.speex;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
	String recordPath = null;
	
	Button start;
	Button stop;
	
	RecordingHelper mRecordHelper = null;

	private String externalDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		
		externalDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		stop.setEnabled(false);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			recordPath = externalDir + "/" + System.currentTimeMillis() + ".wav";
			mRecordHelper = new RecordingHelper(recordPath);
			mRecordHelper.start();
			start.setEnabled(false);
			stop.setEnabled(true);
			break;

		case R.id.stop:
			start.setEnabled(true);
			stop.setEnabled(false);
			mRecordHelper.stop();
			break;
		}
	}

}
