package com.example.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=f0e45f51b2c376ac020d410002270882" +  // Ваш ключ
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")  // Пытаемся получить готовый url_s, но если нет — построим вручную
    suspend fun fetchPhotos(): FlickrResponse

    @GET("services/rest/?method=flickr.photos.search" +
            "&api_key=f0e45f51b2c376ac020d410002270882" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    suspend fun searchPhotos(@Query("text") query: String): FlickrResponse
}