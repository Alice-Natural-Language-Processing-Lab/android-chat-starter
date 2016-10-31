LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE 	:= chat.4
LOCAL_CFLAGS 	:= -w -std=gnu99 -O2 -DNULL=0 -DSOCKLEN_T=socklen_t -DLOCALE_NOT_USED -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64
LOCAL_CFLAGS 	+= -Drestrict='' -D__EMX__ -DOPUS_BUILD -DFIXED_POINT -DUSE_ALLOCA -DHAVE_LRINT -DHAVE_LRINTF -fno-math-errno
LOCAL_CFLAGS 	+= -DANDROID_NDK -DDISABLE_IMPORTGL -fno-strict-aliasing -fprefetch-loop-arrays -DAVOID_TABLES -DANDROID_TILE_BASED_DECODE -DANDROID_ARMV6_IDCT -DHAVE_STRCHRNUL=0
LOCAL_CPPFLAGS 	:= -DBSD=1 -ffast-math -O2 -funroll-loops
#LOCAL_LDLIBS 	:= -llog
LOCAL_LDLIBS 	:= -ljnigraphics -llog


LOCAL_SRC_FILES := \
./utils.c \
./image.c \

LOCAL_SRC_FILES     += \
./libjpeg/jcapimin.c \
./libjpeg/jcapistd.c \
./libjpeg/armv6_idct.S \
./libjpeg/jccoefct.c \
./libjpeg/jccolor.c \
./libjpeg/jcdctmgr.c \
./libjpeg/jchuff.c \
./libjpeg/jcinit.c \
./libjpeg/jcmainct.c \
./libjpeg/jcmarker.c \
./libjpeg/jcmaster.c \
./libjpeg/jcomapi.c \
./libjpeg/jcparam.c \
./libjpeg/jcphuff.c \
./libjpeg/jcprepct.c \
./libjpeg/jcsample.c \
./libjpeg/jctrans.c \
./libjpeg/jdapimin.c \
./libjpeg/jdapistd.c \
./libjpeg/jdatadst.c \
./libjpeg/jdatasrc.c \
./libjpeg/jdcoefct.c \
./libjpeg/jdcolor.c \
./libjpeg/jddctmgr.c \
./libjpeg/jdhuff.c \
./libjpeg/jdinput.c \
./libjpeg/jdmainct.c \
./libjpeg/jdmarker.c \
./libjpeg/jdmaster.c \
./libjpeg/jdmerge.c \
./libjpeg/jdphuff.c \
./libjpeg/jdpostct.c \
./libjpeg/jdsample.c \
./libjpeg/jdtrans.c \
./libjpeg/jerror.c \
./libjpeg/jfdctflt.c \
./libjpeg/jfdctfst.c \
./libjpeg/jfdctint.c \
./libjpeg/jidctflt.c \
./libjpeg/jidctfst.c \
./libjpeg/jidctint.c \
./libjpeg/jidctred.c \
./libjpeg/jmemmgr.c \
./libjpeg/jmemnobs.c \
./libjpeg/jquant1.c \
./libjpeg/jquant2.c \
./libjpeg/jutils.c

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE := libilbc
codec_dir := ilbc_src
LOCAL_SRC_FILES := \
    $(codec_dir)/anaFilter.c \
    $(codec_dir)/constants.c \
    $(codec_dir)/createCB.c \
    $(codec_dir)/doCPLC.c \
    $(codec_dir)/enhancer.c \
    $(codec_dir)/filter.c \
    $(codec_dir)/FrameClassify.c \
    $(codec_dir)/gainquant.c \
    $(codec_dir)/getCBvec.c \
    $(codec_dir)/helpfun.c \
    $(codec_dir)/hpInput.c \
    $(codec_dir)/hpOutput.c \
    $(codec_dir)/iCBConstruct.c \
    $(codec_dir)/iCBSearch.c \
    $(codec_dir)/iLBC_decode.c \
    $(codec_dir)/iLBC_encode.c \
    $(codec_dir)/LPCdecode.c \
    $(codec_dir)/LPCencode.c \
    $(codec_dir)/lsf.c \
    $(codec_dir)/packing.c \
    $(codec_dir)/StateConstructW.c \
    $(codec_dir)/StateSearchW.c \
    $(codec_dir)/syntFilter.c

LOCAL_C_INCLUDES += $(common_C_INCLUDES)
LOCAL_PRELINK_MODULE := false
include $(BUILD_STATIC_LIBRARY)

# Build JNI wrapper
include $(CLEAR_VARS)

LOCAL_MODULE := audiowrapper

LOCAL_C_INCLUDES += \
    $(JNI_H_INCLUDE) \
    $(codec_dir)

LOCAL_SRC_FILES := audiowrapper.c
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

LOCAL_STATIC_LIBRARIES := libilbc
LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
