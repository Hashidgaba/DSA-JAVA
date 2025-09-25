import java.util.*;

class student {
    String name;
    int roll;
    int[] marks = new int[5];
    static ArrayList<student> std = new ArrayList<>();

    student(String name, int roll, int[] marks) {
        this.name = name;
        this.roll = roll;
        this.marks = marks;
        std.add(this);
    }

    void display() {
        
        System.out.println("Student ");
        System.out.println("Student Name: " + name + " Roll number: " + roll);
        System.out.println("marks: ");
        for (int el : marks) {
            System.out.print(el + " ");
        }
        System.out.println("\nAverage Marks: " + getAverage());
    }

    static void allStudents() {
        for (student s : std) {
            s.display();
            System.out.println();
        }
    }

    static void searchByRoll(int r) {
        boolean found = false;
        for (student s : std) {
            if (s.roll == r) {
                System.out.println("Student Found:");
                s.display();
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No student found with roll number " + r);
        }
    }

    double getAverage() {
        int sum = 0;
        for (int m : marks) {
            sum += m;
        }
        return sum / 5.0;
    }
}

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Scanner cin = new Scanner(System.in);
        int choice;
        while (true) {
            System.out.println("1. Add student details");
            System.out.println("2. display all student");
            System.out.println("3. Search");
            System.out.println("enter your choice");
            choice = cin.nextInt();
            cin.nextLine();
            if (choice == 1) {
                System.out.println("Enter your Name");
                String name = cin.nextLine();

                System.out.println("Enter your Roll number");
                int roll = cin.nextInt();
                System.out.println("Enter your marks");
                int[] arr = new int[5];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = cin.nextInt();
                }
                student s1 = new student(name, roll, arr);
                System.out.println("information has been saved");
                System.out.println("Name: " + name + " Roll Number: " + roll + " Marks:");
                for (int el : arr) {
                    System.out.print(el + " ");
                }
                System.out.println();

            } else if (choice == 2) {
                System.out.println("view all students");
                student.allStudents();

            } else if (choice == 3) {
                System.out.println("Enter roll number to search:");
                int rollSearch = cin.nextInt();
                student.searchByRoll(rollSearch);

            } else {
                System.out.println("invalid.....");
                break;
            }
        }
        cin.close();
    }
}
