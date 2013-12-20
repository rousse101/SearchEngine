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

//分词类,单例模式
public class DictSegment {

	private HashSet<String> dict;            //词典
	private HashSet<String> stopWordDict;            //停用词词典
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
	
	
	//htmlDoc的预处理，比如截取body的标签，同时判断页面的类型，是hub，img还是主题页面
	//根据内容提取中文，按照标点符号断句成新的htmlDoc
	//流程，从htmlDoc中读取句子S，如果没有S了，算法结束
	//如果还有，读取S，调用句子切词程序，切完继续读取S
	//后续处理：剔除停用词，统计
	public HashMap<String,DocPos> SegmentFile(String htmlDoc)
	{
		//第一步操作，把html的文件用正则表达式处理，去掉标签等无用信息，保留文本进行操作
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
		//断句cutIntoSentance，把句子传到cutIntoWord，然后获得返回值
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
		System.out.print("编码耗时:"+s1+"ms");
		System.out.print("\t提取标题和正文耗时:"+s2+"ms");
		System.out.print("\t分标题和加索引耗时:"+s3+"ms");
		System.out.print("\t分词正文耗时:"+s4+"ms");
		System.out.print("\t加索引耗时:"+s5+"ms");
		System.out.println("\t总耗时:"+s6+"ms");
		return segResult;
	}
	
//	public ArrayList<String> SegmentKeyWord(String keyWord)
//	{
//		return cutIntoWord(keyWord);
//	}
	
	public ArrayList<String> cutIntoSentance(String htmlDoc)
	{
	    //创建StringTokenizer类的对象tokenizer,并构造字符串tokenizer的分析器
        //以空格符、","、"."及"!"作为定界符
		ArrayList<String> sentance = new ArrayList<String>();
		
		String token = " |。，、；：？！“”‘’《》（）-\t\n\r\f";
	    StringTokenizer tokenizer = new StringTokenizer(htmlDoc,token);

	    //获取字符串str1中语言符号的个数
	    int num = tokenizer.countTokens();
	    
	    //利用循环来获取字符串str1中下一个语言符号,并输出
	    while(tokenizer.hasMoreTokens()) 
	    	sentance.add(tokenizer.nextToken().trim());
		return sentance;
	}
	
	//如果一句话中含有字母或者数字，这些应该不用切分掉，这个还没处理
	//过滤停用词，过滤单字
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
			
			//到这里，temp是分好的词，判断temp是否在停用词表中，如果不是，则放入list中
			if(!stopWordDict.contains(temp) && temp.length() != 1)
				sentanceSegResult.add(temp);
			
			//句子去除temp长度的字符串，继续执行
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
