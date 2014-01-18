package com.kt.chesco;

/**
 * <pre>
 * CHESCO messaging protocol
 * Protocol specifications:
 *
 * All numbers are big endian.
 * All numbers are signed.
 * All strings are UTF-8 encoded.
 *
 * A CHESCO message consists of parameters. The CHESCO class groups all supported parameter types.
 * Each parameter starts with 1 byte for the type of the parameter.
 * After the parameter type each parameter should be read as follows:
 *
 * NULL
 * -------
 * Null parameter
 * It does not consist of any additional data
 *
 * BOOL
 * -------
 * Bool parameter
 * Followed by 1 byte representing the value
 *
 * INT
 * -------
 * Integer parameter
 * Followed by 4 bytes representing the value
 *
 * STR
 * -------
 * String parameter
 * The first 2 bytes contain the length of the data representing the string followed by the actual data
 *
 * MAP
 * -------
 * Map parameter
 * The first two bytes contain the number of key-value pairs following. The key can only be STR, the value can be of any parameter type
 *
 * ARR
 * -------
 * The first two bytes contain the number of parameters following. The parameters can be of any type
 *
 * Each message starts with either an ARR parameter type or MAP parameter type.
 * </pre>
 *
 * @author Todor Pitekov
 * @author Kiril Tabakov
 */
public class CHESCO
{
	public static final byte TYPE_NULL = 1;
    public static final byte TYPE_BOOL = 2;
    public static final byte TYPE_INT = 3;
    public static final byte TYPE_STR = 4;
    public static final byte TYPE_MAP = 5;
    public static final byte TYPE_ARR = 6;
}
