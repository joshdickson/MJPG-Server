/**
 * Copyright (c) 2013 Joshua Dickson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package mpegtest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A basic implementation of a MJPG streamer as a Java servlet. MJPG is commonly used to deliver 
 * image information from networked cameras via an HTTP stream. The set up of this class allws 
 * for dynamic image information that is created in real time to be sent to any tool capable of 
 * reading MJPG including several major internet browsers (Safari, Chrome, and Firefox).
 * 
 * We exclude four necessary images, which can be places in the user's home directory. We name
 * our revolving images 'winter', 'spring', 'summer', and 'fall'. Images can be of any type, but
 * this class is set up to read JPG images. Altering the image source type involves changing
 * the ImageIO.write() function call that returns the image as a byte array.
 * 
 * The byte array could also be loaded live and not generated from a static file.
 * 
 * Feedback may be sent to josh dot dickson at wpi dot edu.
 * 
 * @author Joshua Dickson
 * @version December 10, 2013
 */
@SuppressWarnings("serial")
@WebServlet("/Display")
public class MJPG extends HttpServlet {
	
	private final String image = 
		  "FFD8FFE000104A46494600010200000100010000FFDB004300080606070605080707"
		+ "FFD8FFE000104A46494600010200000100010000FFDB004300080606070605080707"
		+ "FFD8FFE000104A46494600010200000100010000FFDB004300080606070605080707";
	private final byte[] imageBytes;
       
    /**
     * Constructor
     * @see HttpServlet#HttpServlet()
     */
    public MJPG() {
        super();
        imageBytes = convertHexStringToByteArray(image);

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// set the proper content type for MJPG
		response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
		
		// get the output stream to write to
		OutputStream outputStream = response.getOutputStream();
		 
		// loop over and send the images while the browser is present and listening, then return
		while(true) {
			try {

				// write the image and wrapper
				outputStream.write((
					"--BoundaryString\r\n" +
					"Content-type: image/jpeg\r\n" +
					"Content-Length: " +
					imageBytes.length +
					"\r\n\r\n").getBytes());
				outputStream.write(imageBytes);
				outputStream.write("\r\n\r\n".getBytes());
				outputStream.flush();	
				
				// force sleep to not overwhelm the browser, simulate ~20 FPS
				TimeUnit.MILLISECONDS.sleep(50);
			}
			
			// There is a problem with the connection (it likely closed), so close
			catch (Exception e) {
				System.exit(0);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// direct post requests to the get method
		doGet(request, response);
	}
	
	/**
	 * Create a signed byte from a hexidecimal string
	 * @param hexString the string to convert
	 * @return the byte representation of the given string
	 */
	public static byte convertHexStringToByte(String hexString) {
		return new Integer(Integer.parseInt(hexString, 16)).byteValue();
	}
	
	
	/**
	 * Create an array of bytes from a long hexidecimal string
	 * @param hexString the string to convert
	 * @return the array of bytes representing the string
	 */
	public static byte[] convertHexStringToByteArray(String hexString) {
		int arraySize = hexString.length() / 2;
		byte[] byteArray = new byte[arraySize];
		int counter = 0;
		for(int i = 0; i <= hexString.length() - 2; i += 2) {
			byteArray[counter] = convertHexStringToByte(hexString.substring(i, i + 2));
			counter++;
		}
		return byteArray;
	}

}
