<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>{{name}}</title>
    </head>
    <body>
      <h1>{{name}}</h1>
      <big>
        <big>
          <br/>
          Welcome! Your Struts+JSP/Clojure project is created.<br/><br/>
        </big>

        You should modify the generated content to continue hacking on the project.<br/><br/>
      </big>

      <h2><bean:write name="HelloWorldActionForm" property="message"></bean:write></h1>
      <p>
      Numbers <bean:write name="HelloWorldActionForm" property="extra"></bean:write>
      </p>
      <p>
      Sum <bean:write name="HelloWorldActionForm" property="sum"></bean:write>
      </p>
    </body>
</html>
