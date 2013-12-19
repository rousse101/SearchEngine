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
ForwardIndex�ཨ����ҳ������������Ӧ��ϵΪurlӳ�䵽��ҳ��������,Ϊ����������׼��
�����������£������ݿ���ȡ��url���жϸ���ҳ�Ƿ��Ѿ������������û�з������������ļ�����ƫ��
�õ���ҳ�����ݣ�����ҳ���ݽ��зִʣ��õ��������Ĵ��飬Ȼ��ʹ��url����������������
*************************************************/  

/**
* <p>Title: ForwardIndex</p> 
* <p>Description: ����ĳ����ҳ�����÷ִ�ϵͳ�����зִʣ�Ȼ��õ�urlӳ�䵽���������</p> 
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
			String sql = "select * from pageindex"; // Ҫִ�е�SQL���
			ResultSet rs = dbc.executeQuery(sql);
			String url,fileName;
			int offset = 0;
			
			//�����ݿ��ж����ֶΣ�Ȼ��õ���ҳ֮����зִʴ���
			System.out.println("in the process of creating forwardIndex: ");
			while (rs.next()) {				
				url = rs.getString("url"); // ѡ��sname��������
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
//				System.out.println("�ʴ�С: " + segResult.size());
			}

			rs.close();
		
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("------------------------------------------------------------------------");
		System.out.println("create forwardIndex finished!!");
		System.out.println("ѭ���Ĵ�����"+num);
		System.out.println("����������С: " + indexMap.size());
		
		
		return indexMap;
	}
	

	public static void main(String[] args) {
		ForwardIndex forwardIndex = new ForwardIndex();
		HashMap<String, ArrayList<WordFiled>> indexMap = forwardIndex.createForwardIndex();
		
		int num=0;
		for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
			
			Map.Entry entry = (Map.Entry) iter.next();    //map.entry ͬʱȡ����ֵ��
		    String url = (String) entry.getKey();
		    ArrayList<WordFiled> words = (ArrayList<WordFiled>) entry.getValue();

		    System.out.println(url + " ��Ӧ�ķִʽ���ǣ� " + words.size());
		    }
	}

}
