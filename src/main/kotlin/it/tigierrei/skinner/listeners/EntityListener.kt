package it.tigierrei.skinner.listeners

import it.tigierrei.skinner.Skinner
import me.libraryaddict.disguise.DisguiseAPI
import net.citizensnpcs.api.CitizensAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

class EntityListener(private val pl: Skinner) : Listener {

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        if (!DisguiseAPI.isDisguised(event.entity)) {
            if (pl.dataManager.citizens) {
                if (CitizensAPI.getNPCRegistry().isNPC(event.entity)) {
                    return
                }
            }
            if (pl.dataManager.mythicMobs) {
                if (event.entity.customName != null) {
                    return
                }
            }
            if (pl.dataManager.vanillaMobs) {
                val disguise = pl.dataManager.vanillaMobsDisguiseMap[event.entityType]
                if (disguise != null) {
                    DisguiseAPI.disguiseToAll(event.entity, disguise)
                }
            }
        }
    }

}