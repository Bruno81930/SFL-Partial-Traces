package org.diagnosis.app.model.queries;

import org.diagnosis.app.exceptions.queries.QueryException;

public interface Query {
    void query() throws QueryException;
}
