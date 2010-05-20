package com.opencalais.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.opencalais.client.internal.utils.StringUtils;
import com.opencalais.client.model.ContentType;
import com.opencalais.client.model.Metadata;
import com.opencalais.client.model.MetadataElement;
import com.opencalais.client.model.OutputFormat;
import com.opencalais.client.model.Topic;

/**
 * Open Calais Java Client
 *
 * @author Joseph Toth (joetoth@mail.com)
 */
public class OpenCalaisClient {
    String restUrl = "http://api.opencalais.com/enlighten/rest/";

    String licenseID;

    // Processing Directives
    ContentType contentType = ContentType.TEXT_RAW;

    OutputFormat outputFormat = OutputFormat.SIMPLE;

    boolean calculateRelevanceScore = true;

    String reltagBaseURL;

    String enableMetadataType;

    String discardMetadata;

    // User Directives
    boolean allowDistribution = false;

    boolean allowSearch = false;

    String externalID;

    String submitter;

    public static void main(String[] args) {
        OpenCalaisClient client = new OpenCalaisClient(
                "86azn8ab5pvjfp25u9g742zd");
        client.setOutputFormat(OutputFormat.RDF);
        try {
            String a = client
                    .query("asdasfa asdfasf");

            Metadata metadata = OpenCalaisClient.parseSimpleOutputFormat(a);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public OpenCalaisClient() {

    }

    public OpenCalaisClient(String licenseID) {
        this.licenseID = licenseID;
    }

    public String query(String content) throws IOException {
        StringBuilder sb = new StringBuilder(content.length() + 1024);
        sb.append("licenseID=").append(licenseID);
        sb.append("&content=").append(content);
        sb.append("&paramsXML=").append(buildAdditionalParameters());
        URLConnection connection = new URL(restUrl).openConnection();
        connection.addRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        connection.addRequestProperty("Content-Length", sb.length() + "");
        connection.setDoOutput(true);
        OutputStream out = connection.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write(sb.toString());
        writer.flush();

        String result = StringUtils.convertStreamToString(connection
                .getInputStream());

        return result;
    }

    private String buildAdditionalParameters() {
        StringBuilder sb = new StringBuilder(
                "<c:params xmlns:c=\"http://s.opencalais.com/1/pred/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                        + "\n");

        // Processing Directives
        sb.append("<c:processingDirectives ");

        sb.append(" c:contentType=\"" + contentType + "\" ");
        sb.append(" c:outputFormat=\"" + outputFormat + "\" ");
        sb.append(" c:calculateRelevanceScore=\"" + calculateRelevanceScore
                + "\" ");
        sb.append(" c:allowDistribution=\"" + allowDistribution + "\" ");
        sb.append(" c:allowSearch=\"" + allowSearch + "\" ");

        if (enableMetadataType != null) {
            sb.append(" c:enableMetadataType=\"" + enableMetadataType + "\" ");
        }

        if (reltagBaseURL != null) {
            sb.append(" c:reltagBaseURL=\"" + reltagBaseURL + "\" ");
        }

        if (discardMetadata != null) {
            sb.append(" c:discardMetadata=\"" + discardMetadata + "\" ");
        }

        sb.append(">\n");

        sb.append("</c:processingDirectives>\n");

        // User Directives

        sb.append("<c:userDirectives ");
        sb.append(" c:allowDistribution=\"" + allowDistribution + "\" ");
        sb.append(" c:allowSearch=\"" + allowSearch + "\" ");

        if (externalID != null) {
            sb.append(" c:externalID=\"" + externalID + "\" ");
        }

        if (submitter != null) {
            sb.append(" c:submitter=\"" + submitter + "\" ");
        }

        sb.append("></c:userDirectives>\n" + "\n");

        sb.append("<c:externalMetadata>\n" + "\n" + "</c:externalMetadata>\n"
                + "\n" + "</c:params>");

        return sb.toString();
    }

    public static Metadata parseSimpleOutputFormat(String simpleOutput)
            throws IOException {

        Metadata metadata = new Metadata();

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(
                    simpleOutput.getBytes());

            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(inputStream);

            NodeList nodes = doc
                    .getElementsByTagName("CalaisSimpleOutputFormat");

            if (nodes.getLength() == 0) {
                throw new IOException(
                        "Error Occurred simpleOutput is possibly too short");
            } else if (nodes.getLength() > 1) {
                throw new IOException(
                        "More than one <CalaisSimpleOutputFormat> tag was found");
            }

            nodes = nodes.item(0).getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = (Node) nodes.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element element = (Element) node;

                String name = element.getNodeName().toLowerCase();

                if (name.equalsIgnoreCase("topics")) {

                    NodeList topicNodes = element.getChildNodes();

                    for (int j = 0; j < topicNodes.getLength(); j++) {

                        Node topicNode = (Node) topicNodes.item(j);

                        if (topicNode.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }

                        Element topicElement = (Element) topicNode;

                        String topicName = topicElement.getTextContent()
                                .toLowerCase();
                        String taxonomy = topicElement.getAttribute("Taxonomy")
                                .toLowerCase();
                        Double score = new Double(topicElement
                                .getAttribute("Score"));

                        Topic topic = new Topic();
                        topic.setName(topicName);
                        topic.setScore(score);
                        topic.setTaxonomy(taxonomy);

                        metadata.setTopic(topicName, topic);
                    }

                } else {
                    String value = element.getTextContent().toLowerCase();
                    String relevanceStr = element.getAttribute("relevance");

                    Double relevance = null;
                    if (relevanceStr != null && !relevanceStr.trim().equals("")) {
                        relevance = new Double(relevanceStr);
                    }
                    Integer count = new Integer(element.getAttribute("count"));

                    MetadataElement metadataElement = new MetadataElement();
                    metadataElement.setName(name);
                    metadataElement.setValue(value);
                    metadataElement.setCount(count);
                    metadataElement.setRelevance(relevance);

                    metadata.setMetadataElement(name, metadataElement);
                }

            }

        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }

        return metadata;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public void setRestUrl(String restUrl) {
        this.restUrl = restUrl;
    }

    public String getLicenseID() {
        return licenseID;
    }

    public void setLicenseID(String licenseID) {
        this.licenseID = licenseID;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isCalculateRelevanceScore() {
        return calculateRelevanceScore;
    }

    public void setCalculateRelevanceScore(boolean calculateRelevanceScore) {
        this.calculateRelevanceScore = calculateRelevanceScore;
    }

    public String getReltagBaseURL() {
        return reltagBaseURL;
    }

    public void setReltagBaseURL(String reltagBaseURL) {
        this.reltagBaseURL = reltagBaseURL;
    }

}
