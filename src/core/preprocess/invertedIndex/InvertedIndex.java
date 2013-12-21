package core.preprocess.invertedIndex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import configure.Configuration;

import sun.misc.Compare;

import core.preprocess.forwardIndex.ForwardIndex;
import core.util.DBConnection;

/************************************************* 
InvertedIndex�ཨ����ҳ������������Ӧ��ϵΪ����ӳ��url��ͨ����������������
�����������£�����������ȡ������ֵ����������url����������ÿ�����飬����map����Ϊkey
��url��Ϊ��value���룬���õ���map���ǵ�������
*************************************************/  

/**
* <p>Title: InvertedIndex</p> 
* <p>Description: �������������������õ���������</p> 
* <p>Copyright: Copyright (c) 2010</p> 
* <p>Company: </p> 
*  @author  <a href="dreamhunter.dy@gmail.com">dongyu</a> 
*  @version  1.0 
*  @created 2010-03-17 
*/

public class InvertedIndex {

	private HashMap<String, HashMap<String,DocPos>> fordwardIndexMap;
//	private HashMap<String, ArrayList<String>> invertedIndexMap;
	//����������еĳ��ִ���������hashmap�洢�ĵ��ͱ��ĵ��еĴ�����
	private static HashMap<String, HashMap<String,DocPos>> invertedIndexMap;
	private static int N=0;
	
	public int GetTotalDocNum(){
		return N;
	}
	public ArrayList<String> TermList(String keytag){
		ArrayList<String> term = new ArrayList<String>(10);
		for (Iterator iter = invertedIndexMap.entrySet().iterator(); iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
			String word = (String) entry.getKey();
			if(word.startsWith(keytag)){
				term.add(word);
			}
			if(term.size()==10){
				break;
			}
		}
		return term;
	}
	public ArrayList<String> getMayWord(ArrayList<String> keytag){
		if(keytag.size()==1){
			String m = keytag.get(0);
			keytag.remove(0);
			for(int i=0;i<m.length();i++){
			keytag.add(m.substring(i,i+1));
			}	
		}
		ArrayList<String> term = new ArrayList<String>(keytag.size());
		for(String s : keytag){
			int max=5;
			String myword=null;
			for (Iterator iter = invertedIndexMap.entrySet().iterator(); iter.hasNext();) 
			{
				Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
				String word = (String) entry.getKey();
				int num = ((HashMap<String,DocPos>) entry.getValue()).size();
				if(word.contains(s)&&num>max&&word.length()>1){
					if(num>max){
						max = num;
						myword = word;
					}
				}
			}
			term.add(myword);
		}
		return term;
	}
	
