cls
@echo off

set lib_path=librairies
set lombok_path=%lib_path%\Lombok\lombok-1.18.26.jar

set modules_path="%lombok_path%;%lib_path%\jgit\org.eclipse.jgit-6.4.0.202211300538-r.jar;%lib_path%\JavaEWAH\JavaEWAH-1.1.13.jar;%lib_path%\slf4j\slf4j-api-2.0.6.jar;%lib_path%\slf4j\slf4j-simple-2.0.6.jar"

set sources_path="src"
set main_class="src\fr\onsea\aeisonlauncher\AeisonLauncher.java"
set module_info="src\module-info.java"
set destination_path="target\generated-sources\delomboked"

set java_exe="C:\Program Files\Java\jdk-19\bin\java.exe"
set delombok=-jar "%lombok_path%" delombok

cls
@echo on

%java_exe% -Dfile.encoding=UTF-8 %delombok% %sources_path% -e UTF-8 -n -d %destination_path% --module-path %modules_path% --sourcepath %sources_path%