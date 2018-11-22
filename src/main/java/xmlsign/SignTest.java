package xmlsign;

import org.w3c.dom.Document;

import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import static xmlsign.Utils.parseXml;
import static xmlsign.Utils.pretty;
import static xmlsign.Utils.sha256;

public class SignTest {

    private static final String PRIVATE_KEY = "/key.pkcs8";
    private static final String CERTIFICATE = "/cert.pem";

    private static final String XML_TO_SIGN = "<root><message>Hello</message></root>";


    public static void main(String ... args) throws Exception {

        PrivateKey signKey;
        try (var keyIn = SignTest.class.getResourceAsStream(PRIVATE_KEY)) {
            signKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyIn.readAllBytes()));
        }

        Certificate certificate;
        try (var certIn = SignTest.class.getResourceAsStream(CERTIFICATE)) {
            certificate = CertificateFactory.getInstance("X.509").generateCertificate(certIn);
        }

        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        var sha256DigestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null);
        var canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        var signatureMethod = xmlSignatureFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null);
        var keyInfo = keyInfo(xmlSignatureFactory, certificate);

        Document documentToSign = parseXml(XML_TO_SIGN);
        Reference reference = xmlSignatureFactory.newReference("filename", sha256DigestMethod, null, null, "ID_0", sha256(XML_TO_SIGN));
        SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, List.of(reference));

        XMLObject xmlObject = xmlSignatureFactory.newXMLObject(List.of(new DOMStructure(documentToSign.getDocumentElement())), null, null, null);
        XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo, List.of(xmlObject), "Signature", null);


        System.out.print("*** Document before signing:\n" + pretty(documentToSign));

        // ka-boom on JDK 11!
        xmlSignature.sign(new DOMSignContext(signKey, documentToSign));

        System.out.println("\n*** Document after signing:\n" + pretty(documentToSign));
    }


    private static KeyInfo keyInfo(XMLSignatureFactory xmlSignatureFactory, Certificate ... sertifikater) {
        KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(List.of(sertifikater));
        return keyInfoFactory.newKeyInfo(List.of(x509Data));
    }

}
