package com.pack;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBalanceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        
    	PrintWriter out = response.getWriter();
        int accountNumber = Integer.parseInt(request.getParameter("accountNumberCheck"));
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "system", "Shan1506");

            // Check if the account exists
            String checkAccountQuery = "SELECT * FROM account WHERE num = ?";
            PreparedStatement checkAccountStmt = conn.prepareStatement(checkAccountQuery);
            checkAccountStmt.setInt(1, accountNumber);
            ResultSet accountRs = checkAccountStmt.executeQuery();

            if (accountRs.next()) {
                // Account exists, retrieve and display balance
                double currentBalance = accountRs.getDouble("balance");
                out.println("<h1>Current Balance: Rs." + currentBalance + "</h1>");
            } else {
                out.println("<h1>Account does not exist</h1>");
            }

            // Forward back to the services page
            RequestDispatcher rd = request.getRequestDispatcher("Services.html");
            rd.include(request, response);

        } catch (Exception e) {
            out.println("<h1>Exception : " + e.getMessage() + "</h1>");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

