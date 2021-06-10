import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.event.*;
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
	
	static class PathViewer extends JPanel
	{
		AffineTransform at = new AffineTransform();
		Path p = new Path(null);
		
		public void setPath(Path p)
		{
			this.p = p;
			repaint();
		}
		public void drawPath(Graphics2D g)
		{
			at.setToIdentity();
			Rectangle2D bounds = p.getBounds2D();
			double ratio = Math.min(getPreferredSize().width/bounds.getWidth(), getPreferredSize().height/bounds.getHeight());
			at.scale(ratio, ratio);
			at.translate(-1*bounds.getX(), -1*bounds.getY());
			g.draw(p.createTransformedShape(at));
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponents(g);
			drawPath((Graphics2D)g);
		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		//condition that detects OS
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		/*	declarations and initializations	*/
		JFrame window = new JFrame();
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		VectorImageViewer jp = new VectorImageViewer(new File("/Users/student/BlankMap-World.svg"));
		jp.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		PathViewer pathViewer = new PathViewer();
		//pathViewer.setOpaque(true);
		pathViewer.setPreferredSize(new Dimension(100,100));
		
		JList<Path> jl = new JList<>(jp.out.toArray(new Path[jp.out.size()]));
		jl.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				pathViewer.setPath(jl.getSelectedValue());
			}
		});
		
		JTree commandTree = new JTree();
		//JList<ArrayList<Object>> commandList = new JList<>();				//
		//JScrollPane commandListContainer = new JScrollPane(commandList);	//replace with jtextpane or jtree?
		
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
					jl.setListData(jp.out.toArray(new Path[jp.out.size()]));
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
		
/*		JPanel pathViewerContainer = new JPanel();
		JPanel pathViewer = new JPanel()
		{
			
			public void drawPathStepwise(Path path)
			{
				Point2D cp = new Point2D.Double();
				Path step;
				ArrayList<Path> pathList = new ArrayList<Path>();
				double[] coords = new double[6];
				PathIterator pi = path.getPathIterator(null);
				while (!pi.isDone())
				{
					switch (pi.currentSegment(coords))
					{
						case PathIterator.SEG_MOVETO:
							cp.setLocation(coords[0], coords[1]);
							step = new Path(null);
							step.moveTo(coords[0],coords[1]);
							pathList.add(step);
							pi.next();
							break;
						case PathIterator.SEG_LINETO:
							cp.setLocation(coords[0], coords[1]);
							step = new Path(null);
							step.moveTo(cp.getX(),cp.getY());
							step.lineTo(coords[0],coords[1]);
							pathList.add(step);
							pi.next();
							break;
						case PathIterator.SEG_QUADTO:
							cp.setLocation(coords[2],coords[3]);
							step = new Path(null);
							step.moveTo(cp.getX(),cp.getY());
							step.quadTo(coords[0], coords[1], coords[2], coords[3]);
							pathList.add(step);
							pi.next();
							break;
						case PathIterator.SEG_CUBICTO:
							cp.setLocation(coords[4],coords[5]);
							step = new Path(null);
							step.moveTo(cp.getX(),cp.getY());
							step.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
							pathList.add(step);
							pi.next();
							break;
						case PathIterator.SEG_CLOSE:
							System.out.println("placeholder");
							pi.next();
							break;
					}
				}
			}
		};
		pathViewerContainer.setPreferredSize(new Dimension(100,100));
		JButton next = new JButton(
			new AbstractAction("Next")
			{
				public void actionPerformed(ActionEvent e)
				{
					
				}
			})
		);
		pathViewerContainer.add(new JButton("test"));
*/		
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
				Point pt = e.getPoint();
				pt.x += vp.getViewPosition().x;
				pt.y += vp.getViewPosition().y;
				Graphics g = jp.getGraphics();
				Point2D.Double trpt = new Point2D.Double();
				try
				{
					jp.at.inverseTransform(pt, trpt);
				} catch (Exception ex)
				{
					System.err.println("placeholder");
				}
				g.drawLine(0, (int)trpt.y, jp.maxX, (int)trpt.y);
				g.drawLine((int)trpt.x, 0, (int)trpt.x, jp.maxY);
				g.setColor(Color.red);
				g.drawLine(0, pt.y, jp.maxX, pt.y);
				g.drawLine(pt.x, 0, pt.x, jp.maxY);
				g.setColor(Color.black);
//				g.drawLine(e.getX(), e.getY(), (int)trpt.x, (int)trpt.y);
				jp.clicked = 
					jp.out.parallelStream()
//						.map(shape -> shape.createTransformedShape(jp.at))
						.filter(shape -> shape.contains(trpt))
						.min(
							(Shape p1, Shape p2) ->
								(p1.getBounds().width * p1.getBounds().height) - (p2.getBounds().width * p2.getBounds().height))
					.get();
//				System.out.println("\n"+jp.clicked);
				jl.setSelectedValue(jp.clicked, true);
//				commandList.setListData(new Vector(jp.clicked.commands));
				commandTree.setModel(JTree.createTreeModel(new Vector(jp.clicked.commands)));	//why is it protected
				
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
		
		c.gridx = 1;
		window.add(commandListContainer, c);
				
		c.gridx = 2;
		window.add(pathViewer, c);
//		window.add(pathViewerContainer, c);
//		window.add(jsp);
		window.setJMenuBar(bar);
		initializeJFrame(window);		  //initializes the window to your settings

//test
	}
	
}
