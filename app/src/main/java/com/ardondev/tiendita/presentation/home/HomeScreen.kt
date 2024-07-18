package com.ardondev.tiendita.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ardondev.tiendita.R

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        items = listOf("1", "2", "3")
    )
}

@Composable
fun HomeScreen(
    items: List<String>,
) {
    Scaffold(
        floatingActionButton = { MainFAB() }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            items(items) { i ->
                ProductItem()
            }
        }
    }
}

@Composable
fun ProductItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            //Product icon
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Product icon",
                modifier = Modifier
                    .size(40.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                //Product name
                Text(text = "Product name")
                //Product stock
                Text(text = "Stock: 50")
            }

        }
    }
}

@Composable
fun MainFAB() {
    FloatingActionButton(
        onClick = {

        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add product"
        )
    }
}