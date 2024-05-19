package com.user;

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

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        String cnfm = request.getParameter("cnfm");
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@localhost:1521:xe"; 

        if (pwd.equals(cnfm)) {
            Connection con = null;
            PreparedStatement ps = null;
            try {
                Class.forName(driver);
                con = DriverManager.getConnection(url, "system", "Shan1506");

                String query = "INSERT INTO Register (Username, Pwd) VALUES (?, ?)";
                ps = con.prepareStatement(query);

                ps.setString(1, username);
                ps.setString(2, pwd);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    out.println("Successfully registered");
                } else {
                    out.println("Not registered");
                }
            } catch (ClassNotFoundException | SQLException e) {
                out.println("Exception: " + e.getMessage());
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    out.println("Error closing resources: " + e.getMessage());
                }
            }
        } else {
            out.println("Passwords do not match");
        }
    }
}


