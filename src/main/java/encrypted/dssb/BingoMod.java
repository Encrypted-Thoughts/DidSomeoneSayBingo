package encrypted.dssb;

import encrypted.dssb.command.BingoCommands;
import encrypted.dssb.config.BingoConfig;
import encrypted.dssb.config.gameprofiles.GamePreset;
import encrypted.dssb.config.gameprofiles.defaultconfigs.*;
import encrypted.dssb.config.itempools.ItemPool;
import encrypted.dssb.config.itempools.defaultpools.*;
import encrypted.dssb.event.PlayerInventoryChangedCallback;
import encrypted.dssb.util.MapRenderHelper;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static encrypted.dssb.BingoManager.*;

public class BingoMod implements ModInitializer {
	public static final String MOD_ID = "dssbingo";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static BingoConfig CONFIG = new BingoConfig();
	public static ArrayList<GamePreset> GamePresets = new ArrayList<>();
	public static ArrayList<ItemPool> ItemPools = new ArrayList<>();

	@Override
	public void onInitialize() {
		loadConfigs();

		CommandRegistrationCallback.EVENT.register(BingoCommands::register);
		MapRenderHelper.loadTeamSlotAreas();
		MapRenderHelper.loadBingoCardBorder();

		if (!GamePresets.isEmpty()) {
			GameSettings = new GamePreset(GamePresets.get(0));
			for (var preset : GamePresets) {
				if (preset.Name.equals(CONFIG.DefaultGameProfile)) {
					GameSettings = new GamePreset(preset);
					break;
				}
			}
		}

		ServerLifecycleEvents.SERVER_STARTED.register(BingoManager::runOnStartup);
		ServerTickEvents.START_SERVER_TICK.register(BingoManager::runOnServerTickEvent);
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> runAfterPlayerRespawnEvent(newPlayer));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> runOnPlayerConnectionEvent(handler.player, server));

		PlayerInventoryChangedCallback.EVENT.register(BingoManager::checkItem);
	}

	private void loadConfigs() {
		CONFIG.ReadFromFile();

		ItemPools = new ArrayList<>();
		var poolDirectory = FabricLoader.getInstance().getConfigDir().resolve("bingo/itempools").toFile();
		if (poolDirectory.exists()) {
			var files = poolDirectory.listFiles();
			if (files != null) {
				for (var file : files)
					ItemPools.add(new ItemPool(file));
			}
		} else {
			if (poolDirectory.mkdirs()) {
				ItemPool pool = new OverworldEasyItemPool();
				ItemPools.add(pool);
				pool.SaveToFile(pool.Name);

				pool = new OverworldNormalItemPool();
				ItemPools.add(pool);
				pool.SaveToFile(pool.Name);

				pool = new OverworldHardItemPool();
				ItemPools.add(pool);
				pool.SaveToFile(pool.Name);

				pool = new NetherItemPool();
				ItemPools.add(pool);
				pool.SaveToFile(pool.Name);

				pool = new AllItemPool();
				ItemPools.add(pool);
				pool.SaveToFile(pool.Name);
			} else
				BingoMod.LOGGER.error("Unable to create directory to store item pool files.");
		}

		GamePresets = new ArrayList<>();
		var presetDirectory = FabricLoader.getInstance().getConfigDir().resolve("bingo/presets").toFile();
		if (presetDirectory.exists()) {
			var files = presetDirectory.listFiles();
			if (files != null) {
				for (var file : files)
					GamePresets.add(new GamePreset(file));
			}
		} else {
			if (presetDirectory.mkdirs()) {
				GamePreset preset = new EasyPreset();
				GamePresets.add(preset);
				preset.SaveToFile(preset.Name);

				preset = new HardPreset();
				GamePresets.add(preset);
				preset.SaveToFile(preset.Name);

				preset = new NormalPreset();
				GamePresets.add(preset);
				preset.SaveToFile(preset.Name);

				preset = new NetherPreset();
				GamePresets.add(preset);
				preset.SaveToFile(preset.Name);

				preset = new CursedPreset();
				GamePresets.add(preset);
				preset.SaveToFile(preset.Name);

				preset = new SuperPreset();
				GamePresets.add(preset);
				preset.SaveToFile(preset.Name);
			} else
				BingoMod.LOGGER.error("Unable to create directory to store game profile files.");
		}
	}
}