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

    Credentials(String name, String pass)
    {
        this.name = name;
        this.pass = pass;
    }

    Credentials(String name, String pass, AuthPair authPair)
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
            String salted = salt + pass;
            String hash = hashOfString(salted);
            authPair = new AuthPair(hash, salt);
        }

        return authPair;
    }

    private static String hashOfString(String str) throws NoSuchAlgorithmException
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte converted[] = digest.digest(str.getBytes());

        Formatter formatter = new Formatter();

        for (byte b : converted)
        {
            formatter.format("%02x", b);
        }

        return formatter.toString();
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

    @Override
    public String toString()
    {
        return "<AuthPair: hash: " + hash + ", salt: " + salt + ">";
    }
}
