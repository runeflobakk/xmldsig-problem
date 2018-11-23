package xmlsign;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Utils {

    /**
     * The factory's methods to create documentbuilders are thread safe.
     */
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    static {
        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
    }

    static Document newEmptyXmlDocument() {
        return newDocument(DocumentBuilder::newDocument);
    }

    static Document parseXml(String xml) {
        return newDocument(builder -> builder.parse(new ByteArrayInputStream(xml.getBytes(UTF_8))));
    }

    static Document newDocument(DocumentCreator documentCreator) {
        try {
            return documentCreator.createDocument(DOCUMENT_BUILDER_FACTORY.newDocumentBuilder());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @FunctionalInterface
    interface DocumentCreator {
        Document createDocument(DocumentBuilder builder) throws Exception;
    }

    static byte[] sha256(String s) {
        return sha256(s.getBytes(UTF_8));
    }

    static byte[] sha256(byte[] bytes) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static String pretty(Node xml) throws TransformerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Transformer tr = TransformerFactory.newDefaultInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        tr.transform(new DOMSource(xml), new StreamResult(out));
        return out.toString(UTF_8);
    }

    private Utils() {}
}
