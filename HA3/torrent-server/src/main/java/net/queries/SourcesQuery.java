package net.queries;

import net.Message;
import net.Query;
import net.queries.requests.SourcesRequest;
import net.queries.responses.SourcesResponse;

import static utils.Constants.SOURCES_ID;

public class SourcesQuery implements Query {
    @Override
    public Message getEmptyRequest() {
        return new SourcesRequest();
    }

    @Override
    public Message getEmptyResponse() {
        return new SourcesResponse();
    }

    @Override
    public int getId() {
        return SOURCES_ID;
    }
}
