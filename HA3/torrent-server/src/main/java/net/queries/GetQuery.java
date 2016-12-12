package net.queries;

import net.Message;
import net.Query;
import net.queries.requests.GetRequest;

import static utils.Constants.GET_ID;

public class GetQuery implements Query {
    @Override
    public Message getEmptyRequest() {
        return new GetRequest();
    }

    @Override
    public Message getEmptyResponse() {
        // Get-query doesn't have response in form of Message;
        // Rather, it sends raw bytes of file over the wire.
        return null;
    }

    @Override
    public int getId() {
        return GET_ID;
    }
}
