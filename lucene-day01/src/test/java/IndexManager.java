import dao.BookDao;
import dao.impl.BookDaoImpl;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;
import po.Book;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexManager {

    @Test
    public void testCreateIndex() throws Exception {
        // 采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> books = bookDao.findAll();
        // 创建文档集合
        List<Document> documents = new ArrayList<>();
        // 创建文档对象
        Document document = new Document();
        for (Book book : books) {
            // 图书id
            document.add(new LongField("id", book.getId(), Field.Store.YES));
            // 图书名称
            document.add(new TextField("bookName", book.getBookName(), Field.Store.YES));
            // 图书价格
            document.add(new DoubleField("bookPrice", book.getPrice(), Field.Store.YES));
            // 图书图片
            document.add(new StoredField("bookPic", book.getPic()));
            // 图书描述
            document.add(new TextField("bookDesc", book.getBookDesc(), Field.Store.YES));
            documents.add(document);
        }
        // 创建分词器(Analyzer)，用于分词
        Analyzer analyzer = new IKAnalyzer();
        // 创建索引库配置对象，用于配置索引库
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        // 设置索引库打开模式(每次都重新创建)
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // 创建索引库目录对象，用于指定索引库存储位置
        Directory directory = FSDirectory.open(new File("G:\\lucene-index"));
        // 创建索引库操作对象，用于把文档写入索引库
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        // 循环文档，写入索引库
        for (Document doc : documents) {
            indexWriter.addDocument(doc);
            // 提交事务
            indexWriter.commit();
        }
        // 释放资源
        indexWriter.close();
    }

//    @Test
//    public void testCreateIndex() throws Exception {
//
//        BookDao bookDao = new BookDaoImpl();
//        List<Book> bookList = bookDao.findAll();
//        List<Document> documents = new ArrayList<>();
//        for (Book book : bookList) {
//            Document doc = new Document();
//            /**
//             * 图书id
//             * 是否分词：不需要分词
//             是否索引：需要索引
//             是否存储：需要存储
//             -- StringField
//             */
//            doc.add(new StringField("id", book.getId() + "", Field.Store.YES));
//            /**
//             * 图书名称
//             是否分词：需要分词
//             是否索引：需要索引
//             是否存储：需要存储
//             -- TextField
//             */
//            doc.add(new TextField("bookName", book.getBookName(), Field.Store.YES));
//            /**
//             * 图书价格
//             是否分词：（数值型的Field lucene使用内部的分词）
//             是否索引：需要索引
//             是否存储：需要存储
//             -- DoubleField
//             */
//            doc.add(new DoubleField("bookPrice", book.getPrice(), Field.Store.YES));
//            // 图书图片
//            doc.add(new StoredField("bookPic", book.getPic()));
//            /**
//             * 图书描述
//             是否分词：需要分词
//             是否索引：需要索引
//             是否存储：不需要存储
//             -- TextField
//             */
//            doc.add(new TextField("bookDesc", book.getBookDesc(), Field.Store.NO));
//
//            documents.add(doc);
//
//            Analyzer analyzer = new IKAnalyzer();
//
//            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
//            indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//
//            Directory directory = FSDirectory.open(new File("G:\\lucene-index"));
//
//            IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
//
//            for (Document document : documents) {
//                indexWriter.addDocument(document);
//                indexWriter.commit();
//            }
//            indexWriter.close();
//        }
//
//    }


    @Test
    public void testSearchIndex() throws Exception {
        // 创建分析器对象，用于分词
        Analyzer analyzer = new IKAnalyzer();
        // 创建查询解析器对象
        QueryParser queryParser = new QueryParser("bookName", analyzer);
        // 解释查询字符串，得到查询对象
        Query query = queryParser.parse("java");
        // 创建索引库存储目录
        Directory directory = FSDirectory.open(new File("G:\\lucene-index"));
        // 创建IndexReader读取索引库对象
        DirectoryReader reader = DirectoryReader.open(directory);
        // 创建IndexSearcher，执行搜索索引库
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        // 处理结果集
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("总命中记录数: "+topDocs.totalHits);
        // 获取搜索到得文档数组
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        // ScoreDoc对象：只有文档id和分值信息
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("===============================================================================");
            System.out.println("文档ID: "+scoreDoc.doc+", 文档分值: "+scoreDoc.score);
            // 根据文档id获取指定的文档
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("bookName"));
            System.out.println(doc.get("bookPrice"));
            System.out.println(doc.get("bookPic"));
            System.out.println(doc.get("bookDesc"));

        }
        // 释放资源
        reader.close();
    }

//    @Test
//    public void testSearchIndex() throws Exception {
//
//        Analyzer analyzer = new IKAnalyzer();
//
//        QueryParser queryParser = new QueryParser("bookName", analyzer);
//        Query query = queryParser.parse("java");
//        System.out.println(query);
//
//        Directory directory = FSDirectory.open(new File("G:\\lucene-index"));
//        IndexReader indexReader = DirectoryReader.open(directory);
//
//        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        TopDocs topDocs = indexSearcher.search(query, 10);
//        System.out.println("总命中的记录数: " + topDocs.totalHits);
//
//        // 获取搜索到得文档数组
//        ScoreDoc[] docs = topDocs.scoreDocs;
//        for (ScoreDoc doc : docs) {
//            System.out.println("--------华丽分割线----------");
//            System.out.println("文档ID:" + doc.doc + "===" + "文档分值:" + doc.score);
//            Document document = indexReader.document(doc.doc);
//            System.out.println("图书ID: " + document.get("id"));
//            System.out.println("图书名称：" + document.get("bookName"));
//            System.out.println("图书价格：" + document.get("bookPrice"));
//            System.out.println("图书图片：" + document.get("bookPic"));
//            System.out.println("图书描述：" + document.get("bookDesc"));
//        }
//        indexReader.close();
//    }
}
