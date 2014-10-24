package com.JavaPersistence.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.dao.baseDao;
import com.JavaPersistence.dao.parseSQLUtil.EntitySearchHelper;
import com.JavaPersistence.dao.parseSQLUtil.parseSqlUtil;
import com.JavaPersistence.dao.template.daoOptemplate;
import com.JavaPersistence.util.CheckEntityUtil;
import com.JavaPersistence.util.ReadXmlUtil;
import com.JavaPersistence.util.annotationUtil;
import com.JavaPersistence.vo.QueryResult;

/**
 * baseDao接口的实现类
 * 
 * @author AntsMarch
 * 
 */
public class baseDaoImpl implements baseDao {

	// private baseDaoImpl() {
	// }

	/**
	 * 单例设计
	 * 
	 * @author AntsMarch
	 * 
	 */
	private static class baseDaoImplHelper {
		static final baseDaoImpl baseDao = new baseDaoImpl();
	}

	public static baseDaoImpl getInstance() {
		return baseDaoImplHelper.baseDao;
	}

	private static annotationUtil autil = new annotationUtil();

	/**
	 * sava方法,接收传入的实体类对象(有数据),通过sql解析工具类根据实体类对象的信息数据得到拼装的sql和插入的值和类型相对应顺序的list集合
	 * ,从而执行insert操作
	 * 
	 * @params Object entity
	 */
	public Boolean save(Object entity) throws DBException {
		String tableName = autil.getAnnotationTableName(entity.getClass());
		// System.out.println(tableName);
		String primaryName = autil.getAnnotationPrimaryNameByField(entity
				.getClass());
		// 获取实体类对应的全部字段及其值;
		Map<String, Object> fieldsValue = autil.getAllFieldsValue(entity);
		String sql = "insert into " + tableName + "(";
		Element classElement = null;
		if (false == CheckEntityUtil.doCheck(entity.getClass())) {
			throw new DBException("所操作的类在xml中不存在！请检查！");
		}
		classElement = ReadXmlUtil.getElement(entity.getClass());
		// 得到拼接后的Sql语句
		sql = parseSqlUtil.createInsertSql(sql, fieldsValue, classElement,
				primaryName);

		List<Object> objValues = null;
		List<String> objTypes = null;
		// 获取对应的save的实体的字段相对应顺序的值和数据类型的集合(注：顺序必须要匹配,不然会出现错误！)
		try {
			objValues = parseSqlUtil.getListValueToInsert(fieldsValue,
					classElement, primaryName);
			System.out.println(objValues);
			objTypes = parseSqlUtil.getListTypeToInsert(fieldsValue,
					classElement, primaryName);
			// System.out.println(objTypes);
		} catch (Exception e) {
			e.printStackTrace();

			throw new DBException("在执行save的方法出现错误！！！");

		}
		// 调用模板类中的更新方法;
		return daoOptemplate.getInstance().update(sql, objValues, objTypes,
				false);
	}

	/**
	 * update方法,接收传入的实体类对象(有数据),
	 * 通过sql解析工具类根据实体类对象的信息数据得到拼装的sql和插入的值和类型相对应顺序的list集合 ,从而执行update操作
	 * 
	 * @params Object entity
	 */
	public Boolean update(Object entity) throws DBException {
		Element classElement = null;
		if (false == CheckEntityUtil.doCheck(entity.getClass())) {
			throw new DBException("所操作的类在xml中不存在！请检查！");
		}
		String primaryName = autil.getAnnotationPrimaryNameByField(entity
				.getClass());
		classElement = ReadXmlUtil.getElement(entity.getClass());
		// 获取实体类对应的全部字段及其值;
		Map<String, Object> fieldsValue = autil.getAllFieldsValue(entity);

		String tableName = classElement.attributeValue("table");

		String sql = "update " + tableName + " set ";
		// 获取拼接后的Sql语句
		sql = parseSqlUtil.createUpdateSql(sql, fieldsValue, classElement,
				primaryName);

		if (sql == null || sql.equals("update " + tableName + " set ")) {
			return false;
		} else {
			List<Object> objValues = null;
			List<String> objTypes = null;
			// //获取对应的save的实体的字段相对应顺序的值和数据类型的集合(注：顺序必须要匹配,不然会出现错误！)
			try {
				objValues = parseSqlUtil.getListValueToUpdate(fieldsValue,
						classElement, primaryName);
				objTypes = parseSqlUtil.getListTypeToUpdate(fieldsValue,
						classElement, primaryName);
				// System.out.println(objValues);
			} catch (Exception e) {
				e.printStackTrace();

				throw new DBException("在执行Update的List<>方法出现错误！！！");

			}
			// 调用模板类中的更新方法.
			return daoOptemplate.getInstance().update(sql, objValues, objTypes,
					false);
		}

	}

