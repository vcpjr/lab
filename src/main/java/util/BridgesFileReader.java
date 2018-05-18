package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pojo.KGNode;

public class BridgesFileReader {
	private static final Logger LOG = LoggerFactory.getLogger(BridgesFileReader.class);
	private static Workbook workbook;

	public static HashMap<KGNode, String> readKeyBridgesFromFile(File file) {
		HashMap<KGNode, String> bridges = null;
		String filename = file.getName();
		try {

			LOG.info("Reading KG nodes from '" + filename + "' file.");
			bridges = new HashMap<>();

			workbook = new XSSFWorkbook(new FileInputStream(file));
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			//Pula o cabeçalho
			iterator.next();
			iterator.next();
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();

				if(currentRow != null & currentRow.toString() != null){
					//LOG.info("Reading row: " + currentRow.toString());

					Cell cellClassName = currentRow.getCell(0);
					Cell cellHits = currentRow.getCell(1);
					Cell cellGoodRelationsClass = currentRow.getCell(2);

					//					Tweet t;
					//					if(cellTweetText != null && cellTweetText.toString() != null){
					//						boolean isRetweet = cellTweetText.toString().contains("RT ");
					//						//TODO pegar demais dados da planilha (id, userId, creationDate, isRetweet)
					//						//TODO associar os tweets às anotações?
					//						t = new Tweet(1L, 1L, cellTweetText.toString(), new Date(), isRetweet);
					//					}

					if(cellClassName != null && cellClassName.toString() != null){
						//LOG.info("Reading cell: " + cellURI.toString());
						KGNode node = getNodeFromClassName(cellClassName.toString());
						if(node != null) {
							//TODO pegar hits diretos e hits por type
							node.setDirectHits((int) cellHits.getNumericCellValue());

							if(cellGoodRelationsClass != null && cellGoodRelationsClass.toString() != null
									&& !cellGoodRelationsClass.toString().isEmpty()) {
								bridges.put(node, cellGoodRelationsClass.toString());
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			LOG.error("File '" + filename + "' not found");
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("Unknown Exception.");
			e.printStackTrace();
		}

		LOG.info("Read " + bridges.size() + " Key Bridges from file.");
		return bridges;
	}

	/**
	 * Lê o arquivo que foi gerado nos experimentos de 05/2017 e gera
	 * um arquivo de saída com as contagens corretas
	 * 
	 * @param file
	 * 
	 * @return void, mas escreve um arquivo de saída
	 */
	private static void readNerdExecutorClassesFile(File file){
		
		//TODO continuar
		String filename = file.getName();
		try {
			workbook = new XSSFWorkbook(new FileInputStream(file));
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static KGNode getNodeFromClassName(String className) {

		//DBpedia:Organisation, Schema:Agent....
		String[] parts = className.split(":");

		String uri = null;
		KGNode node = null;
		if(parts != null && parts.length == 2) {
			switch (parts[0]) {
			case "DBpedia":
				uri = "http://dbpedia.org/ontology/";
				break;

			case "Schema":
				uri = "http://schema.org/";
				break;

			default:
				break;
			}
			uri += parts[1];

			//node = new KGNode(uri, parts[1]);
			node = new KGNode(uri, KGNode.NODE_TYPE_CLASS);
		}

		return node;
	}
}