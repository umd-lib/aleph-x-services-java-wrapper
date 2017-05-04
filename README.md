# aleph-x-services-java-wrapper

## Introduction

A Java client wrapper for Aleph X-Services API

This project is intended to provide a _thin_ Java wrapper around the Aleph X-Services API.

### Useful Resources

* Introduction to Aleph X-Services -  [https://developers.exlibrisgroup.com/aleph/apis/Aleph-X-Services/introduction-to-aleph-x-services](https://developers.exlibrisgroup.com/aleph/apis/Aleph-X-Services/introduction-to-aleph-x-services)

### Requirements

* Java 1.7

### Dependencies

The code uses the following third-party jars:

* jersey-client: Handles the actual network communication with the Aleph X-server
* slf4j-api: For logging. See [https://www.slf4j.org/manual.html] for information about integrating with the logging tool of your choosing.

## Basic Usage

Broadly speaking, this wrapper does two things:

1) Make an Aleph X-Services API call to Aleph
2) Convert the XML result from the call into a Java-friendly object.

### Aleph X-Services API call

The Aleph X-Server responds to HTTP GET requests. For example, to perform a "read-item" operation for an item with barcode "3205566018297", the following "curl" command can be used:

```
> curl 'http://aleph.lib.example.edu/X?op=read-item&library=mai50&item_barcode=32055660182979&translate=N'
```

The JaxbAlephOp interface (with its implementation JaxbAlephOpImpl) is simply a way to make this request from Java. It consists of a single method:

```
  public T request(WebTarget webTarget, Map<String, String> queryParams, Unmarshaller unmarshaller)
      throws JAXBException;
```

The "webTarget" parameter is a javax.ws.rs.client.WebTarget object responsible for indicating the URL to contact, and "queryParams" is simply a Map of the HTTP GET query parameters to supply in addition to the URL. The "unmarshaller" parameter is a "javax.xml.bind.Unmarshaller" instance.

The following code performs the equivalent of the "curl" command above:

```
    Client client = ClientBuilder.newClient(new ClientConfig());
    WebTarget webTarget = client.target("http://aleph.lib.example.edu/X");
    
    try {
      // Create context
      JAXBContext ctx = JAXBContext.newInstance("edu.umd.lib.axsjw.jaxb");

      // Create unmarshaller
      Unmarshaller unmarshaller = ctx.createUnmarshaller();

      // Populate query params for operation
      Map<String, String> params = new HashMap<>();
      params.put("op", "read-item");
      params.put("library", "mai50");
      params.put("item_barcode", "32055660182979");
      params.put("translate", "N");

      // Perform "read-item" operation
      JaxbAlephOp<ReadItem> opReadItem = new JaxbAlephOpImpl<>();
      ReadItem readItem = opReadItem.request(webTarget, params, unmarshaller);

    } catch (JAXBException e) {
    }
```

### XML Conversion

The Aleph X-Server always returns an XML response. JAXB is used to convert the XML into a more Java-friendly object. JAXB-annotated classes are in the "edu.umd.lib.axsjw.jaxb" package.

The JAXB wrapper classes, such as "CircStatus" and "ReadItem" are extremely simple by design. They do not attempt to do any interpretation of fields values.

For example, a "circ-status" operation returns XML containing a series of "item-data" nodes:

```
<?xml version = "1.0" encoding = "UTF-8"?>
<circ-status>
  <item-data>
    <z30-description></z30-description>
    <loan-status>General coll.</loan-status>
    <due-date>On Shelf</due-date>
    <due-hour></due-hour>
    <sub-library>CPART</sub-library>
    <collection>Folio</collection>
    <location>ND623.M6 C58 1990</location>
    <pages></pages>
    <no-requests></no-requests>
    <location-2></location-2>
    <barcode>31430030992236</barcode>
    <opac-note></opac-note>
  </item-data>
  <item-data>
    <z30-description></z30-description>
    <loan-status>General coll.</loan-status>
    <due-date>On Shelf</due-date>
    <due-hour></due-hour>
    <sub-library>SM-SM</sub-library>
    <collection>Oversize</collection>
    <location>ND623.M6 C58 1990</location>
    <pages></pages>
    <no-requests></no-requests>
    <location-2></location-2>
    <barcode>33127001186305</barcode>
    <opac-note></opac-note>
  </item-data>
  <session-id>V5Y84RLQ6QSSSR2GABI6M7FTMSJ24ESMIDT24XYF2UKMP8QYUI</session-id>
</circ-status>
```

