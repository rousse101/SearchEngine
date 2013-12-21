package core.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.preprocess.index.RawsAnalyzer;
import core.preprocess.index.originalPageGetter;

public class ResultGenerator {
	
	private originalPageGetter pageGetter;
	private HtmlParser parser;
	
	
	public ResultGenerator()
	{
		pageGetter = new originalPageGetter();
		parser = new HtmlParser();
		
		//用于后续匹配title的正则表达式
		
	}
	public String view(String content,ArrayList<String> keyWords){

		String shortContent = "";
		Pattern p_meta;    
		Matcher m_meta;
		for(String s: keyWords){
			String regEx = "[\\s。]{1}([\\S^。])*?("+s+")([\\S]*?[\\s。])"; 
			p_meta = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
			m_meta = p_meta.matcher(content);    
			while(m_meta.find())
				{
					shortContent = m_meta.group(1)+m_meta.group(2)+m_meta.group(3);   
					shortContent = shortContent.toLowerCase();
				}
		}
		return shortContent;
	}
	public Result generateResult(String url,ArrayList<String> keyWords) {
		
		Page page;
		String content ="";		
		String date = "";
		String title = "";
		//String contentText = "";
		String shortContent = "";
		page = pageGetter.getRawsInfo(url);
		content = pageGetter.getContent(page.getRawName(), page.getOffset());
		//content = page.getConnent();
		//由于在之前的getContent中，调用readRawHead，之后再readRawHead
		//所以date的产生式对应着的，因为date在readRawHead生产
		date = pageGetter.getDateFromHead();
		
		title = pageGetter.getTitleFromHead();
		
		String view = view(content,keyWords);
		System.out.println("view"+view);
		//开始产生内容提示
		Pattern p_meta;    
		Matcher m_meta;
		for(String s : keyWords){
			String regEx = "[\\s。]{1}([\\S^。]*?"+s+"[\\S]*?[\\s。])"; 
			p_meta = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
			m_meta = p_meta.matcher(content);    
			while(m_meta.find())
			{
				shortContent = m_meta.group(1);   
				shortContent = shortContent.toLowerCase();
			}
		}
		
		return new Result(title, shortContent, url, date , view);
	}
	
	public static void main(String[] args) {
		String target1 = "<meta name=\"Description\" 中国网为全球用户24小。\" /> ";
		String target2 = " 关注教育门户的最新动态、把握垂直搜索的发展趋势。          我的世界的。 | 一  二 \" name=description>";
		target1  += target2;
		String s1 = "中国";
		String s2 ="教育"; 
		ArrayList<String>words = new ArrayList<String>();
		words.add(s1);
		words.add(s2);
		
		//开始产生内容提示\
		String content ="";
		
		Pattern p_meta;    
		Matcher m_meta;
		for(String s : words){
			String regEx = "[\\s]{1}[^。\\S]*?[\\S]*?[。]"; 
			p_meta = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
			m_meta = p_meta.matcher(target1);    
			while(m_meta.find())
			{
				content += m_meta.group();   
			}
		}
		System.out.println("content:"+content);
	}
	
	
}
