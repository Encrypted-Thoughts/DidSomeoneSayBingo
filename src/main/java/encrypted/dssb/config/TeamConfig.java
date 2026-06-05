package encrypted.dssb.config;

import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.Team;

public class TeamConfig {
    public int Number;
    public String Name;
    public ChatFormatting Color;
    public Team.CollisionRule Collision;
    public boolean FriendlyFire;
    public String BlockId;

    public TeamConfig(int number, String name, ChatFormatting color, Team.CollisionRule collision, boolean friendlyFire, String blockId) {
        Number = number;
        Name = name;
        Color = color;
        Collision = collision;
        FriendlyFire = friendlyFire;
        BlockId = blockId;
    }
}
