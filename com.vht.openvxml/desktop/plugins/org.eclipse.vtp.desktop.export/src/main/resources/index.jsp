<%@ page language="java" contentType="text/plain; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.io.File,java.io.PrintWriter"%>
<%
String requestURI = request.getRequestURI();
//System.out.println(requestURI);
String contextPath = application.getContextPath();
//System.out.println(contextPath);
if(contextPath != null && !contextPath.equals("") && !contextPath.equals("/") && requestURI.startsWith(contextPath))
{
	//remove context path from request URI
	requestURI = requestURI.substring(contextPath.length());
}
//System.out.println(requestURI);
//System.out.println(application.getRealPath(requestURI));
String realPath = application.getRealPath(requestURI);
//System.out.println(realPath);
//System.out.println(request.getPathInfo());
File root = new File(realPath);
//System.out.println(root.exists());
PrintWriter pw = response.getWriter();
for(File child : root.listFiles())
{
	if(child.isDirectory())
	{
		printDir("", child, pw);
	}
}
pw.close();
response.flushBuffer();
%>
<%! 
public void printDir(String prefix, File dir, PrintWriter out)
{
	out.println(prefix + "/" + dir.getName() + "/");
	for(File child : dir.listFiles())
	{
		if(child.isDirectory())
		{
			printDir(prefix + "/" + dir.getName(), child, out);
		}
		else
		{
			out.println(prefix + "/" + dir.getName() + "/" + child.getName());
		}
	}
}
%>
