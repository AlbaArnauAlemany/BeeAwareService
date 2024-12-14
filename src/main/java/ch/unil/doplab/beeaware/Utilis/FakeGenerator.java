package ch.unil.doplab.beeaware.Utilis;

import net.datafaker.Faker;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class FakeGenerator {

    private static final List<String> emailDomains = List.of(
            "@gmail.com", "@hotmail.com", "@unil.ch", "@yahoo.com",
            "@outlook.com", "@protonmail.com", "@icloud.com"
    );

    // Constants for password generation
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_CATEGORIES_FOR_VALID_PASSWORD = 3;

    public static final Pattern upperConstraint = Pattern.compile(".*[A-Z].*");
    public static final Pattern lowerConstraint = Pattern.compile(".*[a-z].*");
    public static final Pattern numberConstraint = Pattern.compile(".*[0-9].*");
    public static final Pattern symbolConstraint = Pattern.compile(".*[~`!@#$%^&*()_\\-+={[}]|\\:;\"'<,>.?/].*");

    private final Faker faker = new Faker();

    // Generate Username
    public String generateUsername() {
        Faker randomFaker = new Faker().options().nextElement(Arrays.asList(
                new Faker(Locale.ENGLISH),
                new Faker(Locale.forLanguageTag("nl")),
                new Faker(Locale.forLanguageTag("es"))
        ));

        String firstName = randomFaker.name().firstName(); // Emory
        String lastName = randomFaker.name().lastName(); // Barton

        return firstName.substring(0, 2).toLowerCase() + lastName.toLowerCase(); // bartonem

    }

    // Generate email
    public String generateEmail(String username) {
        Random random = new Random();
        String emailDomain = emailDomains.get(random.nextInt(emailDomains.size()));
        return username + emailDomain;
    }

    // PasswordGenerator
    public String generateValidPassword () {
        Faker faker = new Faker();
        boolean isValid = false;
        int passwordLength = MIN_PASSWORD_LENGTH + new Random().nextInt(MAX_PASSWORD_LENGTH - MIN_PASSWORD_LENGTH + 1);
        StringBuilder password = new StringBuilder(passwordLength);

        while (!isValid) {
            // Ensure at least one uppercase letter
            password.append(faker.letterify("?").toUpperCase());
            // At least one lowercase letter
            password.append(faker.letterify("?").toUpperCase());
            // At least one digit
            password.append(faker.number().digit());
            // At least one special character
            password.append(faker.options().option("!@#$%^&*()-_=+[{]}|;:'\",<.>/?"));

            while (password.length() < passwordLength) {
                password.append(faker.lorem().characters(1));
            }

            String passwordStr = password.toString();
            if (isValidPassword(passwordStr)) {
                isValid = true;
            }
        }
        return password.toString();
    }

    // Validate password with our rules
    public boolean isValidPassword(String password) {
        int categoryCount = 0;

        // Check if password has at least one uppercase letter
        if (upperConstraint.matcher(password).matches()) categoryCount++;
        // Check if password has at least one lowercase letter
        if (lowerConstraint.matcher(password).matches()) categoryCount++;
        // Check if password has at least one number
        if (numberConstraint.matcher(password).matches()) categoryCount++;
        // Check if password has at least one special character
        if (symbolConstraint.matcher(password).matches()) categoryCount++;

        // Password must contain at least 3 different categories
        return categoryCount >= MIN_CATEGORIES_FOR_VALID_PASSWORD
                && password.length() >= MIN_PASSWORD_LENGTH
                && password.length() <= MAX_PASSWORD_LENGTH;
    }

    // Generate Recommendations (quote from Big Bang Theory XD)
    public String generateRecommendations() {
        Faker faker = new Faker();
        return faker.bigBangTheory().quote();
    }
}
