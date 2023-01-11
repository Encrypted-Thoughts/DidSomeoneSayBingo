package encrypted.dssb.config.gameprofiles;

public class PossibleItemGroup {
    public String Name = "";
    public double Weight;
    public String[] Items;
    public PossibleItemGroup(String[] items) {
        Items = items;
        Weight = 1.0;
    }
}
