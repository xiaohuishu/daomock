package com.JavaPersistence.XML.analysisUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 对po包下所有的类进行解析得到其类名的帮助类
 * 
 * @author AntsMarch
 * 
 */
public class ClassScanHelper {

	/**
	 * 得到po包下所有的Po类的名字
	 * @param file
	 * @param poSrcPath
	 * @return Set<String> result
	 */
	public Set<String> getAllEntity(File file, String poSrcPath) {
		Set<String> result = new HashSet<String>();

		this.scanFolderToGetClass(file, poSrcPath);

		for (String classItem : getClassSet()) {
			result.add(classItem);
		}
		return result;
	}
	
	/**
	 * 通过传入的路径来进行判断解析其路径下所有的文件,通过指定的过滤器来找到解析所需要的名字.
	 * @param file
	 * @param poSrcPath
	 */
	private void scanFolderToGetClass(File file, String poSrcPath) {
		File[] files = file.listFiles(new ClassFileFilterHelper());
		for (File fileItem : files) {
			if (fileItem.isDirectory()) {
				// System.out.println("文件： " + fileItem + ": 文件路径：" + poSrcPath
				// + "." + fileItem.getName());
				this.scanFolderToGetClass(fileItem,
						poSrcPath + "." + fileItem.getName());
			}
			classSet.add(poSrcPath
					+ "."
					+ fileItem.getName().substring(0,
							fileItem.getName().lastIndexOf('.')));
			// System.out.println("po包下解析后的类名： "
			// + poSrcPath
			// + "."
			// + fileItem.getName().substring(0,
			// fileItem.getName().lastIndexOf('.')));

		}

	}

	public Set<String> getClassSet() {
		return classSet;
	}
	//声明一个Set集合,用来存储Po包下所有解析后的po类名
	private Set<String> classSet = new HashSet<String>();
}
