package com.example.calculator.service;

public class ShuntingYard {

    private enum Precedence {
        LPAREN(0),
        RPAREN(1),
        PLUS(2),
        MINUS(3),
        DIV(4),
        MUL(5),
        EOS(7),
        OPERAND(8);

        private int index;

        Precedence(int index) {
            this.index = index;
        }
        public int getIndex(){
            return index;
        }
    }

    private static final int[] stackPrecedence = {0, 19, 12, 12, 13, 13, 13, 0};
    private static final int[] characterPrecedence = {20, 19, 12, 12, 13, 13, 0};
    private static final char[] operators = {'(', ')', '+', '-', '/', '*', ' '};
    private Precedence[] stack;
    private int top;


    private Precedence pop() {
        return stack[top--];
    }

    private void push(Precedence ele) {
        stack[++top] = ele;
    }

    private Precedence getToken(char symbol) {
        switch (symbol) {
            case '(' :
                return Precedence.LPAREN;
            case ')' :
                return Precedence.RPAREN;
            case '+' :
                return Precedence.PLUS;
            case '-' :
                return Precedence.MINUS;
            case '/' :
                return Precedence.DIV;
            case '*' :
                return Precedence.MUL;
            case ' ' :
                return Precedence.EOS;
            default  :
                return Precedence.OPERAND;
        }
    }

    public String postfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        top = 0;
        stack = new Precedence[infix.length()];
        stack[0] = Precedence.EOS;
        Precedence token;
        for (int i = 0; i < infix.length(); i++) {
            token = getToken(infix.charAt(i));
            if (token == Precedence.OPERAND)
                postfix.append(infix.charAt(i));
            else if (token == Precedence.RPAREN)
            {
                while (stack[top] != Precedence.LPAREN)
                    postfix.append(operators[pop().getIndex()]);
                pop();
            }
            else {
                while (stackPrecedence[stack[top].getIndex()] >= characterPrecedence[token.getIndex()])
                    postfix.append(operators[pop().getIndex()]);
                push(token);
            }
        }
        while ((token = pop()) != Precedence.EOS)
            postfix.append(operators[token.getIndex()]);

        return postfix.toString();
    }
}
