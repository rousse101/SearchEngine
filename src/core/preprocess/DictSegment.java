package core.preprocess;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import configure.Configuration;

import core.preprocess.invertedIndex.WordFiled;
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
	public ArrayList<WordFiled> SegmentFile(String htmlDoc)
	{
		//��һ����������html���ļ���������ʽ����ȥ����ǩ��������Ϣ�������ı����в���
		HtmlParser parser = new HtmlParser();
		String temp=null;
		try {
			temp = (new String(htmlDoc.getBytes("GBK"),parser.htmlCode(htmlDoc)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String htmlText = parser.html2Text(temp);
		String titleSentance = parser.htmlTitle(temp).trim();
		System.out.println("title"+titleSentance);
		//�Ͼ�cutIntoSentance���Ѿ��Ӵ���cutIntoWord��Ȼ���÷���ֵ
		ArrayList<String> sentances = cutIntoSentance(htmlText);
		ArrayList<WordFiled> segResult = new ArrayList<WordFiled>();
		ArrayList<String> titles = cutIntoWord(titleSentance, true);
		for(String s : titles){
			WordFiled wf = new WordFiled(s,1);
			segResult.add(wf);
		}
		for(int i = 0; i < sentances.size(); i++)
		{
			//TODO �����ȷֶΣ��ڷִʡ��ִʱ���ɾ������
//			��������
			ArrayList<String> words = cutIntoWord(sentances.get(i), true);
			for(String s : words){
				WordFiled wf = new WordFiled(s,0);
				segResult.add(wf);
			}
			//segResult.addAll(cutIntoWord(sentances.get(i),true));
//			segResult.add(sentances.get(i));
		}
		if(segResult.size()<1&&sentances.size()>10){
			System.out.println("����"+parser.htmlCode(htmlDoc));
			System.out.println("�ִʺ�ҳ��:\n"+htmlText);
//	    	for(String word: sentances){
//	    		//		    			iso8859-1,(new String(word.getBytes("GBK"),"utf-8"))
//				try {
//					System.out.println("word:"+(new String(word.getBytes("GBK"),"UTF-8")));
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	    	}
//	    	try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	    }
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
