package cat.dam.andy.app.ui.screens

import cat.dam.andy.app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class TipusVehicle(val vmax: Int, val drawableRes: Int) {
    MOTO(3, R.drawable.moto),
    TURISME(4, R.drawable.turisme),
    FURGONETA(2, R.drawable.furgo),
    CAMIO(1, R.drawable.camio)
}

class Vehicle(val dorsal: Int) {
    val tipus: TipusVehicle = TipusVehicle.entries.random()
    val nom: String = "${tipus.name.lowercase()}$dorsal"
    var pos by mutableIntStateOf(0)
    var finished by mutableStateOf(false)

    val dorsalColor: Color = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan
    ).random()
}

@Composable
fun Layout1() {

    val vehicles = remember { mutableStateListOf<Vehicle>() }
    val ranking = remember { mutableStateListOf<Int>() }

    val finishLine = 260
    var running by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var inputNum by remember { mutableStateOf("5") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row {
            Button(onClick = { showDialog = true }) {
                Text("Crear")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                enabled = vehicles.isNotEmpty() && !running,
                onClick = { running = true }
            ) {
                Text("Run")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val h = (320f / (if (vehicles.isEmpty()) 1 else vehicles.size)).dp

        vehicles.forEach { vehicle ->

            LaunchedEffect(running) {
                while (running && !vehicle.finished) {
                    delay(300)

                    vehicle.pos += Random.nextInt(1, vehicle.tipus.vmax + 1)

                    if (vehicle.pos >= finishLine) {
                        vehicle.pos = finishLine
                        vehicle.finished = true
                        ranking.add(vehicle.dorsal)

                        if (ranking.size == vehicles.size) {
                            running = false
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(h)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .offset(x = vehicle.pos.dp)
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(vehicle.tipus.drawableRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(Color.Black)
                            .align(Alignment.CenterEnd)

                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("Classificació:")
        if (ranking.isEmpty()) Text("—")
        ranking.forEachIndexed { index, dorsal ->
            val posText = when (index) {
                0 -> "1r"
                1 -> "2n"
                2 -> "3r"
                else -> "${index + 1}è"
            }
            Text("$posText: Vehicle $dorsal")
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Crear vehicles") },
                text = {
                    OutlinedTextField(
                        value = inputNum,
                        onValueChange = {
                            inputNum = it.filter { c -> c.isDigit() }.take(2)
                            if (inputNum.isBlank()) inputNum = "1"
                        },
                        label = { Text("Quants? (1..20)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val n = (inputNum.toIntOrNull() ?: 5).coerceIn(1, 20)
                        vehicles.clear()
                        ranking.clear()
                        for (i in 1..n) vehicles.add(Vehicle(i))
                        running = false
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}