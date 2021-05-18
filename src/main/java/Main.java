/**
 *
 * @author Mr. Maroney
 * Setup for JFrame
 * 
 */
import javax.swing.JFrame;	 //class for main canvas(window)
import java.awt.*;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Main
{
	
	public static void initializeJFrame(JFrame jf){
		
		jf.getContentPane().setBackground(Color.green);	//set color of main window
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //makes the exit icon work correctly
		jf.setSize(800, 1000);				 //sets the size of the window
   
		jf.setLocationRelativeTo(null);					//centers the window
		jf.setTitle("Rectangles");				  // sets the title bar
		jf.setVisible(true);							  // makes the window visible
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		JFrame window = new JFrame();			//makes a jframe
		
		//LettersMove lm = new LettersMove();
	   // window.add(lm);
		
		JPanelClass jp = new JPanelClass();
		window.add(jp);
	   
		initializeJFrame(window);		  //initializes the window to your settings

	  
	}
	
}
