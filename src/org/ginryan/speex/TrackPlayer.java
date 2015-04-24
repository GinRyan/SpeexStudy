package org.ginryan.speex;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class TrackPlayer extends Thread {
	/**
	 * 采样率
	 */
	private long sampleRateInHz;
	/**
	 * 缓冲区长度
	 */
	private int bufferSizeInShorts;
	/**
	 * 音频数据总长度
	 */
	long totalAudioLength;
	/**
	 * 声道数
	 */
	private int channels;
	private int bitDepth;
	public TrackPlayer(int channels, int bufferSize, long sampleRateInHz, int bitDepth) {
		this.channels = channels;
		this.bufferSizeInShorts = bufferSize;
		this.sampleRateInHz = sampleRateInHz;
		this.bitDepth = bitDepth;
		int channelConfig = 1;
		switch (channels) {
		case 1:
			channelConfig = AudioFormat.CHANNEL_OUT_MONO;
			break;
		case 2:
			channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
			break;
		}
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		switch (bitDepth) {
		case 16:
			audioFormat = AudioFormat.ENCODING_PCM_16BIT;
			break;
		case 8:
			audioFormat = AudioFormat.ENCODING_PCM_8BIT;
			break;
		}
		track = new AudioTrack(AudioManager.STREAM_MUSIC, (int) sampleRateInHz, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
	}
	
	public void play(){
		track.play();
	}
	AudioTrack track;
	
	@Override
	public void run() {
		super.run();
		
	}
}	
