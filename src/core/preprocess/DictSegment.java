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
	public ArrayList<WordFiled> SegmentFile(String htmlDoc)
	{
		//第一步操作，把html的文件用正则表达式处理，去掉标签等无用信息，保留文本进行操作
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
		//断句cutIntoSentance，把句子传到cutIntoWord，然后获得返回值
		ArrayList<String> sentances = cutIntoSentance(htmlText);
		ArrayList<WordFiled> segResult = new ArrayList<WordFiled>();
		ArrayList<String> titles = cutIntoWord(titleSentance, true);
		for(String s : titles){
			WordFiled wf = new WordFiled(s,1);
			segResult.add(wf);
		}
		for(int i = 0; i < sentances.size(); i++)
		{
			//TODO 这里先分段，在分词。分词被我删除掉。
//			考虑两点
			ArrayList<String> words = cutIntoWord(sentances.get(i), true);
			for(String s : words){
				WordFiled wf = new WordFiled(s,0);
				segResult.add(wf);
			}
			//segResult.addAll(cutIntoWord(sentances.get(i),true));
//			segResult.add(sentances.get(i));
		}
		if(segResult.size()<1&&sentances.size()>10){
			System.out.println("编码"+parser.htmlCode(htmlDoc));
			System.out.println("分词后页面:\n"+htmlText);
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
