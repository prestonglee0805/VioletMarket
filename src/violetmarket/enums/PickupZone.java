package violetmarket.enums;

public enum PickupZone {
    BOBST_LIBRARY("Bobst Library"),
    PAULSON_CENTER("Paulson Center"),
    KIMMEL_CENTER("Kimmel Center"),
    THIRD_NORTH("Third North"),
    LIPTON_HALL("Lipton Hall"),
    BRITTANY_HALL("Brittany Hall"),
    UNIVERSITY_HALL("University Hall"),
    PALLADIUM("Palladium"),
    CARLYLE_COURT("Carlyle Court");

    private final String displayName;

    PickupZone(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
