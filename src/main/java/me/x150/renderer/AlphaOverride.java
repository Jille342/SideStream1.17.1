package me.x150.renderer;

import java.util.Stack;

public class AlphaOverride {
    private static final Stack<Float> alphaMultipliers = new Stack<>();

    public static void pushAlphaMul(float val) {
        alphaMultipliers.push(Float.valueOf(val));
    }

    public static void popAlphaMul() {
        alphaMultipliers.pop();
    }

    public static float compute(float initialAlpha) {
        float alpha = initialAlpha;
        for (Float alphaMultiplier : alphaMultipliers)
            alpha *= alphaMultiplier.floatValue();
        return alpha;
    }
}
