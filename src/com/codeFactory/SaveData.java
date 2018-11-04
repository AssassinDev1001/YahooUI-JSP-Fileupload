package com.codeFactory;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Servlet implementation class SaveData
 */
@WebServlet("/SaveData")
public class SaveData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String file_name = request.getParameter("file_name");
		String size = request.getParameter("size");
		String date = request.getParameter("date");
		String type = request.getParameter("type");
		String description = request.getParameter("description");
		String connectionURL = "jdbc:mysql://localhost:3306/upload_db"; 
		Connection connection = null; 

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			if(!connection.isClosed())
			{
				PreparedStatement pstatement = null;
				String queryString = "INSERT INTO upload_info (file_name, size, date, type, description) VALUES (?, ?, ?, ?, ?)";
			      
				pstatement = connection.prepareStatement(queryString);
				pstatement.setString(1, file_name);
				pstatement.setString(2, size);
				if(date.compareTo("") == 0) {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDate localDate = LocalDate.now();
					pstatement.setString(3, dtf.format(localDate));
					
				} else {
					pstatement.setString(3, date);
				}
					
				pstatement.setString(4, type);
				pstatement.setString(5, description);
				pstatement.executeUpdate();
				pstatement.close();
				connection.close();
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
