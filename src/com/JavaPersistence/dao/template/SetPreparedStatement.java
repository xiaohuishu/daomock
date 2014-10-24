package com.JavaPersistence.dao.template;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PreparedStatement赋值工具类,通过传入不同的参数,参数类型,和索引来set参数到PreparedStatment中
 * 
 * @author AntsMarch
 * 
 */
public class SetPreparedStatement {

	private SetPreparedStatement() {
	}

	/**
	 * 声明8大基本数据类型
	 */
	private final static int ShortType = 0;

	private final static int IntegerType = 1;

	private final static int LongType = 2;

	private final static int FloatType = 3;

	private final static int DoubleType = 4;

	private final static int BooleanType = 5;

	private final static int StringType = 6;

	private final static int DateType = 7;

	/**
	 * setPreparedStatment具体的实现方法,通过传入的PreparedStatment对象,索引,参数类型,参数值, 来进行配置验证赋值
	 * 
	 * @param pstmt
	 * @param index
	 * @param type
	 * @param parameter
	 * @throws SQLException
	 */
	public static void setPreparedStatementByPropertiesType(
			PreparedStatement pstmt, int index, String type, Object parameter)
			throws SQLException {
		int chanageTypeBeIntValue = changePropertiesTypeToInt(type);
		switch (chanageTypeBeIntValue) {
		case IntegerType:
			pstmt.setInt(index, (Integer) parameter);
			break;
		case LongType:
			pstmt.setLong(index, (Long) parameter);
			break;
		case StringType:

			pstmt.setString(index, String.valueOf(parameter));
			break;
		case ShortType:
			pstmt.setShort(index, (Short) parameter);
			break;
		case DateType: {
			Date time = (Date) parameter;
			SimpleDateFormat formatter = new SimpleDateFormat(					
					"yyyy-MM-dd HH:mm:ss");
			String dateString = formatter.format(time);
			Timestamp timestamp = Timestamp.valueOf(dateString);
			pstmt.setTimestamp(index, timestamp);
		}
			break;
		case BooleanType:
			pstmt.setBoolean(index, Boolean.parseBoolean((String) parameter));
			break;
		case FloatType:
			pstmt.setFloat(index, (Float) parameter);
			break;
		case DoubleType:
			pstmt.setDouble(index, (Double) parameter);
			break;
		default:
			System.out.println("配置的数据类型出现错误！！！！");
		}

	}

	/**
	 * 通过传入的类型字符串,来匹配得到声明的基本数据类型的int值.
	 * 
	 * @param type
	 * @return
	 */
	private static int changePropertiesTypeToInt(String type) {
		int result = -1;
		if (type.equals("Integer") || type.equals("Int")) {

			result = IntegerType;
		} else if (type.equals("Long")) {
			result = LongType;
		} else if (type.equals("String")) {
			result = StringType;
		} else if (type.equals("Short")) {
			result = ShortType;
		} else if (type.equals("Date")) {
			result = DateType;
		} else if (type.equals("Boolean")) {
			result = BooleanType;
		} else if (type.equals("Float")) {
			result = FloatType;
		} else if (type.equals("Double")) {
			result = DoubleType;
		}

		return result;
	}
}
