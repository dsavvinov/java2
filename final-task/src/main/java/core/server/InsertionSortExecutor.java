package core.server;

import static wire.WireMessages.Numbers;

public class InsertionSortExecutor implements TaskExecutor {
    @Override
    public Numbers executeTask(Numbers task) {
        int[] numbers = task.getItemsList().stream().mapToInt(it -> it).toArray();

        for (int i = 0; i < numbers.length; i++) {
            int min = Integer.MAX_VALUE;
            int ind = -1;
            for (int j = i; j < numbers.length; j++) {
                if (numbers[j] < min) {
                    min = numbers[j];
                    ind = j;
                }
            }

            int tmp = numbers[i];
            numbers[i] = min;
            numbers[ind] = tmp;
        }

        Numbers.Builder resultBuilder = Numbers.newBuilder();
        for (int number : numbers) {
            resultBuilder.addItems(number);
        }

        return resultBuilder.build();
    }
}
