package com.example.calculator.controllers;

import com.example.calculator.models.Expression;
import com.example.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculatorController {

    @Autowired
    private CalculatorService calculatorService;

    @PostMapping("/evaluate")
    char calculate(@RequestBody Expression expression){
        return calculatorService.invokeCalculation(expression);
    }
}
