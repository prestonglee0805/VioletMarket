package model;

import exception.InvalidEmailException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentUser extends User {

    private final Wallet wallet;
    private final WishList wishList;
    private final List<Listing> listings;
    private final List<Transaction> purchases;
    private boolean isBanned;

    public StudentUser(String name, String nyuEmail, String password)
            throws InvalidEmailException {
        super(name, nyuEmail, password);
        if (!nyuEmail.endsWith("@nyu.edu")) throw new InvalidEmailException(nyuEmail);
        this.wallet    = new Wallet();
        this.wishList  = new WishList(this.userId);
        this.listings  = new ArrayList<>();
        this.purchases = new ArrayList<>();
        this.isBanned  = false;
    }

    @Override
    public String getRole() { return "STUDENT"; }

    // Listings

    public void addListing(Listing listing) {  
        listings.add(listing);  
    }
    public List<Listing> getListings(){  
        return Collections.unmodifiableList(listings);  
    }

    // Wallet / Wishlist getters

    public Wallet getWallet(){  
        return wallet;  
    }
    public WishList getWishList() {  
        return wishList;  
    }

    // Purchases

    public void addPurchase(Transaction tx) {  
        purchases.add(tx);  
    } 

    //arraylist of purchases
    public List<Transaction> getPurchases() {  
        return Collections.unmodifiableList(purchases);  
    }

    // Ban as according to admin
    public boolean isBanned() {  
        return isBanned;  
    }
    public void setBanned(boolean val) { this.isBanned = val; }
}
