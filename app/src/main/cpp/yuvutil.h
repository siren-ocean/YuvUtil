#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <dirent.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <libyuv/convert.h>
#include <include/libyuv/scale.h>
#include <malloc.h>

#define TAG "YuvSo"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

namespace jniutils {

    void nv21ToI420(jbyte *src_nv21_data, jint width, jint height, jbyte *src_i420_data);

    void rotateI420(jbyte *src_i420_data, jint width, jint height,
                    jbyte *dst_i420_data, jint degree);

    void mirrorI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data);

    void i420ToNV21(jbyte *src_i420_data, jint width, jint height, jbyte *src_nv21_data);

    void scaleI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data,
                   jint dst_width, jint dst_height);

    void rotateToNV21(jbyte *src_i420_data, jint width, jint height,
                      jbyte *src_nv21_data, jint rotation);

    void mirrorToNV21(jbyte *src_i420_data, jint width, jint height, jbyte *src_nv21_data);

    void rotateMirrorToNV21(jbyte *src_i420_data, jint width, jint height,
                            jbyte *src_nv21_data, jint rotation);
}