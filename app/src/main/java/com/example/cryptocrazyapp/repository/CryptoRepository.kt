package com.example.cryptocrazyapp.repository

import com.example.cryptocrazyapp.model.Crypto
import com.example.cryptocrazyapp.model.CryptoList
import com.example.cryptocrazyapp.service.CryptoAPI
import com.example.cryptocrazyapp.util.Constants.API_KEY
import com.example.cryptocrazyapp.util.Constants.CALL_ATTRIBUTES
import com.example.cryptocrazyapp.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.Exception
import javax.inject.Inject

//kapması tüm aktivite boyunca olucak
@ActivityScoped
class CryptoRepository @Inject constructor(
    private val api : CryptoAPI
){

    //tüm listemizi alıyoruz
    suspend fun getCryptoList() : Resource<CryptoList>{
        val response = try {
            api.getCryptoList(API_KEY) //istegi attık
        }catch (e : Exception){
            return Resource.Error("Error.") //istek hata verdi
        }
        return Resource.Success(response) //-> başarılı şekilde alırsak veriyi gönderiyoruz
    }


    suspend fun getCrypto(id : String):Resource<Crypto>{
        val response  = try {
            api.getCrypto(API_KEY,id, CALL_ATTRIBUTES) //istegi attık
        }catch (e : Exception){
            return Resource.Error("Error.") //hata çıktı
        }
        return Resource.Success(response) //başarılı şekilde veriyi aldık ve atadık
    }
}