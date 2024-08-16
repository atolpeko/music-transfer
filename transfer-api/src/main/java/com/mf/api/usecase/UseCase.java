package com.mf.api.usecase;

import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.exception.UseCaseException;
import com.mf.api.usecase.exception.InvalidRequestException;
import com.mf.api.usecase.exception.InvalidStateException;
import com.mf.api.usecase.entity.TransferResult;
import com.mf.api.usecase.entity.TransferRequest;

public interface UseCase {

    /**
     * Transfer from source to target.
     *
     * @param request  request object holding transfer data
     *
     * @return result object containing operation result data
     *
     * @throws InvalidRequestException  if either source or target is not permitted
     * @throws AuthorizationException   if fails to authorize into a music service
     * @throws InvalidStateException    if there are no tracks in source
     * @throws UseCaseException         in case of any other error
     */
    TransferResult transfer(TransferRequest request);
}
