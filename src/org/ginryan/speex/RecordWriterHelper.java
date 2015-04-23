package org.ginryan.speex;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.util.Log;

/**
 * 录音文件写入辅助类
 * 
 * @author Liang
 *
 */
public class RecordWriterHelper {
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
	/**
	 * 输出路径
	 */
	private String recordIn;
	private int bitDepth;

	public RecordWriterHelper(int channels, int bufferSize, long sampleRateInHz, int bitDepth) {
		this.channels = channels;
		this.bufferSizeInShorts = bufferSize;
		this.sampleRateInHz = sampleRateInHz;
		this.bitDepth = bitDepth;
	}

	/**
	 * 设置输出数据流地址
	 * 
	 * @param recordInFile
	 * @return
	 * @throws FileNotFoundException
	 */
	public DataOutputStream setFileOutput(String recordInFile)
			throws FileNotFoundException {
		// 输出文件
		this.recordIn = recordInFile;
		File file = new File(recordInFile);
		FileOutputStream outputFile = new FileOutputStream(file);
		DataOutputStream dataOutputStream = new DataOutputStream(outputFile);
		return dataOutputStream;
	}

	/**
	 * 录音结束后需要将文件复制一遍出来，以便于 这里得到可播放的音频文件
	 * 
	 * @param inFilename
	 * @param outFilename
	 */
	public void copyWaveFile(String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long byteRate = bitDepth * sampleRateInHz * channels / 8;
		
		Log.d("debug_audio", "位深:" + bitDepth);
		Log.d("debug_audio", "采样率:" + sampleRateInHz);
		Log.d("debug_audio", "声道数:" + channels);
		Log.d("debug_audio", "最终比特率:" + byteRate);
		
		// short[] data = new short[bufferSizeInShorts];
		try {
			in = new FileInputStream(recordIn);
			out = new FileOutputStream(outFilename);
			totalAudioLength = in.getChannel().size();
			long totalDataLen = totalAudioLength + 36;
			totalDataLen = totalAudioLength + 36;
			writePcmMetaDataAsHeader(out, totalAudioLength, totalDataLen,sampleRateInHz, channels, byteRate);
			// inChannel.transferTo(0, totalAudioLength, outChannel);
			byte[] transBuffer = new byte[1024];
			int length = 0;
			while ((length = in.read(transBuffer)) != -1) {
				out.write(transBuffer, 0, length);
			}
			in.close();
			out.close();
			new File(recordIn).delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 这里提供一个头信息。作为PCM文件头中的元数据，指明该文件的总时长，总数据长度，采样率，声道，比特率等等数据。 PCM文件头必备。
	 * 
	 */
	public void writePcmMetaDataAsHeader(FileOutputStream out,long totalAudioLength, long totalDataLength, long longSampleRate,
			int channels, long byteRate) throws IOException {

		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLength & 0xff);
		header[5] = (byte) ((totalDataLength >> 8) & 0xff);
		header[6] = (byte) ((totalDataLength >> 16) & 0xff);
		header[7] = (byte) ((totalDataLength >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLength & 0xff);
		header[41] = (byte) ((totalAudioLength >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLength >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLength >> 24) & 0xff);
		out.write(header, 0, 44);
	}
}
