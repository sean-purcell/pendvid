package nutella.pendvid;


public class Pendvid {
	public static void main(String[] args) {
		if(args.length < 2) {
			throw new IllegalArgumentException
				("must pass filename as first argument and out dir as second");
		}

		Readvid.readvid(args[0], args[1]);
	}
}
