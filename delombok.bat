@echo on

set lib_path=J:\Programmations\java\librairies
set m2_path=C:\Users\seyro\.m2\repository

set modules_path=%lib_path%\archives\Lombok\lombok-1.18.26.jar;%m2_path%\org\eclipse\jgit\org.eclipse.jgit\6.4.0.202211300538-r\org.eclipse.jgit-6.4.0.202211300538-r.jar;%m2_path%\com\googlecode\javaewah\JavaEWAH\1.1.13\JavaEWAH-1.1.13.jar;%m2_path%\org\slf4j-api\2.0.6\slf4j-api-2.0.6.jar;%m2_path%\org\slf4j\slf4j-simple\2.0.6\slf4j-simple-2.0.6.jar
set source_path="src"
set destination_path="target\generated-sources\delomboked"

set java_path=C:\Program Files\Java\jdk-19\
set java_exe="%java_path%\bin\java.exe"
set delombok=-jar "%lib_path%\archives\Lombok\lombok-1.18.26.jar" delombok

%java_exe% -Dfile.encoding=UTF-8 --add-modules jdk.incubator.vector %delombok% %source_path% -e UTF-8 -n -d %destination_path% --module-path "%modules_path%"