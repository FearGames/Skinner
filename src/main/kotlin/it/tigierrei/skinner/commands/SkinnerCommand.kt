package it.tigierrei.skinner.commands

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import it.tigierrei.configapi.ConfigFile
import it.tigierrei.skinner.Skinner
import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.LibsDisguises
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.mineskin.SkinOptions
import org.mineskin.data.SkinCallback
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.util.*
import java.util.logging.Level


class SkinnerCommand(val pl: Skinner) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //HELP
        if (args.isEmpty() || (args.isNotEmpty() && args[0].equals("help", ignoreCase = true))) {
            if (!sender.hasPermission("skinner.help")) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
                return true
            }
            sender.sendMessage("${ChatColor.GREEN}/sk upload fileName fileExtensione <disguiseName> [displayName]")
            sender.sendMessage("${ChatColor.GREEN}The displayName parameter is optional")
            sender.sendMessage("${ChatColor.GREEN}/sk skin <disguiseName> to disguise yourself")
            return true
        }
        //RELOAD
        if (args[0].equals("reload", ignoreCase = true)) {
            if (!sender.hasPermission("skinner.reload")) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
                return true
            }
            pl.dataManager.loadConfig()
            sender.sendMessage("${ChatColor.GOLD}[${ChatColor.YELLOW}Skinner${ChatColor.GOLD}] ${ChatColor.GREEN}Config reloaded!")
            return true
        }
        //SKIN
        if (args[0].equals("skin", ignoreCase = true)) {
            if (!sender.hasPermission("skin")) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
                return true
            }
            if (args.size < 2) {
                sender.sendMessage("${ChatColor.RED}You must pass more arguments!Type /sk help for the list of commands")
                return true
            }
            val disguise = DisguiseAPI.getCustomDisguise(args[1])
            if (disguise != null) {
                DisguiseAPI.disguiseToAll((sender as Player), disguise)
                sender.sendMessage("${ChatColor.GREEN}Your skin has been changed!")
            } else {
                sender.sendMessage("${ChatColor.RED}That skin does not exist!")
            }
            return true
        }
        //sk uploadByFile fileName disguiseName <displayName>
        if (args[0].equals("uploadByFile", ignoreCase = true)) {
            if (!sender.hasPermission("uploadByFile")) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
                return true
            }
            if (args.size < 4) {
                sender.sendMessage("${ChatColor.RED}You must pass more arguments!Type /sk help for the list of commands")
                return true
            }
            uploadFromFile(args[1], sender, args[2], getDisplayName(args))
            return true
        }
        //sk uploadByUrl url disguiseName <displayName>
        if (args[0].equals("uploadByUrl", ignoreCase = true)) {
            if (!sender.hasPermission("uploadByUrl")) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
                return true
            }
            if (args.size < 3) {
                sender.sendMessage("${ChatColor.RED}You must pass more arguments!Type /sk help for the list of commands")
                return true
            }
            uploadFromUrl(args[1], sender, args[2], getDisplayName(args))
            return true
        }
        //sk uploadByUsername username disguiseName <displayName>
        if (args[0].equals("uploadByUsername", ignoreCase = true)) {
            if (!sender.hasPermission("uploadByUsername")) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
                return true
            }
            if (args.size < 3) {
                sender.sendMessage("${ChatColor.RED}You must pass more arguments!Type /sk help for the list of commands")
                return true
            }
            val player = pl.server.getOfflinePlayer(args[1])
            if (player == null) {
                sender.sendMessage("${ChatColor.RED}The specified player never joined the server and we can't retrieve his UUID.")
                sender.sendMessage("${ChatColor.RED}If you are sure that the player joined before, please check if you spelled his name right.")
                sender.sendMessage("${ChatColor.RED}Otherwise you can manually retrieve his UUID at the following link:")
                sender.sendMessage("${ChatColor.RED}Otherwise you can manually retrieve his UUID here:")
                sender.sendMessage("${ChatColor.RED}https://mcuuid.net/")
                return true
            }
            uploadFromUUID(player.uniqueId, sender, args[2], getDisplayName(args))
            return true
        }
        //sk uploadByUUID UUID disguiseName <displayName>
        if (args[0].equals("uploadByUUID", ignoreCase = true)) {
            if (!sender.hasPermission("uploadByUUID")) {
                sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
                return true
            }
            if (args.size < 3) {
                sender.sendMessage("${ChatColor.RED}You must pass more arguments!Type /sk help for the list of commands")
                return true
            }
            uploadFromUUID(UUID.fromString(args[1]), sender, args[2], getDisplayName(args))
            return true
        }
        return true
    }

    private fun uploadFromFile(
        skinFile: String,
        sender: CommandSender,
        disguiseName: String,
        displayName: String?
    ) {
        try {
            val file = File(pl.dataFolder.path + "/skins", skinFile)

            if (!file.exists()) {
                sender.sendMessage("§c'$skinFile' doesn't exist. Please be sure that the name and the extension are correct")
                return
            } else {
                file.createNewFile()
            }


            pl.mineskinClient.generateUpload(file, SkinOptions.name(disguiseName), object : SkinCallback {
                override fun done(skin: org.mineskin.data.Skin?) {
                    sender.sendMessage("§aSkin data generated.")
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", skin?.data?.uuid.toString())
                    jsonObject.addProperty("name", "")

                    val property = JsonObject()
                    property.addProperty("name", "textures")
                    property.addProperty("value", skin?.data?.texture?.value)
                    property.addProperty("signature", skin?.data?.texture?.signature)

                    val propertiesArray = JsonArray()
                    propertiesArray.add(property)

                    jsonObject.add("properties", propertiesArray)

                    val disguise = ConfigFile("${pl.dataFolder.parentFile.path}/LibsDisguises/disguises.yml", false)
                    disguise.getSection("Disguises").set(
                        disguiseName,
                        "player ${displayName
                            ?: "\"\""} setSkin {\"id\":\"a149f81bf7844f8987c554afdd4db533\",\"name\":\"libraryaddict\",\"properties\":[{\"signature\":\"${skin?.data?.texture?.signature}\",\"name\":\"textures\",\"value\":\"${skin?.data?.texture?.value}\"}]}"
                    )
                    disguise.save()
                    LibsDisguises.getInstance().reloadConfig()
                }

                override fun waiting(l: Long) {
                    sender.sendMessage("§7Waiting " + l / 1000.0 + "s to upload skin...")
                }

                override fun uploading() {
                    sender.sendMessage("§eUploading skin...")
                }

                override fun error(s: String) {
                    sender.sendMessage("§cError while generating skin: $s")
                    sender.sendMessage("§cPlease make sure the image is a valid skin texture and try again.")
                }

                override fun exception(exception: Exception) {
                    sender.sendMessage("§cException while generating skin, see console for details: " + exception.message)
                    sender.sendMessage("§cPlease make sure the image is a valid skin texture and try again.")
                    pl.logger.log(Level.WARNING, "Exception while generating skin", exception)
                }
            })
        } catch (e: MalformedURLException) {
            sender.sendMessage("§cInvalid URL")
            return
        } catch (e: IOException) {
            sender.sendMessage("§cUnexpected IOException: " + e.message)
            pl.logger.log(
                Level.SEVERE,
                "Unexpected IOException while creating skin '$disguiseName' with source '${pl.dataFolder.path}/skins/$skinFile'",
                e
            )
        }

    }

    private fun uploadFromUrl(url: String, sender: CommandSender, disguiseName: String, displayName: String?) {
        try {
            pl.mineskinClient.generateUrl(url, SkinOptions.name(disguiseName), object : SkinCallback {
                override fun done(skin: org.mineskin.data.Skin?) {
                    sender.sendMessage("§aSkin data generated.")
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", skin?.data?.uuid.toString())
                    jsonObject.addProperty("name", "")

                    val property = JsonObject()
                    property.addProperty("name", "textures")
                    property.addProperty("value", skin?.data?.texture?.value)
                    property.addProperty("signature", skin?.data?.texture?.signature)

                    val propertiesArray = JsonArray()
                    propertiesArray.add(property)

                    jsonObject.add("properties", propertiesArray)

                    val disguise = ConfigFile("${pl.dataFolder.parentFile.path}/LibsDisguises/disguises.yml", false)
                    disguise.getSection("Disguises").set(
                        disguiseName,
                        "player ${displayName
                            ?: "\"\""} setSkin {\"id\":\"a149f81bf7844f8987c554afdd4db533\",\"name\":\"libraryaddict\",\"properties\":[{\"signature\":\"${skin?.data?.texture?.signature}\",\"name\":\"textures\",\"value\":\"${skin?.data?.texture?.value}\"}]}"
                    )
                    disguise.save()
                    LibsDisguises.getInstance().reloadConfig()
                }

                override fun waiting(l: Long) {
                    sender.sendMessage("§7Waiting " + l / 1000.0 + "s to upload skin...")
                }

                override fun uploading() {
                    sender.sendMessage("§eUploading skin...")
                }

                override fun error(s: String) {
                    sender.sendMessage("§cError while generating skin: $s")
                    sender.sendMessage("§cPlease make sure the url is valid and try again.")
                }

                override fun exception(exception: Exception) {
                    sender.sendMessage("§cException while generating skin, see console for details: " + exception.message)
                    sender.sendMessage("§cPlease make sure the url is valid and try again.")
                    pl.logger.log(Level.WARNING, "Exception while generating skin", exception)
                }
            })
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Something went wrong during the upload by URL")
        }
    }

    private fun uploadFromUUID(uuid: UUID, sender: CommandSender, disguiseName: String, displayName: String?) {
        try {
            pl.mineskinClient.generateUser(uuid, SkinOptions.name(disguiseName), object : SkinCallback {
                override fun done(skin: org.mineskin.data.Skin?) {
                    sender.sendMessage("§aSkin data generated.")
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", skin?.data?.uuid.toString())
                    jsonObject.addProperty("name", "")

                    val property = JsonObject()
                    property.addProperty("name", "textures")
                    property.addProperty("value", skin?.data?.texture?.value)
                    property.addProperty("signature", skin?.data?.texture?.signature)

                    val propertiesArray = JsonArray()
                    propertiesArray.add(property)

                    jsonObject.add("properties", propertiesArray)

                    val disguise = ConfigFile("${pl.dataFolder.parentFile.path}/LibsDisguises/disguises.yml", false)
                    disguise.getSection("Disguises").set(
                        disguiseName,
                        "player ${displayName
                            ?: "\"\""} setSkin {\"id\":\"a149f81bf7844f8987c554afdd4db533\",\"name\":\"libraryaddict\",\"properties\":[{\"signature\":\"${skin?.data?.texture?.signature}\",\"name\":\"textures\",\"value\":\"${skin?.data?.texture?.value}\"}]}"
                    )
                    disguise.save()
                    LibsDisguises.getInstance().reloadConfig()
                }

                override fun waiting(l: Long) {
                    sender.sendMessage("§7Waiting " + l / 1000.0 + "s to upload skin...")
                }

                override fun uploading() {
                    sender.sendMessage("§eUploading skin...")
                }

                override fun error(s: String) {
                    sender.sendMessage("§cError while generating skin: $s")
                    sender.sendMessage("§cPlease make sure the username or UUID provided is correct")
                }

                override fun exception(exception: Exception) {
                    sender.sendMessage("§cException while generating skin, see console for details: " + exception.message)
                    sender.sendMessage("§cPlease make sure the username or UUID provided is correct")
                    pl.logger.log(Level.WARNING, "Exception while generating skin", exception)
                }
            })
        } catch (e: Exception) {
            sender.sendMessage("${ChatColor.RED}Something went wrong during the upload by Username/UUID")
        }
    }

    private fun getDisplayName(args: Array<out String>): String {
        var displayName = "\""
        if (args.size > 3) {
            for (i in 3 until args.size) {
                displayName = displayName + " " + args[i];
            }
        }
        displayName += "\""
        return displayName
    }
}