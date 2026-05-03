package violetmarket.enums;

public enum ItemCategory {
    TEXTBOOKS,
    DORM_FURNITURE,
    ELECTRONICS,
    CLOTHING,
    BIKES_SCOOTERS,
    FREE_ITEMS;

    @Override
    public String toString() {
        return name().replace('_', ' ');
    }
}
