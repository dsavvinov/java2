package net;

import net.queries.requests.*;
import net.queries.responses.*;

/** Double-dispatcher for convenient casting of generic Messages to concrete types **/
public interface MessageHandler<T> {
    default T handle(GetRequest getRequest) {
        throw new IllegalArgumentException("Unexpected message type: GetRequest");
    }

    default T handle(ListRequest listRequest) {
        throw new IllegalArgumentException("Unexpected message type: ListRequest");
    }

    default T handle(SourcesRequest sourcesRequest) {
        throw new IllegalArgumentException("Unexpected message type: SourcesRequest");
    }

    default T handle(StatRequest statRequest) {
        throw new IllegalArgumentException("Unexpected message type: StatRequest");
    }

    default T handle(UpdateRequest updateRequest) {
        throw new IllegalArgumentException("Unexpected message type: UpdateRequest");
    }

    default T handle(UploadRequest uploadRequest) {
        throw new IllegalArgumentException("Unexpected message type: UploadRequest");
    }

    default T handle(GetResponse getResponse) {
        throw new IllegalArgumentException("Unexpected message type: GetResponse");
    }

    default T handle(ListResponse listResponse) {
        throw new IllegalArgumentException("Unexpected message type: ListResponse");
    }

    default T handle(SourcesResponse sourcesResponse) {
        throw new IllegalArgumentException("Unexpected message type: SourcesResponse");
    }

    default T handle(StatResponse statResponse) {
        throw new IllegalArgumentException("Unexpected message type: StatResponse");
    }

    default T handle(UpdateResponse updateResponse) {
        throw new IllegalArgumentException("Unexpected message type: UpdateResponse");
    }

    default T handle(UploadResponse uploadResponse) {
        throw new IllegalArgumentException("Unexpected message type: UploadResponse");
    }

}
