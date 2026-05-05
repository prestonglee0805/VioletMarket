package model;

import enums.ItemCategory;
import enums.ItemCondition;
import enums.PickupZone;

public class PhysicalListing extends Listing {

    private PickupZone pickupZone;
    private String pickupTime;

    public PhysicalListing(String title, String description, double price, ItemCondition condition, ItemCategory category, String sellerId, String sellerEmail, PickupZone pickupZone, String pickupTime) {
        super(title, description, price, condition, category, sellerId, sellerEmail);
        this.pickupZone = pickupZone;
        this.pickupTime = pickupTime;
    }

    // Getters

    public PickupZone getPickupZone() {  
        return pickupZone;  
    }
    public String getPickupTime() {  
        return pickupTime;  
    }

    // Setters

    public void setPickupZone(PickupZone zone)    { this.pickupZone = zone; }
    public void setPickupTime(String pickupTime)  { this.pickupTime = pickupTime; }

    @Override
    public String toString(){
        return String.format("[%s] %s — $%.2f | %s | %s | %s | pickup: %s (%s) | posted %s",
            listingId, title, price, category, condition, status,
            pickupZone, pickupTime, datePosted);
    }
}
