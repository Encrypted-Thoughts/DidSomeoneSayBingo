package encrypted.dssb;

import encrypted.dssb.command.BingoCommands;
import encrypted.dssb.config.BingoConfig;
import encrypted.dssb.config.gameprofiles.GameProfileConfig;
import encrypted.dssb.config.gameprofiles.defaultconfigs.*;
import encrypted.dssb.config.itempools.ItemPool;
import encrypted.dssb.config.itempools.defaultpools.NetherItemPool;
import encrypted.dssb.config.itempools.defaultpools.OverworldEasyItemPool;
import encrypted.dssb.config.itempools.defaultpools.OverworldHardItemPool;
import encrypted.dssb.config.itempools.defaultpools.OverworldNormalItemPool;
import encrypted.dssb.config.replaceblocks.ReplacementBlocksConfig;
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
	public static ReplacementBlocksConfig REPLACEMENT_BLOCKS = new ReplacementBlocksConfig();
	public static ArrayList<GameProfileConfig> GameProfiles = new ArrayList<>();
	public static ArrayList<ItemPool> ItemPools = new ArrayList<>();

	@Override
	public void onInitialize() {
		loadConfigs();

		CommandRegistrationCallback.EVENT.register(BingoCommands::register);
		MapRenderHelper.loadTeamSlotAreas();
		MapRenderHelper.loadBingoCardBorder();

		if (GameProfiles.size() > 0) {
			GameSettings = GameProfiles.get(0);
			for (var profile : GameProfiles) {
				if (profile.Name.equals(CONFIG.DefaultGameProfile)) {
					GameSettings = profile;
					break;
				}
			}
		}

		ServerLifecycleEvents.SERVER_STARTED.register(BingoManager::createTeams);
		ServerTickEvents.START_SERVER_TICK.register(BingoManager::runOnServerTickEvent);
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> runAfterPlayerRespawnEvent(newPlayer));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> runOnPlayerConnectionEvent(handler.player));
	}

	private void loadConfigs() {
		CONFIG.ReadFromFile();

		REPLACEMENT_BLOCKS.ReadFromFile();

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
			} else
				BingoMod.LOGGER.error("Unable to create directory to store item pool files.");
		}


		GameProfiles = new ArrayList<>();
		var profileDirectory = FabricLoader.getInstance().getConfigDir().resolve("bingo/profiles").toFile();
		if (profileDirectory.exists()) {
			var files = profileDirectory.listFiles();
			if (files != null) {
				for (var file : files)
					GameProfiles.add(new GameProfileConfig(file));
			}
		} else {
			if (profileDirectory.mkdirs()) {
				GameProfileConfig profile = new EasyProfileConfig();
				GameProfiles.add(profile);
				profile.SaveToFile(profile.Name);

				profile = new HardProfileConfig();
				GameProfiles.add(profile);
				profile.SaveToFile(profile.Name);

				profile = new NormalProfileConfig();
				GameProfiles.add(profile);
				profile.SaveToFile(profile.Name);

				profile = new NetherProfileConfig();
				GameProfiles.add(profile);
				profile.SaveToFile(profile.Name);

				profile = new CursedBingoProfileConfig();
				GameProfiles.add(profile);
				profile.SaveToFile(profile.Name);

				profile = new SuperBingoProfileConfig();
				GameProfiles.add(profile);
				profile.SaveToFile(profile.Name);
			} else
				BingoMod.LOGGER.error("Unable to create directory to store game profile files.");
		}
	}
}