#include <jni.h>
#include <stdlib.h>
#include "Encryption.h"

void encrypt (int *v, int *k){
/* TEA encryption algorithm */
unsigned int y = v[0], z=v[1], sum = 0;
unsigned int delta = 0x9e3779b9, n=32;

	while (n-- > 0){
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;
}

void decrypt (int *v, int *k){
/* TEA decryption routine */
unsigned int n=32, sum, y=v[0], z=v[1];
unsigned int delta=0x9e3779b9l;

	sum = delta<<5;
	while (n-- > 0){
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;
}


// JNI library code for TEA encrypt/decrypt
JNIEXPORT jbyteArray JNICALL Java_Encryption_encryptArray
(JNIEnv *env, jobject object, jbyteArray v, jbyteArray k)
{
	jsize len_k, len_v;
	jbyte *buff_k, *buff_v, *ptr;
	jboolean isCopyK, isCopyV;
	jbyteArray result;

	len_k = (*env)->GetArrayLength(env, k);
	len_v = (*env)->GetArrayLength(env, v);

	if(len_k  != 8*sizeof(int)) {
		printf("Error: Key is not of correct size\n");
		exit(0);
	}

	if(len_v % 4*sizeof(int) != 0) {
		printf("error: values are not padded\n");
		exit(0);
	}

	buff_k = (*env)->GetByteArrayElements(env, k, &isCopyK);
	buff_v = (*env)->GetByteArrayElements(env, v, &isCopyV);

	ptr = buff_v;

	while(ptr < buff_v + len_v) {
		encrypt((int*)ptr, (int*)buff_k);
		ptr += 4*sizeof(int);
	}

	result = (*env)->NewByteArray(env, len_v);
	(*env)->SetByteArrayRegion(env, result, 0, len_v, buff_v);
	return result;
}

JNIEXPORT jbyteArray JNICALL Java_Encryption_decryptArray
(JNIEnv *env, jobject object, jbyteArray v, jbyteArray k)
{
	jsize len_k, len_v;
	jbyte *buff_k, *buff_v, *ptr;
	jboolean isCopyK, isCopyV;
	jbyteArray result;

	len_k = (*env)->GetArrayLength(env, k);
	len_v = (*env)->GetArrayLength(env, v);

	if(len_k  != 8*sizeof(int)) {
		printf("Error: Key is not of correct size\n");
		exit(0);
	}

	if(len_v % 4*sizeof(int) != 0) {
		printf("error: values are not padded\n");
		exit(0);
	}

	buff_k = (*env)->GetByteArrayElements(env, k, &isCopyK);
	buff_v = (*env)->GetByteArrayElements(env, v, &isCopyV);

	ptr = buff_v;

	while(ptr < buff_v + len_v) {
		decrypt((int*)ptr, (int*)buff_k);
		ptr += 4*sizeof(int);
	}

	result = (*env)->NewByteArray(env, len_v);
	(*env)->SetByteArrayRegion(env, result, 0, len_v, buff_v);
	return result;
}

