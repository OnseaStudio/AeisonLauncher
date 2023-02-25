cls
@echo off

set lib_path=librairies
set lombok_path=%lib_path%\Lombok\lombok-1.18.26.jar

set modules_path="%lombok_path%;%lib_path%\jgit\org.eclipse.jgit-6.4.0.202211300538-r.jar;%lib_path%\JavaEWAH\JavaEWAH-1.1.13.jar;%lib_path%\slf4j\slf4j-api-2.0.6.jar;%lib_path%\slf4j\slf4j-simple-2.0.6.jar"
set sources_path="src"

set main_class="src\fr\onsea\aeisonlauncher\AeisonLauncher.java"
set module_info="src\module-info.java"
set destination_path="target\generated-sources\compiled"

set java_path=C:\Program Files\Java\jdk-19
set javac_exe="%java_path%\bin\javac.exe"
set processor=--processor-module-path "%lombok_path%" -proc:only
set print=-verbose -Xlint -J-verbose -g

rem -implicit:class
rem %print%
rem %processor%

%javac_exe% %module_info% %main_class% -encoding UTF-8 -d %destination_path% --module-path %modules_path% -classpath %classes_path% -sourcepath %sources_path%