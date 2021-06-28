cmake_minimum_required(VERSION 3.10)

#include头文件目录
include_directories(src/main/cpp)

#source directory源文件目录
file(GLOB FACE_SRC
        src/main/cpp/yuv_jni.cpp
        src/main/cpp/yuvutil.cpp)
set(FACE_COMPILE_CODE ${FACE_SRC})

#添加libyuv
include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)
add_library(libyuv SHARED IMPORTED)
set_target_properties(libyuv
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/src/main/cpp/include/libs/${ANDROID_ABI}/libyuv.so)

#编译为动态库
add_library(yuv_util SHARED ${FACE_COMPILE_CODE})

#添加工程所依赖的库
target_link_libraries(yuv_util libyuv)