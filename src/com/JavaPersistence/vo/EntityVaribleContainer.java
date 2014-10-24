package com.JavaPersistence.vo;

import java.lang.annotation.Annotation;
/**
 * 实体变量容器类,将实体变量与注解信息,和其类的信息,类型绑定在一起
 * @author AntsMarch
 *
 */
public class EntityVaribleContainer {
	private String variableName;
	private String variableType;
	private String variableRefClass;
	private Annotation annotation;

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	public String getVariableRefClass() {
		return variableRefClass;
	}

	public void setVariableRefClass(String variableRefClass) {
		this.variableRefClass = variableRefClass;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}
}
