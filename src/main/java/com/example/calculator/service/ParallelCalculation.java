package com.example.calculator.service;

import com.example.calculator.models.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;


public class ParallelCalculation extends RecursiveTask<Double> {

    @Autowired
    private CalculatorService calculatorService;
    private List<Character> expressionList;

    public ParallelCalculation(Expression expression){
        this.expressionList = expression.getExpression()
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
    }

    @Override
    protected Double compute() {
        if (expressionList.size() > 3) {
            return ForkJoinTask.invokeAll(createSubtasks())
                    .stream()
                    .mapToDouble(ForkJoinTask::join)
                    .sum();
        } else {
            return processing(expressionList);
        }
    }

    private Collection<ParallelCalculation> createSubtasks() {
        List<ParallelCalculation> dividedTasks = new ArrayList<>();
        dividedTasks.add(null);
        return dividedTasks;
    }

    private Double processing(List<Character> expression) {
        List<Double> values = Arrays.asList(Double.parseDouble(String.valueOf(expression.get(0))),
                Double.parseDouble(String.valueOf(expression.get(2))));
        return calculatorService.calculate(values, expression.get(1));
    }

    private List<Character> splitExpression(List<Character> expression){
        return expression;
    }
}
