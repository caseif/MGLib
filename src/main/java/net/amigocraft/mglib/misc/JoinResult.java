package net.amigocraft.mglib.misc;

public enum JoinResult {

	SUCCESS,
	ROUND_FULL, // not used, RoundFullException thrown instead
	ROUND_PREPARING,
	ROUND_PLAYING,
	INVENTORY_SAVE_ERROR,
	CANCELLED,
	INTERNAL_ERROR

}
