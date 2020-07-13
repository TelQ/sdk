package com.telq.sdk.utils;

import com.telq.sdk.exceptions.httpExceptions.clientSide.*;
import com.telq.sdk.exceptions.httpExceptions.serverSide.InternalServerError;
import com.telq.sdk.exceptions.httpExceptions.serverSide.ServiceUnavailable;

public class HttpCodeStatusChecker {

    public static void statusCheck(int codeStatus) throws Exception {
        switch (codeStatus) {
            case 400: throw new BadRequest();
            case 401: throw new Unauthorized();
            case 403: throw new Forbidden();
            case 404: throw new NotFound();
            case 405: throw new MethodNotAllowed();
            case 410: throw new Gone();
            case 418: throw new Teapot();
            case 429: throw new TooManyRequests();
            case 500: throw new InternalServerError();
            case 503: throw new ServiceUnavailable();
        }
    }

}
