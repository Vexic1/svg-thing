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
	
	
	//	methods with relative coords
	private void relMoveTo(Object dx1, Object dy1)
	{
		super.moveTo(this.getCurrentPoint().getX() + (java.lang.Double)dx1 , this.getCurrentPoint().getY() + (java.lang.Double)dy1);
	}
	private void relLineTo(Object dx1, Object dy1)
	{
		super.lineTo(this.getCurrentPoint().getX() + (java.lang.Double)dx1, this.getCurrentPoint().getY() + (java.lang.Double)dy1);
	}
/*	private void relCurveTo(Object dx1, Object dy1, Object dx2, Object dy2, Object dx3, Object dy3)
	{
		java.lang.Double x1 = this.getCurrentPoint().getX();
		java.lang.Double y1 = this.getCurrentPoint().getY();
		super.curveTo(x1 + (java.lang.Double)dx1, y1 + (java.lang.Double)dy1, x1 + (java.lang.Double)dx2, y1 + (java.lang.Double)dy2, x1 + (java.lang.Double)dx3, y1 + (java.lang.Double)dy3);
	}
	private void relQuadTo(Object dx1, Object dy1, Object dx2, Object dy2)
	{
		java.lang.Double x1 = this.getCurrentPoint().getX();
		java.lang.Double y1 = this.getCurrentPoint().getY();
		super.quadTo(x1 + (java.lang.Double)dx1, y1 + (java.lang.Double)dy1, x1 + (java.lang.Double)dx2, y1 + (java.lang.Double)dy2);
	}
*///				methods for relative bezier curves not practical
	
	
	
	public void parseSVG(String path) throws IOException
	{
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
			java.lang.Double x2,y2;		//coordinates for smooth bezier curves (more practical than getting the previous control/end point through a path iterator)
			switch ((String)command.get(0))
/*			TODO: 
				ensure correct order of parameters, accounting for difference between Java path methods and SVG path commands
				add cases for absolute commands (uppercase)
*/
			{
				case "m":
					relMoveTo
					(
							command.remove(1),
							command.remove(1)
					);
				//break not included because subsequent coordinate pairs are interpreted as "lineto" commands
				case "l":
					while (command.size() > 1)
					{
						relLineTo
						(
							command.remove(1),
							command.remove(1)
						);
					}
					break;
					
				case "h":
					while (command.size() > 1)
					{
						relLineTo
						(
							command.remove(1),
							getCurrentPoint().getX()
						);
					}
					break;
					
				case "v":
					while (command.size() > 1)
					{
						relLineTo
						(
							getCurrentPoint().getX(),
							command.remove(1)
						);
					}
					break;
					
				case "c":
					while (command.size() > 1)
					{
						java.lang.Double x1 = getCurrentPoint().getX() + (double)command.remove(1);
						java.lang.Double y1 = getCurrentPoint().getY() + (double)command.remove(1);
						x2 = getCurrentPoint().getX() + (double)command.remove(1);
						y2 = getCurrentPoint().getY() + (double)command.remove(1);
						java.lang.Double x = getCurrentPoint().getX() + (double)command.remove(1);
						java.lang.Double y = getCurrentPoint().getY() + (double)command.remove(1);
						curveTo(x1, y1, x2, y2, x, y);
					}
					break;
					
				case "s":
					while (command.size() > 1)
					{
						//reflects previous end control point over current point
						java.lang.Double x1 = 2*getCurrentPoint().getX() - x2;
						java.lang.Double y1 = 2*getCurrentPoint().getY() - y2;
						x2 = getCurrentPoint().getX() + (double)command.remove(1);
						y2 = getCurrentPoint().getY() + (double)command.remove(1);
						curveTo
						(
								x1, y1, x2, y2, 
								getCurrentPoint().getX() + (double)command.remove(1), 
								getCurrentPoint().getY() + (double)command.remove(1)
						);
					}
					break;
					
				case "q":
					while (command.size() > 1)
					{
						x2 = getCurrentPoint().getX() + (double)command.remove(1);	//control point (x2 is really x1)
						y2 = getCurrentPoint().getY() + (double)command.remove(1);
						quadTo(
							x2, y2,
							getCurrentPoint().getX() + (double)command.remove(1),
							getCurrentPoint().getY() + (double)command.remove(1)
						);
					}
					break;
					
				case "t":
					while (command.size() > 1)
					{
						x2 = 2*getCurrentPoint().getX() - x2;	//control point
						y2 = 2*getCurrentPoint().getY() - y2;
						quadTo
						(
							x2, y2,
							getCurrentPoint().getX() + (double)command.remove(1),
							getCurrentPoint().getY() + (double)command.remove(1)
						);
					}
					break;
/*				case "a":
				MORE CASES
*/				case "z":
					closePath();
					break;
					
				default:
					//throw exception??
					System.err.println("Invalid input.");
					System.exit(1);
			}
		}
	}
}