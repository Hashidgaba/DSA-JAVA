import java.util.*;

public class App {
    static int size = 0; // current number of elements in the array

    public static void main(String[] args) throws Exception {
        System.out.println("Array Implementation with Time Measurement");

        int capacity = 10;
        int[] arr = new int[capacity];
        size = 0;

        long startTime = System.nanoTime();
        insertAtEnd(arr, 1);
        insertAtEnd(arr, 2);
        insertAtEnd(arr, 3);
        insertAtEnd(arr, 4);
        long endTime = System.nanoTime();
        System.out.println("Time taken to insert 4 elements at end: " + (endTime - startTime) + " ns");

        startTime = System.nanoTime();
        insertAtIndex(arr, 2, 99);
        endTime = System.nanoTime();
        System.out.println("Time taken to insert element 99 at index 2: " + (endTime - startTime) + " ns");

        startTime = System.nanoTime();
        deleteAtIndex(arr, 0);
        endTime = System.nanoTime();
        System.out.println("Time taken to delete element at start (index 0): " + (endTime - startTime) + " ns");

        int midIndex = size / 2;
        startTime = System.nanoTime();
        deleteAtIndex(arr, midIndex);
        endTime = System.nanoTime();
        System.out.println("Time taken to delete element at middle (index " + midIndex + "): " + (endTime - startTime) + " ns");

        int lastIndex = size - 1;
        startTime = System.nanoTime();
        deleteAtIndex(arr, lastIndex);
        endTime = System.nanoTime();
        System.out.println("Time taken to delete element at end (index " + lastIndex + "): " + (endTime - startTime) + " ns");

        System.out.print("Final array elements: ");
        for (int i = 0; i < size; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    
    public static void insertAtIndex(int arr[], int index, int element) {
        if (index < 0 || index > size || size == arr.length) {
            System.out.println("Insertion not possible at index " + index);
            return;
        }
        for (int i = size - 1; i >= index; i--) {
            arr[i + 1] = arr[i];
        }
        arr[index] = element;
        size++;
    }

    public static void insertAtEnd(int arr[], int element) {
        if (size == arr.length) {
            System.out.println("Array is full. Cannot insert at end.");
            return;
        }
        arr[size] = element;
        size++;
    }

    public static void deleteAtIndex(int arr[], int index) {
        if (index < 0 || index >= size) {
            System.out.println("Deletion not possible at index " + index);
            return;
        }
        for (int i = index; i < size - 1; i++) {
            arr[i] = arr[i + 1];
        }
        size--;
    }
}
