import java.util.Scanner;

public class Ui {
    private static final Scanner scanner = new Scanner(System.in);

    public static void start(TaskList taskList) {
        System.out.println("Hello! I'm Quokka");
        System.out.println("What can I do for you?");
        handleUserInput(taskList);
    }

    private static void handleUserInput(TaskList taskList) {
        while (true) {
            // Read user input
            String userInput = scanner.nextLine();

            // Check if the user wants to exit
            if (userInput.equalsIgnoreCase("bye")) {
                Storage.saveTasksToFile(taskList); // Save tasks whenever task list changes
                System.out.println("    Bye. Hope to see you again soon!");
                break;
            }

            // Check if the user wants to list tasks
            if (userInput.equalsIgnoreCase("list")) {
                taskList.displayTasks();
            } else {
                // Handle different types of tasks
                if (userInput.toLowerCase().startsWith("mark ")) {
                    taskList.markTaskAsDone(userInput);
                } else if (userInput.toLowerCase().startsWith("unmark ")) {
                    taskList.markTaskAsNotDone(userInput);
                } else if (userInput.toLowerCase().startsWith("todo")) {
                    taskList.addTask(Parser.parseTodoTask(userInput));
                } else if (userInput.toLowerCase().startsWith("deadline")) {
                    taskList.addTask(Parser.parseDeadlineTask(userInput));
                } else if (userInput.toLowerCase().startsWith("event")) {
                    taskList.addTask(Parser.parseEventTask(userInput));
                } else if (userInput.toLowerCase().startsWith("delete ")) {
                    String[] parts = userInput.split(" ");
                    if (parts.length == 2) {
                        try {
                            int taskIndex = Integer.parseInt(parts[1]);
                            taskList.deleteTask(taskIndex);
                        } catch (NumberFormatException e) {
                            System.out.println("    Invalid task index format");
                        }
                    } else {
                        System.out.println("    Please provide a valid task index to delete");
                    }
                } else {
                    System.out.println("    I'm sorry, I don't understand that command.");
                }
            }
        }
    }
}