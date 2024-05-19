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

public class MoneyTransferServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	String driver = "oracle.jdbc.driver.OracleDriver";
    String url = "jdbc:oracle:thin:@localhost:1521:xe";
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        int senderAccountNumber = Integer.parseInt(request.getParameter("senderAccountNumber"));
        int receiverAccountNumber = Integer.parseInt(request.getParameter("receiverAccountNumber"));
        double transferAmount = Double.parseDouble(request.getParameter("transferAmount"));
        
        if (transferAmount <= 0) {
            out.println("<h1>Invalid transfer amount</h1>");
            return;
        }
        
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "system", "Shan1506");

            // Check if sender and receiver accounts exist
            String checkSenderAccountQuery = "SELECT * FROM account WHERE num = ?";
            PreparedStatement checkSenderAccountStmt = conn.prepareStatement(checkSenderAccountQuery);
            checkSenderAccountStmt.setInt(1, senderAccountNumber);
            ResultSet senderAccountRs = checkSenderAccountStmt.executeQuery();

            String checkReceiverAccountQuery = "SELECT * FROM account WHERE num = ?";
            PreparedStatement checkReceiverAccountStmt = conn.prepareStatement(checkReceiverAccountQuery);
            checkReceiverAccountStmt.setInt(1, receiverAccountNumber);
            ResultSet receiverAccountRs = checkReceiverAccountStmt.executeQuery();

            if (senderAccountRs.next() && receiverAccountRs.next()) {
                // Accounts exist, proceed with money transfer
                double senderCurrentBalance = senderAccountRs.getDouble("balance");
                if (senderCurrentBalance >= transferAmount) {
                    double receiverCurrentBalance = receiverAccountRs.getDouble("balance");
                    double senderNewBalance = senderCurrentBalance - transferAmount;
                    double receiverNewBalance = receiverCurrentBalance + transferAmount;

                    // Update sender's balance
                    String updateSenderBalanceQuery = "UPDATE account SET balance = ? WHERE num = ?";
                    PreparedStatement updateSenderBalanceStmt = conn.prepareStatement(updateSenderBalanceQuery);
                    updateSenderBalanceStmt.setDouble(1, senderNewBalance);
                    updateSenderBalanceStmt.setInt(2, senderAccountNumber);
                    updateSenderBalanceStmt.executeUpdate();

                    // Update receiver's balance
                    String updateReceiverBalanceQuery = "UPDATE account SET balance = ? WHERE num = ?";
                    PreparedStatement updateReceiverBalanceStmt = conn.prepareStatement(updateReceiverBalanceQuery);
                    updateReceiverBalanceStmt.setDouble(1, receiverNewBalance);
                    updateReceiverBalanceStmt.setInt(2, receiverAccountNumber);
                    updateReceiverBalanceStmt.executeUpdate();

                    out.println("<h1>Money transfer of $" + transferAmount + " successful</h1>");
                } else {
                    out.println("<h1>Insufficient balance in sender's account</h1>");
                }
            } else {
                out.println("<h1>Sender or receiver account does not exist</h1>");
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


