package org.elitclass.api.exception.api;

import org.elitclass.api.error.ErrorCodeIfs;

public interface ApiExceptionIfs {

    ErrorCodeIfs getErrorCodeIfs();
    String getErrorDescription();
}
