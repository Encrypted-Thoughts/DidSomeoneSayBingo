package encrypted.dssb.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class BingoCommands {

    public static String bingoCommand = "bingo";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment ignoredRegistrationEnvironment) {
        BingoHelpCommand.register(dispatcher);
        BingoSettingsCommand.register(dispatcher, commandRegistryAccess);
        BingoGenerateCommand.register(dispatcher);
        BingoStartCommand.register(dispatcher);
        BingoEndCommand.register(dispatcher);
        BingoPVPCommand.register(dispatcher);
        BingoPresetsCommand.register(dispatcher);
        BingoSpawnCommand.register(dispatcher);
        BingoGetMapCommand.register(dispatcher);
        BingoTeamsCommand.register(dispatcher);
        BingoVoteEndCommand.register(dispatcher);

        ClarifyCommand.register(dispatcher);
        TeamTPCommand.register(dispatcher);
        PlayerCountCommand.register(dispatcher);
        LeaveCommand.register(dispatcher);
    }
}
