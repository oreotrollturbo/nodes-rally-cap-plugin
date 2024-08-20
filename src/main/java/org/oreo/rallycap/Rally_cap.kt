package org.oreo.rallycap

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.bukkit.plugin.java.JavaPlugin
import org.oreo.rallycap.commands.NodesWhitelistCommand
import org.oreo.rallycap.listeners.PlayerJoin
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class Rally_cap : JavaPlugin() {

    val whitelistedNations: MutableList<String> = mutableListOf()

    private var saveFile: File? = null
    private val gson = Gson()

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()

        // Initialize saveFile
        saveFile = File(dataFolder, "rallyCapWhitelist.json")
        createSaveFileIfNeeded()

        server.pluginManager.registerEvents(PlayerJoin(this), this)
        getCommand("rally-cap")!!.setExecutor(NodesWhitelistCommand(this))

        // Load the whitelist from file if it exists
        loadWhitelistedNations()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        saveWhitelistedNations()
    }

    private fun createSaveFileIfNeeded() {
        saveFile?.let {
            if (!it.exists()) {
                try {
                    it.parentFile.mkdirs() // Create parent directories if needed
                    it.createNewFile() // Create the save file
                } catch (e: IOException) {
                    e.printStackTrace()
                    logger.severe("Could not create save file: ${e.message}")
                }
            }
        }
    }

    fun saveWhitelistedNations() {
        saveFile?.let {
            try {
                FileWriter(it).use { writer ->
                    gson.toJson(whitelistedNations, writer)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                logger.severe("Could not save whitelisted nations: ${e.message}")
            }
        }
    }

    private fun loadWhitelistedNations() {
        saveFile?.let {
            if (it.exists()) {
                try {
                    FileReader(it).use { reader ->
                        val listType = object : TypeToken<MutableList<String>>() {}.type
                        whitelistedNations.addAll(gson.fromJson(reader, listType))
                    }
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    logger.severe("Could not parse the save file: ${e.message}")
                } catch (e: IOException) {
                    e.printStackTrace()
                    logger.severe("Could not read save file: ${e.message}")
                }
            }
        }
    }
}
