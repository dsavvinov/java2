package net.queries;

import net.Message;
import net.Query;
import net.queries.requests.ListRequest;
import net.queries.responses.ListResponse;

import static utils.Constants.LIST_ID;

public class ListQuery implements Query {
    @Override
    public Message getEmptyRequest() {
        return new ListRequest();
    }

    @Override
    public Message getEmptyResponse() {
        return new ListResponse();
    }

    @Override
    public int getId() {
        return LIST_ID;
    }
}
