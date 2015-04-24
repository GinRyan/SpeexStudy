package org.ginryan.speex;

import java.io.DataOutputStream;
import java.io.IOException;

import org.ginryan.speex.WritingThread.OnFinishedRunning;
import org.ginryan.speex.interfaces.Controller;
import org.ginryan.speex.interfaces.RecordAsyncSub;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

/**
 * 录音辅助类
 * 
 * @author Liang
 *
 */
public class RecordingHelper extends RecordAsyncSub implements Controller {
	int bufferSize;
	int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	int bitDepth = 16;
	int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	int sampleRateInHz = 22050;
	AudioRecord audioRecord = null;
	DataOutputStream outputStream = null;

	RawData rawDataBufferCache = new RawData();

	RecordWriterHelper mRecordWriterHelper = null;

	private String outputPath;
	int channels = 1;

	public RecordingHelper(String outputPath) {
		this.outputPath = outputPath;
		bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz,
				channelConfig, audioFormat);
		audioRecord = new AudioRecord(AudioSource.MIC, sampleRateInHz,
				channelConfig, audioFormat, bufferSize);
		rawDataBufferCache.length = bufferSize;
		rawDataBufferCache.buffer = new short[bufferSize];

		switch (channelConfig) {
		case AudioFormat.CHANNEL_IN_MONO:
			channels = 1;
			break;
		case AudioFormat.CHANNEL_IN_STEREO:
			channels = 2;
			break;
		}

		switch (audioFormat) {
		case AudioFormat.ENCODING_PCM_16BIT:
			bitDepth = 16;
			break;

		case AudioFormat.ENCODING_PCM_8BIT:
			bitDepth = 8;
			break;
		}
		mRecordWriterHelper = new RecordWriterHelper(channels, bufferSize,
				sampleRateInHz, bitDepth);
	}

	WritingThread w = new WritingThread();

	@Override
	protected void doAsync() throws IOException {
		setOutputSteam(mRecordWriterHelper.setFileOutput(outputPath + ".~tmp"));
		w.setOutput(outputStream);
		w.setOnFinishedRunning(new OnFinishedRunning() {

			@Override
			public void onFinished() {
				mRecordWriterHelper.copyWaveFile(outputPath);
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		w.start();
		audioRecord.startRecording();
		short[] buffer = new short[bufferSize];

		int readSize = 0;
		while (isRunning()) {
			readSize = audioRecord.read(buffer, 0, bufferSize);
			if (readSize != AudioRecord.ERROR_INVALID_OPERATION) {
				w.send(buffer,readSize);
				// for (int i = 0; i < buffer.length; i++) {
				// outputStream.writeShort(buffer[i]);
				// }
				// outputStream.flush();
			}
		}
		audioRecord.stop();
		audioRecord.release();
	}

	@Override
	protected void doAfterAsync() {// do nothing
	}

	public void setOutputSteam(DataOutputStream outputSteam) {
		this.outputStream = outputSteam;
	}

	public DataOutputStream getOutputSteam() {
		return outputStream;
	}

	@Override
	public void start() {
		isRunning = true;
		exec();
	}

	@Override
	public void stop() {
		isRunning = false;
		w.setRunning(false);
	}
}
