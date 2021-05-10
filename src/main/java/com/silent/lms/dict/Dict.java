package com.silent.lms.dict;

/**
 * Description: 字典接口,所有字典均需实现此接口
 *
 */
public interface Dict<T> {
	public T getCode();

	public String getDesc();
}