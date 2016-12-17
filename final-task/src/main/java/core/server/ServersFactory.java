package core.server;

import core.server.tcp.*;
import core.server.udp.FixedThreadPoolUDPServer;
import core.server.udp.MultipleThreadUDPServer;
import util.Log;
import util.Parameters;
import wire.Protocol;

public class ServersFactory {
    private final Protocol protocol;
    private final TaskExecutor executor;
    private final Log log;

    public ServersFactory(Protocol protocol, TaskExecutor executor, Log log) {
        this.protocol = protocol;
        this.executor = executor;
        this.log = log;
    }

    public Server createServer(ServerType type, Parameters params) {
        switch (type) {
            case SINGLE_THREAD_TCP:
                return new SingleThreadTCPServer(log, protocol, executor, params, false);
            case MULTITHREAD_TCP:
                return new MultiThreadTCPServer(log, protocol, executor, params, true);
            case CACHED_POOL_TCP:
                return new CachedThreadPoolServer(log, protocol, executor, params, true);
            case ASYNC_TCP:
                return new AsyncServer(log, protocol, executor, params, true);
            case NIO_TCP:
                return new NIOServer(log, protocol, executor, params, true, false);
            case MULTITHREAD_UDP:
                return new MultipleThreadUDPServer(log, protocol, executor, params, false);
            case FIXED_POOL_UDP:
                return new FixedThreadPoolUDPServer(log, protocol, executor, params, false);
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }

    public enum ServerType {
        SINGLE_THREAD_TCP,
        MULTITHREAD_TCP,
        CACHED_POOL_TCP,
        ASYNC_TCP,
        NIO_TCP,
        MULTITHREAD_UDP,
        FIXED_POOL_UDP;

        public static ServerType forInt(int arg) {
            switch(arg) {
                case 0: return SINGLE_THREAD_TCP;
                case 1: return MULTITHREAD_TCP;
                case 2: return CACHED_POOL_TCP;
                case 3: return ASYNC_TCP;
                case 4: return NIO_TCP;
                case 5: return MULTITHREAD_UDP;
                case 6: return FIXED_POOL_UDP;
                default:
                    throw new IllegalArgumentException("Unknown Server Type id");
            }
        }
    }
}
