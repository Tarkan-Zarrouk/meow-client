package Main.main.meow.client.ui.clickgui;

public enum Category {
    VISUALS("Visuals"),
    MISC("Misc"),
    PLAYER("Player"),
    CLIENT("Client"),
    MOVEMENT("Movement"),
    CONFIGS("Configs"),
    HUD("Hud");

    private final String title;

    Category(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
