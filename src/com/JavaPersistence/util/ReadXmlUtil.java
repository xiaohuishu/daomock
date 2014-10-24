package com.JavaPersistence.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.XML.analysisUtil.AllPoLoaderHelper;
import com.JavaPersistence.XML.analysisUtil.EntitysToXmlUtil;

/**
 * 对XML进行解析的工具类,可以得到一个rootElement节点,从而获取到自己所需要的信息
 * 
 * @author AntsMarch
 * 
 */
public class ReadXmlUtil {
	// dom4j中读取xml文件的类
	private static SAXReader saxReader = null;
	// xml文件的root节点
	private static Element rootElement = null;
	private static Element entitysElement = null;
	private static String srcPath = "entitysTable.xml";
	private static String decPath = "entitys.xml";

	private ReadXmlUtil() {
	}

	/**
	 * 设置一个静态块,读取XML文件,若指定的xml文件不存在,则重新解析生成,若存在则直接从文件中读取root节点
	 */
	static {
		saxReader = new SAXReader();
		InputStream in = ReadXmlUtil.class.getClassLoader()
				.getResourceAsStream(decPath);
		try {
			int size = in.available();
			// System.out.println("decPath : size :" + size);
			if (size == 0) {

				rootElement = getRootElementByPath(srcPath);

			} else {
				rootElement = getDocument(decPath).getRootElement();
			}
			in.close();
		} catch (Exception e) {

		}

	}

	/**
	 * 返回root节点
	 * 
	 * @return rootElement
	 */
	public static Element getRootElement() {
		return rootElement;
	}

	/**
	 * 根据开始配置的源xml文件来生成rootElement节点.
	 * 
	 * @param srcPath2
	 * @return rootElement
	 * @throws Exception
	 */
	private static Element getRootElementByPath(String srcPath)
			throws Exception {
		Element rootElement = null;

		rootElement = getDocument(srcPath).getRootElement();
		// System.out.println(rootElement.asXML());
		if (getPoPathByXmlConfig(rootElement, "poPath") != null
				|| getPoPathByXmlConfig(rootElement, "voPath") != null) {
			if (rootElement.elements("entity").size() != 0)
				throw new DBException("你已经配置好了entity不需要再生成配置了！！！");

			// System.out.println("sss");
			EntitysToXmlUtil xmlUtil = new EntitysToXmlUtil();

			// 根据PO包下的实体类生成XML节点的工具类

			rootElement = xmlUtil.createXmlByEntitys(
					AllPoLoaderHelper.loadAllPoConfig(rootElement, "poPath"),
					AllPoLoaderHelper.loadAllPoConfig(rootElement, "voPath"));

			entitysElement = rootElement;

			createXmlByEntity(entitysElement);

		}
		return rootElement;
	}

	/**
	 * 根据路径来返回xml文档的文档对象(document)
	 * 
	 * @param path
	 * @return document
	 */
	public static Document getDocument(String path) {
		InputStream in = ReadXmlUtil.class.getClassLoader()
				.getResourceAsStream(path);
		Document doc = null;
		try {
			doc = saxReader.read(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return doc;
	}

	/**
	 * 根据传入的Class类来匹配xml文件中的Element节点,存在则返回这个Class类型的Element，否则返回为空!
	 * 
	 * @param t
	 * @return classElement
	 */
	public static Element getElement(Class<?> entityClass) {
		Element classElement = null;

		@SuppressWarnings("unchecked")
		Iterator<Element> elementIterator = getRootElement().elementIterator();
		while (elementIterator.hasNext()) {
			Element childrenElement = elementIterator.next();
			if (entityClass.getName().equals(
					childrenElement.attributeValue("className"))) {
				classElement = childrenElement;
				break;
			}
		}
		return classElement;
	}

	/**
	 * 判断一个Class类是否在生成的xml文件中存在,存在则返回true，否则返回为false!
	 * 
	 * @param t
	 * @return result
	 */
	public static Boolean getExistOfRootElement(Class<?> t) {
		Boolean result = false;

		@SuppressWarnings("unchecked")
		Iterator<Element> elementIterator = getRootElement().elementIterator();
		while (elementIterator.hasNext()) {
			Element childrenElement = elementIterator.next();
			if (t.getName().equals(childrenElement.attributeValue("className"))) {
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * 根据最初配置的xml文件,拿到Po或者Vo类所在的包名.
	 * 
	 * @param rootElement
	 * @return result
	 */
	public static String getPoPathByXmlConfig(Element rootElement,
			String pathName) {
		String result = null;

		Element element = rootElement.element("usePoSrcPath");

		if (element == null) {
		} else {
			Attribute attribute = element.attribute(pathName);
			if (attribute == null) {
			} else {
				String text = attribute.getText();
				if (text == null || text.equals("")) {
				} else {

					result = text;
				}
			}
		}

		return result;
	}

	/**
	 * 将根据Po类生成的rootElement写入到指定的entitys.xml文件中
	 * 
	 * @param entitysElement
	 * @throws Exception
	 */
	private static void createXmlByEntity(Element entitysElement)
			throws Exception {
		String urlPath = ReadXmlUtil.class.getResource("/entitys.xml")
				.toString();
		//java项目下用replace("/bin","src");build/classes
		urlPath = urlPath.replace("build/classes", "src");
		File file = new File(new URI(urlPath));
		System.out.println(urlPath);
		Writer writer = new FileWriter(file);
		OutputFormat outformat = OutputFormat.createCompactFormat();
		XMLWriter xmlWriter = new XMLWriter(writer, outformat);

		Document document = DocumentFactory.getInstance().createDocument();

		document.add(entitysElement);

		xmlWriter.write(document);

		xmlWriter.close();

	}
}
