package com.jaylizapp.hexrootfuzz

import android.app.Application
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HexViewModel(application: Application) : AndroidViewModel(application) {
    private val shellExecutor = ShellExecutor()
    private val wordlistGenerator = WordlistGenerator()

    companion object {
        private val staticLogs = mutableStateListOf<String>()
        private var isExecutorInitialized = false
    }

    private val baseDir = File(Environment.getExternalStorageDirectory(), "HexRootFuzz").apply {
        if (!exists()) mkdirs()
    }
    
    private val wordlistsDir = File(baseDir, "wordlists").apply {
        if (!exists()) mkdirs()
    }

    val logs: MutableList<String> get() = staticLogs

    var isDarkMode by mutableStateOf(true)

    var fuzzTarget by mutableStateOf("http://example.com/FUZZ")
    var fuzzWordlist by mutableStateOf("${wordlistsDir.absolutePath}/common.txt")
    var fuzzOptions by mutableStateOf("-mc 200")
    var fuzzTool by mutableStateOf("ffuf")

    var passTarget by mutableStateOf("192.168.1.100")
    var passService by mutableStateOf("ssh")
    var passUserlist by mutableStateOf("${wordlistsDir.absolutePath}/common.txt")
    var passPasslist by mutableStateOf("${wordlistsDir.absolutePath}/passwords.txt")
    var passAuth by mutableStateOf(false)

    var isRunning by mutableStateOf(false)
        private set

    // Wordlist PRO States
    var wlBaseFile by mutableStateOf("${wordlistsDir.absolutePath}/common.txt")
    var wlCase by mutableStateOf(true)
    var wlLeet by mutableStateOf(false)
    var wlPrefixes by mutableStateOf("admin,root,sys")
    var wlSuffixes by mutableStateOf("2025,123")
    var wlNumbers by mutableStateOf(false)

    fun generateWordlist() {
        viewModelScope.launch {
            isRunning = true
            logs.add("[INFO] Starting Wordlist Generation...")
            
            try {
                val inputFile = File(wlBaseFile)
                if (!inputFile.exists()) {
                    logs.add("[ERROR] Input file does not exist: $wlBaseFile")
                    isRunning = false
                    return@launch
                }

                val outputFile = File(baseDir, "generated_${System.currentTimeMillis()}.txt")
                logs.add("[INFO] Output will be saved to: ${outputFile.absolutePath}")

                val prefixes = wlPrefixes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val suffixes = wlSuffixes.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                var totalGenerated = 0
                outputFile.bufferedWriter().use { writer ->
                    inputFile.bufferedReader().forEachLine { line ->
                        val word = line.trim()
                        if (word.isNotEmpty()) {
                            val variants = wordlistGenerator.transformWord(
                                word = word,
                                case = wlCase,
                                leet = wlLeet,
                                prefixes = prefixes,
                                suffixes = suffixes,
                                numbers = wlNumbers
                            )
                            variants.forEach {
                                writer.write(it)
                                writer.newLine()
                                totalGenerated++
                            }
                        }
                    }
                }
                logs.add("[INFO] Generation complete. Total variants: $totalGenerated")
            } catch (e: Exception) {
                logs.add("[ERROR] ${e.message}")
            } finally {
                isRunning = false
            }
        }
    }


    init {
        if (!isExecutorInitialized) {
            isExecutorInitialized = true
            logs.add("[INFO] Hex Suite initialized.")
            logs.add("[INFO] Output folder: ${baseDir.absolutePath}")
            logs.add("[INFO] Requesting root access...")
            
            viewModelScope.launch(Dispatchers.IO) {
                copyAssetsToStorage()
                
                // Trigger root request on start
                shellExecutor.executeCommand("id", true)
                
                shellExecutor.output.collect { line ->
                    logs.add(line)
                    if (logs.size > 1000) logs.removeAt(0)
                }
            }
        }
    }

    private fun copyAssetsToStorage() {
        try {
            val assets = getApplication<Application>().assets
            val files = assets.list("wordlists") ?: return
            files.forEach { fileName ->
                val outFile = File(wordlistsDir, fileName)
                if (!outFile.exists()) {
                    assets.open("wordlists/$fileName").use { input ->
                        outFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    logs.add("[INFO] Wordlist copied: $fileName")
                }
            }
        } catch (e: Exception) {
            logs.add("[ERROR] Failed to copy assets: ${e.message}")
        }
    }

    fun runFuzz(useRoot: Boolean) {
        if (isRunning) return
        
        val termuxGoBin = "/data/data/com.termux/files/home/go/bin"
        val fullCmd = when (fuzzTool) {
            "ffuf" -> "$termuxGoBin/ffuf -w $fuzzWordlist -u $fuzzTarget $fuzzOptions"
            "gobuster" -> "gobuster dir -w $fuzzWordlist -u $fuzzTarget $fuzzOptions"
            else -> ""
        }

        if (fullCmd.isNotEmpty()) {
            execute(fullCmd, useRoot)
        }
    }

    fun runPassAudit(useRoot: Boolean) {
        if (isRunning || !passAuth) return
        val cmd = "hydra -L $passUserlist -P $passPasslist $passTarget $passService"
        execute(cmd, useRoot)
    }

    private fun execute(cmd: String, useRoot: Boolean) {
        viewModelScope.launch {
            isRunning = true
            logs.add("[INFO] Starting: $cmd")
            shellExecutor.executeCommand(cmd, useRoot)
            isRunning = false
            logs.add("[INFO] Finished execution")
        }
    }

    fun stop() {
        shellExecutor.stop()
        isRunning = false
        logs.add("[INFO] Stopped by user")
    }

    fun clearLogs() {
        logs.clear()
    }

    fun installTools() {
        val cmd = "pkg update && pkg upgrade -y && pkg install ffuf gobuster hydra -y"
        execute(cmd, false) // Usually termux tools don't need root to install via pkg
    }
}
