package com.example.streamingserviceregioncomparisonapp.ui

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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comparison(name: String, modifier: Modifier = Modifier) {
    val navController: NavHostController = rememberNavController()
    val menuViewModel: MenuViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val compViewModel: CompViewModel = viewModel()

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
                IconButton(onClick = {navController.navigate(route=Screens.Menu.name)}) {
                    Icon(Icons.Filled.Home, contentDescription = "Return to main menu")
                }
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
            composable(route=Screens.Menu.name) {
                MenuScreen(navController, menuViewModel, modifier)
            }
            composable(route=Screens.Settings.name) {
                SettingsScreen(navController, modifier)
            }
            composable(route=Screens.Comp.name) {
                CompScreen(navController, menuViewModel.uiState.collectAsState().value, modifier)
            }
        }
    }
}

enum class Screens {
    Menu,
    Settings,
    Comp
}