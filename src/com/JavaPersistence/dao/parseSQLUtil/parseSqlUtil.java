package com.JavaPersistence.dao.parseSQLUtil;

import java.lang.reflect.Field;
import java.util.*;

import org.dom4j.Element;

import com.JavaPersistence.Exception.DBException;

/**
 * 拼装SQL或者需要的SQL进行处理的工具类
 * 
 * @author AntsMarch
 * 
 */
public class parseSqlUtil {

	private parseSqlUtil() {
	}

	/**
	 * 拼接多表查询的Sql语句,通过传入的vo实体类的节点,和对应的表名和查询条件
	 * 
	 * @param voElement
	 * @param tableNames
	 * @param sql_where
	 * @return String searchSql
	 */
	public static String createSearchSqlByAdvance(Element voElement,
			List<String> tableNames, String sql_where) {
		String searchVarible = "";
		String union = "";
		String table = "";

		// 拿到查询的参数字段:student.stu_id,stu_name,course.cou_id,cou_name,score
		@SuppressWarnings("unchecked")
		Iterator<Element> voelements = voElement.elementIterator();
		while (voelements.hasNext()) {
			Element element = voelements.next();
			if (element.attributeValue("fieldAnnotation") != null
					&& element.attributeValue("union") != null) {
				searchVarible = searchVarible
						+ element.attributeValue("fieldAnnotation") + ",";

				union = union + element.attributeValue("union") + " and ";
			} else {
				searchVarible = searchVarible
						+ element.attributeValue("column") + ",";

			}

		}
		searchVarible = searchVarible.substring(0,
				searchVarible.lastIndexOf(","));

		// 得到多张表名
		for (String tableName : tableNames) {
			table = table + tableName + " ,";

		}
		String searchSql = "select " + searchVarible + " from "
				+ table.substring(0, table.lastIndexOf(","));
		// 拼接前半部分的Sql语句 select
		// student.stu_id,stu_name,course.cou_id,cou_name,score from
		// studnet,score,course where 连接语句
		String search = "";
		if (union == null || "".equals(union)) {

		} else {
			union = union.substring(0, union.lastIndexOf("and"));
			searchSql = searchSql + "where " + union;

			search = " and ";
		}
		// 拼接查询条件
		if (sql_where == null || sql_where.equals("")) {
			return searchSql;
		} else {
			if (union.equals("")) {
				searchSql = searchSql + "where ";
			}
			String[] conditionSearch = sql_where.split(",");

			for (String searchCondition : conditionSearch) {
				Boolean sflag = true;
				@SuppressWarnings("unchecked")
				Iterator<Element> voelements1 = voElement.elementIterator();
				while (voelements1.hasNext()) {
					Element element = voelements1.next();
					// System.out.println(search);
					if (element.attributeValue("column")
							.equals(searchCondition)
							&& element.attributeValue("fieldAnnotation") != null) {

						search = search
								+ element.attributeValue("fieldAnnotation")
								+ "=? and ";
						// System.out.println(search);
						sflag = false;
						break;
					}

				}

				if (sflag) {
					search = search + searchCondition + "=? and ";
				}
			}
			search = search.substring(0, search.lastIndexOf("and"));
			searchSql = searchSql + search;
			return searchSql;
		}

	}

	/**
	 * 得到多表查询设置的条件对应的数据类型,通过与XML中的字段属性进行匹配得到
	 * 
	 * @param voElement
	 * @param sql_where
	 * @return List<String> listTypes
	 */
	public static List<String> getListTypeToSearchByAdvance(Element voElement,
			String sql_where) {

		String[] conditionSearch = sql_where.split(",");
		// System.out.println(conditionSearch.length);
		List<String> listTypes = new ArrayList<String>();

		for (String searchCondition : conditionSearch) {
			@SuppressWarnings("unchecked")
			Iterator<Element> elements = voElement.elementIterator();
			// System.out.println(searchCondition);
			while (elements.hasNext()) {
				Element prop = elements.next();
				if (searchCondition.equals(prop.attributeValue("column"))) {
					// System.out.println(prop.attributeValue("column"));
					listTypes.add(prop.attributeValue("type"));
				}
			}
		}

		return listTypes;
	}

