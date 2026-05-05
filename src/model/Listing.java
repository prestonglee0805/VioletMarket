package model;

import enums.ItemCategory;
import enums.ItemCondition;
import enums.ListingStatus;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Listing {

    protected final String listingId;
    protected String title;
    protected String description;
    protected double price;
    protected ItemCondition condition;
    protected ItemCategory category;
    protected final String sellerId;
    protected final String sellerEmail;
    protected final LocalDate datePosted;
    protected LocalDate expiryDate;
    protected ListingStatus status;

    public Listing(String title, String description, double price,
                   ItemCondition condition, ItemCategory category,
                   String sellerId, String sellerEmail) {
        this.listingId   = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.title       = title;
        this.description = description;
        this.price       = price;
        this.condition   = condition;
        this.category    = category;
        this.sellerId    = sellerId;
        this.sellerEmail = sellerEmail;
        this.datePosted  = LocalDate.now();
        this.expiryDate  = LocalDate.now().plusDays(30);
        this.status      = ListingStatus.ACTIVE;
    }

    // Getters

    public String getListingId()    { return listingId; }
    public String getTitle()        { return title; }
    public String getDescription()  { return description; }
    public double getPrice()        { return price; }
    public ItemCondition getCondition() { return condition; }
    public ItemCategory getCategory()   { return category; }
    public String getSellerId()     { return sellerId; }
    public String getSellerEmail()  { return sellerEmail; }
    public LocalDate getDatePosted()  { return datePosted; }
    public LocalDate getExpiryDate()  { return expiryDate; }
    public ListingStatus getStatus()  { return status; }
    public boolean isActive()         { return status == ListingStatus.ACTIVE; }

    // Setters

    public void setTitle(String title)             { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price)             { this.price = price; }
    public void setStatus(ListingStatus status)    { this.status = status; }
    public void setExpiryDate(LocalDate expiryDate){ this.expiryDate = expiryDate; }

    @Override
    public String toString(){
        return String.format("[%s] %s — $%.2f | %s | %s | %s | posted %s",
            listingId, title, price, category, condition, status, datePosted);
    }
}
