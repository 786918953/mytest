import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class TestQuery01 {

    public void QueryTest(Query query) throws Exception {
        // 查询语法
        System.out.println("查询语法：" + query);

        FSDirectory directory = FSDirectory.open(new File("G:\\lucene-index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("总命中数: " + topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("-------华丽分割线----------");
            System.out.println("文档id: " + scoreDoc.doc
                    + "\t文档分值：" + scoreDoc.score);
            // 根据文档id获取指定的文档
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println("图书Id：" + doc.get("id"));
            System.out.println("图书名称：" + doc.get("bookName"));
            System.out.println("图书价格：" + doc.get("bookPrice"));
            System.out.println("图书图片：" + doc.get("bookPic"));
        }
        // 释放资源
        indexReader.close();
    }

    @Test
    public void TestQuery1() throws Exception{
        /*TermQuery termQuery = new TermQuery(new Term("bookName", "java"));
        QueryTest(termQuery);*/

        /*Query bookPrice = NumericRangeQuery.newDoubleRange("bookPrice", 80d, 100d, false, false);
        QueryTest(bookPrice);*/
    }

    @Test
    public void TestQuery2() throws Exception{
        TermQuery q1 = new TermQuery(new Term("bookName", "java"));
        Query q2 = NumericRangeQuery.newDoubleRange("bookPrice", 80d, 100d, false, false);
        BooleanQuery boo = new BooleanQuery();
        boo.add(q1, BooleanClause.Occur.MUST);
        boo.add(q2, BooleanClause.Occur.MUST);
        QueryTest(boo);
    }

    @Test
    public void testQueryParser() throws Exception {
        Analyzer analyzer = new IKAnalyzer();
        QueryParser queryParser = new QueryParser("", analyzer);
        Query parse = queryParser.parse("bookName:java AND bookName:lucene");
        QueryTest(parse);
    }
}
