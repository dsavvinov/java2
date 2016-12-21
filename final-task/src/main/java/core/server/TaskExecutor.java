package core.server;

import wire.WireMessages;

public interface TaskExecutor {
    WireMessages.Numbers executeTask(WireMessages.Numbers task);
}
