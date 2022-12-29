package org.example;

public class Function {
    private final String name;

    public Function(String name) {
        this.name = name;
    }

    public double getResult(double x) {
        return switch (name) {
            case "A1" -> Math.exp(0.2 * x);
            case "A2" -> Math.pow(2, -x);
            case "A3" -> Math.cos(x);
            case "A4" -> x;
            case "A5" -> Math.sqrt(Math.abs(x));
            default -> 0;
        };
    }


}
