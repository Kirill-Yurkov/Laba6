package server.managers;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import server.Server;
import server.exceptions.CommandValueException;
import server.exceptions.FileException;
import server.exceptions.StopServerException;
import server.patternclass.Coordinates;
import server.patternclass.Event;
import server.patternclass.Ticket;
import server.patternclass.TicketType;
import server.utilities.Validator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * The FileManager class is responsible for managing file operations in the server.
 * It provides functionality for setting the file path, initializing the file, and reading/writing XML files.
 * The FileManager class is used by the Server class to handle file-related tasks.
 *
 * Usage:
 * FileManager fileManager = new FileManager(server);
 * fileManager.setFilePath(filePath); // Set the file path
 * fileManager.initializeFile(); // Initialize the file
 * fileManager.readXML(); // Read the XML file
 * fileManager.writeXML(filePath, tickets); // Write the XML file
 *
 * Example:
 * FileManager fileManager = new FileManager(server);
 * fileManager.setFilePath(filePath);
 * fileManager.initializeFile();
 * fileManager.readXML();
 * fileManager.writeXML(filePath, tickets);
 * ITErator iterable
 */
public class FileManager {
    @Getter
    private Server server;
    @Getter
    private String filePath;
    private Document document;
    private boolean isFileInitialized =false;
    public FileManager(Server server) {
        this.server = server;
    }
    public void setFilePath(String filePath) throws StopServerException {
        if (new File(filePath).canRead() && new File(filePath).canWrite() && ".xml".equals(ReaderWriter.getFileExtension(filePath))) {
            try {
                document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
                this.filePath = filePath;
                server.getReaderWriter().readXML();
                server.getListManager().readTicketList();
            } catch (FileException e) {
                throw new StopServerException("File fail: " + e.getMessage());
            } catch (SAXException | IOException | ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new StopServerException("Wrong file");
        }
    }
    public boolean initializeFile() {
        if (isFileInitialized) {
            return true;
        } else {
            server.getInputOutput().outPut("Введите путь до файла коллекции (xml) :\n~ ");
            try {
                String text = server.getInputOutput().inPut();
                if (text == null) {
                    server.getInputOutput().outPut("\nПолучен сигнал завершения работы.");
                    server.stop();
                    return false;
                } else if (text.isEmpty()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();

                    Element root = document.createElement("root");

                    document.appendChild(root);

                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.transform(new DOMSource(document), new StreamResult(new File("New.xml")));
                    server.getInputOutput().outPut("New file collection created: New.xml\n");
                    return true;
                } else {
                    setFilePath(text);// src/main/resources/Collection.xml
                    isFileInitialized = true;
                    server.getInputOutput().outPut("\n");
                    return true;
                }

            } catch (StopServerException e) {
                server.getInputOutput().outPut(e.getMessage() + "\n");
                isFileInitialized = false;
                server.getInputOutput().outPut("\n");
                return initializeFile();
            } catch (Exception e){
                isFileInitialized = false;
                server.getInputOutput().outPut("\n");
                return initializeFile();
            }

        }
    }

    public void initializeFile(String text) {
            try {
                if (text == null) {
                    server.getInputOutput().outPut("\nОшибка.");
                    server.stop();
                } else if (text.isEmpty()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();

                    Element root = document.createElement("TicketsBook");

                    document.appendChild(root);

                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.transform(new DOMSource(document), new StreamResult(new File("New.xml")));
                    server.getInputOutput().outPut("New file collection created: New.xml\n");
                } else {
                    setFilePath(text);// src/main/resources/Collection.xml
                    isFileInitialized = true;
                    server.getInputOutput().outPut("Файл успешно прочитан\n");
                }

            } catch (StopServerException e) {
                server.getInputOutput().outPut(e.getMessage() + "\n");
                isFileInitialized = false;
                server.getInputOutput().outPut("\n");
            } catch (Exception e){
                isFileInitialized = false;
                server.getInputOutput().outPut("\n");
            }

    }


    public class InputOutput {
        @Getter
        @Setter
        private BufferedReader reader;
        @Getter
        @Setter
        private BufferedOutputStream writer;
        private String lastOut = "";
        public String inPut() {
            try {
                String str = reader.readLine();
                return str;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void outPut(String text) {
            try {
                if(!lastOut.equals("\n") || !text.equals("\n")){
                    writer.write(text.getBytes());
                    writer.flush();
                    lastOut = text;
                }
            } catch (IOException ignored) {
            }
        }
    }

    @Getter
    public class ReaderWriter {
        @Setter
        private List<String> collectionInfo = new ArrayList<>();

        @Setter
        private List<Ticket> collectionTicket = new ArrayList<>();

        public static String getFileExtension(String mystr) {
            int index = mystr.indexOf('.');
            return index == -1? null : mystr.substring(index);
        }

        public void readXML() throws FileException {
            try {
                setCollectionTicket(readTickets(document));
                setCollectionInfo(readCollectionInfo(document));
            } catch (ParseException | FileException | CommandValueException e) {
                throw new FileException("fail read: " + e.getMessage());
            }
        }

        private List<Ticket> readTickets(Document document) throws FileException, CommandValueException {
            List<Ticket> tickets = new ArrayList<>();
            try {
                Element root = document.getDocumentElement();
                NodeList ticketList = root.getElementsByTagName("Ticket");
                for (int i = 0; i < ticketList.getLength(); i++) {
                    Node ticketNode = ticketList.item(i);
                    if (ticketNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element ticketElement = (Element) ticketNode;

                        String ticketIdString = ticketElement.getElementsByTagName("TicketId").item(0).getTextContent();
                        if (!Validator.isValidLonger(ticketIdString, 0)) {
                            throw new CommandValueException("incorrect TicketId value");
                        }
                        long ticketId = Long.parseLong(ticketIdString);

                        String ticketName = ticketElement.getElementsByTagName("TicketName").item(0).getTextContent();
                        if (!Validator.isValidString(ticketName)) {
                            throw new CommandValueException("incorrect TicketName value");
                        }

                        String coordinateXString = ticketElement.getElementsByTagName("CoordinateX").item(0).getTextContent();
                        if (!Validator.isValidLonger(coordinateXString, -503)) {
                            throw new CommandValueException("incorrect CoordinateX vale");
                        }
                        long coordinateX = Long.parseLong(coordinateXString);

                        String coordinateYString = ticketElement.getElementsByTagName("CoordinateY").item(0).getTextContent();
                        if (!Validator.isValidLonger(coordinateYString, -664)) {
                            throw new CommandValueException("incorrect CoordinateX vale");
                        }
                        long coordinateY = Long.parseLong(coordinateYString);

                        Date date;
                        try {
                            String date1 = ticketElement.getElementsByTagName("TicketCreationDate").item(0).getTextContent();
                            SimpleDateFormat formatter = new SimpleDateFormat("EEE LLL d HH:m:s z yyyy", Locale.ENGLISH);
                            date = formatter.parse(date1);
                        } catch (Exception e) {
                            throw new CommandValueException("incorrect TicketCreationDate value");
                        }

                        String ticketPriceString = ticketElement.getElementsByTagName("TicketPrice").item(0).getTextContent();
                        Integer ticketPrice;
                        if (Validator.isValidIntegerWithNull(ticketPriceString, 0) == null) {
                            ticketPrice = null;
                        } else if (!Boolean.TRUE.equals(Validator.isValidIntegerWithNull(ticketPriceString, 0))) {
                            throw new CommandValueException("incorrect TicketPrice value");
                        } else {
                            ticketPrice = Integer.parseInt(ticketElement.getElementsByTagName("TicketPrice").item(0).getTextContent());
                        }

                        String ticketTypeString = ticketElement.getElementsByTagName("TicketType").item(0).getTextContent();
                        if (!Validator.isValidTicketType(ticketTypeString)) {
                            throw new CommandValueException("incorrect icketType value");
                        }
                        TicketType ticketType = TicketType.valueOf(ticketTypeString);
                        try {
                            String eventIdString = ticketElement.getElementsByTagName("EventId").item(0).getTextContent();
                            if (!Validator.isValidInteger(eventIdString, 0)) {
                                throw new CommandValueException("incorrect EventId value");
                            }
                            Integer eventId = Integer.parseInt(eventIdString);

                            String eventName = ticketElement.getElementsByTagName("EventName").item(0).getTextContent();

                            String eventMinAgeString = ticketElement.getElementsByTagName("EventMinAge").item(0).getTextContent();
                            Long eventMinAge;
                            if (Validator.isValidLongerWithNull(eventMinAgeString) == null) {
                                eventMinAge = null;
                            } else if (!Boolean.TRUE.equals(Validator.isValidLongerWithNull(eventMinAgeString))) {
                                throw new CommandValueException("incorrect EventMinAge value");
                            } else {
                                eventMinAge = Long.parseLong(eventMinAgeString);
                            }


                            String eventTicketsCountString = ticketElement.getElementsByTagName("EventTicketsCount").item(0).getTextContent();
                            if (!Validator.isValidInteger(eventTicketsCountString, 0)) {
                                throw new CommandValueException("incorrect EventTicketsCount value");
                            }
                            int eventTicketsCount = Integer.parseInt(eventTicketsCountString);

                            String eventDescription = ticketElement.getElementsByTagName("EventDescription").item(0).getTextContent();
                            if (Validator.isValidStringWithNull(eventDescription) == null) {
                                eventDescription = null;
                            } else if (!Boolean.TRUE.equals(Validator.isValidStringWithNull(eventDescription))) {
                                throw new CommandValueException("incorrect EventDescription");
                            }
                            Event event = new Event(eventName, eventMinAge, eventTicketsCount, eventDescription);
                            event.setId(eventId);
                            Ticket ticket = new Ticket(
                                    ticketName,
                                    new Coordinates(coordinateX, coordinateY),
                                    date,
                                    ticketPrice,
                                    ticketType,
                                    event
                            );
                            ticket.setId(ticketId);
                            tickets.add(ticket);
                        } catch (NullPointerException ignored) {
                            Ticket ticket = new Ticket(
                                    ticketName,
                                    new Coordinates(coordinateX, coordinateY),
                                    date,
                                    ticketPrice,
                                    ticketType,
                                    null
                            );
                            ticket.setId(ticketId);
                            tickets.add(ticket);
                        }
                    }
                }
            } catch (CommandValueException e) {
                throw new FileException("incorrect ticket: " + e.getMessage());
            } catch (Exception e) {
                throw new FileException("incorrect ticket");
            }
            return tickets;
        }

        public void writeXML(String filePath, List<Ticket> tickets) {
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
                cleanup(document);
                remove(document, "Ticket");
                remove(document, "Collection");
                fillCollectionInfo(document);
                fill(document, tickets);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new BufferedOutputStream(new FileOutputStream(filePath)));
                transformer.transform(source, result);
            } catch (Exception ignored) {
            }
        }

        private void fill(Document document, List<Ticket> tickets) {
            Node root = document.getDocumentElement();

            for (Ticket ticket : tickets) {
                List<Element> elementList = new ArrayList<>();
                Element currTicket = document.createElement("Ticket");

                Element ticketId = document.createElement("TicketId");
                ticketId.setTextContent(String.valueOf(ticket.getId()));
                elementList.add(ticketId);

                Element ticketName = document.createElement("TicketName");
                ticketName.setTextContent(ticket.getName());
                elementList.add(ticketName);

                Element coordinateX = document.createElement("CoordinateX");
                coordinateX.setTextContent(String.valueOf(ticket.getCoordinates().getX()));
                elementList.add(coordinateX);

                Element coordinateY = document.createElement("CoordinateY");
                coordinateY.setTextContent(String.valueOf(ticket.getCoordinates().getY()));
                elementList.add(coordinateY);

                Element ticketCreationDate = document.createElement("TicketCreationDate");
                ticketCreationDate.setTextContent(String.valueOf(ticket.getCreationDate()));
                elementList.add(ticketCreationDate);

                Element ticketPrice = document.createElement("TicketPrice");
                ticketPrice.setTextContent(String.valueOf(ticket.getPrice()));
                elementList.add(ticketPrice);

                Element ticketType = document.createElement("TicketType");
                ticketType.setTextContent(String.valueOf(ticket.getType()));
                elementList.add(ticketType);
                try {
                    Element eventId = document.createElement("EventId");
                    eventId.setTextContent(String.valueOf(ticket.getEvent().getId()));
                    elementList.add(eventId);

                    Element eventName = document.createElement("EventName");
                    eventName.setTextContent(ticket.getEvent().getName());
                    elementList.add(eventName);

                    Element eventMinAge = document.createElement("EventMinAge");
                    eventMinAge.setTextContent(String.valueOf(ticket.getEvent().getMinAge()));
                    elementList.add(eventMinAge);

                    Element eventTicketsCount = document.createElement("EventTicketsCount");
                    eventTicketsCount.setTextContent(String.valueOf(ticket.getEvent().getTicketsCount()));
                    elementList.add(eventTicketsCount);

                    Element eventDescription = document.createElement("EventDescription");
                    eventDescription.setTextContent(ticket.getEvent().getDescription());
                    elementList.add(eventDescription);
                } catch (NullPointerException ignored) {
                }

                for (Element element : elementList) {
                    currTicket.appendChild(element);
                }
                root.appendChild(currTicket);
            }
        }

        private void remove(Document document, String removing) {
            Element root = document.getDocumentElement();
            NodeList elementList = root.getElementsByTagName(removing);
            Node[] elementsToRemove = new Node[elementList.getLength()];
            for (int i = 0; i < elementList.getLength(); i++) {
                Node node = elementList.item(i);
                elementsToRemove[i] = node;
            }
            for (Node node : elementsToRemove) {
                root.removeChild(node);
            }
        }

        private void cleanup(Document document) throws XPathExpressionException {
            XPath xp = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList) xp.evaluate("//text()[normalize-space(.)='']", document, XPathConstants.NODESET);
            for (int i = 0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                node.getParentNode().removeChild(node);
            }
        }

        private List<String> readCollectionInfo(Document document) throws ParseException, FileException {
            Element root = document.getDocumentElement();
            Node ticketNode = root.getElementsByTagName("Collection").item(0);
            try {
                if (ticketNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element collection = (Element) ticketNode;
                    String typeCollection = collection.getElementsByTagName("CollectionType").item(0).getTextContent();
                    String date1 = collection.getElementsByTagName("CollectionCreationDate").item(0).getTextContent();
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE LLL d HH:m:s z yyyy", Locale.ENGLISH);
                    formatter.setLenient(false);
                    Date date = formatter.parse(date1);
                    int countCollection = Integer.parseInt(collection.getElementsByTagName("CollectionCount").item(0).getTextContent());
                    String xmlVersionCollection = collection.getElementsByTagName("CollectionXmlVersion").item(0).getTextContent();
                    String xmlEncoding = collection.getElementsByTagName("CollectionXmlEncoding").item(0).getTextContent();
                    List<String> list = new ArrayList<>();
                    list.add("Type of collection: " + typeCollection);
                    list.add("Creation date of collection: " + date);
                    list.add("Count of elements in collection: " + countCollection);
                    list.add("XML version: " + xmlVersionCollection);
                    list.add("XML encoding: " + xmlEncoding);
                    return list;
                }
            } catch (NullPointerException ignored) {
                return new ArrayList<>();
            } catch (Exception e) {
                throw new FileException("incorrect collection info");
            }
            throw new FileException("incorrect collection info");
        }

        private void fillCollectionInfo(Document document) {
            List<Element> list = new ArrayList<>();
            Node root = document.getDocumentElement();
            Element collection = document.createElement("Collection");
            Element typeCollection = collection.getOwnerDocument().createElement("CollectionType");
            typeCollection.setTextContent("CSV");
            list.add(typeCollection);

            Element date = collection.getOwnerDocument().createElement("CollectionCreationDate");
            date.setTextContent(String.valueOf(new Date()));
            list.add(date);

            Element countCollection = collection.getOwnerDocument().createElement("CollectionCount");
            countCollection.setTextContent(String.valueOf(server.getListManager().getTicketList().size()));
            list.add(countCollection);

            Element xmlVersionCollection = collection.getOwnerDocument().createElement("CollectionXmlVersion");
            xmlVersionCollection.setTextContent(document.getXmlVersion());
            list.add(xmlVersionCollection);

            Element xmlEncoding = collection.getOwnerDocument().createElement("CollectionXmlEncoding");
            xmlEncoding.setTextContent(document.getXmlEncoding());
            list.add(xmlEncoding);
            for (Element element : list) {
                collection.appendChild(element);
            }
            root.appendChild(collection);
        }
    }
}
