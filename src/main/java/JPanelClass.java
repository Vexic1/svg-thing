import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class JPanelClass extends JPanel
{
	JViewport vp;
	Rectangle r;
	ArrayList<Path2D.Double> out;
	AffineTransform at = new AffineTransform();
	
	JPanelClass() throws ParserConfigurationException, SAXException, IOException
	{
		out = SVGParser.parseFile(new File("/Users/student/BlankMap-World.svg"));
		
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
		vp = (JViewport)getParent();
		
		out.parallelStream()
			.forEach(shape ->
			{
				if (shape.createTransformedShape(at).intersects(getVisibleRect()))
					g2.draw(shape.createTransformedShape(at));
			});
		g2.draw(getVisibleRect());
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);  //very important to put this first all the time
//		drawItems((Graphics2D)g);
		drawItemsInView((Graphics2D)g);
	}
	
}
