package core.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class Spider {

	private ArrayList<URL> urls;
	//启动线程数
	private final int gatherNum ;
	//每个线程抓取的页面
	private final int ThreadPage;
	
	
	public Spider(ArrayList<URL> urls,int ThreadNum,int ThreadPage)
	{
		this.urls = urls;
		this.gatherNum = ThreadNum;
		this.ThreadPage = ThreadPage;
	}
	
	/**
	 * 启动线程gather，然后开始收集网页资料
	 */
	public void start() {
		Dispatcher disp = new Dispatcher(urls);
		for(int i = 0; i < gatherNum; i++)
		{
			Thread gather = new Thread(new Gather(String.valueOf(i), disp, ThreadPage));
			gather.start();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ArrayList<URL> urls = new ArrayList<URL>();
		try {
//			种子文件的格式是http://XXX/,后续检验需要使用
			//urls.add(new URL("http://ast.nlsde.buaa.edu.cn/"));		
			//urls.add(new URL("http://www.baidu.com"));
			//urls.add(new URL("http://www.google.com"));
			//urls.add(new URL("http://www.sohu.com"));
			urls.add(new URL("http://news.163.com/"));
			urls.add(new URL("http://sports.163.com/"));
			urls.add(new URL("http://war.163.com/"));
			urls.add(new URL("http://money.163.com/"));
			urls.add(new URL("http://auto.163.com/"));
			urls.add(new URL("http://tech.163.com/"));
			urls.add(new URL("http://mobile.163.com/"));
			urls.add(new URL("http://digi.163.com/"));
			urls.add(new URL("http://lady.163.com/"));
//			urls.add(new URL("http://edu.sina.com.cn/"));			
//			urls.add(new URL("http://edu.163.com/"));
//			urls.add(new URL("http://ast.nlsde.buaa.edu.cn/"));
			//urls.add(new URL("http://www.chsi.com.cn/"));
			//urls.add(new URL("http://www.eol.cn/"));
			//urls.add(new URL("http://www.edutv.net.cn/"));
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		Spider spider = new Spider(urls,5,10);
		spider.start();

	}

}
