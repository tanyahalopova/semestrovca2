import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BTreePerformance {
    public static void main(String[] args) {
        BTree bTree = new BTree(5);
        int[] array = generateRandomArray(10000);

        List<OperationResult> insertionResults = new ArrayList<>();
        List<OperationResult> searchResults = new ArrayList<>();
        List<OperationResult> deletionResults = new ArrayList<>();

        // Добавление элементов в структуру
        for (int i = 0; i < 9999; i++) {
            int index = getRandomIndex(array.length);
            int element = array[index];
            OperationResult result = performInsertion(bTree, element);
            insertionResults.add(result);
        }
        int count = 0;
        // Поиск элементов в структуре
        for (int i = 0; i < 100; i++) {
            int index = getRandomIndex(array.length);
            int element = array[index];
            OperationResult result = performSearch(bTree, element);
            searchResults.add(result);
        }

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            int index = getRandomIndex(array.length);
            int element = array[index];
            while (list.contains(element)) {
                index = getRandomIndex(array.length);
                element = array[index];
            }
            list.add(element);
        }

        // Удаление элементов из структуры
        for (int i = 0; i < 1000; i++) {
            int element = list.get(i);
            OperationResult result = performDeletion(bTree, element);
            deletionResults.add(result);
        }

        // Вычисление среднего времени и количества операций для вставки
        double avgInsertionTime = calculateAverageTime(insertionResults);
        double avgInsertionOperations = calculateAverageOperations(insertionResults);

        // Вычисление среднего времени и количества операций для поиска
        double avgSearchTime = calculateAverageTime(searchResults);
        double avgSearchOperations = calculateAverageOperations(searchResults);

        // Вычисление среднего времени и количества операций для удаления
        double avgDeletionTime = calculateAverageTime(deletionResults);
        double avgDeletionOperations = calculateAverageOperations(deletionResults);

        // Вывод результатов
        System.out.println("Average Insertion Time: " + avgInsertionTime + " ns");
        System.out.println("Average Insertion Operations: " + avgInsertionOperations);
        System.out.println("Average Search Time: " + avgSearchTime + " ns");
        System.out.println("Average Search Operations: " + avgSearchOperations);
        System.out.println("Average Deletion Time: " + avgDeletionTime + " ns");
        System.out.println("Average Deletion Operations: " + avgDeletionOperations);
    }

    public static int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100000); // Генерируем случайное целое число
        }
        return array;
    }

    public static int getRandomIndex(int length) {
        Random random = new Random();
        return random.nextInt(length);
    }

    public static OperationResult performInsertion(BTree bTree, int element) {
        long startTime = System.nanoTime();
        bTree.insert(element);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        long operations = bTree.getOperationsCount();
        return new OperationResult(executionTime, operations);
    }

    public static OperationResult performSearch(BTree bTree, int element) {
        long startTime = System.nanoTime();
        bTree.search(element);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        long operations = bTree.getOperationsCount();
        return new OperationResult(executionTime, operations);
    }

    public static OperationResult performDeletion(BTree bTree, int element) {
        long startTime = System.nanoTime();
        bTree.remove(element);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        long operations = bTree.getOperationsCount();
        return new OperationResult(executionTime, operations);
    }

    public static double calculateAverageTime(List<OperationResult> results) {
        long totalExecutionTime = 0;
        for (OperationResult result : results) {
            totalExecutionTime += result.getExecutionTime();
        }
        return (double) totalExecutionTime / results.size();
    }

    public static double calculateAverageOperations(List<OperationResult> results) {
        int totalOperations = 0;
        for (OperationResult result : results) {
            totalOperations += result.getOperations();
        }
        return (double) totalOperations / results.size();
    }

    private static class OperationResult {
        private long executionTime;
        private long operations;

        public OperationResult(long executionTime, long operations) {
            this.executionTime = executionTime;
            this.operations = operations;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public long getOperations() {
            return operations;
        }
    }
}


