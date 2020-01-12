//
// Created by Absinthe on 2019/11/23.
//

#include <jni.h>
#ifndef ANYWHERE_NATIVE_LIB_H
#define ANYWHERE_NATIVE_LIB_H

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL
Java_com_absinthe_anywhere_1_utils_manager_IzukoHelper_checkSignature(JNIEnv *env, jclass clazz);

#ifdef __cplusplus
}
#endif

#endif //ANYWHERE_NATIVE_LIB_H
