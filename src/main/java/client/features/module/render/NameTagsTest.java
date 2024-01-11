package client.features.module.render;

import client.features.module.Module;
import client.setting.NumberSetting;

public class NameTagsTest extends Module {

  public static   NumberSetting scale;
    public NameTagsTest() {
        super("NameTagsTest", 0, Category.RENDER);
    }
    public void init() {
        super.init();
        scale  =new NumberSetting("Scale", 1, 0, 10,1);
        addSetting(scale);
    }
}
