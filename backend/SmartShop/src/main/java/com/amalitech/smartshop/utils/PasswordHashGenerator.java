package com.amalitech.smartshop.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to generate BCrypt password hashes.
 * Run this to get hashes for updating vendor passwords.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        String password = "vendor123";
        
        // Generate two different hashes for the two vendors
        String hash1 = BCrypt.hashpw(password, BCrypt.gensalt());
        String hash2 = BCrypt.hashpw(password, BCrypt.gensalt());
        
        System.out.println("Hash 1 for vendor@smartshop.com:");
        System.out.println(hash1);
        System.out.println();
        System.out.println("Hash 2 for robert.wilson@smartshop.com:");
        System.out.println(hash2);
        System.out.println();
        System.out.println("SQL Update Commands:");
        System.out.println("UPDATE users SET password = '" + hash1 + "' WHERE email = 'vendor@smartshop.com';");
        System.out.println("UPDATE users SET password = '" + hash2 + "' WHERE email = 'robert.wilson@smartshop.com';");
    }
}
