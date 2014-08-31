package net.amigocraft.mglib.api;

public enum LobbyType {

	STATUS,
	PLAYERS;

	/**
	 * Retrieves a LobbySign value corresponding to the given string.
	 * @param s the string to match.
	 * @return a LobbySign value corresponding to the given string.
	 * @throws IllegalArgumentException if the given string does not match a lobby type.
	 * @since 0.1.0
	 */
	public static LobbyType fromString(String s){
		if (s.equalsIgnoreCase("STATUS")){
			return LobbyType.STATUS;
		}
		else if (s.equalsIgnoreCase("PLAYERS")){
			return LobbyType.PLAYERS;
		}
		else {
			throw new IllegalArgumentException("Invalid string!");
		}
	}

}
