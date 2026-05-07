import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password";
        String encoded = encoder.encode(password);
        System.out.println("Password hash for '" + password + "': " + encoded);
    }
}
