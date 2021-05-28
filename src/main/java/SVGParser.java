import java.awt.geom.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * TODO:
 *		make everything work
 *		write better javadoc
 *		parse XML
 * [insert description here]
 * @author [redacted]
 */
public class SVGParser
{
	static public ArrayList<Path2D.Double> parseFile(File f) throws ParserConfigurationException, SAXException, IOException 
	{
		ArrayList<Path2D.Double> out = new ArrayList<Path2D.Double>();
		
		NodeList paths = DocumentBuilderFactory
			.newDefaultInstance()
			.newDocumentBuilder()
			.parse(f)
			.getDocumentElement()
			.getElementsByTagName("path");
		
		for (int i = 0; i < paths.getLength(); i++)
			out.add
			(
				parseSVG
				(
					paths
						.item(i)
						.getAttributes()
						.getNamedItem("d")
						.getTextContent()
				)
			);
		
		return out;
	}
		
	static private Path2D.Double parseSVG(String path) throws IOException
	{
		Path2D.Double out = new Path2D.Double();
		out.moveTo(0.0,0.0);
		ArrayList<ArrayList<Object>> commands = new ArrayList<ArrayList<Object>>();
		Pattern.compile("(?=[mlhvcsqtaz])",Pattern.CASE_INSENSITIVE).splitAsStream(path)
				.forEach
				(
					s -> 
					{
						ArrayList<Object> command = new ArrayList<Object>();
						command.add(s.charAt(0));
						if (s.charAt(0) != 'z')
						{
							Pattern.compile("[ ,]").splitAsStream(s.substring(1).strip())
								.map(d -> Double.valueOf(d))
								.forEach(d -> command.add(d));
							commands.add(command);
						}
					}
				);
				
		for (ArrayList<Object> command : commands)
		{
			Double x2 = 0.0;
			Double y2 = 0.0;		//coordinates for smooth bezier curves (more practical than getting the previous control/end point through a path iterator)
			switch ((char)command.get(0))
/*			TODO: 
				ensure correct order of parameters, accounting for difference between Java path methods and SVG path commands
				add cases for absolute commands (uppercase)
*/
			{
				case 'M':
					out.moveTo((double)command.remove(1),(double)command.remove(1));
				//neither break nor while loop included because subsequent coordinate pairs are interpreted as "lineto" commands
				case 'L':
					while (command.size() > 1)
						out.lineTo((double)command.remove(1),(double)command.remove(1));
					break;
				case 'm':
					out.moveTo
					(
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY() + (double)command.remove(1)
					);
				//ditto
				case 'l':
					while (command.size() > 1)
						out.lineTo
						(
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY() + (double)command.remove(1)
						);
					break;
				
				case 'H':
					while (command.size() > 1)
						out.lineTo((double)command.remove(1), out.getCurrentPoint().getY());
					break;
					
				case 'h':
					while (command.size() > 1)
						out.lineTo
						(
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY()
						);
					break;
					
				case 'V':
					while (command.size() > 1)
						out.lineTo(out.getCurrentPoint().getX(), (double)command.remove(1));
					break;
					
				case 'v':
					while (command.size() > 1)
						out.lineTo
						(
							out.getCurrentPoint().getX(),
							out.getCurrentPoint().getX() + (double)command.remove(1)
						);
					break;
				
				case 'C':
					while (command.size() > 1)
					{
						Double x1 = (double)command.remove(1);
						Double y1 = (double)command.remove(1);
						x2 = (double)command.remove(1);
						y2 = (double)command.remove(1);
						Double x = (double)command.remove(1);
						Double y = (double)command.remove(1);
						out.curveTo(x1, y1, x2, y2, x, y);
					}
					break;					
					
				case 'c':
					while (command.size() > 1)
					{
						Double x1 = out.getCurrentPoint().getX() + (double)command.remove(1);
						Double y1 = out.getCurrentPoint().getY() + (double)command.remove(1);
						x2 = out.getCurrentPoint().getX() + (double)command.remove(1);
						y2 = out.getCurrentPoint().getY() + (double)command.remove(1);
						Double x = out.getCurrentPoint().getX() + (double)command.remove(1);
						Double y = out.getCurrentPoint().getY() + (double)command.remove(1);
						out.curveTo(x1, y1, x2, y2, x, y);
					}
					break;
					
				case 'S':
					while (command.size() > 1)
					{
						//reflects previous end control point over current point
						Double x1 = 2*out.getCurrentPoint().getX() - x2;
						Double y1 = 2*out.getCurrentPoint().getY() - y2;
						x2 = (double)command.remove(1);
						y2 = (double)command.remove(1);
						out.curveTo
						(
								x1, y1, x2, y2, 
								(double)command.remove(1), 
								(double)command.remove(1)
						);
					}
					break;
					
				case 's':
					while (command.size() > 1)
					{
						//reflects previous end control point over current point
						Double x1 = 2*out.getCurrentPoint().getX() - x2;
						Double y1 = 2*out.getCurrentPoint().getY() - y2;
						x2 = out.getCurrentPoint().getX() + (double)command.remove(1);
						y2 = out.getCurrentPoint().getY() + (double)command.remove(1);
						out.curveTo
						(
								x1, y1, x2, y2, 
								out.getCurrentPoint().getX() + (double)command.remove(1), 
								out.getCurrentPoint().getY() + (double)command.remove(1)
						);
					}
					break;
	
				case 'Q':
					while (command.size() > 1)
					{
						x2 = (double)command.remove(1);	//control point (x2 is really x1)
						y2 = (double)command.remove(1);
						out.quadTo
						(
							x2, y2,
							(double)command.remove(1),
							(double)command.remove(1)
						);
					}
					break;					
					
				case 'q':
					while (command.size() > 1)
					{
						x2 = out.getCurrentPoint().getX() + (double)command.remove(1);	//control point (x2 is really x1)
						y2 = out.getCurrentPoint().getY() + (double)command.remove(1);
						out.quadTo(
							x2, y2,
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY() + (double)command.remove(1)
						);
					}
					break;

				case 'T':
					while (command.size() > 1)
					{
						x2 = 2*out.getCurrentPoint().getX() - x2;	//control point
						y2 = 2*out.getCurrentPoint().getY() - y2;
						out.quadTo
						(
							x2, y2,
							(double)command.remove(1),
							(double)command.remove(1)
						);
					}
					break;

					
				case 't':
					while (command.size() > 1)
					{
						x2 = 2*out.getCurrentPoint().getX() - x2;	//control point
						y2 = 2*out.getCurrentPoint().getY() - y2;
						out.quadTo
						(
							x2, y2,
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY() + (double)command.remove(1)
						);
					}
					break;
					
				case 'A':
					//https://math.stackexchange.com/a/434482
					double w = 2.0*(double)command.remove(1);
					double h = 2.0*(double)command.remove(1);
					double rot = (double)command.remove(1);
					command.remove(1);
					command.remove(1);
					double x = (double)command.remove(1);
					double y = (double)command.remove(1);
					out.getCurrentPoint().getX();
					out.getCurrentPoint().getY();					
					
					out.append(
						new Arc2D.Double(
							w,
							h,
							
						).getPathIterator(affine transform rotate??), true);
/*
				MORE CASES
*/				case 'Z':
				case 'z':
					out.closePath();
					break;
					
				default:
					//define exception??
					System.err.println("Invalid input.");
					System.exit(1);
			}
		}
		return out;
	}
}