package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.preprocess.DictSegment;
import core.preprocess.index.originalPageGetter;
import core.util.*;

public class testParseHtml {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String testFile = "test\\test.txt";
		HtmlParser parser = new HtmlParser();
		originalPageGetter pageGetter = new originalPageGetter();
		DictSegment dictSeg = new DictSegment();
		String htmlDoc = pageGetter.getContent(testFile, 0);
		ArrayList<String> segResult = new ArrayList<String>();
		
		//�ĵ�����׶�
		String temp=null;
			try {
				temp = (new String(htmlDoc.getBytes("GBK"),parser.htmlCode(htmlDoc)));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		String title = parser.htmlTitle(htmlDoc);
		String htmlText = parser.html2Text(temp);
		
		System.out.println("title:"+title);
			//�Ͼ�cutIntoSentance���Ѿ��Ӵ���cutIntoWord��Ȼ���÷���ֵ
			ArrayList<String> sentances = dictSeg.cutIntoSentance(htmlText);
			segResult = new ArrayList<String>();
			for(int i = 0; i < sentances.size(); i++)
			{
				//TODO �����ȷֶΣ��ڷִʡ��ִʱ���ɾ������
//				��������
				ArrayList<String> words = dictSeg.cutIntoWord(sentances.get(i),true);
				for(String word:words){
					System.out.println(word);
				}
//				segResult.add(sentances.get(i));
			}	
	}	
}
