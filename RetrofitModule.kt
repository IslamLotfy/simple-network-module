package com.oysteregypt.oyster.injection.modules.retrofitModule

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.oysteregypt.oyster.injection.baseUrl.BaseUrlModule
import com.oysteregypt.oyster.injection.modules.jsonParser.gsonModule.DateDeserializer
import com.oysteregypt.oyster.injection.modules.jsonParser.gsonModule.GsonModule
import com.oysteregypt.oyster.injection.modules.okhttpclient.OkhttpClientModule
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [GsonModule::class, BaseUrlModule::class, OkhttpClientModule::class])
class RetrofitModule {

	@Provides
	fun providesDateDeserializer(context: Context): DateDeserializer {
		return DateDeserializer(context)
	}

	@Provides
	@Singleton
	fun gsonConverterFactory(gson: Gson, dateDeserializer: DateDeserializer)
			: GsonConverterFactory = GsonConverterFactory.create(GsonBuilder()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
			.registerTypeAdapter(Date::class.java, dateDeserializer)
			.create())

	@Provides
	@Singleton
	fun providesRetrofitInstance(
			@Named(value = BaseUrlModule.DAGGER_CONSTANTS.BASE_URL) baseUrl: String,
			client: OkHttpClient,
			gsonConverterFactory: GsonConverterFactory,
			dateDeserializer: DateDeserializer
	) =
			Retrofit.Builder().baseUrl(baseUrl)
					.client(client)
					.addConverterFactory(GsonConverterFactory.create(GsonBuilder()
							.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
							.registerTypeAdapter(Date::class.java, dateDeserializer)
							.create()))
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.build()
}