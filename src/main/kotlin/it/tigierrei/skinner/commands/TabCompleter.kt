package it.tigierrei.skinner.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return if (cmd.name.equals("skinner", ignoreCase = true)) {
            if (args.size == 1) {
                mutableListOf(
                    "uploadByFile",
                    "uploadByUrl",
                    "uploadByUsername",
                    "uploadByUUID",
                    "help",
                    "skin",
                    "reload"
                )
            } else {
                null
            }
        } else {
            null
        }
    }

}