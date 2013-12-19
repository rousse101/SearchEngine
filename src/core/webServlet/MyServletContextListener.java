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
		System.out.println("ϵͳ�ѹر�");
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		invertedIndex = new InvertedIndex();
		invertedIndex.createInvertedIndex();
	}

}
