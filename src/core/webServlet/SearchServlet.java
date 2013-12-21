package core.webServlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.query.Response;
import core.util.Result;

public class SearchServlet extends HttpServlet {

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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
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
		Response rp = new Response();
		String keyword = new String(request.getParameter("keyword").getBytes("ISO-8859-1"),"GB2312"); 
		String model = new String(request.getParameter("model").getBytes("ISO-8859-1"),"GB2312"); 
		String curnum = new String(request.getParameter("CurrentNum").getBytes("ISO-8859-1"),"GB2312"); 
		int md =Integer.parseInt(model);
		int cn = Integer.parseInt(curnum);
		ArrayList<Result> results = rp.getResponse(keyword,cn,md);
		int tn = rp.getResultSize();
		request.getSession().setAttribute("results", results);
		request.getSession().setAttribute("keyword", keyword);
		request.getSession().setAttribute("curnum", cn);
		request.getSession().setAttribute("pagenum", tn/10+1);
		response.sendRedirect("search.jsp");
		
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
