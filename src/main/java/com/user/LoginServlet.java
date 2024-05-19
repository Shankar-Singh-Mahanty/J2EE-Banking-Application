package com.user;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		response.setContentType("text/html");
		
    	PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "system", "Shan1506");

            String sql = "SELECT * FROM Register WHERE Username = ? AND pwd = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                out.println("<h1>Hello " + username + ", Welcome to Banking Services</h1>");
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                RequestDispatcher rd = request.getRequestDispatcher("Services.html");
                rd.include(request, response);
            } else {
                out.println("<h1>Login Failed, Please Login Again</h1>");
                RequestDispatcher rd = request.getRequestDispatcher("Login.html");
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
