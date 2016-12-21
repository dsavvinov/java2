package core.client;

import java.util.Random;

import static wire.WireMessages.Numbers;

public class TasksProviderPRN implements TasksProvider {
    private final Random rng;
    private final int arraySize;
    public TasksProviderPRN(Random rng, int arraySize) {
        this.rng = rng;
        this.arraySize = arraySize;
    }

    @Override
    public Numbers nextTask() {
        Numbers.Builder builder = Numbers.newBuilder();

        for (int i = 0; i < arraySize; i++) {
            builder.addItems(rng.nextInt());
        }

        return builder.build();
    }
}
