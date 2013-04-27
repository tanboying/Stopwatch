LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_PROGUARD_ENABLED := full


LOCAL_SRC_FILES := $(call all-java-files-under, src)
#LOCAL_SRC_FILES := $(call all-java-files-under, ../DXAD,src)



ifeq ($(USE_BPHELPER_SRC),true)
LOCAL_STATIC_JAVA_LIBRARIES += bphelper
else
LOCAL_STATIC_JAVA_LIBRARIES += bphelper-permmgr
endif

LOCAL_PACKAGE_NAME := Stopwatch 
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

include $(BUILD_MULTI_PREBUILT)

