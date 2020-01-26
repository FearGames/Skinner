package it.tigierrei.skinner.listeners

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDespawnEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import it.tigierrei.skinner.Skinner
import me.libraryaddict.disguise.DisguiseAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MythicMobsListener(private val pl: Skinner) : Listener {

    @EventHandler
    fun onMythicMobSpawn(event: MythicMobSpawnEvent) {
        pl.dataManager.mythicMobsAlive[event.entity] = event.mobType
        if (pl.dataManager.mythicMobsDisguiseMap.containsKey(event.mobType.internalName) && !DisguiseAPI.isDisguised(
                event.entity
            )
        ) {
            val disguise = pl.dataManager.mythicMobsDisguiseMap[event.mobType.internalName]
            if (disguise != null) {
                DisguiseAPI.disguiseToAll(event.entity, disguise)
            }
        }
    }

    @EventHandler
    fun onMythicMobDeath(event: MythicMobDeathEvent) {
        pl.dataManager.mythicMobsAlive.remove(event.entity)
    }

    @EventHandler
    fun onMythicMobDespawn(event: MythicMobDespawnEvent) {
        pl.dataManager.mythicMobsAlive.remove(event.entity)
    }
}