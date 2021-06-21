#include "yuvutil.h"

namespace jniutils {

    /**
     * nv21转I420
     */
    void nv21ToI420(jbyte *src_nv21_data, jint width, jint height, jbyte *src_i420_data) {
        jint src_y_size = width * height;
        jint src_u_size = (width >> 1) * (height >> 1);

        jbyte *src_nv21_y_data = src_nv21_data;
        jbyte *src_nv21_vu_data = src_nv21_data + src_y_size;

        jbyte *src_i420_y_data = src_i420_data;
        jbyte *src_i420_u_data = src_i420_data + src_y_size;
        jbyte *src_i420_v_data = src_i420_data + src_y_size + src_u_size;


        libyuv::NV21ToI420((const uint8 *) src_nv21_y_data, width,
                           (const uint8 *) src_nv21_vu_data, width,
                           (uint8 *) src_i420_y_data, width,
                           (uint8 *) src_i420_u_data, width >> 1,
                           (uint8 *) src_i420_v_data, width >> 1,
                           width, height);
    }

    /**
     * 旋转I420
     */
    void rotateI420(jbyte *src_i420_data, jint width, jint height,
                    jbyte *dst_i420_data, jint degree) {
        jint src_i420_y_size = width * height;
        jint src_i420_u_size = (width >> 1) * (height >> 1);

        jbyte *src_i420_y_data = src_i420_data;
        jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
        jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

        jbyte *dst_i420_y_data = dst_i420_data;
        jbyte *dst_i420_u_data = dst_i420_data + src_i420_y_size;
        jbyte *dst_i420_v_data = dst_i420_data + src_i420_y_size + src_i420_u_size;

        //要注意这里的width和height在旋转之后是相反的
        if (degree == libyuv::kRotate90 || degree == libyuv::kRotate270) {
            libyuv::I420Rotate((const uint8 *) src_i420_y_data, width,
                               (const uint8 *) src_i420_u_data, width >> 1,
                               (const uint8 *) src_i420_v_data, width >> 1,
                               (uint8 *) dst_i420_y_data, height,
                               (uint8 *) dst_i420_u_data, height >> 1,
                               (uint8 *) dst_i420_v_data, height >> 1,
                               width, height,
                               (libyuv::RotationMode) degree);
        } else if (degree == libyuv::kRotate180) {
            libyuv::I420Rotate((const uint8 *) src_i420_y_data, width,
                               (const uint8 *) src_i420_u_data, width >> 1,
                               (const uint8 *) src_i420_v_data, width >> 1,
                               (uint8 *) dst_i420_y_data, width,
                               (uint8 *) dst_i420_u_data, width >> 1,
                               (uint8 *) dst_i420_v_data, width >> 1,
                               width, height,
                               (libyuv::RotationMode) degree);
        }
    }

