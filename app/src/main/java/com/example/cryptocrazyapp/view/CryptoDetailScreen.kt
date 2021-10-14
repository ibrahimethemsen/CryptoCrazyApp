package com.example.cryptocrazyapp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.cryptocrazyapp.model.Crypto
import com.example.cryptocrazyapp.util.Resource
import com.example.cryptocrazyapp.viewmodel.CryptoDetailViewModel
import kotlinx.coroutines.launch

/** detay sayfamıza id ve price listeden gelecek
 * */
//@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CryptoDetailScreen(
    id:String,
    price : String,
    navController: NavController,
    viewModel : CryptoDetailViewModel = hiltViewModel()
) {
    //side effects -> yan etkiler : uygulamamızın state'nde değişiklik yapan composable dışında olan şeylere deniyor
    /*
    //Step-1 / Wrong

    //rememberCoroutineScope -> daha çok kullanıcının kendisinin tetiklemesini istediğimiz durumlarda kullanmalıyız
    val scope = rememberCoroutineScope() //coroutine scope oluşturduk

    var cryptoItem by remember{
        mutableStateOf<Resource<Crypto>>(Resource.Loading())
    }

    /** launch burada
    "Calls to launch should happen inside a LaunchedEffect and not composition" hatası veriyor
     şimdilik bunu görmezden gelmek için class'ın üstüne @SuppressLint("CoroutineCreationDuringComposition")
     ekledik
     * */
    scope.launch {
        cryptoItem = viewModel.getCrypto(id)
        println(cryptoItem.data)
    }*/

    /*çözüm ve verimli yol 1->
    var cryptoItem by remember {
        mutableStateOf<Resource<Crypto>>(Resource.Loading())
    }

    //key -> fonksiyonun ne zaman çalışacağını belirtiyor
    LaunchedEffect(key1 = Unit){
        cryptoItem = viewModel.getCrypto(id)
        println(cryptoItem.data)
    }
    */
    //çözüm yolu 2
    val cryptoItem by produceState<Resource<Crypto>>(initialValue = Resource.Loading()){
        value = viewModel.getCrypto(id)
    }
    //by ile tanımlamaz da = yaparsak sonuna .value eklemeliyiz


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            /** Burada viewModel'da ki getCrypto fonksiyonumuzu çağırmamız gerekiyor ama onu suspend
             *  yaptığımız için bir coroutineScope'a ihtiyacımız var. bunun için ilk önce step1 i yapıcaz
             *  bu yanlış bir yöntem sonra da doğrusunu yapıcaz.
             * */

            when(cryptoItem){
                is Resource.Success -> {
                    //cryptoItem bir liste verdiği için data dedik ve bir eleman gelicek zaten buraya
                    //onun için index'e 0 verdik
                    val selectedCrypto = cryptoItem.data!![0]
                    Text(
                        text = selectedCrypto.name,
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.padding(2.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )

                    //logoyu indirmek için coil kütüphanesini kullanıcaz
                    Image(
                        painter = rememberImagePainter(data = selectedCrypto.logo_url),
                        contentDescription = selectedCrypto.name,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(200.dp, 200.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )


                    Text(
                        text = price,
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.padding(2.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Center
                    )
                }

                is Resource.Error -> {
                    Text(text = cryptoItem.message!!)
                }

                is Resource.Loading -> {
                    CircularProgressIndicator(color = MaterialTheme.colors.primary)
                }
            }
        }
    }
}