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
InvertedIndex类建立网页倒排索引，对应关系为词组映射url，通过正向索引来建立
建立过程如下，从正向索引取得索引值，遍历其中url，对于其中每个词组，推入map中作为key
而url作为其value插入，最后得到的map就是倒排索引
*************************************************/  

/**
* <p>Title: InvertedIndex</p> 
* <p>Description: 调用正向索引，遍历得到倒排索引</p> 
* <p>Copyright: Copyright (c) 2010</p> 
* <p>Company: </p> 
*  @author  <a href="dreamhunter.dy@gmail.com">dongyu</a> 
*  @version  1.0 
*  @created 2010-03-17 
*/

public class InvertedIndex {

	private HashMap<String, HashMap<String,DocPos>> fordwardIndexMap;
//	private HashMap<String, ArrayList<String>> invertedIndexMap;
	//添加了索引中的出现次数。利用hashmap存储文档和本文档中的次数。
	private static HashMap<String, HashMap<String,DocPos>> invertedIndexMap;
	private static int N=0;
	
	public int GetTotalDocNum(){
		return N;
	}
	public ArrayList<String> TermList(String keytag){
		ArrayList<String> term = new ArrayList<String>(10);
		for (Iterator iter = invertedIndexMap.entrySet().iterator(); iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next(); // map.entry 同时取出键值对
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
				Map.Entry entry = (Map.Entry) iter.next(); // map.entry 同时取出键值对
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
		//遍历原来的正向索引，进行倒排
		for (Iterator iter = fordwardIndexMap.entrySet().iterator(); iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next(); // map.entry 同时取出键值对
			String url = (String) entry.getKey();
			HashMap<String,DocPos> words = (HashMap<String,DocPos>) entry.getValue();
//			获取文档的词总算
			
			for (Iterator it = words.entrySet().iterator(); it.hasNext();) 
			{
				Map.Entry en = (Map.Entry) it.next(); // map.entry 同时取出键值对
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
	//计算文档的得分idf为出现词的文档数。
	//idf*词出现的总数
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
				Map.Entry entry = (Map.Entry) iter.next(); // map.entry 同时取出键值对
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
			
			System.out.println("真可惜，没找到关键词"+keyWord);
			return null;
		}
	}
	//实现接口排序,从到小的排序方式
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
	//排序value是double的hashmap
	public ArrayList<String> SortDoc(HashMap<String,Double> urls){
		ArrayList<String> result = new ArrayList<String>(urls.keySet());
		ByValueComparator bvc = new ByValueComparator(urls);
		Collections.sort(result,bvc);
		return result;
	}
	//这个内部类是实现排序value值是date的hasmap
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
	//sortBytime内部使用的调用方法。会使用bYtimecompare排序类。
	private ArrayList <String> SortTime(HashMap<String,Date> urls){
		ArrayList<String> result = new ArrayList<String>(urls.keySet());
		ByTimeComparator bvc = new ByTimeComparator(urls);
		Collections.sort(result,bvc);
		System.out.println("over sort time");
		return result;
	}
	//按时间排序url
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
						String date = rs.getString("pagetime"); // 选择sname这列数据
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
	
	//按热度排序url
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
						int count = rs.getInt("count"); // 选择sname这列数据
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
//			Map.Entry entry = (Map.Entry) iter.next();    //map.entry 同时取出键值对
//		    String word = (String) entry.getKey();
//		    HashMap<String ,DocPos> urls = ( HashMap<String ,DocPos>) entry.getValue();
//			for (Iterator it = urls.entrySet().iterator(); it.hasNext();) {
//				
//				Map.Entry ent = (Map.Entry) it.next();    //map.entry 同时取出键值对
//			    String url = (String) ent.getKey();
//			    DocPos dp = (DocPos) ent.getValue();
//			    System.out.println(word + " 对应的分词结果是： ");
//			    System.out.println("\tURL是    "+url);
//			    System.out.print("\t标题中次数   "+dp.getTitleTime());
//			    System.out.println("\t正文中结果是： "+dp.getBodyTime());
//		    }
//		}
		
		//To test
//		invertedIndex.ReaderIndex();
//		String key = "教育";
//		System.out.println("倒排索引大小"+invertedIndexMap.size());
//		HashMap<String,Double> urls = invertedIndex.DocScore(key);
//		Date end  = new Date();
//		if(urls != null)
//		{
//			System.out.println("得到了"+urls.size()+"个结果,耗时"+(end.getTime()-start.getTime())+"ms");
//			for (Iterator iter = urls.entrySet().iterator(); iter.hasNext();) 
//			{
//				Map.Entry entry = (Map.Entry) iter.next(); // map.entry 同时取出键值对
//				String url = (String) entry.getKey();
//				double score =(Double)entry.getValue();
//				System.out.println("结果网页:"+url+"\t词频得分: "+score);
//			}
//			System.out.println("#################################");
//			System.out.println("排序");
//			for(String url:invertedIndex.SortDoc(urls)){
//				System.out.println("结果网页:"+url);
//			}
//				
//		}
//		else
//		{
//			System.out.println("真可惜，没找到您要搜索的关键词");
//		}
	}

}
