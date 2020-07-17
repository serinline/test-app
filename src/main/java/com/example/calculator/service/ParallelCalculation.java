package com.example.calculator.service;

import com.example.calculator.models.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.transform.Result;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;


public class ParallelCalculation extends RecursiveTask<List<Character>> {

    @Autowired
    private CalculatorService calculatorService;

    @Autowired
    private ShuntingYard shuntingYard;

    private LinkedList<Character> expressionList;

    public ParallelCalculation(Expression expression){
        this.expressionList = (LinkedList<Character>) shuntingYard.postfix(expression.getExpression())
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
    }

    private ParallelCalculation(List<Character> expression){
        this.expressionList = (LinkedList<Character>) expression;
    }

    @Override
    protected List<Character> compute() {
        if (expressionList.size() > 3) {
            List<List<Character>> subTasks = createSubtaskHelper(expressionList);
//            List<Future<Result>> results = ForkJoinTask.invokeAll(createSubtask(subTasks))
//                    .stream()
//                    .map(Objects::toString)
//                    .collect(Collectors.toList());
            return expressionList;
        } else {
            return expressionList;
        }
    }

    private Collection<ParallelCalculation> createSubtask(List<List<Character>> list) {
        List<ParallelCalculation> dividedTasks = new ArrayList<>();
        for (List<Character> l : list){
            dividedTasks.add(new ParallelCalculation(l));
        }
        return dividedTasks;
    }

    private Double calculateHelper(List<Character> expression) {
        List<Double> values = Arrays.asList(Double.parseDouble(String.valueOf(expression.get(0))),
                Double.parseDouble(String.valueOf(expression.get(2))));
        return calculatorService.calculate(values, expression.get(1));
    }

    private List<List<Character>> createSubtaskHelper(List<Character> expression){
        List<List<Character>> tasks = new ArrayList<>();
        for (int i = 2; i < expression.size(); ++i){
            if (Character.isDigit(expression.get(i-2)) && Character.isDigit(expression.get(i-1)) && !Character.isDigit(expression.get(i))){
                tasks.add(Arrays.asList(expression.get(i-2), expression.get(i-1), expression.get(i)));
            }
        }
        return tasks;
    }

    public static int findList(List<Character> longExpression, List<Character> shortExpression) {
        return Collections.indexOfSubList(Arrays.asList(longExpression), Arrays.asList(shortExpression));
    }

}
