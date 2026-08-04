package com.jaylizapp.hexrootfuzz

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jaylizapp.hexrootfuzz.ui.theme.*

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
    val isDarkMode = viewModel.isDarkMode

    val currentAccent = if (isDarkMode) HexAccent else LightAccent
    val currentBg = if (isDarkMode) HexBg else LightBg
    val currentPanel = if (isDarkMode) HexPanel else Color.White
    val currentText = if (isDarkMode) Color.White else Color.Black
    val currentAccentLow = if (isDarkMode) HexAccentLow else LightAccentLow

    val titleShadow = Shadow(
        color = Color.Black.copy(alpha = 0.5f),
        offset = Offset(4f, 4f),
        blurRadius = 8f
    )

    val hexTitle = buildAnnotatedString {
        val capsStyle = SpanStyle(
            color = currentAccent,
            fontWeight = FontWeight.Black,
            shadow = titleShadow,
            fontFamily = FontFamily.Monospace
        )
        val themeStyle = SpanStyle(
            color = currentText,
            fontWeight = FontWeight.Black,
            shadow = titleShadow,
            fontFamily = FontFamily.Monospace
        )

        withStyle(style = themeStyle) { append("😈 ") }
        withStyle(style = capsStyle) { append("HEX ") }
        withStyle(style = capsStyle) { append("ROOT ") }
        withStyle(style = themeStyle) { append("FUZZ") }
        withStyle(style = themeStyle) { append(" 😈") }
    }

    Scaffold(
        topBar = {
            Surface(
                color = currentBg,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = hexTitle, fontSize = 22.sp)
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = currentAccent)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(currentPanel)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Limpiar Consola", color = currentText, fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    viewModel.clearLogs()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Instalar Herramientas", color = currentText, fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    viewModel.installTools()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = currentBg,
                contentColor = currentAccent,
                modifier = Modifier.navigationBarsPadding(),
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontFamily = FontFamily.Monospace, 
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Visible
                            ) 
                        }
                    )
                }
            }
        },
        containerColor = currentBg
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().background(currentBg)) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> FuzzingScreen(viewModel)
                    1 -> PasswordScreen(viewModel)
                    2 -> WordlistScreen(viewModel)
                    3 -> LogsScreen(viewModel)
                }
            }
            ConsoleSection(viewModel)
        }
    }
}

@Composable
fun HexButton(
    text: String, 
    icon: ImageVector, 
    isError: Boolean = false, 
    accent: Color, 
    accentLow: Color, 
    panel: Color, 
    onClick: () -> Unit
) {
    val isDark = panel != Color.White
    val errorColor = if (isDark) Color.Red else Color(0xFFD32F2F)
    val errorBg = if (isDark) Color(0xFF330000) else Color(0xFFFFEBEE)

    Button(
        onClick = onClick,
        modifier = Modifier.height(42.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isError) errorBg else panel),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isError) errorColor else accentLow),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, 
                contentDescription = null, 
                modifier = Modifier.size(16.dp), 
                tint = if (isError) errorColor else if (isDark) Color.White else accent
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text, 
                color = if (isError) errorColor else if (isDark) Color.White else Color.Black, 
                fontSize = 11.sp, 
                fontWeight = FontWeight.Bold, 
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun HexInput(
    value: String, 
    onValueChange: (String) -> Unit, 
    label: String, 
    icon: ImageVector, 
    accent: Color,
    isDarkMode: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val panelColor = if (isDarkMode) HexPanel else Color(0xFFF0F2F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = accent.copy(0.6f), modifier = Modifier.size(18.dp)) },
        trailingIcon = trailingIcon,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = textColor, fontFamily = FontFamily.Monospace, fontSize = 14.sp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accent,
            unfocusedBorderColor = Color.DarkGray,
            cursorColor = accent,
            focusedLabelColor = accent,
            unfocusedLabelColor = Color.Gray,
            focusedContainerColor = panelColor,
            unfocusedContainerColor = panelColor
        )
    )
}

@Composable
fun WordlistSelector(currentPath: String, isDarkMode: Boolean, onSelect: (String) -> Unit) {
    val wordlists = listOf("common.txt", "directories.txt", "endpoints.txt", "passwords.txt")
    val baseDir = "/sdcard/HexRootFuzz/wordlists"
    val accent = if (isDarkMode) HexAccent else LightAccent
    val panel = if (isDarkMode) HexPanel else Color(0xFFE8EAF6)
    
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(wordlists) { name ->
            val path = "$baseDir/$name"
            val isSelected = currentPath == path
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(path) },
                label = { Text(name, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accent,
                    selectedLabelColor = if (isDarkMode) Color.Black else Color.White,
                    containerColor = panel,
                    labelColor = if (isDarkMode) HexWhite else Color.DarkGray
                ),
                border = BorderStroke(1.dp, if (isSelected) accent else Color.DarkGray)
            )
        }
    }
}

