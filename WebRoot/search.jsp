<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<jsp:directive.page import="core.query.Response" />
<jsp:directive.page import="core.util.Result" />

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>搜索一下你就知道</title>
    
    <style>
	#search{
	width:78px;
	height:28px;
	font:14px "宋体"
	}
	
	#textArea{
	width:300px;
	height:30px;
	font:14px "宋体"
	}
	</style>

  </head>
  
  <body>
    
    <%	
		//String keyword = new String(request.getParameter("keyword").getBytes("ISO-8859-1"),"GB2312");
		String keyword=(String)session.getAttribute("keyword");
		int totalnum=(Integer)session.getAttribute("pagenum");
		int curnum=(Integer)session.getAttribute("curnum");
		ArrayList<Result> results=(ArrayList<Result>)session.getAttribute("results"); 	
	%>
    <form action="Search?model=0&CurrentNum=1" name="search" method="Post">
	<table border="0" height="30px" width="450px" align="center">		
		<tr>
			
			<a href ="Search?keyword=<%=keyword%>&model=1&CurrentNum=1">按时间排序</a>
			<td width ="66%"><input name="keyword" type="text" 
				maxlength="100" id="textArea" value=<%=keyword%>></td>
			<td height="29" align="center"><input type="submit" value="搜索一下" id = "search"></td>
			<td><img src="logo.jpg" /></td>
		</tr>
	</table>
	</form>
	<%  
		for(Result result : results)
		{
	%>	
			<h2><a href=<%=result.getUrl()%> target="_blank"><%=result.getTitle()%></a></h2>
			<p><%=result.getContent()%><p>
			<p><%=result.getUrl()%> &nbsp;&nbsp;&nbsp; <%=result.getDate()%><p>
	<%  		
		}
	%>
	<h2 align="center">第
	<%  
		for(int i =1;i<=totalnum;i++)
		{
		if(i==curnum){
			out.print(i);
		}
		else{
		
	%>	
			<a href ="Search?keyword=<%=keyword%>&model=0&CurrentNum=<%=i%>"><%=i%></a>
	<%  
			}		
		}
	%>
	页</h2>
  </body>
</html>
