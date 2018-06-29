package it.menzani.bts.components.worldreset;

class ParseException extends RuntimeException {
    ParseException(String message) {
        super(message);
    }

    ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
