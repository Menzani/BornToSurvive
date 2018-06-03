package it.menzani.bts.config;

enum Problem {
    NULL_OR_EMPTY("must not be null nor empty");

    private final String sentence;

    Problem(String sentence) {
        this.sentence = sentence;
    }

    @Override
    public String toString() {
        return sentence;
    }
}
