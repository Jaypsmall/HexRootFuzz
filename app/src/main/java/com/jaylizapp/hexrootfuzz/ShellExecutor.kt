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
            val termuxLib = "/data/data/com.termux/files/usr/lib"
            val termuxGoBin = "/data/data/com.termux/files/home/go/bin"
            val termuxHome = "/data/data/com.termux/files/home"
            val commonPaths = "$termuxGoBin:$termuxBin:/system/bin:/system/xbin:/vendor/bin:/sbin"
            
            val envCmd = "export HOME=$termuxHome && export PATH=$commonPaths:\$PATH && export LD_LIBRARY_PATH=$termuxLib"
            
            val pb = if (useRoot) {
                ProcessBuilder("su", "-c", "$envCmd && $command")
            } else {
                ProcessBuilder("sh", "-c", "$envCmd && $command")
            }

            // Merge environment variables
            val env = pb.environment()
            env["HOME"] = termuxHome
            val currentPath = env["PATH"] ?: ""
            env["PATH"] = "$commonPaths:$currentPath"
            env["LD_LIBRARY_PATH"] = termuxLib

            process = pb.start()

            val reader = BufferedReader(InputStreamReader(process?.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process?.errorStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                _output.emit(line ?: "")
            }
            while (errorReader.readLine().also { line = it } != null) {
                // Check if it's a real error or just stderr output (like tools often do)
                _output.emit(line ?: "")
            }

            val exitCode = process?.waitFor() ?: -1
            if (exitCode == 0) {
                _output.emit("[SUCCESS] Finished successfully")
            } else {
                _output.emit("[ERROR] Exit code: $exitCode")
            }
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
