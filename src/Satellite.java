import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Satellite extends WorldObject {
	
	// Map containing info about the optimal Satellite (value) to connect to in order to find the wanted Satellite (key)
	private final Map<Satellite, Satellite> connectionMap;
	// Map of distances between satellites that can see each other
	private final Map<Satellite, Double> distanceMap;
	
	private final String name;

	public Satellite(String name, double latitude, double longitude, double distanceFromSurface) {
		super(latitude, longitude, distanceFromSurface);
		this.name = name;
		
		connectionMap = new HashMap<>();
		distanceMap = new HashMap<>();
	}
	
	/**
	 * Populate the distance map and insert satellites with straight connections to connectionMap
	 * @param satellitesToCheck
	 * @post Satellites with straight connection can be found in the connectionMap
	 */
	public void populateDistanceMap(List<Satellite> satellitesToCheck) {
		for (Satellite s : satellitesToCheck) {
			if (s != this && WorldObject.canConnect(this, s)) {
				distanceMap.put(s, WorldObject.getDistanceBetween(this, s));
				
				// Since straight connection is the shortest route in this universe,
				// populate map with satellites with straight connections available
				connectionMap.put(s, s);
			}
		}
	}
	
	/**
	 * Find optimal routes to satellites not yet found
	 * @param satellitesToCheck
	 */
	public void populateConnectionMap(List<Satellite> satellitesToCheck) {
		for (Satellite s : satellitesToCheck) {
			if (s != this && !connectionMap.containsKey(s)) {
				List<Satellite> dontCheck = new ArrayList<>();
				dontCheck.add(this);
				Satellite bestNeighbour = null;
				double bestDistance = 0;
				for (Satellite neighbour : distanceMap.keySet()) {
					double distance = distanceMap.get(neighbour) + neighbour.recursiveFind(s, dontCheck);

					if (bestNeighbour == null || bestDistance > distance) {
						bestNeighbour = neighbour;
						bestDistance = distance;
					}
				}
				
				if (bestNeighbour != null) {
					connectionMap.put(s, bestNeighbour);
				}
			}
		}
	}
	
	public double recursiveFind(Satellite toFind, List<Satellite> dontCheck) {
		List<Double> findings = new ArrayList<>();
		// Ugly magic number to make sure dead-ends are not used
		findings.add(99999999.9);
		dontCheck.add(this);
		
		for (Satellite neighbour : distanceMap.keySet()) {
			if (!dontCheck.contains(neighbour)) {
				if (neighbour == toFind) {
					findings.add(distanceMap.get(neighbour));
				} else {
					findings.add(distanceMap.get(neighbour) + neighbour.recursiveFind(toFind, dontCheck));
				}
			}
		}
		
		dontCheck.remove(this);
		return Collections.min(findings);
	}
	
	public Satellite getOptimumConnectionTo(Satellite destination) {
		return connectionMap.get(destination);
	}
	
	public double getDistanceTo(Satellite other) {
		return distanceMap.get(other);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		String result = "=== SATELLITE ===\n"
				+ "Name: " + name + "\n"
						+ "Latitude: " + latitude + "(" + Math.toDegrees(latitude) + " deg)\n"
								+ "Longitude: " + longitude + "(" + Math.toDegrees(longitude) + " deg)\n"
										+ "Distance from surface: " + distanceFromSurface + "\n"
						+ "Distance map:\n";
		for (Satellite s : distanceMap.keySet()) {
			result += s.name + ": " + distanceMap.get(s) + " km\n";
		}
		result += "Connection map:\n";
		for (Satellite s : connectionMap.keySet()) {
			result += s.name + " -> " + connectionMap.get(s).name + "\n";
		}
		return result;
	}
}
