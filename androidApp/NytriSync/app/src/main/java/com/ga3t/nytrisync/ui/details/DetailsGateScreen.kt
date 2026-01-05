package com.ga3t.nytrisync.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DetailsGateScreen(
    onExists: () -> Unit,
    onNotExists: () -> Unit
) {
    val vm: DetailsGateViewModel = viewModel(factory = DetailsGateViewModel.factory())
    val s = vm.state

    androidx.compose.runtime.LaunchedEffect(s.exists, s.error) {
        if (s.exists == true) onExists()
        if (s.exists == false) onNotExists()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (s.loading) {
            CircularProgressIndicator()
        } else if (s.error != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: ${s.error}", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Text("Retrying…")
            }
        }
    }
}