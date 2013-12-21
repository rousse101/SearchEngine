package core.preprocess.forwardIndex;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import core.preprocess.DictSegment;
import core.preprocess.index.originalPageGetter;
import core.preprocess.invertedIndex.DocPos;
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
	private HashMap<String, HashMap<String,DocPos>> indexMap = new HashMap<String, HashMap<String,DocPos>>();
	private originalPageGetter pageGetter = new originalPageGetter();
	private DictSegment dictSeg = new DictSegment();
	
	public ForwardIndex()
	{}
	
	public HashMap<String, HashMap<String,DocPos>> createForwardIndex()
	{
		int num=0;
		try {
			HashMap<String,DocPos> segResult = new HashMap<String,DocPos>();
			String sql = "select * from pageindex"; // 要执行的SQL语句
			ResultSet rs = dbc.executeQuery(sql);
			String url,fileName;
			int offset = 0;
			
			//从数据库中读出字段，然后得到网页之后进行分词处理
			System.out.println("in the process of creating forwardIndex: ");
			while (rs.next()) {				
				url = rs.getString("url"); // 选择sname这列数据
				Date start = new Date();
				fileName = rs.getString("raws");
				offset = Integer.parseInt(rs.getString("offset"));
				String htmlDoc = pageGetter.getContent(fileName, offset);
				String title  = pageGetter.getTitleFromHead();
				Date doc = new Date();
				segResult = dictSeg.SegmentFile(htmlDoc,title);
				Date seg = new Date();
				indexMap.put(url, segResult);
				Date ind = new Date();
				long doctime = doc.getTime()-start.getTime();
				long segtime = seg.getTime()-doc.getTime();
				long indextime = ind.getTime()-seg.getTime();
//				System.out.print("读Html耗时:"+doctime+"ms");
//				System.out.print("\t分词耗时:"+segtime+"ms");
//				System.out.println("\t加索引耗时:"+indextime+"ms");
				num++;
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
	
	public Boolean WriteForwardIndex(HashMap<String, HashMap<String,DocPos>> indexMap){
		try {
			FileOutputStream outStream = new FileOutputStream("Index\\ForwardIndex.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
			objectOutputStream.writeInt(indexMap.size());
			for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();    //map.entry 同时取出键值对
			    String url = (String) entry.getKey();
			    HashMap<String,DocPos> words = (HashMap<String,DocPos>) entry.getValue();
			    objectOutputStream.writeObject(url);
			    objectOutputStream.writeObject(words);
			    objectOutputStream.reset();
			}
//			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public HashMap<String,HashMap<String,DocPos>>  ReaderForwardIndex(){
		HashMap<String, HashMap<String,DocPos>> indexMap = new HashMap<String,HashMap<String,DocPos>>();
		try {
			FileInputStream freader = new FileInputStream("Index\\ForwardIndex.txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			try {
				int num = objectInputStream.readInt();
				for(int n =0;n<num;n++){
					String url = (String)objectInputStream.readObject();
					HashMap<String,DocPos> wf = (HashMap<String,DocPos>)objectInputStream.readObject();
					indexMap.put(url, wf);
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return indexMap;
		
	}
	public static void main(String[] args) {
		ForwardIndex forwardIndex = new ForwardIndex();
		
		HashMap<String, HashMap<String,DocPos>> indexMap = forwardIndex.createForwardIndex();
		if(forwardIndex.WriteForwardIndex(indexMap))
			System.out.println("索引写入成功，请查看Index/ForwardIndex.txt！！");
		else
			System.out.println("索引写入失败！！");
		
//		TO TEST
//		HashMap<String,  HashMap<String ,DocPos>> indexMap = forwardIndex.ReaderForwardIndex();
//		for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
//			
//			Map.Entry entry = (Map.Entry) iter.next();    //map.entry 同时取出键值对
//		    String url = (String) entry.getKey();
//		    HashMap<String ,DocPos> words = ( HashMap<String ,DocPos>) entry.getValue();
//			for (Iterator it = words.entrySet().iterator(); it.hasNext();) {
//				
//				Map.Entry ent = (Map.Entry) it.next();    //map.entry 同时取出键值对
//			    String word = (String) ent.getKey();
//			    DocPos dp = (DocPos) ent.getValue();
//			    System.out.println(url + " 对应的分词结果是： ");
//			    System.out.print("\t分词是    "+word);
//			    System.out.print("\t标题中次数   "+dp.getTitleTime());
//			    System.out.println("\t正文中结果是： "+dp.getBodyTime());
//		    }
//		}
	}

}
