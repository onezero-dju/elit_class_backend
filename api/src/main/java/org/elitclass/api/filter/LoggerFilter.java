package org.elitclass.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
public class LoggerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        var req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        var res = new ContentCachingResponseWrapper((HttpServletResponse) response);
        log.info("INIT URI: {}", req.getRequestURI());
        chain.doFilter(req,res);

        var headersNames = req.getHeaderNames();
        var headersValues = new StringBuilder();

        headersNames.asIterator().forEachRemaining(headerKey->{
            var headerValue = req.getHeader(headerKey);

            headersValues.append("[")
                    .append(headerKey)
                    .append(":")
                    .append(headerValue)
                    .append("]");
        });

        var requestBody = new String(req.getContentAsByteArray());

        var uri = req.getRequestURI();
        var method = req.getMethod();
        log.info(">>>> uri: {}, method: {}, header: {}, body: {}",uri,method,headersValues,requestBody);

        var responseHeadersValues = new StringBuilder();

        res.getHeaderNames().forEach(headerKey ->{
            var headerValue = req.getHeader(headerKey);

            responseHeadersValues.append("[")
                    .append(headerKey)
                    .append(":")
                    .append(headerValue)
                    .append("]");
        });
        var responseBody = new String(res.getContentAsByteArray());
        log.info("<<<< uri: {}, method: {}, header: {}, body: {}",uri,method,responseHeadersValues,responseBody);

        res.copyBodyToResponse();


    }


}
