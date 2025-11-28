package com.mobile_dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
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
import kotlin.math.sqrt

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
                    ElectricalLoadCalculatorScreen()
                }
            }
        }
    }
}

data class EquipmentRow(
    val id: Int,
    val name: String,
    var erEfficiency: String = "",
    var loadPower: String = "",
    var loadVoltage: String = "",
    var erAmount: String = "",
    var erRatedPower: String = "",
    var utilizationRate: String = "",
    var reactivePower: String = ""
)

@Composable
fun ElectricalLoadCalculatorScreen() {
    var selectedEquipment by remember { mutableStateOf(0) }

    var rows by remember {
        mutableStateOf(
            mapOf(
                1 to EquipmentRow(1, "Шліфувальний верстат"),
                2 to EquipmentRow(2, "Свердлильний верстат"),
                3 to EquipmentRow(3, "Фугувальний верстат"),
                4 to EquipmentRow(4, "Циркулярна пила"),
                5 to EquipmentRow(5, "Прес"),
                6 to EquipmentRow(6, "Полірувальний верстат"),
                7 to EquipmentRow(7, "Фрезерний верстат"),
                8 to EquipmentRow(8, "Вентилятор")
            )
        )
    }

    val erPowerFactor = 1.16

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Веб калькулятор для розрахунку електричних навантажень об'єктів з використанням методу впорядкованих діаграм",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Equipment Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Найменування ЕП (електроприймача):",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                rows.values.forEach { row ->
                    EquipmentButton(
                        text = "[${row.id}] ${row.name}",
                        isSelected = selectedEquipment == row.id,
                        onClick = { selectedEquipment = row.id }
                    )
                }
            }
        }

        // Input Section
        if (selectedEquipment > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Параметри для [${selectedEquipment}] ${rows[selectedEquipment]?.name}:",
                        fontWeight = FontWeight.Bold
                    )

                    val currentRow = rows[selectedEquipment]
                    if (currentRow != null) {
                        InputField(
                            label = "η_н (ККД ЕП)",
                            value = currentRow.erEfficiency,
                            onValueChange = {
                                rows = rows.toMutableMap().apply {
                                    this[selectedEquipment] = currentRow.copy(erEfficiency = it)
                                }
                            }
                        )

                        InputField(
                            label = "cos φ (коефіцієнт потужності)",
                            value = currentRow.loadPower,
                            onValueChange = {
                                rows = rows.toMutableMap().apply {
                                    this[selectedEquipment] = currentRow.copy(loadPower = it)
                                }
                            }
                        )

                        InputField(
                            label = "U_н, кВ (напруга навантаження)",
                            value = currentRow.loadVoltage,
                            onValueChange = {
                                rows = rows.toMutableMap().apply {
                                    this[selectedEquipment] = currentRow.copy(loadVoltage = it)
                                }
                            }
                        )

                        InputField(
                            label = "n, шт (кількість ЕП)",
                            value = currentRow.erAmount,
                            onValueChange = {
                                rows = rows.toMutableMap().apply {
                                    this[selectedEquipment] = currentRow.copy(erAmount = it)
                                }
                            }
                        )

                        InputField(
                            label = "P_н, кВт (номінальна потужність)",
                            value = currentRow.erRatedPower,
                            onValueChange = {
                                rows = rows.toMutableMap().apply {
                                    this[selectedEquipment] = currentRow.copy(erRatedPower = it)
                                }
                            }
                        )

                        InputField(
                            label = "K_в (коефіцієнт використання)",
                            value = currentRow.utilizationRate,
                            onValueChange = {
                                rows = rows.toMutableMap().apply {
                                    this[selectedEquipment] = currentRow.copy(utilizationRate = it)
                                }
                            }
                        )

                        InputField(
                            label = "tg φ (коефіцієнт реактивної потужності)",
                            value = currentRow.reactivePower,
                            onValueChange = {
                                rows = rows.toMutableMap().apply {
                                    this[selectedEquipment] = currentRow.copy(reactivePower = it)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Results Table
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Розрахункова таблиця:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ResultsTable(rows, erPowerFactor)
            }
        }
    }
}

