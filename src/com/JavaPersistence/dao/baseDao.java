package com.JavaPersistence.dao;

import java.util.List;
import java.util.Map;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.vo.QueryResult;

/**
 * 基础Dao接口,声明save(),update(),delete(),findAllEntity(),findAllBySearch(),
 * findAllEntityByMaual();
 * 
 * @author AntsMarch
 * 
 */
public interface baseDao {
	// 保存方法
	public Boolean save(Object entity) throws DBException;

	// 更新方法

	public Boolean update(Object entity) throws DBException;

	// 删除方法
	public <T> Boolean delete(Class<T> entity, List<Object> id)
			throws DBException;

	// 通用的查询方法(1.单表查询 2.多表查询)
	public <T> QueryResult<T> findAllEntitys(Class<T> voClass,
			List<Class<?>> entitysClass, int firstIndex, int maxResult,
			Map<String, String> OrderBy, String sql_where,
			List<Object> parames, int flag) throws DBException;

	// 传入定义的SQL语句进行查询
	public <T> QueryResult<T> findAllEntityByMaual(Class<T> entity,
			int firstIndex, int maxResult, String sql, List<Object> parames,
			List<String> listTypes, int flag) throws DBException;

	// 查询方法
	public <T> QueryResult<T> findAllEntity(Class<T> entity, int firstIndex,
			int maxResult, Map<String, String> OrderBy, String sql_where,
			List<Object> parames, int flag) throws DBException;

	// 多表查询
	public <T> QueryResult<T> findAllBySearch(Class<T> voClass,
			List<Class<?>> entitysClass, int firstIndex, int maxResult,
			Map<String, String> OrderBy, String sql_where,
			List<Object> parames, int flag) throws DBException;
}
