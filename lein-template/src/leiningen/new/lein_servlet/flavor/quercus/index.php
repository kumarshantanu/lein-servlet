<html>
<head>
<title>{{name}} home</title>
</head>
<body/>
<br/><br/>
<h1>{{name}}</h1>
<br/>
<big><big>
Welcome! Your PHP(Quercus)/Clojure project is created.<br/>
</big>
<br/>
You should modify the generated content to continue hacking on the project.
</big>

<br/><br/>
<?php

echo "Now is ", date("Y-m-d H:i:s"), "<br/><br/>";

// calling Java instance methods

import java.util.ArrayList;

$list = new ArrayList();
$list->add("a");
$list->add("b");
echo "List as array ";
print_r($list);
echo "<br/><br/>";

// calling Java static methods

$system = java_class("java.lang.System");
echo "Time in millis ", $system->currentTimeMillis(), "<br/><br/>";

// calling Clojure functions

$jclojure = java_class("{{sanitized}}.util.jclojure");

$vector = $jclojure->getvar("clojure.core", "vector");
echo "Vector as array ";
print_r($vector->invoke(1, 2, 3, 4, 5));
echo "<br/><br/>";

$sum = $jclojure->getvar("{{name}}.core", "sum");
echo "Sum ";
echo $sum->invoke(1, 2, 3, 4, 5);
echo "<br/><br/>";


?>
</body>
</html>
