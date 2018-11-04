package service;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.KGNodeDAO;
import pojo.KGNode;
import util.CSVReport;

/**
 * Executes the CheckComplete algorithm (STEP 2).  
 * 
 * @author Vilmar César Pereira Júnior
 */

public class BridgeExecutor {

	private static final String APP_ROOT = System.getProperty("user.dir");
	private static final Logger LOG = LoggerFactory.getLogger(NerdExecutor.class);
	private static final File outputPath = new File(APP_ROOT,"output");
	private KGNodeDAO dao = new KGNodeDAO();

	// Key: DBpedia resource (class or instance) id
	// Value: Class name from high-level ontology
	private HashMap<Integer, String> keyBridges;
	private HashMap<Integer, String> newBridges;
	private HashMap<Integer, String> inconsistentBridges;

	private KGNode root;

	public BridgeExecutor(){
		dao = new KGNodeDAO();
		keyBridges = dao.getBridges(KGNode.BRIDGE_TYPE_KEY);
	}

	public void execute(){
		//TODO teste com a raiz variável, de acordo com os termos encontrados em cada tweet
		for(Integer idNode: keyBridges.keySet()){
			this.root = dao.getById(idNode, dao.getConnection());

			this.checkComplete(this.root, keyBridges.get(idNode));
		}

		newBridges = dao.getBridges(KGNode.BRIDGE_TYPE_NEW);
		inconsistentBridges = dao.getBridges(KGNode.BRIDGE_TYPE_INCONSISTENT);

		LOG.info("********************************************************************");
		LOG.info("******************BridgeExecutor execution complete*****************");
		LOG.info("************ Key Bridges: " + this.keyBridges.size());
		LOG.info("************ New Bridges: " + this.newBridges.size());
		LOG.info("************ Inconsistent Bridges: " + this.inconsistentBridges.size());

		generateReportCSV();
	}

	private void generateReportCSV() {
		CSVReport bridgeReport = new CSVReport("Resource; #Direct Hits; #Indirect Hits (Type); #Indirect Hits (Subclass); Accumulated; GoodRelations (gr:) class; Type; #Instances associated");

		LOG.info("******************BridgeExecutor keyBridges CSV generation*****************");
		String nodeText;
		HashMap<Integer, String> allBridges = dao.getBridges(null);
		LOG.info("******************BridgeExecutor newBridges CSV generation*****************");

		for(Integer id: allBridges.keySet()){
			Connection conn = dao.getConnection();
			KGNode n = dao.getById(id, conn);
			ArrayList<KGNode> instances = dao.getInstancesByTypeId(n.getId(), conn);

			int accumulatedHits = n.getDirectHits() + n.getIndirectHitsSubclassOf() + n.getIndirectHitsType();
			nodeText = String.format(Locale.US, "%s;%d;%d;%d;%d;%s;%s;%d", n.getLabel(), n.getDirectHits(), n.getIndirectHitsType(), n.getIndirectHitsSubclassOf(), accumulatedHits, allBridges.get(n.getId()), n.getBridgeType(), instances.size());
			bridgeReport.append(nodeText);
		}
		String postfixFilename = String.format(Locale.US,"%s.csv",LocalDate.now().toString());
		bridgeReport.generate(new File(outputPath, "bridgesCheckedCompleted-" + postfixFilename));

		LOG.info("***************************BridgeExecutor CSV end**************************");


	}

	/**
	 * 
	 * @param node node from hierarchy on Knowledge Graph
	 * @param dc domain class bridged to b
	 * 
	 * @returns void, but the algorithm populates newBridges and inconsistentBridges maps
	 */
	private void checkComplete(KGNode node, String domainClass){

		KGNodeDAO dao = new KGNodeDAO();
		ArrayList<KGNode> directSubclassesFromNode = dao.getDirectSubclassesFromNode(node.getId(), dao.getConnection());

		List<Integer> idsFromKeyBridges = new ArrayList<Integer>(keyBridges.keySet());

		if(directSubclassesFromNode.isEmpty()) {
			dao.insertBridge(node.getUri(), domainClass, KGNode.BRIDGE_TYPE_NEW);
		}else {
			for(KGNode childNode: directSubclassesFromNode){
				if(childNode != null && !childNode.getLabel().contains(KGNode.URL_ROOT)){
					String childDomainClass = keyBridges.get(childNode.getId());
					if(childDomainClass == null){
						checkComplete(childNode, domainClass);
					}else{
						if(idsFromKeyBridges.contains(childNode.getId())){
							LOG.info("** Domain class: "+ domainClass + "exists on keyBridges");
							if(keyBridges.get(childNode.getId()) != null 
									&& keyBridges.get(childNode.getId()).equals(domainClass)){
								dao.insertBridge(childNode.getUri(), domainClass, KGNode.BRIDGE_TYPE_NEW);
							}else{
								dao.insertBridge(childNode.getUri(), domainClass, KGNode.BRIDGE_TYPE_INCONSISTENT);
							}
						}else {
							dao.insertBridge(childNode.getUri(), domainClass, KGNode.BRIDGE_TYPE_NEW);
						}
					}
				}

			}
			dao.closeConnection();
		}
	}

	public boolean containsLabel(final List<KGNode> list, final String label){
		return list.stream().filter(o -> o.getLabel().equals(label)).findFirst().isPresent();
	}
}