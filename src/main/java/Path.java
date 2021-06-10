import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Path extends Path2D.Double
{
	String id;
	ArrayList<ArrayList<Object>> commands;

	public Path(String id)
	{
		super();
		this.id = id;
	}

	@Override
	public String toString()
	{
		if (id != null)
		{
			return id;
		}
		else 
		{
			return super.toString();
		}
	}
}
