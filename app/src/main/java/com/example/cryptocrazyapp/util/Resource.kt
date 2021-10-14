package com.example.cryptocrazyapp.util

/** Api'ye bi istek yaptığımızda hata-yükleniyor-başarı durumları var bu dosya herhangi bir T
 *  döndürüyor bu yüzden herhangi bir yerde bu dosyayı kullanıp başarı-hata-yükleniyor'u ele alabiliyoruz
 * */

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}