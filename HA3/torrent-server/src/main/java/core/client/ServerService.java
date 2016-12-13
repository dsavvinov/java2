package core.client;

import exceptions.InvalidProtocolException;
import net.queries.responses.ListResponse;
import net.queries.responses.SourcesResponse;
import net.queries.responses.UpdateResponse;
import net.queries.responses.UploadResponse;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Generic facade of the server service, providing common high-level operations
 * with server.
 *
 * It is ServerService implementors responsibility to maintain consistent
 * state of client, like persisting list of seeded files, etc.
 */
public interface ServerService {
    UpdateResponse update() throws IOException, InvalidProtocolException;
    ListResponse list() throws IOException, InvalidProtocolException;
    SourcesResponse sources(int fileID) throws IOException, InvalidProtocolException;
    UploadResponse upload(Path absolutePath) throws IOException, InvalidProtocolException;
}
