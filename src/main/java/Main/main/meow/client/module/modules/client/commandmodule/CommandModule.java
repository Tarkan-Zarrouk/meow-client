package Main.main.meow.client.module.modules.client.commandmodule;

import Main.main.meow.client.module.modules.ClientModule;
import Main.main.meow.client.module.modules.client.commandmodule.modes.NotifyModes;
import Main.main.meow.client.module.modules.client.commandmodule.modes.prefix.PrefixColor;
import Main.main.meow.client.module.modules.client.commandmodule.modes.prefix.PrefixFormatting;
import Main.main.meow.client.ui.clickgui.model.Category;
import Main.main.meow.client.ui.clickgui.view.setting.modulesettings.EnumSetting;

public class CommandModule extends ClientModule {
    private static final String NAME = "CommandSetting";
    private static final Category CATEGORY = Category.CLIENT;
    // note that we set it to toggable due to the frequency of the default command "*" being used. (cough, rusherhack, cough)
    private static final boolean ENABLED = true;
    private static final String DESCRIPTION = "Customize the command setting";
    private static final EnumSetting<NotifyModes> NOTIFICATION_MODE = new EnumSetting<>("Alert Notification", "Change between alerting the command message in chat and in the screen via notify", NotifyModes.CHAT);
    private static final EnumSetting<PrefixColor> PREFIX_COLOR = new EnumSetting<>("Prefix Color", "Change between the ChatFormatting color schemes (its' a lot)", PrefixColor.AQUA);
    private static final EnumSetting<PrefixFormatting> PREFIX_FORMATTING = new EnumSetting<>("Prefix Format", "Swap between ChatFormatting formats (it's a lot)", PrefixFormatting.RESET);

    public CommandModule() {
        super(NAME, CATEGORY, ENABLED, DESCRIPTION);
        this.addSetting(NOTIFICATION_MODE, PREFIX_COLOR, PREFIX_FORMATTING);
    }
    public static PrefixColor getPrefixColor() {
        return PREFIX_COLOR.value();
    }
    public static PrefixFormatting getPrefixFormatting() {
        return PREFIX_FORMATTING.value();
    }

    public static NotifyModes getNotificationMode() {
        return NOTIFICATION_MODE.value();
    }
}
