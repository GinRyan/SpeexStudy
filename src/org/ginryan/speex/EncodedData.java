package org.ginryan.speex;
/**
 * Speex编码后的数据
 * @author Liang
 *
 */
public class EncodedData {
	/**
	 * 时间偏移量
	 */
	public long timeOffset;
	/**
	 * 缓冲长度
	 */
	public int length;
	/**
	 * 缓冲区数据
	 */
	public byte[] buffer = new byte[2048];
}
