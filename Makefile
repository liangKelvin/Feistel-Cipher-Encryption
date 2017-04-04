make: 

	javac *.java
	javah Encryption
	# for compiling on lab machines aka Linux
	gcc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -shared -fpic -o libEncryption.so lib_encryption.c

	# for compiling on macOs 
	#gcc -c -fPIC -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers/ lib_Encryption.c -o libEncryption.o
	#libtool -dynamic -lSystem libEncryption.o -o libEncryption.dylib
	#LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.


clean: 

	rm Client.class
	rm Encryption.class
	rm Encryption.h 
	rm Request.class
	rm Response.class
	rm Server.class 
	rm ServerThread.class
	rm User.class
	rm libEncryption.dylib
	rm libEncryption.o
