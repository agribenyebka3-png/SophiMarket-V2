package fr.sophi.market.jobs;

public enum JobType {
    MINER("Mineur", "⛏"),
    FARMER("Fermier", "🌾"),
    HUNTER("Chasseur", "🏹"),
    ALCHEMIST("Alchimiste", "⚗");

    private final String displayName;
    private final String icon;

    JobType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String displayName() { return displayName; }
    public String icon() { return icon; }
}
