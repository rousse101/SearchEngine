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
				Date start = new Date();
				fileName = rs.getString("raws");
				offset = Integer.parseInt(rs.getString("offset"));
				String htmlDoc = pageGetter.getContent(fileName, offset);
				Date doc = new Date();
				segResult = dictSeg.SegmentFile(htmlDoc);
				Date seg = new Date();
				indexMap.put(url, segResult);
				Date ind = new Date();
				long doctime = doc.getTime()-start.getTime();
				long segtime = seg.getTime()-doc.getTime();
				long indextime = ind.getTime()-seg.getTime();
				System.out.print("��Html��ʱ:"+doctime+"ms");
				System.out.print("\t�ִʺ�ʱ:"+segtime+"ms");
				System.out.println("\t��������ʱ:"+indextime+"ms");
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
		System.out.println("ѭ���Ĵ�����"+num);
		System.out.println("����������С: " + indexMap.size());
		
		
		return indexMap;
	}
	
	public Boolean WriteForwardIndex(HashMap<String, ArrayList<WordFiled>> indexMap){
		try {
			FileOutputStream outStream = new FileOutputStream("Index\\ForwardIndex.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
			objectOutputStream.writeInt(indexMap.size());
			for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();    //map.entry ͬʱȡ����ֵ��
			    String url = (String) entry.getKey();
			    ArrayList<WordFiled> words = (ArrayList<WordFiled>) entry.getValue();
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
	public HashMap<String, ArrayList<WordFiled>>  ReaderForwardIndex(){
		HashMap<String, ArrayList<WordFiled>> indexMap = new HashMap<String, ArrayList<WordFiled>>();
		try {
			FileInputStream freader = new FileInputStream("Index\\ForwardIndex.txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			try {
				int num = objectInputStream.readInt();
				for(int n =0;n<num;n++){
					String url = (String)objectInputStream.readObject();
					ArrayList<WordFiled> wf = (ArrayList<WordFiled>)objectInputStream.readObject();
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
		HashMap<String, ArrayList<WordFiled>> indexMap = forwardIndex.createForwardIndex();
		if(forwardIndex.WriteForwardIndex(indexMap))
			System.out.println("����д��ɹ�����鿴Index/ForwardIndex.txt����");
		else
			System.out.println("����д��ʧ�ܣ���");
		
//		TO TEST
//		HashMap<String, ArrayList<WordFiled>> indexMap = forwardIndex.ReaderForwardIndex();
//		for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
//			
//			Map.Entry entry = (Map.Entry) iter.next();    //map.entry ͬʱȡ����ֵ��
//		    String url = (String) entry.getKey();
//		    ArrayList<WordFiled> words = (ArrayList<WordFiled>) entry.getValue();
//
//		    System.out.println(url + " ��Ӧ�ķִʽ���ǣ� " + words.size());
//		    }
	}

}
