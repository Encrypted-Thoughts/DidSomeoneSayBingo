package encrypted.dssb.config.replaceblocks;

import net.minecraft.util.math.BlockPos;

public class Coordinates {
    public int X;
    public int Y;
    public int Z;

    public Coordinates(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(X, Y, Z);
    }
}
