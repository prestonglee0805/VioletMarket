package model;
import enums.ItemCategory;
import enums.ItemCondition;
import enums.ListingStatus;
import interfaces.Searchable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListingCatalog implements Searchable {
    private final List<Listing> listings;
    private final List<Transaction> transactions;

    public ListingCatalog(){
        this.listings     = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    // Catalog management
    public void addListing(Listing listing){
        listings.add(listing);
    }

    public boolean removeListing(String listingId){
        return listings.removeIf(l -> l.getListingId().equals(listingId));
    }

    public Optional<Listing> findById(String listingId){
        return listings.stream()
            .filter(l -> l.getListingId().equals(listingId))
            .findFirst();
    }

    public List<Listing> getActiveListings(){
        return listings.stream()
            .filter(Listing::isActive)
            .collect(Collectors.toList());
    }

    public List<Listing> getAllListings(){
        return Collections.unmodifiableList(listings);
    }

    // Admin moderation
    public void flagListing(String listingId){
        findById(listingId).ifPresent(l -> l.setStatus(ListingStatus.FLAGGED));
    }

    // Transactions
    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions(){
        return Collections.unmodifiableList(transactions);
    }

    // Searchable

    @Override
    public List<Listing> search(String keyword){
        String kw = keyword.toLowerCase();
        return listings.stream()
            .filter(Listing::isActive)
            .filter(l -> l.getTitle().toLowerCase().contains(kw)
                      || l.getDescription().toLowerCase().contains(kw))
            .collect(Collectors.toList());
    }

    @Override
    public List<Listing> filterByCategory(ItemCategory category){
        return listings.stream()
            .filter(Listing::isActive)
            .filter(l -> l.getCategory() == category)
            .collect(Collectors.toList());
    }

    @Override
    public List<Listing> filterByCondition(ItemCondition condition){
        return listings.stream()
            .filter(Listing::isActive)
            .filter(l -> l.getCondition() == condition)
            .collect(Collectors.toList());
    }

    @Override
    public List<Listing> sortByPrice(boolean ascending){
        Comparator<Listing> byPrice = Comparator.comparingDouble(Listing::getPrice);
        return listings.stream()
            .filter(Listing::isActive)
            .sorted(ascending ? byPrice : byPrice.reversed())
            .collect(Collectors.toList());
    }
}
