//
// Created by Absinthe on 2019/11/22.
//

#include <jni.h>
#include <string>
#include <cstring>
#include <malloc.h>
#include "izuko.h"

extern "C"
const char *PACKAGE_NAME = "com.absinthe.anywhere_";
const char *RELEASE_SIGN_MD5 = "D689B2D1B05AD9C1F7E41B4608C4DDF3";

/**
 * getApplication
 *
 * @param env
 * @return j_object
 */
static jobject getApplication(JNIEnv *env) {
    jobject application = nullptr;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");

    if (activity_thread_clz != nullptr) {
        jmethodID currentApplication = env->GetStaticMethodID(
                activity_thread_clz, "currentApplication", "()Landroid/app/Application;");
        if (currentApplication != nullptr) {
            application = env->CallStaticObjectMethod(activity_thread_clz, currentApplication);
        }
        env->DeleteLocalRef(activity_thread_clz);
    }
    return application;
}

/**
 * HexToString
 *
 * @param source
 * @param dest
 * @param sourceLen
 */
#pragma clang diagnostic push
#pragma ide diagnostic ignored "cppcoreguidelines-avoid-magic-numbers"
#pragma ide diagnostic ignored "hicpp-signed-bitwise"
static void ToHexStr(const char *source, char *dest, int sourceLen) {
    short i;
    char highByte;
    char lowByte;

    for (i = 0; i < sourceLen; i++) {
        highByte = source[i] >> 4;
        lowByte = (char) (source[i] & 0x0f);
        highByte += 0x30;

        if (highByte > 0x39) {
            dest[i * 2] = (char) (highByte + 0x07);
        } else {
            dest[i * 2] = highByte;
        }

        lowByte += 0x30;
        if (lowByte > 0x39) {
            dest[i * 2 + 1] = (char) (lowByte + 0x07);
        } else {
            dest[i * 2 + 1] = lowByte;
        }
    }
}
#pragma clang diagnostic pop

/**
 *
 * byteArrayToMd5
 *
 * @param env
 * @param source
 * @return j_string
 */
static jstring ToMd5(JNIEnv *env, jbyteArray source) {
    // MessageDigest
    jclass classMessageDigest = env->FindClass("java/security/MessageDigest");
    // MessageDigest.getInstance()
    jmethodID midGetInstance = env->GetStaticMethodID(classMessageDigest, "getInstance",
                                                      "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    // MessageDigest object
    jobject objMessageDigest = env->CallStaticObjectMethod(classMessageDigest, midGetInstance,
                                                           env->NewStringUTF("md5"));

    jmethodID midUpdate = env->GetMethodID(classMessageDigest, "update", "([B)V");
    env->CallVoidMethod(objMessageDigest, midUpdate, source);

    // Digest
    jmethodID midDigest = env->GetMethodID(classMessageDigest, "digest", "()[B");
    auto objArraySign = (jbyteArray) env->CallObjectMethod(objMessageDigest, midDigest);

    jsize intArrayLength = env->GetArrayLength(objArraySign);
    jbyte *byte_array_elements = env->GetByteArrayElements(objArraySign, nullptr);
    size_t length = (size_t) intArrayLength * 2 + 1;
    char *char_result = (char *) malloc(length);
    memset(char_result, 0, length);

    ToHexStr((const char *) byte_array_elements, char_result, intArrayLength);
    // add \0 at tail
    *(char_result + intArrayLength * 2) = '\0';

    jstring stringResult = env->NewStringUTF(char_result);
    // release
    env->ReleaseByteArrayElements(objArraySign, byte_array_elements, JNI_ABORT);
    // Pointer
    free(char_result);

    return stringResult;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_absinthe_anywhere_1_utils_manager_IzukoHelper_checkSignature(JNIEnv *env, jclass clazz) {

    jobject context = getApplication(env);
    // get Context object
    jclass cls = env->GetObjectClass(context);
    // get getPackageManager() ID
    jmethodID mid = env->GetMethodID(cls, "getPackageManager",
                                     "()Landroid/content/pm/PackageManager;");

    // get PackageManager
    jobject pm = env->CallObjectMethod(context, mid);

    // get getPackageName() ID
    mid = env->GetMethodID(cls, "getPackageName", "()Ljava/lang/String;");
    // get current package name
    auto packageName = (jstring) env->CallObjectMethod(context, mid);
    const char *c_pack_name = env->GetStringUTFChars(packageName, nullptr);

    // check package name, exit if not same
    if (strcmp(c_pack_name, PACKAGE_NAME) != 0) {
        exit(0);
    }
    // get PackageManager object
    cls = env->GetObjectClass(pm);
    // get getPackageInfo() ID
    mid = env->GetMethodID(cls, "getPackageInfo",
                           "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    // get PackageInfo
    jobject packageInfo = env->CallObjectMethod(pm, mid, packageName, 0x40); //GET_SIGNATURES = 64;
    // get PackageInfo object
    cls = env->GetObjectClass(packageInfo);
    // get signature array prop ID
    jfieldID fid = env->GetFieldID(cls, "signatures", "[Landroid/content/pm/Signature;");
    // get signature array
    auto signatures = (jobjectArray) env->GetObjectField(packageInfo, fid);
    // get signature
    jobject signature = env->GetObjectArrayElement(signatures, 0);

    // get Signature object
    cls = env->GetObjectClass(signature);
    mid = env->GetMethodID(cls, "toByteArray", "()[B");
    // get current signature info
    auto signatureByteArray = (jbyteArray) env->CallObjectMethod(signature, mid);
    //turn to jstring
    jstring str = ToMd5(env, signatureByteArray);
    char *c_msg = (char *) env->GetStringUTFChars(str, nullptr);

    if (strcmp(c_msg, RELEASE_SIGN_MD5) != 0) {
        exit(0);
    }
}