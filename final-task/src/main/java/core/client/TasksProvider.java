package core.client;

import wire.WireMessages;

public interface TasksProvider {
    WireMessages.Numbers nextTask();
}
