package org.slackcoder.twilight.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.lang.Nullable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Data
@NoArgsConstructor
@Node("User")
public class User {
    @Id
    private UUID userId;  // 用户唯一标识

    private String username;  // 用户名

    @Nullable
    private String email;  // 邮箱（可为空）

    private String password;  //SHA-256

    private int userType;  //0=student, 1=teacher, 2=admin

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
            byte[] encodedHash = digest.digest(password.getBytes());
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
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }
}
