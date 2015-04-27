package org.ginryan.speex;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * 写入处理队列
 * 
 * @author Liang
 *
 */
public class RawDataWritingThread extends Thread {
	LinkedList<RawData> rawDataQueue = null;
	Object flag = new Object();
	boolean isRunning = false;
	DataOutputStream output = null;

	public RawDataWritingThread() {
		rawDataQueue = new LinkedList<RawData>();
	}

	/**
	 * 设置输出
	 * 
	 * @param output
	 *            数据输出流
	 */
	public void setOutput(DataOutputStream output) {
		this.output = output;
	}

	Object runningLock = new Object();

	@Override
	public void run() {
		super.run();

		while (isRunning) {
			try {
				synchronized (runningLock) {
					RawData raw = rawDataQueue.get(0);
					rawDataQueue.remove(0);
					if (raw != null) {
						System.out.println("正在写入缓存长度:" + raw.length);
						// 开始写入
						for (int i = 0; i < raw.buffer.length; i++) {
							output.writeShort(raw.buffer[i]);
						}
						output.flush();
					}
				}
			} catch (IOException e) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (Exception e) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}

		if (onFinishedRunning != null) {
			onFinishedRunning.onFinished();
		}
	}

	OnFinishedRunning onFinishedRunning;

	public void setOnFinishedRunning(OnFinishedRunning onFinishedRunning) {
		this.onFinishedRunning = onFinishedRunning;
	}

	public interface OnFinishedRunning {
		public void onFinished();
	}

	@Override
	public synchronized void start() {
		isRunning = true;
		super.start();
	}

	/**
	 * 组配缓冲对象
	 * 
	 * @param buffer
	 *            缓冲区
	 * @param readSize
	 */
	public void send(short[] buffer, int readSize) {
		RawData raw = new RawData();
		// synchronized (flag) {
		raw.length = readSize;
		raw.buffer = new short[readSize];
		System.arraycopy(buffer, 0, raw.buffer, 0, readSize);
		// }
		// 不用put测试一下
		rawDataQueue.add(raw);
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isRunning() {
		return isRunning;
	}
}
