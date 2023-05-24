package dev.worldgen.stranded.screen;

import dev.worldgen.stranded.config.ConfigHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;


public class CustomizeStrandedLevelScreen extends Screen {
    private ButtonListWidget list;
    private final Screen parent;
    private ButtonWidget confirmButton;
    public CustomizeStrandedLevelScreen(Screen parent, GeneratorOptionsHolder generatorOptionsHolder) {
        super(Text.translatable("config.stranded.title"));
        this.parent = parent;
    }

    protected void init() {
        Map<String, Object> TEMP_CONFIG_VALUES = new HashMap<>();
        list = new ButtonListWidget(client, width, height, 32, height-32, 25 );
        list.addSingleOptionEntry(
                new SimpleOption("config.stranded.island_size", SimpleOption.emptyTooltip(), (optionText, value) -> {
                    return Text.translatable("options.generic_value", optionText, Text.translatable("config.stranded.island_size_value", new Object[]{value}));
                }, new SimpleOption.ValidatingIntSliderCallbacks(1, 100), ConfigHandler.getConfigValue("island_size"), (value) -> {
                    TEMP_CONFIG_VALUES.put("island_size", value);
                })
        );
        list.addSingleOptionEntry(
                SimpleOption.ofBoolean(
                        "config.stranded.compatible_mode",
                        SimpleOption.constantTooltip(Text.translatable("config.stranded.compatible_mode.tooltip")),
                        (Boolean) ConfigHandler.getConfigValue("compatible_mode"),
                        (value) -> TEMP_CONFIG_VALUES.put("compatible_mode", value)
                )
        );
        addSelectableChild(list);
        this.confirmButton = this.addDrawableChild(
                new ButtonWidget(
                        width/2 - 155,
                        height - 28,
                        150, 20,
                        ScreenTexts.DONE,
                        (button) -> {
                            for (Map.Entry<String, Object> tempConfigValues : TEMP_CONFIG_VALUES.entrySet()) {
                                ConfigHandler.setConfigValue(tempConfigValues.getKey(), tempConfigValues.getValue());
                            }
                            this.client.setScreen(this.parent);
                        }
                )
        );
        this.confirmButton = this.addDrawableChild(
                new ButtonWidget(
                        width/2 + 5,
                        height - 28,
                        150, 20,
                        ScreenTexts.CANCEL,
                        (button) -> this.client.setScreen(this.parent)
                )
        );
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        list.render(matrices, mouseX, mouseY, delta);
        drawCenteredTextWithShadow(matrices, textRenderer, title.asOrderedText(), width / 2, 5, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
