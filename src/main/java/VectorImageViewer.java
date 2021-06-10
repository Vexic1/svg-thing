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

public class VectorImageViewer extends JPanel
{
	int maxX = 0;
	int maxY = 0;
	JViewport vp;
	Rectangle r;
	ArrayList<Path> out;
	AffineTransform at = new AffineTransform();
	Path clicked;
	
	VectorImageViewer(File svgFile) throws ParserConfigurationException, SAXException, IOException
	{
		out = SVGParser.parseFile(svgFile);
		
		setVisible(true);
		setFocusable(true);

		for (Path shape : out)
		{
			maxX = (int)shape.getBounds2D().getMaxX() > maxX ? (int)shape.getBounds2D().getMaxX() : maxX;
			maxY = (int)shape.getBounds2D().getMaxY() > maxY ? (int)shape.getBounds2D().getMaxX() : maxY;
		}
		setPreferredSize(new Dimension(maxX, maxY));

	}
	VectorImageViewer()
	{
		out = new ArrayList<Path>();
		setVisible(true);
		setFocusable(true);
	}
	
	public void setFile(File svgFile) throws ParserConfigurationException, SAXException, IOException
	{
		out = SVGParser.parseFile(svgFile);
		for (Path shape : out)
		{
			maxX = (int)shape.getBounds2D().getMaxX() > maxX ? (int)shape.getBounds2D().getMaxX() : maxX;
			maxY = (int)shape.getBounds2D().getMaxY() > maxY ? (int)shape.getBounds2D().getMaxX() : maxY;
		}
		setPreferredSize(new Dimension(maxX, maxY));
	}
	
	public void scale(double factor)
	{
		setPreferredSize(new Dimension(
				(int)(maxX*factor), 
				(int)(maxY*factor))
		);
	}
	
	public void drawItems(Graphics2D g2)
	{
		g2.transform(at);
		for (Path shape : out)
			g2.draw(shape);
	}
	
	public void drawItemsInView(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		vp = (JViewport)getParent();
		
		out.parallelStream()
			.forEach(shape ->
			{
//				g2.setColor(Color.blue);
//				g2.draw(shape);
				g2.setColor(Color.black);
				Shape transformed = shape.createTransformedShape(at);
				if (transformed.intersects(getVisibleRect()))
					g2.draw(transformed);
/*					if (shape == clicked)
						g2.fill(transformed);
					else
						g2.draw(transformed);
*/			});
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);  //very important to put this first all the time
//		drawItems((Graphics2D)g);
		drawItemsInView((Graphics2D)g);
	}
	
}
