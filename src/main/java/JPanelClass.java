import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class JPanelClass extends JPanel
{
	ArrayList<Path2D.Double> out;
	AffineTransform at = new AffineTransform();
	
	JPanelClass() throws ParserConfigurationException, SAXException, IOException
	{
		SVGParser parser = new SVGParser();
		out = parser.parseFile(new File("/Users/student/BlankMap-World.svg"));

		this.setVisible(true);
		this.setFocusable(true);

		int maxX = 0;
		int maxY = 0;
		for (Path2D.Double shape : out)
		{
			maxX = (int)shape.getBounds2D().getMaxX() > maxX ? (int)shape.getBounds2D().getMaxX() : maxX;
			maxY = (int)shape.getBounds2D().getMaxY() > maxY ? (int)shape.getBounds2D().getMaxX() : maxY;
		}
		this.setPreferredSize(new Dimension(maxX, maxY));
	}
		
	public void drawItems(Graphics2D g2)
	{
		g2.transform(at);
		for (Path2D.Double shape : out)
			g2.draw(shape);
	}
	
	public void drawItemsInView(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		JViewport vp = (JViewport)getParent();
		
		g2.transform(at);
		g2.draw((Rectangle2D)vp.getViewRect());
		out.parallelStream()
			.forEach(shape ->
			{
				if (shape.intersects((Rectangle2D)vp.getViewRect()))
					g2.draw(shape);
			});
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);  //very important to put this first all the time
//		drawItems((Graphics2D)g);
		drawItemsInView((Graphics2D)g);
	}
	
}
