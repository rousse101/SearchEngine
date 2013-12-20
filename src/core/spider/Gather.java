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
		file = new File("Raws\\RAW__" + ID + ".txt");           //�趨������ļ���

		try {
			file.createNewFile();
			bfWriter = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//ע�⣬��û��ܹ�̽�����ӽ�����ʱ�䣬�Ӷ��ѳ���ʱ��̫��������ֱ��kill��
	//��ֹ��һ��url��ͣ�����õ�����
	public void run() {
		
		int counter = 0;
		while(counter <= ThreadPage)		//ÿ���߳���ȡ100����ҳ
		{
			URL url = disp.getURL();
			System.out.println(url);
			System.out.println("�̺߳� " + ID + "\tURLΪ: " + url.toString());
			String htmlDoc = client.getDocumentAt(url);
			System.out.println("ThreadPage "+ThreadPage);
			if(htmlDoc.length() != 0)
			{
				ArrayList<URL> newURL = analyzer.doAnalyzer(bfWriter, url, htmlDoc);
				counter ++;
				System.out.println("Ok");
				if(newURL.size() != 0&&!disp.outofSize())
					disp.insert(newURL);
				System.out.println("URL�������");
			}
			
		}
		
	}

}
