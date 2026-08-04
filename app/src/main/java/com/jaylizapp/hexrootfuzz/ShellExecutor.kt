package com.jaylizapp.hexrootfuzz

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class ShellExecutor {
    private val _output = MutableSharedFlow<String>(extraBufferCapacity = 100)
    val output: SharedFlow<String> = _output

    private var process: Process? = null

    suspend fun executeCommand(command: String, useRoot: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            val termuxBin = "/data/data/com.termux/files/usr/bin"
            val commonPaths = "$termuxBin:/system/bin:/system/xbin:/vendor/bin:/sbin"
            
            val pb = if (useRoot) {
                ProcessBuilder("su", "-c", "export PATH=\$PATH:$commonPaths && $command")
            } else {
                ProcessBuilder("sh", "-c", "export PATH=\$PATH:$commonPaths && $command")
            }

            // Merge PATH to ensure tools are found
            val env = pb.environment()
            val currentPath = env["PATH"] ?: ""
            env["PATH"] = "$currentPath:$commonPaths"

            process = pb.start()

            val reader = BufferedReader(InputStreamReader(process?.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process?.errorStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                _output.emit(line ?: "")
            }
            while (errorReader.readLine().also { line = it } != null) {
                _output.emit("[ERROR] $line")
            }

            process?.waitFor()
        } catch (e: Exception) {
            _output.emit("[EXCEPTION] ${e.message}")
        } finally {
            process = null
        }
    }

    fun stop() {
        process?.destroy()
        process = null
    }
}
