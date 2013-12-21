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
				String title  = pageGetter.getTitleFromHead();
				Date doc = new Date();
				segResult = dictSeg.SegmentFile(htmlDoc,title);
				Date seg = new Date();
				indexMap.put(url, segResult);
				Date ind = new Date();
				long doctime = doc.getTime()-start.getTime();
				long segtime = seg.getTime()-doc.getTime();
				long indextime = ind.getTime()-seg.getTime();
//				System.out.print("��Html��ʱ:"+doctime+"ms");
//				System.out.print("\t�ִʺ�ʱ:"+segtime+"ms");
//				System.out.println("\t��������ʱ:"+indextime+"ms");
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
	
	public Boolean WriteForwardIndex(HashMap<String, HashMap<String,DocPos>> indexMap){
		try {
			FileOutputStream outStream = new FileOutputStream("Index\\ForwardIndex.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
			objectOutputStream.writeInt(indexMap.size());
			for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();    //map.entry ͬʱȡ����ֵ��
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
			System.out.println("����д��ɹ�����鿴Index/ForwardIndex.txt����");
		else
			System.out.println("����д��ʧ�ܣ���");
		
//		TO TEST
//		HashMap<String,  HashMap<String ,DocPos>> indexMap = forwardIndex.ReaderForwardIndex();
//		for (Iterator iter = indexMap.entrySet().iterator(); iter.hasNext();) {
//			
//			Map.Entry entry = (Map.Entry) iter.next();    //map.entry ͬʱȡ����ֵ��
//		    String url = (String) entry.getKey();
//		    HashMap<String ,DocPos> words = ( HashMap<String ,DocPos>) entry.getValue();
//			for (Iterator it = words.entrySet().iterator(); it.hasNext();) {
//				
//				Map.Entry ent = (Map.Entry) it.next();    //map.entry ͬʱȡ����ֵ��
//			    String word = (String) ent.getKey();
//			    DocPos dp = (DocPos) ent.getValue();
//			    System.out.println(url + " ��Ӧ�ķִʽ���ǣ� ");
//			    System.out.print("\t�ִ���    "+word);
//			    System.out.print("\t�����д���   "+dp.getTitleTime());
//			    System.out.println("\t�����н���ǣ� "+dp.getBodyTime());
//		    }
//		}
	}

}
