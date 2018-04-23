package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
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
import pojo.Tweet;

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
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();

				if(currentRow != null & currentRow.toString() != null){
					//LOG.info("Reading row: " + currentRow.toString());

					Cell cellTweetId = currentRow.getCell(0);
					Cell cellTweetUserId = currentRow.getCell(1);
					Cell cellTweetText = currentRow.getCell(2);
					Cell cellURI = currentRow.getCell(6);
					Cell cellChosenClass = currentRow.getCell(7);

					Tweet t;
					if(cellTweetText != null && cellTweetText.toString() != null){
						boolean isRetweet = cellTweetText.toString().contains("RT ");
						//TODO pegar demais dados da planilha (id, userId, creationDate, isRetweet)
						//TODO associar os tweets às anotações?
						t = new Tweet(1L, 1L, cellTweetText.toString(), new Date(), isRetweet);
					}

					if(cellURI != null && cellURI.toString() != null &&
							cellChosenClass != null && cellChosenClass.toString() != null){
						//LOG.info("Reading cell: " + cellURI.toString());
						String uri = cellURI.toString();
						KGNode node = new KGNode(uri);
						bridges.put(node, cellChosenClass.toString());
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
}