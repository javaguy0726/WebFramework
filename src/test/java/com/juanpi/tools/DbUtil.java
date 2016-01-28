package com.juanpi.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;
import org.testng.Assert;

/**
 * Utility class for manipulating database.<br/>
 * 操纵数据库工具类
 * 
 * @author Renta
 *
 */
public class DbUtil {

    private Connection conn = null;
    private QueryRunner qr = null;
    private String url;
    private String jdbcDriver;
    private String user;
    private String password;
	
    public Logger logger;
    
    
	public DbUtil(){                           //默认数据库
		this.logger = Logger.getLogger(DbUtil.class.getName());
		this.url = "jdbc:mysql://192.168.143.232:3309/js_order";
		this.jdbcDriver = "com.mysql.jdbc.Driver";
		this.user = "js_xiudang";
		this.password = "d8F02&c19G?5_b4$5c6$";
	}
	
	public DbUtil(String url, String user, String password){                        //指定数据库
		this.logger = Logger.getLogger(DbUtil.class.getName());
		this.url = "jdbc:mysql:" + url;
		this.jdbcDriver = "com.mysql.jdbc.Driver";
		this.user = user;
		this.password = password;
	}
	
	public void buildConnection(){            
		DbUtils.loadDriver(this.jdbcDriver);
			try {
				this.conn = DriverManager.getConnection(url, user, password);
				this.qr = new QueryRunner();
				
//	            List results = (List) qr.query(conn, "select id,name from guestbook", new BeanListHandler(Guestbook.class))
			} catch (SQLException e) {
				logger.error("Connection failed!");
			} 
	}

	/**
	 * 查询并将结果的每一行数据属性名值对，存入Map，再讲所有行存入List
	 * 
	 * @param sql
	 */
	public List<Map<String, Object>> queryToMapList(String sql, Object... params){
		try{
			return this.qr.query(conn, sql, new MapListHandler(), params);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询并将结果的第一行数据属性名值对存入Map
	 * 
	 * @param sql
	 */
	public Map<String, Object> queryToMap(String sql, Object... params){
		try{
			return this.qr.query(conn, sql, new MapHandler(), params);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询并将结果的第一行数据属性名值对存入Map
	 * 
	 * @param sql
	 */
	public Map<String, Object> queryToMap(String sql){
		try{
			return this.qr.query(conn, sql, new MapHandler());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Including update, insert, delete, etc. <br/>
	 * 包括数据库update，insert，delete等操作
	 * 
	 * @param sql
	 * @param params
	 * @return rows updated
	 */
	public int update(String sql, Object... params){
		int rows = 0;
		try{
			rows = this.qr.update(conn, sql, params);
		} catch (SQLException e) {
			Assert.fail("SQL Exception: maybe database connection errors occur. Please check the connection !" + e.getCause());
		}
		if(rows ==0){
			this.logger.warn(" 0 rows are updated !");
		}else{
			this.logger.info(rows+" rows are updated !");
		}
		return rows;
	}
	
	public void closeConnection(){
		try {
			if(conn != null){
				DbUtils.close(conn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}

