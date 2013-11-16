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
    public static final int NO_ERROR = 0;
    public static final int INVALID_MESSAGE_ERROR = 100;
    public static final int INVALID_ACTION_ERROR = 101;
    public static final int INVALID_NAME_ERROR = 102;
    public static final int INVALID_PASS_ERROR = 103;

    private int code;

    public ChessHeroException()
    {
        this.code = NO_ERROR;
    }

    public ChessHeroException(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }
}
