package model;

import exception.InvalidEmailException;
import interfaces.Ratable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentUser extends User implements Ratable {

    private final Wallet wallet;
    private final WishList wishList;
    private final List<Listing> listings;
    private final List<Rating> ratings;
    private boolean isBanned;

    public StudentUser(String name, String nyuEmail, String password)
            throws InvalidEmailException {
        super(name, nyuEmail, password);
        if (!nyuEmail.endsWith("@nyu.edu")) throw new InvalidEmailException(nyuEmail);
        this.wallet = new Wallet();
        this.wishList = new WishList(this.userId);
        this.listings = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.isBanned = false;
    }

    // Role

    @Override
    public String getRole(){
        return "STUDENT";
    }

    // Listings

    public void addListing(Listing listing){
        listings.add(listing);
    }

    public List<Listing> getListings(){
        return Collections.unmodifiableList(listings);
    }

    // Ratable

    @Override
    public void addRating(double score, String comment, String reviewerId){
        ratings.add(new Rating(reviewerId, score, comment));
    }

    @Override
    public double getAverageRating(){
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToDouble(Rating::getScore).average().orElse(0.0);
    }

    @Override
    public int getRatingCount(){
        return ratings.size();
    }

    public List<Rating> getRatings(){
        return Collections.unmodifiableList(ratings);
    }

    // Wallet / Wishlist

    public Wallet getWallet(){
        return wallet;
    }

    public WishList getWishList(){
        return wishList;
    }

    // Ban

    public boolean isBanned(){
        return isBanned;
    }

    public void setBanned(boolean val){
        this.isBanned = val;
    }
}
