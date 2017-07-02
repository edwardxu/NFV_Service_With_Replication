package algs.faulttolerant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;

import algs.Algorithm;
import graph.Node;
import simulation.Parameters;
import simulation.SDNRoutingSimulator;
import system.DataCenter;
import system.InternetLink;
import system.Request;
import system.Switch;

public class Heuristic implements Algorithm {
	
	private SDNRoutingSimulator simulator = null;
	
	private ArrayList<Request> requests = null; 
	
	private double totalCost = 0d;
	
	private double averageCost = 0d; 
	
	private int numOfAdmittedReqs = 0;
	
	public Heuristic(SDNRoutingSimulator sim, ArrayList<Request> requests) {
		this.simulator = sim;
		this.requests = requests;
	}
	
	public void run() {
		// the heuristic algorithm.
		Collections.sort(this.getRequests(), Request.RequestPacketRateComparator);
		List<Request> admittedRequest = new ArrayList<Request>();
		
		for (Request request : this.getRequests()) {
			// set the ranks of all data centers 
			ArrayList<DataCenter> dcs = new ArrayList<DataCenter>();
			for (Switch swDC : this.simulator.getSwitchesAttachedDataCenters()) {
				
				DataCenter dc = swDC.getAttachedDataCenter();
				dcs.add(dc);
				Set<InternetLink> incidentLinks = new HashSet<InternetLink>();
				double availAccumulativeBandwidth = 0d; 
				for (InternetLink link : this.getSimulator().getNetwork().edgeSet()) {
					if (this.simulator.getNetwork().getEdgeTarget(link).equals(swDC) || this.simulator.getNetwork().getEdgeTarget(link).equals(swDC)){
						incidentLinks.add(link);
						availAccumulativeBandwidth += link.getAvailableBandwidth();
					}
				}
				
				dc.setRank(dc.getAvailableComputing() * availAccumulativeBandwidth);
			}
			// sort the data centers according to the rank
			Collections.sort(dcs, DataCenter.DataCenterRankComparator);
			
			DataCenter dcForActiveInstance = null;
			ArrayList<DataCenter> dcsForStandbyInstances = new ArrayList<DataCenter>();
			
			for (DataCenter candidateDC : dcs) {
				if (null == dcForActiveInstance) {
					if (conditionCheck(candidateDC, request, true, dcForActiveInstance))
						dcForActiveInstance = candidateDC;
					else if (conditionCheck(candidateDC, request, false, dcForActiveInstance)){
						dcsForStandbyInstances.add(candidateDC);
					}
				} else { 
					if (conditionCheck(candidateDC, request, false, dcForActiveInstance)){
						dcsForStandbyInstances.add(candidateDC);
					} else 
						break;
				}
			}
			
			if (null != dcForActiveInstance && !dcsForStandbyInstances.isEmpty()){
				// found active instance and its stand-by instances.
				// admit this request.
				Switch sourceSwitch = request.getSourceSwitch();
				Switch destinationSwitch = request.getDestinationSwitches().get(0);
				List<InternetLink> sPath = new ArrayList<InternetLink>();
				double processingCostReq = dcForActiveInstance.getCosts()[request.getServiceChainType()] * request.getPacketRate();
				
				double pathCostReq = 0d;
				if (!sourceSwitch.equals(dcForActiveInstance.getAttachedSwitch())) {
					DijkstraShortestPath<Node, InternetLink> shortestPath = new DijkstraShortestPath<Node, InternetLink>(this.simulator.getNetwork(), sourceSwitch, dcForActiveInstance.getAttachedSwitch());
					for (int i = 0; i < shortestPath.getPathEdgeList().size(); i ++) {
						sPath.add(shortestPath.getPathEdgeList().get(i)); 	
						pathCostReq += shortestPath.getPathEdgeList().get(i).getLinkCost() * request.getPacketRate();
					}
				}
				
				if (!destinationSwitch.equals(dcForActiveInstance.getAttachedSwitch())) {
					DijkstraShortestPath<Node, InternetLink> shortestPath = new DijkstraShortestPath<Node, InternetLink>(this.simulator.getNetwork(), dcForActiveInstance.getAttachedSwitch(), destinationSwitch);
					for (int i = 0; i < shortestPath.getPathEdgeList().size(); i ++) {						
						sPath.add(shortestPath.getPathEdgeList().get(i));
						pathCostReq += shortestPath.getPathEdgeList().get(i).getLinkCost() * request.getPacketRate();
					}
				}
				
				for (InternetLink il : sPath){
					il.admitRequest(request);
				}
				
				double pathCostReqUpdate = 0; 
				List<InternetLink> sPathUpdate = new ArrayList<InternetLink>();
				for (DataCenter dcStandBy : dcsForStandbyInstances) {
					if (!dcForActiveInstance.getAttachedSwitch().equals(dcStandBy.getAttachedSwitch())) {
						DijkstraShortestPath<Node, InternetLink> shortestPath = new DijkstraShortestPath<Node, InternetLink>(this.simulator.getNetwork(), dcForActiveInstance.getAttachedSwitch(), dcStandBy.getAttachedSwitch());
						for (int i = 0; i < shortestPath.getPathEdgeList().size(); i ++) {
							sPathUpdate.add(shortestPath.getPathEdgeList().get(i)); 
							pathCostReqUpdate += shortestPath.getPathEdgeList().get(i).getLinkCost() * request.getPacketRate() * Parameters.updateRatio;
						}
					}
				}
				
				this.setTotalCost(this.getTotalCost() + pathCostReq + pathCostReqUpdate + processingCostReq);
				for (InternetLink il : sPathUpdate) {
					il.admitRequestStateUpdate(request);
				}
				admittedRequest.add(request);	
			}
		}
		
		this.setNumOfAdmittedReqs(admittedRequest.size());
		this.setAverageCost(this.getTotalCost() / this.getNumOfAdmittedReqs());
		
	}
	
