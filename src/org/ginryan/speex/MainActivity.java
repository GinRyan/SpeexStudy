package org.ginryan.speex;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public static class RecordingHelper extends RecordAsyncSub {
		int bufferSizeInBytes;
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		int audioConfig = AudioFormat.CHANNEL_IN_MONO;
		int sampleRateInHz = 44100;
		AudioRecord audioRecord = null;
		
		short[] buffer = null;

		public RecordingHelper() {
			bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
					audioConfig, audioFormat);
			audioRecord = new AudioRecord(AudioSource.MIC, sampleRateInHz,
					audioConfig, audioFormat, bufferSizeInBytes);
			buffer = new short[bufferSizeInBytes];

		}

		DataOutputStream outputSteam = null;

		@Override
		protected void doAsync() throws IOException {
			audioRecord.startRecording();
			while (isRunning()) {
				audioRecord.read(buffer, 0, bufferSizeInBytes);
				for (int i = 0; i < buffer.length; i++) {
					outputSteam.writeShort(buffer[i]);
				}
				outputSteam.flush();
			}
			audioRecord.stop();
			outputSteam.close();
		}

		@Override
		protected void doAfterAsync() {// do nothing
		}

		public void setOutputSteam(DataOutputStream outputSteam) {
			this.outputSteam = outputSteam;
		}

		public DataOutputStream getOutputSteam() {
			return outputSteam;
		}
	}

	public static class RawData {
		int length;
		short[] buffer = new short[1024];
	}
}
