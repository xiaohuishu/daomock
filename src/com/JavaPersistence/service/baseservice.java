package com.JavaPersistence.service;

import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.dao.baseDao;

/**
 * 基础service
 * 
 * @author antsmarth
 * 
 */
public class baseservice {
	// 定义基础的basedao
	private baseDao basedao;

	public baseDao getBasedao() {
		return basedao;
	}

	public void setBasedao(baseDao basedao) {
		this.basedao = basedao;
	}

	/**
	 * saveService方法
	 * 
	 * @param entity
	 * @return Boolean
	 * @throws DBException
	 */
	public Boolean saveTest(Object entity) throws DBException {

		return basedao.save(entity);

	}

}
