package com.lboro.msbr.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.lboro.msbr.ui.comparison.CompScreen
import com.lboro.msbr.ui.comparison.CompViewModel
import com.lboro.msbr.ui.menu.MenuScreen
import com.lboro.msbr.ui.menu.MenuViewModel
import com.lboro.msbr.ui.settings.SettingsScreen

/****************************************************
 DATA
 ****************************************************/
//Screens enum - used for navigation.
    enum class Screens {
        Menu,
        Settings,
        Comp
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comparison(locationClient: FusedLocationProviderClient, modifier: Modifier = Modifier) {
    /****************************************************
    VARIABLES
     ****************************************************/
    //establish navigation and viewModels
    val navController: NavHostController = rememberNavController()
    val menuViewModel: MenuViewModel = viewModel()
    val dataViewModel: DataViewModel = viewModel(factory = DataViewModelFactory())
    val compViewModel: CompViewModel = viewModel()

    /****************************************************
    STRUCTURE
     ****************************************************/
    Scaffold (topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text("Movies By Region")
            },
            actions = {
                //top app bar home button
                IconButton(onClick = {navController.navigate(route=Screens.Menu.name)}) {
                    Icon(Icons.Filled.Home, contentDescription = "Return to main menu")
                }
                //top app bar settings button
                IconButton(onClick = {navController.navigate(route=Screens.Settings.name)}) {
                    Icon(Icons.Filled.Settings, contentDescription = "Change App Settings")
                }
            }
        )
    }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Menu.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            //navigate to menu
            composable(route=Screens.Menu.name) {
                MenuScreen(navController, menuViewModel, compViewModel, dataViewModel, modifier)
            }
            //navigate to settings
            composable(route=Screens.Settings.name) {
                SettingsScreen(dataViewModel, locationClient, modifier)
            }
            //navigate to comparison screen
            composable(route=Screens.Comp.name) {
                CompScreen(compViewModel, navController, dataViewModel, modifier)
            }
        }
    }
}

