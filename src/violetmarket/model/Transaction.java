package violetmarket.model;

import violetmarket.enums.PickupZone;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private final String transactionId;
    private final Listing listingRef;
    private final StudentUser buyerRef;
    private final StudentUser sellerRef;
    private final double agreedPrice;
    private final PickupZone pickupZone;
    private final LocalDateTime completedAt;

    public Transaction(Listing listingRef, StudentUser buyerRef, StudentUser sellerRef,
                       double agreedPrice, PickupZone pickupZone) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.listingRef    = listingRef;
        this.buyerRef      = buyerRef;
        this.sellerRef     = sellerRef;
        this.agreedPrice   = agreedPrice;
        this.pickupZone    = pickupZone;
        this.completedAt   = LocalDateTime.now();
    }

    // Getters

    public String getTransactionId(){
        return transactionId;
    }

    public Listing getListingRef(){
        return listingRef;
    }

    public StudentUser getBuyerRef(){
        return buyerRef;
    }

    public StudentUser getSellerRef(){
        return sellerRef;
    }

    public double getAgreedPrice(){
        return agreedPrice;
    }

    public PickupZone getPickupZone(){
        return pickupZone;
    }

    public LocalDateTime getCompletedAt(){
        return completedAt;
    }

    @Override
    public String toString(){
        return String.format(
            "Transaction[%s] '%s' — buyer: %s, seller: %s, price: $%.2f, pickup: %s, date: %s",
            transactionId, listingRef.getTitle(),
            buyerRef.getName(), sellerRef.getName(),
            agreedPrice, pickupZone, completedAt.toLocalDate());
    }
}
