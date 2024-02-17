import Quokka.exceptions.QuokkaException;
import Quokka.tasks.Deadline;
import Quokka.tasks.Event;
import Quokka.tasks.Task;
import Quokka.tasks.Todo;

import java.util.Scanner;
import java.io.*;

public class Quokka {
    private static final int MAX_TASKS = 100;
    private static Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    private static final String DATA_FILE_PATH = "tasks.txt";

    static {
        loadTasksFromFile();
    }


    private static void addTask(Task newTask) {
        try {
            if (newTask != null) {
                if (taskCount < MAX_TASKS) {
                    tasks[taskCount++] = newTask;
                    System.out.println("     Got it. I've added this task:");
                    System.out.println("       " + newTask);
                    System.out.println("     Now you have " + taskCount + " tasks in the list.");
                } else {
                    throw new QuokkaException("    Sorry, the task list is full. You cannot add more tasks.");
                }
            }
        } catch (QuokkaException e) {
            System.out.println("     Error: " + e.getMessage());
        }
    }

    private static Todo parseTodoTask(String userInput) {
        try {
            String description = userInput.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new QuokkaException("Please provide a description for the todo task.");
            }
            return new Todo(description, false);
        } catch (QuokkaException e) {
            System.out.println("    Error: " + e.getMessage());
            return null;
        }
    }

    private static Deadline parseDeadlineTask(String userInput) {
        try {
            String[] parts = userInput.split("/by", 2);
            if (parts.length == 2) {

                String description = parts[0].substring("deadline".length()).trim();
                String by = parts[1].trim();
                if (description.isEmpty() || by.isEmpty()) {
                    throw new QuokkaException("Please provide both description and deadline for the task.");
                }

                return new Deadline(description, by, false);
            } else {
                throw new QuokkaException("Invalid deadline format. Please use: deadline [description] /by [date/time]");
            }
        } catch (QuokkaException e) {
            System.out.println("     Error: " + e.getMessage());
            return null;
        }
    }

    private static Event parseEventTask(String userInput) {
        try {
            String[] parts = userInput.split("/from", 2);
            if (parts.length == 2) {
                String description = parts[0].substring("event".length()).trim();
                String[] dateTimes = parts[1].split("/to", 2);
                if (dateTimes.length == 2) {
                    String from = dateTimes[0].trim();
                    String to = dateTimes[1].trim();
                    if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                        throw new QuokkaException("Please provide description, start time, and end time for the event task.");
                    }
                    return new Event(description, from, to, false);
                } else {
                    throw new QuokkaException("Invalid event format. Please use: event [description] /from [start] /to [end]");
                }
            } else {
                throw new QuokkaException("Invalid event format. Please use: event [description] /from [start] /to [end]");
            }
        } catch (QuokkaException e) {
            System.out.println("    Error: " + e.getMessage());
            return null;
        }
    }

    private static void displayTasks() {
        if (taskCount == 0) {
            System.out.println("    No tasks added yet.");
        } else {
            System.out.println("    Here are the tasks in your list:");
            for (int i = 0; i < taskCount; i++) {
                System.out.println("    " + (i + 1) + ". " + tasks[i]);
            }
        }
    }

    private static void saveTasksToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE_PATH))) {
            for (int i = 0; i < taskCount; i++) {
                writer.println(tasks[i].toString());
            }
        } catch (IOException e) {
            System.out.println("Error occurred while saving tasks to file: " + e.getMessage());
        }
    }

    private static void loadTasksFromFile() {
        File file = new File(DATA_FILE_PATH);
        if (!file.exists()) {
            System.out.println("No existing data file found. Starting with empty task list.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            taskCount = 0;
            while (scanner.hasNextLine() && taskCount < MAX_TASKS) {
                String taskData = scanner.nextLine();
                Task task = Task.parseFromFileString(taskData);
                if (task != null) {
                    tasks[taskCount++] = task;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found: " + e.getMessage());
        } catch (QuokkaException e) {
            System.out.println("Data file contains corrupted data: " + e.getMessage());
        }
    }

    private static void markTaskAsDone(String userInput) {
        try {
            updateTaskStatus(userInput, true, "Nice! I've marked this task as done:");
        } catch (QuokkaException e) {
            System.out.println("     Error: " + e.getMessage());
        }
    }

    private static void markTaskAsNotDone(String userInput) {
        try {
            updateTaskStatus(userInput, false, "OK, I've marked this task as not done yet:");
        } catch (QuokkaException e) {
            System.out.println("     Error: " + e.getMessage());
        }
    }

    private static void updateTaskStatus(String userInput, boolean newStatus, String statusMessage) {
        try {
            String[] parts = userInput.split(" ", 2);
            if (parts.length == 2) {
                int taskIndex = Integer.parseInt(parts[1]) - 1;
                if (taskIndex >= 0 && taskIndex < taskCount) {
                    tasks[taskIndex].setStatus(newStatus);
                    System.out.println("    " + statusMessage);
                    System.out.println("      " + tasks[taskIndex]);
                } else {
                    throw new QuokkaException("Invalid task index.");
                }
            } else {
                throw new QuokkaException("Please provide a valid task index to mark as done or not done.");
            }
        } catch (NumberFormatException e) {
            System.out.println("     Error: Invalid task index format.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Hello! I'm Quokka");
        System.out.println("What can I do for you?");

        while (true) {
            // Read user input
            String userInput = scanner.nextLine();

            // Check if the user wants to exit
            if (userInput.equalsIgnoreCase("bye")) {
                saveTasksToFile(); //Save tasks whenever task list changes
                System.out.println("    Bye. Hope to see you again soon!");
                break;
            }

            // Check if the user wants to list tasks
            if (userInput.equalsIgnoreCase("list")) {
                displayTasks();
            } else {
                // Handle different types of tasks
                if (userInput.toLowerCase().startsWith("mark ")) {
                    markTaskAsDone(userInput);
                } else if (userInput.toLowerCase().startsWith("unmark ")) {
                    markTaskAsNotDone(userInput);
                } else if (userInput.toLowerCase().startsWith("todo")) {
                    addTask(parseTodoTask(userInput));
                } else if (userInput.toLowerCase().startsWith("deadline")) {
                    addTask(parseDeadlineTask(userInput));
                } else if (userInput.toLowerCase().startsWith("event")) {
                    addTask(parseEventTask(userInput));
                } else {
                    System.out.println("    I'm sorry, I don't understand that command.");
                }
            }
        }

        // Close the scanner
        scanner.close();
    }
}
