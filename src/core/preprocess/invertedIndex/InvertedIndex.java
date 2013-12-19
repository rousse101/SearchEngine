package core.preprocess.invertedIndex;

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

	private HashMap<String, ArrayList<String>> fordwardIndexMap;
//	private HashMap<String, ArrayList<String>> invertedIndexMap;
	//����������еĳ��ִ���������hashmap�洢�ĵ��ͱ��ĵ��еĴ�����
	private static HashMap<String, HashMap<String,Integer>> invertedIndexMap;
	private int N=0;
	
	public InvertedIndex()
	{
		if(invertedIndexMap==null){
			ForwardIndex forwardIndex = new ForwardIndex();
			fordwardIndexMap = forwardIndex.createForwardIndex();
		}
	}
	public int GetTotalDocNum(){
		return N;
	}
	public HashMap<String, HashMap<String,Integer>> createInvertedIndex() {
		
		invertedIndexMap = new HashMap<String, HashMap<String,Integer>>();
		N = fordwardIndexMap.size();
		//����ԭ�����������������е���
		for (Iterator iter = fordwardIndexMap.entrySet().iterator(); iter.hasNext();) 
		{
			Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
			String url = (String) entry.getKey();
			ArrayList<String> words = (ArrayList<String>) entry.getValue();
//			��ȡ�ĵ��Ĵ�����
			
			String word;
			for(int i = 0; i < words.size(); i++)
			{
				word = words.get(i);
				//���������л�û������ʣ���������ʣ��ٰ�url������
				if(!invertedIndexMap.containsKey(word))
				{
					HashMap<String,Integer> urls = new HashMap<String,Integer>();
					urls.put(url, 1);
					invertedIndexMap.put(word, urls);
				}
				//�������Ѿ���������ĵ����Ͱ������Ƶ+1
//				���������û������ĵ�����Ҫ�ҵ����key�Ӷ���url������
				else
				{
					HashMap<String, Integer> urls = invertedIndexMap.get(word);
					if(!urls.containsKey(url))
						urls.put(url, 1);
					else{
						int num = urls.get(url);
						num++;
						urls.put(url, num);
					}
				}
			}
		}

		System.out.println("***************************************************************");
		System.out.println("create invertedIndex finished!!");
		System.out.println("the size of invertedIndex is : " + invertedIndexMap.size());
		return invertedIndexMap;
	}

	public HashMap<String,HashMap<String,Integer>> getInvertedIndex()
	{
		return invertedIndexMap;
	}
	//�����ĵ��ĵ÷�idfΪ���ִʵ��ĵ�����
	//idf*�ʳ��ֵ�����
	public HashMap<String,Double> DocScore(String keyWord){
		HashMap<String,Double> result = new HashMap<String,Double>();
		HashMap<String,Integer> urls = invertedIndexMap.get(keyWord);
		if(urls != null)
		{
			double N = GetTotalDocNum();
			double df = N/urls.size();
			double idf = Math.log10(df);
			for (Iterator iter = urls.entrySet().iterator(); iter.hasNext();) 
			{
				Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
				String url = (String) entry.getKey();
				double score = idf * ((Integer)entry.getValue());
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
		invertedIndex.createInvertedIndex();
		
		String key = "ϰ��ƽ";
		HashMap<String,Double> urls = invertedIndex.DocScore(key);
		Date end  = new Date();
		if(urls != null)
		{
			System.out.println("�õ���"+urls.size()+"�����,��ʱ"+(end.getTime()-start.getTime())+"ms");
			for (Iterator iter = urls.entrySet().iterator(); iter.hasNext();) 
			{
				Map.Entry entry = (Map.Entry) iter.next(); // map.entry ͬʱȡ����ֵ��
				String url = (String) entry.getKey();
				double score =(Double)entry.getValue();
				System.out.println("�����ҳ:"+url+"\t��Ƶ�÷�: "+score);
			}
//			for(String url : urls)
				
		}
		else
		{
			System.out.println("���ϧ��û�ҵ���Ҫ�����Ĺؼ���");
		}
	}

}
