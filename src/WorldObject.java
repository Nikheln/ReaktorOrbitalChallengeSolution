
public abstract class WorldObject {
	
	final double latitude, longitude, distanceFromSurface;
	
	public WorldObject(double latitude, double longitude, double distanceFromSurface) {
		this.latitude = Math.toRadians(latitude);
		this.longitude = Math.toRadians(longitude);
		this.distanceFromSurface = distanceFromSurface;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getDistanceFromSurface() {
		return distanceFromSurface;
	}
	
	/**
	 * Check if two objects in the world can see each other
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 * 		True if two objects can see each other, false otherwise
	 */
	public static boolean canConnect(WorldObject obj1, WorldObject obj2) {
		// Calculate if the distance between objects is shorter than the sum of their horizon distances
		return getSurfaceDistanceBetween(obj1, obj2) < getHorizonDistance(obj1) + getHorizonDistance(obj2);
	}
	
	/**
	 * Calculate the distance between two objects in the world
	 * @param obj1
	 * @param obj2
	 * @return
	 * 		Distance between WorldObjects (in km) 
	 */
	public static double getDistanceBetween(WorldObject obj1, WorldObject obj2) {
		double obj1OrigoDist = OrbitalChallengeSolver.EARTH_RADIUS + obj1.distanceFromSurface;
		double obj2OrigoDist = OrbitalChallengeSolver.EARTH_RADIUS + obj2.distanceFromSurface;
		double angle = getCentralAngleBetween(obj1, obj2);
		
		return Math.sqrt(
				Math.pow(obj1OrigoDist,2) +
				Math.pow(obj2OrigoDist, 2) -
				2 * obj1OrigoDist * obj2OrigoDist * Math.cos(angle));
	}
	
	/**
	 * Calculate the distance between the coordinates of two objects in the world
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static double getSurfaceDistanceBetween(WorldObject obj1, WorldObject obj2) {
		return getCentralAngleBetween(obj1, obj2) * OrbitalChallengeSolver.EARTH_RADIUS;
	}
	

	public static double getHorizonDistance(WorldObject object) {
		return Math.sqrt(object.distanceFromSurface *
				(2 * OrbitalChallengeSolver.EARTH_RADIUS + object.distanceFromSurface));
	}
	/**
	 * Calculate the central angle between two objects in the world using spherical law of cosines
	 * @param obj1
	 * @param obj2
	 * @return
	 * 		Central angle between the objects in degress
	 */
	public static double getCentralAngleBetween(WorldObject obj1, WorldObject obj2) {
		return 2*Math.asin(Math.sqrt(
				Math.pow(Math.sin((obj2.latitude-obj1.latitude)/2), 2) +
				Math.cos(obj1.latitude)*Math.cos(obj2.latitude)*Math.pow(Math.sin((obj2.longitude-obj1.longitude)/2), 2)));
	}
}
