package com.example.bondmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bondmap.ui.BondDetailsScreen
import com.example.bondmap.ui.BondListScreen
import com.example.bondmap.ui.SearchScreen
import com.example.bondmap.ui.theme.BondMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BondMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "list"
                    ) {
                        composable("list") {
                            BondListScreen(
                                onOpenDetails = { id ->
                                    navController.navigate("details/$id")
                                },
                                onOpenSearch = {
                                    navController.navigate("search")
                                }
                            )
                        }
                        composable("search") {
                            SearchScreen(
                                onBack = { navController.popBackStack() },
                                onOpenDetails = { id ->
                                    navController.navigate("details/$id")
                                }
                            )
                        }
                        composable(
                            route = "details/{bondId}",
                            arguments = listOf(
                                navArgument("bondId") { type = NavType.LongType }
                            )
                        ) { entry ->
                            val bondId = entry.arguments?.getLong("bondId") ?: return@composable
                            BondDetailsScreen(
                                bondId = bondId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
