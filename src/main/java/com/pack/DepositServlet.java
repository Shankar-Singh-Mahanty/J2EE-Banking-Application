package com.pack;

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

public class DepositServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:xe";

        int accountNumber = Integer.parseInt(request.getParameter("accountNumberDeposit"));
        int amount = Integer.parseInt(request.getParameter("amountDeposit"));

        PrintWriter out = response.getWriter();

        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, "system", "Shan1506");

            String query = "SELECT balance FROM Account WHERE num=?";
            PreparedStatement psSelect = con.prepareStatement(query);
            psSelect.setInt(1, accountNumber);
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                int currentBalance = rs.getInt("balance");
                int newBalance = currentBalance + amount;

                String updateQuery = "UPDATE Account SET balance=? WHERE num=?";
                PreparedStatement psUpdate = con.prepareStatement(updateQuery);
                psUpdate.setInt(1, newBalance);
                psUpdate.setInt(2, accountNumber);

                int updateCount = psUpdate.executeUpdate();
                if (updateCount > 0)
                    out.println("<h1>Deposit Successful</h1>");
                else
                    out.println("<h1>Deposit Failed</h1>");
            } else {
                out.println("<h1>Account Not Found</h1>");
            }

        } catch (Exception e) {
            out.println("<h1>Exception: " + e.getMessage() + "</h1>");
        }
    }
}
