package com.kt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;

/**
 * @author Todor Pitekov
 * @author Todor Pitekov
 *
 * The Credentials class groups methods for password and username validation as well as generating a password salt
 * and salting a password
 */

public class Credentials
{
    public static final short MIN_NAME_LENGTH = 3;
    public static final short MAX_NAME_LENGTH = 50;
    public static final short MIN_PASS_LENGTH = 3;
    public static final short MAX_PASS_LENGTH = 50;

    private static SecureRandom csprng = new SecureRandom();

	/**
	 * Checks whether the specified username is valid
	 * @param name A {@code String} to check against
	 * @return True if the name is a valid username, false if not
	 */
    public static boolean isNameValid(String name)
    {
        int length = name.trim().length();
        return (length >= MIN_NAME_LENGTH && length <= MAX_NAME_LENGTH);
    }

	/**
	 * Checks whether the specified password is valid
	 * @param pass A {@code String} to check against
	 * @return True if the password is a valid password, false if not
	 */
    public static boolean isPassValid(String pass)
    {
        int length = pass.length();
        return (length >= MIN_PASS_LENGTH && length <= MAX_PASS_LENGTH);
    }

	/**
	 * Intelligently performs an online search for the user with the specified username, performs
	 * complex text analyses and applies algorithms for extracting context to finally determine if the user
	 * is an asshole
	 * @param name A {@code String} to check against
	 * @return True if the user has been an asshole to other people, false otherwise
	 */
    public static boolean isBadUser(String name)
    {
        String lowercase = name.toLowerCase();
        return (name.contains("andonov") || name.contains("filip") || name.contains("felipe") || name.contains("fil") ||
                name.contains("андонов") || name.contains("филип") || name.contains("фелипе") || name.contains("фил"));
    }

	/**
	 * Prepends the specified salt to the specified text and hashes it using sha-256
	 * @param text A {@code String} to salt
	 * @param salt A {@code int} for salt
	 * @return The resulting sha-256 digest in hex representation
	 * @throws NoSuchAlgorithmException
	 */
    public static String saltAndHash(String text, int salt) throws NoSuchAlgorithmException
    {
        String salted = salt + text;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte converted[] = digest.digest(salted.getBytes());

        Formatter formatter = new Formatter();

        for (byte b : converted)
        {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

	/**
	 * Generates a secure random number to be used as salt
	 * @return An {@code int}
	 * @throws NoSuchAlgorithmException
	 */
    public static int generateSalt() throws NoSuchAlgorithmException
    {
        return csprng.nextInt(Integer.MAX_VALUE);
    }
}

/**
 * The AuthPair class represents a password hash and its corresponding salt
 */
class AuthPair
{
    private String hash = null;
    private int salt;

	/**
	 * Initializes a newly created {@code AuthPair} instance with a hash and its corresponding salt
	 * @param hash A {@code String}
	 * @param salt An {@code int}
	 */
    AuthPair(String hash, int salt)
    {
        this.hash = hash;
        this.salt = salt;
    }

	/**
	 * Salts and hashes the specified password to determine if it matches the hash of this instance
	 * @param password A {@code String} to check against
	 * @return True if the matches the hash, false if not
	 * @throws NoSuchAlgorithmException
	 */
    public boolean matches(String password) throws NoSuchAlgorithmException
    {
        String hashed = Credentials.saltAndHash(password, salt);
        return hashed.equals(hash);
    }

    @Override
    public String toString()
    {
        return "<AuthPair: hash: " + hash + ", salt: " + salt + ">";
    }
}
