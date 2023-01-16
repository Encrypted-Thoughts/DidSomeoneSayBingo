package encrypted.dssb.config.gameprofiles;

import java.util.ArrayList;

public class StartingItem {
    public String Name;
    public int Amount;
    public boolean OnRespawn;
    public boolean AutoEquip;
    public ArrayList<Enchantment> Enchantments = new ArrayList<>();

    public StartingItem(String name, int amount, boolean onRespawn, boolean autoEquip) {
        Name = name;
        Amount = amount;
        OnRespawn = onRespawn;
        AutoEquip = autoEquip;
    }

    public StartingItem(String name, int amount, boolean onRespawn, boolean autoEquip, ArrayList<Enchantment> enchantments) {
        Name = name;
        Amount = amount;
        OnRespawn = onRespawn;
        AutoEquip = autoEquip;
        Enchantments = enchantments;
    }
}
