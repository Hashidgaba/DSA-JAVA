//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
       int arr[] = {9,8,7,6,5,4,3};
        int n = arr.length;

        // Outer loop: Kitni baar chalana hai
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {

                // Agar pehla number dusre se bada hai -> SWAP
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        for(int i = 0; i < n -1;i ++){
            System.out.println(arr[i]);
        }
    }
}