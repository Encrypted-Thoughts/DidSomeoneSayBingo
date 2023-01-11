package encrypted.dssb.util;

import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;

public class TeleportHelper {
    public static void teleport(ServerPlayerEntity player, ServerWorld world, double x, double y, double z, float yaw, float pitch) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (!World.isValid(blockPos))
            throw new SimpleCommandExceptionType(Text.translatable("commands.teleport.invalidPosition")).create();
        else {
            float f = MathHelper.wrapDegrees(yaw);
            float g = MathHelper.wrapDegrees(pitch);
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getId());
            player.stopRiding();
            if (player.isSleeping())
                player.wakeUp(true, true);
            if (world == player.world)
                player.networkHandler.requestTeleport(x, y, z, f, g, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class));
            else
                player.teleport(world, x, y, z, f, g);
            player.setHeadYaw(f);

            if (!player.isFallFlying()) {
                player.setVelocity(player.getVelocity().multiply(1.0, 0.0, 1.0));
                player.setOnGround(true);
            }
        }
    }

    private static final Dynamic4CommandExceptionType FAILED_TEAMS_EXCEPTION = new Dynamic4CommandExceptionType((pilesCount, x, z, maxSpreadDistance) -> Text.translatable("commands.spreadplayers.failed.teams", pilesCount, x, z, maxSpreadDistance));
    private static final Dynamic4CommandExceptionType FAILED_ENTITIES_EXCEPTION = new Dynamic4CommandExceptionType((pilesCount, x, z, maxSpreadDistance) -> Text.translatable("commands.spreadplayers.failed.entities", pilesCount, x, z, maxSpreadDistance));
    private static final Dynamic2CommandExceptionType INVALID_HEIGHT_EXCEPTION = new Dynamic2CommandExceptionType((maxY, worldBottomY) -> Text.translatable("commands.spreadplayers.failed.invalid.height", maxY, worldBottomY));

    public static HashMap<AbstractTeam, BlockPos> spreadPlayers(ServerWorld world, Vec2f center, float spreadDistance, float maxRange, int maxY, List<ServerPlayerEntity> players, boolean respectTeams) throws CommandSyntaxException {
        int i = world.getBottomY();
        if (maxY < i)
            throw INVALID_HEIGHT_EXCEPTION.create(maxY, i);
        Random random = Random.create();
        double d = center.x - maxRange;
        double e = center.y - maxRange;
        double f = center.x + maxRange;
        double g = center.y + maxRange;
        var piles = TeleportHelper.makePiles(random, world.getScoreboard().getTeams().size(), d, e, f, g);
        TeleportHelper.spread(center, spreadDistance, world, random, d, e, f, g, maxY, piles, respectTeams);
        getMinDistance(players, world, piles, maxY, respectTeams);
        return getTeamPiles(world, maxY, piles);
    }

    private static void spread(Vec2f center, double spreadDistance, ServerWorld world, Random random, double minX, double minZ, double maxX, double maxZ, int maxY, Pile[] piles, boolean respectTeams) throws CommandSyntaxException {
        boolean bl = true;
        double d = 3.4028234663852886E38;

        int i;
        for (i = 0; i < 10000 && bl; ++i) {
            bl = false;
            d = 3.4028234663852886E38;

            int k;
            Pile pile2;
            for (int j = 0; j < piles.length; ++j) {
                Pile pile = piles[j];
                k = 0;
                pile2 = new Pile();

                for (int l = 0; l < piles.length; ++l) {
                    if (j != l) {
                        Pile pile3 = piles[l];
                        double e = pile.getDistance(pile3);
                        d = Math.min(e, d);
                        if (e < spreadDistance) {
                            ++k;
                            pile2.x += pile3.x - pile.x;
                            pile2.z += pile3.z - pile.z;
                        }
                    }
                }

                if (k > 0) {
                    pile2.x /= k;
                    pile2.z /= k;
                    double f = pile2.absolute();
                    if (f > 0.0) {
                        pile2.normalize();
                        pile.subtract(pile2);
                    } else
                        pile.setPileLocation(random, minX, minZ, maxX, maxZ);

                    bl = true;
                }

                if (pile.clamp(minX, minZ, maxX, maxZ))
                    bl = true;
            }

            if (!bl) {
                int var29 = piles.length;

                for (k = 0; k < var29; ++k) {
                    pile2 = piles[k];
                    if (!pile2.isSafe(world, maxY)) {
                        pile2.setPileLocation(random, minX, minZ, maxX, maxZ);
                        bl = true;
                    }
                }
            }
        }

        if (d == 3.4028234663852886E38)
            d = 0.0;

        if (i >= 10000) {
            if (respectTeams)
                throw FAILED_TEAMS_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d));
            else
                throw FAILED_ENTITIES_EXCEPTION.create(piles.length, center.x, center.y, String.format(Locale.ROOT, "%.2f", d));
        }
    }

    private static HashMap<AbstractTeam, BlockPos> getTeamPiles(ServerWorld world, int maxY, Pile[] piles) {
        int i = 0;
        var map = new HashMap<AbstractTeam, BlockPos>();
        for (var team : world.getScoreboard().getTeams()) {
            if (!map.containsKey(team)) {
                map.put(team, new BlockPos(MathHelper.floor(piles[i].x) + 0.5, piles[i].getY(world, maxY), MathHelper.floor(piles[i].z) + 0.5));
                i++;
            }
        }
        return map;
    }

    private static Pile[] makePiles(Random random, int count, double minX, double minZ, double maxX, double maxZ) {
        Pile[] piles = new Pile[count];
        for (int i = 0; i < piles.length; ++i) {
            Pile pile = new Pile();
            pile.setPileLocation(random, minX, minZ, maxX, maxZ);
            piles[i] = pile;
        }
        return piles;
    }

    private static void getMinDistance(Collection<? extends Entity> entities, ServerWorld world, Pile[] piles, int maxY, boolean respectTeams) {
        int i = 0;
        Map<AbstractTeam, Pile> map = Maps.newHashMap();

        double e;
        for (var entity : entities) {
            Pile pile;
            if (respectTeams) {
                AbstractTeam abstractTeam = entity instanceof PlayerEntity ? entity.getScoreboardTeam() : null;
                if (!map.containsKey(abstractTeam))
                    map.put(abstractTeam, piles[i++]);

                pile = map.get(abstractTeam);
            } else
                pile = piles[i++];

            entity.teleport((double) MathHelper.floor(pile.x) + 0.5, pile.getY(world, maxY), (double) MathHelper.floor(pile.z) + 0.5);
            e = Double.MAX_VALUE;

            for (Pile pile2 : piles) {
                if (pile != pile2) {
                    double f = pile.getDistance(pile2);
                    e = Math.min(f, e);
                }
            }
        }
    }

    static class Pile {
        double x;
        double z;

        double getDistance(Pile other) {
            double d = this.x - other.x;
            double e = this.z - other.z;
            return Math.sqrt(d * d + e * e);
        }

        void normalize() {
            double d = this.absolute();
            this.x /= d;
            this.z /= d;
        }

        double absolute() {
            return Math.sqrt(this.x * this.x + this.z * this.z);
        }

        public void subtract(Pile other) {
            this.x -= other.x;
            this.z -= other.z;
        }

        public boolean clamp(double minX, double minZ, double maxX, double maxZ) {
            boolean bl = false;
            if (this.x < minX) {
                this.x = minX;
                bl = true;
            } else if (this.x > maxX) {
                this.x = maxX;
                bl = true;
            }

            if (this.z < minZ) {
                this.z = minZ;
                bl = true;
            } else if (this.z > maxZ) {
                this.z = maxZ;
                bl = true;
            }

            return bl;
        }

        public int getY(BlockView blockView, int maxY) {
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.x, maxY + 1, this.z);
            boolean bl = blockView.getBlockState(mutable).isAir();
            mutable.move(Direction.DOWN);

            boolean bl3;
            for (boolean bl2 = blockView.getBlockState(mutable).isAir(); mutable.getY() > blockView.getBottomY(); bl2 = bl3) {
                mutable.move(Direction.DOWN);
                bl3 = blockView.getBlockState(mutable).isAir();
                if (!bl3 && bl2 && bl)
                    return mutable.getY() + 1;

                bl = bl2;
            }

            return maxY + 1;
        }

        public boolean isSafe(BlockView world, int maxY) {
            BlockPos blockPos = new BlockPos(this.x, this.getY(world, maxY) - 1, this.z);
            BlockState blockState = world.getBlockState(blockPos);
            Material material = blockState.getMaterial();
            return blockPos.getY() < maxY && !material.isLiquid() && material != Material.FIRE;
        }

        public void setPileLocation(Random random, double minX, double minZ, double maxX, double maxZ) {
            this.x = MathHelper.nextDouble(random, minX, maxX);
            this.z = MathHelper.nextDouble(random, minZ, maxZ);
        }
    }
}
