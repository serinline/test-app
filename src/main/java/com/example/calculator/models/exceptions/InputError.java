package com.example.calculator.models.exceptions;

public class InputError extends RuntimeException{
    private String exceptionMsg;

    public InputError(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }
    public String getExceptionMsg(){
        return this.exceptionMsg;
    }
    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }
}