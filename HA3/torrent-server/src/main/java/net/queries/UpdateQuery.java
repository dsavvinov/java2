package net.queries;

import net.Message;
import net.Query;
import net.queries.requests.UpdateRequest;
import net.queries.responses.UpdateResponse;

import static utils.Constants.UPDATE_ID;

public class UpdateQuery implements Query {
    @Override
    public Message getEmptyRequest() {
        return new UpdateRequest();
    }

    @Override
    public Message getEmptyResponse() {
        return new UpdateResponse();
    }

    @Override
    public int getId() {
        return UPDATE_ID;
    }
}
