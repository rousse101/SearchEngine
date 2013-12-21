<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"
	+request.getServerName()+":"
	+request.getServerPort()+path+"/";
	
	System.out.println("basePath is "+basePath);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>新闻搜索</title>
      
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
  
  <script type="text/javascript">
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
</script>


  <body>
  
  <div align="center">
	<img src="logo.gif"><br/>
	<form action="Search?model=0&CurrentNum=1" 
			name="search" 
			method="Post" 
			enctype="application/x-www-form-urlencoded">
			<input name="keyword" type="text" maxlength="100" onkeyup="auto();" style=" width: 400px; height: 30px; font-size: 18px;"
				id="textArea" />
			<input type="submit" style="height:32px;" value="搜索一下" 
				id = "search" />
	</form>
	</div>
	
	<div id="auto" style="-webkit-appearance:none; border-style: solid; border-width: 1px; visibility: hidden; position: absolute;">
	    <select id="tags" onkeyup="text();" size="0" style=" margin:-2px;">
	 	</select>
	 </div>
  </body>

</html>
