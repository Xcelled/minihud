package fi.dy.masa.minihud.gui.widget;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.IntegerTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;
import fi.dy.masa.malilib.gui.widget.button.BooleanConfigButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.config.BaseConfigWidget;
import fi.dy.masa.minihud.config.InfoLine;

public class InfoLineConfigWidget extends BaseConfigWidget<InfoLine>
{
    protected final BaseTextFieldWidget textField;
    protected final BooleanConfigButton booleanButton;
    protected final KeyBindConfigButton hotkeyButton;
    protected final KeybindSettingsWidget settingsWidget;

    protected final IntArrayList initialHotkeyValue = new IntArrayList();
    protected final boolean initialBooleanValue;
    protected final int initialLineOrder;
    protected final String initialLineOrderStringValue;

    public InfoLineConfigWidget(InfoLine config,
                                DataListEntryWidgetData constructData,
                                ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.initialBooleanValue = this.config.getBooleanValue();
        this.initialLineOrder = this.config.getLineOrder();
        this.initialLineOrderStringValue = String.valueOf(this.initialLineOrder);
        this.config.getKeyBind().getKeysToList(this.initialHotkeyValue);

        this.textField = new BaseTextFieldWidget(24, 16);
        this.textField.setTextValidator(new IntegerTextFieldWidget.IntValidator(this.config.getLineOrderConfig().getMinIntegerValue(),
                                                                                this.config.getLineOrderConfig().getMaxIntegerValue()));
        this.textField.setListener((str) -> {
            this.config.getLineOrderConfig().setValueFromString(str);
            this.updateWidgetState();
        });

        this.booleanButton = new BooleanConfigButton(-1, 20, config.getBooleanConfig());
        this.booleanButton.setActionListener(() -> {
            this.config.getBooleanConfig().toggleBooleanValue();
            this.updateWidgetState();
        });

        this.hotkeyButton = new KeyBindConfigButton(120, 20, config.getKeyBind(), ctx.getKeybindEditingScreen());
        this.hotkeyButton.setValueChangeListener(this::updateWidgetState);

        this.settingsWidget = new KeybindSettingsWidget(config.getKeyBind(), config.getDisplayName());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.textField);
        this.addWidget(this.booleanButton);
        this.addWidget(this.hotkeyButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int y = this.getY() + 1;
        int elementWidth = this.getElementWidth();

        int w = elementWidth - this.booleanButton.getWidth() - 24 - 20 - 6;
        this.hotkeyButton.setWidth(w);

        this.textField.setPosition(this.getElementsStartPosition(), y + 2);
        this.booleanButton.setPosition(this.textField.getRight() + 2, y);
        this.hotkeyButton.setPosition(this.booleanButton.getRight() + 2, y);
        this.settingsWidget.setPosition(this.hotkeyButton.getRight() + 2, y);
        this.resetButton.setPosition(this.settingsWidget.getRight() + 4, y);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.textField.setText(String.valueOf(this.config.getLineOrderConfig().getIntegerValue()));
        this.booleanButton.setEnabled(this.config.getBooleanConfig().isLocked() == false);
        this.booleanButton.updateButtonState();

        this.hotkeyButton.updateButtonState();
    }

    @Override
    public void onAboutToDestroy()
    {
        String text = this.textField.getText();

        if (text.equals(this.initialLineOrderStringValue) == false)
        {
            this.config.getLineOrderConfig().setValueFromString(text);
        }
    }

    @Override
    protected boolean isResetEnabled()
    {
        return this.config.isModified() && this.config.getBooleanConfig().isLocked() == false;
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getBooleanValue() != this.initialBooleanValue ||
               this.config.getLineOrder() != this.initialLineOrder ||
               this.config.getKeyBind().matches(this.initialHotkeyValue) == false;
    }
}
