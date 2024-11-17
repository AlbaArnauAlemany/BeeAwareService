package ch.unil.doplab.beeaware.domain;

import ch.unil.doplab.beeaware.Domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    private Token token;
    private String key;
    private Date expiration;
    private Long beezzerId;
    private Role role;

    @BeforeEach
    void setUp() {
        key = "testTokenKey";
        expiration = new Date(System.currentTimeMillis() + 3600000);
        beezzerId = 12345L;
        role = Role.BEEZZER;

        token = new Token(key, expiration, beezzerId, role);
    }

    @Test
    void testConstructorWithArguments() {
        assertEquals(key, token.getKey());
        assertEquals(expiration, token.getExpiration());
        assertEquals(beezzerId, token.getBeezzerId());
        assertEquals(role, token.getRole());
    }

    @Test
    void testNoArgsConstructor() {
        Token emptyToken = new Token();
        assertNull(emptyToken.getKey());
        assertNull(emptyToken.getExpiration());
        assertNull(emptyToken.getBeezzerId());
        assertNull(emptyToken.getRole());
    }

    @Test
    void testSettersAndGetters() {
        String newKey = "newTokenKey";
        Date newExpiration = new Date(System.currentTimeMillis() + 7200000);
        Long newBeezzerId = 67890L;
        Role newRole = Role.ADMIN;

        token.setKey(newKey);
        token.setExpiration(newExpiration);
        token.setBeezzerId(newBeezzerId);
        token.setRole(newRole);

        assertEquals(newKey, token.getKey());
        assertEquals(newExpiration, token.getExpiration());
        assertEquals(newBeezzerId, token.getBeezzerId());
        assertEquals(newRole, token.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        Token anotherToken = new Token(key, expiration, beezzerId, role);
        assertEquals(token, anotherToken);
        assertEquals(token.hashCode(), anotherToken.hashCode());
    }

    @Test
    void testNotEquals() {
        Token differentToken = new Token("differentKey", expiration, beezzerId, role);
        assertNotEquals(token, differentToken);
    }

    @Test
    void testToString() {
        String expected = "Token(key=" + key + ", expiration=" + expiration + ", beezzerId=" + beezzerId + ", role=" + role + ")";
        assertEquals(expected, token.toString());
    }

    @Test
    void testExpirationDateIsValid() {
        Date pastDate = new Date(System.currentTimeMillis() - 1000); // Une date passée
        token.setExpiration(pastDate);

        assertTrue(token.getExpiration().before(new Date()), "Token expiration date should be in the past.");
    }
}
