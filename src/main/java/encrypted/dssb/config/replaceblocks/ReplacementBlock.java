package encrypted.dssb.config.replaceblocks;

public class ReplacementBlock {
    public Coordinates Pos;
    public String DefaultBlock;
    public String RedBlock;
    public String GreenBlock;
    public String BlueBlock;
    public String PurpleBlock;
    public String PinkBlock;
    public String OrangeBlock;
    public String YellowBlock;
    public String CyanBlock;

    public ReplacementBlock(int x, int y, int z, String defaultBlock, String redBlock, String greenBlock, String blueBlock, String purpleBlock, String pinkBlock, String orangeBlock, String yellowBlock, String cyanBlock) {
        Pos = new Coordinates(x, y, z);
        DefaultBlock = defaultBlock;
        RedBlock = redBlock;
        GreenBlock = greenBlock;
        BlueBlock = blueBlock;
        PurpleBlock = purpleBlock;
        PinkBlock = pinkBlock;
        OrangeBlock = orangeBlock;
        YellowBlock = yellowBlock;
        CyanBlock = cyanBlock;
    }
}
