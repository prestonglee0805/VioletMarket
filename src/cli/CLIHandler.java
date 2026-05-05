package cli;

import enums.ItemCategory;
import enums.ItemCondition;
import enums.ListingStatus;
import enums.PickupZone;
import exception.InvalidEmailException;
import exception.InsufficientFundsException;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLIHandler {

    private final Scanner scanner;
    private final ListingCatalog catalog;
    private final List<StudentUser> users;
    private final AdminModerator admin;
    private User currentUser;

    public CLIHandler(){
        this.scanner  = new Scanner(System.in);
        this.catalog  = new ListingCatalog();
        this.users    = new ArrayList<>();
        this.admin    = initAdmin();
        loadData();
    }


    private AdminModerator initAdmin(){
        try {
            return new AdminModerator("Admin", "admin@nyu.edu", "admin123");
        } catch (InvalidEmailException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData(){
        loadUsers();
        loadListings();
    }

    private void loadUsers(){
        try {
            String content = Files.readString(Paths.get("data/users.json"));
            for (String block : content.split("\\}")){
                if (!block.contains("\"email\"")) continue;
                String name     = jsonField(block, "name");
                String email    = jsonField(block, "email");
                String password = jsonField(block, "password");
                String balance  = jsonField(block, "walletBalance");
                if (name.isEmpty() || email.isEmpty()) continue;
                try {
                    StudentUser u = new StudentUser(name, email, password);
                    if (!balance.isEmpty()) u.getWallet().deposit(Double.parseDouble(balance));
                    users.add(u);
                } catch (Exception e){
                    System.out.println("Skipping user: " + e.getMessage());
                }
            }
        } catch (IOException e){
            System.out.println("Warning: could not load data/users.json — " + e.getMessage());
        }
    }

    private void loadListings(){
        try {
            String content = Files.readString(Paths.get("data/listings.json"));
            for (String block : content.split("\\}")){
                if (!block.contains("\"title\"")) continue;
                String title       = jsonField(block, "title");
                String description = jsonField(block, "description");
                String price       = jsonField(block, "price");
                String condition   = jsonField(block, "condition");
                String category    = jsonField(block, "category");
                String sellerEmail = jsonField(block, "sellerEmail");
                String pickupZone  = jsonField(block, "pickupZone");
                String pickupTime  = jsonField(block, "pickupTime");
                if (title.isEmpty() || sellerEmail.isEmpty()) continue;
                try {
                    StudentUser seller = findStudentByEmail(sellerEmail);
                    if (seller == null) continue;
                    PhysicalListing listing = new PhysicalListing(
                        title, description,
                        Double.parseDouble(price),
                        ItemCondition.valueOf(condition),
                        ItemCategory.valueOf(category),
                        seller.getUserId(),
                        seller.getNyuEmail(),
                        PickupZone.valueOf(pickupZone),
                        pickupTime);
                    seller.addListing(listing);
                    catalog.addListing(listing);
                } catch (Exception e){
                    System.out.println("Skipping listing: " + e.getMessage());
                }
            }
        } catch (IOException e){
            System.out.println("Warning: could not load data/listings.json — " + e.getMessage());
        }
    }

    private String jsonField(String block, String field){
        Pattern p = Pattern.compile("\"" + field + "\"\\s*:\\s*\"?([^\"\\n,}]+)\"?");
        Matcher m = p.matcher(block);
        return m.find() ? m.group(1).trim() : "";
    }

    public void run(){
        printBanner();
        while (true){
            if (currentUser == null){
                showAuthMenu();
            } else if (currentUser instanceof AdminModerator){
                showAdminMenu();
            } else {
                showStudentMenu();
            }
        }
    }

    private void printBanner(){
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         V I O L E T M A R K E T          ║");
        System.out.println("║      NYU Student Peer-to-Peer Market     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    //Login Auth

    private void showAuthMenu(){
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> login();
            case "2" -> register();
            case "0" -> { System.out.println("Goodbye!"); System.exit(0); }
            default  -> System.out.println("Invalid option.");
        }
    }

    private void login(){
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (admin.getNyuEmail().equals(email) && admin.checkPassword(password)){
            currentUser = admin;
            System.out.println("Welcome, Admin!");
            return;
        }

        for (StudentUser u : users){
            if (u.getNyuEmail().equals(email) && u.checkPassword(password)){
                if (u.isBanned()){
                    System.out.println("Your account has been banned. Contact admin.");
                    return;
                }
                currentUser = u;
                System.out.println("Welcome back, " + u.getName() + "!");
                return;
            }
        }
        System.out.println("Invalid email or password.");
    }

    private void register(){
        System.out.print("Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("NYU Email (@nyu.edu): ");
        String email = scanner.nextLine().trim();

        for (StudentUser u : users){
            if (u.getNyuEmail().equals(email)){
                System.out.println("Email already registered.");
                return;
            }
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            StudentUser newUser = new StudentUser(name, email, password);
            users.add(newUser);
            currentUser = newUser;
            System.out.println("Account created! Welcome, " + name + ".");
            System.out.println("Your wallet has been provisioned with $0.00.");
        } catch (InvalidEmailException e){
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // student main menu

    private void showStudentMenu(){
        StudentUser student = (StudentUser) currentUser;
        System.out.println("\n--- VioletMarket | " + student.getName()
            + " | Wallet: $" + String.format("%.2f", student.getWallet().getBalance()) + " ---");
        System.out.println("1. Browse Listings");
        System.out.println("2. Post a Listing");
        System.out.println("3. My Listings");
        System.out.println("4. Wallet");
        System.out.println("5. Wishlist");
        System.out.println("6. View Purchases");
        System.out.println("0. Logout");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> browseListings(student);
            case "2" -> postListing(student);
            case "3" -> myListings(student);
            case "4" -> walletMenu(student);
            case "5" -> wishlistMenu(student);
            case "6" -> viewPurchases(student);
            case "0" -> { currentUser = null; System.out.println("Logged out."); }
            default  -> System.out.println("Invalid option.");
        }
    }

    // browse catalog of items

    private void browseListings(StudentUser student){
        System.out.println("\n--- Browse Listings ---");
        System.out.println("1. View All Active Listings");
        System.out.println("2. Search & Filter");
        System.out.println("0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> viewAllActiveListings(student);
            case "2" -> searchMenu(student);
        }
    }

    private void viewAllActiveListings(StudentUser student){
        List<Listing> active = catalog.getActiveListings();
        if (active.isEmpty()){
            System.out.println("No active listings.");
            return;
        }
        System.out.println("\n--- Active Listings (" + active.size() + ") ---");
        for (int i = 0; i < active.size(); i++){
            System.out.println((i + 1) + ". " + active.get(i));
        }
        System.out.print("\nEnter number to view details (0 to go back): ");
        int idx = readInt();
        if (idx < 1 || idx > active.size()) return;
        viewListingDetail(active.get(idx - 1), student);
    }

    private void viewListingDetail(Listing listing, StudentUser student){
        System.out.println("\n--- Listing Detail ---");
        System.out.println("ID:          " + listing.getListingId());
        System.out.println("Title:       " + listing.getTitle());
        System.out.println("Description: " + listing.getDescription());
        System.out.printf ("Price:       $%.2f%n", listing.getPrice());
        System.out.println("Category:    " + listing.getCategory());
        System.out.println("Condition:   " + listing.getCondition());
        System.out.println("Status:      " + listing.getStatus());
        System.out.println("Seller:      " + listing.getSellerEmail());
        System.out.println("Posted:      " + listing.getDatePosted());
        if (listing instanceof PhysicalListing pl){
            System.out.println("Pickup Zone: " + pl.getPickupZone());
            System.out.println("Pickup Time: " + pl.getPickupTime());
        }

        if (listing.getSellerId().equals(student.getUserId())){
            System.out.println("(This is your listing)");
            return;
        }

        System.out.println("\n1. Buy Now");
        System.out.println("2. Add to Wishlist");
        System.out.println("0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> purchaseItem(listing, student);
            case "2" -> {
                boolean added = student.getWishList().addListing(listing);
                System.out.println(added ? "Added to wishlist!" : "Already in your wishlist.");
            }
        }
    }

    //Make a purchase

    private void purchaseItem(Listing listing, StudentUser buyer){
        if (!listing.isActive()){
            System.out.println("This listing is no longer available.");
            return;
        }

        StudentUser seller = findStudentById(listing.getSellerId());
        if (seller == null){
            System.out.println("Seller not found.");
            return;
        }

        System.out.printf("Confirm purchase of \"%s\" for $%.2f? (y/n): ",
            listing.getTitle(), listing.getPrice());
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")){
            System.out.println("Purchase cancelled.");
            return;
        }

        try {
            buyer.getWallet().debit(listing.getPrice());
            seller.getWallet().credit(listing.getPrice());
            listing.setStatus(ListingStatus.SOLD);

            PickupZone zone = (listing instanceof PhysicalListing pl)
                ? pl.getPickupZone()
                : PickupZone.KIMMEL_CENTER;

            Transaction tx = new Transaction(listing, buyer, seller, listing.getPrice(), zone);
            catalog.addTransaction(tx);

            buyer.addPurchase(tx);

            // Remove from every user's wishlist
            for (StudentUser u : users){
                u.getWishList().removeListing(listing.getListingId());
            }

            System.out.println("\nPurchase successful!");
            System.out.println("Transaction ID: " + tx.getTransactionId());
            System.out.printf("$%.2f transferred to %s.%n", listing.getPrice(), seller.getName());
            System.out.println("Arrange pickup at: " + zone);

        } catch (InsufficientFundsException e){
            System.out.println("Purchase failed: " + e.getMessage());
        }
    }

    // Search display

    private void searchMenu(StudentUser student){
        System.out.println("\n--- Search & Filter ---");
        System.out.println("1. Search by keyword");
        System.out.println("2. Filter by category");
        System.out.println("3. Filter by condition");
        System.out.println("4. Sort by price: low to high");
        System.out.println("5. Sort by price: high to low");
        System.out.println("0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();

        List<Listing> results = new ArrayList<>();
        switch (choice){
            case "0" -> { return; }
            case "1" -> {
                System.out.print("Keyword: ");
                results = catalog.search(scanner.nextLine().trim());
            }
            case "2" -> {
                ItemCategory cat = selectCategory();
                if (cat == null) return;
                results = catalog.filterByCategory(cat);
            }
            case "3" -> {
                ItemCondition cond = selectCondition();
                if (cond == null) return;
                results = catalog.filterByCondition(cond);
            }
            case "4" -> results = catalog.sortByPrice(true);
            case "5" -> results = catalog.sortByPrice(false);
            default  -> { System.out.println("Invalid option."); return; }
        }

        if (results.isEmpty()){
            System.out.println("No results found.");
            return;
        }
        System.out.println("\n--- Results (" + results.size() + ") ---");
        for (int i = 0; i < results.size(); i++){
            System.out.println((i + 1) + ". " + results.get(i));
        }
        System.out.print("\nEnter number to view details (0 to go back): ");
        int idx = readInt();
        if (idx < 1 || idx > results.size()) return;
        viewListingDetail(results.get(idx - 1), student);
    }

    // Posting a listing

    private void postListing(StudentUser student){
        System.out.println("\n--- Post a Listing (enter 0 at any prompt to cancel) ---");
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        if (title.equals("0")) { System.out.println("Cancelled."); return; }
        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Price (0 for free): $");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e){
            System.out.println("Invalid price.");
            return;
        }

        ItemCategory category = selectCategory();
        if (category == null) return;

        ItemCondition condition = selectCondition();
        if (condition == null) return;

        PickupZone zone = selectPickupZone();
        if (zone == null) return;

        System.out.print("Preferred Pickup Time: ");
        String pickupTime = scanner.nextLine().trim();

        PhysicalListing listing = new PhysicalListing(
            title, desc, price, condition, category,
            student.getUserId(), student.getNyuEmail(), zone, pickupTime);
        student.addListing(listing);
        catalog.addListing(listing);

        System.out.println("\nListing posted! ID: " + listing.getListingId());
    }

    // My Listings

    private void myListings(StudentUser student){
        List<Listing> mine = student.getListings();
        if (mine.isEmpty()){
            System.out.println("You have no listings.");
            return;
        }
        System.out.println("\n--- My Listings ---");
        for (int i = 0; i < mine.size(); i++){
            System.out.println((i + 1) + ". " + mine.get(i));
        }
        System.out.print("\nEnter number to manage (0 to go back): ");
        int idx = readInt();
        if (idx < 1 || idx > mine.size()) return;
        manageMyListing(mine.get(idx - 1));
    }

    private void manageMyListing(Listing listing){
        System.out.println("\n" + listing);
        System.out.println("1. Edit Title");
        System.out.println("2. Edit Description");
        System.out.println("3. Edit Price");
        System.out.println("4. Mark as Sold");
        System.out.println("5. Remove Listing");
        System.out.println("0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> {
                System.out.print("New title: ");
                String newTitle = scanner.nextLine().trim();
                if (!newTitle.isEmpty()){
                    listing.setTitle(newTitle);
                    System.out.println("Title updated.");
                }
            }
            case "2" -> {
                System.out.print("New description: ");
                String newDesc = scanner.nextLine().trim();
                if (!newDesc.isEmpty()){
                    listing.setDescription(newDesc);
                    System.out.println("Description updated.");
                }
            }
            case "3" -> {
                System.out.print("New price: $");
                try {
                    double newPrice = Double.parseDouble(scanner.nextLine().trim());
                    listing.setPrice(newPrice);
                    System.out.println("Price updated to $" + String.format("%.2f", newPrice));
                } catch (NumberFormatException e){
                    System.out.println("Invalid price.");
                }
            }
            case "4" -> {
                listing.setStatus(ListingStatus.SOLD);
                System.out.println("Listing marked as sold.");
            }
            case "5" -> {
                catalog.removeListing(listing.getListingId());
                System.out.println("Listing removed.");
            }
        }
    }

    // Wallet

    private void walletMenu(StudentUser student){
        System.out.printf("%n--- Wallet | Balance: $%.2f ---%n",
            student.getWallet().getBalance());
        System.out.println("1. Deposit Funds");
        System.out.println("2. View Transaction History");
        System.out.println("0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> {
                System.out.print("Amount: $");
                try {
                    double amount = Double.parseDouble(scanner.nextLine().trim());
                    student.getWallet().deposit(amount);
                    System.out.printf("Deposited $%.2f. New balance: $%.2f%n",
                        amount, student.getWallet().getBalance());
                } catch (NumberFormatException e){
                    System.out.println("Invalid amount.");
                } catch (IllegalArgumentException e){
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case "2" -> {
                List<String> history = student.getWallet().getTransactionHistory();
                if (history.isEmpty()){
                    System.out.println("No transactions yet.");
                } else {
                    System.out.println("\n--- Transaction History ---");
                    history.forEach(System.out::println);
                }
            }
        }
    }

    // Wishlist

    private void wishlistMenu(StudentUser student){
        List<Listing> saved = student.getWishList().getSavedListings();
        System.out.println("\n--- Wishlist (" + saved.size() + " items) ---");
        if (saved.isEmpty()){
            System.out.println("Your wishlist is empty.");
            return;
        }
        for (int i = 0; i < saved.size(); i++){
            Listing l = saved.get(i);
            String tag = l.isActive() ? "" : " [" + l.getStatus() + "]";
            System.out.println((i + 1) + ". " + l + tag);
        }
        System.out.print("\nEnter number to act on (0 to go back): ");
        int idx = readInt();
        if (idx < 1 || idx > saved.size()) return;

        Listing listing = saved.get(idx - 1);
        System.out.println("1. Buy Now");
        System.out.println("2. Remove from Wishlist");
        System.out.println("0. Back");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> {
                if (!listing.isActive()){
                    System.out.println("This item is no longer available (" + listing.getStatus() + ").");
                } else {
                    purchaseItem(listing, student);
                }
            }
            case "2" -> {
                student.getWishList().removeListing(listing.getListingId());
                System.out.println("Removed from wishlist.");
            }
        }
    }

    //View purchases functionality

    private void viewPurchases(StudentUser student){
        List<Transaction> purchases = student.getPurchases();
        System.out.println("\n--- My Purchases (" + purchases.size() + ") ---");
        if (purchases.isEmpty()){
            System.out.println("You have not purchased anything yet.");
            return;
        }
        for (int i = 0; i < purchases.size(); i++){
            Transaction tx = purchases.get(i);
            System.out.printf("%d. [%s] \"%s\" — $%.2f | Seller: %s | Pickup: %s | %s%n",
                i + 1,
                tx.getTransactionId(),
                tx.getListingRef().getTitle(),
                tx.getAgreedPrice(),
                tx.getListingRef().getSellerEmail(),
                tx.getPickupZone(),
                tx.getCompletedAt().toLocalDate());
        }
    }

    // ── Admin Menu ───────────────────────────────────────────────────────────

    private void showAdminMenu(){
        System.out.println("\n--- ADMIN PANEL ---");
        System.out.println("1. Delete a Listing");
        System.out.println("2. Remove a User");
        System.out.println("0. Logout");
        System.out.print("> ");
        String choice = scanner.nextLine().trim();
        switch (choice){
            case "1" -> adminDeleteListing();
            case "2" -> adminRemoveUser();
            case "0" -> { currentUser = null; System.out.println("Admin logged out."); }
            default  -> System.out.println("Invalid option.");
        }
    }

    private void adminDeleteListing(){
        List<Listing> all = catalog.getAllListings();
        if (all.isEmpty()){ System.out.println("No listings."); return; }
        System.out.println("\n--- All Listings (" + all.size() + ") ---");
        all.forEach(System.out::println);
        System.out.print("\nListing ID to delete (0 to cancel): ");
        String id = scanner.nextLine().trim().toUpperCase();
        if (id.equals("0")) return;
        // Also remove from every user's wishlist before deleting
        for (StudentUser u : users){
            u.getWishList().removeListing(id);
        }
        boolean removed = catalog.removeListing(id);
        System.out.println(removed ? "Listing " + id + " deleted." : "Listing not found.");
    }

    private void adminRemoveUser(){
        if (users.isEmpty()){ System.out.println("No users."); return; }
        System.out.println("\n--- All Users (" + users.size() + ") ---");
        users.forEach(System.out::println);
        System.out.print("\nUser email to remove (0 to cancel): ");
        String email = scanner.nextLine().trim();
        if (email.equals("0")) return;
        StudentUser target = findStudentByEmail(email);
        if (target == null){ System.out.println("User not found."); return; }
        // Remove all their listings from every wishlist, then from catalog
        for (Listing l : target.getListings()){
            for (StudentUser u : users){
                u.getWishList().removeListing(l.getListingId());
            }
            catalog.removeListing(l.getListingId());
        }
        users.remove(target);
        System.out.println(target.getName() + " (" + target.getNyuEmail() + ") removed.");
    }

    //for enum

    private ItemCategory selectCategory(){
        ItemCategory[] values = ItemCategory.values();
        System.out.println("Select category:");
        for (int i = 0; i < values.length; i++){
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        System.out.println("  0. Cancel");
        System.out.print("> ");
        int idx = readInt();
        if (idx == 0){ System.out.println("Cancelled."); return null; }
        if (idx < 1 || idx > values.length){ System.out.println("Invalid selection."); return null; }
        return values[idx - 1];
    }

    private ItemCondition selectCondition(){
        ItemCondition[] values = ItemCondition.values();
        System.out.println("Select condition:");
        for (int i = 0; i < values.length; i++){
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        System.out.println("  0. Cancel");
        System.out.print("> ");
        int idx = readInt();
        if (idx == 0){ System.out.println("Cancelled."); return null; }
        if (idx < 1 || idx > values.length){ System.out.println("Invalid selection."); return null; }
        return values[idx - 1];
    }

    private PickupZone selectPickupZone(){
        PickupZone[] values = PickupZone.values();
        System.out.println("Select pickup zone:");
        for (int i = 0; i < values.length; i++){
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        System.out.println("  0. Cancel");
        System.out.print("> ");
        int idx = readInt();
        if (idx == 0){ System.out.println("Cancelled."); return null; }
        if (idx < 1 || idx > values.length){ System.out.println("Invalid selection."); return null; }
        return values[idx - 1];
    }

    // helper functions
    private StudentUser findStudentById(String userId){
        return users.stream()
            .filter(u -> u.getUserId().equals(userId))
            .findFirst().orElse(null);
    }

    private StudentUser findStudentByEmail(String email){
        return users.stream()
            .filter(u -> u.getNyuEmail().equals(email))
            .findFirst().orElse(null);
    }

    private int readInt(){
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e){
            return -1;
        }
    }
}
