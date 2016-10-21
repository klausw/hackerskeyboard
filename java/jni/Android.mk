LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/src

LOCAL_SRC_FILES := \
	jni/org_pocketworkstation_pckeyboard_BinaryDictionary.cpp \
	src/dictionary.cpp \
	src/char_utils.cpp

#ifneq ($(TARGET_ARCH),x86)
#LOCAL_NDK_VERSION := 4
#LOCAL_SDK_VERSION := 8
#endif

LOCAL_MODULE := libjni_pckeyboard

LOCAL_MODULE_TAGS := user

include $(BUILD_SHARED_LIBRARY)
