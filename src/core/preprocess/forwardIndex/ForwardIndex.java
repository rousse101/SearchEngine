package core.preprocess.forwardIndex;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.preprocess.DictSegment;
import core.preprocess.index.originalPageGetter;
import core.preprocess.invertedIndex.WordFiled;
import core.util.DBConnection;

/************************************************* 
ForwardIndex类建立网页正向索引，对应关系为url映射到网页所含词组,为倒排索引做准备
建立过程如下，从数据库中取出url，判断该网页是否已经分析过，如果没有分析过，根据文件名和偏移
得到网页的内容，对网页内容进行分词，得到传回来的词组，然后使得url和这个词组关联起来
*************************************************/  

/**
* <p>Title: ForwardIndex</p> 
* <p>Description: 对于某个网页，调用分词系统，进行分词，然后得到url映射到词组的索引</p> 
* <p>Copyright: Copyright (c) 2010</p> 
* <p>Company: </p> 
*  @author  <a href="dreamhunter.dy@gmail.com">dongyu</a> 
*  @version  1.0 
*  @created 2010-03-17 
*/  

public class ForwardIndex {

	private DBConnection dbc = new DBConnection();
	private HashMap<String, ArrayList<WordFiled>> indexMap = new HashMap<String, ArrayList<WordFiled>>();
	private originalPageGetter pageGetter = new originalPageGetter();
	private DictSegment dictSeg = new DictSegment();
	
	public ForwardIndex()
	{}
	
	public HashMap<String, ArrayList<WordFiled>> createForwardIndex()
	{
		int num=0;
		try {
			ArrayList<WordFiled> segResult = new ArrayList<WordFiled>();
			String sql = "select * from pageindex"; // 要执行的SQL语句
			ResultSet rs = dbc.executeQuery(sql);
			String url,fileName;
			int offset = 0;
			
			//从数据库中读出字段，然后得到网页之后进行分词处理
			System.out.println("in the process of creating forwardIndex: ");
			while (rs.next()) {				
				url = rs.getString("url"); // 选择sname这列数据
//				System.out.println(url);
				
				if(url.equals("http://www.sogou.com/")){
					System.out.println();
				}
				
				fileName = rs.getString("raws");
				offset = Integer.parseInt(rs.getString("offset"));
				String htmlDoc = pageGetter.getContent(fileName, offset);
				segResult = dictSeg.SegmentFile(htmlDoc);
				indexMap.put(url, segResult);
//				while(end==last)
//				{
//						System.out.println("url\t"+url);
//						System.out.println("segResult size\t"+segResult.size());
//						Thread.sleep(10000);
//					};
				num++;
//				System.out.println("词大小: " + segResult.size());
			}

			rs.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("------------------------------------------------------------------------");
		System.out.println("create forwardIndex finished!!");
		System.out.println("循环的次数："+num);
		System.out.println("正向索引大小: " + indexMap.size());
		
		
		return indexMap;
	}
	

	public static void main(String[] args) {
		ForwardIndex forwardIndex = new ForwardIndex();
		HashMap<String, ArrayList<WordFiled>> indexMap = forwardIndex.createForwardIndex();
		
		int num=0;
		for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
			
			Map.Entry entry = (Map.Entry) iter.next();    //map.entry 同时取出键值对
		    String url = (String) entry.getKey();
		    ArrayList<WordFiled> words = (ArrayList<WordFiled>) entry.getValue();

		    System.out.println(url + " 对应的分词结果是： " + words.size());
		    }
	}

}
