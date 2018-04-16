package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	public static List<KGNode> readKGNodesFromFile(File file) {
		List<KGNode> nodes = null;
		String filename = file.getName();
		try {
			LOG.info("Reading KG nodes from '" + filename + "' file.");
			nodes = new ArrayList<>();

			workbook = new XSSFWorkbook(new FileInputStream(file));
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			//Pula o cabeçalho
			iterator.next();
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();

				if(currentRow != null & currentRow.toString() != null){
					LOG.info("Reading row: " + currentRow.toString());

					Cell cellURI = currentRow.getCell(6);

					if(cellURI != null && cellURI.toString() != null){
						LOG.info("Reading cell: " + cellURI.toString());
						String label = cellURI.toString();
						String uri = cellURI.getStringCellValue();
						KGNode node = new KGNode(label, uri);
						nodes.add(node);
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

		LOG.info("Read " + nodes.size() + " Knowledge Graphs from file.");
		return nodes;
	}
}