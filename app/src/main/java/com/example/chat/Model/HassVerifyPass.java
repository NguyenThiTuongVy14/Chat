package com.example.chat.Model;

import org.mindrot.jbcrypt.BCrypt;

public class HassVerifyPass {
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
