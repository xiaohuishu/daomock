package com.JavaPersistence.Exception;

/**
 * 自定义异常类,重写构造方法
 * 
 * @author AntsMarch
 * 
 */
public class DBException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DBException(String exceptionMsg) {
		super(exceptionMsg);
	}
}
