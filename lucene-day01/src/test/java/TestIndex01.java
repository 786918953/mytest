import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class TestIndex01 {

    private IndexWriter indexWriter;

    @Before
    public void beforeTest() throws Exception {

        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        FSDirectory directory = FSDirectory.open(new File("G:\\lucene-index"));
        indexWriter = new IndexWriter(directory, indexWriterConfig);

    }

    @After
    public void afterTest() throws Exception{
        indexWriter.commit();
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    @Test
    public void testAddIndex() throws Exception{
        Document document = new Document();
        document.add(new TextField("bookkk","java黄金瓶中一剪梅", Field.Store.YES));
        indexWriter.addDocument(document);
    }

    @Test
    public void testDeleteIndex() throws Exception{
        Term term = new Term("bookkk","java");
        indexWriter.deleteDocuments(term);
    }

    @Test
    public void testDeleteAllIndex() throws Exception{
        indexWriter.deleteAll();
    }

    @Test
    public void testUpdateIndex() throws Exception{
        Document document = new Document();
        document.add(new TextField("name", "123456", Field.Store.YES));
        Term term = new Term("name","solr");
        indexWriter.updateDocument(term, document);
    }


}
