import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main
{
	public static void initializeJFrame(JFrame jf)
	{
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //makes the exit icon work correctly
//		jf.setSize(1100, 800);				 //sets the size of the window
		jf.pack();
		jf.setTitle("test");				  // sets the title bar
		jf.setVisible(true);
		
	}
	
//	public static void test()
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		//condition that detects OS
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		/*	declarations and initializations	*/
		JFrame window = new JFrame();
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		VectorImageViewer jp = new VectorImageViewer(new File("/Users/student/BlankMap-World.svg"));
		JList<Path2D.Double> jl = new JList<>(jp.out.toArray(new Path2D.Double[jp.out.size()]));
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("SVG file", "svg"));
		JMenuItem open = new JMenuItem(new AbstractAction("Open")
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					fc.showOpenDialog(null);
					jp.setFile(fc.getSelectedFile());
					jp.repaint();
					jl.setListData(jp.out.toArray(new Path2D.Double[jp.out.size()]));
				}
				catch (Exception err)	//placeholder
				{
					System.err.println("test");
				}
			}
		});
		file.add(open);
		bar.add(file);
				
		window.setLayout(new GridBagLayout());
		
		JSlider js = new JSlider(JSlider.VERTICAL, 1, 10, 4);
		js.createStandardLabels(1);
		js.setMajorTickSpacing(1);
		js.setPaintTicks(true);
		js.setSnapToTicks(true);
		
		JScrollPane sp = new JScrollPane(jp, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setPreferredSize(new Dimension(500,300));
		
		JViewport vp = sp.getViewport();
		GridBagConstraints c = new GridBagConstraints();
		JScrollPane jlsp = new JScrollPane(jl);
		ChangeListener cl = new ChangeListener()
		{//anonymous class
//			int zoom;
			
			public void stateChanged(ChangeEvent e)
			{
				int zoom = ((JSlider)e.getSource()).getValue();
				System.out.println(zoom/4.0);
				jp.at.setToScale(zoom/4.0, zoom/4.0);
				jp.scale(zoom/4.0);
				jp.repaint();
			}
		};
		js.addChangeListener(cl);

		MouseAdapter mouse = new MouseAdapter()
		{
			int lastX, lastY;
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				jp.clicked = 
					jp.out.parallelStream()
						.map(shape -> shape.createTransformedShape(jp.at))
						.filter(shape -> shape.contains(e.getPoint()))
						.peek(System.out::println)
						.min(
							(Shape p1, Shape p2) ->
								(p1.getBounds().width * p1.getBounds().height) - (p2.getBounds().width * p2.getBounds().height))
					.get();
				System.out.println("\n"+jp.clicked);
				jl.setSelectedValue(jp.clicked, true);
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				lastX = e.getX();
				lastY = e.getY();
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
			}
		
			@Override
			public void mouseDragged(MouseEvent e)
			{
				vp.setViewPosition
				(
					new Point
					(	
						vp.getViewPosition().x + (lastX - e.getX()), 
						vp.getViewPosition().y + (lastY - e.getY())
					)
				);
				lastX = e.getX();
				lastY = e.getY();
			}
		};
		sp.addMouseListener(mouse);
		sp.addMouseMotionListener(mouse);
		
		JPanel imageContainer = new JPanel();
		imageContainer.add(sp);
		imageContainer.add(js);
		
		/*	adding everything to the JFrame	*/
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		window.add(imageContainer, c);
						
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		window.add(jlsp, c);
//		window.add(jsp);
		window.setJMenuBar(bar);
		initializeJFrame(window);		  //initializes the window to your settings

//test
	}
	
}
