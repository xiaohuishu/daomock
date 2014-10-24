package com.JavaPersistence.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.JavaPersistence.Annotation.FieldAnnotation;
import com.JavaPersistence.Annotation.ForeginKeysAnnotation;
import com.JavaPersistence.Annotation.PoClassAnnotation;
import com.JavaPersistence.Annotation.PrimaryKeysAnnotation;
import com.JavaPersistence.Annotation.TableAnnotation;
import com.JavaPersistence.Annotation.VaribleAnnotation;
import com.JavaPersistence.vo.EntityVaribleContainer;

/**
 * 注解工具类,对注解信息进行处理.
 * 
 * @author AntsMarch
 * 
 */
public class annotationUtil {
	/**
	 * 通过传来的实体类的Class类型来获取加在这个class类上的表名注解
	 * 
	 * @param entity
	 * @return tableName
	 */
	public String getAnnotationTableName(Class<?> entity) {
		String tableName = null;
		if (entity.isAnnotationPresent(TableAnnotation.class)) {
			TableAnnotation tableAnnotation = entity
					.getAnnotation(TableAnnotation.class);
			tableName = tableAnnotation.table();
		}

		return tableName;
	}

	/**
	 * 通过传入的实体类的Class类型,通过获取这个方法上的注解来获取方法上的主键注解
	 * 
	 * @param entity
	 * @return primaryName
	 */
	public String getAnnotationPrimaryNameByMethod(Class<?> entity) {
		String primaryName = null;
		Method[] methods = entity.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(PrimaryKeysAnnotation.class)) {
				PrimaryKeysAnnotation primary = entity
						.getAnnotation(PrimaryKeysAnnotation.class);
				primaryName = primary.column();
				break;
			}
		}
		return primaryName;
	}

	/**
	 * 通过传入的实体类的Class类型,通过获取这个字段上的注解来获取方法上的主键注解
	 * 
	 * @param entity
	 * @return primaryName
	 */
	public String getAnnotationPrimaryNameByField(Class<?> entity) {
		String primaryName = "";
		Field[] fields = entity.getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKeysAnnotation.class)) {
				// System.out.println(field.getName());
				PrimaryKeysAnnotation primary = field
						.getAnnotation(PrimaryKeysAnnotation.class);
				primaryName += primary.column() + ",";

			}
		}
		// System.out.println(primaryName.substring(0,
		// primaryName.lastIndexOf(',')));
		if (primaryName == null || primaryName.equals("")) {
			return primaryName;
		} else {
			return primaryName.substring(0, primaryName.lastIndexOf(','));
		}
	}

	/**
	 * 通过传入的类名（完整的类名）,来获取加在这个类上面的表的注解
	 * 
	 * @param className
	 * @return tableAnnotation
	 * @throws ClassNotFoundException
	 */
	public Annotation getTableAnnotation(String className)
			throws ClassNotFoundException {
		return Class.forName(className).getAnnotation(TableAnnotation.class);
	}

	/**
	 * 通过传入的类名（完整的类名）,来获取加在这个类上面的po类的注解
	 * 
	 * @param className
	 * @return PoClassAnnotation
	 * @throws ClassNotFoundException
	 */
	public Annotation getPoClassAnnotation(String className)
			throws ClassNotFoundException {
		return Class.forName(className).getAnnotation(PoClassAnnotation.class);
	}

	/**
	 * 通过传入的实体类的完整的类名,取得这个加在这个类上的主键注解信息,将变量,注解,变量类型,Class类型绑定在一起,封装到一个对象中
	 * 
	 * @param className
	 * @return EntityVaribleContainer entityContainer
	 * @throws ClassNotFoundException
	 */
	public List<EntityVaribleContainer> getEntityVaribleContainerByPrimary(
			String className) throws ClassNotFoundException {
		List<EntityVaribleContainer> entityContainerList = new ArrayList<EntityVaribleContainer>();
		// boolean flag = false;
		Field[] fields = Class.forName(className).getDeclaredFields();
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof PrimaryKeysAnnotation) {
					EntityVaribleContainer entityContainer = new EntityVaribleContainer();
					entityContainer.setAnnotation(annotation);
					entityContainer.setVariableName(field.getName());

					String fieldType = field.getType().getSimpleName()
							.toLowerCase();
					// System.out.println(className + "类中的字段：" + field.getName()
					// + " : 类型是:  "
					// + field.getType().getSimpleName().toLowerCase());
					if (!fieldType.equals("int")) {
						String a = fieldType.substring(0, 1).toUpperCase();
						String b = fieldType.substring(1, fieldType.length());
						fieldType = a + b;
					} else {
						fieldType = "Integer";
					}
					entityContainer.setVariableType(fieldType);
					entityContainerList.add(entityContainer);
					/*
					 * flag = true; break;
					 */
				}
			}
			// if (flag == true) {
			// break;
			// }
		}
		return entityContainerList;
	}

	/**
	 * 通过传入的实体类的完整的类名,取得这个加在这个类上的外键注解信息,将变量,注解,变量类型,Class类型绑定在一起,封装到一个实体变量容器对象中,
	 * 最后返回一个List<EntityVaribleContainer> container集合
	 * 
	 * @param className
	 * @return List<EntityVaribleContainer> container
	 * @throws ClassNotFoundException
	 */
	public List<EntityVaribleContainer> getForeignKeyAnnotation(String className)
			throws ClassNotFoundException {
		List<EntityVaribleContainer> container = new ArrayList<EntityVaribleContainer>(
				4);

		Field[] fields = Class.forName(className).getDeclaredFields();
		for (Field field : fields) {
			Annotation[] fieldsAnnotations = field.getAnnotations();
			for (Annotation annotation : fieldsAnnotations) {
				if (annotation instanceof ForeginKeysAnnotation) {
					EntityVaribleContainer entityVariable = new EntityVaribleContainer();
					entityVariable.setAnnotation(annotation);
					entityVariable.setVariableName(field.getName());
					entityVariable.setVariableRefClass(field.getType()
							.getName());
					container.add(entityVariable);
				}
			}
		}

		return container;
	}

	/**
	 * 获得Vo实体类上的变量注解容器信息
	 * 
	 * @param className
	 * @return List<EntityVaribleContainer> voContainerList
	 * @throws ClassNotFoundException
	 */
	public List<EntityVaribleContainer> getEntityVaribleContainerByVo(
			String className) throws ClassNotFoundException {
		List<EntityVaribleContainer> entityContainerList = new ArrayList<EntityVaribleContainer>();
		// boolean flag = false;
		Field[] fields = Class.forName(className).getDeclaredFields();
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof FieldAnnotation) {
					EntityVaribleContainer entityContainer = new EntityVaribleContainer();
					entityContainer.setAnnotation(annotation);
					entityContainer.setVariableName(field.getName());

					String fieldType = field.getType().getSimpleName()
							.toLowerCase();
					// System.out.println(className + "类中的字段：" + field.getName()
					// + " : 类型是:  "
					// + field.getType().getSimpleName().toLowerCase());
					if (!fieldType.equals("int")) {
						String a = fieldType.substring(0, 1).toUpperCase();
						String b = fieldType.substring(1, fieldType.length());
						fieldType = a + b;
					} else {
						fieldType = "Integer";
					}
					entityContainer.setVariableType(fieldType);
					entityContainerList.add(entityContainer);
					/*
					 * flag = true; break;
					 */
				}
			}
			// if (flag == true) {
			// break;
			// }
		}
		return entityContainerList;
	}

	/**
	 * 通过传入的实体类的完整的类名,取得这个加在这个类上的变量注解信息,将变量,注解,变量类型,Class类型绑定在一起,封装到一个实体变量容器对象中,
	 * 最后返回一个List<EntityVaribleContainer> entityVaribleContainer
	 * 
	 * @param className
	 * @return List<EntityVaribleContainer> entityVaribleContainer;
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public List<EntityVaribleContainer> getOnVaribleAnnotation(String className)
			throws SecurityException, ClassNotFoundException {
		List<EntityVaribleContainer> entityVaribleContainer = new ArrayList<EntityVaribleContainer>();
		Field[] fields = Class.forName(className).getDeclaredFields();

		for (Field field : fields) {
			boolean isPrimary = false;

			Annotation variableAnnotation = null;
			Annotation[] fieldsAnnotations = field.getAnnotations();
			for (Annotation annotation : fieldsAnnotations) {
				if (annotation instanceof PrimaryKeysAnnotation
						|| annotation instanceof ForeginKeysAnnotation
						|| annotation instanceof FieldAnnotation) {
					isPrimary = true;
					break;
				} else if (annotation instanceof VaribleAnnotation) {
					variableAnnotation = annotation;
					break;
				}
			}
			if (!isPrimary) {
				EntityVaribleContainer entityVarible = new EntityVaribleContainer();
				entityVarible.setAnnotation(variableAnnotation);
				entityVarible.setVariableName(field.getName());
				String type = field.getType().getSimpleName().toLowerCase();
				if (!type.equals("String")) {
					String a = type.substring(1);
					String b = type.substring(0, 1).toUpperCase();
					type = b + a;
				} else {
					type = "Integer";
				}
				entityVarible.setVariableType(type);
				entityVaribleContainer.add(entityVarible);
			}
		}

		return entityVaribleContainer;
	}

	/**
	 * 传入一个实体类对象,通过反射得到这个类中字段对应的值得键值对.注意的是field.setAccessible(true);这个必须要设置,
	 * 默认这个字段可以不能被访问
	 * 
	 * @param Object
	 *            entity
	 * @return Map<String,Object> properties
	 */
	public Map<String, Object> getAllFieldsValue(Object object) {
		Map<String, Object> properties = new HashMap<String, Object>();
		Field[] fields = object.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				properties.put(fields[i].getName(), fields[i].get(object));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	/**
	 * 传入一个实体类对象,通过反射得到这个类中字段对应的值得键值对.这个方法不用设置field.setAccessible(true);
	 * 直接通过得到实体类的readMethod()方法来得到对应字段的值.(利用java内省来做)
	 * 
	 * @param bean
	 * @return Map<String,Object> beanMap
	 */
	public Map<String, Object> getBeanInfo(Object bean) {
		Map<String, Object> beanMap = new HashMap<String, Object>();
		bean.getClass().getDeclaredFields();
		/*
		 * Method[] methos = bean.getClass().getDeclaredMethods(); for(Method
		 * method : methos) { String methodName = method.getName();
		 * 
		 * String fieldName = methodName.substring(3).toLowerCase();
		 * if(methodName.indexOf("get")==0){ try { Object ret =
		 * method.invoke(bean); if(ret!=null){ beanMap.put(fieldName, ret);
		 * 
		 * } }catch (Exception e) { e.printStackTrace(); } } }
		 */
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] propertys = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor element : propertys) {
				Method readMethod = element.getReadMethod();

				String fieldName = element.getName().toLowerCase();
				Object ret = readMethod.invoke(bean);
				if (ret != null) {
					beanMap.put(fieldName, ret);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beanMap;
	}

	/**
	 * 通过传入的实体类的Class类型,得到实体类上字段对应的PropertyDescriptior对象(java内省),就是字段的描述,
	 * 包括其getter和setter方法
	 * 
	 * @param entityClass
	 * @return Map<String,PropertyDescriptor> beanMap
	 * @throws Exception
	 */
	public Map<String, PropertyDescriptor> getBeanInfo(Class<?> entityClass)
			throws Exception {
		Map<String, PropertyDescriptor> beanMap = new HashMap<String, PropertyDescriptor>();

		BeanInfo beanInfo = Introspector.getBeanInfo(entityClass);

		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

		for (PropertyDescriptor pd : pds) {

			String fieldName = pd.getName().toLowerCase();

			beanMap.put(fieldName, pd);
		}
		return beanMap;
	}

}
