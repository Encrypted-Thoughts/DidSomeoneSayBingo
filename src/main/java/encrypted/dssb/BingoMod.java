package encrypted.dssb;

import encrypted.dssb.command.BingoCommands;
import encrypted.dssb.config.BingoConfig;
import encrypted.dssb.config.gameprofiles.GameProfileConfig;
import encrypted.dssb.config.gameprofiles.defaultconfigs.EasyProfileConfig;
import encrypted.dssb.config.gameprofiles.defaultconfigs.HardProfileConfig;
import encrypted.dssb.config.gameprofiles.defaultconfigs.NetherProfileConfig;
import encrypted.dssb.config.gameprofiles.defaultconfigs.NormalProfileConfig;
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

		GameProfiles = new ArrayList<>();
		var directory = FabricLoader.getInstance().getConfigDir().resolve("bingo/profiles").toFile();
		if (directory.exists()) {
			var files = directory.listFiles();
			if (files != null) {
				for (var file : files)
					GameProfiles.add(new GameProfileConfig(file));
			}
		} else {
			if (directory.mkdirs()) {
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
			} else
				BingoMod.LOGGER.error("Unable to create directory to store game profile files.");
		}
	}
}