    /**
     * I420镜像
     */
    void mirrorI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data) {
        jint src_i420_y_size = width * height;
        jint src_i420_u_size = (width >> 1) * (height >> 1);

        jbyte *src_i420_y_data = src_i420_data;
        jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
        jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

        jbyte *dst_i420_y_data = dst_i420_data;
        jbyte *dst_i420_u_data = dst_i420_data + src_i420_y_size;
        jbyte *dst_i420_v_data = dst_i420_data + src_i420_y_size + src_i420_u_size;

        libyuv::I420Mirror((const uint8 *) src_i420_y_data, width,
                           (const uint8 *) src_i420_u_data, width >> 1,
                           (const uint8 *) src_i420_v_data, width >> 1,
                           (uint8 *) dst_i420_y_data, width,
                           (uint8 *) dst_i420_u_data, width >> 1,
                           (uint8 *) dst_i420_v_data, width >> 1,
                           width, height);

    }

    /**
     * i420转nv21
     */
    void i420ToNV21(jbyte *src_i420_data, jint width, jint height, jbyte *src_nv21_data) {
        jint src_y_size = width * height;
        jint src_u_size = (width >> 1) * (height >> 1);

        jbyte *src_i420_y_data = src_i420_data;
        jbyte *src_i420_u_data = src_i420_data + src_y_size;
        jbyte *src_i420_v_data = src_i420_data + src_y_size + src_u_size;

        jbyte *src_nv21_y_data = src_nv21_data;
        jbyte *src_nv21_vu_data = src_nv21_data + src_y_size;


        libyuv::I420ToNV21(
                (const uint8 *) src_i420_y_data, width,
                (const uint8 *) src_i420_u_data, width >> 1,
                (const uint8 *) src_i420_v_data, width >> 1,
                (uint8 *) src_nv21_y_data, width,
                (uint8 *) src_nv21_vu_data, width,
                width, height);
    }

    void scaleI420(jbyte *src_i420_data, jint width, jint height, jbyte *dst_i420_data,
                   jint dst_width, jint dst_height) {

        jint src_i420_y_size = width * height;
        jint src_i420_u_size = (width >> 1) * (height >> 1);
        jbyte *src_i420_y_data = src_i420_data;
        jbyte *src_i420_u_data = src_i420_data + src_i420_y_size;
        jbyte *src_i420_v_data = src_i420_data + src_i420_y_size + src_i420_u_size;

        jint dst_i420_y_size = dst_width * dst_height;
        jint dst_i420_u_size = (dst_width >> 1) * (dst_height >> 1);
        jbyte *dst_i420_y_data = dst_i420_data;
        jbyte *dst_i420_u_data = dst_i420_data + dst_i420_y_size;
        jbyte *dst_i420_v_data = dst_i420_data + dst_i420_y_size + dst_i420_u_size;

        libyuv::I420Scale((const uint8 *) src_i420_y_data, width,
                          (const uint8 *) src_i420_u_data, width >> 1,
                          (const uint8 *) src_i420_v_data, width >> 1,
                          width, height,
                          (uint8 *) dst_i420_y_data, dst_width,
                          (uint8 *) dst_i420_u_data, dst_width >> 1,
                          (uint8 *) dst_i420_v_data, dst_width >> 1,
                          dst_width, dst_height,
                          libyuv::FilterModeEnum::kFilterBox);
    }

    /**
     * 旋转->nv21
     */
    void rotateToNV21(jbyte *src_i420_data, jint width, jint height,
                      jbyte *src_nv21_data, jint rotation) {

        int size = width * height * 3 / 2;
        jbyte *i420_rotate = (jbyte *) malloc(sizeof(jbyte) * size);

        jniutils::rotateI420(src_i420_data, width, height, i420_rotate, rotation);
        if (rotation == 90 || rotation == 270) {
            int temp = width;
            width = height;
            height = temp;
        }
        jniutils::i420ToNV21(i420_rotate, width, height, src_nv21_data);
        free(i420_rotate);
    }

    /**
     * 镜像->nv21
     */
    void mirrorToNV21(jbyte *src_i420_data, jint width, jint height, jbyte *src_nv21_data) {

        int size = width * height * 3 / 2;
        jbyte *i420_mirror = (jbyte *) malloc(sizeof(jbyte) * size);

        jniutils::mirrorI420(src_i420_data, width, height, i420_mirror);
        jniutils::i420ToNV21(i420_mirror, width, height, src_nv21_data);
        free(i420_mirror);
    }

    /**
    * 旋转->镜像->nv21
    */
    void rotateMirrorToNV21(jbyte *src_i420_data, jint width, jint height,
                            jbyte *src_nv21_data, jint rotation) {

        int size = width * height * 3 / 2;
        jbyte *i420_rotate = (jbyte *) malloc(sizeof(jbyte) * size);
        jbyte *i420_mirror = (jbyte *) malloc(sizeof(jbyte) * size);

        jniutils::rotateI420(src_i420_data, width, height, i420_rotate, rotation);
        if (rotation == 90 || rotation == 270) {
            int temp = width;
            width = height;
            height = temp;
        }

        jniutils::mirrorI420(i420_rotate, width, height, i420_mirror);
        jniutils::i420ToNV21(i420_mirror, width, height, src_nv21_data);
        free(i420_rotate);
        free(i420_mirror);
    }
}