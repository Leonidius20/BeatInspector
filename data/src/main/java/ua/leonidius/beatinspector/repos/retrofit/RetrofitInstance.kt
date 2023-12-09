package ua.leonidius.beatinspector.repos.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.spotify.com/v1/")
    .addConverterFactory(GsonConverterFactory. create())
    .client(OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor()) // todo: creating object in other place, in "app"
        .build())
    .build()

// todo what about the keys

val spotifyRetrofitClient = retrofit.create(SpotifyRetrofitClient::class.java)