package com.oysteregypt.oyster.injection.modules.okhttpclient

import com.oysteregypt.oyster.BuildConfig
import com.oysteregypt.oyster.dataSource.pref.AppPreferencesHelper
import com.oysteregypt.oyster.injection.modules.jsonParser.gsonModule.GsonModule
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [GsonModule::class])
class OkhttpClientModule {

	private val interceptor = Interceptor {
		var original = it.request()
		val originalHttpUrl = original.url
		val url = originalHttpUrl
				.newBuilder()
//                .addQueryParameter("lang", lang)
				.build()
		val requestBuilder = original.newBuilder().url(url)
		requestBuilder.addHeader("Content-Type", "application/json")
		original = if (ACCESSTOKEN.TOKEN.length > 1) {
			requestBuilder
					.addHeader(
							"authorization",
							"Bearer " + AppPreferencesHelper.instance.accessToken
					).build()
		} else {
			requestBuilder
					.removeHeader("Authorization")
					.build()
		}

		it.proceed(original)

	}

	@Provides
	fun provideLoggingInterceptor() =
			if (BuildConfig.DEBUG) {
				val httpLoggingInterceptor = HttpLoggingInterceptor()
				httpLoggingInterceptor.apply { httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY }
			} else {
				val httpLoggingInterceptor = HttpLoggingInterceptor()
				httpLoggingInterceptor.apply {
					httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
				}
			}

	@Provides
	@Singleton
	fun providesOkHttpClient(
			cache: Cache,
			loggingInterceptor: HttpLoggingInterceptor,
			mPrefrenceHelper: AppPreferencesHelper
	): OkHttpClient =
			OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).writeTimeout(
					60,
					TimeUnit.SECONDS
			).cache(cache).addInterceptor(interceptor).addInterceptor(loggingInterceptor).build()
}