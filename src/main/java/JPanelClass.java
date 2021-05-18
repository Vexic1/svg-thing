

/**
 *
 * @author [redacted]
 * Make a JPanel to add to your window.
 * Most of the functionality of the GUI will be in the JPanels
 * You may have more than one JPanel in a JFrame.
 * Think the look of your GUI project before you start coding!
 */

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class JPanelClass extends JPanel
{
	ArrayList<Path2D.Double> out;
	
	JPanelClass() throws ParserConfigurationException, SAXException, IOException
	{
		// adding objects to your JPanel, Initial state of object
		SVGParser test = new SVGParser();
		out = test.parseFile(new File("/Users/student/BlankMap-World.svg"));
		
		// seting up JPanel properties for display
		Dimension dim = new Dimension(500,600);
		
		this.setPreferredSize(dim);
		this.setBackground(Color.red);
	 //   this.setLayout(null);
		this.setVisible(true);
		this.setFocusable(true);
			  
	}
	
	public void drawItems(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		for (Path2D.Double shape : out)
			g2.draw(shape);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);  //very important to put this first all the time
	   
		drawItems(g);
	}
	
}