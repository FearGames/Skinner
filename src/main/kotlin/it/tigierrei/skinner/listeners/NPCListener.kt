package it.tigierrei.skinner.listeners

import it.tigierrei.skinner.Skinner
import me.libraryaddict.disguise.DisguiseAPI
import net.citizensnpcs.api.event.NPCSpawnEvent
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class NPCListener(private val pl: Skinner) : Listener {

    @EventHandler
    fun onNpcSpawn(event: NPCSpawnEvent) {
        val npc = event.npc
        //Npc of player type is bugged
        if (npc.entity != null && npc.entity.type != EntityType.PLAYER && pl.dataManager.citizensDisguiseMap.containsKey(
                npc.name
            )
        ) {
            val disguise = pl.dataManager.citizensDisguiseMap[npc.name]
            if (disguise != null) {
                Bukkit.getScheduler().runTaskLater(pl, Runnable {
                    DisguiseAPI.disguiseToAll(npc.entity, disguise)
                }, 1L)
                return
            }
        }
    }
}