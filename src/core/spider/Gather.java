package core.spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Gather implements Runnable {

	private Dispatcher disp;
	private String ID;
	private final int ThreadPage;
	private URLClient client = new URLClient();
	private WebAnalyzer analyzer = new WebAnalyzer();
	private File file;
	private BufferedWriter bfWriter;
	
	public Gather(String ID, Dispatcher disp,int num)
	{
		this.ID = ID;
		this.disp = disp;
		this.ThreadPage = num;
		file = new File("Raws\\RAW__" + ID + ".txt");           //设定输出的文件名

		try {
			file.createNewFile();
			bfWriter = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//注意，最好还能够探测链接建立的时间，从而把持续时间太长的链接直接kill掉
	//防止在一个url上停留过久的问题
	public void run() {
		
		int counter = 0;
		while(counter <= ThreadPage)		//每个线程提取100个网页
		{
			URL url = disp.getURL();
			System.out.println(url);
			System.out.println("线程号 " + ID + "\tURL为: " + url.toString());
			String htmlDoc = client.getDocumentAt(url);
			System.out.println("ThreadPage "+ThreadPage);
			if(htmlDoc.length() != 0)
			{
				ArrayList<URL> newURL = analyzer.doAnalyzer(bfWriter, url, htmlDoc);
				counter ++;
				System.out.println("Ok");
				if(newURL.size() != 0&&!disp.outofSize())
					disp.insert(newURL);
				System.out.println("URL分析完毕");
			}
			
		}
		
	}

}
