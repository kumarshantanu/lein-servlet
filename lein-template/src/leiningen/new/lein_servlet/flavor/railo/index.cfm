<html>
<head>
<title>{{name}} home</title>
</head>
<body/>
<br /><br />
<h1>{{name}}</h1>
<big><big>
Welcome! Your CFML(Railo)/Clojure project is created.<br />
</big>
<br />
You should modify the generated content to continue hacking on the project.
</big>

<br /><br />
<cfoutput>Today is: #now()#</cfoutput>
<!--- calling Java instance methods --->
<cfset myList = createObject("java", "java.util.ArrayList").init() />
<cfset myList.add("a") />
<cfset myList.add("b") />
<cfdump var="#myList#" label="java.util.ArrayList" />
<!--- calling Java static methods --->
<cfset ctm = createObject("java", "java.lang.System").currentTimeMillis() />
<cfoutput>Time in millis: #ctm#<br /><br /></cfoutput>
<!--- calling Clojure functions --->
<cfset vector = createObject("java", "{{sanitized}}.util.jclojure").getvar("clojure.core", "vector") />
<cfset vinst = vector.invoke(1, 2, 3, 4, 5) />
Vector:
<cfdump var="#vinst#" label="clojure.core/vector" />
<cfset sum = createObject("java", "{{sanitized}}.util.jclojure").getvar("{{name}}.core", "sum") />
<cfset sinst = sum.invoke(1, 2, 3, 4, 5) />
<cfoutput>Sum: #sinst#</cfoutput>
</body>
</html>
