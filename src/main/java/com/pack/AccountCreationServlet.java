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
import java.sql.SQLException;

public class AccountCreationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
        int num = Integer.parseInt(request.getParameter("accountNumber"));
        String name = request.getParameter("name");
        int balance = Integer.parseInt(request.getParameter("balance"));
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "system", "Shan1506");

            String sql = "INSERT INTO Account VALUES(?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, num);
            stmt.setString(2, name);
            stmt.setInt(3, balance);
            int count = stmt.executeUpdate();

            if (count > 0) {
                out.println("<h1>Account Created Successfully</h1>");
                RequestDispatcher rd = request.getRequestDispatcher("Services.html");
                rd.include(request, response);
            } else {
                out.println("<h1>Account Creation Failed, Try Again</h1>");
                RequestDispatcher rd = request.getRequestDispatcher("Services.html");
                rd.include(request, response);
            }
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



