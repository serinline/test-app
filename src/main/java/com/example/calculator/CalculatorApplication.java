package com.example.calculator;

import com.example.calculator.models.Expression;
import com.example.calculator.service.CalculatorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalculatorApplication.class, args);
		CalculatorService calculatorService = new CalculatorService();
		char c = calculatorService.invokeCalculation(new Expression("8-1"));
		System.out.println("Result: " + c);
	}
}
