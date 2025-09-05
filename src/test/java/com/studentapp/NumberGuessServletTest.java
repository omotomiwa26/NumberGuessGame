package com.studentapp;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

public class NumberGuessServletTest {
    private NumberGuessServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @Before
    public void setUp() throws Exception {
        servlet = new NumberGuessServlet();
        servlet.init();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testGuessTooLow() throws Exception {
        Mockito.when(request.getParameter("guess")).thenReturn("1");
        servlet.doPost(request, response);
        assertTrue(responseWriter.toString().contains("Your guess is too low"));
    }

    @Test
    public void testGuessTooHigh() throws Exception {
        Mockito.when(request.getParameter("guess")).thenReturn("100");
        servlet.doPost(request, response);
        assertTrue(responseWriter.toString().contains("Your guess is too high"));
    }

    @Test
    public void testCorrectGuess() throws Exception {
        int correctGuess = servlet.getTargetNumber();
        Mockito.when(request.getParameter("guess")).thenReturn(String.valueOf(correctGuess));
        servlet.doPost(request, response);
        assertTrue(responseWriter.toString().contains("Congratulations! You guessed the number!"));
    }

	// New test case to verify the response when the guess is out of the valid range (1-100).
    @Test
    public void testGuessOutOfRange() throws Exception {
        // Mock a guess that is too high, outside the 1-100 range.
        Mockito.when(request.getParameter("guess")).thenReturn("101");
        servlet.doPost(request, response);
        assertTrue(responseWriter.toString().contains("Your guess is outside the range"));
    }

    // New test case to verify the response for a non-numeric guess.
    @Test
    public void testInvalidGuess() throws Exception {
        // Mock an invalid input that cannot be parsed as an integer.
        Mockito.when(request.getParameter("guess")).thenReturn("abc");
        servlet.doPost(request, response);
        assertTrue(responseWriter.toString().contains("That's not a valid number!"));
    }
}

