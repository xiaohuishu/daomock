package com.JavaPersistence.dao.parseSQLUtil;

import java.util.*;

import org.dom4j.Element;

/**
 * 查询操作的工具类
 * 
 * @author AntsMarch
 * 
 */
public class EntitySearchHelper {
	private EntitySearchHelper() {
	}

	/**
	 * 根据查询的条件和查询条件的值,和实体类的Element节点,来得到查询条件属性节点,值得键值对
	 * 
	 * @param classElement
	 * @param sql_wheres
	 * @param parames
	 * @return Map<Element,Object> results
	 */
	public static Map<Element, Object> getElementWithSqlWhereParame(
			Element classElement, String sql_wheres, List<Object> parames) {
		String[] sql_where = sql_wheres.split(",");

		Map<Element, Object> results = new HashMap<Element, Object>();

		for (int i = 0; i < sql_where.length; i++) {
			System.out.println(sql_where[i]);
			Element prop = getColumnByPropertiesName(sql_where[i], classElement);
			results.put(prop, parames.get(i));
		}

		return results;
	}

	/**
	 * 通过传入的Order查询的结果根据什么来进行排序,来解析Sql语句
	 * 
	 * @param orderBy
	 * @param sql
	 * @param element
	 * @return String sql
	 */
	public static String createOrderBySql(Map<String, String> orderBy,
			String sql, Element element) {
		if (orderBy != null) {
			Iterator<String> iterator = orderBy.keySet().iterator();
			sql = sql + "order by";
			while (iterator.hasNext()) {
				String key = iterator.next();
				sql = sql
						+ " "
						+ getColumnByPropertiesName(key, element)
								.attributeValue("column") + " "
						+ orderBy.get(key) + ",";
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
		}
		return sql;
	}

	/**
	 * 根据传入的字段属性名,来得到生成的配置文件中字段的节点
	 * 
	 * @param propertiesNmae
	 * @param element
	 * @return resultElement
	 */
	private static Element getColumnByPropertiesName(String propertiesNmae,
			Element element) {

		Element resultElement = null;
		@SuppressWarnings("unchecked")
		Iterator<Element> elementIterator = element.elementIterator();
		while (elementIterator.hasNext()) {
			Element elementI = elementIterator.next();
			if (elementI.attributeValue("column").equals(propertiesNmae)) {
				resultElement = elementI;
				break;
			}

		}
		return resultElement;
	}

}
