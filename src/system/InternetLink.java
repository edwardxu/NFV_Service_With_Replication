package system;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

import simulation.Parameters;

public class InternetLink extends DefaultWeightedEdge{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7286332007574167278L;
	
	private double bandwidthCapacity;
		
	private double linkCost;// used in the first problem 
	
	private double linkDelay;
	
	// dynamic properties that need to be reset:
	private double costMetric = 0d;// used in the online problem;
	
	// used by edges in auxiliary graphs
	private InternetLink parent = null;
	
	private List<Request> admittedRequests = new ArrayList<Request>();
	
	private List<Request> admittedRequestsStateUpdate = new ArrayList<Request>();
	
	/**
	 * Default constructor 
	 */
	public InternetLink() {
		super();
	}
	
	public InternetLink(LinkInitialParameters li) {
		super();
		this.bandwidthCapacity = li.capacity;
		this.linkCost = li.linkCost;
		this.linkDelay = li.delay;
	}
	
	public void admitRequest(Request req) {
		if (!this.admittedRequests.contains(req))
			this.admittedRequests.add(req);
	}
	
	public void admitRequestStateUpdate(Request req){
		if (!this.admittedRequestsStateUpdate.contains(req))
			this.admittedRequestsStateUpdate.add(req);
	}
	
	public double getAvailableBandwidth() {
		double occupiedBandwidth = 0d; 
		for (Request req : this.admittedRequests){
			occupiedBandwidth += req.getPacketRate() * Parameters.packetSize;
		}
		
		for (Request req: this.admittedRequestsStateUpdate){
			occupiedBandwidth += req.getPacketRate() * Parameters.packetSize * Parameters.updateRatio;
		}
		
		return this.bandwidthCapacity - occupiedBandwidth; 
	}
	
	/**
	 * Retrieves the source of this edge. 
	 * 
	 * @return source of this edge
	 */
	public Object getSource() {
		return super.getSource();
	}

	/**
	 * Retrieves the target of this edge.
	 * 
	 * @return target of this edge
	 */
	public Object getTarget() {
		return super.getTarget();
	}
	
	@Override
	public String toString() {
		//return super.toString();
		return "Capacity: " + this.bandwidthCapacity;
	}
//	
//	@Override
//	public boolean equals(Object another) {
//
//		// Check for self-comparison
//		if (this == another)
//			return true;
//
//		// Use instanceof instead of getClass here for two reasons
//		// 1. if need be, it can match any supertype, and not just one class;
//		// 2. it renders an explict check for "that == null" redundant, since
//		// it does the check for null already - "null instanceof [type]" always
//		// returns false.
//		if (!(another instanceof InternetLink))
//			return false;
//
//		Object thisS = this.getSource();
//		Object anotherS = ((InternetLink) another).getSource();
//
//		Object thisT = this.getTarget();
//		Object anotherT = ((InternetLink) another).getTarget();
//
//		// The algorithm only accepts comparison between identical V types.
//		if ((!(thisS instanceof Node)) && (!(anotherS instanceof Node))
//				&& (!(thisT instanceof Node)) && (!(anotherT instanceof Node)))
//			return false;
//		
//		if((((Node)thisS).getID() == ((Node)anotherS).getID())&&(((Node)thisT).getID() == ((Node)anotherT).getID()))
//			return true;
//		
//		return false;
//	}
	
	public double getLinkCost() {
		return linkCost;
	}

	public void setLinkCost(double linkCost) {
		this.linkCost = linkCost;
	}

	public double getCostMetric() {
		return costMetric;
	}

	public void setCostMetric(double costMetric) {
		this.costMetric = costMetric;
	}
	
	public void reset() {
		this.setCostMetric(0d);
		this.admittedRequests = new ArrayList<Request>();
		this.admittedRequestsStateUpdate = new ArrayList<Request>();
	}

	public InternetLink getParent() {
		return parent;
	}

	public void setParent(InternetLink parent) {
		this.parent = parent;
	}

	public double getLinkDelay() {
		return linkDelay;
	}

	public void setLinkDelay(double linkDelay) {
		this.linkDelay = linkDelay;
	}

	public List<Request> getAdmittedRequestsStateUpdate() {
		return admittedRequestsStateUpdate;
	}

	public void setAdmittedRequestsStateUpdate(List<Request> admittedRequestsStateUpdate) {
		this.admittedRequestsStateUpdate = admittedRequestsStateUpdate;
	}
}