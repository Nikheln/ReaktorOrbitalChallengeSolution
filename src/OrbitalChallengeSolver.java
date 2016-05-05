import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OrbitalChallengeSolver {
	
	public static final double EARTH_RADIUS = 6371.00;
	
	public static void main(String[] args) {
		String csvFile = "C:\\Users\\Niko\\workspace\\ReaktorOrbitalChallenge\\orbitalChallengeData.csv";
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(csvFile)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		SurfaceUser startPoint = null, endPoint = null;
		List<Satellite> satellites = new ArrayList<>();
		
		for (String line : content.split("\n")) {
			String[] values = line.split(",");
			if (values[0].matches("^SAT.*")) {
				// Satellite
				String name = values[0];
				double latitude = Double.parseDouble(values[1]);
				double longitude = Double.parseDouble(values[2]);
				double altitude = Double.parseDouble(values[3]);
				
				Satellite newSatellite = new Satellite(name, latitude, longitude, altitude);
				satellites.add(newSatellite);
			} else if (values[0].matches("ROUTE")) {
				// Route
				double latitude1 = Double.parseDouble(values[1]);
				double longitude1 = Double.parseDouble(values[2]);
				startPoint = new SurfaceUser(latitude1, longitude1);
				double latitude2 = Double.parseDouble(values[3]);
				double longitude2 = Double.parseDouble(values[4]);
				endPoint = new SurfaceUser(latitude2, longitude2);
			} else {
				// Seed row
				System.out.println(values[0]);
			}
		}
		
		
		// At this point, all users and satellites should be loaded
		satellites.stream().forEach(s -> s.populateDistanceMap(satellites));
		satellites.stream().forEach(s -> s.populateConnectionMap(satellites));
		startPoint.findVisibleSatellites(satellites);
		endPoint.findVisibleSatellites(satellites);
		
		Double shortestDistance = null;
		List<Satellite> shortestRoute = new ArrayList<>();
		for (Satellite startSatellite : startPoint.getVisibleSatellites()) {
			for (Satellite endSatellite : endPoint.getVisibleSatellites()) {
				List<Satellite> intermediates = new ArrayList<>();
				double totalDistance = WorldObject.getDistanceBetween(startPoint, startSatellite) +
						WorldObject.getDistanceBetween(endPoint, endSatellite);
				Satellite lastLink = startSatellite;
				intermediates.add(startSatellite);
				while (lastLink != endSatellite) {
					lastLink = lastLink.getOptimumConnectionTo(endSatellite);
					totalDistance += lastLink.getDistanceTo(intermediates.get(intermediates.size()-1));
					intermediates.add(lastLink);
				}
				
				if (shortestDistance == null || totalDistance < shortestDistance) {
					shortestRoute = intermediates;
					shortestDistance = totalDistance;
				}
			}
		}
		
		String answerString = "Shortest route: ";
		for (Satellite s : shortestRoute) {
			answerString += s.getName() + ",";
		}
		System.out.println(answerString.substring(0, answerString.length()-1));
	}
}