	/**
	 * 删除的具体实现方法,通过sql解析工具类根据实体类对象的信息数据得到拼装的sql和插入的值和类型相对应顺序的list集合
	 * ,从而执行delete操作
	 * 
	 * @param Class
	 *            <T> entity
	 * @param List
	 *            <Object> id
	 * @return Boolean
	 */
	public <T> Boolean delete(Class<T> entity, List<Object> id)
			throws DBException {

		Element classElement = null;

		if (false == CheckEntityUtil.doCheck(entity)) {
			throw new DBException("所操作的类在xml中不存在！请检查！");
		}

		classElement = ReadXmlUtil.getElement(entity);

		String tableName = autil.getAnnotationTableName(entity);
		String primaryName = autil.getAnnotationPrimaryNameByField(entity);

		List<Element> primaryElements = parseSqlUtil.getColumnByPrimaryName(
				primaryName, classElement);
		List<Element> foreginElements = parseSqlUtil.getColumnByForeginName(
				primaryName, classElement);
		String sql = "delete from " + tableName + " where ";

		List<String> listTypes = new ArrayList<String>();

		Boolean flag = true;

		if (foreginElements.size() != 0) {
			if (foreginElements.get(0).attributeValue("name")
					.equals(primaryElements.get(0).attributeValue("name"))) {
				for (Element prop : primaryElements) {
					sql = sql + prop.attributeValue("column") + "=? and ";
				}
				for (Element elementF1 : foreginElements) {

					listTypes.add(elementF1.attributeValue("type"));

				}

				flag = false;
			} else {

			}

		}

		if (flag == true && primaryElements.size() != 0) {
			for (Element prop : primaryElements) {

				sql = sql + prop.attributeValue("column") + "=? and ";
				listTypes.add(prop.attributeValue("type"));
			}

		}

		sql = sql.substring(0, sql.lastIndexOf("and"));
		System.out.println(sql);

		// System.out.println(listTypes);
		// 调用模板类的更新方法;
		return daoOptemplate.getInstance().update(sql, id, listTypes, false);

	}

	/**
	 * 查询的实现方法，通过传入已经定义好的SQL语句传入进行SQL的查询处理返回QueryResult<T>对象
	 * 
	 * @param entity
	 * @param firstIndex
	 * @param maxResult
	 * @param sql
	 * @param parames
	 * @param flag
	 * @return QueryResult<T> result;
	 * @throws DBException
	 */
	public <T> QueryResult<T> findAllEntityByMaual(Class<T> entity,
			int firstIndex, int maxResult, String sql, List<Object> parames,
			List<String> listTypes, int flag) throws DBException {
		// 拿到vo实体类在xml映射文件中的实体节点
		Element entityElement = null;

		if (false == CheckEntityUtil.doCheck(entity)) {
			throw new DBException("所操作的类在xml中不存在！请检查！");
		}

		entityElement = ReadXmlUtil.getElement(entity);
		String finalSql = sql + " limit " + firstIndex + "," + maxResult;
		QueryResult<T> result = new QueryResult<T>();
		List<T> list = daoOptemplate.getInstance().query(finalSql, parames,
				listTypes, entityElement, entity);
		int resultSize = list.size();
		result.setResults(list);
		result.setCountRecords(resultSize);

		return result;
	}

	/**
	 * 1.单表查询 2.多表查询 综合的统一外部提供的接口来进行查询的方法
	 */
	public <T> QueryResult<T> findAllEntitys(Class<T> voClass,
			List<Class<?>> entitysClass, int firstIndex, int maxResult,
			Map<String, String> OrderBy, String sql_where,
			List<Object> parames, int flag) throws DBException {
		if (entitysClass == null) {
			return findAllEntity(voClass, firstIndex, maxResult, OrderBy,
					sql_where, parames, flag);
		} else {
			return findAllBySearch(voClass, entitysClass, firstIndex,
					maxResult, OrderBy, sql_where, parames, flag);
		}

	}

