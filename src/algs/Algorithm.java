package algs;

import java.util.ArrayList;

import simulation.SDNRoutingSimulator;
import system.Request;

public interface Algorithm {
	
	SDNRoutingSimulator simulator = null;
	
	ArrayList<Request> requests = null; 
	
	double totalCost = 0d;
	
	double averageCost = 0d; 
	
	int numOfAdmittedReqs = 0;

}
