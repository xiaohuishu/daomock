package com.JavaPersistence.XML.analysisUtil;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件过滤器帮助类,对一个文件夹下所有的文件进行指定的过滤
 * 
 * @author AntsMarch
 * 
 */
public class ClassFileFilterHelper implements FileFilter {

	/**
	 * 过滤方法,只返回后缀为.class的文件
	 * @param File pathname
	 * @return result(boolean)
	 */
	@Override
	public boolean accept(File pathname) {
		boolean result = true;
		if (pathname.isFile()) {
			String filename = pathname.getName();
			String filtername = filename.substring(filename.lastIndexOf('.'),
					filename.length());
			if (!filtername.equals(".class")) {
				result = false;
			}

		}

		return result;
	}

}
