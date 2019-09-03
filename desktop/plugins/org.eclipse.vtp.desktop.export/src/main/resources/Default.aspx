<%@ Page Language="C#" %>
<%@ Import Namespace="System.IO" %>
<%
   Context.Response.ContentType = "text/plain";
	
    string path = Server.MapPath(Request.ServerVariables["PATH_INFO"]); // to specify application's base directory
    path = path.Replace("Default.aspx", null);
    path = path.Replace("default.aspx", null);
 
  // to get folder names only
    foreach (string s in Directory.GetDirectories(path, "*.*", SearchOption.AllDirectories))
    {
        Response.Write("/" + s.Remove(0, path.Length).Replace("\\", "/") + "/\r\n");
    }
 
    // to get list of all files
    foreach (string s in Directory.GetFiles(path, "*.*", SearchOption.AllDirectories))
    {
        Response.Write("/" + s.Remove(0, path.Length).Replace("\\", "/")  + "\r\n");
    }
%>