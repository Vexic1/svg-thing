import java.awt.geom.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class SVGParser
{
	
	
	public static ArrayList<Path2D.Double> parseFile(File f) throws ParserConfigurationException, SAXException, IOException 
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
		
	private static Path2D.Double parseSVG(String path) throws IOException
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
					
//					case 'A':
					//https://math.stackexchange.com/a/434482
					//https://www.geogebra.org/calculator
/*					construct an arc such that it starts at the current point and ends at the end point
					(such that the arc's ellipse intersects both points)
					equation of an ellipse:
					
						(x - x0)^2				(y - y0)^2
						----------		+		----------	=	1
							a^2						b^2
					
					where x0, y0 is the center (may not be needed),
					a is the semi-major axis (longest radius)
					b is the semi-minor axis (shortest radius)
					
					SVG specifies arcs like so:
					radius 1
					radius 2
					angle ellipse is rotated by
					whether the arc is the shortest between the two points or the longest
					whether the arc goes clockwise or counterclockwise (flips ellipse)
					the end point
					
					Java specifies arcs like so:
					the upper-left corner of the arc's bounding rectangle
					the width and height of the ellipse
					the starting and ending angle
					
					-or-
					
					2 lines that meet, the starting angle tangent to one and the ending angle tangent to the other, given the radius of the arc
					
					-or-
					
					the upper-left corner of the arc
					the dimensions of the ellipse
					the starting and ending angle
*/
//					Arc2D.Double arc = new Arc2D.Double();
//					arc.setArc(out.getCurrentPoint(), size, 0, 0, 0);
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