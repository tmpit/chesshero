package com.kt;

/**
 * Created by Toshko on 12/7/13.
 *
 * CHESCO messaging protocol
 * Protocol specifications:
 *
 * All numbers are big endian.
 * All numbers are signed.
 * All strings are UTF-8 encoded.
 *
 * Each message consists of a 2 byte header and header byte body.
 * The body consists of parameters. Each parameter starts with 1 byte for the type of the parameter.
 * After the parameter type each parameter should be read as follows (about notation refer to the bottom of this comment):
 *
 * BOOL
 * [val:1]
 *
 * INT
 * [val:4]
 *
 * STR
 * [length:2] // The length of the data representing the string
 * [data:length] // The data representing the string
 *
 * MAP
 * [count:2] // The number of pairs following
 * [STR:x][param:y] // X key-value pairs. The key in a pair can only be a string, the value can be of any parameter type
 *
 * ARR
 * [count:2] // The number of parameters contained in the array
 * [param:x] // X parameters
 *
 * About notation:
 * Brackets represent a group of bytes representing a part of the parameter.
 * Within the brackets the part before the colon describes what that group of bytes represents.
 * The part after the colon is the length (in bytes) of the group
 */

public class CHESCO
{
    public static final byte TYPE_BOOL = 1;
    public static final byte TYPE_INT = 2;
    public static final byte TYPE_STR = 3;
    public static final byte TYPE_MAP = 4;
    public static final byte TYPE_ARR = 5;
}
