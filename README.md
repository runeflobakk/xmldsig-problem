# XMLDSig problem on Java 11

If you using XMLDSig using the standard `java.xml.crypto` Java APIs, and in particular relying on the default implementation provided by the JDK (as is quite common), you may run into the following error when upgrading from Java 8, 9, or 10, to Java 11:

```
org.w3c.dom.DOMException: HIERARCHY_REQUEST_ERR: An attempt was made to insert a node where it is not permitted.
    at java.xml/com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl.insertBefore(CoreDocumentImpl.java:439)
    at java.xml/com.sun.org.apache.xerces.internal.dom.NodeImpl.appendChild(NodeImpl.java:237)
    at java.xml.crypto/org.jcp.xml.dsig.internal.dom.XmlWriterToTree.writeStartElement(XmlWriterToTree.java:104)
    at java.xml.crypto/org.jcp.xml.dsig.internal.dom.DOMXMLSignature.marshal(DOMXMLSignature.java:213)
    at java.xml.crypto/org.jcp.xml.dsig.internal.dom.DOMXMLSignature.sign(DOMXMLSignature.java:325)
    at somewhere.in.your.Code
```

The code in this repository demonstrates a solution to this in the [6012c7241](https://github.com/runeflobakk/xmldsig-problem/commit/6012c72413078fceea56e435dd8dcc845a0c5dba) commit. More details are available (discard the ranting) in the commit message.


## Background

The implementation of XML signature and encryption in the JDK is based on [Apache Santuario](http://santuario.apache.org/), and was [updated from version 1.5.4 to 2.1.1 in Java 11](https://www.oracle.com/technetwork/java/javase/11-relnote-issues-5012449.html#JDK-8177334) ([JDK-8177334](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8177334)).