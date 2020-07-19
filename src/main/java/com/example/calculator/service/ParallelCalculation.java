package com.example.calculator.service;

import com.example.calculator.models.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
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
        this.expressionList = getPolishNotationOfExpression(expression);
        this.resultSet = new HashMap<>();
    }

    public ParallelCalculation(ArrayList<Character> expression){
        this.expressionList = expression;
        this.resultSet = new HashMap<>();
    }

    private ParallelCalculation(ArrayList<Character> expression, int index){
        this.expressionList = expression;
        this.resultSet = new HashMap<>();
        this.subtaskIndex = index;
    }

    @Override
    protected HashMap<Integer, Double> compute() {
        if (expressionList.size() > 3) {
            List<Integer> indexes = getIndexOfSubArray(expressionList);
            List<ArrayList<Character>> subTasks = createSubtaskHelper(expressionList);
            Collection<ParallelCalculation> tasks = createSubtask(subTasks, indexes);
            for (ParallelCalculation p : tasks) {
                p.fork();
                resultSet.putAll(p.join());
            }
            return resultSet;
        }
        else {
            resultSet.put(this.subtaskIndex, calculateHelper(this.expressionList));
            return resultSet;
        }
    }

    private Collection<ParallelCalculation> createSubtask(List<ArrayList<Character>> list, List<Integer> indexes) {
        List<ParallelCalculation> dividedTasks = new ArrayList<>();
        for (ArrayList<Character> l : list){
            dividedTasks.add(new ParallelCalculation(l, indexes.get(list.indexOf(l))));
        }
        return dividedTasks;
    }

    private Double calculateHelper(ArrayList<Character> expression) {
        List<Double> values = Arrays.asList(Double.parseDouble(String.valueOf(expression.get(0))),
                Double.parseDouble(String.valueOf(expression.get(1))));
        return calculatorService.calculate(values, expression.get(2));
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

    private List<Integer> getIndexOfSubArray(List<Character> expression) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 2; i < expression.size(); ++i) {
            if (Character.isDigit(expression.get(i - 2)) && Character.isDigit(expression.get(i - 1)) && !Character.isDigit(expression.get(i))) {
                indexes.add(i-2);
            }
        }
        return indexes;
    }

    private int findList(List<Character> longExpression, ArrayList<Character> shortExpression) {
        return Collections.indexOfSubList(Arrays.asList(longExpression), Arrays.asList(shortExpression));
    }

    private ArrayList<Character> generateNewExpression(ArrayList<Character> expression, Map<Integer, Double> valuesToAdd){
        ArrayList<Character> newExpression = expression;
        valuesToAdd = sortArray(valuesToAdd);
        for (Map.Entry<Integer, Double> entry : valuesToAdd.entrySet()){
            double value = entry.getValue();
            newExpression.add(entry.getKey(), (char) value);
            newExpression.remove(entry.getKey()+1);
            newExpression.remove(entry.getKey()+2);
            newExpression.remove(entry.getKey()+3);
        }
        return newExpression;
    }

    private ArrayList<Character> getPolishNotationOfExpression(Expression expression){
        return (ArrayList<Character>) shuntingYard.postfix(expression.getExpression())
                .chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
    }

    private HashMap<Integer, Double> sortArray(Map<Integer, Double> map){
        return map.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .collect( Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new));
    }

    public char getResult(Expression expression){
        ArrayList<Character> expList = getPolishNotationOfExpression(expression);
        if (expression.getExpression().length() == 1){
            return expression.getExpression().toCharArray()[0];
        }
        else {
            ForkJoinPool pool = new ForkJoinPool();
            ParallelCalculation task = new ParallelCalculation(expression);
            Map<Integer, Double> results  = pool
                    .invoke(task);
            expList = generateNewExpression(expList, results);
        }
        return expList.get(0);
    }

}
