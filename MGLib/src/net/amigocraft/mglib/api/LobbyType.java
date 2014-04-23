package net.amigocraft.mglib.api;

public enum LobbyType {

	STATUS,
	PLAYERS;
	
	/**
	 * Retrieves a LobbySign value corresponding to the given string.
	 * @param s the string to match.
	 * @return a LobbySign value corresponding to the given string.
	 * @since 0.1
	 */
	public static LobbyType fromString(String s){
		if (s.equalsIgnoreCase("STATUS"))
			return LobbyType.STATUS;
		else if (s.equalsIgnoreCase("PLAYERS"))
			return LobbyType.PLAYERS;
		else
			throw new IllegalArgumentException("Invalid string!");
	}
	
}
