package com.example.cryptocrazyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.cryptocrazyapp.ui.theme.CryptoCrazyAppTheme
import com.example.cryptocrazyapp.view.CryptoDetailScreen
import com.example.cryptocrazyapp.view.CryptoListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoCrazyAppTheme {
                //navigasyoncontroller
                //remember yapmamızın sebebi değişiklik olduğunda hatırlaması recomposition olayıydı
                val navController = rememberNavController()

                NavHost(navController = navController,startDestination = "crypto_list_screen"){

                    //ekranlarımızı composable içinde yazacağız
                    //bu composable bizim liste sayfamız
                    composable("crypto_list_screen"){
                        //buraya crypto paralarımızın listesi olan ekranımız gelecek
                        //route id miz
                        CryptoListScreen(navController = navController)
                    }


                    /** buraya 2 argüman vereceğiz crypto ıd ve fiyatını
                     * bunun için argümanlarımızı da oluşturacağız
                     * burada argümanlarımızı sıralı olarak route içinde de yazmalıyız
                     * bu composable bizim detay sayfamız
                     * */
                    composable("crypto_detail_screen/{cryptoId}/{cryptoPrice}",arguments = listOf(
                        /** argümanlarımız liste halinde geliyor argümanımızın adını name kısmına yazıyoruz
                         *  argümanımızın type'nı ise içerisine yazıyoruz
                         * */
                        navArgument("cryptoId"){
                            type = NavType.StringType
                        },
                        navArgument("cryptoPrice"){
                            type = NavType.StringType
                        }
                    )){
                        /** argümanlarımızı aldık şimdi bunları değişkenimizie atayıp kullanabiliriz
                         *  bu ekran detay ekranımız argümanlarımızı detay kısımda kullanıyoruz
                         * */

                        val cryptoId = remember{
                            //key bizim argümanımızın name kısmı
                            it.arguments?.getString("cryptoId")
                        }

                        val cryptoPrice = remember{
                            it.arguments?.getString("cryptoPrice")
                        }

                        //elvis operatoru (?:) kullanmamızın sebebebi null döndürebilir diyor onun için kullandık
                        CryptoDetailScreen(
                            id = cryptoId ?: "",
                            price = cryptoPrice ?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
