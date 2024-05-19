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

public class WithdrawServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
        int accountNumber = Integer.parseInt(request.getParameter("accountNumberWithdraw"));
        double amount = Double.parseDouble(request.getParameter("amountWithdraw"));
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "system", "Shan1506");

            // Check if the account exists
            String checkAccountQuery = "SELECT * FROM Account WHERE num = ?";
            PreparedStatement checkAccountStmt = conn.prepareStatement(checkAccountQuery);
            checkAccountStmt.setInt(1, accountNumber);
            ResultSet accountRs = checkAccountStmt.executeQuery();

            if (accountRs.next()) {
                // Account exists, proceed with withdrawal
                double currentBalance = accountRs.getDouble("balance");
                if (currentBalance >= amount) {
                    double newBalance = currentBalance - amount;

                    // Update balance
                    String updateBalanceQuery = "UPDATE Account SET balance = ? WHERE num = ?";
                    PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                    updateBalanceStmt.setDouble(1, newBalance);
                    updateBalanceStmt.setInt(2, accountNumber);
                    int updateCount = updateBalanceStmt.executeUpdate();

                    if (updateCount > 0) {
                        out.println("<h1>Withdrawal of Rs. " + amount + " successful</h1>");
                    } else {
                        out.println("<h1>Withdrawal failed</h1>");
                    }
                } else {
                    out.println("<h1>Insufficient balance</h1>");
                }
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