	/**
	 * 对SearchSql操作的所需的SQL进行拼装,根据传入查询条件,查询条件对应的值,分页数据,实体类的节点,标志位,等
	 * 
	 * @param sql
	 * @param firstIndex
	 * @param lastIndex
	 * @param OrderBy
	 * @param classElement
	 * @param condition
	 * @param flag
	 * @param countFlag
	 * @return String sql;
	 */
	public static String createSearchSql(String sql, int firstIndex,
			int lastIndex, Map<String, String> OrderBy, Element classElement,
			Map<Element, Object> condition, int flag, int countFlag) {

		System.out.println(condition);
		Iterator<Element> conditionElement = condition.keySet().iterator();
		while (conditionElement.hasNext()) {
			Element prop = conditionElement.next();

			if (flag == 0) {

				sql = sql + " " + prop.attributeValue("column") + "=? and";
			} else {
				sql = sql + " " + prop.attributeValue("column") + "=? or";
			}
		}
		if (flag == 0) {
			sql = sql.substring(0, sql.lastIndexOf("and"));
		} else {
			sql = sql.substring(0, sql.lastIndexOf("or"));
		}

		if (countFlag == 1) {
			sql = EntitySearchHelper.createOrderBySql(OrderBy, sql,
					classElement);
			sql = sql + " limit " + firstIndex + "," + lastIndex;
		}

		return sql;
	}

	/**
	 * 对InsertSql操作的所需的SQL进行拼装,根据传入的字段的键值对,和对应实体类的节点信息
	 * 
	 * @param sql
	 * @param fieldsValue
	 * @param classElement
	 * @param primaryName
	 * @return String InsertSql+")"
	 */
	public static String createInsertSql(String sql,
			Map<String, Object> fieldsValue, Element classElement,
			String primaryName) {
		List<Element> primaryElements = getColumnByPrimaryName(primaryName,
				classElement);
		// System.out.println(fieldsValue);
		// Boolean[] idIncrements = new Boolean[2];
		List<Boolean> idIncrements = new ArrayList<Boolean>();
		// System.out.println(primaryElements.size());
		for (Element primaryElement : primaryElements) {

			idIncrements.add(Boolean.parseBoolean(primaryElement
					.attributeValue("auto_increment")));
			sql = sql + primaryElement.attributeValue("column") + ",";
		}

		int countColumn = 0;

		Iterator<String> iterator = fieldsValue.keySet().iterator();
		while (iterator.hasNext()) {
			String fieldName = iterator.next();
			// 实体类对象对应的键值对不能为空
			if (fieldsValue.get(fieldName) != null) {
				Element prop = getColumnByPropertiesName(fieldName,
						classElement);
				if (prop != null
						&& (prop.attribute("key") == null || prop
								.attributeValue("key").equals("foreign"))) {
					sql = sql + prop.attributeValue("column") + ",";
					countColumn++;
				}
			}
		}

		sql = sql.substring(0, sql.lastIndexOf(",")) + ") values (";
		// System.out.println(sql);

		for (int i = 0; i < idIncrements.size(); i++) {

			if (idIncrements.get(i) == true) {
				sql = sql + "null,";
			} else {
				sql = sql + "?,";
			}
		}
		for (int i = 0; i < countColumn; i++) {
			sql = sql + "?,";
		}
		System.out.println(sql.substring(0, sql.lastIndexOf(',')) + ")");

		return sql.substring(0, sql.lastIndexOf(',')) + ")";

	}

