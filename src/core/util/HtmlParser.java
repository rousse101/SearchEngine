package core.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

	public HtmlParser(){}
	
	//&quot;&nbsp;
	//TODO 还有问题，有时候会返回null
public int GetCount(String htmlDoc){
	
	Pattern pattern=Pattern.
	compile("totalCount\\s+=\\s+([0-9]+),");
	Matcher matcher=pattern.matcher(htmlDoc);
	if(matcher.find()){
		String temp=matcher.group(1);
		return Integer.parseInt(temp);
	}
	else
	{
		return 0;	
	}
}
public Date parseNewsTime(String url, String htmlDoc) {
		
		int host = 0;
		String patternString = null;
		if (url.contains(".163.")) {
			host = 1;
			patternString = "(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})　来源: <a"; //"(\\d{4}-[01]\\d-[0-3]\\d\\s{1,2}[012]\\d:[0-6]\\d:[0-6]\\d)　来源: <a";
		} else if (url.contains(".ifeng.")) {
			host = 2;
			patternString = "<span[\\s]+itemprop=\"datePublished\" class=\"ss01\">(\\d{4}年\\d{2}月\\d{2}日\\s?\\d{2}:\\d{2})</span>";
		} else if (url.contains(".sina.")) {
			host = 3;
			patternString = "<span id=\"pub_date\">(\\d{4}年\\d{2}月\\d{2}日\\s?\\d{2}:\\d{2})</span>";
		} else {
			return null;
		}
		Pattern pattern = Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);   
		Matcher matcher = pattern.matcher(htmlDoc);
		
		if (matcher.find()) {
			String time =  matcher.group(1);
			SimpleDateFormat df = new SimpleDateFormat();
			df.applyPattern("yyyy-MM-dd HH:mm:ss");
			/*switch (host) {
			case 1:
				df.applyPattern("yyyy-MM-dd HH:mm:ss");
				break;
			case 2:
				df.applyPattern("yyyy年MM月dd日 HH:mm");
				break;
			case 3:
				df.applyPattern("yyyy年MM月dd日HH:mm");
				
			}*/
			try {
				return df.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("cann't find the date time");
		}
		
		return null;
	}

	public String htmlCode(String htmlDoc)
	{
		final String patternString = "<meta[^>]*charset=([^>]*\\s*>)";   		
		Pattern pattern = Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);   
		Matcher matcher = pattern.matcher(htmlDoc);
		String tempURL;
		//初次匹配到的url是形如：<a href="http://bbs.life.sina.com.cn/" target="_blank">
		//为此，需要进行下一步的处理，把真正的url抽取出来，可以对于前两个"之间的部分进行记录得到url
		if(matcher.find())
		{
//			System.out.println("匹配上了");
			tempURL = matcher.group(1);
			if(tempURL==null)return "utf-8";
			tempURL = tempURL.replace("\"","");
			tempURL = tempURL.replace("/","");
			tempURL = tempURL.replace(">","");
			return tempURL.trim();
		}
		return "utf-8";
	}
	public String htmlTitle(String inputString){
		String regEx_title = "<title[^>]*?>[\\s\\S]*?</title>"; 
		Pattern p_title = Pattern.compile(regEx_title,Pattern.CASE_INSENSITIVE);
		Matcher m_title = p_title.matcher(inputString); 
		String title = null;
		while(m_title.find())
		{
			title = m_title.group();   
			//取其中第1个'>'和第二个'<'之间的文字
			title = title.substring(title.indexOf(">")+1, title.lastIndexOf("<"));
			break;
		}
		return title;
	}
	
	public String html2Text(String inputString) 
	{    	
		String htmlStr = inputString; //含html标签的字符串    
		String textStr ="";    
		Pattern p_script,p_style,p_html,p_href,p_filter,p_com;    
		Matcher m_script,m_style,m_html,m_href,m_filter,m_com;      
	          
	    try { 
	    	//定义script正则式{或<script[^>]*?>[\s\S]*?<\/script> } 
	    	String regEx_script = "<(?:no)?script[^>]*?>[\\s\\S]*?</(?:no)?script>";    
	    	//定义style正则式{或<style[^>]*?>[\s\S]*?<\/style> }    
	    	String regEx_style = "<style[^>]*?>[\\s\\S]*?</style>"; 
	    	String regEx_href = "<a[^>]*?>[\\s\\S]*?</a>"; 
	    	String regEx_com = "<!--[^>]*?[\\s\\S]*?-->"; 
	    	
	    	//定义HTML标签的正则表达式 
	    	String regEx_html = "<[^>]+>";
	        String[] filter = {"&quot;", "&nbsp;","&lt;","&gt;","\\s{2,}","\n"};
	       
	        p_com = Pattern.compile(regEx_com,Pattern.CASE_INSENSITIVE);    
	        m_com = p_com.matcher(htmlStr);    
	        htmlStr = m_com.replaceAll(""); //过滤script标签    
	        
	        p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);    
	        m_script = p_script.matcher(htmlStr);    
	        htmlStr = m_script.replaceAll(""); //过滤script标签    
	   
	        p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);    
	        m_style = p_style.matcher(htmlStr);    
	        htmlStr = m_style.replaceAll(""); //过滤style标签    
	        
	        p_href = Pattern.compile(regEx_href,Pattern.CASE_INSENSITIVE);    
	        m_href = p_href.matcher(htmlStr);    
	        htmlStr = m_href.replaceAll(""); //过滤style标签    
	        
	        p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);    
	        m_html = p_html.matcher(htmlStr);    
	        htmlStr = m_html.replaceAll(""); //过滤html标签    
	           
	        //过滤style标签    &quot; &nbsp;
	        for(int i = 0; i < filter.length; i++)
	        {
	        	p_filter = Pattern.compile(filter[i],Pattern.CASE_INSENSITIVE);    
	        	m_filter = p_filter.matcher(htmlStr);    
		        htmlStr = m_filter.replaceAll(" "); 
	        }
	        
	        textStr = htmlStr;    
	           
	    }catch(Exception e) {    
	       System.err.println("Html2Text: " + e.getMessage());    
	    }    
	          
	    return textStr;//返回文本字符串    
	}
	
	//URL还需要做的工作，去除一些无用链接，修复一些相对路径的链接
	public ArrayList<URL> urlDetector(String htmlDoc,URL url)
	{
		String str2 = url.toString();
		System.out.println("传入的url"+str2);
		String pos = str2.substring(7);
        String start= str2.substring(0,7+pos.indexOf("/"));
        System.out.println("文本前缀是"+start);
		final String patternString = "<[a|A]\\s+href=([^>]*\\s*>)";   		
		Pattern pattern = Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);   
		
		ArrayList<URL> allURLs = new ArrayList<URL>();

		Matcher matcher = pattern.matcher(htmlDoc);
		String tempURL;
		//初次匹配到的url是形如：<a href="http://bbs.life.sina.com.cn/" target="_blank">
		//为此，需要进行下一步的处理，把真正的url抽取出来，可以对于前两个"之间的部分进行记录得到url
		while(matcher.find())
		{
			try {
				
				tempURL = matcher.group();			
				tempURL = tempURL.substring(tempURL.indexOf("\"")+1);			
				if(!tempURL.contains("\""))
					continue;
				
				tempURL = tempURL.substring(0, tempURL.indexOf("\""));					
				//System.out.println(tempURL);
				//即使在之前的处理下，还是有可能发生意外的，比如，程序用的是相对的url
				//这样，这个字符串就不可以用于url的初始化，我们先把这部分省略不考虑
				//之后可以写一个补充host的方法将这些url补齐
				if(tempURL.startsWith(start)){
					System.out.println("得到的结果URL;"+tempURL);
					allURLs.add(new URL(tempURL));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return allURLs;	
	}
	
}
