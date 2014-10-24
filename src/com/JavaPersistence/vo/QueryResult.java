package com.JavaPersistence.vo;

import java.util.List;

/**
 * 查询结果对象类
 * @author AntsMarch
 *
 * @param <T>
 */
public class QueryResult<T> {
	private List<T> results;
	private long countRecords;

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public long getCountRecords() {
		return countRecords;
	}

	public void setCountRecords(long countRecords) {
		this.countRecords = countRecords;
	}

}
