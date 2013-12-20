package core.preprocess;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import configure.Configuration;

import core.preprocess.invertedIndex.DocPos;
import core.util.HtmlParser;

//�ִ���,����ģʽ
public class DictSegment {

	private HashSet<String> dict;            //�ʵ�
	private HashSet<String> stopWordDict;            //ͣ�ôʴʵ�
	private DictReader dictReader = new DictReader();
	private static final int maxLength = 4;
	private static String dictFile = "";
	private static String stopDictFile = "";
	private Configuration conf;
	public DictSegment()
	{
		conf = new Configuration();
		dictFile = conf.getValue("DICTIONARYPATH")+"\\Dictionary\\wordlist.txt";
		stopDictFile = conf.getValue("DICTIONARYPATH")+"\\Dictionary\\stopWord.txt";
		
		dict = dictReader.scanDict(dictFile);
		stopWordDict = dictReader.scanDict(stopDictFile);
	}
	
	
	//htmlDoc��Ԥ���������ȡbody�ı�ǩ��ͬʱ�ж�ҳ������ͣ���hub��img��������ҳ��
	//����������ȡ���ģ����ձ����ŶϾ���µ�htmlDoc
	//���̣���htmlDoc�ж�ȡ����S�����û��S�ˣ��㷨����
	//������У���ȡS�����þ����дʳ������������ȡS
	//���������޳�ͣ�ôʣ�ͳ��
	public HashMap<String,DocPos> SegmentFile(String htmlDoc)
	{
		//��һ����������html���ļ���������ʽ����ȥ����ǩ��������Ϣ�������ı����в���
		HtmlParser parser = new HtmlParser();
		String temp=null;
		Date st1 = new Date();
		try {
			temp = (new String(htmlDoc.getBytes("GBK"),parser.htmlCode(htmlDoc)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Date st2 = new Date();
		String htmlText = parser.html2Text(temp);
		String titleSentance = parser.htmlTitle(temp);
		Date st3 = new Date();
		//�Ͼ�cutIntoSentance���Ѿ��Ӵ���cutIntoWord��Ȼ���÷���ֵ
		HashMap<String,DocPos> segResult = new HashMap<String,DocPos>();
		
		if(titleSentance!=null){
			ArrayList<String> titles = cutIntoWord(titleSentance, true);
			for(String s : titles){
				if(segResult.containsKey(s)){
					DocPos wf =segResult.get(s);
					int num = wf.getTitleTime()+1;
					wf.setTitleTime(num);
				}
				else{
					DocPos wf = new DocPos(1,0);
					segResult.put(s, wf);
				}
				
			}
		}
		Date st4 = new Date();
		ArrayList<String> sentances = cutIntoSentance(htmlText);
		Date st5 = new Date();
		
		for(int i = 0; i < sentances.size(); i++)
		{
			ArrayList<String> words = cutIntoWord(sentances.get(i), true);
			for(String s : words){
				if(segResult.containsKey(s)){
					DocPos wf =segResult.get(s);
					int num = wf.getBodyTime()+1;
					wf.setBodyTime(num);
				}
				else{
					DocPos wf = new DocPos(0,1);
					segResult.put(s, wf);
				}
			}
		}
		Date st6 = new Date();
		long s1 = st2.getTime()-st1.getTime();
		long s2 = st3.getTime()-st2.getTime();
		long s3 = st4.getTime()-st3.getTime();
		long s4 = st5.getTime()-st4.getTime();
		long s5 = st6.getTime()-st5.getTime();
		long s6 = st6.getTime()-st1.getTime();
		System.out.print("�����ʱ:"+s1+"ms");
		System.out.print("\t��ȡ��������ĺ�ʱ:"+s2+"ms");
		System.out.print("\t�ֱ���ͼ�������ʱ:"+s3+"ms");
		System.out.print("\t�ִ����ĺ�ʱ:"+s4+"ms");
		System.out.print("\t��������ʱ:"+s5+"ms");
		System.out.println("\t�ܺ�ʱ:"+s6+"ms");
		return segResult;
	}
	
//	public ArrayList<String> SegmentKeyWord(String keyWord)
//	{
//		return cutIntoWord(keyWord);
//	}
	
	public ArrayList<String> cutIntoSentance(String htmlDoc)
	{
	    //����StringTokenizer��Ķ���tokenizer,�������ַ���tokenizer�ķ�����
        //�Կո����","��"."��"!"��Ϊ�����
		ArrayList<String> sentance = new ArrayList<String>();
		
		String token = " |������������������������������-\t\n\r\f";
	    StringTokenizer tokenizer = new StringTokenizer(htmlDoc,token);

	    //��ȡ�ַ���str1�����Է��ŵĸ���
	    int num = tokenizer.countTokens();
	    
	    //����ѭ������ȡ�ַ���str1����һ�����Է���,�����
	    while(tokenizer.hasMoreTokens()) 
	    	sentance.add(tokenizer.nextToken().trim());
		return sentance;
	}
	
	//���һ�仰�к�����ĸ�������֣���ЩӦ�ò����зֵ��������û����
	//����ͣ�ôʣ����˵���
	public ArrayList<String> cutIntoWord(String sentance,Boolean model){
		if(model==false){
			return cutIntoWord(sentance,true);
		}
		else
		{
			ArrayList<String> result = new ArrayList<String>();
			StringReader sr = new StringReader(sentance);
			IKSegmenter ik=new IKSegmenter(sr, true); 
			Lexeme lex=null; 
			try {
				while((lex=ik.next())!=null){
					result.add(lex.getLexemeText());
				}
			} catch (IOException e) {
				return result;
			}
			return result;
		}
	}
	public ArrayList<String> cutIntoWord(String sentance)
	{	
		int currLen = 0;
		String wait2cut = sentance;
		ArrayList<String> sentanceSegResult = new ArrayList<String>();
		
		while(wait2cut.length() != 0)
		{
			String temp;
			if(wait2cut.length() >= maxLength)
				currLen = maxLength;
			else
				currLen = wait2cut.length();
				
			temp = wait2cut.substring(0, currLen);
			
			while(!dict.contains(temp) && currLen > 1)
			{
				currLen--;
				temp = temp.substring(0, currLen);
			}
			
			//�����temp�ǷֺõĴʣ��ж�temp�Ƿ���ͣ�ôʱ��У�������ǣ������list��
			if(!stopWordDict.contains(temp) && temp.length() != 1)
				sentanceSegResult.add(temp);
			
			//����ȥ��temp���ȵ��ַ���������ִ��
			wait2cut = wait2cut.substring(currLen);	
		}
		
		//System.out.println(result);		
		return sentanceSegResult;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DictSegment dictSeg = new DictSegment();
	}

}
