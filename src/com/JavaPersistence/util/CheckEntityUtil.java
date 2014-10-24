package com.JavaPersistence.util;

import java.io.IOException;

import org.dom4j.DocumentException;

/**
 * 由于用到的次数相当的的多,所以我选择将这个类变成是final
 * 
 * @author AntsMarch
 */
public class CheckEntityUtil {
	
	private CheckEntityUtil(){ }
	/** 
	 * 以什么方式去检测是不是entity阿？ 第一,就是 检测包名是不是最后以po结束 第二,去xml中匹配是不是存在呢
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 * */
	public static Boolean doCheck(Class<?> entity) {
		return ReadXmlUtil.getExistOfRootElement(entity);
	}

}