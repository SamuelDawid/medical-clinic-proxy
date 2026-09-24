package com.example.medicalclinicproxy.customErrorDecoder;

import com.example.medicalclinicproxy.exceptions.*;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class MedicalClinicClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException feignException = feign.FeignException.errorStatus(methodKey, response);
        HttpStatus status = HttpStatus.valueOf(response.status());
        String responseBody = feignException.contentUTF8();
        log.error("Feign client error. Method: {}, Status: {}, Body: {}",
                methodKey, status, responseBody);
        return switch (status) {
            case NOT_FOUND -> new ResourceNotFoundException(response.request().toString());
            case CONFLICT -> new MedicalClinicConflictException(responseBody);
            case BAD_REQUEST, UNPROCESSABLE_CONTENT -> new MedicalClinicProxyBadRequestException(responseBody, status);
            case UNAUTHORIZED, FORBIDDEN -> new MedicalClinicAuthException(responseBody, status);
            case BAD_GATEWAY, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT, TOO_MANY_REQUESTS -> new RetryableException(
                    response.status(),
                    feignException.getMessage(),
                    response.request().httpMethod(),
                    feignException,
                    (Long) null,
                    response.request()
            );

            default -> status.is5xxServerError()
                    ? new MedicalClinicUnavailableException(responseBody, status)
                    : new MedicalClinicProxyException(responseBody, status);
        };
    }
}
