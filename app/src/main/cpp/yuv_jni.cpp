#include <android/bitmap.h>
#include <android/log.h>
#include <jni.h>
#include <string>
#include <vector>
#include <malloc.h>

#include "yuvutil.h"

using namespace std;
#define TAG "YuvSo"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)


extern "C" {

JNIEXPORT jbyteArray JNICALL
Java_siren_ocean_yuv_YuvUtil_nv21RotateMirror(JNIEnv *env, jclass instance,
                                              jbyteArray src_, jint width, jint height,
                                              jint rotation, jboolean isMirror, jfloat ratio) {
    jbyte *src_data = env->GetByteArrayElements(src_, NULL);

    int size = width * height * 3 / 2;
    jbyte *i420_data = (jbyte *) malloc(sizeof(jbyte) * size);
    jniutils::nv21ToI420(src_data, width, height, i420_data);

    jbyte *mData;
    int mWidth, mHeight, mSize;

    if (ratio < 1) {
        mWidth = (int) (width * ratio);
        mHeight = (int) (height * ratio);
        mSize = mWidth * mHeight * 3 / 2;
        mData = (jbyte *) malloc(sizeof(jbyte) * mSize);
        jniutils::scaleI420(i420_data, width, height, mData, mWidth, mHeight);
        free(i420_data);
    } else {
        mWidth = width;
        mHeight = height;
        mSize = size;
        mData = i420_data;
    }

    jbyte *nv21_data = (jbyte *) malloc(sizeof(jbyte) * mSize);
    if (rotation != 0) {
        if (isMirror) {
            jniutils::rotateMirrorToNV21(mData, mWidth, mHeight, nv21_data, rotation);
        } else {
            jniutils::rotateToNV21(mData, mWidth, mHeight, nv21_data, rotation);
        }
    } else {
        if (isMirror) {
            jniutils::mirrorToNV21(mData, mWidth, mHeight, nv21_data);
        } else {
            jniutils::i420ToNV21(mData, mWidth, mHeight, nv21_data);
        }
    }

    jbyteArray byteArray = env->NewByteArray(mSize);
    env->SetByteArrayRegion(byteArray, 0, mSize, nv21_data);
    env->ReleaseByteArrayElements(src_, src_data, 0);

    free(mData);
    free(nv21_data);
    return byteArray;
}
}