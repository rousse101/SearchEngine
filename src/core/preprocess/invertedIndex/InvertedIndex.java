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

	private HashMap<String, ArrayList<WordFiled>> fordwardIndexMap;
//	private HashMap<String, ArrayList<String>> invertedIndexMap;
	//����������еĳ��ִ���������hashmap�洢�ĵ��ͱ��ĵ��еĴ�����
	private static HashMap<String, HashMap<String,DocPos>> invertedIndexMap;
	private static int N=0;
	
	public int GetTotalDocNum(){
		return N;
	}
	public Boolean WriteIndex(HashMap<String, HashMap<String,DocPos>> invertedIndexMap,int N){
		try {
			Configuration conf = new Configuration();
			FileOutputStream outStream = new FileOutputStream(conf.getValue("INDEXPATH")+"\\Index\\Index.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream); 
			objectOutputStream.writeObject(invertedIndexMap);
			objectOutputStream.close();
			FileOutputStream outN = new FileOutputStream(conf.getValue("INDEXPATH")+"\\Index\\conf.txt");
			PrintStream p=new PrintStream(outN);
			p.print(""+N);
			p.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public Boolean ReaderIndex(){
		
		invertedIndexMap =new  HashMap<String, HashMap<String,DocPos>>();
		Configuration conf = new Configuration();
		try {
			FileInputStream freader = new FileInputStream(conf.getValue("INDEXPATH")+"\\Index\\Index.txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			try {
				invertedIndexMap = (HashMap<String, HashMap<String,DocPos>>)objectInputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			
			FileReader fr = new FileReader(conf.getValue("INDEXPATH")+"\\Index\\conf.txt");
			BufferedReader br = new BufferedReader(fr);
			String s;
			s = br.readLine();
			System.out.println(s);
			this.N= Integer.parseInt(s);
			
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
			ArrayList<WordFiled> words = (ArrayList<WordFiled>) entry.getValue();
//			��ȡ�ĵ��Ĵ�����
			
			String word;
			for(int i = 0; i < words.size(); i++)
			{
				WordFiled wf = words.get(i);
				word = wf.getWord();
				//���������л�û������ʣ���������ʣ��ٰ�url������
				if(!invertedIndexMap.containsKey(word))
				{
					HashMap<String,DocPos> urls = new HashMap<String,DocPos>();
					DocPos dp;
					if(wf.getType()==0){
						dp = new DocPos(0,1);
					}
					else{
						dp = new DocPos(1,0);
					}
					urls.put(url, dp);
					invertedIndexMap.put(word, urls);
				}
				//�������Ѿ���������ĵ����Ͱ������Ƶ+1
//				���������û������ĵ�����Ҫ�ҵ����key�Ӷ���url������
				else
				{
					HashMap<String, DocPos> urls = invertedIndexMap.get(word);
					if(!urls.containsKey(url)){
						DocPos dp;
						if(wf.getType()==0){
							dp = new DocPos(0,1);
						}
						else{
							dp = new DocPos(1,0);
						}
						urls.put(url, dp);
					}
						
					else{
						DocPos dp = urls.get(url);
						if(wf.getType()==0){
							int num = dp.getBodyTime()+1;
							dp.setBodyTime(num);
						}
						else{
							int num = dp.getTitleTime()+1;
							dp.setTitleTime(num);
						}
						urls.put(url, dp);
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
				System.out.println(tf);
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
	public ArrayList<String> SortDoc(HashMap<String,Double> urls){
		ArrayList<String> result = new ArrayList<String>(urls.keySet());
		ByValueComparator bvc = new ByValueComparator(urls);
		Collections.sort(result,bvc);
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date start = new Date();
		InvertedIndex invertedIndex = new InvertedIndex();
		int N = invertedIndex.createInvertedIndex();
		invertedIndex.WriteIndex(invertedIndexMap,N);
		
		//To test
//		invertedIndex.ReaderIndex();
//		String key = "����";
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
