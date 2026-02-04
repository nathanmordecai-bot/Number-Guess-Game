package com.studentapp;

import java.io.IOException;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NumberGuessServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private int targetNumber;

    public void init() throws ServletException {
        targetNumber = new Random().nextInt(100) + 1;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String message;
        String shake = null;
        String result = null;

        try {
            int guess = Integer.parseInt(request.getParameter("guess"));

            if (guess < targetNumber) {
                message = "Your guess is too low. Try again!";
                shake = "true";
            } else if (guess > targetNumber) {
                message = "Your guess is too high. Try again!";
                shake = "true";
            } else {
                message = "🎉 Congratulations! You guessed the number!";
                result = "win";
                targetNumber = new Random().nextInt(100) + 1;
            }

        } catch (NumberFormatException e) {
            message = "Invalid input. Please enter a valid number.";
            shake = "true";
        }

        request.setAttribute("message", message);
        request.setAttribute("shake", shake);
        request.setAttribute("result", result);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}