	/**
	 * 多表查询的dao实现方法,基本思路： 通过传入的Vo对象的Class类型,和组成Vo的Po的Class类型;
	 * 通过反射的得到Vo对象与po对象相对应的字段属性; 之后拼接多表查询的SQL语句和得到对应的查询条件和对应的值; 调用查询模板的查询方法;
	 * 
	 * @param voClass
	 * @param entitysClass
	 * @param sql_where
	 * @param parames
	 * @param flag
	 * @return QueryResult<T> results;
	 * @throws DBException
	 */
	public <T> QueryResult<T> findAllBySearch(Class<T> voClass,
			List<Class<?>> entitysClass, int firstIndex, int maxResult,
			Map<String, String> OrderBy, String sql_where,
			List<Object> parames, int flag) throws DBException {
		// 拿到vo实体类在xml映射文件中的实体节点
		Element voElement = null;

		if (false == CheckEntityUtil.doCheck(voClass)) {
			throw new DBException("所操作的类在xml中不存在！请检查！");
		}

		voElement = ReadXmlUtil.getElement(voClass);
		// 得到组成vo的Po实体的节点的集合,和对应物理表的名字;
		List<Element> entitysElement = new ArrayList<Element>();
		List<String> tableNames = new ArrayList<String>();
		for (Class<?> entity : entitysClass) {
			if (false == CheckEntityUtil.doCheck(entity)) {
				throw new DBException("所操作的类在xml中不存在！请检查！");
			}
			Element element = ReadXmlUtil.getElement(entity);

			entitysElement.add(element);

			String tableName = element.attributeValue("table");

			tableNames.add(tableName);

		}
		if (null == sql_where || "".equals(sql_where)) {
			// 通过得到的Vo实体的节点,涉及的几张表,查询条件;来进行拼接Sql语句
			String searchSql = parseSqlUtil.createSearchSqlByAdvance(voElement,
					tableNames, sql_where);
			searchSql = searchSql + " limit " + firstIndex + "," + maxResult;
			System.out.println(searchSql);
			List<T> list = daoOptemplate.getInstance().query(searchSql,
					voElement, voClass);
			int listSize = list.size();
			QueryResult<T> result = new QueryResult<T>();
			result.setResults(list);
			result.setCountRecords(listSize);
			return result;
		} else {
			// 通过得到的Vo实体的节点,涉及的几张表,查询条件;来进行拼接Sql语句
			String searchSql = parseSqlUtil.createSearchSqlByAdvance(voElement,
					tableNames, sql_where);
			searchSql = searchSql + " limit " + firstIndex + "," + maxResult;
			System.out.println(searchSql);
			// 得到查询条件对应的数据类型,通过和Vo的字段数据进行匹配
			List<String> listTypes = parseSqlUtil.getListTypeToSearchByAdvance(
					voElement, sql_where);

			// 调用模板类的查询方法
			List<T> list = daoOptemplate.getInstance().query(searchSql,
					parames, listTypes, voElement, voClass);
			int listSize = list.size();
			QueryResult<T> result = new QueryResult<T>();
			result.setResults(list);
			result.setCountRecords(listSize);

			return result;
		}

	}

	/**
	 * 查询的实现方法,通过传入的实体类的Class类型,查询所需要的数据,根据生成的po持久化的xml文件来拼接查询的SQL语句
	 */
	public <T> QueryResult<T> findAllEntity(Class<T> entity, int firstIndex,
			int maxResult, Map<String, String> OrderBy, String sql_where,
			List<Object> parames, int flag) throws DBException {
		// 得到查询实体类对应的节点Element
		Element element = null;
		if (false == CheckEntityUtil.doCheck(entity)) {
			throw new DBException("所操作的类在xml中不存在！请检查！");
		}
		element = ReadXmlUtil.getElement(entity);

		String tableName = element.attributeValue("table");

		String searchSql = "select * from " + tableName;

		String countSql = "select count(*) from " + tableName;

		List<Object> objValues = null;
		List<String> listTypes = null;
		// 开始拼接查询的Sql语句,按照查询条件的值是否为空为首要的判定依据来进行不同的拼接,拼接完成得到Sql语句和对应查询条件的值类型的对应,调用模板类中的查询方法;
		if (null == sql_where || "".equals(sql_where)) {
			searchSql = searchSql + " limit " + firstIndex + "," + maxResult;
			// 查询条件为空的情况
			List<T> list = daoOptemplate.getInstance().query(searchSql,
					element, entity);
			int count = daoOptemplate.getInstance().queryCount(countSql);

			QueryResult<T> result = new QueryResult<T>();
			result.setResults(list);
			result.setCountRecords(count);

			return result;

		} else {
			// 查询条件不为空的情况
			Map<Element, Object> condition = null;
			searchSql = searchSql + " where ";
			countSql = countSql + " where ";
			condition = EntitySearchHelper.getElementWithSqlWhereParame(
					element, sql_where, parames);
			System.out.println(condition);
			searchSql = parseSqlUtil.createSearchSql(searchSql, firstIndex,
					maxResult, OrderBy, element, condition, flag, 1);
			System.out.println(searchSql);
			countSql = parseSqlUtil.createSearchSql(countSql, firstIndex,
					maxResult, OrderBy, element, condition, flag, 0);
			System.out.println(countSql);
			objValues = parseSqlUtil.getListValueToSearch(condition);

			listTypes = parseSqlUtil.getListTypeToSearch(condition);

			List<T> list = daoOptemplate.getInstance().query(searchSql,
					objValues, listTypes, element, entity);
			int count = daoOptemplate.getInstance().queryCount(countSql,
					objValues, listTypes, element, entity);

			QueryResult<T> result = new QueryResult<T>();
			result.setResults(list);
			result.setCountRecords(count);

			return result;

		}

	}

}
