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
//		Dimension dim = new Dimension(500,600);
		
//		this.setPreferredSize(dim);
	 //   this.setLayout(null);
		this.setVisible(true);
		this.setFocusable(true);
			  
	}
	
	public void drawItems(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		int maxX = 0;
		int maxY = 0;
		for (Path2D.Double shape : out)
		{
			maxX = (int)shape.getBounds2D().getMaxX() > maxX ? (int)shape.getBounds2D().getMaxX() : maxX;
			maxY = (int)shape.getBounds2D().getMaxY() > maxY ? (int)shape.getBounds2D().getMaxX() : maxY;
			g2.draw(shape);
		}
		this.setPreferredSize(new Dimension(maxX, maxY));
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);  //very important to put this first all the time
	   
		drawItems(g);
	}
	
}