	/**
	 * 对UpdateSql操作的所需的SQL进行拼装,根据传入的字段的键值对,和对应实体类的节点信息
	 * 
	 * @param sql
	 * @param fieldsValue
	 * @param classElement
	 * @param primaryName
	 * @return String UpdateSql
	 */
	public static String createUpdateSql(String sql,
			Map<String, Object> fieldsValue, Element classElement,
			String primaryName) {
		Iterator<String> fieldsIterator = fieldsValue.keySet().iterator();

		Boolean flag = false;
		while (fieldsIterator.hasNext()) {
			String fieldName = fieldsIterator.next();

			if (fieldsValue.get(fieldName) != null) {
				Element element = getColumnByPropertiesName(fieldName,
						classElement);
				if ((element.attribute("update") == null || element
						.attributeValue("update").equals("true"))
						&& (element.attribute("key") == null)) {
					sql = sql + " " + element.attributeValue("column") + "=? ,";
					flag = true;
				}
				if (element != null && element.attribute("key") != null
						&& element.attributeValue("key").equals("foreign")) {
					String fkClassName = element.attributeValue("className");
					try {
						Field field = Class.forName(fkClassName)
								.getDeclaredField(
										element.attributeValue("column"));
						field.setAccessible(true);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new DBException(
									"parseSqlUtil UpdateSQL语句处理出现错误！");
						} catch (DBException e1) {
							e1.printStackTrace();
						}
					}
					sql = sql + " " + element.attributeValue("column") + "=? ,";
					flag = true;

				}
			}
		}

		if (flag == true) {
			sql = sql.substring(0, sql.lastIndexOf(',')) + " where ";
			List<Element> primaryElements = getColumnByPrimaryName(primaryName,
					classElement);
			for (Element idElement : primaryElements) {

				sql = sql + idElement.attributeValue("column") + "=? and ";

			}
			System.out.println(sql.substring(0, sql.lastIndexOf("and")));
			return sql.substring(0, sql.lastIndexOf("and"));
		}
		
