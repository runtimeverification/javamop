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

public class PomFile {

    private String pom;
    private String fullPath;
    private String artifactId;
    private boolean isEkstazi;

    public static final String MOP_AGENT_STRING="-javaagent:${settings.localRepository}" +
        "/javamop-agent/javamop-agent/1.0/javamop-agent-1.0.jar";
    public static final String PREDICT_AGENT_STRING="-javaagent:/home/owolabi/RV-Predict/lib/rv-predict.jar -Xbootclasspath/a:/home/owolabi/RV-Predict/lib/rv-predict.jar -XX:hashCode=1";
    public static final String EKSTAZI_AGENT_STRING="-javaagent:${settings.localRepository}" +
            "/org/ekstazi/org.ekstazi.core/4.1.0/org.ekstazi.core-4.1.0.jar=mode=junit";
    public static String AGENT_STRING;
    public PomFile(String pom, String tool) {
        if (tool.equals("predict")){
            AGENT_STRING = PREDICT_AGENT_STRING;
        } else if (tool.equals("ekstazi")){
            AGENT_STRING = EKSTAZI_AGENT_STRING;
            isEkstazi = true;
        }  else {
            AGENT_STRING = MOP_AGENT_STRING;
        }

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
    // about dependency srcs and dependency outputs
    public void rewrite() {
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
            Node surefirePlugin;
            Node plugin;
            Node artifactID;
            Node config;
            Node argLine;
            // Node useSystemClassLoader;
            Node excludesFile;

            if (plugins == null) {
                plugins = doc.createElement("plugins");

                surefirePlugin = makeSureFirePlugin(doc);

                plugins.appendChild(surefirePlugin);

                build.appendChild(plugins);
                // Add the surefire plugin
            } else {
                //look for the surefire plugin to see if it exists
                boolean surefireFound = false;
                NodeList pluginsChildren = plugins.getChildNodes();
                for(int j=0; j < pluginsChildren.getLength(); j++){
                    plugin = pluginsChildren.item(j);
                    artifactID = getDirectChild(plugin,"artifactId");
                    if ((artifactID != null) && artifactID.getTextContent().equals("maven-surefire-plugin")){
                        surefireFound = true;

                        // if surefire plugin exists, it either has configuration element or it doesn't
                        config = getDirectChild(plugin, "configuration");
                        if ((config != null)){
                            argLine = getDirectChild(config, "argLine");
                            if (argLine == null){
                                createArgLineElement(doc, config);
                            } else{
                                String currentText = argLine.getTextContent();
                                String newText = currentText + " " + AGENT_STRING;
                                argLine.setTextContent(newText);
                            }

                            if (isEkstazi){
                                excludesFile = getDirectChild(config,"excludesFile");
                                if (excludesFile == null){
                                    createExcludesFileElement(doc, plugin);
                                }
                            }

                            // useSystemClassLoader = getDirectChild(config,"useSystemClassLoader");
                            // if (useSystemClassLoader == null){
                            //     createClassLoaderElement(doc, config);
                            // } else {
                            //     useSystemClassLoader.setTextContent("true");
                            // }
                        } else {
                            config = doc.createElement("configuration");
                            extendBasicPlugin(doc, config);
                            plugin.appendChild(config);
                        }
                    }
                }

                if (!surefireFound){
                    surefirePlugin = makeSureFirePlugin(doc);
                    plugins.appendChild(surefirePlugin);
                }
            }

            if (isEkstazi){
                Node ekstaziPlugin = makeEkstaziPlugin(doc);
                plugins.appendChild(ekstaziPlugin);
            }

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

    private void createExcludesFileElement(Document doc, Node plugin) {
        Node excludesFile;
        excludesFile = doc.createElement("excludesFile");
        excludesFile.setTextContent("myExcludes");
        plugin.appendChild(excludesFile);
    }

    private Node makeEkstaziPlugin(Document doc) {
        Node ekstaziPlugin;
        Node groupID;
        Node artifactID;
        Node version;
        Node executions;
        Node execution;
        Node goals;
        Node goal1;
        Node goal2;
        Node id;

        ekstaziPlugin = doc.createElement("plugin");

        groupID = doc.createElement("groupId");
        groupID.setTextContent("org.ekstazi");
        ekstaziPlugin.appendChild(groupID);

        artifactID = doc.createElement("artifactId");
        artifactID.setTextContent("ekstazi-maven-plugin");
        ekstaziPlugin.appendChild(artifactID);

        version = doc.createElement("version");
        version.setTextContent("4.1.0");
        ekstaziPlugin.appendChild(version);

        executions = doc.createElement("executions");

        execution = doc.createElement("execution");

        id = doc.createElement("id");
        id.setTextContent("doit");
        execution.appendChild(id);

        goals = doc.createElement("goals");

        goal1 = doc.createElement("goal");
        goal1.setTextContent("select");
        goals.appendChild(goal1);

        goal2 = doc.createElement("goal");
        goal2.setTextContent("restore");
        goals.appendChild(goal2);

        execution.appendChild(goals);
        executions.appendChild(execution);
        ekstaziPlugin.appendChild(executions);
        return ekstaziPlugin;
    }

    private Node makeSureFirePlugin(Document doc) {
        Node surefirePlugin;
        Node config;

        surefirePlugin = doc.createElement("plugin");
        createBasicPlugin(doc, surefirePlugin);
        config = doc.createElement("configuration");
        extendBasicPlugin(doc, config);
        surefirePlugin.appendChild(config);
        return surefirePlugin;
    }

    private void extendBasicPlugin(Document doc, Node config) {
        createArgLineElement(doc, config);
        if (isEkstazi){
            createExcludesFileElement(doc, config);
        }
        // createClassLoaderElement(doc, config);
    }

    // private void createClassLoaderElement(Document doc, Node config) {
    //     Node useSystemClassLoader;
    //     useSystemClassLoader = doc.createElement("useSystemClassLoader");
    //     useSystemClassLoader.setTextContent("true");
    //     config.appendChild(useSystemClassLoader);
    // }

    private void createArgLineElement(Document doc, Node config) {
        Node argLine;
        argLine = doc.createElement("argLine");
        argLine.setTextContent(AGENT_STRING);
        config.appendChild(argLine);
    }

    private void createBasicPlugin(Document doc, Node surefirePlugin) {
        Node groupID;
        Node artifactID;
        Node version;
        Node excludesFile;

        groupID = doc.createElement("groupId");
        groupID.setTextContent("org.apache.maven.plugins");
        surefirePlugin.appendChild(groupID);

        artifactID = doc.createElement("artifactId");
        artifactID.setTextContent("maven-surefire-plugin");
        surefirePlugin.appendChild(artifactID);

        version = doc.createElement("version");
        version.setTextContent("2.16");
        surefirePlugin.appendChild(version);
    }

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
        Map<String,PomFile> mapping = new HashMap<String,PomFile>();
        String input;
        try {
            // First create objects out of all the pom.xml files passed in
            while ((input = bufReader.readLine()) != null) {
                PomFile p = null;
                if ( args.length > 0 && args[0] != null){
                    p = new PomFile(input, args[0]);
                } else {
                    p = new PomFile(input, "");
                }
                mapping.put(p.getArtifactId(), p);
            }

            // Go through all the objects and have them rewrite themselves using information from
            // dependencies
            for (Map.Entry<String,PomFile> entry : mapping.entrySet()) {
                PomFile p = entry.getValue();

                // Have the object rewrite itself (the pom) with mop stuff
                p.rewrite();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

