package net;

import net.requests.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RequestsComparison {
    static void assertRequestsEqual(Request expected, Request actual) {
        assertEquals(expected.getType(), actual.getType());
        switch (expected.getType()) {
            case LIST:
                listsRequestsEqual((ListRequestData) expected.getData(),
                        (ListRequestData) actual.getData());
                break;
            case SOURCES:
                sourcesRequestsEqual((SourcesRequestData) expected.getData(),
                        (SourcesRequestData) actual.getData());
                break;
            case UPDATE:
                updateRequestsEqual((UpdateRequestData) expected.getData(),
                        (UpdateRequestData) actual.getData());
                break;
            case UPLOAD:
                uploadRequestsEqual((UploadRequestData) expected.getData(),
                        (UploadRequestData) actual.getData());
                break;
            case STAT:
                statRequestsEqual((StatRequestData) expected.getData(),
                        (StatRequestData) actual.getData());
                break;
            case GET:
                getRequestsEqual((GetRequestData) expected.getData(),
                        (GetRequestData) actual.getData());
                break;
            default:
                fail("Unknown type of request: " + expected.getType());
        }
    }

    private static void getRequestsEqual(GetRequestData expected, GetRequestData actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getPart(), actual.getPart());
    }

    private static void statRequestsEqual(StatRequestData expected, StatRequestData actual) {
        assertEquals(expected.getId(), actual.getId());
    }

    private static void uploadRequestsEqual(UploadRequestData expected, UploadRequestData actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getSize(), actual.getSize());
    }

    private static void updateRequestsEqual(UpdateRequestData expected, UpdateRequestData actual) {
        assertArrayEquals(expected.getIds(), actual.getIds());
        assertEquals(expected.getClientPort(), actual.getClientPort());
    }

    private static void sourcesRequestsEqual(SourcesRequestData expected, SourcesRequestData actual) {
        assertEquals(expected.getRequestedId(), actual.getRequestedId());
    }

    private static void listsRequestsEqual(ListRequestData expected, ListRequestData actual) {
    }

}
