#include "me_sunstorm_showmanager_ltc_LibLTCWrapper.h"

#include <cstdlib>

#include "jni.h"
#include "ltc.h"

LTCEncoder* encoder;
SMPTETimecode st;
ltcsnd_sample_t* buffer;

JNIEXPORT void JNICALL Java_me_sunstorm_showmanager_ltc_LibLTCWrapper_init(JNIEnv*, jobject, jint sampleRate, jint frameRate)
{
	encoder = ltc_encoder_create(sampleRate, frameRate, frameRate == 25 ? LTC_TV_625_50 : LTC_TV_525_60, 0);
	ltc_encoder_set_filter(encoder, 0);
	ltc_encoder_set_filter(encoder, 25.0);
	ltc_encoder_set_volume(encoder, -18.0);

	buffer = (ltcsnd_sample_t*) calloc(ltc_encoder_get_buffersize(encoder), sizeof(ltcsnd_sample_t));
}


JNIEXPORT void JNICALL Java_me_sunstorm_showmanager_ltc_LibLTCWrapper_setTime(JNIEnv*, jobject, jint hour, jint min, jint sec, jint frame)
{
	st.years = 1;
	st.months = 1;
	st.days = 1;
	st.hours = hour;
	st.mins = min;
	st.secs = sec;
	st.frame = sec;
	ltc_encoder_set_timecode(encoder, &st);
}

JNIEXPORT jbyteArray JNICALL Java_me_sunstorm_showmanager_ltc_LibLTCWrapper_getData(JNIEnv* env, jobject This)
{
	ltc_encoder_encode_frame(encoder);
	int len = ltc_encoder_copy_buffer(encoder, buffer);
	jbyteArray ret = env->NewByteArray(len);
	env->SetByteArrayRegion(ret, 0, len, reinterpret_cast<const jbyte*>(buffer));
	return ret;
}


JNIEXPORT void JNICALL Java_me_sunstorm_showmanager_ltc_LibLTCWrapper_free(JNIEnv*, jobject)
{
	ltc_encoder_free(encoder);
	free(buffer);
}