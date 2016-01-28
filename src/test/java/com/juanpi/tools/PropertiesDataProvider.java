package com.juanpi.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author renta
 * @Desription Loading values from *.properties files
 * 
 * */

public class PropertiesDataProvider {

	// get the key-value
	public static String getTestData(String configFilePath, String key) {
		Configuration config = null;
		try {
			config = new PropertiesConfiguration(configFilePath);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return String.valueOf(config.getProperty(key));

	}

	//Update the properties files
	public static void writeProperties(String key, String value) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("config/goods.properties"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		OutputStream output = null;
		try {
			output = new FileOutputStream("config/goods.properties");
			for (Enumeration<?> e = properties.propertyNames(); e
					.hasMoreElements();) {
				String s = (String) e.nextElement(); 
				if (s.equals(key)) {
					//if key duplicates,  then remove
					properties.setProperty(s, value);
				} else {
					properties.setProperty(key, value);
				}
			}

			// properties.setProperty(key,value);
			properties.store(output, "wangyang modify" + new Date().toString());// 保存键值对到文件中
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
