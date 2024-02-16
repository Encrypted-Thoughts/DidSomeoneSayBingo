package encrypted.dssb.config;

import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;

public class TeamConfig {
    public int Number;
    public String Name;
    public Formatting Color;
    public AbstractTeam.CollisionRule Collision;
    public boolean FriendlyFire;
    public String BlockId;

    public TeamConfig(int number, String name, Formatting color, AbstractTeam.CollisionRule collision, boolean friendlyFire, String blockId) {
        Number = number;
        Name = name;
        Color = color;
        Collision = collision;
        FriendlyFire = friendlyFire;
        BlockId = blockId;
    }
}
