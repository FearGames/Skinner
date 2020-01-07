package it.tigierrei.configapi;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigFile {

    private File configFile;
    private FileConfiguration config;
    private boolean debug;
    private Logger logger = Bukkit.getLogger();
    private JavaPlugin plugin;

    /**
     * Use this constructor in case you want to create a new blank config file
     *
     * @param filePath Path of the config file
     * @param debug    True to enable logger
     */
    public ConfigFile(String filePath, boolean debug) {
        configFile = new File(filePath);
        this.debug = debug;
        checkExistance();
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Use this constructor in case you want to copy an internal file (or template)
     *
     * @param filePath Path of the config file
     * @param debug    True to enable logger
     * @param plugin   Instance of the plugin using the API
     */
    public ConfigFile(String filePath, boolean debug, JavaPlugin plugin) {
        configFile = new File(filePath);
        this.debug = debug;
        checkExistance();
        config = YamlConfiguration.loadConfiguration(configFile);
        this.plugin = plugin;
    }

    /**
     * Use this constructor in case you want to load an existing file
     *
     * @param configFile The file you want to load
     * @param debug      True to enable logger
     */
    public ConfigFile(File configFile, boolean debug) {
        this.configFile = configFile;
        this.debug = debug;
        checkExistance();
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Set a value in the config
     *
     * @param name  Property name
     * @param value Value
     */
    public void set(String name, Object value) {
        config.set(name, value);
        save();
    }

    /**
     * Create a configuration section in the config
     *
     * @param name Name of the section
     */
    public void createSection(String name) {
        config.createSection(name);
        save();
    }

    /**
     * Create a configuration section in the config and initialize it
     *
     * @param name Name of the section
     * @param map  Map collection to initialize the section
     */
    public void createSection(String name, Map<?, ?> map) {
        config.createSection(name, map);
        save();
    }

    /**
     * Retrieve configuration section
     *
     * @param name Name of the section
     * @return ConfigurationSection
     */
    public ConfigurationSection getSection(String name) {
        return config.getConfigurationSection(name);
    }

    /**
     * Retrieve value from the config
     *
     * @param name Name of the value
     * @return Value of the property
     */
    public Object get(String name) {
        return config.get(name);
    }

    /**
     * Get string list
     *
     * @param name List name
     * @return List of strings
     */
    public List<String> getStringList(String name) {
        return config.getStringList(name);
    }

    /**
     * Check if the config has a property
     *
     * @param name Name of the property
     * @return true if exist, else false
     */
    public boolean hasProperty(String name) {
        return config.get(name) != null;
    }

    private void checkExistance() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        if (!configFile.exists()) {
            if (debug) {
                logger.log(Level.INFO, configFile.getName() + " not exist, creating a new one...");
            }
            if (plugin != null) {
                plugin.saveResource(configFile.getName(), false);
            } else {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                configFile.createNewFile();
                if (debug) {
                    logger.log(Level.INFO, configFile.getName() + "has been created.");
                }
            } catch (IOException e) {
                if (debug) {
                    logger.log(Level.SEVERE, ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "Impossible to create " + configFile.getName());
                    logger.log(Level.SEVERE, ChatColor.BOLD + " " + ChatColor.DARK_RED + "Error caused by:");
                    logger.log(Level.SEVERE, ChatColor.RED + e.getCause().toString());
                }
            }
        } else {
            logger.log(Level.INFO, configFile.getName() + " has been found and loaded.");
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            if (debug) {
                logger.log(Level.SEVERE, "Error while saving the config file " + ChatColor.DARK_GRAY + configFile.getName());
            }
            e.printStackTrace();
        }
    }
}
