package com.example.calculator.models;

public class Expression {

    private String expression;

    public Expression(){}

    public Expression(String expression){
        this.setExpression(expression);
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
