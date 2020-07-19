package com.example.calculator.controllers;

import com.example.calculator.models.Expression;
import com.example.calculator.models.exceptions.InputError;
import com.example.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {

    @Autowired
    private CalculatorService calculatorService;

    @PostMapping("/evaluate")
    @ExceptionHandler({InputError.class})
    char calculate(@RequestBody Expression expression){
        if (expression.getExpression().isEmpty()){
            throw new InputError("Expression doesn't exist");
        }
        if (expression.getExpression().chars().anyMatch(Character::isLetter)){
            throw new InputError("Unable to calculate result of string of letters");
        }
        else
            return calculatorService.invokeCalculation(expression);
    }
}
