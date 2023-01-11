package encrypted.dssb.config.gameprofiles;

public class StatusEffect {
    public String Type;
    public int Duration;
    public int Amplifier;
    public boolean Ambient;
    public boolean ShowParticles;
    public boolean ShowIcon;
    public boolean OnRespawn;

    public StatusEffect(String type, int duration, int amplifier, boolean onRespawn) {
        Type = type;
        Duration = duration;
        Amplifier = amplifier;
        Ambient = false;
        ShowParticles = false;
        ShowIcon = true;
        OnRespawn = onRespawn;
    }
}
