import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class PomFileCobertura {

    private String pom;
    private String fullPath;
    private String artifactId;
   

    public PomFileCobertura(String pom) {
   
        this.pom = pom;
        try {
            this.fullPath = new File(pom).getParentFile().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Parse document for projectId
        findProjectId(pom);
    }

    public static Node getDirectChild(Node parent, String name)
    {
        for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
            {
                if(child instanceof Node && name.equals(child.getNodeName())) return child;
            }
        return null;
    }

    private void findProjectId(String pom) {
        File pomFile = new File(pom);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory
            .newInstance();
        dbFactory.setNamespaceAware(false);
        dbFactory.setValidating(false);
        DocumentBuilder dBuilder;
        try {

            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomFile);

            // Find high-level artifact Id
            Node project = doc.getElementsByTagName("project").item(0);
            NodeList projectChildren = project.getChildNodes();
            for (int i = 0; i < projectChildren.getLength(); i++) {
                Node n = projectChildren.item(i);

                if (n.getNodeName().equals("artifactId")) {
                    this.artifactId = n.getTextContent();
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("File does not exit: " + pom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Rewrite contents of own pom.xml, augmented with information
        
    // -----------Wajih rewriting pom for cobertura -----------
 public void rewrite_cobertura() {
        File pomFile = new File(this.pom);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory
            .newInstance();
        dbFactory.setNamespaceAware(false);
        dbFactory.setValidating(false);
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomFile);
	   
            // Check if <build> tag exists; if not have to make one
            Node build;
            if (doc.getElementsByTagName("build").getLength() == 0) {
                build = doc.createElement("build");
                doc.getElementsByTagName("project").item(0).appendChild(build);
            }
            else {
                // Should only be one <build> structure
                build = doc.getElementsByTagName("build").item(0);
            }
            NodeList buildChildren = build.getChildNodes();

	    // Search for <plugins>
            Node plugins = null;
            for (int i = 0; i < buildChildren.getLength(); i++) {
                if (buildChildren.item(i).getNodeName().equals("plugins")) {
                    plugins = buildChildren.item(i);
                    break;
                }
            }

	    // Add new <plugins> if non-existant
            Node coberturaPlugin;
            Node plugin;
            Node artifactID;
            Node config;

            if (plugins == null) {
                plugins = doc.createElement("plugins");
                coberturaPlugin = makeCoberturaPlugin(doc);
                plugins.appendChild(coberturaPlugin);
                build.appendChild(plugins);
                // Add the Cobertura plugin
            } else {
                //look for the cobertura plugin to see if it exists
                boolean coberturaFound = false;
		NodeList pluginsChildren = plugins.getChildNodes();
                for(int j=0; j < pluginsChildren.getLength(); j++){
                    plugin = pluginsChildren.item(j);
                    artifactID = getDirectChild(plugin,"artifactId");
                    if ((artifactID != null) && artifactID.getTextContent().equals("cobertura-maven-plugin")){
                        coberturaFound = true;
                        Node configInner = getDirectChild(plugin,"configuration");
			if (configInner == null){
			    configInner = doc.createElement("configuration");
			    plugin.appendChild(configInner);
			}
			Node dep = getDirectChild(configInner,"dependencyLocationsEnabled");
			if ((dep != null) && dep.getTextContent().equals("false")){

			}else{
			    extendCoberturaConfig(doc,configInner);
			}
			Node skip = getDirectChild(configInner,"skip");
			if(skip != null)
			    skip.setTextContent("false");
                    }
                }
                if (!coberturaFound){
                    coberturaPlugin = makeCoberturaPlugin(doc);
                    plugins.appendChild(coberturaPlugin);
                }
            }

	    // check if <reporting> is there

	    
	    Node reporting;
            if (doc.getElementsByTagName("reporting").getLength() == 0) {
                reporting = doc.createElement("reporting");
                doc.getElementsByTagName("project").item(0).appendChild(reporting);
            }
            else {
                // Should only be one <reporting> structure
                reporting = doc.getElementsByTagName("reporting").item(0);
            }
            NodeList reportingChildren = reporting.getChildNodes();
	    // Search for <plugins>
            Node repPlugins = null;
            for (int i = 0; i < reportingChildren.getLength(); i++) {
                if (reportingChildren.item(i).getNodeName().equals("plugins")) {
                    repPlugins = reportingChildren.item(i);
                    break;
                }
            }

	    // Add new <plugins> if non-existant
            Node coberturaRepPlugin;
            Node repPlugin;
            Node repArtifactID;
            Node repConfig;

            if (repPlugins == null) {
                repPlugins = doc.createElement("plugins");
                coberturaRepPlugin = makeCoberturaRepPlugin(doc);
                repPlugins.appendChild(coberturaRepPlugin);
                reporting.appendChild(repPlugins);
                // Add the Cobertura  plugin in reporting
            } else {
                //look for the cobertura plugin to see if it exists
                boolean coberturaFound = false;
		NodeList pluginsChildren = repPlugins.getChildNodes();
                for(int j=0; j < pluginsChildren.getLength(); j++){
                    plugin = pluginsChildren.item(j);
                    repArtifactID = getDirectChild(plugin,"artifactId");
                    if ((repArtifactID != null) && repArtifactID.getTextContent().equals("cobertura-maven-plugin")){
                        coberturaFound = true;
			Node configInner = getDirectChild(plugin,"configuration");
			if (configInner == null){
			    configInner = doc.createElement("configuration");
			    plugin.appendChild(configInner);
			}
			Node formats =  getDirectChild(configInner,"formats");
			if (formats != null){
			    configInner.removeChild(formats);
			    
			}
			extendCoberturaRepConfig(doc,configInner);
			
                        // if cobertura plugin exists in reporting, then we don't have to do anything
                    }
                }
                if (!coberturaFound){
                    coberturaRepPlugin = makeCoberturaRepPlugin(doc);
                    repPlugins.appendChild(coberturaRepPlugin);
                }
            }
	    
	    // END of rewriting stuff in POM . now just housekeeping for file writing
            doc.normalizeDocument();
            // Construct string representation of the file
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();

            // Rewrite the pom file with this string
            PrintWriter filewriter = new PrintWriter(this.pom);
            filewriter.println(output);
            filewriter.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("File does not exit: " + this.pom);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    // ---------- Cobertura
    

    // ------- Wajih

    private Node makeCoberturaPlugin(Document doc){
	Node coberturaPlugin;
	Node config;

	coberturaPlugin = doc.createElement("plugin");
	createBasicCoberturaPlugin(doc,coberturaPlugin);
	config = doc.createElement("configuration");
	extendCoberturaConfig(doc,config);
	coberturaPlugin.appendChild(config);
	return coberturaPlugin;
    }
    private Node makeCoberturaRepPlugin(Document doc){
	Node coberturaRepPlugin;
	Node config;

	coberturaRepPlugin = doc.createElement("plugin");
	createRepCoberturaPlugin(doc,coberturaRepPlugin);
	config = doc.createElement("configuration");
	extendCoberturaRepConfig(doc,config);
	coberturaRepPlugin.appendChild(config);
	return coberturaRepPlugin;
    }
    
    
    private void extendCoberturaConfig(Document doc, Node config){
	Node dependencyNode;
	dependencyNode = doc.createElement("dependencyLocationsEnabled");
	dependencyNode.setTextContent("false");
	config.appendChild(dependencyNode);
    }
    private void extendCoberturaRepConfig(Document doc, Node config){
	Node formats;
	Node formatHtml;
	Node formatXml;
	
	formats = doc.createElement("formats");

	formatHtml = doc.createElement("format");
	formatHtml.setTextContent("html");
	formats.appendChild(formatHtml);

	formatXml = doc.createElement("format");
	formatXml.setTextContent("xml");
	formats.appendChild(formatXml);
	
	config.appendChild(formats);
    }
   
    private void createBasicCoberturaPlugin(Document doc, Node coberturaPlugin){
	Node groupId;
	Node artifactId;
	Node version;

	
        groupId = doc.createElement("groupId");
        groupId.setTextContent("org.codehaus.mojo");
        coberturaPlugin.appendChild(groupId);

        artifactId = doc.createElement("artifactId");
        artifactId.setTextContent("cobertura-maven-plugin");
        coberturaPlugin.appendChild(artifactId);

        version = doc.createElement("version");
        version.setTextContent("2.7");
        coberturaPlugin.appendChild(version);
    }
    private void createRepCoberturaPlugin(Document doc, Node coberturaRepPlugin){
	Node groupId;
	Node artifactId;
	Node version;
        groupId = doc.createElement("groupId");
        groupId.setTextContent("org.codehaus.mojo");
        coberturaRepPlugin.appendChild(groupId);

        artifactId = doc.createElement("artifactId");
        artifactId.setTextContent("cobertura-maven-plugin");
        coberturaRepPlugin.appendChild(artifactId);

        version = doc.createElement("version");
        version.setTextContent("2.7");
        coberturaRepPlugin.appendChild(version);
    }
    
    // -----------------

    // Accessors
    public String getFullPath() {
        return this.fullPath;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public static void main(String[] args) {
        InputStreamReader isReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isReader);
        Map<String,PomFileCobertura> mapping = new HashMap<String,PomFileCobertura>();
        String input;
        try {
            // First create objects out of all the pom.xml files passed in
            while ((input = bufReader.readLine()) != null) {
                PomFileCobertura p = null;
                if ( args.length > 0 && args[0] != null){
                    p = new PomFileCobertura(input);
                } else {
                    p = new PomFileCobertura(input);
                }
                mapping.put(p.getArtifactId(), p);
            }

            // Go through all the objects and have them rewrite themselves using information from
            // dependencies
            for (Map.Entry<String,PomFileCobertura> entry : mapping.entrySet()) {
                PomFileCobertura p = entry.getValue();

                // Have the object rewrite itself (the pom) with cobertura 
                p.rewrite_cobertura();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

