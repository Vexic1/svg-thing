import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Main
{
	static GridBagLayout lo = new GridBagLayout();
	public static void initializeJFrame(JFrame jf)
	{
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //makes the exit icon work correctly
		jf.setSize(800, 700);				 //sets the size of the window
		jf.setLayout(lo);
		jf.setTitle("test");				  // sets the title bar
		jf.setVisible(true);
		
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		JFrame window = new JFrame();
		JPanelClass jp = new JPanelClass();
		JSlider js = new JSlider(JSlider.VERTICAL, 1, 10, 4);
		JScrollPane sp = new JScrollPane(jp, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setPreferredSize(new Dimension(700,600));
		JViewport vp = sp.getViewport();
		ChangeListener cl = new ChangeListener()
		{//anonymous class
			int zoom;
			
			public void stateChanged(ChangeEvent e)
			{
				zoom = ((JSlider)e.getSource()).getValue();
				jp.at.setToScale(zoom/4.0, zoom/4.0);	//play with
				jp.repaint();
				jp.revalidate();
			}
			public int getZoom()
			{
				return zoom;
			}
		};
		js.addChangeListener(cl);
				
		window.add(sp);
		window.add(js);

//		drag scroll
//		sp.setAutoscrolls(false);
		MouseAdapter mouse = new MouseAdapter()
		{
			int lastX, lastY;			
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				lastX = e.getX();
				lastY = e.getY();
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				System.out.println(vp.getViewRect());
//				System.out.println(jp.r);
				System.out.println(jp.getVisibleRect());
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				vp.setViewPosition(new Point(vp.getViewPosition().x + (lastX - e.getX()), vp.getViewPosition().y + (lastY - e.getY())));
//				jp.r.translate(lastX - e.getX(), lastY - e.getY());
//				vp.scrollRectToVisible(jp.r);
				lastX = e.getX();
				lastY = e.getY();
			}
		};
		sp.addMouseListener(mouse);
		sp.addMouseMotionListener(mouse);
		initializeJFrame(window);		  //initializes the window to your settings

//test
	}
	
}
