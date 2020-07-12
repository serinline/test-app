package com.example.calculator.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

@Service
public class CalculatorService {

    public static Double calculate(List<Double> values, String operator){
        return Arrays.stream(Operation.values())
                .filter(op -> op.getSymbol().equals(operator))
                .findFirst()
                .get()
                .getResult(values.get(0), values.get(1));
    }


    enum Operation {

        ADD("+", Double::sum),
        SUB("-", (a, b) -> a - b),
        MUL("*", (a, b) -> a * b),
        DIV("/", (a, b) -> a / b);

        private final String symbol;
        private final DoubleBinaryOperator operator;

        private Operation(String symbol, DoubleBinaryOperator operator) {
            this.symbol = symbol;
            this.operator = operator;
        }

        public double getResult(double d1, double d2) {
            return operator.applyAsDouble(d1, d2);
        }

        public String getSymbol() {
            return symbol;
        }
    }
}



