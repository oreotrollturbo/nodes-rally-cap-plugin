package org.oreo.rallycap.listeners

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.oreo.rallycap.Rally_cap
import org.oreo.rallycap.pleasedontlook.GetWarClass
import phonon.nodes.Nodes

class PlayerJoin (private val plugin: Rally_cap) : Listener {

    val maxRally = plugin.config.getInt("nation-max-rally")

    @EventHandler
    fun playerJoined(e : PlayerJoinEvent){

        if (!GetWarClass.isWarOn()){
            return
        }

        val player = e.player
        val nation = Nodes.getResident(player)?.town?.nation ?: return
        val nationName = nation.name

        if (plugin.whitelistedNations.contains(nationName) || player.isOp || nation.capital.leader == Nodes.getResident(player)){
            return
        }

        if (nation.playersOnline.size >= maxRally){
            player.kickPlayer("${ChatColor.RED}Your nation is already at max rally")
        }
    }
}