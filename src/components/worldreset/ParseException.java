package it.menzani.bts.components.worldreset;

class ParseException extends Exception {
    ParseException(String message) {
        super(message);
    }

    ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
