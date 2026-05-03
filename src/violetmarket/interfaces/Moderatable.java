package violetmarket.interfaces;

public interface Moderatable {
    void flagListing(String listingId);
    boolean deleteListing(String listingId);
}
