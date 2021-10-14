package com.example.cryptocrazyapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptocrazyapp.model.CryptoListItem
import com.example.cryptocrazyapp.repository.CryptoRepository
import com.example.cryptocrazyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

//normalde ViewModelInject yapmamız lazımdı ama HiltViewModel dedigimizden @Inject'de yapabiliyoruz
@HiltViewModel
class CryptoListViewModel @Inject constructor(
    private val repository : CryptoRepository
) :ViewModel() {
    //burada livedata'yı kullanmamıza gerek yok composeda mutablestate'larımız var onları kullanabiliriz

    //mutableState'lerimizi oluşturuyoruz

    //burada Type veriyoruz ne alacağını biliyor ve şimdilik boş bir liste veriyoruz
    var cryptoList = mutableStateOf<List<CryptoListItem>>(listOf())

    //hata mesajımızı takip edecek mutableState
    var errorMessage = mutableStateOf("")

    // yüklenip yüklenmediğini takip edecek mutableState
    var isLoading = mutableStateOf(false)

    /** Arama işlemini gelen liste üzerinde yaptığımızdan sürekli olarak arama yapıp listeyi
     *  tekrar internetten çekmemek için aldığımız listeyi bir diger listemize-initialCryptoList-
     *  atayacağız sürekli olarak internetten veriyi çekmek yerine ilk aldığımız liste üzerinde
     *  aramayı yaparken arama bittiğinde ilk atadığımız listeden geriye verimizi alacağız
     * */
    private var initialCryptoList = listOf<CryptoListItem>()

    //arama başladı mı onu kontrol ediyor
    private var isSearchStarting = true

    //direk internetten verimizi çekiyor
    init {
        loadCrypto()
    }

    fun searchCryptoList(query : String){
        val listToSearch = if (isSearchStarting){
            cryptoList.value
        }else{
            initialCryptoList
        }

        /** arama işlemleri çok fazla cpu gerektirdiği için thread'ini belirtiyoruz
         * */
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()){
                cryptoList.value = initialCryptoList
                isSearchStarting = true
                return@launch
            }

            val results = listToSearch.filter {
                /** contains -> barındırma anlamına geliyor içinde aradığımız olan ne varsa getiriyor
                 *  trim -> boşlukları alıyor
                 *  ignoreCase -> büyük kücük duyarlılığı true dersek duyarlı olmuyor
                 * */
                it.currency.contains(query.trim(),ignoreCase = true)
            }

            if (isSearchStarting){
                initialCryptoList = cryptoList.value
                isSearchStarting = false
            }

            cryptoList.value = results
        }
    }



    /** suspend fun ları burada cagırırken bizden bir CoroutineScope içinde çağırmamız gerektiğini
     *  söylüyor. Bunu aşmanın iki yolu var. 1-> fun'ı suspend yapmak suspend fun loadCrypto gibi
     *  2-> viewModelScope.launch{} yapmak. iki adımı kullanmanın da kendine göre bir bedeli var
     * */
    fun loadCrypto(){
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getCryptoList()
            when(result){
                is Resource.Success -> {
                    // mapIndexed ile liste halinde olan veriyi tek tek alıp kullanabiliyoruz.
                    val cryptoItems = result.data!!.mapIndexed { index, cryptoListItem ->
                        CryptoListItem(cryptoListItem.currency,cryptoListItem.price)
                    } as List<CryptoListItem> //normalde as List<CryptoListItem>'a gerek yok ama hata ile karşılaşırsak bir sıkıntı olursa aklımızda bulunması için koyduk

                    errorMessage.value = ""
                    isLoading.value = false
                    cryptoList.value += cryptoItems
                }

                //hata verirse
                is Resource.Error -> {
                    errorMessage.value = result.message!!
                    isLoading.value = false
                }
            }
        }
    }

}