	public Boolean WriteIndex(HashMap<String, HashMap<String,DocPos>> invertedIndexMap,int N){
		try {
			Configuration conf = new Configuration();
			FileOutputStream outStream = new FileOutputStream(conf.getValue("INDEXPATH")+"\\Index\\Index.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream); 
			objectOutputStream.writeInt(N);
			objectOutputStream.writeInt(invertedIndexMap.size());
			objectOutputStream.writeObject(invertedIndexMap);
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public Boolean ReaderIndex(){
		
		Configuration conf = new Configuration();
		try {
			FileInputStream freader = new FileInputStream(conf.getValue("INDEXPATH")+"\\Index\\Index.txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			try {
				this.N= objectInputStream.readInt();
				int num = objectInputStream.readInt();
				invertedIndexMap =new  HashMap<String, HashMap<String,DocPos>>(num);
				invertedIndexMap = (HashMap<String, HashMap<String,DocPos>>)objectInputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			freader.close();
			objectInputStream.close();
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if(invertedIndexMap.size()>0)
		return true;
		else 
		return false;
		
	}
	
	public int createInvertedIndex() {
		ForwardIndex forwardIndex = new ForwardIndex();
		fordwardIndexMap = forwardIndex.ReaderForwardIndex();
		invertedIndexMap = new HashMap<String, HashMap<String,DocPos>>();
		N = fordwardIndexMap.size();
		//����ԭ�����������������е���
		for (Iterator iter = fordwardIndexMap.entrySet().iterator(); iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
			String url = (String) entry.getKey();
			HashMap<String,DocPos> words = (HashMap<String,DocPos>) entry.getValue();
//			��ȡ�ĵ��Ĵ�����
			
			for (Iterator it = words.entrySet().iterator(); it.hasNext();) 
			{
				Map.Entry en = (Map.Entry) it.next(); // map.entry ͬʱȡ����ֵ��
				String word = (String) en.getKey();
				DocPos dp  = (DocPos) en.getValue();
				
				if(!invertedIndexMap.containsKey(word))
				{
					HashMap<String,DocPos> urls = new HashMap<String,DocPos>();
					urls.put(url, dp);
					invertedIndexMap.put(word, urls);
				}
				else{
					HashMap<String,DocPos> urls = invertedIndexMap.get(word);
					if(urls.containsKey(url)){
						DocPos dpin = urls.get(url);
						int tn = dp.getTitleTime()+dpin.getTitleTime();
						int bn = dp.getBodyTime()+dpin.getBodyTime();
						dp.setBodyTime(bn);
						dp.setTitleTime(tn);
						urls.put(url, dp);
						invertedIndexMap.put(word, urls);
					}
					else{
						urls.put(url, dp);
						invertedIndexMap.put(word, urls);
					}
						
				}
					
			}
		}
		System.out.println("***************************************************************");
		System.out.println("create invertedIndex finished!!");
		System.out.println("the size of invertedIndex is : " + invertedIndexMap.size());
		return N;
	}

	public HashMap<String,HashMap<String,DocPos>> getInvertedIndex()
	{
		return invertedIndexMap;
	}
	//�����ĵ��ĵ÷�idfΪ���ִʵ��ĵ�����
	//idf*�ʳ��ֵ�����
	public HashMap<String,Double> DocScore(String keyWord){
		double Wtitle = 0.6;
		double Wbody = 0.4;
		HashMap<String,Double> result = new HashMap<String,Double>();
		HashMap<String,DocPos> urls = invertedIndexMap.get(keyWord);
		if(urls != null)
		{
			double N = GetTotalDocNum();
			double df = N/urls.size();
			double idf = Math.log10(df);
			for (Iterator iter = urls.entrySet().iterator(); iter.hasNext();) 
			{
				Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
				String url = (String) entry.getKey();
				DocPos dp  = (DocPos)entry.getValue();
				double tf_title ,tf_body;
				if(dp.getTitleTime()==0){
					tf_title = 0;
				}
				else{
					tf_title =1+ Math.log10(dp.getTitleTime());
				}
				if(dp.getBodyTime()==0){
					tf_body =0;
				}
				else{
					tf_body = 1+Math.log10(dp.getBodyTime());
				}
				double tf = Wtitle*tf_title+Wbody*tf_body;
				double score = idf *tf;
				result.put(url, score);
			}
			return result;
				
		}
		else
		{
			
			System.out.println("���ϧ��û�ҵ��ؼ���"+keyWord);
			return null;
		}
	}
	//ʵ�ֽӿ�����,�ӵ�С������ʽ
	class ByValueComparator implements Comparator<String> {
		HashMap<String, Double> here;
		public ByValueComparator(HashMap<String, Double> urls){
			here  = urls;
		}
		public int compare(String o1, String o2) {
			if(here.containsKey(o1)&&here.containsKey(o2)){
				if(here.get(o1)<here.get(o2))
					return 1;
				else 
					return -1;
			}
			else{
				return 0;
			}
		}
	}
	//����value��double��hashmap
	public ArrayList<String> SortDoc(HashMap<String,Double> urls){
		ArrayList<String> result = new ArrayList<String>(urls.keySet());
		ByValueComparator bvc = new ByValueComparator(urls);
		Collections.sort(result,bvc);
		return result;
	}
	//����ڲ�����ʵ������valueֵ��date��hasmap
	class ByTimeComparator implements Comparator<String> {
		HashMap<String, Date> here;
		public ByTimeComparator(HashMap<String, Date> urls){
			here  = urls;
		}
		public int compare(String o1, String o2) {
			if(here.containsKey(o1)&&here.containsKey(o2)){
				if(here.get(o1).compareTo(here.get(o2))<0)return 1;
				else return -1;
			}
			else{
				return 0;
			}
		}
	}
	//sortBytime�ڲ�ʹ�õĵ��÷�������ʹ��bYtimecompare�����ࡣ
	private ArrayList <String> SortTime(HashMap<String,Date> urls){
		ArrayList<String> result = new ArrayList<String>(urls.keySet());
		ByTimeComparator bvc = new ByTimeComparator(urls);
		Collections.sort(result,bvc);
		System.out.println("over sort time");
		return result;
	}
	//��ʱ������url
	public ArrayList<String> SortBytime(ArrayList<String> urls){
		HashMap<String,Date> sortmap = new HashMap<String,Date>(urls.size());
		DBConnection dbc = new DBConnection();
		DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(String url :urls){
			String sql = "select * from pageindex where url ='"+url+"'";
			ResultSet rs = dbc.executeQuery(sql);
			try{
				if(rs.next())
					{
						String date = rs.getString("pagetime"); // ѡ��sname��������
						Date time =null;
						try {
							time = df.parse(date);
						} catch (ParseException e) {
						}
						sortmap.put(url, time);
					}
			rs.close();
			}catch(SQLException s){
				s.printStackTrace();
			}
		}
		dbc.close();
		
		return SortTime(sortmap);
	}
	
	//���ȶ�����url
	public ArrayList<String> SortByHot(ArrayList<String> urls){
		HashMap<String,Double> sortmap = new HashMap<String,Double>(urls.size());
		DBConnection dbc = new DBConnection();
		DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(String url :urls){
			String sql = "select * from pageindex where url ='"+url+"'";
			ResultSet rs = dbc.executeQuery(sql);
			try{
				if(rs.next())
					{
						int count = rs.getInt("count"); // ѡ��sname��������
						sortmap.put(url, count/1.0);
					}
			rs.close();
			}catch(SQLException s){
				s.printStackTrace();
			}
		}
		dbc.close();
		return SortDoc(sortmap);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date start = new Date();
		InvertedIndex invertedIndex = new InvertedIndex();
		
		int N = invertedIndex.createInvertedIndex();
		invertedIndex.WriteIndex(invertedIndexMap,N);
		
//		invertedIndex.ReaderIndex();
//		for (Iterator iter = invertedIndexMap.entrySet().iterator(); iter.hasNext();) {
//			
//			Map.Entry entry = (Map.Entry) iter.next();    //map.entry ͬʱȡ����ֵ��
//		    String word = (String) entry.getKey();
//		    HashMap<String ,DocPos> urls = ( HashMap<String ,DocPos>) entry.getValue();
//			for (Iterator it = urls.entrySet().iterator(); it.hasNext();) {
//				
//				Map.Entry ent = (Map.Entry) it.next();    //map.entry ͬʱȡ����ֵ��
//			    String url = (String) ent.getKey();
//			    DocPos dp = (DocPos) ent.getValue();
//			    System.out.println(word + " ��Ӧ�ķִʽ���ǣ� ");
//			    System.out.println("\tURL��    "+url);
//			    System.out.print("\t�����д���   "+dp.getTitleTime());
//			    System.out.println("\t�����н���ǣ� "+dp.getBodyTime());
//		    }
//		}
		
		//To test
//		invertedIndex.ReaderIndex();
//		String key = "����";
//		System.out.println("����������С"+invertedIndexMap.size());
//		HashMap<String,Double> urls = invertedIndex.DocScore(key);
//		Date end  = new Date();
//		if(urls != null)
//		{
//			System.out.println("�õ���"+urls.size()+"�����,��ʱ"+(end.getTime()-start.getTime())+"ms");
//			for (Iterator iter = urls.entrySet().iterator(); iter.hasNext();) 
//			{
//				Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
//				String url = (String) entry.getKey();
//				double score =(Double)entry.getValue();
//				System.out.println("�����ҳ:"+url+"\t��Ƶ�÷�: "+score);
//			}
//			System.out.println("#################################");
//			System.out.println("����");
//			for(String url:invertedIndex.SortDoc(urls)){
//				System.out.println("�����ҳ:"+url);
//			}
//				
//		}
//		else
//		{
//			System.out.println("���ϧ��û�ҵ���Ҫ�����Ĺؼ���");
//		}
	}

}
