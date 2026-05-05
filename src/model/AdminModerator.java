package model;

import exception.InvalidEmailException;

public class AdminModerator extends User {

    public AdminModerator(String name, String nyuEmail, String password)
            throws InvalidEmailException {
        super(name, nyuEmail, password);
        if (!nyuEmail.endsWith("@nyu.edu")) throw new InvalidEmailException(nyuEmail);
    }

    @Override
    public String getRole() { return "ADMIN"; }
}
