package com.JavaPersistence.XML.analysisUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.JavaPersistence.Annotation.PoClassAnnotation;
import com.JavaPersistence.Annotation.PrimaryKeysAnnotation;
import com.JavaPersistence.Exception.DBException;
import com.JavaPersistence.util.annotationUtil;
import com.JavaPersistence.vo.EntityVaribleContainer;

/**
 * 根据PO包下的实体类生成XML节点的工具类
 * 
 * @author AntsMarch
 * 
 */
public class EntitysToXmlUtil {

	/**
	 * 根据Po包下所有的实体类,生成相对应的xml节点<entity></entity>最后返回一个总的实体类节点rootElement<entitys
	 * ><entity></entity></entitys>
	 * 
	 * @param entitys
	 * @return Element rootElement
	 * @throws ClassNotFoundException
	 * @throws DBException
	 */
	public Element createXmlByEntitys(Set<String> entitys, Set<String> voentitys)
			throws ClassNotFoundException, DBException {
		DocumentFactory factory = new DocumentFactory();
		Element rootElement = factory.createElement("entitys");

		// po类信息生成
		if (entitys == null)
			//throw new ErrorException("po包路径配置不正确,路径下没有实体类;");
		{
			
		}
		else {
			for (String entityName : entitys) {
				// System.out.println(entityName);
				Element entity = this.createElementByPo(factory, entityName);

				if (entity == null) {
					continue;
				}
				//System.out.println(entity.asXML());
				rootElement.add(entity);
			}
		}
		// System.out.println(voentitys);
		// vo类信息生成
		if (voentitys == null) // throw new
								// ErrorException("vo包路径配置不正确,路径下没有对应的vo实体类");
		{

		} else {
			for (String entityName : voentitys) {
				System.out.println(entityName);
				Element entity = this.createElementByVo(factory, entityName);
				// System.out.println(entity);
				if (entity == null) {
					continue;
				}
				//System.out.println(entity.asXML());
				rootElement.add(entity);
			}
		}

		//System.out.println(rootElement.asXML());
		return rootElement;
	}

	/**
	 * 对单个实体类生成对应的实体类节点,当这个实体类必须有主键注解才会生成
	 * 
	 * @param factory
	 * @param entityName
	 * @return Element entityElement
	 * @throws ClassNotFoundException
	 * @throws DBException
	 */
	private Element createElementByPo(DocumentFactory factory, String entityName)
			throws ClassNotFoundException, DBException {
		Class<?> entity = null;
		entity = Class.forName(entityName);
		Field[] fields = entity.getDeclaredFields();
		boolean flag = false;
		for (Field field : fields) {
			Annotation primaryAnnotation = field
					.getAnnotation(PrimaryKeysAnnotation.class);
			if (primaryAnnotation != null) {
				flag = true;
			}
		}
		if (flag == false)
			// { return null;}
			throw new DBException(entityName + "没有配置主键注解,请重新配置好！");

		// System.out.println(this.createElementIfPrimaryKeyExists(factory,
		// entityName).asXML());
		return this.createElementIfPrimaryKeyExists(factory, entityName);

	}

	/**
	 * 对单个VO实体类生成对应的实体类节点,当这个实体类必须有po类的注解才会生成
	 * 
	 * @param factory
	 * @param entityName
	 * @return Element entity
	 * @throws ClassNotFoundException
	 * @throws DBException
	 */
	private Element createElementByVo(DocumentFactory factory, String entityName)
			throws ClassNotFoundException, DBException {
		Class<?> entity = null;

		entity = Class.forName(entityName);
		boolean flag = false;

		Annotation poClassAnnotation = entity
				.getAnnotation(PoClassAnnotation.class);
		if (poClassAnnotation != null) {
			flag = true;
		}
		if (flag == false)
			//throw new ErrorException(entity + " vo类没有配置po类注解,请重新配置好！");
		 { return null; }
		// System.out.println("sss");
		return this.createElementIfPoClassExist(factory, entityName);
	}

	/**
	 * 当有主键的时候开始对这个实体类生成实体类节点<entity><property /></entity>
	 * 
	 * @param factory
	 * @param entityName
	 * @return Elment entityElement
	 * @throws ClassNotFoundException
	 */
	private Element createElementIfPrimaryKeyExists(DocumentFactory factory,
			String entityName) throws ClassNotFoundException {
		Element entity = factory.createElement("entity");
		annotationUtil autil = new annotationUtil();
		Annotation tableAnnotation = autil.getTableAnnotation(entityName);
		AnnotationContentFetchHelper fetchHelper = new AnnotationContentFetchHelper();
		Map<String, String> entityConfigValue = fetchHelper
				.getAnnotationContent(tableAnnotation);
		if (entityConfigValue.get("table") == null) {
			return null;
		}
		entity.add(factory.createAttribute(entity, "className", entityName));
		entity.add(factory.createAttribute(entity, "table",
				entityConfigValue.get("table")));
		this.createElementsForPrimary(factory, fetchHelper, autil, entity,
				entityName);
		this.createElementsForForeign(factory, fetchHelper, autil, entity,
				entityName);
		this.createElementForVariable(factory, fetchHelper, autil, entity,
				entityName);
		return entity;
	}

