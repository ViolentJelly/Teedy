package com.sismics.docs.core.dao;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.util.context.ThreadLocalContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * DocumentDao integration tests.
 */
public class DocumentDaoTest extends BaseTransactionalTest {
    private final DocumentDao documentDao = new DocumentDao();

    @Test
    // 验证创建文档后，可以按 ID 读取、按用户查询，并且文档总数会增加。
    public void testCreateAndFindByUserId() throws Exception {
        User user = createUser("document_create");
        long initialCount = documentDao.getDocumentCount();

        Document document = createDocument(user, "Create title", "Create description");
        ThreadLocalContext.get().getEntityManager().flush();

        Assert.assertNotNull(document.getId());
        Assert.assertEquals(initialCount + 1, documentDao.getDocumentCount());

        Document storedDocument = documentDao.getById(document.getId());
        Assert.assertNotNull(storedDocument);
        Assert.assertEquals("Create title", storedDocument.getTitle());
        Assert.assertEquals("Create description", storedDocument.getDescription());
        Assert.assertEquals(user.getId(), storedDocument.getUserId());

        List<Document> userDocuments = documentDao.findByUserId(user.getId());
        Assert.assertEquals(1, userDocuments.size());
        Assert.assertEquals(document.getId(), userDocuments.get(0).getId());
    }

    @Test
    // 验证更新文档后，标题和各个元数据字段都会被正确写回数据库。
    public void testUpdateDocument() throws Exception {
        User user = createUser("document_update");
        Document document = createDocument(user, "Original title", "Original description");
        ThreadLocalContext.get().getEntityManager().flush();

        Document update = new Document();
        update.setId(document.getId());
        update.setTitle("Updated title");
        update.setDescription("Updated description");
        update.setSubject("Updated subject");
        update.setIdentifier("Updated identifier");
        update.setPublisher("Updated publisher");
        update.setFormat("Updated format");
        update.setSource("Updated source");
        update.setType("Updated type");
        update.setCoverage("Updated coverage");
        update.setRights("Updated rights");
        update.setCreateDate(document.getCreateDate());
        update.setLanguage(document.getLanguage());
        update.setFileId(document.getFileId());

        documentDao.update(update, user.getId());
        ThreadLocalContext.get().getEntityManager().flush();

        Document storedDocument = documentDao.getById(document.getId());
        Assert.assertNotNull(storedDocument);
        Assert.assertEquals("Updated title", storedDocument.getTitle());
        Assert.assertEquals("Updated description", storedDocument.getDescription());
        Assert.assertEquals("Updated subject", storedDocument.getSubject());
        Assert.assertEquals("Updated identifier", storedDocument.getIdentifier());
        Assert.assertEquals("Updated publisher", storedDocument.getPublisher());
        Assert.assertEquals("Updated format", storedDocument.getFormat());
        Assert.assertEquals("Updated source", storedDocument.getSource());
        Assert.assertEquals("Updated type", storedDocument.getType());
        Assert.assertEquals("Updated coverage", storedDocument.getCoverage());
        Assert.assertEquals("Updated rights", storedDocument.getRights());
    }

    @Test
    // 验证删除文档后，按 ID 查询会返回空，并且文档总数会恢复。
    public void testDeleteDocumentAndGetByIdReturnsNull() throws Exception {
        User user = createUser("document_delete");
        long initialCount = documentDao.getDocumentCount();
        Document document = createDocument(user, "Delete title", "Delete description");
        ThreadLocalContext.get().getEntityManager().flush();

        Assert.assertEquals(initialCount + 1, documentDao.getDocumentCount());

        documentDao.delete(document.getId(), user.getId());
        ThreadLocalContext.get().getEntityManager().flush();

        Assert.assertNull(documentDao.getById(document.getId()));
        Assert.assertEquals(initialCount, documentDao.getDocumentCount());
    }

    private Document createDocument(User user, String title, String description) {
        Document document = new Document();
        document.setUserId(user.getId());
        document.setTitle(title);
        document.setDescription(description);
        document.setLanguage("eng");
        document.setCreateDate(new Date());
        document.setSubject("subject");
        document.setIdentifier("identifier");
        document.setPublisher("publisher");
        document.setFormat("format");
        document.setSource("source");
        document.setType("type");
        document.setCoverage("coverage");
        document.setRights("rights");
        documentDao.create(document, user.getId());
        return document;
    }
}
