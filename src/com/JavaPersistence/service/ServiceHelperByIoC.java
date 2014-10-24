package com.JavaPersistence.service;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.util.annotationUtil;

public class ServiceHelperByIoC {

	// // ApplicationContext ac = new FileSystemXmlApplicationContext(
	// // "src/applicationContext.xml");
	// // ac.getBean("service");
	private ServiceHelperByIoC() {

	}

	private static Element rootElement = null;
	private static SAXReader saxReader = null;
	private static String pathName = "applicationContext.xml";
	private static annotationUtil autil = null;
	/**
	 * 设置一个静态块,来获取解析的对象
	 */
	static {

		autil = new annotationUtil();
		saxReader = new SAXReader();
		try {
			rootElement = getRootElementByXml(pathName);
		} catch (DBException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 根据设定的xml配置文件名,来得到这个xml文件的根节点
	 * 
	 * @param pathName
	 * @return Element rootElement
	 * @throws DBException
	 */
	private static Element getRootElementByXml(String pathName)
			throws DBException {
		Element rootElement = null;
		InputStream in = ServiceHelperByIoC.class.getResourceAsStream("/"
				+ pathName);

		Document doc = null;
		try {
			doc = saxReader.read(in);
		} catch (DocumentException e) {

			e.printStackTrace();
		}

		rootElement = doc.getRootElement();

		if (rootElement == null)
			throw new DBException("请先配置好XML文件！");

		return rootElement;

	}

	/**
	 * 获取xml文件的根节点
	 * 
	 * @return
	 */
	public static Element getRootElement() {
		return rootElement;
	}

	/**
	 * 一个公开的接口,获取传入需要得到的service的ID得到service的方法; annotationUtil autil = new
	 * annotationUtil();
	 * 
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static Object getServiceByXmlConfigAnalysis(String serviceId)
			throws DBException {
		return getServiceByXml(getRootElement(), serviceId);
	}

	/**
	 * 通过拿到获取的xml配置文件的根结点和传入的指定的service名字,通过解析得到这个service对象
	 * 
	 * @param rootElement
	 * @param objName
	 * @return
	 * @throws DBException
	 * @throws Exception
	 */
	private static Object getServiceByXml(Element rootElement, String objName)
			throws DBException {

		@SuppressWarnings("unchecked")
		List<Element> elementList = rootElement.elements();

		// 获取目标指定的service节点和其他的非service的节点数组
		Element goalElement = null;
		List<Element> linkElements = new ArrayList<Element>();
		for (Element element : elementList) {
			if (objName.equals(element.attributeValue("id"))) {
				goalElement = element;
			} else {
				linkElements.add(element);
			}
		}
		// 实例化service对象
		Class<?> goalClass = null;
		try {
			goalClass = Class.forName(goalElement.attributeValue("class"));
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}

		// 如果需要得到的对象没有对应的依赖对象,就直接实例化这个对象，否则则进行对应的解析处理
		if (goalElement.elements() == null) {
			try {
				return goalClass.newInstance();

			} catch (Exception e) {

				e.printStackTrace();
				throw new DBException("目标对象实例化出现错误！");
			}
		} else {

			Object refObject = null;

			List<String> proName = new ArrayList<String>();
			// 得到这个service依赖的对象;
			for (@SuppressWarnings("unchecked")
			Iterator<Element> iteratorprop = goalElement.elementIterator(); iteratorprop
					.hasNext();) {
				Element prop = iteratorprop.next();
				String fieldName = prop.attributeValue("name");

				proName.add(fieldName);
				String linkName = prop.attributeValue("ref");

				for (Element otherElement : linkElements) {
					if (linkName.equals(otherElement.attributeValue("id"))) {
						String linkClassName = otherElement
								.attributeValue("class");
						try {
							refObject = Class.forName(linkClassName)
									.newInstance();
						} catch (Exception e) {

							e.printStackTrace();
							throw new DBException("依赖对象实例化出现错误！！！");
						}
					}

				}

			}

			return XmlConfigAnalysis(goalClass, refObject, proName);
		}
	}

	/**
	 * 根据传入的目标对象的Class,依赖的对象,属性的值;来进行分析得到目标的对象
	 * 
	 * @param goalClass
	 * @param refObject
	 * @param proName
	 * @return
	 * @throws DBException
	 */
	private static Object XmlConfigAnalysis(Class<?> goalClass,
			Object refObject, List<String> proName) throws DBException {
		Object goalObject = null;

		Map<String, PropertyDescriptor> pros = null;
		try {
			goalObject = goalClass.newInstance();
			pros = autil.getBeanInfo(goalClass);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new DBException("目标对象实例化出现错误或者获取目标对象的属性描述出现出现！！！");
		}
		// 利用java.Beans的工具类来对目标对象依赖的对象进行处理
		for (Entry<String, PropertyDescriptor> pro : pros.entrySet()) {
			String valueName = pro.getValue().getName();
			for (String value : proName) {
				if (valueName.equals(value)) {
					Method writeMethod = pro.getValue().getWriteMethod();

					try {
						writeMethod.invoke(goalObject, refObject);
					} catch (IllegalAccessException e) {

						e.printStackTrace();
					} catch (IllegalArgumentException e) {

						e.printStackTrace();
					} catch (InvocationTargetException e) {

						e.printStackTrace();
					}
				}

			}

		}

		return goalObject;
	}
}
