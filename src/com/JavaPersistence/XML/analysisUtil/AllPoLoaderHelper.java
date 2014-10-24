package com.JavaPersistence.XML.analysisUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import org.dom4j.Element;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.util.ReadXmlUtil;

/**
 * 将所有的PO包下进行加载的帮助类
 * 
 * @author AntsMarch
 * 
 */
public class AllPoLoaderHelper {

	private AllPoLoaderHelper() {
	}

	/**
	 * 根据一开始配置的xml文件,通过传入这个文件的rootElement,得到所配置的po包的包名,类得到Po包实际存在的物理路径,
	 * 得到一个file之后对这个file进行解析得到Po包下所有的Po类名,将之存在Set<String> entitys中
	 * 
	 * @param rootElement
	 * @return Set<String> entitys or null;
	 * @throws DBException
	 */
	public static Set<String> loadAllPoConfig(Element rootElement,
			String pathName) throws DBException {
		String poSrcPath = ReadXmlUtil.getPoPathByXmlConfig(rootElement,
				pathName);

		URL url = AllPoLoaderHelper.class.getClassLoader().getResource(
				"entitysTable.xml");

		String filePath = url.toString();

		if (poSrcPath == null || "".equals(poSrcPath)) {

		} else {
			filePath = filePath.substring(0, filePath.lastIndexOf('/')) + "/"
					+ replaceByPath(poSrcPath);

			System.out.println("文件路径(字符串)： " + filePath);
			File file = null;
			try {
				file = new File(new URI(filePath));
				System.out.println("文件路径是(文件类型)：" + file);

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			if (file.exists()) {
				ClassScanHelper classHelper = new ClassScanHelper();
				Set<String> entitys = classHelper.getAllEntity(file, poSrcPath);
				if (entitys.size() == 0)

					throw new DBException(
							"entitysTable.xml文件配置的poSrcPath路径错误！");

				else {

					for (Iterator<String> iterators = entitys.iterator(); iterators
							.hasNext();) {
						System.out.println("po类名是：" + iterators.next());
					}

					return entitys;
				}

			} else

				throw new DBException("entitysTable.xml文件不存在或者配置路径不正确！！！");

		}
		return null;

	}

	/**
	 * 替代字符串方法,将字符串中所有的'.'替换成'/'
	 * 
	 * @param poSrcPath
	 * @return poSrcPath.replace('.','/');
	 */
	private static String replaceByPath(String poSrcPath) {
		return poSrcPath.replace('.', '/');
	}
}
