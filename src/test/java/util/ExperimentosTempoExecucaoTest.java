package util;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import dao.KGNodeDAO;
import dao.TweetDAO;

public class ExperimentosTempoExecucaoTest {

	KGNodeDAO kgDAO = new KGNodeDAO();
	TweetDAO tDAO = new TweetDAO();
	
    @Before
    public void setUp() {
    		
    }

    @Test
    public void testCSVContentInMemory() throws IOException {
    }

}
