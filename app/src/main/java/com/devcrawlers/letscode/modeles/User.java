package com.devcrawlers.letscode.modeles;


import com.devcrawlers.letscode.Provider;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    int id=0;
    String fullname = "";
    String username = "";
    String password = "";
    String email = "";
    String image = "";
    String role = "user"; // admin
    String provider = Provider.guest.name();

    public static User guest() {
        User user = new User(-1,
                "Guest",
                "Guest",
                "null",
                "null",
                "",
                "guest",
                Provider.guest.name());
        return user;
    }


    public boolean isGuest() {
        return role.equals("guest");
    }

    public boolean isAdmin() {
        return role.equals("admin");
    }
}
