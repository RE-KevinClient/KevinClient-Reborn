package kevin.persional.milk.guis.clickgui.buttons;

import kevin.persional.milk.guis.font.FontLoaders;
import kevin.persional.milk.utils.key.ClickUtils;
import kevin.persional.milk.utils.render.anims.AnimationUtils;
import kevin.module.FloatValue;
import kevin.utils.RenderUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class FloatButton extends Button {
    public FloatValue value;
    double valueAnim = 0;
    AnimationUtils animationUtils = new AnimationUtils();
    public FloatButton(FloatValue value){
        this.value = value;
    }

    @Override
    public void drawButton(int x, int y, int mx, int my, float pticks, float alpha) {
        int intalpha = (int)(alpha * 255);
        FontLoaders.novo20.drawString(value.getName() + ":", x, y, new Color(255, 255, 255, intalpha).getRGB());
        int dx = x + 195;
        int tx = x + 295;
        int cx = (int) (100 * ((value.get() - value.getMinimum()) / (value.getMaximum() - value.getMinimum())));
        valueAnim = animationUtils.animate(cx, valueAnim, 0.2);
        RenderUtils.drawRect(dx, y + 3, tx, y + 4, new Color(0, 166, 255, intalpha).getRGB());
        FontLoaders.novo18.drawString(String.format("%.2f", value.get()), tx + 7, y, -1);
        RenderUtils.drawSector(dx + valueAnim, y + 4, 0, 360, 5, new Color(49, 49, 49, intalpha));
        RenderUtils.drawSector(dx + valueAnim, y + 4, 0, 360, 4, new Color(200, 200, 200, intalpha));

        if(ClickUtils.isClickable(dx, y + 3 - 2, tx, y + 5 + 2, mx, my) && Mouse.isButtonDown(0)){
            float width = tx - dx;
            float pp = (mx - dx) / width;
            pp = Math.max(0, pp);
            pp = Math.min(1, pp);
            float mmx = value.getMinimum() + pp * (value.getMaximum() - value.getMinimum());
            value.set(mmx);
        }
        super.drawButton(x, y, mx, my, pticks, alpha);
    }

    @Override
    public void clickButton(int x, int y, int mx, int my) {
        super.clickButton(x, y, mx, my);
    }
}
