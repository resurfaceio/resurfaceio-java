// Copyright (c) 2016 Resurface Labs LLC, All Rights Reserved

package io.resurface;

import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static io.resurface.Mocks.*;
import static org.junit.Assert.assertTrue;

/**
 * Tests against servlet filter for HTTP usage logging.
 */
public class HttpLoggerForServletsTest {

    @Test
    public void logsHtmlCallTest() throws IOException, ServletException {
        HttpLogger logger = HttpLoggerFactory.get().disable().tracingStart();
        try {
            HttpLoggerForServlets filter = new HttpLoggerForServlets();
            filter.doFilter(Mocks.mockRequest(), Mocks.mockResponse(), Mocks.mockHtmlApp());
            assertTrue("tracing history is 2", logger.tracingHistory().size() == 2);
            String message = logger.tracingHistory().get(0);
            assertTrue("has category", message.contains("{\"category\":\"http_request\","));
            assertTrue("has url", message.contains("\"url\":\"" + MOCK_URL + "\"}"));
            message = logger.tracingHistory().get(1);
            assertTrue("has category", message.contains("{\"category\":\"http_response\","));
            assertTrue("has code", message.contains("\"code\":404,"));
            assertTrue("has body", message.contains("\"body\":\"" + MOCK_HTML_ESCAPED + "\"}"));
        } finally {
            logger.tracingStop().enable();
        }
    }

    @Test
    public void logsJsonCallTest() throws IOException, ServletException {
        HttpLogger logger = HttpLoggerFactory.get().disable().tracingStart();
        try {
            HttpLoggerForServlets filter = new HttpLoggerForServlets();
            filter.doFilter(Mocks.mockRequest(), Mocks.mockResponse(), Mocks.mockJsonApp());
            assertTrue("tracing history is 2", logger.tracingHistory().size() == 2);
            String message = logger.tracingHistory().get(0);
            assertTrue("has category", message.contains("{\"category\":\"http_request\","));
            assertTrue("has url", message.contains("\"url\":\"" + MOCK_URL + "\"}"));
            message = logger.tracingHistory().get(1);
            assertTrue("has category", message.contains("{\"category\":\"http_response\","));
            assertTrue("has code", message.contains("\"code\":500,"));
            assertTrue("has body", message.contains("\"body\":\"" + MOCK_JSON_ESCAPED + "\"}"));
        } finally {
            logger.tracingStop().enable();
        }
    }

    @Test
    public void skipsLoggingForUnmatchedCallTest() throws IOException, ServletException {
        HttpLogger logger = HttpLoggerFactory.get().disable().tracingStart();
        try {
            HttpLoggerForServlets filter = new HttpLoggerForServlets();
            filter.doFilter(Mocks.mockRequest(), Mocks.mockResponse(), Mocks.mockCustomApp());
            filter.doFilter(Mocks.mockRequest(), Mocks.mockResponse(), Mocks.mockCustomRedirectApp());
            filter.doFilter(Mocks.mockRequest(), Mocks.mockResponse(), Mocks.mockHtmlRedirectApp());
            filter.doFilter(Mocks.mockRequest(), Mocks.mockResponse(), Mocks.mockJsonRedirectApp());
            assertTrue("tracing history is 0", logger.tracingHistory().size() == 0);
        } finally {
            logger.tracingStop().enable();
        }
    }

}