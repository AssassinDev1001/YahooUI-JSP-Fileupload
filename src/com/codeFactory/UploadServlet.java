package com.codeFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;


/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private final String UPLOAD_DIRECTORY = "D:/234";
	private final String ALLOW_EXTENSION = "jpg,png";
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public UploadServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		if(ServletFileUpload.isMultipartContent(request))
		{
			try
			{
				List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				String[] extension_array = ALLOW_EXTENSION.split(",");
				for(FileItem item : multiparts)
				{
					if(!item.isFormField())
					{
						File uploaded_file = new File(item.getName());
						String file_extension = getFileExtension(uploaded_file); 
						
						boolean is_allow = false;
						for(int i = 0; i < extension_array.length; i++)
						{
							if(extension_array[i].compareTo(file_extension) == 0)
							{
								is_allow = true;
								continue;
							}
						}
						
						if(is_allow == false)
						{
							response.setContentType("text/plain");
							response.setCharacterEncoding("UTF-8"); 
							response.getWriter().write("__invalid__");
						} else {
							
							Timestamp timestamp = new Timestamp(System.currentTimeMillis());
							
							String timestamp_str = timestamp.toString().replace(".", "")
													.replace(":", "")
													.replace(" ", "")
													.replace("-", "");
							
							String real_name = uploaded_file.getName();
							String base_name = FilenameUtils.getBaseName(real_name);
							String file_name = base_name + "_" + timestamp_str + "." + file_extension;
							long size = item.getSize();
							item.write(new File(UPLOAD_DIRECTORY + File.separator + file_name));
							
							response.setContentType("text/plain");
							response.setCharacterEncoding("UTF-8"); 
							response.getWriter().write(file_name);
						}

					}
				}
				request.setAttribute("message", "File uploaded successfully.");
			}
			catch(Exception ex)
			{
				request.setAttribute("message", "File upload failed due to : " + ex);
			}
		}
		else
		{
			request.setAttribute("message", "Sorry this servlet only handles file upload request.");
		}
	}
	
	private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

}
