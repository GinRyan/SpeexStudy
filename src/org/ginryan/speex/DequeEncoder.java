package org.ginryan.speex;

import java.util.LinkedList;

import org.ginryan.speex.clib.Speex;

/**
 * 带缓存队列的编码处理线程
 * 
 * @author Liang
 *
 */
public class DequeEncoder extends Thread {
	Speex speex = new Speex();
	public boolean isRunning = false;
	/**
	 * 互斥锁
	 */
	Object mutex = new Object();
	/**
	 * 转换音频编码后的集合，这个集合将会转移到下一个线程中，用于写入到文件/封装/网络等
	 */
	LinkedList<EncodedData> encodedDataList = new LinkedList<EncodedData>();
	/**
	 * 转换音频编码前的集合
	 */
	LinkedList<RawData> rawDataList = new LinkedList<RawData>();
	/**
	 * 帧长度
	 */
	int frameSize;

	public DequeEncoder() {
		speex.init();
		frameSize = speex.getFrameSize();
	}

	@Override
	public void run() {
		super.run();
		while (isRunning) {
			synchronized (mutex) {
				if (rawDataList.isEmpty()) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			synchronized (mutex) {
				RawData rawData = rawDataList.get(0);
				EncodedData encodedData = new EncodedData();
				rawDataList.remove(0);
				short[] temp = new short[rawData.length];
				System.arraycopy(rawData.buffer, 0, temp, 0, rawData.length);
				int handledLength = speex.encode(temp, 0, encodedData.buffer, frameSize);
				encodedData.length = handledLength;
				encodedDataList.add(encodedData);
				//还未处理完毕，这仅仅是添加到一个队列当中，还需要有一个现成用于封包写入文件
			}
		}
	}
	
	public void setRunning(boolean isRunning) {
		synchronized (mutex) {
			this.isRunning = isRunning;
			if (this.isRunning) {
				mutex.notify();
			}
		}
	}

	public boolean isRunning() {
		synchronized (mutex) {
			return isRunning;
		}
	}
}