The "CircStatus" class has the following method:

```
public Map<String, String>[] getItemDataMaps()
```

which returns an array, each entry corresponding to an "item-data" node. The entry itself is a Map of key/value pairs corresponding to the child nodes. For example, to retrieve the "due-date" field from the first "item-data" element, the following code can be used:

```
circStatus.getItemDataMaps()[0].get("item-data")
```

The XML returned by Aleph always contains a "sessionId" tag, and may contain an "error" tag, if an error occurs. Instead of handling these individually in each JAXB-annotated class, the classes typically extend the "AbstractAlephOpResult" which provides "getSessionId", "getError", and "isError" methods.

### Example Code

See the code in the "src/main/resources/edu/umd/lib/axsjw/examples" for complete examples.

## Development Note

This wrapper library does not currently handle all Aleph X-Services operations.

When adding additional JAXB-annotated classes for parsing XML results to the "edu.umd.lib.axsjw.jaxb" package, the src/main/resources/edu/umd/lib/axsjw/jaxb/jaxb.index file should be updated with the class names, so that the JAXBContext class can auto-discover them.

## Packages

### edu.umd.lib.axsjw.jaxb

Contains classes used by JAXB to unmarshal XML received from the Aleph X server into Java objects.

**Note:** When adding JAXB-annotated classes to this package, be sure to update the src/main/resources/edu/umd/lib/axsjw/jaxb/jaxb.index file, so that the JAXBContext class can auto-discover them.

### edu.umd.lib.axsjw.net

Contains interfaces and classes handling network communications with the Aleph X Server. Uses the Jersey client for configuring and making network requests.

### edu.umd.lib.axsjw.examples

Provides simple examples of Aleph X-server interaction using this library.

### edu.umd.lib.axsjw.facade

Contains convenience classes that use JAXB and network classes to perform
useful work. For example, the TitleAvailability class performs multiple "read-item" and "circ-status" operations to determinge the availability of a particular title.

## Test File Generation

The XML files used for testing were generated by running the following "curl" commands against an Aleph X-Server (where [SERVER_NAME] is the name of the server):

* src/test/resources/test-data/circ-status-error-item-not-found.xml
```
curl 'http://[SERVER_NAME]/X?op=circ-status&sys_no=999999999&library=mai01&translate=N'
```

* src/test/resources/test-data/circ-status-valid-multiple-items.xml
```
curl 'http://[SERVER_NAME]/X?op=circ-status&sys_no=001834881&library=mai01&translate=N'
```

* src/test/resources/test-data/circ-status-valid-single-item.xml
```
curl 'http://[SERVER_NAME]/X?op=circ-status&sys_no=001834879&library=mai01&translate=N'
```

* src/test/resources/test-data/read-item-error-item-not-found-barcode.xml
```
curl 'http://[SERVER_NAME]/X?op=read-item&library=mai50&item_barcode=BARCODE_DOES_NOT_EXIST&translate=N'
```

* src/test/resources/test-data/read-item-valid.xml
```
curl 'http://[SERVER_NAME]/X?op=read-item&library=mai50&item_barcode=32055660182979&translate=N'
```

## License

This code is released under Apache License v2.0. See [LICENSE] for more information.

[https://www.slf4j.org/manual.html]: https://www.slf4j.org/manual.html
[LICENSE]: LICENSE