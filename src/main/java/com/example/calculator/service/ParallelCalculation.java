package com.example.calculator.service;

import com.example.calculator.models.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class ParallelCalculation extends RecursiveTask <HashMap<Integer, Double>> {

    @Autowired
    private CalculatorService calculatorService = new CalculatorService();

    @Autowired
    private ShuntingYard shuntingYard = new ShuntingYard();

    private ArrayList<Character> expressionList;
    private HashMap<Integer, Double> resultSet;
    private int subtaskIndex;


    public ParallelCalculation(Expression expression){
        System.out.println("constructor pc expession " + expression.getExpression());
        this.expressionList = getPolishNotationOfExpression(expression);
        this.resultSet = new HashMap<>();
    }

    private ParallelCalculation(ArrayList<Character> expression, int index){
        System.out.println("constructor pc arraylist " + " " + expression);
        this.expressionList = expression;
        this.resultSet = new HashMap<>();
        this.subtaskIndex = index;
    }

    @Override
    protected HashMap<Integer, Double> compute() {
        System.out.println("compute");
        if (expressionList.size() > 3) {
            List<Integer> indexes = getIndexOfSubArray(expressionList);
            List<ArrayList<Character>> subTasks = createSubtaskHelper(expressionList);
            ForkJoinTask.invokeAll(createSubtask(subTasks, indexes));
        }
        else {
            resultSet.put(this.subtaskIndex, calculateHelper(this.expressionList));
            System.out.println(resultSet.keySet() + " " + resultSet.values());
            return resultSet;
        }
        return null;
    }

    private Collection<ParallelCalculation> createSubtask(List<ArrayList<Character>> list, List<Integer> indexes) {
        System.out.println("subtask");
        List<ParallelCalculation> dividedTasks = new ArrayList<>();
        for (ArrayList<Character> l : list){
            dividedTasks.add(new ParallelCalculation(l, indexes.get(list.indexOf(l))));
        }
        return dividedTasks;
    }

    private Double calculateHelper(ArrayList<Character> expression) {
        System.out.println("counting");
        List<Double> values = Arrays.asList(Double.parseDouble(String.valueOf(expression.get(0))),
                Double.parseDouble(String.valueOf(expression.get(1))));
        return calculatorService.calculate(values, expression.get(2));
    }

    private List<ArrayList<Character>> createSubtaskHelper(List<Character> expression){
        System.out.println("subtask creator");
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

    private List<Integer> getIndexOfSubArray(List<Character> expression) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 2; i < expression.size(); ++i) {
            if (Character.isDigit(expression.get(i - 2)) && Character.isDigit(expression.get(i - 1)) && !Character.isDigit(expression.get(i))) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private int findList(List<Character> longExpression, ArrayList<Character> shortExpression) {
        System.out.println("findlist " + shortExpression);
        System.out.println(longExpression);
        return Collections.indexOfSubList(Arrays.asList(longExpression), Arrays.asList(shortExpression));
    }

    private ArrayList<Character> replaceInList(ArrayList<Character> expression, double result, int index){
        System.out.println("replacer " + result);
        expression.subList(index, index+3).clear();
        expression.add(index, (char)result);
        System.out.println("replacer " + this.expressionList.toString());
        return expression;
    }

    private ArrayList<Character> getPolishNotationOfExpression(Expression expression){
        return (ArrayList<Character>) shuntingYard.postfix(expression.getExpression())
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
    }

    public char getResult(Expression expression){
        System.out.println("get result");
        ArrayList<Character> expList = getPolishNotationOfExpression(expression);
        if (expression.getExpression().length() == 1){
            return expression.getExpression().toCharArray()[0];
        }
        else {
            while (expList.size() > 1 ){
                ForkJoinPool pool = new ForkJoinPool();
                ParallelCalculation task = new ParallelCalculation(expression);
                pool.invoke(task);
                System.out.println("get result " + expressionList);
                return expressionList.get(0);
            }
        }
        return expList.get(0);
    }
}
