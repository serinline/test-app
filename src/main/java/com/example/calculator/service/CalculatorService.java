package com.example.calculator.service;

import com.example.calculator.models.Expression;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

@Service
public class CalculatorService {

    ParallelCalculation parallelCalculation;

    public char invokeCalculation(Expression expression){
        parallelCalculation = new ParallelCalculation(expression);
        return parallelCalculation.getResult(expression);
    }

    public Double calculate(List<Double> values, char operator){
        return Arrays.stream(Operation.values())
                .filter(op -> op.getSymbol().equals(operator))
                .findFirst()
                .get()
                .getResult(values.get(0), values.get(1));
    }

    enum Operation {

        ADD('+', Double::sum),
        SUB('-', (a, b) -> a - b),
        MUL('*', (a, b) -> a * b),
        DIV('/', (a, b) -> a / b);

        private final Character symbol;
        private final DoubleBinaryOperator operator;

        Operation(Character symbol, DoubleBinaryOperator operator) {
            this.symbol = symbol;
            this.operator = operator;
        }

        public double getResult(double d1, double d2) {
            return operator.applyAsDouble(d1, d2);
        }

        public Character getSymbol() {
            return symbol;
        }
    }
}



