package com.printcounter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.printcounter.domain.model.PrintFormat
import com.printcounter.domain.model.EmbroideryFormat
import com.printcounter.ui.components.CounterCard
import com.printcounter.ui.components.FormatButton

@Composable
fun CounterScreen(
    onAddPrint: (Int, String) -> Unit,
    onAddEmbroidery: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var printQuantity by remember { mutableIntStateOf(1) }
    var embroideryQuantity by remember { mutableIntStateOf(1) }
    var selectedPrintFormat by remember { mutableStateOf(PrintFormat.A4.name) }
    var selectedEmbroideryFormat by remember { mutableStateOf(EmbroideryFormat.A4.name) }
    
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ПЕЧАТЬ SECTION
        Text(
            text = "Печать",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Text(
            text = "Формат:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(PrintFormat.values().size) { index ->
                val format = PrintFormat.values()[index]
                FormatButton(
                    format = format.name,
                    isSelected = selectedPrintFormat == format.name,
                    onClick = { selectedPrintFormat = format.name }
                )
            }
        }
        
        CounterCard(
            title = "Печать - $selectedPrintFormat",
            quantity = printQuantity,
            onIncrement = { printQuantity++ },
            onDecrement = { if (printQuantity > 1) printQuantity-- }
        )
        
        Button(
            onClick = {
                onAddPrint(printQuantity, selectedPrintFormat)
                printQuantity = 1
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Добавить печать")
        }
        
        Divider(
            modifier = Modifier.padding(vertical = 20.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        
        // ВЫШИВКА SECTION
        Text(
            text = "Вышивка",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Text(
            text = "Формат:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(EmbroideryFormat.values().size) { index ->
                val format = EmbroideryFormat.values()[index]
                FormatButton(
                    format = format.name,
                    isSelected = selectedEmbroideryFormat == format.name,
                    onClick = { selectedEmbroideryFormat = format.name }
                )
            }
        }
        
        CounterCard(
            title = "Вышивка - $selectedEmbroideryFormat",
            quantity = embroideryQuantity,
            onIncrement = { embroideryQuantity++ },
            onDecrement = { if (embroideryQuantity > 1) embroideryQuantity-- }
        )
        
        Button(
            onClick = {
                onAddEmbroidery(embroideryQuantity, selectedEmbroideryFormat)
                embroideryQuantity = 1
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Добавить вышивку")
        }
    }
}
