package com.test.session;
/*
 * 1：把配置文件加载到内存，即：把他们放到configuration类对象中
 * 2:生产sqlsession
 */

import com.test.config.Configuration;
import com.test.config.MappedStatement;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class SqlsessionFactory {

	private Configuration configuration = new Configuration();
	
	public SqlsessionFactory() {
		//1、加载数据库信息
		loadDBinfo();
		//2、加载mapper信息
		loadMapinfo();
	}

	private void loadMapinfo() {
		InputStream inputStream = SqlsessionFactory.class.getClassLoader().getResourceAsStream("application.properties");
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			String jdbcurl = properties.getProperty("jdbcurl");
			String driverclass = properties.getProperty("driverclass");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			configuration.setJdbcDriver(driverclass);
			configuration.setJdbcUrl(jdbcurl);
			configuration.setJdbcUsername(username);
			configuration.setJdbcPassword(password);
		} catch (Exception e) {
		}
	}

	private void loadDBinfo() {
		URL resources = null;
		resources = SqlsessionFactory.class.getClassLoader().getResource("mapper");
		File mappers = new File(resources.getFile());
		if(mappers.isDirectory()) {
			File[] files = mappers.listFiles();
			for (File file : files) {
				loadMapperInfo(file);
			}
		}
	}
	
	private void loadMapperInfo(File file) {
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(file);
			System.out.println(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element node = document.getRootElement();
		String namespace = node.attribute("namespace").getData().toString();
		List<Element> selects = node.elements("select");
		for (Element element : selects) {
			MappedStatement mappedStatement = new MappedStatement();
			String id = element.attribute("id").getData().toString();
			String resultMap = element.attribute("resultMap").getData().toString();
			String sql = element.getData().toString();
			String sourceid = namespace+"."+id;
			mappedStatement.setSourceid(sourceid);
			mappedStatement.setResultMap(resultMap);
			mappedStatement.setSql(sql);
			mappedStatement.setNamespace(namespace);
			configuration.getMappedstatements().put(sourceid, mappedStatement);
		}
	}

	public static void main(String[] args) {
		InputStream inputStream = SqlsessionFactory.class.getClassLoader().getResourceAsStream("application.properties");
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			String jdbcurl = properties.getProperty("jdbcurl");
			System.out.println(jdbcurl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Sqlsession openSqlsession() {
		return new DefaultSqlsession(configuration);
	}
}