	/**
	 * 当有po类注解的时候开始对这个实体类生成实体类节点<entity><property /></entity>
	 * 
	 * @param factory
	 * @param entityName
	 * @return Element entity
	 * @throws ClassNotFoundException
	 */
	private Element createElementIfPoClassExist(DocumentFactory factory,
			String entityName) throws ClassNotFoundException {
		Element entity = factory.createElement("entity");
		annotationUtil autil = new annotationUtil();

		Annotation poClassAnnotation = autil.getPoClassAnnotation(entityName);
		AnnotationContentFetchHelper fetchHelper = new AnnotationContentFetchHelper();
		Map<String, String> entityConfigValue = fetchHelper
				.getAnnotationContentByVoClass(poClassAnnotation);
		if (entityConfigValue.get("Class") == null) {
			return null;
		}

		entity.add(factory.createAttribute(entity, "className", entityName));
		entity.add(factory.createAttribute(entity, "Class",
				entityConfigValue.get("Class")));

		this.createElementForByFieldAnnotationToVo(factory, fetchHelper, autil,
				entity, entityName);

		this.createElementForVariable(factory, fetchHelper, autil, entity,
				entityName);

		return entity;
	}

	/**
	 * 对VO实体类的变量(配上注解的字段属性)创造节点<property />
	 * 
	 * @param factory
	 * @param fetchHelper
	 * @param autil
	 * @param entity
	 * @param entityName
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	private void createElementForByFieldAnnotationToVo(DocumentFactory factory,
			AnnotationContentFetchHelper fetchHelper, annotationUtil autil,
			Element entity, String entityName) throws SecurityException,
			ClassNotFoundException {
		List<EntityVaribleContainer> containers = autil
				.getEntityVaribleContainerByVo(entityName);
		for (EntityVaribleContainer container : containers) {
			Element idElement = factory.createElement("property");
			this.addAttributeForElement(factory, idElement, container,
					fetchHelper);
			// System.out.println(idElement);
			entity.add(idElement);
		}
	}

	/**
	 * 对实体类的变量(没有配上注解的字段属性)创造节点<property />
	 * 
	 * @param factory
	 * @param fetchHelper
	 * @param autil
	 * @param entity
	 * @param entityName
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	private void createElementForVariable(DocumentFactory factory,
			AnnotationContentFetchHelper fetchHelper, annotationUtil autil,
			Element entity, String entityName) throws SecurityException,
			ClassNotFoundException {
		List<EntityVaribleContainer> containers = autil
				.getOnVaribleAnnotation(entityName);

		for (EntityVaribleContainer container : containers) {
			Element idElement = factory.createElement("property");
			this.addAttributeForElement(factory, idElement, container,
					fetchHelper);
			entity.add(idElement);
		}
	}

	/**
	 * 对实体类的主键属性(配上主键注解的字段属性)创造节点<property />
	 * 
	 * @param factory
	 * @param fetchHelper
	 * @param autil
	 * @param entity
	 * @param entityName
	 * @throws ClassNotFoundException
	 */
	private void createElementsForPrimary(DocumentFactory factory,
			AnnotationContentFetchHelper fetchHelper, annotationUtil autil,
			Element entity, String entityName) throws ClassNotFoundException {
		List<EntityVaribleContainer> containers = autil
				.getEntityVaribleContainerByPrimary(entityName);
		for (EntityVaribleContainer container : containers) {
			Element idElement = factory.createElement("property");
			this.addAttributeForElement(factory, idElement, container,
					fetchHelper);
			entity.add(idElement);
		}
	}

	/**
	 * 对实体类的外键键属性(配上外键注解的字段属性)创造节点<property />
	 * 
	 * @param factory
	 * @param fetchHelper
	 * @param autil
	 * @param entity
	 * @param entityName
	 * @throws ClassNotFoundException
	 */
	private void createElementsForForeign(DocumentFactory factory,
			AnnotationContentFetchHelper fetchHelper, annotationUtil autil,
			Element entity, String entityName) throws ClassNotFoundException {
		List<EntityVaribleContainer> containers = autil
				.getForeignKeyAnnotation(entityName);
		for (EntityVaribleContainer container : containers) {
			Element idElement = factory.createElement("property");
			this.addAttributeForElement(factory, idElement, container,
					fetchHelper);
			entity.add(idElement);
		}
	}

	/**
	 * 为节点加上具体的attributeValue,<property name="" type="" className="" column=""
	 * />
	 * 
	 * @param factory
	 * @param idElement
	 * @param container
	 * @param fetchHelper
	 */
	private void addAttributeForElement(DocumentFactory factory,
			Element idElement, EntityVaribleContainer container,
			AnnotationContentFetchHelper fetchHelper) {
		idElement.add(factory.createAttribute(idElement, "name",
				container.getVariableName()));

		if (container.getVariableType() != null) {
			idElement.add(factory.createAttribute(idElement, "type",
					container.getVariableType()));
		}
		if (container.getVariableRefClass() != null) {
			idElement.add(factory.createAttribute(idElement, "className",
					container.getVariableRefClass()));
		}
		if (container.getAnnotation() == null) {
			idElement.add(factory.createAttribute(idElement, "column",
					container.getVariableName()));
		} else {

			Map<String, String> configValues = fetchHelper
					.getAnnotationContent(container.getAnnotation());
			// System.out.println("注解的详细信息： " + configValues);
			Set<String> keys = configValues.keySet();
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				String configName = iterator.next();
				String configValue = configValues.get(configName).replace(":",
						"=");
				// System.out.println(configName + " : " + configValue);
				idElement.add(factory.createAttribute(idElement, configName,
						configValue));

				// System.out.println(idElement.asXML());
			}
			if (configValues.get("column").equals("")) {
				idElement.add(factory.createAttribute(idElement, "column",
						container.getVariableName()));
			}

		}
	}
}
