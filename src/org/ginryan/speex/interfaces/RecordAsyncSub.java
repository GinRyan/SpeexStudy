package org.ginryan.speex.interfaces;

import java.io.IOException;

import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

public abstract class RecordAsyncSub extends AsyncTask<Void, Integer, Void> {

	public boolean isRunning = false;

	@Override
	protected Void doInBackground(Void... params) {
		try {
			doAsync();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			doAfterAsync();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected abstract void doAfterAsync() throws IOException;

	protected abstract void doAsync() throws IOException;

	public boolean isRunning() {
		return isRunning;
	}

	public void exec() {
		isRunning = true;
		AsyncTaskCompat.executeParallel(this);
	}

}
