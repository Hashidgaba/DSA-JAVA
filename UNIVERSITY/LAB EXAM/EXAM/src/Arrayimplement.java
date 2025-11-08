
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//question 1 Array books stock management system
import java.util.Arrays;

public class Arrayimplement {
    static int[] arr;
    static int[] newArr;
    static int capacity;
    static int size = 0;

    Arrayimplement(int capacity) {
        this.capacity = capacity;
        arr = new int[capacity];
        this.size = 0;

    }

    static void insert(int value) {
        if (size >= arr.length) {
            System.out.println("Array is full");
            return;
        }
        arr[size] = value;
        size++;
    }

    static void delete(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid index, index  should be between 0 to " + (size - 1));
            return;
        }

        for (int i = index; i < size - 1; i++) {
            arr[i] = arr[i + 1];
        }
        size--;
    }

    static void display() {
        for (int i = 0; i < size; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    static void displayNew(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    static void insertAtfirst(int value) {
        if (size >= arr.length) {
            System.out.println("current array is full we can update its size and insert values");
            int newArr[] = new int[arr.length + 1];
            newArr[0] = value;
            for (int i = 1; i < newArr.length; i++) {
                newArr[i] = arr[i - 1];
            }
            displayNew(newArr);
            return;
        } else {
            for (int i = size; i > 0; i--) {
                arr[i] = arr[i - 1];
            }
            arr[0] = value;
            size++;
            display();
            return;
        }
    }

    static void sorting() {
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        display();
    }

    public static void main(String[] args) {
        System.out.println("Book store management system");
        Arrayimplement arr = new Arrayimplement(5);
        arr.insert(10);
        arr.insert(13);
        arr.insert(45);
        arr.insert(67);
        arr.insert(789);
        arr.display();
        arr.delete(4);
        arr.display();
        arr.delete(4);
    }
}