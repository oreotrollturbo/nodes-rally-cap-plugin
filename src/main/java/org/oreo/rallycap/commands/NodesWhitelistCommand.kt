package org.oreo.rallycap.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.oreo.rallycap.Rally_cap
import org.oreo.rallycap.pleasedontlook.GetWarClass
import phonon.nodes.Nodes

class NodesWhitelistCommand(private val plugin: Rally_cap) : CommandExecutor, TabCompleter {

    val maxRally = plugin.config.getInt("nation-max-rally")

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        // Ensure only server operators can use this command
        if (!sender.isOp) {
            sender.sendMessage("${ChatColor.RED}Only server operators can use this command.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Usage: /$label <add|remove|get> <nation_name>")
            return true
        }

        val subCommand = args[0].toLowerCase()

        when (subCommand) {
            "add" -> {
                if (args.size < 2) {
                    sender.sendMessage("${ChatColor.RED}Please provide a nation name.")
                    return true
                }

                val nationName = args[1]
                val nation = Nodes.getNationFromName(nationName)

                if (nation == null) {
                    sender.sendMessage("${ChatColor.RED}Nation not found: $nationName")
                    return true
                }

                if (plugin.whitelistedNations.contains(nation.name)) {
                    sender.sendMessage("${ChatColor.RED}$nationName is already whitelisted.")
                } else {
                    plugin.whitelistedNations.add(nation.name)
                    plugin.saveWhitelistedNations()
                    sender.sendMessage("${ChatColor.AQUA}Added $nationName to the whitelist.")
                }
            }
            "remove" -> {
                if (args.size < 2) {
                    sender.sendMessage("${ChatColor.RED}Please provide a nation name.")
                    return true
                }

                val nationName = args[1]
                val nation = Nodes.getNationFromName(nationName)

                if (nation == null) {
                    sender.sendMessage("${ChatColor.RED}Nation not found: $nationName")
                    return true
                }

                if (plugin.whitelistedNations.contains(nation.name)) {
                    plugin.whitelistedNations.remove(nation.name)
                    plugin.saveWhitelistedNations()
                    sender.sendMessage("${ChatColor.AQUA}Removed $nationName from the whitelist.")
                } else {
                    sender.sendMessage("${ChatColor.RED}$nationName is not in the whitelist.")
                }
            }
            "get" -> {
                if (plugin.whitelistedNations.isEmpty()) {
                    sender.sendMessage("${ChatColor.YELLOW}The whitelist is currently empty.")
                } else {
                    sender.sendMessage("${ChatColor.GREEN}Whitelisted Nations:")
                    plugin.whitelistedNations.forEach { nation ->
                        sender.sendMessage(" - ${ChatColor.AQUA}$nation")
                    }
                }
            }
            "kick" -> {

                if (!GetWarClass.isWarOn()){
                    sender.sendMessage("${ChatColor.RED}War isn't enabled")
                    return true
                }

                for (player:Player in Bukkit.getOnlinePlayers()){
                    val nation = Nodes.getResident(player)?.town?.nation ?: continue
                    val nationName = nation.name

                    if (plugin.whitelistedNations.contains(nationName) || player.isOp || nation.capital.leader == Nodes.getResident(player)){
                        continue
                    }

                    sender.sendMessage("${ChatColor.GREEN}Kicking players that are passing caps")
                    if (nation.playersOnline.size >= maxRally){
                        player.kickPlayer("${ChatColor.RED}Your nation is already at max rally")
                        sender.sendMessage("${ChatColor.AQUA}- kicked " + player.name)
                    }
                }
            }
            else -> {
                sender.sendMessage("${ChatColor.RED}Invalid subcommand. Use 'add', 'remove', or 'get'.")
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {

        // Ensure tab completion is only available to server operators
        if (!sender.isOp) {
            return emptyList()
        }

        if (args.size == 1) {
            return mutableListOf("add", "remove", "get","kick").filter { it.startsWith(args[0]) }.toMutableList()
        }

        if (args.size == 2 && args[0].equals("remove", ignoreCase = true)) {
            return plugin.whitelistedNations.filter { it.startsWith(args[1], ignoreCase = true) }.toMutableList()
        }

        return emptyList();
    }
}
