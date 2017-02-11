package org.apache.jmeter.protocol.http.sampler;

import org.apache.jmeter.samplers.SampleResult;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.*;
import java.net.*;

public class ShareJson {
//	public static String INPUT_JSON;
//	public static String OUTPUT_JSON;
    public static Map map;
	
	public ShareJson(){
		try{
		map=InitDataSource();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	public int InitOutputJson(String outputjson){
		int sucessflag=0;
		if(outputjson != null && outputjson != ""){
			String OUTPUT_JSON=outputjson;
		    sucessflag=OUTPUT_JSON.indexOf("\"code\":\"0000\"");
			
		}
		return sucessflag;
		
	}
	public String GetName(String inputJson){
		int head=inputJson.indexOf("OI_");
		int tail=inputJson.indexOf("\"", head);
		String name=inputJson.substring(head, tail);
		return name;
		
	}
	 public static Map FindJson(String name){
        String driver = "oracle.jdbc.OracleDriver";
	    String url = map.get("database").toString();
	    String user = map.get("user").toString();
	    String password = map.get("password").toString();
        String sql="select * from base.soa_message where busi_code='"+name+"'";
        Connection con = null;
	    PreparedStatement pstm = null;
	    ResultSet rs = null;
        Map JsonMap=new HashMap();
         try{
             Class.forName(driver);
             con = DriverManager.getConnection(url, user, password);
             pstm = con.prepareStatement(sql);
	         rs = pstm.executeQuery();
             while(rs.next()){
                 String request=rs.getString("input_json");
                 //request=JsonFormatTool.formatJson(request.toString(),"  ");
                 JsonMap.put("request", request);
                 
             }
             try{
                 rs.close();
                 pstm.close();
                 con.close();
                 }catch(SQLException e){
                     e.printStackTrace();
                 }
         } catch(Exception e){
             e.printStackTrace();
         }
         System.out.println("查找接口："+name);
         return JsonMap;
     }
	 
	 public int InsertJson(String name,String Json){
        String driver = "oracle.jdbc.OracleDriver";
	    String url = map.get("database").toString();
	    String user = map.get("user").toString();
	    String password = map.get("password").toString();
        Connection con = null;
	    PreparedStatement pstm = null;
        int remark=0;
	    String busi_code=name;
            String input_json=Json;
            String describe="";
            String param="";
            String paramsql="";
            //查询接口说明
           
            String selectsql=map.get("interfacesql").toString();
            try{
                Class.forName(driver);
                con = DriverManager.getConnection(url, user, password);
                ResultSet rs = null;
                selectsql=selectsql.replace("$OIname$", name);
                pstm = con.prepareStatement(selectsql);
		        rs = pstm.executeQuery();
                while(rs.next()){                      
                describe=rs.getString("busi_access_name");
                }
                       
            }catch(Exception e){
        
                 e.printStackTrace();
    
             }
           
            String sql="insert into base.soa_message values('"+busi_code+"','"+describe+"','"+input_json+"','"+param+"','"+paramsql+"',sysdate)";
             try{
                pstm = con.prepareStatement(sql);
                remark=pstm.executeUpdate(sql);
                pstm.close();
                con.close();
        
    
             }catch(Exception e){
        
                 e.printStackTrace();
    
             }
        System.out.println(name+"入库成功");
        return remark;
    }
	 public static Map InitDataSource() throws Exception{
	     //INIT=1;
	     String database;//数据库
	     String paramsql;//查询参数SQL
	     String interfacesql;//查询interface_idSQL
	     String user;
	     String password;
	     Map map=new HashMap();
	     //提取配置
	        String path="DataBase.txt";
	        File file=new File(path);
	        Reader in=new FileReader(file);
	        char[]c=new char[(int)file.length()];
	        in.read(c);
	        
	        in.close();
	        String tmp=new String(c);
	         
	        //提取数据库url
	        int left =tmp.indexOf("{");
	        int right=tmp.indexOf("}");
	        database=tmp.substring(left+1,right);
	        map.put("database", database);
	        //用户名
	        left=tmp.indexOf("{", right);
	        right=tmp.indexOf("}",left);
	        user=tmp.substring(left+1,right);
	        map.put("user", user);
	        //密码
	        left=tmp.indexOf("{", right);
	        right=tmp.indexOf("}",left);
	        password=tmp.substring(left+1,right);
	        map.put("password", password);
	        //提取查询参数SQL
	        left=tmp.indexOf("{", right);
	        right=tmp.indexOf("}",left);
	        paramsql=tmp.substring(left+1,right);
	        map.put("paramsql", paramsql);
	        //提取查询interface_idSQL
	        left=tmp.indexOf("{", right);
	        right=tmp.indexOf("}",left);
	        interfacesql=tmp.substring(left+1,right);
	        map.put("interfacesql", interfacesql);
	        
	        return map;
	        
	    }
	
	 public static void main(String[] args){
		

	}

}
