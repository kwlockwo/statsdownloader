SET JAVA_HOME=.\jre_x86_1.8.0_31
SET LIB_PATH=.\lib
SET APP_JAR=statsDownloader.jar
SET SUPPORT_JARS=%LIB_PATH%\commons-io-2.4.jar;%LIB_PATH%\jsoup-1.7.2.jar;%LIB_PATH%\opencsv-2.3.jar;%LIB_PATH%\miglayout-core-4.2.jar;%LIB_PATH%\miglayout-swing-4.2.jar;%LIB_PATH%\jasypt.1.9.2.jar
SET CLASS_PATH=.;%APP_JAR%;%SUPPORT_JARS%

%JAVA_HOME%\bin\java -cp %CLASS_PATH% Run