public class Student {
    // Node class for singly linked list
    static class Node {
        String data; // Student name or ID
        Node next;

        Node(String data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;

    // Constructor
    public Student() {
        this.head = null;
    }

    public void addAtBeginning(String student) {
        Node newNode = new Node(student);
        newNode.next = head;
        head = newNode;
    }

    public void addAtEnd(String student) {
        Node newNode = new Node(student);
        if (head == null) {
            head = newNode;
            return;
        }
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }

    public void deleteFirst() {
        if (head == null) {
            System.out.println("List is empty. Cannot delete.");
            return;
        }
        head = head.next;
    }

    public void deleteLast() {
        if (head == null) {
            System.out.println("List is empty. Cannot delete.");
            return;
        }
        if (head.next == null) {
            head = null;
            return;
        }
        Node current = head;
        while (current.next.next != null) {
            current = current.next;
        }
        current.next = null;
    }

    public void display() {
        if (head == null) {
            System.out.println("No borrowers in the list.");
            return;
        }
        Node current = head;
        System.out.print("Current borrowers: ");
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Student list = new Student();
        list.addAtBeginning("HASHID");
        list.addAtEnd("BULBUL");
        list.addAtBeginning("IRFAN");
        list.display(); 
        list.deleteFirst();
        list.display(); 
        list.deleteLast();
        list.display(); 
    }
}
