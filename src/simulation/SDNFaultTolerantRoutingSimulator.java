package simulation;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.ThreadContext;

import algs.faulttolerant.Greedy;
import algs.faulttolerant.Heuristic;
import system.InternetLink;
import system.Switch;

public class SDNFaultTolerantRoutingSimulator extends SDNRoutingSimulator {
	
	public SDNFaultTolerantRoutingSimulator() {
		super();
	}
	
	public static void main(String[] args) {
		
		ArrayList<Runnable> listOfTasks = new ArrayList<>();
		
	    for (String arg : args) {
			switch (arg) {
			case "POA":
				listOfTasks.add(new Thread(() -> performanceOptimalNetworkSizesBR(), "PER-OPT-ALL"));
				break;
			case "POG":
				listOfTasks.add(new Thread(() -> performanceOptimalSDCRatioBR("GEANT"), "PER-OPT-GEANT"));
				break;
			case "PO4755":
				listOfTasks.add(new Thread(() -> performanceOptimalSDCRatioBR("AS4755"), "PER-OPT-AS4755"));
				break;
			case "PO1755":
				listOfTasks.add(new Thread(() -> performanceOptimalSDCRatioBR("AS1755"), "PER-OPT-AS1755"));
				break;
			case "PASA":
				listOfTasks.add(new Thread(() -> performanceApproSplittableNetworkSizesBR(), "PER-APP-SPLITTABLE-ALL"));
				break;
			case "PASG":
				listOfTasks.add(new Thread(() -> performanceApproSplittableSDCRatioBR("GEANT"), "PER-APP-SPLITTABLE-GEANT"));
				break;
			case "PAS4755":
				listOfTasks.add(new Thread(() -> performanceApproSplittableSDCRatioBR("AS4755"), "PER-APP-SPLITTABLE-AS4755"));
				break;
			case "PAS1755":
				listOfTasks.add(new Thread(() -> performanceApproSplittableSDCRatioBR("AS1755"), "PER-APP-SPLITTABLE-AS1755"));
				break;
			case "PAUA":
				listOfTasks.add(new Thread(() -> performanceApproUnSplittableNetworkSizesBR(), "PER-APP-UNSPLITTABLE-ALL"));
				break;
			case "PAUG":
				listOfTasks.add(new Thread(() -> performanceApproUnSplittableSDCRatioBR("GEANT"), "PER-APP-UNSPLITTABLE-GEANT"));
				break;
			case "PAU4755":
				listOfTasks.add(new Thread(() -> performanceApproUnSplittableSDCRatioBR("AS4755"), "PER-APP-UNSPLITTABLE-AS4755"));
				break;
			case "PAU1755":
				listOfTasks.add(new Thread(() -> performanceApproUnSplittableSDCRatioBR("AS1755"), "PER-APP-UNSPLITTABLE-AS1755"));
				break;
			case "IRO":
				listOfTasks.add(new Thread(() -> impactOfMinRhoOptimalBR(), "IMPACT-RHO-OPT"));
				break; 
			case "IRAS":
				listOfTasks.add(new Thread(() -> impactOfMinRhoSplittableBR(), "IMPACT-RHO-APP-SPLITTABLE"));
				break; 
			case "IRAU":
				listOfTasks.add(new Thread(() -> impactOfMinRhoUnSplittableBR(), "IMPACT-RHO-APP-UNSPLITTABLE"));
				break;
			case "IDO":
				listOfTasks.add(new Thread(() -> impactOfDCNumOptimalBR(), "IMPACT-RATIO-OPT"));
				break; 
			case "IDAS":
				listOfTasks.add(new Thread(() -> impactOfDCNumSplittableBR(), "IMPACT-RATIO-APP-SPLITTABLE"));
				break; 
			case "IDAU":
				listOfTasks.add(new Thread(() -> impactOfDCNumUnSplittableBR(), "IMPACT-RATIO-APP-UNSPLITTABLE"));
				break;
			default:
				System.out.println("Unknown argument: " + arg);
				System.exit(1);
			}
	    }

	    listOfTasks.forEach(threadPool::execute);

	    threadPool.shutdown();
	    try {
	      threadPool.awaitTermination(1L, TimeUnit.DAYS);
	    } catch (InterruptedException ie) {
	      ie.printStackTrace();
	    }
		// first set of experiments. 
		//performanceOptimalNetworkSizesBR();
		//performanceOptimalNumReqsBR("GEANT");
		//performanceOptimalNumReqsBR("AS4755");
		//performanceOptimalNumReqsBR("AS1755");
		
		// second set of experiments. 
		//performanceApproSplittableNetworkSizesBR();
		//performanceApproSplittableNumReqsBR("GEANT");
		//performanceApproSplittableNumReqsBR("AS4755");
		//performanceApproSplittableNumReqsBR("AS1755");
		
		// third set of experiments.
		//performanceApproUnSplittableNetworkSizesBR();
		//performanceApproUnSplittableNumReqsBR("GEANT");
		//performanceApproUnSplittableNumReqsBR("AS4755");
		//performanceApproUnSplittableNumReqsBR("AS1755");
		
		// fourth set of experiments
		//impactOfSwitchToDCRatioOptimalBR();
		//impactOfSwitchToDCRatioSplittableBR();
		//impactOfSwitchToDCRatioUnSplittableBR();
		
		// fifth set of experiments
		//impactOfMinRhoOptimalBR();
		//impactOfMinRhoSplittableBR();
		//impactOfMinRhoUnSplittableBR();
		
		
		//performanceApproUnSplittableBRNumReqs("GEANT");
		//performanceHeuristicNumReqs("GEANT");
		//performanceHeuristicNumReqs("AS1755");
		//performanceHeuristicNumReqs("AS4755");
	}
	
	
	public static void performanceHeuristic() {
		
		ThreadContext.put("threadName", "PER-HEU");
		int [] networkSizes = {50, 100, 150, 200, 250};
		int numAlgs = 2;
		
		double [][] aveTotalCosts = new double [networkSizes.length][numAlgs];
		double [][] aveRunningTime = new double [networkSizes.length][numAlgs];
		double [][] aveNumOfAdmitted = new double [networkSizes.length][numAlgs];
		for (int sizeI = 0; sizeI < networkSizes.length; sizeI ++) {
			for (int j = 0; j < numAlgs; j ++) {
				aveTotalCosts[sizeI][j] = 0d;
				aveRunningTime[sizeI][j] = 0d;
				aveNumOfAdmitted[sizeI][j] = 0d;
			}
		}
		
		double numRound = 2;
		
		for (int sizeI = 0; sizeI < networkSizes.length; sizeI ++) {
			
			SDNFaultTolerantRoutingSimulator.logger.info("Number of nodes: " + networkSizes[sizeI]);
			Parameters.numOfNodes = networkSizes[sizeI];
			Parameters.K = (int) (Parameters.numOfNodes * Parameters.ServerToNodeRatio);
			
			for (int round = 0; round < numRound; round ++) {
				
				SDNFaultTolerantRoutingSimulator.logger.info("Round : " + round);
				SDNFaultTolerantRoutingSimulator simulator = new SDNFaultTolerantRoutingSimulator();
				String postFix = "";
				if (round > 0) postFix = "-" + round;
				
				Initialization.initNetwork(simulator, 0, Parameters.numOfNodes, false, postFix);
				Initialization.initDataCenters(simulator, true);
				Initialization.initEdgeWeights(simulator);
				
				Initialization.initUnicastRequests(simulator, false, true, true);
				
				// optimal solution for the problem with identical data rates. 
				Heuristic heuAlg = new Heuristic(simulator, simulator.getUnicastRequests());
				long startTime = System.currentTimeMillis();
				heuAlg.run();			
				long endTime   = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				
				aveTotalCosts[sizeI][0] += (heuAlg.getTotalCost() / numRound);					
				aveRunningTime[sizeI][0] += (totalTime / numRound);
				aveNumOfAdmitted[sizeI][0] += (heuAlg.getNumOfAdmittedReqs() / numRound);
				
				// reset 
				for (Switch sw : simulator.getSwitches()) {
					sw.reset();
					if (null != sw.getAttachedDataCenter())
						sw.getAttachedDataCenter().reset();
				}
				
				for (InternetLink il : simulator.getNetwork().edgeSet())
					il.reset();
				
				// optimal solution for the problem with identical data rates. 
				Greedy greedyAlg = new Greedy(simulator, simulator.getUnicastRequests());
				startTime = System.currentTimeMillis();
				greedyAlg.run(true);
				endTime   = System.currentTimeMillis();
				totalTime = endTime - startTime;
				
				aveTotalCosts[sizeI][1] += (greedyAlg.getTotalCost() / numRound);					
				aveRunningTime[sizeI][1] += (totalTime / numRound);
				aveNumOfAdmitted[sizeI][1] += (greedyAlg.getNumOfAdmittedReqs() / numRound);
				
				// reset 
				for (Switch sw : simulator.getSwitches()) {
					sw.reset();
					if (null != sw.getAttachedDataCenter())
						sw.getAttachedDataCenter().reset();
				}
				
				for (InternetLink il : simulator.getNetwork().edgeSet())
					il.reset();
			}
		}
		
		SDNFaultTolerantRoutingSimulator.logger.info("Num of requests admitted------------------------");
		for (int sizeI = 0; sizeI < networkSizes.length; sizeI ++) {
			String out = networkSizes[sizeI] + " ";
			for (int j = 0; j < numAlgs; j ++)
				out += aveNumOfAdmitted[sizeI][j] + " ";
			
			SDNFaultTolerantRoutingSimulator.logger.info(out);
		}
		
		SDNFaultTolerantRoutingSimulator.logger.info("Average cost---------------------------------");
		for (int sizeI = 0; sizeI < networkSizes.length; sizeI ++) {
			String out = networkSizes[sizeI] + " ";
			for (int j = 0; j < numAlgs; j ++)
				out += aveTotalCosts[sizeI][j] + " ";
			
			SDNFaultTolerantRoutingSimulator.logger.info(out);
		}
		
		SDNFaultTolerantRoutingSimulator.logger.info("Running time--------------------------");
		for (int sizeI = 0; sizeI < networkSizes.length; sizeI ++) {
			String out = networkSizes[sizeI] + " ";
			for (int j = 0; j < numAlgs; j ++)
				out += aveRunningTime[sizeI][j] + " ";
			
			SDNFaultTolerantRoutingSimulator.logger.info(out);
		}
		
		ThreadContext.remove("threadName");
	}

}
