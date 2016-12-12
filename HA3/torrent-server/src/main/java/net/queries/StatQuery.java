package net.queries;

import net.Message;
import net.Query;
import net.queries.requests.StatRequest;
import net.queries.responses.StatResponse;

import static utils.Constants.STAT_ID;

public class StatQuery implements Query {
    @Override
    public Message getEmptyRequest() {
        return new StatRequest();
    }

    @Override
    public Message getEmptyResponse() {
        return new StatResponse();
    }

    @Override
    public int getId() {
        return STAT_ID;
    }
}
