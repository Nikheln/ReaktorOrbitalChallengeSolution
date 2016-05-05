import java.util.ArrayList;
import java.util.List;

public class SurfaceUser extends WorldObject {
	
	private final List<Satellite> visibleSatellites;

	public SurfaceUser(double latitude, double longitude) {
		super(latitude, longitude, 0.0);
		
		visibleSatellites = new ArrayList<>();
	}
	
	/**
	 * Find satellites visible to this WorldObject
	 * @param allSatellites
	 * @post visibleSatellites is populated with Satellite objects visible from this object
	 */
	public void findVisibleSatellites(List<Satellite> allSatellites) {
		for (Satellite s : allSatellites) {
			System.out.println(WorldObject.getSurfaceDistanceBetween(this, s) + " " + WorldObject.getHorizonDistance(s));
			if (WorldObject.canConnect(this, s)) {
				visibleSatellites.add(s);
			}
		}
	}
	
	public List<Satellite> getVisibleSatellites() {
		return visibleSatellites;
	}
}
