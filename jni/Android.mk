LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := gpcnt
LOCAL_SRC_FILES := gpcnt.c

include $(BUILD_SHARED_LIBRARY)
