package system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import graph.Node;
import simulation.SDNRoutingSimulator;

public class Request extends Node {
	
	//private double ID;
	
	private Switch sourceSwitch = null;
	
	// multicast request
	private ArrayList<Switch> destinationSwitches = null;
	
	private int serviceChainType = -1;
	
	private ServiceChain serviceChain = null; 
	
	private double delayRequirement = 0d;//
	
	private double packetRate = 0d;
	
	private Request parent = null;
	
	private boolean isDummy = false; 
	
	public static Comparator<Request> RequestPacketRateComparator = new Comparator<Request>() {
		public int compare(Request req1, Request req2) {

			Double packetRate1 = req1.getPacketRate();
			Double packetRate2 = req2.getPacketRate();

			// ascending order
			return packetRate1.compareTo(packetRate2);

			// descending order
			// return fruitName2.compareTo(fruitName1);
		}
	};
	
	// create a virtual request, please note that this is not a clone constructor. 
	public Request(Request parent, double dataRate) {
		super(SDNRoutingSimulator.idAllocator.nextId(), "Virtual Request");
		this.parent = parent;
		this.packetRate = dataRate;
		this.sourceSwitch = parent.getSourceSwitch();
		this.destinationSwitches = parent.getDestinationSwitches();
		this.delayRequirement = parent.getDelayRequirement(); 
		this.serviceChainType = parent.getServiceChainType();
	}
	
	// create a dummy request node. 
	public Request() {
		super(SDNRoutingSimulator.idAllocator.nextId(), "Dummy Request");
		this.setDummy(true);
	}
	
	public Request(Switch sourceSwitch, ArrayList<Switch> destinationSwitches, 
			double dataRate, int serviceChainType, double delayRequirement){
		super(SDNRoutingSimulator.idAllocator.nextId(), "Request");
		this.setSourceSwitch(sourceSwitch);
		this.setDestinationSwitches(destinationSwitches);
		this.setPacketRate(dataRate); 
		this.setServiceChainType(serviceChainType);
		this.setDelayRequirement(delayRequirement);
		// sort destination nodes in to increasing order of their IDs. 
		Collections.sort(this.getDestinationSwitches());
		
		double SCID = SDNRoutingSimulator.idAllocator.nextId(); 
		ServiceChain sc = new ServiceChain(SCID, "Service Chain: " + SCID, this.getServiceChainType(), false);
		sc.setProcessingCapacity(this.getPacketRate());
		this.setServiceChain(sc);
	}

	public Switch getSourceSwitch() {
		return sourceSwitch;
	}

	public void setSourceSwitch(Switch sourceSwitch) {
		this.sourceSwitch = sourceSwitch;
	}

	public ArrayList<Switch> getDestinationSwitches() {
		return destinationSwitches;
	}

	public void setDestinationSwitches(ArrayList<Switch> destinationSwitches) {
		this.destinationSwitches = destinationSwitches;
	}
	
//	@Override
//	public boolean equals(Object another) {
//		if (this == another)
//			return true;
//
//		if (!(another instanceof MulticastRequest))
//			return false;
//
//		if (!this.getSourceSwitch().equals(((MulticastRequest) another).getSourceSwitch()))
//			return false;
//		
//		if (this.getDestinationSwitches().size() != ((MulticastRequest) another).getDestinationSwitches().size())
//			return false;
//		
//		// check destination switches, whether they are the same. 
//		for (int i = 0; i < this.getDestinationSwitches().size(); i ++){
//			if (!this.getDestinationSwitches().get(i).equals(((MulticastRequest) another).destinationSwitches.get(i)))
//				return false;
//		}
//		
//		return true;
//	}
	
	@Override
	public boolean equals(Object another) {
		if (this == another)
			return true;

		if (!(another instanceof Request))
			return false;

		if (this.getID() == ((Request) another).getID())
			return true;
		else 
			return false;
	}

	public int getServiceChainType() {
		return serviceChainType;
	}

	public void setServiceChainType(int serviceChainType) {
		this.serviceChainType = serviceChainType;
	}

	public double getDelayRequirement() {
		return delayRequirement;
	}

	public void setDelayRequirement(double delayRequirement) {
		this.delayRequirement = delayRequirement;
	}

	public double getPacketRate() {
		return packetRate;
	}

	public void setPacketRate(double dataRate) {
		this.packetRate = dataRate;
	}

	public Request getParent() {
		return parent;
	}

	public void setParent(Request parent) {
		this.parent = parent;
	}

	public boolean isDummy() {
		return isDummy;
	}

	public void setDummy(boolean isDummy) {
		this.isDummy = isDummy;
	}

	public ServiceChain getServiceChain() {
		return serviceChain;
	}

	public void setServiceChain(ServiceChain serviceChain) {
		this.serviceChain = serviceChain;
	}
}
