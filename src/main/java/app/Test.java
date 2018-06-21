package app;

import java.util.ArrayList;
import java.util.HashMap;

import dao.KGNodeDAO;
import pojo.KGNode;

public class Test {

private static HashMap<String,String> kb = new HashMap<>();
private static HashMap<String,String> nb = new HashMap<>();
private static HashMap<String,String> ib = new HashMap<>();
private static ArrayList<String> hierarchy = new ArrayList<>();

public static void main(String[] args) {
	setUp();
	String r = "Thing";
	int hierarchyIndex = 0;
	String c = null;
	checkComplete(r, c, hierarchyIndex);
	
	System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%");
	System.out.println("IBs: " + ib.size());
	System.out.println("NBs: " + nb.size());
	System.out.println("KBs: " + kb.size());

	for (String key: kb.keySet()) {
		System.out.println("KB: " + key + " - " + kb.get(key));
	}
	
	for (String key: nb.keySet()) {
		System.out.println("NB: " + key + " - " + nb.get(key));
	}
	
	for (String key: ib.keySet()) {
		System.out.println("IB: " + key + " - " + ib.get(key));
	}
	
	System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%");

}

public static void setUp(){
	
	hierarchy.add("Thing");
	hierarchy.add("Organization");
	hierarchy.add("Broadcaster");
	hierarchy.add("RadioStation");

	kb.put("Organization", "BusinessEntity");
	kb.put("RadioStation", "Brand");
}
	
public static void checkComplete(String r, String c, int hierarchyIndex){
	String dc = kb.get(r);
	if(c == null){
		if(dc != null){
			nb.put(r,dc);
			c = dc;
		}
	}else{
		if(dc != null){
			ib.put(r,dc);
		}
	}
	hierarchyIndex++;
	if(hierarchyIndex < hierarchy.size()){
		String child = hierarchy.get(hierarchyIndex);
		checkComplete(child, c, hierarchyIndex);
	}
}

private static ArrayList<String> getChildren(int index) {
	ArrayList<String> res = new ArrayList<>();
	
	for(int i = index; i< hierarchy.size(); i++){
		res.add(hierarchy.get(i));
	}
	
	return res;
}

public void foo(){
 KGNodeDAO dao = new KGNodeDAO();
 
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Agent"),"more general than gr classes", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:AdministrativeRegion"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Album"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Animal"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Anime"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ArchitecturalStructure"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Artist"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Athlete"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Award"),"gr:Brand", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Bacteria"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Band"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Beverage"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Biomolecule"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:BodyOfWater"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Book"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:BritishRoyalty"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Broadcaster"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:BroadcastNetwork"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Building"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Cartoon"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ChemicalCompound"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ChemicalSubstance"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:City"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:ComicsCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Company"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Country"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Currency"),"gr:PriceSpecification.hasCurrency", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Device"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Disease"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:EducationalInstitution"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:EthnicGroup"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Eukaryote"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Event"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:FictionalCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Film"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Fish"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Food"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Genre"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:GivenName"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:GovernmentAgency"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:InformationAppliance"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Infrastructure"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Insect"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Island"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Lake"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Language"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Locomotive"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:LunarCrater"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Magazine"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Mammal"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MeanOfTransportation"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MilitaryUnit"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Museum"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicalArtist"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicalWork"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicFestival"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:MusicGenre"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Name"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:NaturalPlace"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Newspaper"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Non-ProfitOrganisation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:OfficeHolder"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Organisation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PeriodicalLiterature"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Person"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Place"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Plant"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PoliticalParty"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Politician"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:PopulatedPlace"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RaceHorse"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RadioStation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RecordLabel"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Region"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Reptile"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:River"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Road"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Rocket"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:RouteOfTransportation"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Royalty"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Settlement"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Ship"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Single"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoapCharacter"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoccerClub"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SoccerPlayer"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Software"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Species"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SportsLeague"),"gr:Brand", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:SportsTeam"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Stream"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TelevisionShow"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TelevisionStation"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TimePeriod"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:TopicalConcept"),"no gr class", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Town"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:University"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:VideoGame"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Village"),"gr:Location", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Weapon"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Website"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Work"),"gr:Product", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Wrestler"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Writer"),"gr:BusinessEntity", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:WrittenWork"),"gr:ProductOrService", KGNode.BRIDGE_TYPE_KEY);
 dao.insertBridge(KGNode.getDBpediaClassURI("DBpedia:Year"),"no gr class; but may be relevant for e-business", KGNode.BRIDGE_TYPE_KEY);
}
}
