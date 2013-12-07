package com.kt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/16/13
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */

public class Credentials
{
    public static final short MIN_NAME_LENGTH = 3;
    public static final short MAX_NAME_LENGTH = 50;
    public static final short MIN_PASS_LENGTH = 3;
    public static final short MAX_PASS_LENGTH = 50;

    private static SecureRandom csprng = new SecureRandom();

    private String name = null;
    private String pass = null;
    private AuthPair authPair = null;

    public static boolean isNameValid(String name)
    {
        int length = name.length();
        return (length >= MIN_NAME_LENGTH && length <= MAX_NAME_LENGTH);
    }

    public static boolean isPassValid(String pass)
    {
        int length = pass.length();
        return (length >= MIN_PASS_LENGTH && length <= MAX_PASS_LENGTH);
    }

    public static boolean isBadUser(String name)
    {
        String lowercase = name.toLowerCase();
        return (name.contains("andonov") || name.contains("filip") || name.contains("felipe") || name.contains("fil") ||
                name.contains("андонов") || name.contains("филип") || name.contains("фелипе") || name.contains("фил"));
    }

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

    public Credentials(String name, String pass)
    {
        this.name = name;
        this.pass = pass;
    }

    public Credentials(String name, String pass, AuthPair authPair)
    {
        this.name = name;
        this.pass = pass;
        this.authPair = authPair;
    }

    public String getName()
    {
        return name;
    }

    public String getPass()
    {
        return pass;
    }

    public AuthPair getAuthPair() throws NoSuchAlgorithmException
    {
        if (null == authPair)
        {
            int salt = csprng.nextInt(Integer.MAX_VALUE);
            String hash = Credentials.saltAndHash(pass, salt);
            authPair = new AuthPair(hash, salt);
        }

        return authPair;
    }

    @Override
    public String toString()
    {
        return "<Credentials: name: " + name + ", pass: " + pass + ", authPair: " + authPair + ">";
    }
}

class AuthPair
{
    private String hash = null;
    private int salt;

    AuthPair(String hash, int salt)
    {
        this.hash = hash;
        this.salt = salt;
    }

    public String getHash()
    {
        return hash;
    }

    public int getSalt()
    {
        return salt;
    }

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
