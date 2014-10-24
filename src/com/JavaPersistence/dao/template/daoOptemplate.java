package com.JavaPersistence.dao.template;

import java.sql.*;
import java.util.*;

import org.dom4j.Element;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.util.DBUtil;

/**
 * Dao方法中数据库操作的模板类;
 * 包括保存，更新的update(String sql, List<Object> objParams,
			List<String> paramTypes, boolean flag)throws ErrorException
	查询的query(String sql, List<Object> objParams,
			List<String> paramTypes, Element entityElement, Class<T> entityClass)
 * 
 * @author AntsMarch
 * 
 */
public class daoOptemplate {

	/**
	 * 设计daoOptemplate类为单例
	 */
	private daoOptemplate() {
	}

	private static class Optemplate {
		static final daoOptemplate daoOpt = new daoOptemplate();
	}

	public static daoOptemplate getInstance() {
		return Optemplate.daoOpt;
	}

	/**
	 * 通过传入的更新SQL语句和参数集合,对应的参数类型来执行数据库更新操作！
	 * 
	 * @param sql
	 * @param objParams
	 * @param paramTypes
	 * @param flag
	 * @return Boolean updateFlag
	 * @throws DBException 
	 */
	public Boolean update(String sql, List<Object> objParams,
			List<String> paramTypes, boolean flag) throws DBException {
		Connection conn = null;
		PreparedStatement pstm = null;
		Boolean updateFlag = false;
		try {
			conn = DBUtil.getConnection();
			pstm = flag ? conn.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS) : conn
					.prepareStatement(sql);
			if (null == objParams) {

			} else {
				for (int i = 0; i < objParams.size(); i++) {
					SetPreparedStatement.setPreparedStatementByPropertiesType(
							pstm, i + 1, paramTypes.get(i), objParams.get(i));
				}
			}
			// 设置不自动提交
			conn.setAutoCommit(false);
			int result = pstm.executeUpdate();

			conn.commit();
			if (result > 0)
				updateFlag = true;
		} catch (SQLException ex) {
			ex.printStackTrace();

			throw new DBException("数据库更新方法出现错误！！！");

		} finally {
			try {
				pstm.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return updateFlag;
	}

	/**
	 * 通过传入的查询SQL语句和需要查询实体类节点,对应的参数类型来执行数据库查询操作.
	 * 
	 * @param sql
	 * @param entity
	 * @param entityClass
	 * @return List<T> resultList
	 */
	public <T> List<T> query(String sql, Element entity, Class<T> entityClass) {
		List<T> resultList = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);

			ResultSet rs = pstm.executeQuery();
			resultList = daoObjectMapper.getObjectByResultSet(rs, entity,
					entityClass);
			if (resultList.size() == 0)
				System.out.println("查询的数据为空！！！");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstm.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return resultList;
	}

	/**
	 * 通过传入的查询SQL语句和参数集合,对应的参数类型来执行数据库更新操作！
	 * 
	 * @param sql
	 * @param objParams
	 * @param paramTypes
	 * @param entityElement
	 * @param entityClass
	 * @return List<String> reldata;
	 * @throws DBException
	 */
	public <T> List<T> query(String sql, List<Object> objParams,
			List<String> paramTypes, Element entityElement, Class<T> entityClass)
			throws DBException {
		List<T> reldata = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);
			if (null == objParams) {

			} else {

				for (int i = 0; i < objParams.size(); i++) {
					SetPreparedStatement.setPreparedStatementByPropertiesType(
							pstm, i + 1, paramTypes.get(i), objParams.get(i));
				}

			}
			ResultSet rs = pstm.executeQuery();

			reldata = daoObjectMapper.getObjectByResultSet(rs, entityElement,
					entityClass);

			if (reldata.size() == 0){
				System.out.println("查询的数据为空！！！");
			}
		} catch (Exception e) {
			e.printStackTrace();

			throw new DBException("query方法数据库操作出现异常！！！");

		} finally {
			try {
				pstm.close();
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return reldata;
	}

	/**
	 * 通过查询的sql语句直接得到查询的结果有多少记录
	 * 
	 * @param sql
	 * @return int count
	 */
	public <T> int queryCount(String sql) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);

			ResultSet rs = pstm.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstm.close();
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return count;
	}

	/**
	 * 通过传入的查询SQL语句和参数集合,对应的参数类型来得到查询的记录数！
	 * 
	 * @param sql
	 * @param objParams
	 * @param paramTypes
	 * @param entityElement
	 * @param entityClass
	 * @return count int
	 */
	public <T> int queryCount(String sql, List<Object> objParams,
			List<String> paramTypes, Element entityElement, Class<T> entityClass) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DBUtil.getConnection();
			pstm = conn.prepareStatement(sql);

			if (null == objParams) {

			} else {
				for (int i = 0; i < objParams.size(); i++) {
					SetPreparedStatement.setPreparedStatementByPropertiesType(
							pstm, i + 1, paramTypes.get(i), objParams.get(i));
				}
			}
			ResultSet rs = pstm.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstm.close();
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return count;
	}

}
