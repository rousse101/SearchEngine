package core.preprocess.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import configure.Configuration;

import core.util.DBConnection;
import core.util.MD5;
import core.util.Page;

/************************************************* 
originalPageGetter类实现根据输入参数从原始raws文件中读取网页的功能
输入的参数是：rawsname， offset，验证的url信息和验证的connent摘要信息
*************************************************/  

/**
* <p>Title: originalPageGetter</p> 
* <p>Description: 从原始raws中获取网页</p> 
* <p>Copyright: Copyright (c) 2003</p> 
* <p>Company: </p> 
*  @author  <a href="dreamhunter.dy@gmail.com">dongyu</a> 
*  @version  1.0 
*  @created 2010-03-17
*/  

public class originalPageGetter {

	private String lengthFromHead;
	private String dateFromHead;
	private String titleFromHead;
	private String urlFromHead;
	private Configuration conf = new Configuration();
	
	
	
	public String getDateFromHead() {
		return dateFromHead;
	}

	public void setDateFromHead(String dateFromHead) {
		this.dateFromHead = dateFromHead;
	}

	public String getTitleFromHead() {
		return titleFromHead;
	}

	public void setTitleFromHead(String titleFromHead) {
		this.titleFromHead = titleFromHead;
	}

	public String getPage(String url)
	{
		Page page = getRawsInfo(url);
		String content = "";
		try {
			//从数据库中读出的文件名中不含有分隔符，需要预处理一下
			StringBuffer tfileName = new StringBuffer();
			tfileName.append(page.getRawName());
			tfileName.insert(4, "\\");
			String fileName = conf.getValue
				("INDEXPATH")+"\\"+tfileName.toString();
			
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bfReader = new BufferedReader(fileReader);

			String word;
			bfReader.skip(page.getOffset());
			
			readRawHead(bfReader);
			content = readRawContent(bfReader);
			
			MD5 md5 = new MD5();
			String contentMD5 = md5.getMD5ofStr(content);
			
			if(contentMD5.equals(page.getConnent()))
				System.out.println("一样的哦");
			else
				System.out.println("不一样的哦");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return content;	
	}
	
	//重载的getPage函数，改叫getContent了，通过传入的文件名和偏移得到网页内容
	public String getContent(String file, int offset)
	{
		
		String content = "";
		BufferedReader bfReader = null;
		try {			
			//从数据库中读出的文件名中不含有分隔符，需要预处理一下
			StringBuffer tfileName = new StringBuffer();
			tfileName.append(file);
			String fileName = conf.getValue
			("INDEXPATH")+"\\Index\\"+tfileName.toString();
			
			FileReader fileReader = new FileReader(fileName.toString());
			bfReader = new BufferedReader(fileReader);

			for(int i =0;i<offset*6;i++)bfReader.readLine();
			readRawHead(bfReader);
			content = readRawContent(bfReader);
			bfReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bfReader!=null)
				try {
					bfReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return content;	
	}
	
	public Page getRawsInfo(String url)
	{
		DBConnection dbc = new DBConnection();
		String sql = " select * from pageindex where url='"+url+"' ";
		ResultSet rs = dbc.executeQuery(sql);
		
		String connent = "";
		String raws = "";
		int offset = 0;
		
		try {
			while(rs.next())
			{
				connent = rs.getString("connent"); // 选择connent这列数据
				offset = Integer.parseInt(rs.getString("offset")); // 选择offset这列数据
				raws = rs.getString("raws"); // 选择connent这列数据
			}
			
			return new Page(url, offset, connent, raws);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String readRawHead(BufferedReader bfReader)
	{		
		//String urlLine = null;
		try {	
			urlFromHead = bfReader.readLine();
			if(urlFromHead != null)
				urlFromHead = urlFromHead.substring(urlFromHead.indexOf(":")+1, urlFromHead.length());
			
			dateFromHead = bfReader.readLine();	
			if(dateFromHead != null)
				dateFromHead = dateFromHead.substring(dateFromHead.indexOf(":")+1, dateFromHead.length());
			
			titleFromHead = bfReader.readLine();	
			if(titleFromHead != null)
				titleFromHead = titleFromHead.substring(titleFromHead.indexOf(":")+1, titleFromHead.length());
			
			lengthFromHead = bfReader.readLine();	
			if(lengthFromHead != null)
				lengthFromHead = lengthFromHead.substring(lengthFromHead.indexOf(":")+1, lengthFromHead.length());
			
			bfReader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}		
		return lengthFromHead;
	}
	
	private String readRawContent(BufferedReader bfReader)
	{
		String content=null;
		try {
			content = bfReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
}
