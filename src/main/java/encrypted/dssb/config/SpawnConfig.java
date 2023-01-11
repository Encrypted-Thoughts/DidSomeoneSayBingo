package encrypted.dssb.config;

import encrypted.dssb.config.replaceblocks.Coordinates;

public class SpawnConfig {
    public String Dimension = "minecraft:overworld";
    public Coordinates HubCoords = new Coordinates(0, 200, 0);
    public String HubMode = "adventure";
    public boolean TeleportToHubOnJoin = true;
    public boolean TeleportToHubOnRespawn = true;
}
