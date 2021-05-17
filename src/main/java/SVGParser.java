import java.awt.geom.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * TODO:
 *		make everything work
 *		write better javadoc
 * [insert description here]
 * @author [redacted]
 */
public class SVGParser
{
//	WHAT??????
/*	public SVGParser(String svg)	//change??
	{
		this.svg = svg;
	  //...?
	}
*/
/*	public SVGParser(File svg)
	{
		
	}
*/	
	
	public Path2D.Double parseSVG(String path) throws IOException
	{
		Path2D.Double out = new Path2D.Double();
		ArrayList<ArrayList<Object>> commands = new ArrayList<ArrayList<Object>>();
		Pattern.compile("(?=[mlhvcsqtaz])",Pattern.CASE_INSENSITIVE).splitAsStream(path)
				.forEach
				(
					s -> 
					{
						ArrayList<Object> command = new ArrayList<Object>();
						command.add(s.charAt(0));
						Pattern.compile("[ ,]").splitAsStream(s.substring(1).strip())
							.map(d -> java.lang.Double.valueOf(d))
							.forEach(d -> command.add(d));
						commands.add(command);
					}
				);
				
		for (ArrayList<Object> command : commands)
		{
			Double x2,y2;		//coordinates for smooth bezier curves (more practical than getting the previous control/end point through a path iterator)
			switch ((String)command.get(0))
/*			TODO: 
				ensure correct order of parameters, accounting for difference between Java path methods and SVG path commands
				add cases for absolute commands (uppercase)
*/
			{
				case "m":
					out.moveTo
					(
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY() + (double)command.remove(1)
					);
				//break not included because subsequent coordinate pairs are interpreted as "lineto" commands
				case "l":
					while (command.size() > 1)
					{
						out.lineTo
						(
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY() + (double)command.remove(1)
						);
					}
					break;
					
				case "h":
					while (command.size() > 1)
					{
						out.lineTo
						(
							out.getCurrentPoint().getX() + (double)command.remove(1),
							out.getCurrentPoint().getY()
						);
					}
					break;
					
				case "v":
					while (command.size() > 1)
					{
						out.lineTo
						(
							out.getCurrentPoint().getX(),
							out.getCurrentPoint().getX() + (double)command.remove(1)
						);
					}
					break;
					
				case "c":
					while (command.size() > 1)
					{
						java.lang.Double x1 = out.getCurrentPoint().getX() + (double)command.remove(1);
						java.lang.Double y1 = out.getCurrentPoint().getY() + (double)command.remove(1);
						x2 = out.getCurrentPoint().getX() + (double)command.remove(1);
						y2 = out.getCurrentPoint().getY() + (double)command.remove(1);
						Double x = out.getCurrentPoint().getX() + (double)command.remove(1);
						Double y = out.getCurrentPoint().getY() + (double)command.remove(1);
						out.curveTo(x1, y1, x2, y2, x, y);
					}
					break;
					
				case "s":
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
					
				case "q":
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
					
				case "t":
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
/*				case "a":
				MORE CASES
*/				case "z":
					out.closePath();
					break;
					
				default:
					//throw exception??
					System.err.println("Invalid input.");
					System.exit(1);
			}
		}
		return out;
	}
}