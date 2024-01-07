package client.utils.math;

import client.event.listeners.EventRender2D;
import client.event.listeners.RenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class MatrixUtil {
    private static RenderEvent lastEvent = null;
    private static int cachedScaleFactor = 1;

    public static BillboardPos getBillboardPos(Vec3d posVec) {
        if (lastEvent == null) return null;
        Quaternion pos = new Quaternion((float) (posVec.x - lastEvent.offsetX), (float) (posVec.y - lastEvent.offsetY), (float) (posVec.z - lastEvent.offsetZ), 1);
        return new BillboardPos(pos, lastEvent.model, lastEvent.projection, cachedScaleFactor);
    }

    // 4x4 by 4x1
    public static Quaternion mult(Matrix4f m, Quaternion q) {
        return q;
    }
    public static  void onRender(RenderEvent event) {
        lastEvent = event;
        cachedScaleFactor = MinecraftClient.getInstance().getWindow().calculateScaleFactor(MinecraftClient.getInstance().options.guiScale, MinecraftClient.getInstance().forcesUnicodeFont());
    }

}
