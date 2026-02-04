package com.studentapp;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NumberGuessServletTest {

    private NumberGuessServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @Before
    public void setUp() throws Exception {
        servlet = new NumberGuessServlet();
        servlet.init();

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        // Mock dispatcher
        dispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(request.getRequestDispatcher("/index.jsp")).thenReturn(dispatcher);
    }

    @Test
    public void testGuessTooLow() throws Exception {
        Mockito.when(request.getParameter("guess")).thenReturn("1");

        servlet.doPost(request, response);

        Mockito.verify(request).setAttribute(Mockito.eq("message"),
                Mockito.contains("too low"));
        Mockito.verify(dispatcher).forward(request, response);
    }

    @Test
    public void testGuessTooHigh() throws Exception {
        Mockito.when(request.getParameter("guess")).thenReturn("100");

        servlet.doPost(request, response);

        Mockito.verify(request).setAttribute(Mockito.eq("message"),
                Mockito.contains("too high"));
        Mockito.verify(dispatcher).forward(request, response);
    }

    @Test
    public void testCorrectGuess() throws Exception {
        int correctGuess = servlet.getTargetNumber();
        Mockito.when(request.getParameter("guess")).thenReturn(String.valueOf(correctGuess));

        servlet.doPost(request, response);

        Mockito.verify(request).setAttribute(Mockito.eq("message"),
                Mockito.contains("Congratulations"));
        Mockito.verify(request).setAttribute(Mockito.eq("result"),
                Mockito.eq("win"));
        Mockito.verify(dispatcher).forward(request, response);
    }
}