package com.dessert.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SshShellUtil {

	private static Logger logger = Logger.getLogger(SshShellUtil.class);
	private Connection conn;  
    private String ipAddr;  
    private String charset = Charset.defaultCharset().toString();  
    private String userName;  
    private String password;  
    
    private static final int TIME_OUT = 1000 * 5 * 60;
	
    public SshShellUtil(String ipAddr, String userName, String password, String charset) {  
        this.ipAddr = ipAddr;  
        this.userName = userName;  
        this.password = password;  
        if (charset != null) {  
            this.charset = charset;  
        }  
    }  
    
    public boolean connect() {  
    	boolean state = false;
        this.conn = new Connection(ipAddr);  
        try {
			conn.connect();
			logger.info("正在连接远程主机..."+ this.ipAddr);
			state = conn.authenticateWithPassword(userName, password);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("连接远程主机失败. 请确认账号和密码 !");
			e.printStackTrace();
		} 
        return state;
    }  
    
    public int executeCommand(String cmds) {  
    	InputStream stdOut = null;
        InputStream stdErr = null;
        String outStr = "";                         //打印执行脚本期间的输出
        String outErr = "";                         //打印执行脚本的错误信息
        int ret = -1;
        try {  
            if (this.connect()) {
            	logger.info("连接成功，正在执行脚本中...");
                Session session = conn.openSession();
                session.execCommand(cmds);
                stdOut = new StreamGobbler(session.getStdout());
                outStr = processStream(stdOut, charset);
                stdErr = new StreamGobbler(session.getStderr());
                outErr = processStream(stdErr, charset);
                session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
                
//                System.out.println("outStr=" + outStr);
//                System.out.println("outErr=" + outErr);
                ret = session.getExitStatus();
                logger.info("执行脚本完毕.");
            }
        } catch (IOException e1) {
        	logger.error("执行过程中遇到文件读写问题.");
            e1.printStackTrace();  
        } finally {
        	 if(ret == 0){
             	logger.info("脚本正常执行完毕.");
             }else{
            	logger.warn("脚本没有正常执行完毕.");
             }
        	logger.info("关闭远程连接...");
            if (conn != null) {
                conn.close();
            }
            IOUtils.closeQuietly(stdOut);
            IOUtils.closeQuietly(stdErr);
        } 
        return ret;                            //正常执行完毕，返回0
    }  
  
    public String processStream(InputStream in, String charset) {  
      
        byte[] buf = new byte[2048];  
        StringBuffer sb = new StringBuffer();  
        try {  
            while (in.read(buf) != -1) {  
                sb.append(new String(buf, charset));  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return sb.toString();  
        
    }
    
}
