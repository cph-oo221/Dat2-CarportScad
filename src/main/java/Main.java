import org.abstractica.javacsg.JavaCSG;
import org.abstractica.javacsg.JavaCSGFactory;

public class Main
{
	public static void main(String[] args)
	{
		JavaCSG csg = JavaCSGFactory.createDefault();


//		Rafters poles = new Rafters(csg,55,20, 205, 4);
//		csg.view(poles.generate());


		Rems rem = new Rems(csg, 600, 300, 40);
		csg.view(rem.testGen());
	}
}
