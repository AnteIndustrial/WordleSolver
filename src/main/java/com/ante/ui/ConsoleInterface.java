package com.ante.ui;

import com.ante.WordleDriver;
import com.ante.solver.WordleSolverBasic;
import com.ante.solver.WordleSolverRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleInterface implements WordleInterface {

    private final Scanner scanner;

    public ConsoleInterface(){
        scanner = new Scanner(System.in);
    }

    @Override
    public void print(Object toPrint) {
        System.out.println(toPrint);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    public static void main(String[] args) {
        ConsoleInterface console = new ConsoleInterface();
        console.print("1: manual solver");
        console.print("2: auto solver");
        console.print("3: bulk solver");
        //or d1, d2, d3 to turn on debug logging
        //or r1, r2, r3 to use RandomSolver
        //q1 or q2 for quordle, o1 or o2 for octordle
        //4 is for testing minimax solving, which is incomplete

        String input = console.readLine();

        WordleDriver driver;
        if(input.length() > 1 && input.charAt(0) == 'r'){
            driver = new WordleDriver(console, new WordleSolverRandom());
            input = input.substring(1);
        } else {
            driver = new WordleDriver(console, new WordleSolverBasic());
        }


        if(input.length() > 1 && input.charAt(0) == 'd'){
            driver.setDebug();
            input = input.substring(1);
        }
        switch (input) {
            case "1" -> driver.manualSolver();
            case "o1" -> driver.multiManualSolver(8);
            case "q1" -> driver.multiManualSolver(4);
            case "2" -> {
                console.print("Enter target word");
                String target = console.readLine();
                driver.autoSolver(target);
            }
            case "o2" -> {
                console.print("Enter target words");
                List<String> targets = new ArrayList<>(8);
                for(int i = 0; i < 8; i++){
                    targets.add(console.readLine());
                }
                driver.multiAutoSolver(targets);
            }
            case "q2" -> {
                console.print("Enter target words");
                List<String> targets = new ArrayList<>(8);
                for(int i = 0; i < 4; i++){
                    targets.add(console.readLine());
                }
                driver.multiAutoSolver(targets);
            }
            case "3" -> driver.bulkSolver();
            case "4" -> driver.minimax();
            default -> console.print("Invalid input");
        }
    }
}