@Composable
fun ToolSelector(selectedTool: String, isDarkMode: Boolean, onToolSelected: (String) -> Unit) {
    val tools = listOf("ffuf", "gobuster")
    val accent = if (isDarkMode) HexAccent else LightAccent
    val panel = if (isDarkMode) HexPanel else Color(0xFFE8EAF6)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tools.forEach { tool ->
            val isSelected = selectedTool == tool
            FilterChip(
                selected = isSelected,
                onClick = { onToolSelected(tool) },
                label = { Text(tool, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accent,
                    selectedLabelColor = if (isDarkMode) Color.Black else Color.White,
                    containerColor = panel,
                    labelColor = if (isDarkMode) Color.White else Color.Black
                ),
                border = BorderStroke(1.dp, if (isSelected) accent else Color.DarkGray)
            )
        }
    }
}

@Composable
fun FuzzingScreen(vm: HexViewModel) {
    val isDarkMode = vm.isDarkMode
    val accent = if (isDarkMode) HexAccent else LightAccent
    val accentLow = if (isDarkMode) HexAccentLow else LightAccentLow
    val panel = if (isDarkMode) HexPanel else Color.White
    val textColor = if (isDarkMode) HexWhite else Color.Black

    Column(modifier = Modifier.padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
        HexInput(
            value = vm.fuzzTarget,
            onValueChange = { vm.fuzzTarget = it },
            label = "Target URL (FUZZ)",
            icon = Icons.Default.Language,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Quick Select Wordlist:", color = if (isDarkMode) HexText else Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        WordlistSelector(vm.fuzzWordlist, isDarkMode) { vm.fuzzWordlist = it }
        HexInput(
            value = vm.fuzzWordlist,
            onValueChange = { vm.fuzzWordlist = it },
            label = "Wordlist Path",
            icon = Icons.Default.Description,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(12.dp))
        HexInput(
            value = vm.fuzzOptions,
            onValueChange = { vm.fuzzOptions = it },
            label = "Options (e.g. -mc 200)",
            icon = Icons.Default.Build,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tool: ", color = textColor, fontFamily = FontFamily.Monospace)
                ToolSelector(vm.fuzzTool, isDarkMode) { vm.fuzzTool = it }
            }
            IconButton(onClick = { vm.isDarkMode = !vm.isDarkMode }) {
                Icon(
                    Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = accent
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ControlButtons(vm, onRun = { vm.runFuzz(true) })
    }
}

@Composable
fun PasswordScreen(vm: HexViewModel) {
    val isDarkMode = vm.isDarkMode
    val accent = if (isDarkMode) HexAccent else LightAccent
    val accentLow = if (isDarkMode) HexAccentLow else LightAccentLow
    val panel = if (isDarkMode) HexPanel else Color.White
    val textColor = if (isDarkMode) HexWhite else Color.Black

    Column(modifier = Modifier.padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
        HexInput(
            value = vm.passTarget,
            onValueChange = { vm.passTarget = it },
            label = "Target Host",
            icon = Icons.Default.Dns,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(12.dp))
        HexInput(
            value = vm.passService,
            onValueChange = { vm.passService = it },
            label = "Service (e.g. ssh, ftp)",
            icon = Icons.Default.SettingsInputComponent,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Quick Select Userlist:", color = if (isDarkMode) HexText else Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        WordlistSelector(vm.passUserlist, isDarkMode) { vm.passUserlist = it }
        HexInput(
            value = vm.passUserlist,
            onValueChange = { vm.passUserlist = it },
            label = "Userlist Path",
            icon = Icons.Default.Person,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("Quick Select Passlist:", color = if (isDarkMode) HexText else Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        WordlistSelector(vm.passPasslist, isDarkMode) { vm.passPasslist = it }
        HexInput(
            value = vm.passPasslist,
            onValueChange = { vm.passPasslist = it },
            label = "Passlist Path",
            icon = Icons.Default.Lock,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = vm.passAuth, onCheckedChange = { vm.passAuth = it }, colors = CheckboxDefaults.colors(checkedColor = accent))
            Text("I have authorization", color = textColor, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(20.dp))
        ControlButtons(vm, onRun = { vm.runPassAudit(true) })
    }
}

@Composable
fun WordlistScreen(vm: HexViewModel) {
    val isDarkMode = vm.isDarkMode
    val accent = if (isDarkMode) HexAccent else LightAccent
    val accentLow = if (isDarkMode) HexAccentLow else LightAccentLow
    val panel = if (isDarkMode) HexPanel else Color.White
    val textColor = if (isDarkMode) HexWhite else Color.Black

    Column(modifier = Modifier.padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Quick Select Base Wordlist:", color = if (isDarkMode) HexText else Color.Gray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        WordlistSelector(vm.wlBaseFile, isDarkMode) { vm.wlBaseFile = it }
        HexInput(
            value = vm.wlBaseFile,
            onValueChange = { vm.wlBaseFile = it },
            label = "Base Wordlist Path",
            icon = Icons.Default.Description,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = vm.wlCase, onCheckedChange = { vm.wlCase = it }, colors = CheckboxDefaults.colors(checkedColor = accent))
            Text("Case variants", color = textColor, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(checked = vm.wlLeet, onCheckedChange = { vm.wlLeet = it }, colors = CheckboxDefaults.colors(checkedColor = accent))
            Text("Leet subs", color = textColor, fontFamily = FontFamily.Monospace)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = vm.wlNumbers, onCheckedChange = { vm.wlNumbers = it }, colors = CheckboxDefaults.colors(checkedColor = accent))
            Text("Append numbers (00-99)", color = textColor, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(8.dp))
        HexInput(
            value = vm.wlPrefixes,
            onValueChange = { vm.wlPrefixes = it },
            label = "Prefixes (comma separated)",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(12.dp))
        HexInput(
            value = vm.wlSuffixes,
            onValueChange = { vm.wlSuffixes = it },
            label = "Suffixes (comma separated)",
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            accent = accent,
            isDarkMode = isDarkMode
        )
        Spacer(modifier = Modifier.height(20.dp))
        HexButton(
            text = "GENERATE WORDLIST",
            icon = Icons.Default.AutoFixHigh,
            accent = accent,
            accentLow = accentLow,
            panel = panel,
            onClick = { vm.generateWordlist() }
        )
    }
}

@Composable
fun LogsScreen(vm: HexViewModel) {
    val isDarkMode = vm.isDarkMode
    val bg = if (isDarkMode) HexBg else LightBg
    val panel = if (isDarkMode) HexPanel else Color.White
    val accent = if (isDarkMode) HexAccent else LightAccent
    val okColor = if (isDarkMode) HexOk else Color(0xFF2E7D32)

    Column(modifier = Modifier.fillMaxSize().background(bg)) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
            HexButton(
                text = "Clear",
                icon = Icons.Default.Delete,
                accent = Color.Gray,
                accentLow = Color.DarkGray,
                panel = panel,
                onClick = { vm.clearLogs() }
            )
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(vm.logs) { log ->
                Text(
                    text = log,
                    color = when {
                        log.startsWith("[ERROR]") -> accent
                        log.startsWith("[INFO]") -> if (isDarkMode) Color.Cyan else Color(0xFF0056D2)
                        log.startsWith("[SUCCESS]") -> okColor
                        else -> okColor.copy(alpha = 0.8f)
                    },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ConsoleSection(vm: HexViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDarkMode = vm.isDarkMode
    val terminalBg = if (isDarkMode) HexBg else Color(0xFF1E1E1E) // Terminal siempre oscuro o gris oscuro
    val currentAccent = if (isDarkMode) HexAccent else LightAccent
    val currentAccentLow = if (isDarkMode) HexAccentLow else LightAccentLow
    val currentOk = if (isDarkMode) HexOk else Color(0xFF4CAF50)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(terminalBg, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .border(1.dp, currentAccentLow, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .padding(1.dp)
            .clickable {
                val fullLog = vm.logs.joinToString("\n")
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("HexLogs", fullLog)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
            }
    ) {
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.Black.copy(0.2f), terminalBg))
        ))
        
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Text("CONSOLE OUTPUT (Click to Copy)", color = currentAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            HorizontalDivider(color = currentAccentLow, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
            
            SelectionContainer {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(vm.logs) { log ->
                        Text(
                            text = log,
                            color = when {
                                log.startsWith("!") -> currentAccent
                                log.startsWith("[ERROR]") -> currentAccent
                                log.startsWith("[#]") -> Color.Cyan
                                log.startsWith("[✔]") -> currentOk
                                log.startsWith("[INFO]") -> Color.Cyan
                                else -> currentOk.copy(alpha = 0.8f)
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ControlButtons(vm: HexViewModel, onRun: () -> Unit) {
    val isDarkMode = vm.isDarkMode
    val accent = if (isDarkMode) HexAccent else LightAccent
    val accentLow = if (isDarkMode) HexAccentLow else LightAccentLow
    val panel = if (isDarkMode) HexPanel else Color.White

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            HexButton(
                text = "RUN (ROOT)",
                icon = Icons.Default.PlayArrow,
                isError = false,
                accent = accent,
                accentLow = accentLow,
                panel = panel,
                onClick = onRun
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            HexButton(
                text = "STOP",
                icon = Icons.Default.Stop,
                isError = true,
                accent = accent,
                accentLow = accentLow,
                panel = panel,
                onClick = { vm.stop() }
            )
        }
    }
}
