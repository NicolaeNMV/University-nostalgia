package controllers;

import models.*;

public class Security extends Secure.Security {
    static boolean authentify(String email, String password) {
        return true;
        User user = User.find("email = ?", email).first();
        return user != null && user.password.equals(password);
    }
}