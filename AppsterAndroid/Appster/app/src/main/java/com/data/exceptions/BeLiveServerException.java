package com.data.exceptions;

import java.io.IOException;

/**
 * Created by thanhbc on 11/13/17.
 */

public class BeLiveServerException extends IOException{
    public final int code;

    public BeLiveServerException(String message, int code) {
        super(message);
        this.code = code;
    }
}
