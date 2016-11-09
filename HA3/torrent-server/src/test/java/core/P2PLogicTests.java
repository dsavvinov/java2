package core;

import database.DatabaseProvider;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DatabaseProvider.class)
public class P2PLogicTests {
    private ClientDBMock client1db = new ClientDBMock();
}
