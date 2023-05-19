import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import persistence.ConnectionPool;

public class Main
{
	public static void main(String[] args)
	{
		ConnectionPool connectionPool = new ConnectionPool();


		JavaCSG csg = JavaCSGFactory.createDefault();
		Geometry3D cyl = csg.cylinder3D(10, 20, 32, true);
		csg.view(cyl);
	}
}
