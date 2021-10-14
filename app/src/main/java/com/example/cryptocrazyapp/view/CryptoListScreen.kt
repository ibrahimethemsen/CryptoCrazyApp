package com.example.cryptocrazyapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptocrazyapp.model.CryptoListItem
import com.example.cryptocrazyapp.viewmodel.CryptoListViewModel


@Composable
fun CryptoListScreen(
    navController: NavController,
    viewModel: CryptoListViewModel = hiltViewModel() //böylece viewModel'ı kullanabilir hale getirdik
) {

    //arka plan rengini ve boyutunu yazmak için SurFace kullanıyoruz
    Surface(
        color = MaterialTheme.colors.secondary, //arka plan rengimiz
        modifier = Modifier.fillMaxSize() //tüm ekranı kaplayacak
    ) {
        //alt alta sırayla app ismi/search bar ve listemizi yazıcaz bunun için column kullanıyoruz
        Column {
            //uygulama ismimiz
            Text(
                text = "Crypto Crazy",
                //yatay olarak hepsini kaplayacak
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), //boşlugunu verdik
                textAlign = TextAlign.Center, //ortalayacak
                fontSize = 44.sp, //boyutu
                fontWeight = FontWeight.Bold, //kalınlığı
                color = MaterialTheme.colors.primary //renği
            )

            Spacer(modifier = Modifier.height(10.dp))
            //Search Bar
            SearchBar(
                hint = "Search ...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ){
                //onSearch kısmı burası burada viewModel'a gidip searchCryptoList'i açıyoruz
                //lambda olarak yazdığımız için direk {} açabildk
                viewModel.searchCryptoList(it)
            }

            Spacer(modifier = Modifier.height(10.dp))
            //listemiz
            CryptoList(navController = navController)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {} //arama olarak verildiğinde {} blogu içinde viewModel fun cağıracağız
) {
    //girilecek text
    var text by remember {
        mutableStateOf("")
    }

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    //burada text'i by dediğimiz için String olarak alabiliyoruz = deseydik .value dememiz lazımdı
    //onValueChange'ye değiştiğinde yapılacak olanı yazıyoruz
    //Box kullanmadan önce bozuk şekilde hint ve editText geliyor bunun için Box kullanıyoruz

    Box(
        modifier = modifier
    ) {
        BasicTextField(value = text, onValueChange = {
            text = it
            onSearch(it) //onSearch'u SearchBar'ı yerine koyup kullandığımızda yazacağız
        },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape) //gölgeleme
                .background(Color.White, CircleShape) //arka plan yuvarlaklık
                .padding(horizontal = 20.dp, vertical = 12.dp) //boşluklar
                .onFocusChanged {
                    /** searc'e tıklanıldığında hint'i göstermek için bir algoritma kurucaz bunun için
                     *  BasicTextField üstünde isHintDisplayed'i yazıyoruz. ilk olarak boş değilse durumu
                     *  yazdığımızdan tıklanılmış ve birşeyler yazılmış olarak başlatıyoruz
                     */
                    //tıklanılmamışsa yani it.isFocused != true ise ve
                    // text.isEmpty yani edit text boş ise hint'i göstericek
                    isHintDisplayed = it.isFocused != true && text.isEmpty()
                }
        )

        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

//ViewModelda yazdıgımız mutableStateları ve error/loading'i kullanmak için bir @Composable daha oluşturuyoruz
@Composable
fun CryptoList(navController: NavController,viewModel: CryptoListViewModel = hiltViewModel()) {
    //listemiz
    val cryptoList by remember { viewModel.cryptoList }

    //hata mesajı
    val errorMessage by remember { viewModel.errorMessage }

    //yüklenme
    val isLoading by remember { viewModel.isLoading }

    CryptoListView(cryptos = cryptoList, navController = navController)

    Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxSize()) {
        if (isLoading){
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if (errorMessage.isNotEmpty()){
            //retryView yazıcaz yani tekrar tıklanılabilen bir composable
            RetryView(error = errorMessage) {
                viewModel.loadCrypto()
            }
        }
    }
}

@Composable
fun RetryView(
    error : String,
    onRetry : () -> Unit
){
    Column {
        Text(text = error,color = Color.Red,fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            onRetry
        },modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Retry")
        }
    }
}


//listemizi yazıyoruz
@Composable
fun CryptoListView(cryptos : List<CryptoListItem>,navController: NavController) {
    LazyColumn(contentPadding = PaddingValues(5.dp)){
        //liste elemanlarımızı tek tek veriyoruz
        items(cryptos){crypto ->
            CryptoRow(navController = navController, crypto = crypto)
        }
    }
}


//listemizdeki bir row görünümünü yazdık
@Composable
fun CryptoRow(
    navController: NavController,
    crypto : CryptoListItem
){
    //iki text'imiz olucak price ve currency
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.secondary)
            .clickable {
                //tıklanma işlemi detaylara gidicek ve currency ve price'ı karşıya argüman olarak verecek
                navController.navigate("crypto_detail_screen/${crypto.currency}/${crypto.price}")
            }
    ) {
        Text(
            text = crypto.currency,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(2.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )

        Text(
            text = crypto.price,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(2.dp),
            color = MaterialTheme.colors.primaryVariant
        )
    }
}