package com.example.calculator;

import com.example.calculator.service.CalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CalculatorServiceUnitTest {

    @Autowired
    private CalculatorService calculatorService;

    @Test
    public void shouldCalculateResult(){
        assertEquals(3.0, calculatorService.calculate(Arrays.asList(2.0, 1.0), '+'));
        assertEquals(1.0, calculatorService.calculate(Arrays.asList(2.0, 1.0), '-'));
    }
}
