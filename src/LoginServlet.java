//package com.cloud.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

//import com.cloud.pojo.User;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    static Logger logger = Logger.getLogger(LoginServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {

        String emailVal = null, passwordVal = null;
        
        String email = request.getParameter("email");
        String password = request.getParameter("pass");

        String errorMsg = null;
        if (email == null || email.equals("")) 
        {
            errorMsg = "User Name required";
            emailVal = "User Name required";
            passwordVal = null;
        } 
        else if (password == null || password.equals("")) 
        {
            errorMsg = "Password required";
            emailVal = null;
            passwordVal = "Password required";
        }
        String msg = "";
      //  DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
       // Date date = new Date();

        if (errorMsg != null) 
        {

            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
            PrintWriter out = response.getWriter();
            msg = "<font color=black>" + errorMsg + "</font>";
            request.setAttribute("action", "adminLogin");
           // request.setAttribute("date", dateFormat.format(date));
            request.setAttribute("msg", msg);
            request.setAttribute("emailVal", emailVal);
            request.setAttribute("passwordVal", passwordVal);

            rd.include(request, response);
        } 
        else 
        {
            Connection con = (Connection) getServletContext().getAttribute("DBConnection");
            PreparedStatement ps = null;
            ResultSet rs = null;
            ResultSet rs1 = null;
            try 
            {
               ps = con.prepareStatement("select email, uname, pass from Signup where email='" + email + "' and pass='" + password + "'");
               rs = ps.executeQuery();
                if (rs != null) 
                {
                    boolean checkNext = rs.next();
                    if (checkNext == true) 
                    {
                        HttpSession session = request.getSession();
                        session.setAttribute("loginId", email);
                        session.setAttribute("Pass", password);
                       // request.setAttribute("action", "AdminCabDetails");
                        RequestDispatcher rd = getServletContext().getRequestDispatcher("/welcome.jsp");
                        rd.include(request, response);
                    } 
                    else 
                    {
                        request.setAttribute("action", "Login");
                       // request.setAttribute("date", dateFormat.format(date));
                        request.setAttribute("msg", "<font color=black>User Name or Password is wrong.</font>");
                        request.setAttribute("passwordVal", "<font color=black>User Name or Password is wrong.</font>");
                        RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
                        rd.include(request, response);
                    }
                } 
               
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Database connection problem");
                throw new ServletException("DB Connection problem.");
            } finally {
                try {
                    if (rs1 != null) {
                        rs1.close();
                    }
                    rs.close();
                    ps.close();
                } catch (SQLException e) {
                   logger.error("SQLException in closing PreparedStatement or ResultSet");;
                }

            }
        }
    }
}
