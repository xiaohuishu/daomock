package com.JavaPersistence.dao.template;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.dom4j.Element;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.util.ReadXmlUtil;

/**
 * 对象解析工具类
 * 
 * @author AntsMarch
 * 
 */
public class daoObjectMapper {

	private daoObjectMapper() {
	}

	/**
	 * 根据配置的Po实体类的配置文件,通过传入的一个ResultSet结果集,通过classElement,Class<T>
	 * entityClass来解析获取一个对应的实体类对象
	 * 
	 * @param resultSet
	 * @param classElement
	 * @param entityClass
	 * @return List<T> entitys
	 * @throws SQLException
	 * @throws DBException
	 */
	public static <T> List<T> getObjectByResultSet(ResultSet resultSet,
			Element classElement, Class<T> entityClass) throws SQLException {
		if (classElement == null)
			classElement = ReadXmlUtil.getElement(entityClass);

		List<T> entitys = new ArrayList<T>();
		while (resultSet.next()) {
			Map<String, Object> fieldsValue = new HashMap<String, Object>();

			for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
				// System.out.println("result : "
				// + resultSet.getMetaData().getColumnLabel(i + 1));
				fieldsValue.put(resultSet.getMetaData().getColumnLabel(i + 1),
						resultSet.getObject(i + 1));

				// System.out.println(resultSet.getMetaData()
				// .getColumnLabel(i + 1)
				// + " : "
				// + resultSet.getObject(i + 1));
			}
			T entity = null;
			entity = setAllFieldsValue(classElement, entityClass, fieldsValue);
			// System.out.println(entity.toString());
			entitys.add(entity);
		}
		return entitys;
	}

	/**
	 * 通过classElement节点,对象的Class类型,和对象字段的键值对,来解析成一个对应的实体类对象
	 * 
	 * @param classElement
	 * @param entityClass
	 * @param fieldsValue
	 * @return T entity
	 * @throws DBException
	 */
	public static <T> T setAllFieldsValue(Element classElement,
			Class<T> entityClass, Map<String, Object> fieldsValue) {
		if (null == classElement) {
			classElement = ReadXmlUtil.getElement(entityClass);
		}
		T entity = null;

		try {
			entity = entityClass.newInstance();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}
		for (Iterator<String> i = fieldsValue.keySet().iterator(); i.hasNext();) {
			String column = i.next();
			@SuppressWarnings("unchecked")
			Iterator<Element> classIterator = classElement.elementIterator();
			while (classIterator.hasNext()) {
				Element prop = classIterator.next();
				if (prop.attributeValue("column").equals(column)) {
					Field field = null;
					try {
						field = entityClass.getDeclaredField(prop
								.attributeValue("name"));
						field.setAccessible(true);

						if (prop.attribute("className") != null
								&& fieldsValue.get(prop
										.attributeValue("column")) != null) {
							Class<?> fatherClass = Class.forName(prop
									.attributeValue("className"));

							Object father = null;
							father = fatherClass.newInstance();

							Field id = fatherClass.getDeclaredField(prop
									.attributeValue("column"));
							id.setAccessible(true);
							if ("java.math.BigDecimal".equals(fieldsValue
									.get(prop.attributeValue("column"))
									.getClass().getName())) {
								BigDecimal decimal = (BigDecimal) fieldsValue
										.get(prop.attributeValue("column"));
								if (prop.attributeValue("type")
										.equals("Double")) {
									id.set(father, decimal.doubleValue());
								} else if (prop.attributeValue("type").equals(
										"Float")) {
									id.set(father, decimal.floatValue());
								}
							} else {
								id.set(father, fieldsValue.get(prop
										.attributeValue("column")));
							}
							System.out.println(father);
							field.set(entity, father);

						} else {
							if (fieldsValue.get(prop.attributeValue("column")) != null) {
								if ("java.math.BigDecimal".equals(fieldsValue
										.get(prop.attributeValue("column"))
										.getClass().getName())) {
									BigDecimal decimal = (BigDecimal) fieldsValue
											.get(prop.attributeValue("column"));
									if (prop.attributeValue("type").equals(
											"Double")) {
										field.set(entity, decimal.doubleValue());
									} else if (prop.attributeValue("type")
											.equals("Float")) {
										field.set(entity, decimal.floatValue());
									}

								} else {
									// System.out.println(field.getType().getName());
									if (field.getType().getName()
											.equals("[Ljava.lang.String;")) {
										String[] fieldValues = new String[1];
										fieldValues[0] = (String) fieldsValue
												.get(prop
														.attributeValue("column"));
										field.set(entity, fieldValues);
									}
									else {

										field.set(entity, fieldsValue.get(prop
												.attributeValue("column")));

									} //
										// System.out.println(field
										// + " : "
										// + fieldsValue.get(prop
										// .attributeValue("column")));
								}
								// System.out.println(fieldsValue.get(prop
								// .attributeValue("column")));

							}
						}
						break;
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return entity;
	}

}
