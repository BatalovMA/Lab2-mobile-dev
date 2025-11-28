package com.mobile_dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

// Dark theme colors matching globals.css
private val DarkBackground = Color(0xFF212121)
private val LightForeground = Color(0xFFFFFFFF)
private val PrimaryColor = Color(0xFF90CAF9)
private val SecondaryColor = Color(0xFFCE93D8)
private val BorderColor = Color(0xFFFFFFFF)

private val AppColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.Black,
    primaryContainer = PrimaryColor.copy(alpha = 0.3f),
    onPrimaryContainer = LightForeground,
    secondary = SecondaryColor,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryColor.copy(alpha = 0.3f),
    onSecondaryContainer = LightForeground,
    background = DarkBackground,
    onBackground = LightForeground,
    surface = DarkBackground,
    onSurface = LightForeground,
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = LightForeground,
    outline = BorderColor
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EmissionCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun EmissionCalculatorScreen() {
    var coalAmountVal by remember { mutableStateOf("") }
    var oilAmountVal by remember { mutableStateOf("") }
    var gasAmountVal by remember { mutableStateOf("") }

    val coalAmount = coalAmountVal.toDoubleOrNull() ?: 0.0
    val oilAmount = oilAmountVal.toDoubleOrNull() ?: 0.0
    val gasAmount = gasAmountVal.toDoubleOrNull() ?: 0.0

    // Constants
    val filterEfficiency = 0.985

    // Coal constants
    val coalHoC = 20.47  // Heat of combustion
    val coalFlyAsh = 0.8
    val coalAshAmount = 25.20
    val coalAshCombustibles = 1.5

    // Oil constants
    val oilHoC = 40.40
    val oilFlyAsh = 1.0
    val oilAshAmount = 0.15
    val oilAshCombustibles = 0.0

    // Natural gas constants
    val gasHoC = 33.08
    val gasFlyAsh = 0.0
    val gasAshAmount = 0.0
    val gasAshCombustibles = 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Веб калькулятор для розрахунку валових викидів шкідливих речовин у вигляді суспендованих твердих частинок при спалювання вугілля, мазуту та природного газу",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Input Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Вхідні дані:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                InputField(
                    label = "Донецьке газове вугілля марки ГР (т):",
                    value = coalAmountVal,
                    onValueChange = { coalAmountVal = it }
                )

                InputField(
                    label = "Високосірчистий мазут марки 40 (т):",
                    value = oilAmountVal,
                    onValueChange = { oilAmountVal = it }
                )

                InputField(
                    label = "Природній газ із газопроводу Уренгой-Ужгород (м³):",
                    value = gasAmountVal,
                    onValueChange = { gasAmountVal = it }
                )
            }
        }

        // Constants Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Константи:",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Ступінь очищення золовловлювачем: ${filterEfficiency * 100}%")

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Вугілля:", fontWeight = FontWeight.Bold)
                Text("  • Теплота згоряння: $coalHoC МДж/кг")
                Text("  • Частка золи винесення: $coalFlyAsh")
                Text("  • Зольність палива: $coalAshAmount%")
                Text("  • Вміст горючих у золі: $coalAshCombustibles%")

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Мазут:", fontWeight = FontWeight.Bold)
                Text("  • Теплота згоряння: $oilHoC МДж/кг")
                Text("  • Частка золи винесення: $oilFlyAsh")
                Text("  • Зольність палива: $oilAshAmount%")
                Text("  • Вміст горючих у золі: $oilAshCombustibles%")

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Природний газ:", fontWeight = FontWeight.Bold)
                Text("  • Теплота згоряння: $gasHoC МДж/м³")
                Text("  • Зольність: $gasAshAmount (відсутня)")
            }
        }

        // Results Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Результати розрахунків:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                val coalEmission = calcEmissionRate(
                    coalHoC, coalFlyAsh, coalAshAmount,
                    coalAshCombustibles, filterEfficiency, coalAmount
                )

                ResultCard(
                    title = "Валовий викид при спалюванні вугілля",
                    value = String.format("%.2f", coalEmission),
                    unit = "т",
                    description = "Розраховано на основі характеристик Донецького газового вугілля марки ГР"
                )

                val oilEmission = calcEmissionRate(
                    oilHoC, oilFlyAsh, oilAshAmount,
                    oilAshCombustibles, filterEfficiency, oilAmount
                )

                ResultCard(
                    title = "Валовий викид при спалюванні мазуту",
                    value = String.format("%.2f", oilEmission),
                    unit = "т",
                    description = "Розраховано на основі характеристик високосірчистого мазуту марки 40"
                )

                val gasEmission = calcEmissionRate(
                    gasHoC, gasFlyAsh, gasAshAmount,
                    gasAshCombustibles, filterEfficiency, gasAmount
                )

                ResultCard(
                    title = "Валовий викид при спалюванні природного газу",
                    value = String.format("%.2f", gasEmission),
                    unit = "т",
                    description = "Розраховано для газу з газопроводу Уренгой-Ужгород"
                )

                val totalEmission = coalEmission + oilEmission + gasEmission

                Divider()

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Загальний валовий викид:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${String.format("%.2f", totalEmission)} т",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("0") }
        )
    }
}

@Composable
fun ResultCard(title: String, value: String, unit: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = unit,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Calculation functions
fun calcGrossEmission(
    itemHoC: Double,
    itemFlyAsh: Double,
    itemAshAmount: Double,
    itemAshCombustibles: Double,
    itemFilterEfficiency: Double
): Double {
    return (10.0.pow(6) / itemHoC) * itemFlyAsh *
           (itemAshAmount / (100.0 - itemAshCombustibles)) *
           (1.0 - itemFilterEfficiency)
}

fun calcEmissionRate(
    itemHoC: Double,
    itemFlyAsh: Double,
    itemAshAmount: Double,
    itemAshCombustibles: Double,
    itemFilterEfficiency: Double,
    itemAmount: Double
): Double {
    return 10.0.pow(-6) *
           calcGrossEmission(itemHoC, itemFlyAsh, itemAshAmount, itemAshCombustibles, itemFilterEfficiency) *
           itemHoC *
           itemAmount
}
