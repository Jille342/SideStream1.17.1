/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

import java.util.Optional;

public final class RotationUtils {
    static MinecraftClient mc = MinecraftClient.getInstance();
    public static final double DEG_TO_RAD = Math.PI / 180.0;

    public static final double RAD_TO_DEG = 180.0 / Math.PI;

    private RotationUtils() {}
    private static float serverYaw;
    private static float serverPitch;


    public static Vec3d getEyesPos()
    {
        assert mc.player != null;
        return new Vec3d(MinecraftClient.getInstance().player.getX(),
                mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()),
                mc.player.getZ());
    }
    public static Vec3d getServerLookVec()
    {
        float f = MathHelper.cos(-serverYaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-serverYaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-serverPitch * 0.017453292F);
        float f3 = MathHelper.sin(-serverPitch * 0.017453292F);
        return new Vec3d(f1 * f2, f3, f * f2);
    }
    private static float[] getNeededRotations(Vec3d vec)
    {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }
    public static float limitAngleChange(float current, float intended,
                                         float maxChange)
    {
        float change = MathHelper.wrapDegrees(intended - current);

        change = MathHelper.clamp(change, -maxChange, maxChange);

        return MathHelper.wrapDegrees(current + change);
    }


    public static Rotation wrapAnglesToRelative(Rotation current, Rotation target) {
        if (current.yawIsReallyClose(target)) {
            return new Rotation(current.getYaw(), target.getPitch());
        }
        return target.subtract(current).normalize().add(current);
    }

    public static Rotation calcRotationFromVec3d(Vec3d orig, Vec3d dest, Rotation current) {
        return wrapAnglesToRelative(current, calcRotationFromVec3d(orig, dest));
    }

    private static Rotation calcRotationFromVec3d(Vec3d orig, Vec3d dest) {
        double[] delta = {orig.x - dest.x, orig.y - dest.y, orig.z - dest.z};
        double yaw = MathHelper.atan2(delta[0], -delta[2]);
        double dist = Math.sqrt(delta[0] * delta[0] + delta[2] * delta[2]);
        double pitch = MathHelper.atan2(delta[1], dist);
        return new Rotation(
                (float) (yaw * RAD_TO_DEG),
                (float) (pitch * RAD_TO_DEG)
        );
    }

    public static Vec3d calcVec3dFromRotation(Rotation rotation) {
        float f = MathHelper.cos(-rotation.getYaw() * (float) DEG_TO_RAD - (float) Math.PI);
        float f1 = MathHelper.sin(-rotation.getYaw() * (float) DEG_TO_RAD - (float) Math.PI);
        float f2 = -MathHelper.cos(-rotation.getPitch() * (float) DEG_TO_RAD);
        float f3 = MathHelper.sin(-rotation.getPitch() * (float) DEG_TO_RAD);
        return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

    public static Optional<Rotation> reachableOffset(Entity entity, BlockPos pos, Vec3d offsetPos, double blockReachDistance, boolean wouldSneak) {
        /*Vec3d eyes = wouldSneak ? RayTraceUtils.inferSneakingEyePosition(entity) : entity.getPositionEyes(1.0F);
        Rotation rotation = calcRotationFromVec3d(eyes, offsetPos, new Rotation(entity.rotationYaw, entity.rotationPitch));
        RayTraceResult result = RayTraceUtils.rayTraceTowards(entity, rotation, blockReachDistance, wouldSneak);
        //System.out.println(result);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (result.getBlockPos().equals(pos)) {
                return Optional.of(rotation);
            }
            if (entity.world.getBlockState(pos).getBlock() instanceof BlockFire && result.getBlockPos().equals(pos.down())) {
                return Optional.of(rotation);
            }
        }
        return Optional.empty();*/
        return null;
    }

    public static boolean faceEntityClient(Entity entity)
    {
        // get position & rotation
        Vec3d eyesPos = getEyesPos();
        Vec3d lookVec = getServerLookVec();

        // try to face center of boundingBox
        Box bb = entity.getBoundingBox();
        if(faceVectorClient(bb.getCenter()))
            return true;

        // if not facing center, check if facing anything in boundingBox
        return bb.intersects(eyesPos,
                eyesPos.add(lookVec.multiply(6)));
    }
    public static boolean faceVectorClient(Vec3d vec)
    {
        float[] rotations = getNeededRotations(vec);

        float oldYaw =mc.player.prevYaw;
        float oldPitch = mc.player.prevPitch;

        mc.player.bodyYaw =
                limitAngleChange(oldYaw, rotations[0], 30);
        mc.player.setPitch(rotations[1]);

        return Math.abs(oldYaw - rotations[0])
                + Math.abs(oldPitch - rotations[1]) < 1F;
    }
}
