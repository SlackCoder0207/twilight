package org.slackcoder.twilight.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Data
public class User {
    @Id
    private UUID userId;
    private String username;
    private String email;
    private String password;
    private int userType;

    public User(String username, String email, String password, int userType) {
        this.userId = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = hashPassword(password);
        this.userType = userType;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256加密异常", e);
        }
    }
}
