/**
 * 
 */
package core.webServlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import core.preprocess.invertedIndex.InvertedIndex;

/**
 * @author Administrator
 *
 */
public class MyServletContextListener implements ServletContextListener {
	private InvertedIndex invertedIndex;
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("系统已关闭");
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		invertedIndex = new InvertedIndex();
		if(invertedIndex.ReaderIndex())
			System.out.println("倒排索构建成功。");
		System.out.println(invertedIndex.GetTotalDocNum());
	}

}
