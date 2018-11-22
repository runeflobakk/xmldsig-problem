package xmlsign;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

final class Utils {

    static Document parseXml(String xml) {
        try {
            return DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(UTF_8)));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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

    private Utils() {}

    static String pretty(Node xml) throws TransformerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Transformer tr = TransformerFactory.newDefaultInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        tr.transform(new DOMSource(xml), new StreamResult(out));
        return out.toString(UTF_8);
    }
}
