package net;

import net.responses.*;

import static org.junit.Assert.*;

public class ResponseComparison {
    static void assertResponsesEqual(Response expected, Response actual) {
        assertEquals(expected.getType(), actual.getType());
        switch(expected.getType()) {
            case LIST:
                listResponsesEqual((ListResponseData) expected.getData(),
                        (ListResponseData) actual.getData());
                break;
            case SOURCES:
                sourcesResponsesEqual( (SourcesResponseData) expected.getData(),
                        (SourcesResponseData) actual.getData());
                break;
            case UPDATE:
                updateResponsesEqual( (UpdateResponseData) expected.getData(),
                        (UpdateResponseData) actual.getData());
                break;
            case UPLOAD:
                uploadResponsesEqual( (UploadResponseData) expected.getData(),
                        (UploadResponseData) actual.getData());
                break;
            case STAT:
                statResponsesEqual( (StatResponseData) expected.getData(),
                        (StatResponseData) actual.getData());
                break;
            case GET:
                getResponsesEqual( (GetResponseData) expected.getData(),
                        (GetResponseData) actual.getData());
                break;
            default:
                fail("Unknown request type: " + expected.getType());
        }
    }

    private static void getResponsesEqual(GetResponseData data, GetResponseData data1) { }

    private static void statResponsesEqual(StatResponseData expected, StatResponseData actual) {
        assertArrayEquals(expected.getParts(), actual.getParts());
    }

    private static void uploadResponsesEqual(UploadResponseData expected, UploadResponseData actual) {
        assertEquals(expected.getId(), actual.getId());
    }

    private static void updateResponsesEqual(UpdateResponseData expected, UpdateResponseData actual) {
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    private static void sourcesResponsesEqual(SourcesResponseData expected, SourcesResponseData actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            SourcesResponseData.Source expSource = expected.get(i);
            SourcesResponseData.Source actSource = actual.get(i);

            assertEquals(expSource.getPort(), actSource.getPort());
            assertEquals(expSource.getHost(), actSource.getHost());
        }
    }

    private static void listResponsesEqual(ListResponseData expected, ListResponseData actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            ListResponseData.ListResponseItem expItem = expected.get(i);
            ListResponseData.ListResponseItem actItem = actual.get(i);

            assertEquals(expItem.id, actItem.id);
            assertEquals(expItem.name, actItem.name);
            assertEquals(expItem.size, actItem.size);
        }
    }

}
