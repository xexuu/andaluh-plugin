package com.xexuu.andaluh;

import com.andaluh.Andaluh;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AndaluhPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    private static final char DEFAULT_VVF = 'h';
    private static final String CONFIG_PLAYERS = "players";
    private static final String CONFIG_DEBUG = "debug";

    private final Map<UUID, PlayerSettings> settingsByPlayer = new ConcurrentHashMap<>();
    private boolean debugEnabled = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        debugEnabled = getConfig().getBoolean(CONFIG_DEBUG, false);
        loadSettings();

        getServer().getPluginManager().registerEvents(this, this);

        PluginCommand command = getCommand("andaluh");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public void onDisable() {
        saveSettings();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        PlayerSettings settings = getSettings(event.getPlayer());
        if (!settings.enabled) {
            return;
        }

        Component baseMessage = event.originalMessage();
        String message = PlainTextComponentSerializer.plainText().serialize(baseMessage);
        if (message.startsWith("/")) {
            return;
        }

        String translated = Andaluh.epa(message, settings.mode.getVaf(), DEFAULT_VVF, true, false);
        if (debugEnabled) {
            String visible = PlainTextComponentSerializer.plainText().serialize(event.message());
            getLogger().info("Andaluh chat [" + event.getPlayer().getName() + "] orig='" + message + "' visible='" + visible + "' -> '" + translated + "'");
        }
        Component updated = Component.text(translated, baseMessage.style());
        event.message(updated);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("andaluh.use")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar /andaluh");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo jugadores pueden usar /andaluh");
            return true;
        }

        PlayerSettings settings = getSettings(player);

        if (args.length == 0) {
            sendStatus(player, settings);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "on":
                settings.enabled = true;
                saveSettings();
                player.sendMessage(ChatColor.GREEN + "Andaluh activado");
                return true;
            case "off":
                settings.enabled = false;
                saveSettings();
                player.sendMessage(ChatColor.YELLOW + "Andaluh desactivado");
                return true;
            case "modo":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Uso: /andaluh modo <estandar|ceceo|seseo|heheo>");
                    return true;
                }
                AndaluhMode mode = AndaluhMode.fromInput(args[1]);
                if (mode == null) {
                    player.sendMessage(ChatColor.RED + "Modo desconocido: " + args[1]);
                    return true;
                }
                settings.mode = mode;
                saveSettings();
                player.sendMessage(ChatColor.GREEN + "Modo andaluh: " + mode.getLabel());
                return true;
            case "test":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Uso: /andaluh test <texto>");
                    return true;
                }
                String original = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                String result = Andaluh.epa(original, settings.mode.getVaf(), DEFAULT_VVF, true, false);
                player.sendMessage(ChatColor.AQUA + "Andaluh (" + settings.mode.getLabel() + "): " + result);
                return true;
            default:
                player.sendMessage(ChatColor.RED + "Uso: /andaluh on|off | /andaluh modo <estandar|ceceo|seseo|heheo> | /andaluh test <texto>");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> results = new ArrayList<>();
        if (!sender.hasPermission("andaluh.use")) {
            return results;
        }

        if (args.length == 1) {
            addIfMatches(results, args[0], "on");
            addIfMatches(results, args[0], "off");
            addIfMatches(results, args[0], "modo");
            addIfMatches(results, args[0], "test");
            return results;
        }

        if (args.length == 2 && "modo".equalsIgnoreCase(args[0])) {
            for (AndaluhMode mode : AndaluhMode.values()) {
                addIfMatches(results, args[1], mode.getLabel());
            }
            return results;
        }

        return results;
    }

    private PlayerSettings getSettings(Player player) {
        return settingsByPlayer.computeIfAbsent(player.getUniqueId(), ignored -> new PlayerSettings());
    }

    private void sendStatus(Player player, PlayerSettings settings) {
        String status = settings.enabled ? "ON" : "OFF";
        player.sendMessage(ChatColor.AQUA + "Andaluh: " + status + " | modo=" + settings.mode.getLabel());
    }

    private void addIfMatches(List<String> results, String prefix, String option) {
        if (prefix == null || prefix.isBlank()) {
            results.add(option);
            return;
        }
        if (option.startsWith(prefix.toLowerCase(Locale.ROOT))) {
            results.add(option);
        }
    }

    private void loadSettings() {
        settingsByPlayer.clear();
        FileConfiguration config = getConfig();
        debugEnabled = config.getBoolean(CONFIG_DEBUG, false);
        ConfigurationSection playersSection = config.getConfigurationSection(CONFIG_PLAYERS);
        if (playersSection == null) {
            return;
        }

        for (String key : playersSection.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException ex) {
                getLogger().warning("Invalid UUID in config: " + key);
                continue;
            }

            ConfigurationSection entry = playersSection.getConfigurationSection(key);
            if (entry == null) {
                continue;
            }

            PlayerSettings settings = new PlayerSettings();
            settings.enabled = entry.getBoolean("enabled", false);
            String modeText = entry.getString("mode", AndaluhMode.ESTANDAR.getLabel());
            AndaluhMode mode = AndaluhMode.fromInput(modeText);
            settings.mode = mode == null ? AndaluhMode.ESTANDAR : mode;

            settingsByPlayer.put(uuid, settings);
        }
    }

    private void saveSettings() {
        FileConfiguration config = getConfig();
        config.set(CONFIG_PLAYERS, null);

        for (Map.Entry<UUID, PlayerSettings> entry : settingsByPlayer.entrySet()) {
            String base = CONFIG_PLAYERS + "." + entry.getKey().toString();
            PlayerSettings settings = entry.getValue();
            config.set(base + ".enabled", settings.enabled);
            config.set(base + ".mode", settings.mode.getLabel());
        }

        saveConfig();
    }
}
