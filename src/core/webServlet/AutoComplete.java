package core.webServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.preprocess.invertedIndex.InvertedIndex;

public class AutoComplete extends HttpServlet {

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	
	private ArrayList<String> termList=new ArrayList<String>(10);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		
		String keyTag=new String(request.getParameter("tag").getBytes("ISO-8859-1"), "GB2312"); 
		
		
		//Araaylist<String> auto(keytag)
		InvertedIndex invertedIndex = new InvertedIndex();
		if(!keyTag.equals("")){
			
			System.out.println("keyTag is:"+keyTag.trim());
			termList= invertedIndex.TermList(keyTag.trim());
			
			StringBuffer results=new StringBuffer(1000);
			int length=termList.size();
			for(int i=0; i<length; i++){
					results.append(termList.get(i));
					results.append(',');
			}
			if(results.length()>0){
				results.deleteCharAt(results.length()-1);
				System.out.println("results is:"+results.toString());
				out.write(results.toString());
			}
		}
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		
	}

}
