package com.jaylizapp.hexrootfuzz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jaylizapp.hexrootfuzz.ui.theme.HexRootFuzzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HexRootFuzzTheme {
                HexApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HexApp(viewModel: HexViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val tabs = listOf("Fuzzing", "Password", "Wordlist", "Logs")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.fuzz_icon),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("HEX-Style Demoníaco™", color = Color.Red, style = MaterialTheme.typography.headlineMedium) 
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Red)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.DarkGray)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Limpiar Consola", color = Color.White) },
                                onClick = {
                                    viewModel.clearLogs()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Instalar Herramientas", color = Color.White) },
                                onClick = {
                                    viewModel.installTools()
                                    showMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.Red
                )
            )
        },
        bottomBar = {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Black,
                contentColor = Color.Red,
                modifier = Modifier.navigationBarsPadding() // Soluciona el solapamiento con la barra del sistema
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color.Black)) {
            when (selectedTab) {
                0 -> FuzzingScreen(viewModel)
                1 -> PasswordScreen(viewModel)
                2 -> WordlistScreen(viewModel)
                3 -> LogsScreen(viewModel)
            }
        }
    }
}

@Composable
fun WordlistScreen(vm: HexViewModel) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(
            value = vm.wlBaseFile,
            onValueChange = { vm.wlBaseFile = it },
            label = { Text("Base Wordlist Path") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = vm.wlCase, onCheckedChange = { vm.wlCase = it })
            Text("Case variants", color = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(checked = vm.wlLeet, onCheckedChange = { vm.wlLeet = it })
            Text("Leet subs", color = Color.White)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = vm.wlNumbers, onCheckedChange = { vm.wlNumbers = it })
            Text("Append numbers (00-99)", color = Color.White)
        }
        OutlinedTextField(
            value = vm.wlPrefixes,
            onValueChange = { vm.wlPrefixes = it },
            label = { Text("Prefixes (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.wlSuffixes,
            onValueChange = { vm.wlSuffixes = it },
            label = { Text("Suffixes (comma separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { vm.generateWordlist() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228B22))
        ) {
            Text("GENERATE WORDLIST")
        }
    }
}



@Composable
fun FuzzingScreen(vm: HexViewModel) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(
            value = vm.fuzzTarget,
            onValueChange = { vm.fuzzTarget = it },
            label = { Text("Target URL (FUZZ)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.fuzzWordlist,
            onValueChange = { vm.fuzzWordlist = it },
            label = { Text("Wordlist Path") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.fuzzOptions,
            onValueChange = { vm.fuzzOptions = it },
            label = { Text("Options (e.g. -mc 200)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tool: ", color = Color.White)
            RadioButton(selected = vm.fuzzTool == "ffuf", onClick = { vm.fuzzTool = "ffuf" })
            Text("ffuf", color = Color.White)
            RadioButton(selected = vm.fuzzTool == "gobuster", onClick = { vm.fuzzTool = "gobuster" })
            Text("gobuster", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ControlButtons(vm, onRun = { vm.runFuzz(true) })
    }
}

@Composable
fun PasswordScreen(vm: HexViewModel) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(
            value = vm.passTarget,
            onValueChange = { vm.passTarget = it },
            label = { Text("Target Host") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.passService,
            onValueChange = { vm.passService = it },
            label = { Text("Service (e.g. ssh, ftp)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.passUserlist,
            onValueChange = { vm.passUserlist = it },
            label = { Text("Userlist Path") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.passPasslist,
            onValueChange = { vm.passPasslist = it },
            label = { Text("Passlist Path") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = vm.passAuth, onCheckedChange = { vm.passAuth = it })
            Text("I have authorization", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ControlButtons(vm, onRun = { vm.runPassAudit(true) })
    }
}

@Composable
fun LogsScreen(vm: HexViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { vm.clearLogs() }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                Text("Clear")
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFF111111)).padding(8.dp)) {
            items(vm.logs) { log ->
                Text(
                    text = log,
                    color = if (log.startsWith("[ERROR]")) Color.Red else if (log.startsWith("[INFO]")) Color.Cyan else Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ControlButtons(vm: HexViewModel, onRun: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(
            onClick = onRun,
            enabled = !vm.isRunning,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF228B22))
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("RUN (ROOT)")
        }
        Button(
            onClick = { vm.stop() },
            enabled = vm.isRunning,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Icon(Icons.Default.Stop, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("STOP")
        }
    }
}
