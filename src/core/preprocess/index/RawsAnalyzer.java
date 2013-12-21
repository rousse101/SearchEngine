package core.preprocess.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import core.util.DBConnection;
import core.util.HtmlParser;
import core.util.MD5;
import core.util.Page;

/************************************************* 
RawsAnalyzer类实现了从原始网页集合Raws的分析操作，在完整MD5摘要算法之后，
建立网页URL、网页内容摘要、网页在Raws中偏移的映射、所属Raws的映射 
算法传入的参数为raws所在的目录，需要遍历其中的众多文件
*************************************************/  

/**
* <p>Title: RawsAnalyzer</p> 
* <p>Description: 从原始网页集合，完成映射解析</p> 
* <p>Copyright: Copyright (c) 2003</p> 
* <p>Company: </p> 
*  @author  <a href="dreamhunter.dy@gmail.com">dongyu</a> 
*  @version  1.0 
*  @created 2010-03-16 
*/  

public class RawsAnalyzer {

	private DBConnection dbc = new DBConnection();
	private MD5 md5 = new MD5();
//	private int offset;
	private Page page;
	private String rootDirectory;
	
	private String contentMD5;
	private String Rawurl;
	private String Rawtime;
	
	public RawsAnalyzer(String rootName)
	{
		this.rootDirectory = rootName;
		page = new Page();
	}
	
	public void createPageIndex()
	{
		ArrayList<String> fileNames = getSubFile(rootDirectory);
		for(String fileName : fileNames)
			createPageIndex(fileName);	
	}
	//TODO
	public int createRawIndex(BufferedWriter bfWriter,String htmlDoc){
		
		
		HtmlParser parser = new HtmlParser();
		String temp = null;
		try {
			temp = (new String(htmlDoc.getBytes("GBK"),parser.htmlCode(htmlDoc)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String htmlText = parser.html2Text(temp);
		contentMD5 = md5.getMD5ofStr(htmlText);
		String titleSentance = parser.htmlTitle(temp);
		
		
		String URLStr = "url:" + Rawurl + "\n";
		String dateStr = "date:" + Rawtime + "\n";
		String titleStr = "tittle:" + titleSentance + "\n";
		String textlen = "length:" + htmlText.length() + "\n";
		System.out.println("the url is " + URLStr);
		
		System.out.println(contentMD5);
		try{
		//数据头部分
		bfWriter.append(URLStr);
		bfWriter.append(dateStr);
		bfWriter.append(titleStr);
		bfWriter.append(textlen);
		bfWriter.newLine();
		
		//数据部分
		bfWriter.append(htmlText);
		bfWriter.newLine();
		
		bfWriter.flush();	
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	public void createPageIndex(String fileName)
	{
		try
		{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bfReader = new BufferedReader(fileReader);
			String myfile = "Text"+fileName.substring(fileName.indexOf("_"),fileName.length());
			String RawText = "Index\\"+myfile;
			System.out.println(RawText);
			File file = new File(RawText);           //设定输出的文件名
			BufferedWriter bfWriter= null;
			try {
				file.createNewFile();
				bfWriter = new BufferedWriter(new FileWriter(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String word;
			int offset = 0;
			int oldOffset = 0;
			
			//bfReader已经把version:1.0读入了
			while((word = bfReader.readLine()) != null)
			{
				oldOffset = offset;
				readRawHead(bfReader);
				String content = readRawContent(bfReader);
				
				offset += createRawIndex( bfWriter,content);
				
//				System.out.println("the offset in " + fileName +" is: " + offset);
//				System.out.println("the url is " + url);
				
				page.setPage(Rawurl, oldOffset, contentMD5, myfile,Rawtime);
				page.add2DB(dbc);
			}	
			bfWriter.close();
			bfReader.close();
			//dbc.close();
			
			System.out.println("finish the exectution of this raw file");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String readRawHead(BufferedReader bfReader)
	{		
		String urlLine = null;
		try {
			
			urlLine = bfReader.readLine();	
			if(urlLine != null)
				Rawurl = urlLine.substring(urlLine.indexOf(":")+1, urlLine.length());
			
			String date = bfReader.readLine();	
			if(date != null)
				Rawtime = date.substring(date.indexOf(":")+1, date.length());
			String temp;
			while(!(temp = bfReader.readLine()).trim().isEmpty())
			{
				;
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return urlLine;
	}
	
	private String readRawContent(BufferedReader bfReader)
	{
		StringBuffer strBuffer = new StringBuffer();
		
		try {		
			String word;
			while((word = bfReader.readLine()) != null)
			{
				if(word.trim().isEmpty())
					break;
				else
					strBuffer.append(word + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return strBuffer.toString();
	}
	
	public static ArrayList<String> getSubFile(String fileName) {   
		
		ArrayList<String> fileNames = new ArrayList<String>();
		
		File parentF = new File(fileName);   
		
		if (!parentF.exists()) {   
			System.out.println("unexisting file or directory");   
		    return null;   
		}   
		if (parentF.isFile()) {   
			System.out.println("it is a file");
			fileNames.add(parentF.getAbsolutePath());   
		    return fileNames;   
		}   
		
		System.out.println(fileName + " isn't  a file");
		String[] subFiles = parentF.list();   
		for (int i = 0; i < subFiles.length; i++) {   
			fileNames.add(fileName + "\\" + subFiles[i]); 
		}   
		
		return fileNames;
	}
	
	public static void main(String[] args) {
		DBConnection dbc = new DBConnection();
		String sql ="delete from pageindex";
		dbc.executeUpdate(sql);
		RawsAnalyzer analyzer = new RawsAnalyzer("Raws");
		analyzer.createPageIndex();

	}
	
}
