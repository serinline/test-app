package com.example.calculator.service;

import com.example.calculator.models.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

public class ParallelCalculation extends RecursiveAction {

    @Autowired
    private CalculatorService calculatorService = new CalculatorService();

    @Autowired
    private ShuntingYard shuntingYard = new ShuntingYard();

    private ArrayList<Character> expressionList;

    public ParallelCalculation(Expression expression){
        this.expressionList = (ArrayList<Character>) shuntingYard.postfix(expression.getExpression())
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
    }

    private ParallelCalculation(ArrayList<Character> expression){
        this.expressionList = expression;
    }

    @Override
    protected void compute() {
        if (expressionList.size() > 3) {
            List<ArrayList<Character>> subTasks = createSubtaskHelper(expressionList);
            ForkJoinTask.invokeAll(createSubtask(subTasks));
        } else {
            replaceInList(expressionList, calculateHelper(expressionList));
        }
    }

    private Collection<ParallelCalculation> createSubtask(List<ArrayList<Character>> list) {
        List<ParallelCalculation> dividedTasks = new ArrayList<>();
        for (ArrayList<Character> l : list){
            dividedTasks.add(new ParallelCalculation(l));
        }
        return dividedTasks;
    }

    private Double calculateHelper(ArrayList<Character> expression) {
        List<Double> values = Arrays.asList(Double.parseDouble(String.valueOf(expression.get(0))),
                Double.parseDouble(String.valueOf(expression.get(1))));
        return calculatorService.calculate(values, expression.get(2));
    }

    private void replaceInList(ArrayList<Character> expression, double result){
        int index = findList(this.expressionList, expression);
        this.expressionList.subList(index, index+2).clear();
        this.expressionList.add(index, (char) result);
    }

    private List<ArrayList<Character>> createSubtaskHelper(List<Character> expression){
        List<ArrayList<Character>> tasks = new ArrayList<>();
        for (int i = 2; i < expression.size(); ++i){
            if (Character.isDigit(expression.get(i-2)) && Character.isDigit(expression.get(i-1)) && !Character.isDigit(expression.get(i))){
                ArrayList<Character> subTask = new ArrayList<>();
                subTask.add(expression.get(i-2));
                subTask.add(expression.get(i-1));
                subTask.add(expression.get(i));
                tasks.add(subTask);
            }
        }
        return tasks;
    }

    private int findList(List<Character> longExpression, ArrayList<Character> shortExpression) {
        return Collections.indexOfSubList(Arrays.asList(longExpression), Arrays.asList(shortExpression));
    }

    public char getResult(Expression expression){
        if (expression.getExpression().length() == 1){
            return expression.getExpression().toCharArray()[0];
        }
        else {
            ForkJoinPool pool = new ForkJoinPool();
            ParallelCalculation task = new ParallelCalculation(expression);
            pool.invoke(task);
            return expressionList.get(0);
        }
    }

}
