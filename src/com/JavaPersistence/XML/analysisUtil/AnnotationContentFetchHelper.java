package com.JavaPersistence.XML.analysisUtil;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

//import com.JavaPersistence.util.annotationUtil;

/**
* 获取这个注解的详细信息的工具类
* 
* @author AntsMarch
* 
*/
public class AnnotationContentFetchHelper {
	/**
	 * 传入一个注解的参数,通过解析分解这注解的详细信息,得到一个Map<String,String>集合;
	 * 
	 * @param annotation
	 * @return Map<String,String> result
	 */
	public Map<String, String> getAnnotationContent(Annotation annotation) {
		Map<String, String> result = null;
		String annotationToString = annotation.toString();
		annotationToString = annotationToString.substring(
				annotationToString.indexOf('(') + 1,
				annotationToString.indexOf(')')).trim();
		if (null == annotationToString || "".equals(annotationToString)) {

		} else {
			result = new HashMap<String, String>(5);
			String[] messages = annotationToString.split(",");

			if (messages.length == 1) {
				String[] messageAttribute = messages[0].split("=");
				result.put(messageAttribute[0], messageAttribute[1]);
			} else {
				for (String message : messages) {
					message = message.trim();
					String[] messageAttribute = message.split("=");
					result.put(messageAttribute[0], messageAttribute[1]);
				}
			}
		}

		return result;
	}

	/**
	 * 传入一个注解的参数,通过解析分解这注解的详细信息,得到一个Map<String,String>集合针对Vo类来解析;
	 * 
	 * @param annotation
	 * @return Map<String,String> result
	 */
	public Map<String, String> getAnnotationContentByVoClass(
			Annotation annotation) {
		Map<String, String> result = null;
		String annotationToString = annotation.toString();
		annotationToString = annotationToString.substring(
				annotationToString.indexOf('(') + 1,
				annotationToString.indexOf(')')).trim();
		if (null == annotationToString || "".equals(annotationToString)) {

		} else {
			// System.out.println(annotationToString);
			result = new HashMap<String, String>(5);
			String[] messages = annotationToString.split("=");

			result.put(messages[0], messages[1]);
		}

		return result;
	}


}
