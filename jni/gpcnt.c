#include "com_gamepadcontrol_Gamepad.h"
#include <string.h>
#include <jni.h>
#include <fcntl.h>
#include <linux/input.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <sys/types.h>
#include <stdlib.h>

static struct input_event ev4;
static int event4_fd = -1;
int rd, i;
char buf[500];
char str[100];
int ret[3][2];
int cur;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	event4_fd = open("/dev/input/event4", O_RDONLY);

	return JNI_VERSION_1_6;
}


JNIEXPORT jstring JNICALL Java_com_gamepadcontrol_Gamepad_readData (JNIEnv *env, jobject obj) {
    if (event4_fd < 0) {
    	event4_fd = open("/dev/input/event4", O_RDONLY);
    	sprintf(buf, "{retcode: %d}", event4_fd);
    	return (*env)->NewStringUTF(env, buf);
    }

    cur = 0;
    while (1) {
    	rd = read(event4_fd, &ev4, sizeof(struct input_event));

    	if (rd < 0) {
    		sprintf(buf, "{retcode: %d}", rd);
    		return (*env)->NewStringUTF(env, buf);
    	}

    	if (ev4.type == 0 && ev4.code == 0 && ev4.value == 0) break;
    	ret[cur][0] = ev4.type;
    	ret[cur][1] = ev4.code;
    	ret[cur][2] = ev4.value;
    	++ cur;
    }

    // JSONObject
    /*
    sprintf(buf, "{retcode: %d, type1: %d, code1: %d, value1: %d,\
    		type2: %d, code2: %d, value2: %d,\
    		type3: %d, code3: %d, value3: %d}", rd, ev4[0].type, ev4[0].code, ev4[0].value, \
    		ev4[1].type, ev4[1].code, ev4[1].value, \
    		ev4[2].type, ev4[2].code, ev4[2].value);
    		*/
    sprintf(buf, "{retcode: %d, data: [{type: %d, code: %d, value: %d}", cur, ret[0][0], ret[0][1], ret[0][2]);
    for (i = 1; i < cur; ++ i) {
    	sprintf(str, ", {type: %d, code: %d, value: %d}", ret[i][0], ret[i][1], ret[i][2]);
    	strcat(buf, str);
    }
    strcat(buf, "]}");
    return (*env)->NewStringUTF(env, buf);

    /*
    jclass objclass = (*env)->FindClass(env, "com/gamepadcontrol/Gamepad$InputData");
    jmethodID mid = (*env)->GetMethodID(env, objclass, "<init>", "()V");
    jobject nobj = (*env)->NewObject(env, objclass, mid);
    jfieldID type1 = (*env)->GetFieldID(env, objclass, "type1", "I");
    jfieldID code1 = (*env)->GetFieldID(env, objclass, "code1", "I");
    jfieldID value1 = (*env)->GetFieldID(env, objclass, "value1", "I");
    jfieldID type2 = (*env)->GetFieldID(env, objclass, "type2", "I");
    jfieldID code2 = (*env)->GetFieldID(env, objclass, "code2", "I");
    jfieldID value2 = (*env)->GetFieldID(env, objclass, "value2", "I");
    jfieldID type3 = (*env)->GetFieldID(env, objclass, "type3", "I");
    jfieldID code3 = (*env)->GetFieldID(env, objclass, "code3", "I");
    jfieldID value3 = (*env)->GetFieldID(env, objclass, "value3", "I");

    (*env)->SetIntField(env, objclass, type1, ev4[0].type);
    (*env)->SetIntField(env, objclass, code1, ev4[0].code);
    (*env)->SetIntField(env, objclass, value1, ev4[0].value);
    (*env)->SetIntField(env, objclass, type2, ev4[1].type);
    (*env)->SetIntField(env, objclass, code2, ev4[1].code);
    (*env)->SetIntField(env, objclass, value2, ev4[1].value);
    (*env)->SetIntField(env, objclass, type3, ev4[2].type);
    (*env)->SetIntField(env, objclass, code3, ev4[2].code);
    (*env)->SetIntField(env, objclass, value3, ev4[2].value);
    */
}
