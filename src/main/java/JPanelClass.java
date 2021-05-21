import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class JPanelClass extends JPanel
{
	JViewport vp;
	Rectangle r;
	int maxX = 0;
	int maxY = 0;
	Dimension dim = new Dimension();
	ArrayList<Path2D.Double> out;
	AffineTransform at = new AffineTransform();
	
	JPanelClass() throws ParserConfigurationException, SAXException, IOException
	{
		out = SVGParser.parseFile(new File("/Users/student/BlankMap-World.svg"));
		
		this.setVisible(true);
		this.setFocusable(true);

		for (Path2D.Double shape : out)
		{
			dim.width = (int)shape.getBounds2D().getMaxX() > maxX ? (int)shape.getBounds2D().getMaxX() : dim.width;
			dim.height = (int)shape.getBounds2D().getMaxY() > maxY ? (int)shape.getBounds2D().getMaxX() : dim.height;
		}
		this.setPreferredSize(dim);	
	}
	
	public void scale(int factor)
	{
		dim.width *= factor;
		dim.height *= factor;
		setPreferredSize(dim);
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
		
		g2.setClip(getVisibleRect());
		out.parallelStream()
			.forEach(shape ->
			{
				g2.draw(shape.createTransformedShape(at));
/*				if (shape.createTransformedShape(at).intersects(getVisibleRect()) || getVisibleRect().contains(shape.createTransformedShape(at).getBounds()))
					g2.draw(shape.createTransformedShape(at));
*/			});
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
