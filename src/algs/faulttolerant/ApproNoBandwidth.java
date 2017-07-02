package algs.faulttolerant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import algs.flow.Commodity;
import algs.flow.MinCostFlowEdge;
import graph.Node;
import simulation.SDNRoutingSimulator;
import system.DataCenter;
import system.InternetLink;
import system.Request;
import system.Switch;
import utils.Combination;

public class ApproNoBandwidth {
	
	private SDNRoutingSimulator simulator = null;
	
	private ArrayList<Request> requests = null; 
	
	private double totalCost = 0d;
	
	private double averageCost = 0d; 
	
	private int numOfAdmittedReqs = 0;
	
	public ApproNoBandwidth(SDNRoutingSimulator sim, ArrayList<Request> requests) {
		this.setSimulator(sim);
		this.setRequests(requests);
	}
	
	public void run() {
		;
	}
	
	public static ListenableDirectedWeightedGraph<Node, MinCostFlowEdge> constructAuxiliaryGraph(
			SDNRoutingSimulator simulator, 
			ArrayList<Request> requests, 
			SimpleWeightedGraph<Node, InternetLink> originalGraph, 
			ArrayList<Switch> switchesWithDCs, 
			ArrayList<Commodity> commodities, 
			double networkCapacityScaleDownRatio
			) {
		
		ListenableDirectedWeightedGraph<Node, MinCostFlowEdge> auxiliaryGraph = new ListenableDirectedWeightedGraph<Node, MinCostFlowEdge>(MinCostFlowEdge.class);
	
		// for each candidiate data center, list a set of data centers for stand-by instances. 
		Map<DataCenter, ArrayList<DataCenter>> DCAndStandByDCs = new HashMap<DataCenter, ArrayList<DataCenter>>();
		
		List<DataCenter> dcs = new ArrayList<DataCenter>();
		for (Switch dcSW : simulator.getSwitchesAttachedDataCenters()){
			DataCenter dc = dcSW.getAttachedDataCenter();
			dcs.add(dc);
		}
		
		for (DataCenter dc : dcs){
			List<DataCenter> otherDCs = new ArrayList<DataCenter>();
			
			for (DataCenter dc1 : dcs){
				if (!dc.equals(dc1))
					otherDCs.add(dc1);
			}
			
			for (int i = 0; i < otherDCs.size(); i ++){
				Combination comb = new Combination();
				comb.combination(array, n, combinations);
			}
			
		}
		
		return auxiliaryGraph;
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
