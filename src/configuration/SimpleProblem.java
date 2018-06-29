package it.menzani.bts.configuration;

enum SimpleProblem implements Problem {
    NULL_SECTION("section could not be found"),
    NULL_OR_EMPTY("must not be null nor empty");

    private final String sentence;

    SimpleProblem(String sentence) {
        this.sentence = sentence;
    }

    @Override
    public String description() {
        return sentence;
    }
}