		return sql;
	}

	/**
	 * 通过传入的查询条件的键值对,根据xml中的配置信息来匹配,类匹配与SQL语句相对应的值得顺序
	 * 
	 * @param Map
	 *            <Element,Object> condition
	 * @return List<Object> listValue
	 */
	public static List<Object> getListValueToSearch(
			Map<Element, Object> condition) {
		List<Object> listValue = new ArrayList<Object>();
		Iterator<Element> conditionElement = condition.keySet().iterator();

		while (conditionElement.hasNext()) {
			Element prop = conditionElement.next();

			listValue.add(condition.get(prop));

		}

		return listValue;
	}

	/**
	 * 通过传入的查询条件键值对,根据xml中的配置信息来匹配,类匹配与SQL语句相对应的查询条件值的类型的顺序
	 * 
	 * @param Map
	 *            <Element,Object> condition
	 * @return List<String> typeList;
	 */
	public static List<String> getListTypeToSearch(
			Map<Element, Object> condition) {
		List<String> typelist = new ArrayList<String>();
		Iterator<Element> conditionElement = condition.keySet().iterator();

		while (conditionElement.hasNext()) {
			Element prop = conditionElement.next();
			if (prop.attribute("key") != null
					&& prop.attributeValue("key").equals("primary")
					&& prop.attributeValue("auto_increment").equals("false")) {
				typelist.add("String");

			} else {

				typelist.add(prop.attributeValue("type"));
			}
		}

		return typelist;
	}

	/**
	 * 通过传入的更新实体类的字段键值对,根据xml中的配置信息来匹配,类匹配与SQL语句相对应的值得顺序
	 * 
	 * @param fieldsValue
	 * @param classElement
	 * @param primaryName
	 * @return List<Object> listValue
	 */
	public static List<Object> getListValueToUpdate(
			Map<String, Object> fieldsValue, Element classElement,
			String primaryName) {
		List<Object> listValue = new ArrayList<Object>();
		Boolean flag = false;

		Iterator<String> fieldsIterator = fieldsValue.keySet().iterator();
		while (fieldsIterator.hasNext()) {
			String fieldName = fieldsIterator.next();

			if (fieldsValue.get(fieldName) != null) {
				Element elementF = getColumnByPropertiesName(fieldName,
						classElement);
				if ((elementF.attribute("update") == null || elementF
						.attributeValue("update").equals("true"))
						&& (elementF.attribute("key") == null)) {
					// System.out.println(fieldsValue.get(fieldName));
					listValue.add(fieldsValue.get(fieldName));
					flag = true;
				}
				if (elementF != null && elementF.attribute("key") != null
						&& elementF.attributeValue("key").equals("foreign")) {
					String fkClassName = elementF.attributeValue("className");
					try {
						Field field = Class.forName(fkClassName)
								.getDeclaredField(
										elementF.attributeValue("column"));
						field.setAccessible(true);
						listValue.add(field.get(fieldsValue.get(elementF
								.attributeValue("name"))));
						flag = true;
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new DBException(
									"parseSqlUtil UpdateSQL语句处理出现错误！");
						} catch (DBException e1) {
							e1.printStackTrace();
						}
					}

				}

			}
		}

		List<Element> foreginElements = getColumnByForeginName(primaryName,
				classElement);
		List<Element> primaryElements = getColumnByPrimaryName(primaryName,
				classElement);
		if (foreginElements.size() != 0) {
			for (Element elementF1 : foreginElements) {
				// if (elementF != null && elementF.attribute("key") != null
				// && element.attributeValue("key").equals("foreign")) {

				String fkClassName = elementF1.attributeValue("className");
				try {
					Field field = Class.forName(fkClassName).getDeclaredField(
							elementF1.attributeValue("column"));

					// System.out.println(element.attributeValue("column"));
					field.setAccessible(true);
					listValue.add(field.get(fieldsValue.get(elementF1
							.attributeValue("name"))));
					// System.out.println(field.get(fieldsValue.get(elementF1
					// .attributeValue("name"))));
				} catch (Exception e) {
					e.printStackTrace();
					try {
						throw new DBException("");
					} catch (DBException e1) {
						e1.printStackTrace();
					}
				}
			}
			// System.out.println(foreginElements.get(0).attributeValue("name"));
			if (foreginElements.get(0).attributeValue("name")
					.equals(primaryElements.get(0).attributeValue("name"))) {
				flag = false;
			} else {
				flag = true;
			}
		}

		if (flag == true && primaryElements.size() != 0) {
			String[] names = primaryName.split(",");
			for (String name : names) {
				listValue.add(fieldsValue.get(name));

				// System.out.println(fieldsValue.get(name));
			}
		}

		return listValue;
	}

	/**
	 * 通过传入的更新实体类的字段键值对,根据xml中的配置信息来匹配,类匹配与SQL语句相对应的值的类型的顺序
	 * 
	 * @param fieldsValue
	 * @param classElement
	 * @param primaryName
	 * @return List<String> typeList
	 */
	public static List<String> getListTypeToUpdate(
			Map<String, Object> fieldsValue, Element classElement,
			String primaryName) {
		List<String> typeList = new ArrayList<String>();
		Boolean flag = false;

		Iterator<String> iterator = fieldsValue.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();

			if (fieldsValue.get(name) != null) {
				Element prop = getColumnByPropertiesName(name, classElement);
				if (prop != null
						&& (prop.attribute("key") == null || prop
								.attributeValue("key").equals("foreign"))) {
					// System.out.println(prop.attributeValue("type"));
					typeList.add(prop.attributeValue("type"));
					flag = true;

				}
				/*
				 * if (prop != null && prop.attribute("key") == null) {
				 * typeList.add(prop.attributeValue("type")); flag = true; //
				 * System.out.println(prop.attributeValue("type")); }
				 */

			}
		}

		List<Element> foreginElements = getColumnByForeginName(primaryName,
				classElement);
		List<Element> primaryElements = getColumnByPrimaryName(primaryName,
				classElement);
		if (foreginElements.size() != 0) {
			for (Element elementF1 : foreginElements) {

				typeList.add(elementF1.attributeValue("type"));

			}
			// System.out.println(foreginElements.get(0).attributeValue("name"));
			if (foreginElements.get(0).attributeValue("name")
					.equals(primaryElements.get(0).attributeValue("name"))) {
				flag = false;
			} else {
				flag = true;
			}
		}

		if (flag == true && primaryElements.size() != 0) {
			for (Element primaryElement : primaryElements) {
				typeList.add(primaryElement.attributeValue("type"));
			}
		}

		return typeList;
	}

	/**
	 * 通过传入的插入实体类的字段键值对,根据xml中的配置信息来匹配,类匹配与SQL语句相对应的值得顺序
	 * 
	 * @param fieldsValue
	 * @param classElement
	 * @param primaryName
	 * @return List<Object> listValue
	 * @throws Exception
	 */
	public static List<Object> getListValueToInsert(
			Map<String, Object> fieldsValue, Element classElement,
			String primaryName) {
		// System.out.println(fieldsValue);
		List<Object> listValue = new ArrayList<Object>();

		List<Element> primaryElements = getColumnByPrimaryName(primaryName,
				classElement);
		List<Element> foreginElements = getColumnByForeginName(primaryName,
				classElement);
		Boolean flag = true;
		if (foreginElements.size() != 0) {
			for (Element elementF1 : foreginElements) {
				// if (elementF != null && elementF.attribute("key") != null
				// && element.attributeValue("key").equals("foreign")) {

				String fkClassName = elementF1.attributeValue("className");
				try {
					Field field = Class.forName(fkClassName).getDeclaredField(
							elementF1.attributeValue("column"));

					// System.out.println(element.attributeValue("column"));
					field.setAccessible(true);
					listValue.add(field.get(fieldsValue.get(elementF1
							.attributeValue("name"))));
					// System.out.println(field.get(fieldsValue.get(elementF1
					// .attributeValue("name"))));
				} catch (Exception e) {
					e.printStackTrace();
					try {
						throw new DBException("");
					} catch (DBException e1) {
						e1.printStackTrace();
					}
				}
			}
			// System.out.println(foreginElements.get(0).attributeValue("name"));
			if (foreginElements.get(0).attributeValue("name")
					.equals(primaryElements.get(0).attributeValue("name"))) {
				flag = false;
			} else {
				flag = true;
			}
		}

		if (flag == true && primaryElements.size() != 0) {
			for (Element primaryElement : primaryElements) {
				if (primaryElement.attributeValue("auto_increment").equals(
						"false")) {

					listValue.add(fieldsValue.get(primaryElement
							.attributeValue("name")));

				}
			}
		}

		Iterator<String> iterator = fieldsValue.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();

			if (fieldsValue.get(name) != null) {
				Element prop = getColumnByPropertiesName(name, classElement);

				if (prop != null && prop.attribute("key") == null) {
					listValue.add(fieldsValue.get(name));
					flag = true;
				}
				if (prop != null && prop.attribute("key") != null
						&& prop.attributeValue("key").equals("foreign")) {
					String fkClassName = prop.attributeValue("className");
					try {
						Field field = Class
								.forName(fkClassName)
								.getDeclaredField(prop.attributeValue("column"));

						// System.out.println(element.attributeValue("column"));
						field.setAccessible(true);
						listValue.add(field.get(fieldsValue.get(prop
								.attributeValue("name"))));
						// System.out.println(field.get(fieldsValue.get(elementF1
						// .attributeValue("name"))));
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new DBException("");
						} catch (DBException e1) {
							e1.printStackTrace();
						}
					}
				}

			}

		}

		return listValue;
	}

	/**
	 * 通过传入的插入实体类的字段键值对,根据xml中的配置信息来匹配,类匹配与SQL语句相对应的值的类型的顺序
	 * 
	 * @param fieldsValue
	 * @param classElement
	 * @param primaryName
	 * @return List<String> typeList
	 * @throws Exception
	 */
	public static List<String> getListTypeToInsert(
			Map<String, Object> fieldsValue, Element classElement,
			String primaryName) {
		List<String> typeList = new ArrayList<String>();
		Boolean flag = true;
		List<Element> foreginElements = getColumnByForeginName(primaryName,
				classElement);
		List<Element> primaryElements = getColumnByPrimaryName(primaryName,
				classElement);
		if (foreginElements.size() != 0) {
			for (Element elementF1 : foreginElements) {

				typeList.add(elementF1.attributeValue("type"));

			}
			// System.out.println(foreginElements.get(0).attributeValue("name"));
			if (foreginElements.get(0).attributeValue("name")
					.equals(primaryElements.get(0).attributeValue("name"))) {
				flag = false;
			} else {
				flag = true;
			}
		}

		if (flag == true && primaryElements.size() != 0) {
			for (Element primaryElement : primaryElements) {
				if (primaryElement.attributeValue("auto_increment").equals(
						"false")) {
					typeList.add(primaryElement.attributeValue("type"));
				}
			}
		}
		Iterator<String> iterator = fieldsValue.keySet().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();

			if (fieldsValue.get(name) != null) {
				Element prop = getColumnByPropertiesName(name, classElement);

				if (prop != null
						&& (prop.attribute("key") == null || prop
								.attributeValue("key").equals("foreign"))) {
					typeList.add(prop.attributeValue("type"));

				}

			}
		}

		return typeList;
	}

	/**
	 * 根据传入的主键字段属性名,来得到生成的配置文件中字段的节点
	 * 
	 * @param propertiesNmae
	 * @param element
	 * @return resultElement
	 */
	public static List<Element> getColumnByPrimaryName(String propertiesName,
			Element element) {
		String[] names = propertiesName.split(",");
		List<Element> resultElement = new ArrayList<Element>();
		@SuppressWarnings("unchecked")
		Iterator<Element> elementIterator = element.elementIterator();
		while (elementIterator.hasNext()) {
			Element elementI = elementIterator.next();
			for (String name : names) {
				// System.out.println(name + " : "
				// + elementI.attributeValue("column"));
				if (elementI.attributeValue("update") != null
						&& elementI.attributeValue("column").equals(name)
						&& elementI.attributeValue("key").equals("primary")) {
					resultElement.add(elementI);
				}
			}
		}
		return resultElement;
	}

	/**
	 * 根据传入外键字段属性名,来得到生成的配置文件中字段的节点
	 * 
	 * @param propertiesNmae
	 * @param element
	 * @return resultElement
	 */
	public static List<Element> getColumnByForeginName(String propertiesName,
			Element element) {
		String[] names = propertiesName.split(",");
		List<Element> resultElement = new ArrayList<Element>();
		@SuppressWarnings("unchecked")
		Iterator<Element> elementIterator = element.elementIterator();
		while (elementIterator.hasNext()) {
			Element elementI = elementIterator.next();
			for (String name : names) {
				// System.out.println(name + " : "
				// + elementI.attributeValue("column"));
				if (elementI.attributeValue("column").equals(name)
						&& elementI.attributeValue("key").equals("foreign")) {
					resultElement.add(elementI);
				}
			}
		}
		return resultElement;
	}

	/**
	 * 根据传入的字段属性名,来得到生成的配置文件中字段的节点
	 * 
	 * @param propertiesNmae
	 * @param element
	 * @return resultElement
	 */
	public static Element getColumnByPropertiesName(String propertiesNmae,
			Element element) {

		Element resultElement = null;
		@SuppressWarnings("unchecked")
		Iterator<Element> elementIterator = element.elementIterator();
		while (elementIterator.hasNext()) {
			Element elementI = elementIterator.next();
			if (elementI.attributeValue("name").equals(propertiesNmae)) {
				resultElement = elementI;
				break;
			}

		}
		return resultElement;
	}

}
