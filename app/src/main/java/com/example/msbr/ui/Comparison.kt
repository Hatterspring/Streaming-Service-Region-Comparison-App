package com.example.msbr.ui

import android.app.Application
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
import com.example.msbr.ui.comparison.CompScreen
import com.example.msbr.ui.comparison.CompViewModel
import com.example.msbr.ui.comparison.DBViewModel
import com.example.msbr.ui.comparison.DBViewModelFactory
import com.example.msbr.ui.menu.MenuScreen
import com.example.msbr.ui.menu.MenuViewModel
import com.example.msbr.ui.settings.SettingsScreen
import com.example.msbr.ui.settings.SettingsViewModel

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
fun Comparison(modifier: Modifier = Modifier) {
    /****************************************************
    VARIABLES
     ****************************************************/
    //establish navigation and viewModels
    val navController: NavHostController = rememberNavController()
    val menuViewModel: MenuViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val compViewModel: CompViewModel = viewModel()
    val dbViewModel: DBViewModel = viewModel(factory = DBViewModelFactory())

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
                Text("Streaming Region Comparison")
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
                MenuScreen(navController, menuViewModel, compViewModel, modifier)
            }
            //navigate to settings
            composable(route=Screens.Settings.name) {
                SettingsScreen(settingsViewModel, modifier)
            }
            //navigate to comparison screen
            composable(route=Screens.Comp.name) {
                CompScreen(compViewModel, settingsViewModel, dbViewModel, modifier)
            }
        }
    }
}

