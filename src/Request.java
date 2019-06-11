import java.util.Arrays;

public class Request {

	public RequestType type;
	public String[] params;

	public Request(RequestType type, String... params) {
		this.type = type;
		this.params = params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(type); 
		for (int i = 0; i < params.length; i++)
			sb.append(" " + params[i]);
		return sb.toString(); 
	}

	public static Request parse(String line) {
		try {
			String[] items = line.trim().split("\\s+");
			switch (items[0].toUpperCase()) {
			case "PLAYERS":
				return new Request(RequestType.PLAYERS);
			case "HELP":
				return new Request(RequestType.HELP);
			case "MOVE":
                 return new Request(RequestType.MOVE, items[1], items[2], items[3]);
             case "STOPSERVER":
                 return new Request(RequestType.STOPSERVER);
            case "EASTEREGG":
                return new Request(RequestType.EASTEREGG);
			case "QUIT":
			case "LOGOUT": 
				return new Request(RequestType.LOGOUT);
			default:
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return new Request(RequestType.INVALID, line);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (!Arrays.equals(params, other.params))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