@Composable
fun EquipmentButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(text, textAlign = TextAlign.Start, fontSize = 13.sp)
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true
        )
    }
}

@Composable
fun ResultsTable(rows: Map<Int, EquipmentRow>, erPowerFactor: Double) {
    val calculations = remember(rows) {
        calculateTotals(rows, erPowerFactor)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        // Individual rows
        rows.values.forEach { row ->
            val product1 = calcProduct1(
                row.erAmount.toDoubleOrNull() ?: 0.0,
                row.erRatedPower.toDoubleOrNull() ?: 0.0
            )
            val product2 = calcProduct2(
                row.erAmount.toDoubleOrNull() ?: 0.0,
                row.erRatedPower.toDoubleOrNull() ?: 0.0,
                row.utilizationRate.toDoubleOrNull() ?: 0.0
            )
            val product3 = calcProduct3(
                row.erAmount.toDoubleOrNull() ?: 0.0,
                row.erRatedPower.toDoubleOrNull() ?: 0.0,
                row.utilizationRate.toDoubleOrNull() ?: 0.0,
                row.reactivePower.toDoubleOrNull() ?: 0.0
            )
            val product4 = calcProduct4(
                row.erAmount.toDoubleOrNull() ?: 0.0,
                row.erRatedPower.toDoubleOrNull() ?: 0.0
            )
            val groupCurrent = calcGroupCurrent(
                row.erAmount.toDoubleOrNull() ?: 0.0,
                row.erRatedPower.toDoubleOrNull() ?: 0.0,
                row.loadVoltage.toDoubleOrNull() ?: 1.0,
                row.loadPower.toDoubleOrNull() ?: 1.0,
                row.erEfficiency.toDoubleOrNull() ?: 1.0
            )

            EquipmentResultRow(
                name = "[${row.id}]",
                values = listOf(
                    row.erAmount,
                    row.erRatedPower,
                    String.format("%.2f", product1),
                    row.utilizationRate,
                    String.format("%.2f", product2),
                    String.format("%.2f", product3),
                    String.format("%.2f", product4),
                    String.format("%.2f", groupCurrent)
                )
            )
        }

        Divider(thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

        // Totals row
        TotalsResultRow(calculations)
    }
}

@Composable
fun EquipmentResultRow(name: String, values: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                modifier = Modifier.width(40.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            values.forEach { value ->
                Text(
                    text = value,
                    modifier = Modifier.width(60.dp),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TotalsResultRow(calculations: Calculations) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                "Загальні результати:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ResultItem("Загальна кількість ЕП:", String.format("%.0f", calculations.totalERAmount))
            ResultItem("Σ(n·Pн):", String.format("%.2f", calculations.totalProduct1) + " кВт")
            ResultItem("Коефіцієнт використання (Kв):", String.format("%.3f", calculations.totalUtilizationRate))
            ResultItem("Σ(n·Pн·Kв):", String.format("%.2f", calculations.totalProduct2) + " кВт")
            ResultItem("Σ(n·Pн·Kв·tgφ):", String.format("%.2f", calculations.totalProduct3) + " квар")
            ResultItem("Σ(n·Pн²):", String.format("%.2f", calculations.totalProduct4))
            ResultItem("Ефективна кількість ЕП (nе):", String.format("%.2f", calculations.erEffectiveQuantity))
            ResultItem("Розрахунковий коефіцієнт (Kр):", String.format("%.2f", calculations.erPowerFactor))
            ResultItem("Розрахункове активне навантаження (Pр):", String.format("%.2f", calculations.activeLoad) + " кВт")
            ResultItem("Розрахункове реактивне навантаження (Qр):", String.format("%.2f", calculations.reactiveLoad) + " квар")
            ResultItem("Повна потужність (Sр):", String.format("%.2f", calculations.fullPower) + " кВ·А")
            ResultItem("Розрахунковий груповий струм (Iр):", String.format("%.2f", calculations.erGroupCurrent) + " А")
        }
    }
}

@Composable
fun ResultItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

data class Calculations(
    val totalERAmount: Double,
    val totalProduct1: Double,
    val totalProduct2: Double,
    val totalProduct3: Double,
    val totalProduct4: Double,
    val totalUtilizationRate: Double,
    val erEffectiveQuantity: Double,
    val erPowerFactor: Double,
    val activeLoad: Double,
    val reactiveLoad: Double,
    val fullPower: Double,
    val erGroupCurrent: Double
)

fun calculateTotals(rows: Map<Int, EquipmentRow>, erPowerFactor: Double): Calculations {
    val totalERAmount = rows.values.sumOf { it.erAmount.toDoubleOrNull() ?: 0.0 }

    val totalProduct1 = rows.values.sumOf {
        calcProduct1(
            it.erAmount.toDoubleOrNull() ?: 0.0,
            it.erRatedPower.toDoubleOrNull() ?: 0.0
        )
    }

    val totalProduct2 = rows.values.sumOf {
        calcProduct2(
            it.erAmount.toDoubleOrNull() ?: 0.0,
            it.erRatedPower.toDoubleOrNull() ?: 0.0,
            it.utilizationRate.toDoubleOrNull() ?: 0.0
        )
    }

    val totalProduct3 = rows.values.sumOf {
        calcProduct3(
            it.erAmount.toDoubleOrNull() ?: 0.0,
            it.erRatedPower.toDoubleOrNull() ?: 0.0,
            it.utilizationRate.toDoubleOrNull() ?: 0.0,
            it.reactivePower.toDoubleOrNull() ?: 0.0
        )
    }

    val totalProduct4 = rows.values.sumOf {
        calcProduct4(
            it.erAmount.toDoubleOrNull() ?: 0.0,
            it.erRatedPower.toDoubleOrNull() ?: 0.0
        )
    }

    val totalUtilizationRate = if (totalProduct1 > 0) totalProduct2 / totalProduct1 else 0.0
    val erEffectiveQuantity = if (totalProduct4 > 0) totalProduct1.pow(2) / totalProduct4 else 0.0
    val activeLoad = erPowerFactor * totalProduct2
    val reactiveLoad = erPowerFactor * totalProduct3
    val fullPower = sqrt(activeLoad.pow(2) + reactiveLoad.pow(2))

    // Use first row's voltage for group current calculation
    val firstVoltage = rows[1]?.loadVoltage?.toDoubleOrNull() ?: 10.0
    val erGroupCurrent = if (firstVoltage > 0) activeLoad / firstVoltage else 0.0

    return Calculations(
        totalERAmount = totalERAmount,
        totalProduct1 = totalProduct1,
        totalProduct2 = totalProduct2,
        totalProduct3 = totalProduct3,
        totalProduct4 = totalProduct4,
        totalUtilizationRate = totalUtilizationRate,
        erEffectiveQuantity = erEffectiveQuantity,
        erPowerFactor = erPowerFactor,
        activeLoad = activeLoad,
        reactiveLoad = reactiveLoad,
        fullPower = fullPower,
        erGroupCurrent = erGroupCurrent
    )
}

// Calculation functions
fun calcProduct1(erAmount: Double, erRatedPower: Double): Double {
    return erAmount * erRatedPower
}

fun calcProduct2(erAmount: Double, erRatedPower: Double, utilizationRate: Double): Double {
    return calcProduct1(erAmount, erRatedPower) * utilizationRate
}

fun calcProduct3(erAmount: Double, erRatedPower: Double, utilizationRate: Double, reactivePower: Double): Double {
    return calcProduct2(erAmount, erRatedPower, utilizationRate) * reactivePower
}

fun calcProduct4(erAmount: Double, erRatedPower: Double): Double {
    return erAmount * erRatedPower.pow(2)
}

fun calcGroupCurrent(erAmount: Double, erRatedPower: Double, loadVoltage: Double, loadPower: Double, erEfficiency: Double): Double {
    if (loadVoltage == 0.0 || loadPower == 0.0 || erEfficiency == 0.0) return 0.0
    return (erAmount * erRatedPower) / (sqrt(3.0) * loadVoltage * loadPower * erEfficiency)
}
