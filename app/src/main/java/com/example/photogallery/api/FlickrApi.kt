package com.example.photogallery.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=5c1a5951a8a17c676482dea0f679850e" +  // Ваш ключ
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")  // Пытаемся получить готовый url_s, но если нет — построим вручную
    suspend fun fetchPhotos(): FlickrResponse

    @GET("services/rest/?method=flickr.photos.search" +
            "&api_key=5c1a5951a8a17c676482dea0f679850e" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    suspend fun searchPhotos(@Query("text") query: String): FlickrResponse
}