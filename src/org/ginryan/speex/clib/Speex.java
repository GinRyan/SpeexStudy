
package org.ginryan.speex.clib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Speex  {

	/**
	 * quality
	 * 1 : 4kbps (very noticeable artifacts, usually intelligible)
	 * 2 : 6kbps (very noticeable artifacts, good intelligibility)
	 * 4 : 8kbps (noticeable artifacts sometimes)
	 * 6 : 11kpbs (artifacts usually only noticeable with headphones)
	 * 8 : 15kbps (artifacts not usually noticeable)
	 */
	private static final int DEFAULT_COMPRESSION = 8;
	private Logger log = LoggerFactory.getLogger(Speex.class);
	
	public Speex() {
	}
	/**
	 * 初始化编解码器
	 */
	public void init() {
		load();	
		open(DEFAULT_COMPRESSION);
		log.debug("speex opened");	
	}
	/**
	 * 加载speex本地库
	 */
	private void load() {
		try {
			System.loadLibrary("speex");
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	/**
	 * 开启一个允许编解码的对象，这个过程初始化编解码的码率、质量、帧大小等等
	 * @param compression 压缩质量
	 * @return 开启成功则返回0。如果当前已经开启，则总是返回0
	 */
	public native int open(int compression);
	/**
	 * 获取编码帧大小 
	 * @return 返回用于编码的帧大小
	 */
	public native int getFrameSize();
	/**
	 * 对一段已经speex编码的字节数组进行解码
	 * @param encoded 已经由speex编码的字节数组
	 * @param lin 解码后的原始PCM数据
	 * @param size 原编码帧大小
	 * @return 解码后帧大小
	 */
	public native int decode(byte encoded[], short lin[], int size);
	/**
	 * 对一段原始PCM编码的字节数组编码为speex
	 * @param lin 待编码的原始PCM数组
	 * @param offset 每次取样后有效数据开始的偏移量，一般为0
	 * @param encoded 输出speex编码后的数据
	 * @param size 帧大小(这里很奇怪)
	 * @return  返回编码后的字节数组长度
	 */
	public native int encode(short lin[], int offset, byte encoded[], int size);
	/**
	 * 关闭编解码对象释放内存
	 */
	public native void close();
	
}
