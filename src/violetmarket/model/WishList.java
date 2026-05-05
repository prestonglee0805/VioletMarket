package violetmarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WishList {

    private final String ownerId;
    private final List<Listing> savedListings;

    public WishList(String ownerId){
        this.ownerId       = ownerId;
        this.savedListings = new ArrayList<>();
    }

    // Operations

    public boolean addListing(Listing listing){
        boolean alreadySaved = savedListings.stream().anyMatch(l -> l.getListingId().equals(listing.getListingId()));
        if (alreadySaved){ 
            return false; 
        }
        savedListings.add(listing);
        return true;
    }

    public boolean removeListing(String listingId){
        return savedListings.removeIf(l -> l.getListingId().equals(listingId));
    }

    public boolean contains(String listingId){
        return savedListings.stream().anyMatch(l -> l.getListingId().equals(listingId));
    }

    // Getters
    public String getOwnerId(){
        return ownerId;
    }

    public List<Listing> getSavedListings(){
        return Collections.unmodifiableList(savedListings);
    }

    public int size(){
        return savedListings.size();
    }
}
