make: 

	javac *.java
	javah Encryption

	# for compiling on macOs 
	gcc -c -fPIC -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers/ lib_Encryption.c -o libEncryption.o
	libtool -dynamic -lSystem libEncryption.o -o libEncryption.dylib
	LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.

lab:
	javac *.java
	javah Encryption
	#for compiling on lab machines aka Linux
	gcc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -shared -fpic -o libEncryption.so lib_encryption.c


clean: 

	rm Client.class
	rm Encryption.class
	rm Encryption.h 
	rm Server.class 
	rm ServerThread.class
	rm User.class
	rm SaveLoadController.class
	rm DiffieH.class
	rm libEncryption.dylib
	rm libEncryption.o
	rm users.ser

server:
	java Server

client1: 
	java Client Kelvin 123 localhost

client2: 
	java Client Max 123 localhost

client3:
	java Client Jerry 123 localhost
