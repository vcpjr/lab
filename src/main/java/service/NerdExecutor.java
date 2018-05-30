package service;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import dao.KGNodeDAO;
import pojo.KGNode;
import pojo.Tweet;
import pojo.dbpediaspotlight.Annotation;
import util.CSVReport;
import util.TweetFileReader;

public class NerdExecutor {

    private static final String APP_ROOT = System.getProperty("user.dir");
    private static final Logger LOG = LoggerFactory.getLogger(NerdExecutor.class);
    private static final File outputPath = new File(APP_ROOT,"output");

    private final List<Tweet> tweets;
    private final SpotlightRest rest;
    private final String inputFilename;

    public NerdExecutor(File datasetFile) {
        rest = new SpotlightRest();
        tweets = TweetFileReader.readTweetsFromFile(datasetFile);
        String filePath = datasetFile.getName();
        inputFilename = filePath.split("\\.")[0];
    }
    
    /**
     * Importa as planilhas geradas pelo NerdExecutor nos primeiros experimentos (05/2017)
     */
    public void importBR2015Dataset(){
    	
    	
    }

    public void execute(float confidence, String language) {
    	//TODO alterar para incluir: 
    	//Alterar para gerar uma planilha só
    	//tweet id (id), user id, text, pre-processed text, confidence, mention, URI, Chosen class, Request time

        //TODO confirmar a contagem de HITS (criar KGNode? Salvar no banco?)
        //Diretos: menções a instâncias
        //Indiretos por Type: uma classe diretamente relacionada à uma instância
        //Indiretos por Subclass-Of: uma classe é relacionada através de uma relação de subsumption até uma classe type de uma instância
        
        /*
         * Exemplo: 
         * 
         * Resource(directHits, indirectHitsType, indirectHitsSubclassOf)
         * 			
         *    	 Thing(0,0,2)
         *	       |
         *	     Person(0,0,2)
         *	       |
         *	     Singer(0,2,0)
         *    	/             \
         * Selena(1,0,0)	 Anitta(1,0,0)			   
         * 
         * */
        
    	KGNodeDAO dao = new KGNodeDAO();
    	ArrayList<Integer> annotatedInstanceIds = new ArrayList<>();
        for(int i = 0; i < tweets.size(); ++i) {
            try {
            	Tweet t = tweets.get(i);
                final Annotation annotated = rest.getAnnotation(t.getText(), confidence, language);
                annotated.getResources().forEach(resource -> {
	            	String uri = resource.getURI();
	            	KGNode nodeInstance = dao.getKGNode(uri, KGNode.RELATIONSHIP_INSTANCE, 1);
	            	
	            	if(nodeInstance != null){
	            		int directHitsOnInstance = nodeInstance.getDirectHits();
	            		
	            		int nodeInstanceId = dao.insert(nodeInstance);
	            		if(!annotatedInstanceIds.contains(nodeInstanceId)){
	            			annotatedInstanceIds.add(nodeInstanceId);
	            		}
	            		
	            		resource.getTypes().forEach(classType-> {
	            			String dbpediaTypeURI = KGNode.getDBpediaClassURI(classType);
	            			//System.out.println("Type: " + dbpediaTypeURI);
	            			
	            			if(dbpediaTypeURI != null){
	            				KGNode nodeClassType = dao.getKGNode(dbpediaTypeURI, KGNode.RELATIONSHIP_TYPE_OF, directHitsOnInstance);
	            				int classTypeId = dao.insertType(nodeInstanceId, nodeClassType);
	            				//System.out.println("INSERT (Type, Instance): (" + nodeClassType.getUri() +"," + nodeInstance.getUri() + ")" );
	            				
	            				ArrayList<KGNode> subclasses = dao.getSuperclassesOf_SpotlightQuery(nodeClassType);
	            				subclasses.forEach(subclass ->{
	            					//System.out.println("Subclass: " + subclass.getUri());
	            					KGNode nodeClassSubclassOf = dao.getKGNode(subclass.getUri(), KGNode.RELATIONSHIP_SUBCLASS_OF, directHitsOnInstance);
	            					dao.insertSuperclass(classTypeId, nodeClassSubclassOf);
	            				});
	            			}
	            		});
	            	}
                });
            } catch (UnexpectedStatusCodeException e) {
                LOG.error(String.format(Locale.US,
                    "Unexpected status code error: tweet=%s, confidence=%.2f, language=%s",
                    tweets.get(i).getText(), confidence, language),
                    e);
            } catch (JsonSyntaxException e) {
                LOG.error("Json Syntax error to annotate tweet message: '" + tweets.get(i).getText() + "'", e);
            } catch (Exception e) {
                LOG.error("Unknown exception", e);
            }
        }

        generateReportCSV(annotatedInstanceIds, language, confidence);
    }

	
	private void generateReportCSV(ArrayList<Integer> instanceIds, String language, float confidence) {
    	CSVReport nerdReport = new CSVReport("Resource; #Direct Hits; #Indirect Hits (Type); #Indirect Hits (Subclass)");
    	KGNodeDAO dao = new KGNodeDAO();
    	
    	LOG.info("******************NerdExecutor CSV generation*****************");

    	ArrayList<KGNode> resources = new ArrayList<>();
    	for(Integer id: instanceIds){
    		KGNode n = dao.getById(id, null);
    		System.out.println("* Instance");
    		appendNodeOnReport(n, resources, nerdReport);
    		ArrayList<KGNode> types = dao.getTypesByInstanceId(n.getId(), dao.getConnection());
    		for(KGNode type: types){
    			System.out.println("** Type");
    			appendNodeOnReport(type, resources, nerdReport);
    			
    			Connection conn = dao.getConnection();
        		ArrayList<KGNode> subclasses = dao.getSuperclassesPath(type.getId(), type.getId(), new ArrayList<>(), conn);
        		dao.closeConnection();
        		for(KGNode subclass: subclasses){
        			System.out.println("*** Subclass");
        			appendNodeOnReport(subclass, resources, nerdReport);
        		}
    		}
    	}
    	String postfixFilename = String.format(
    			Locale.US,
    			"%s-%s-%s-%.2f.csv",
    			inputFilename.replaceAll("\\s", ""),
    			LocalDate.now().toString(),
    			language,
    			confidence);
    	
    	nerdReport.generate(new File(outputPath, "nerdExecution-" + postfixFilename));

    	LOG.info("***************************NerdExecutor CSV end**************************");
    }

	private void appendNodeOnReport(KGNode n, ArrayList<KGNode> resources, CSVReport nerdReport) {
		if(!KGNodeDAO.containsLabel(resources, n.getLabel())){
			resources.add(n);
			String nodeText = String.format(Locale.US, "%s;%d;%d;%d", n.getLabel(), n.getDirectHits(), n.getIndirectHitsType(), n.getIndirectHitsSubclassOf());
			nerdReport.append(nodeText);
			System.out.println(nodeText);
		}
	}

	
	public static KGNode findByURI(List<KGNode> nodes, String uri) {
	    return nodes.stream().filter(node -> uri.equals(node.getUri())).findFirst().orElse(null);
	}
}