	private boolean conditionCheck(DataCenter dc, Request request, boolean activeInstance, DataCenter activeInstanceDC){
		boolean DCOK = false; 
		
		if (activeInstance) {
			// computing resource requirement
			DCOK &= (dc.getAvailableComputing() > request.getServiceChain().getComputingResourceDemand() * request.getPacketRate());
			
			if (!DCOK)
				return DCOK;
			// bandwidth resource requirement
			double bandwidthAvailSP = 0;
			double delay = Double.MAX_VALUE; 
			
			Switch sourceSwitch = request.getSourceSwitch();
			Switch destinationSwitch = request.getDestinationSwitches().get(0);
			Switch swDC = dc.getAttachedSwitch();
			
			if (!sourceSwitch.equals(swDC)) {
				DijkstraShortestPath<Node, InternetLink> shortestPathSToDC = new DijkstraShortestPath<Node, InternetLink>(this.simulator.getNetwork(), sourceSwitch, swDC);
				for (int i = 0; i < shortestPathSToDC.getPathEdgeList().size(); i ++) {
					if (0 == i) {
						bandwidthAvailSP = Double.MAX_VALUE;
						delay = 0d; 
					}
			
					double bandwidthAvailLink = shortestPathSToDC.getPathEdgeList().get(i).getAvailableBandwidth();
					if (bandwidthAvailSP >= bandwidthAvailLink)
						bandwidthAvailSP = bandwidthAvailLink;
					
					delay += shortestPathSToDC.getPathEdgeList().get(i).getLinkDelay();
				}
			}
			
			if (!destinationSwitch.equals(swDC)) {
				DijkstraShortestPath<Node, InternetLink> shortestPathSToDC = new DijkstraShortestPath<Node, InternetLink>(this.simulator.getNetwork(), swDC, destinationSwitch);
				for (int i = 0; i < shortestPathSToDC.getPathEdgeList().size(); i ++) {
					if (0 == i && 0 == bandwidthAvailSP) {
						bandwidthAvailSP = Double.MAX_VALUE;
						delay = 0d; 
					}
			
					double bandwidthAvailLink = shortestPathSToDC.getPathEdgeList().get(i).getAvailableBandwidth();
					if (bandwidthAvailSP >= bandwidthAvailLink)
						bandwidthAvailSP = bandwidthAvailLink;
					
					delay += shortestPathSToDC.getPathEdgeList().get(i).getLinkDelay();
				}
			}
			
			DCOK &= (bandwidthAvailSP >= (request.getPacketRate() * Parameters.packetSize)) & (delay < request.getDelayRequirement());
			
		} else {			
			// bandwidth resource requirement
			
			double bandwidthAvailSP = 0;			
			Switch sourceSwitch = activeInstanceDC.getAttachedSwitch();
			Switch swDC = dc.getAttachedSwitch();
			
			if (!sourceSwitch.equals(swDC)) {
				DijkstraShortestPath<Node, InternetLink> shortestPathSToDC = new DijkstraShortestPath<Node, InternetLink>(this.simulator.getNetwork(), sourceSwitch, swDC);
				for (int i = 0; i < shortestPathSToDC.getPathEdgeList().size(); i ++) {
					if (0 == i) {
						bandwidthAvailSP = Double.MAX_VALUE;
					}
			
					double bandwidthAvailLink = shortestPathSToDC.getPathEdgeList().get(i).getAvailableBandwidth();
					if (bandwidthAvailSP >= bandwidthAvailLink)
						bandwidthAvailSP = bandwidthAvailLink;
				}
			}
			
			DCOK &= (bandwidthAvailSP >= (request.getPacketRate() * Parameters.packetSize * Parameters.updateRatio));
		}
		return DCOK;
	}

	public SDNRoutingSimulator getSimulator() {
		return simulator;
	}

	public void setSimulator(SDNRoutingSimulator simulator) {
		this.simulator = simulator;
	}

	public ArrayList<Request> getRequests() {
		return requests;
	}

	public void setRequests(ArrayList<Request> requests) {
		this.requests = requests;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getAverageCost() {
		return averageCost;
	}

	public void setAverageCost(double averageCost) {
		this.averageCost = averageCost;
	}

	public int getNumOfAdmittedReqs() {
		return numOfAdmittedReqs;
	}

	public void setNumOfAdmittedReqs(int numOfAdmittedReqs) {
		this.numOfAdmittedReqs = numOfAdmittedReqs;
	}

}
