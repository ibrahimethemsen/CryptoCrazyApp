package com.example.cryptocrazyapp.service

import com.example.cryptocrazyapp.model.Crypto
import com.example.cryptocrazyapp.model.CryptoList
import retrofit2.http.GET
import retrofit2.http.Query

//api mizi yazıyoruz
interface CryptoAPI {

    /** retrofit bizim için gidip url de verileri bulacak listemiz için anahtarımızı vermemiz yeterli
     * */
    @GET("prices")
    suspend fun getCryptoList(
        @Query("key") key : String
    ) : CryptoList

    /** Retrofit'e keyi vereceğiz id yi vereceğiz neyi istediğimizi attributes'i vereceğiz ve bize
     *  istediklerimizi tek tek döndürecek bu detay sayfası için lazım. her iki @GET isteğinde de
     *  dikkat etmemiz gereken nokta @GET parantezleri içinde verdiğimiz ifadenin url deki yerinin doğru olması
     *  gerekli.
     * */

    @GET("currencies")
    suspend fun getCrypto(
        @Query("key") key : String,
        @Query("ids") id : String,
        @Query("attributes") attributest : String
    ) : Crypto

}