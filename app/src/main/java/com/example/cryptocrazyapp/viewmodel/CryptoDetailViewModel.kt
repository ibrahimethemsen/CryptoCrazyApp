package com.example.cryptocrazyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cryptocrazyapp.model.Crypto
import com.example.cryptocrazyapp.repository.CryptoRepository
import com.example.cryptocrazyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//detay viewModel'ı yazacağız

@HiltViewModel
class CryptoDetailViewModel @Inject constructor(
    private val repository: CryptoRepository
)  : ViewModel() {

    //bir crypto alacak fonksiyonumuz
    /** suspend fun iki şekilde çağırabiliyorduk burada ListViewModel'da uyguladığımız scope açma
     *  metodu yerine suspend yaparak suspend fun çağırdık
     * */

    suspend fun getCrypto(id : String) : Resource<Crypto>{
        return repository.getCrypto(id)
    }
}