package com.ankur.main.joins;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.ankur.main.common.SparkBatchJob;
import com.ankur.model.json.EmployeeDeptInfo;
import com.ankur.model.json.EmployeeInfo;
import com.ankur.util.constants.StringConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import scala.Tuple2;

public class RightOuterJoin extends SparkBatchJob {

	private static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

	public static void main(String[] args) {
		String employeeInfoFilePath = getFilePath(StringConstants.SAMPLE_DATA_JOINS_FOLDER + "/" + StringConstants.EMPLOYEE_INFO_JSON_FILE);
		System.out.println(employeeInfoFilePath);
		String employeeDeptFilePath = getFilePath(StringConstants.SAMPLE_DATA_JOINS_FOLDER + "/" + StringConstants.EMPLOYEE_DEPARTMENT_JSON_FILE);
		System.out.println(employeeDeptFilePath);
		JavaSparkContext context = getContext(RightOuterJoin.class.getSimpleName());
		JavaPairRDD<String, EmployeeInfo> empInfoData = context.textFile(employeeInfoFilePath).map(str -> gson.fromJson(str, EmployeeInfo.class))
				.mapToPair(emp -> new Tuple2<String, EmployeeInfo>(emp.getId(), emp));
		JavaPairRDD<String, EmployeeDeptInfo> empDeptData = context.textFile(employeeDeptFilePath).map(str -> gson.fromJson(str, EmployeeDeptInfo.class))
				.mapToPair(emp -> new Tuple2<String, EmployeeDeptInfo>(emp.getId(), emp));
		JavaRDD<EmployeeDeptInfo> populatedEmployee = empInfoData.rightOuterJoin(empDeptData).filter(tuple -> !tuple._2._1.isPresent()).map(tuple -> tuple._2._2);
		System.out.println(populatedEmployee.count());
		populatedEmployee.foreach(emp -> System.out.println(emp));
	}
}
