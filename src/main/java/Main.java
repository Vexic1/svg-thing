import java.io.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Main
{
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException
	{
		SVGParser test = new SVGParser();
		ArrayList<Path2D.Double> out = test.parseFile(new File("/Users/student/BlankMap-World.svg"));
	}
}