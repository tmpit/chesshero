package com.kt;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/16/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChessHeroException extends Throwable
{
    private int code;

    public ChessHeroException(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }
}
