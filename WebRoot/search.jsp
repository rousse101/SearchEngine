<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<jsp:directive.page import="core.query.Response" />
<jsp:directive.page import="core.util.Result" />

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%	
		//String keyword = new String(request.getParameter("keyword").getBytes("ISO-8859-1"),"GB2312");
		String keyword=(String)session.getAttribute("keyword");
		System.out.println("keyword is "+keyword);
		int totalnum=(Integer)session.getAttribute("pagenum");
		int curnum=(Integer)session.getAttribute("curnum");
		ArrayList<Result> results=(ArrayList<Result>)session.getAttribute("results"); 	
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
  
  <body onload="okload('<%=keyword %>');">
	
	<script type="text/javascript">
	
	function okload(words) {
	 	//alert(words);
	 	var keyss = words;
	 	//document.getElementById("keywordjs").value;   //可以设置一个隐藏域存储关键词
		var keys = keyss.split("+");
		var bookmark;
		if(document.createRange){
			var range = document.createRange();
		}
		else{
			var range = document.body.createTextRange();
			bookmark = range.getBookmark();
		}
		var key;
		for(var i = 0;key = keys[i];i++){
			if(range.findText){
				range.collapse(true);
				range.moveToBookmark(bookmark);
				while(range.findText(key)){
					range.pasteHTML(range.text.fontcolor("#ff0000"));
				}
			}else{
				var s,n;
				s = window.getSelection();
				s.collapse(document.body,0);
				while(window.find(key)){
					var n = document.createElement("SPAN");
					n.style.color="#ff0000";
					s.getRangeAt(0).surroundContents(n);
				}
			}
		}
	 }
	
	
	
	var xmlHttpRequest;
	
	function createXmlHttpRequest()
	{
		if(window.ActiveXObject)
		{
			try
			{
				xmlHttpRequest = new ActiveXObject("Microsoft.XMLHTTP");
			}catch(e)
			{
				xmlHttpRequest = new ActiveXObject("Msxml2.XMLHTTP");
			}
			return xmlHttpRequest;
		}
		else if(window.XMLHttpRequest)
		{
			return new XMLHttpRequest();
		}
	}
	
	function auto()
	{
		var query = document.getElementById("textArea");
		var auto = document.getElementById("auto");
		var tags = document.getElementById("tags");
		if(event.keyCode == 40)
		{  
			if(query.value != "" && auto.style.visibility != "hidden")
			{
				tags.focus();
				tags.selectedIndex = 0;
				query.value = tags.options[0].text;
				return;
			}
		}
		xmlHttpRequest = createXmlHttpRequest();
		xmlHttpRequest.onreadystatechange = backFct;
		var url = "<%=basePath%>"+"AutoComplete?tag=" + query.value;
		xmlHttpRequest.open("post", url, true);
		xmlHttpRequest.send(null);
	}
	
	function backFct()
	{
		if(xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200)
		{
			var rs = xmlHttpRequest.responseText;
			
			if(rs != "")
			{
				var tagsRs = rs.split(",");
				var auto = document.getElementById("auto");
				var tags = document.getElementById("tags");
				var query = document.getElementById("textArea");
				tags.length = 0;
				tags.size = tagsRs.length;
				//alert(tags.options.length);
				for(var i=0; i<tagsRs.length; i++)
				{
					tags.options.add(new Option(tagsRs[i], tagsRs[i])); //这个兼容IE与firefox
					//var option = document.createElement("option");
					//option.setAttribute("text", tagsRs[i]);
					//tags.options[i] = option;
				}
				auto.style.width = query.style.width;
				tags.style.width = query.style.width;
				auto.style.left = query.offsetLeft - 1;
				auto.style.top = query.offsetTop + query.offsetHeight + 1;
				auto.style.visibility = "visible";
			} 
			else
			{
				document.getElementById("auto").style.visibility = "hidden";
			}
		}
	}
	
	function text()
	{
		var query = document.getElementById("textArea");
		var auto = document.getElementById("auto");
		var tags = document.getElementById("tags");
		if(event.keyCode == 40 || event.keyCode == 38)   //40=down, 38=up
		{  
			if(query.value != "" && auto.style.visibility != "hidden")
			{
				query.value = tags.options[tags.selectedIndex].value;
			}
		}
		else if(event.keyCode == 13)   //13=enter
		{
			auto.style.visibility = "hidden";
			query.focus();
		}
	}
	
	function myclick()
	{
		var query = document.getElementById("textArea");
		var auto = document.getElementById("auto");
		var tags = document.getElementById("tags");
			if(query.value != "" && auto.style.visibility != "hidden")
			{
				query.value = tags.options[tags.selectedIndex].value;
				auto.style.visibility = "hidden";
				query.focus();
			}
	}
	
</script>

	<a href ="Search?keyword=<%=keyword%>&model=1&CurrentNum=1">按时间排序</a>
	<a href ="Search?keyword=<%=keyword%>&model=2&CurrentNum=1">按热度排序</a>
	<div align="center">
    
	<img src="logo.gif" />
    <form action="Search?model=0&CurrentNum=1" name="search" method="Post">
		<input style=" width: 400px; height: 30px; font-size: 18px;" 
			name="keyword" type="text" maxlength="150" 
			id="textArea" onkeyup="auto();" value=<%=keyword%> >
		<input type="submit" value="搜索一下" id = "search">
	</form>
	</div>
	
	<div id="auto" style="-webkit-appearance:none; border-style: solid; border-width: 1px; visibility: hidden; position: absolute;">
	    <select id="tags" onkeyup="text();" onclick="myclick();" size="0" style=" margin:-2px;">
	 	</select>
	 </div>
	<%  
	 ArrayList<String>  mayword=(ArrayList<String>)session.getAttribute("maywords"); 
	 if(mayword!=null&&mayword.size()!=0){	
	 	out.print("<p>你要找的是不是：");
	 	for(String s :mayword){
	 	out.print("\t<a href=\"Search?keyword="+s+"&model=0&CurrentNum=1\">"+s+"</a>");
	 	}
	 	out.print("</p>");
	 }
	 %>	
	
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
