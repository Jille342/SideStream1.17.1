package client.ui.clicckgui2;

import client.features.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGui extends Screen {

    private final List<ClickGuiWindow> windows = new ArrayList<>();

    public ClickGui() {
        super(Text.of(""));
        double currentX = 50;
        for (Module.Category c : Module.Category.values()) {
            windows.add(new ClickGuiWindow((float) currentX, 30, c));
            currentX += 150;
        }
    }

    @Override
    protected void init() {
        windows.forEach(ClickGuiWindow::init);
        super.init();
    }

    @Override
    public void render(MatrixStack context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        windows.forEach(m -> m.render(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        windows.forEach(m -> m.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        windows.forEach(m -> m.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        windows.forEach(m -> m.mouseScrolled(mouseX, mouseY,amount));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        windows.forEach(m -> m.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public void onClose() {
        windows.forEach(m -> m.onClose());
       super.onClose();
    }
}
