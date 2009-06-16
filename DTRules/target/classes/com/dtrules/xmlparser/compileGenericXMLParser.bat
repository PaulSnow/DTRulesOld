dir
set pdir=C:\eb\eb_dev2\RulesEngine\DTRules\src\main\java\com\dtrules
set pdir2=C:\jflex-1.4.1
set xmldir=%pdir%\xmlparser
java -classpath %pdir2%\lib\JFlex.jar JFlex.Main -d %xmldir% %xmldir%\GenericXMLParser.flex
pause