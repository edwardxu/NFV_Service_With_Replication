package system;


import graph.Node;
import simulation.Parameters;
import utils.RanNum;

public class ServiceChain extends Node {
	
	private int serviceChainType = -1;
		
	private double processingCapacity = 0d;
	
	private double computingResourceDemand = 0d;
	
	private ServiceChain parent = null;
		
	private DataCenter homeDataCenter = null;
	
	private Switch switchHomeDataCenter = null; 
	
	public ServiceChain(double id, String name) {
		super(id, name);
		
		// randomly generate the service chain type
		this.setServiceChainType(RanNum.getRandomIntRange(Parameters.serviceChainProcessingDelays.length - 1, 0));
		double minProcessingCapacity = Parameters.serviceChainProcessingCapacities[serviceChainType][0];
		double maxProcessingCapacity = Parameters.serviceChainProcessingCapacities[serviceChainType][1];
		this.setProcessingCapacity(RanNum.getRandomDoubleRange(maxProcessingCapacity, minProcessingCapacity));
		
		double minCompDemand = Parameters.serviceChainComputingDemandPerUnitPacketRate[serviceChainType][0];
		double maxCompDemand = Parameters.serviceChainComputingDemandPerUnitPacketRate[serviceChainType][1];
		this.setComputingResourceDemand(RanNum.getRandomDoubleRange(maxCompDemand, minCompDemand));
		
	}
	
	public ServiceChain(double id, String name, ServiceChain parent, double processingCapacity){
		super(id, name);
		this.setParent(parent);
		if (null != parent)
			this.serviceChainType = parent.getServiceChainType();
		
		this.processingCapacity = processingCapacity;
	}
	
	public ServiceChain(double id, String name, int serviceChainType, boolean serviceChainsWithBasicRate){
		super(id, name);
		this.setServiceChainType(serviceChainType);

		if (!serviceChainsWithBasicRate) {
			double minProcessingCapacity = Parameters.serviceChainProcessingCapacities[serviceChainType][0];
			double maxProcessingCapacity = Parameters.serviceChainProcessingCapacities[serviceChainType][1];
			this.setProcessingCapacity(RanNum.getRandomDoubleRange(maxProcessingCapacity, minProcessingCapacity));
		} else {
			this.setProcessingCapacity(Parameters.minPacketRate);
		}
	}
	
	public ServiceChain(double id, String name, int serviceChainType, double computingDem, double processingCapacity){
		super(id, name);
		this.setComputingResourceDemand(computingDem);
		this.setProcessingCapacity(processingCapacity);
		this.setServiceChainType(serviceChainType);
	}
	
	public double getComputingResourceDemand() {
		return computingResourceDemand;
	}

	public void setComputingResourceDemand(double computingResourceDemand) {
		this.computingResourceDemand = computingResourceDemand;
	}

	public double getProcessingCapacity() {
		return processingCapacity;
	}

	public void setProcessingCapacity(double processingCapacity) {
		this.processingCapacity = processingCapacity;
	}

	public int getServiceChainType() {
		return serviceChainType;
	}

	public void setServiceChainType(int serviceChainType) {
		this.serviceChainType = serviceChainType;
	}

	public ServiceChain getParent() {
		return parent;
	}

	public void setParent(ServiceChain parent) {
		this.parent = parent;
	}

	public DataCenter getHomeDataCenter() {
		return homeDataCenter;
	}

	public void setHomeDataCenter(DataCenter homeDataCenter) {
		this.homeDataCenter = homeDataCenter;
	}

	public Switch getSwitchHomeDataCenter() {
		return switchHomeDataCenter;
	}

	public void setSwitchHomeDataCenter(Switch switchHomeDataCenter) {
		this.switchHomeDataCenter = switchHomeDataCenter;
	}
}
