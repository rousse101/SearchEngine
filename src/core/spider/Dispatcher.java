package core.spider;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Dispatcher {

	private static ArrayList<URL> urls = new ArrayList<URL>();
	private static HashMap<URL,Integer> map = new HashMap<URL,Integer>();
	public Dispatcher(ArrayList<URL> urls) {    
		this.urls = urls; 
		for(URL url: urls){
			map.put(url, 0);
		}
	}    
	public boolean outofSize(){
		if(urls.size()>5000){
			return true;
		}
		return false;
	}
	public synchronized URL getURL()		
	{
		//堆栈无数据，不能出栈
		while(urls.isEmpty()){ 
			try{ 
				wait(); // 等待生产者写入数据 
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
			} 
		}
		
		this.notify(); 
		URL url = urls.get(0);
		urls.remove(url);
		
	    return url; 
	}

	public synchronized void insert(URL url)
	{
		if(!map.containsKey(url))
		{
			urls.add(url);
			map.put(url, 0);
		}
	}

	public synchronized void insert(ArrayList<URL> analyzedURL)
	{
		for(URL url : analyzedURL)
		{
			//if(!urls.contains(url) && !visitedURLs.contains(url))
			if(!map.containsKey(url))
			{
				urls.add(url);
				map.put(url, 0);
			}
		}
		System.out.println("url大小"+urls.size());
	}
    
}
