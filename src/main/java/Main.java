import Printables.Pole;
import entities.OrderItem;
import entities.Receipt;
import entities.Wood;
import exceptions.DatabaseException;
import org.abstractica.javacsg.Geometry3D;
import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;
import persistence.ConnectionPool;
import persistence.Facade;

import javax.persistence.criteria.Order;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws DatabaseException
	{
		ConnectionPool connectionPool = new ConnectionPool();

		JavaCSG csg = JavaCSGFactory.createDefault();

		Geometry3D poles = csg.union3D(Pole.print(20,74));

		csg.view(poles);
	}
}
