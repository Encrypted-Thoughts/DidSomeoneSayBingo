package encrypted.dssb.config.itempools;

public class ItemGroup {
    public double Weight;
    public String[] Items;
    public ItemGroup(String[] items) {
        Items = items;
        Weight = 1.0;
    }
}
