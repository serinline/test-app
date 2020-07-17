package com.example.calculator;

import com.example.calculator.service.ShuntingYard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShuntingYardUnitTest {

    @Autowired
    private ShuntingYard shuntingYard = new ShuntingYard();

    @Test
    public void testPostfixCalculation(){
        assertTrue("12+3*4/67*8/-9+1-".equals(shuntingYard.postfix("(1+2)*3/4-6*7/8+9-1")));
    }

}
