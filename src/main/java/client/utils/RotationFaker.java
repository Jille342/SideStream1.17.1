/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
public final class RotationFaker implements MCUtil
{
    private boolean fakeRotation;
    private float serverYaw;
    private float serverPitch;
    private float realYaw;
    private float realPitch;

    public void faceVectorPacket(Vec3d vec)
    {
        Rotation needed = RotationUtils.getNeededRotations(vec);
        ClientPlayerEntity player = mc.player;

        fakeRotation = true;
        serverYaw =
                RotationUtils.limitAngleChange(player.getYaw(), needed.yaw());
        serverPitch = needed.pitch();
    }

    public static void faceVectorClient(Vec3d vec)
    {
        Rotation needed = RotationUtils.getNeededRotations(vec);

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        player.setYaw(
                RotationUtils.limitAngleChange(player.getYaw(), needed.yaw()));
        player.setPitch(needed.pitch());
    }

    public void faceVectorClientIgnorePitch(Vec3d vec)
    {
        Rotation needed = RotationUtils.getNeededRotations(vec);

        ClientPlayerEntity player = mc.player;
        player.setYaw(
                RotationUtils.limitAngleChange(player.getYaw(), needed.yaw()));
        player.setPitch(0);
    }

    public float getServerYaw()
    {
        return fakeRotation ? serverYaw : mc.player.getYaw();
    }

    public float getServerPitch()
    {
        return fakeRotation ? serverPitch : mc.player.getPitch();
    }
}
