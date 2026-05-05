package model;

import exception.InvalidEmailException;
import interfaces.Moderatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminModerator extends User implements Moderatable {

    private final List<String> flaggedListingIds;
    private boolean moveOutModeActive;

    public AdminModerator(String name, String nyuEmail, String password)
            throws InvalidEmailException {
        super(name, nyuEmail, password);
        if (!nyuEmail.endsWith("@nyu.edu")) throw new InvalidEmailException(nyuEmail);
        this.flaggedListingIds  = new ArrayList<>();
        this.moveOutModeActive  = false;
    }

    @Override
    public String getRole() { return "ADMIN"; }


    @Override
    public void flagListing(String listingId) {
        if (!flaggedListingIds.contains(listingId)) flaggedListingIds.add(listingId);
    }

    @Override
    public boolean deleteListing(String listingId) {
        flaggedListingIds.remove(listingId);
        return true;
    }


    public void banUser(StudentUser user){  
        user.setBanned(true);  
    } 

    public void unbanUser(StudentUser user){  
        user.setBanned(false); 
    }

    public boolean isMoveOutModeActive() {  
        return moveOutModeActive;  
    }

    public void setMoveOutModeActive(boolean val){  
        this.moveOutModeActive = val;  
    }

    //Getter methods

    public List<String> getFlaggedListingIds() {
        return Collections.unmodifiableList(flaggedListingIds);
    }
}
