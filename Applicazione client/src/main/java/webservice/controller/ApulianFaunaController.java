package webservice.controller;
import static webservice.controller.GraphDBConnector.GetResultFromQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

//This controller manages the client request for the data of the Apulian Fauna Repository
@RestController
@EnableAutoConfiguration
public class ApulianFaunaController {

    @PostMapping(value = "/ricercaTassonomia",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ricercaTassonomia()
            throws JsonProcessingException {

        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX : <http://localhost/apulian_fauna#>\n" +
                "prefix wd: <http://www.wikidata.org/entity/>\n" +
                "PREFIX schema: <http://schema.org/>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "  SELECT ?animalLabel ?firstLevelClassLabel ?secondLevelClassLabel ?sitelink\n" +
                "    WHERE {\n" +
                "         ?animal a ?firstLevelClass; \n" +
                "                       rdfs:label ?animalLabel .\n" +
                "         #OTTENIMENTO LINK WIKIPEDIA\n" +
                "         optional {\n" +
                "             SERVICE <https://query.wikidata.org/sparql> {\n" +
                "                ?sitelink schema:about ?animal.\n" +
                "                FILTER REGEX(STR(?sitelink), \"en.wikipedia.org/wiki/\") .\n" +
                "             }\n" +
                "          }\n" +
                "          ?firstLevelClass rdfs:subClassOf wd:Q729; \n" +
                "\t\t\t       rdfs:label ?firstLevelClassLabel.\n" +
                "    \t  FILTER(?firstLevelClass != wd:Q729)\n" +
                "          FILTER NOT EXISTS {         \t\t\t\n" +
                "                       ?animal a ?other .\n" +
                "                       ?firstLevelClass rdfs:subClassOf ?other .\n" +
                "            \t   \t   FILTER(?other != ?firstLevelClass && ?other != wd:Q729 && ?other != owl:Thing)\n" +
                "          }\n" +
                "          optional{\n" +
                "         ?secondLevelClass rdfs:subClassOf ?firstLevelClass; \n" +
                "rdfs:label ?secondLevelClassLabel.\n" +
                "                   ?animal a ?secondLevelClass\n" +
                "        FILTER (?secondLevelClass != ?firstLevelClass)\n" +
                "            FILTER NOT EXISTS { \n" +
                "?animal a ?other .\n" +
                "                \t?secondLevelClass rdfs:subClassOf ?other .\n" +
                "                \tFILTER(\n" +
                "\t\t\t\t\t?other != ?secondLevelClass && ?other != ?firstLevelClass      && ?other != wd:Q729 && ?other != owl:Thing)\n" +
                "            \t}\n" +
                "          \t  }\n" +
                "     }\n" +
                "    order by ?firstLevelClassLabel ?secondLevelClassLabel ?animalLabel\n";

        ArrayList<String[]> valueResults = new ArrayList<>();

        for(BindingSet solution : GetResultFromQuery(queryString))
        {
            String[] solutionValues = new String[4];

            solutionValues[0] = solution.getValue("firstLevelClassLabel").stringValue();

            if(solution.getValue("secondLevelClassLabel") != null)
                solutionValues[1] = solution.getValue("secondLevelClassLabel").stringValue();
            else
                solutionValues[1] = "";

            solutionValues[2] = solution.getValue("animalLabel").stringValue();

            Value siteLinkValue = solution.getValue("sitelink");

            if(siteLinkValue != null)
                solutionValues[3] = siteLinkValue.stringValue();
            else
                solutionValues[3] = "#";

            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }


    @PostMapping(value = "/ricercaAnimaliFuoriHabitat",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ricercaAnimaliFuoriHabitat(@RequestParam(value = "year") String year)
            throws JsonProcessingException {

        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX : <http://localhost/apulian_fauna#>\n" +
                "    prefix wd: <http://www.wikidata.org/entity/>\n" +
                "    prefix time: <http://www.w3.org/2006/time#>\n" +
                "    prefix lode: <http://linkedevents.org/ontology/>\n" +
                "\n" +
                "    SELECT  distinct ?labelAnimal ?labelPlaceSighting\n" +
                "    WHERE {\n" +
                " ?animal a wd:Q729; rdfs:label ?labelAnimal; :preferredHabitat ?animalHabitat.\n" +
                "                ?sighting a :Sighting; lode:atTime ?istantSighting; lode:involved ?animal; lode:atPlace ?placeSighting.\n" +
                "                ?istantSighting time:inXSDDate ?dateIstantSighting.\n" +
                "                ?placeSighting rdfs:label ?labelPlaceSighting; a wd:Q179049.\n" +
                "\n" +
                "                FILTER NOT EXISTS {\t?placeSighting :hasHabitat ?animalHabitat }\n" +
                "\n" +
                "        FILTER (year(?dateIstantSighting) = "+ year +")\n" +
                "    }" +
                "ORDER BY ?labelAnimal ?labelPlaceSighting";

        ArrayList<String[]> valueResults = new ArrayList<>();

        for(BindingSet solution : GetResultFromQuery(queryString))
        {
            String[] solutionValues = new String[2];

            solutionValues[0] = solution.getValue("labelAnimal").stringValue();
            solutionValues[1] = solution.getValue("labelPlaceSighting").stringValue();

            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }

    @PostMapping(value = "/ricercaAreeRicercatori",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ricercaAreeRicercatori(@RequestParam(value = "area") String area)
            throws JsonProcessingException {

        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX : <http://localhost/apulian_fauna#>\n" +
                "\n" +
                "    SELECT distinct ?labelRicercatore ?labelProject\n" +
                "    WHERE {?ricercatore :workOnZone ?protectedArea; :workOnProject ?project; rdfs:label ?labelRicercatore.\n" +

                "            ?project rdfs:label ?labelProject .\n" +
                "\n" +
                "    FILTER (?protectedArea = <"+ area +">)"
                  +  "}" +
                "ORDER BY ?labelRicercatore ?labelProject";

        ArrayList<String[]> valueResults = new ArrayList<>();

        for (BindingSet solution : GetResultFromQuery(queryString)) {
            String[] solutionValues = new String[2];

            solutionValues[0] = solution.getValue("labelRicercatore").stringValue();
            solutionValues[1] = solution.getValue("labelProject").stringValue();

            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }

    @PostMapping(value = "/ricercaDirigenti",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ricercaDirigenti()
            throws JsonProcessingException {

        String queryString = "PREFIX : <http://localhost/apulian_fauna#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT ?labelChiefResearcher ?labelProgettoDiRicerca ?endYear\n" +
                "WHERE {\n" +
                "    ?chiefResearcher :projectLeader ?progettoDiRicerca; rdfs:label ?labelChiefResearcher.\n" +
                "    ?progettoDiRicerca rdfs:label ?labelProgettoDiRicerca.     \n" +
                "    \n" +
                "    optional {       \n" +
                "        ?progettoDiRicerca :endYear ?endYear.\n" +
                "    }\n" +
                "   \n" +
                "}\n" +
                "ORDER BY DESC(?endYear) ?labelChiefResearcher ?labelProgettoDiRicerca";

        System.out.println(queryString);

        ArrayList<String[]> valueResults = new ArrayList<>();

        for (BindingSet solution : GetResultFromQuery(queryString)) {
            String[] solutionValues = new String[3];

            solutionValues[0] = solution.getValue("labelChiefResearcher").stringValue();
            solutionValues[1] = solution.getValue("labelProgettoDiRicerca").stringValue();

            Value endYear = solution.getValue("endYear");

            if(endYear != null)
                solutionValues[2] = endYear.stringValue();
            else
                solutionValues[2] = "";

            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }

    @PostMapping(value = "/ricercaAnimaliARischio",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ricercaAnimaliARischio()
            throws JsonProcessingException {

        String queryString = "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "    PREFIX : <http://localhost/apulian_fauna#>\n" +
                "    prefix wd: <http://www.wikidata.org/entity/>\n" +
                "\n" +
                "    SELECT  distinct ?labelAnimal\n" +
                "    WHERE {\n" +
                " ?animal a wd:Q729; rdfs:label ?labelAnimal; :hasStatus :AtRisk.\n" +

                "    }" +
                "ORDER BY ?labelAnimal";



        ArrayList<String[]> valueResults = new ArrayList<>();

        for (BindingSet solution : GetResultFromQuery(queryString)) {
            String[] solutionValues = new String[1];

            solutionValues[0] = solution.getValue("labelAnimal").stringValue();

            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }




    @PostMapping(value = "/ottieniAnimali",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ottieniAnimali(@RequestParam(value = "filter") String filter)
            throws JsonProcessingException {

        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "prefix wd: <http://www.wikidata.org/entity/>\n" +
                "\n" +
                "SELECT distinct  ?animal ?label\n" +
                "WHERE {\n" +
                " ?animal a wd:Q729.\n" +
                "?animal rdfs:label ?label.\n" +
                "FILTER REGEX(?label, \""+ filter.trim() +"\", \"i\")\n" +
                " }" +
                "ORDER BY ?label\n";

        ArrayList<String[]> valueResults = new ArrayList<>();

        for(BindingSet solution : GetResultFromQuery(queryString))
        {
            String[] solutionValues = new String[2];

            solutionValues[0] = solution.getValue("animal").stringValue();
            solutionValues[1] = solution.getValue("label").stringValue();

            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }

    @PostMapping(value = "/ottieniAree",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ottieniAree(@RequestParam(value = "filter") String filter)
            throws JsonProcessingException {

        String queryString = "PREFIX wd: <http://www.wikidata.org/entity/>\n" +
                "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX : <http://localhost/apulian_fauna#>\n" +
                "\n" +
                "select distinct ?area ?label where {     \n" +
                "\t#COMUNI ITALIANI\n" +
                "    {\n" +
                "        SERVICE <https://query.wikidata.org/sparql> { \n" +
                "        \n" +
                "            ?area wdt:P31 wd:Q747074;\n" +
                "            wdt:P131 ?province;\n" +
                "            rdfs:label ?label.\n" +
                "\n" +
                "            #PROVINCIA APPARTENENTE ALLA PUGLIA \n" +
                "            ?province wdt:P131 wd:Q1447. \n" +
                "\n" +
                "            filter(lang(?label) = 'en')\n" +
                "    \t}\n" +
                "    }\n" +
                "    #AREE PROTETTE\n" +
                "    UNION{\n" +
                "        ?area a :SecondLevelArea.\n" +
                "        ?area rdfs:label ?label.    \n" +
                "\t}\n" +
                "    FILTER REGEX(?label, \""+ filter +"\", \"i\")\n" +
                "}" +
                "ORDER BY ?label";

        ArrayList<String[]> valueResults = new ArrayList<>();

        for(BindingSet solution : GetResultFromQuery(queryString))
        {
            String[] solutionValues = new String[2];

            solutionValues[0] = solution.getValue("area").stringValue();
            solutionValues[1] = solution.getValue("label").stringValue();

            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }

    @PostMapping(value = "/ricercaAvvistamenti",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String ricercaAvvistamenti(@RequestParam(value = "year") String year,
                                             @RequestParam(value = "animale") String animale,
                                             @RequestParam(value = "area") String area)
            throws JsonProcessingException {

        //valorizzazione parametri query
        if(animale == null || animale.isEmpty()){
            animale = "?animal";
        }
        else{
            animale = "<" + animale + ">";
        }
        if(area == null || area.isEmpty()){
            area = "?place";
        }
        else{
            area = "<" + area + ">";
        }

        String queryString = "PREFIX : <http://localhost/apulian_fauna#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX lode: <http://linkedevents.org/ontology/>\n" +
                "PREFIX time: <http://www.w3.org/2006/time#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "\n" +
                "SELECT distinct ?labelAnimal ?labelPlace (SUM(?animalSightings) as ?animalSightings)\n" +
                "\n" +
                "WHERE { \n" +
                "    ?avvistamento lode:involved ?animal ; lode:atPlace ?place;  lode:atTime ?time.\n" +
                "    ?avvistamento :numberOfSightings ?animalSightings .\n" +
                "    \n" +
                "     ?animal rdfs:label ?labelAnimal .\n" +
                "     ?place rdfs:label ?labelPlace .\n" +
                "     ?time time:inXSDDate ?timeDate\n" +
                "    \n" +
                "    FILTER (year(?timeDate) = "+ year +")\n" +
                "    \n" +
                "    FILTER (?animal = "+ animale +")\n" +
                "    FILTER (?place = "+ area +")\n" +
                "            \n" +
                "}\n" +
                "GROUP BY ?animal ?labelAnimal ?place ?labelPlace " +
                "ORDER BY ?labelAnimal ?labelPlace";

        ArrayList<String[]> valueResults = new ArrayList<>();

        for (BindingSet solution : GetResultFromQuery(queryString)) {
            String[] solutionValues = new String[3];

            //potrebbe essere nullo il valore, in caso di nessun risultato
            Value labelAnimal = solution.getValue("labelAnimal");
            if(labelAnimal == null)
                break;

            solutionValues[0] = labelAnimal.stringValue();
            solutionValues[1] = solution.getValue("labelPlace").stringValue();
            solutionValues[2] = solution.getValue("animalSightings").stringValue();
            valueResults.add(solutionValues);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(valueResults);

        return json;
    }
    @PostMapping(value = "/inserisciAvvistamento",  produces = MediaType.APPLICATION_JSON_VALUE)
    public static String inserisciAvvistamento(@RequestParam(value = "date") String date,
                                        @RequestParam(value = "animal") String animal,
                                        @RequestParam(value = "numberOfSightings") String numberOfSightings,
                                        @RequestParam(value = "area") String area,
                                        @RequestParam(value = "areaLabel") String areaLabel) throws JsonProcessingException {

        String queryForNumberOfSightings = "PREFIX : <http://localhost/apulian_fauna#>\n" +
                "PREFIX lode: <http://linkedevents.org/ontology/>\n" +
                "             \n" +
                "SELECT  (count(?sighting) as ?count )\n" +
                "WHERE {\n" +
                "    ?sighting a :Sighting. ?sighting lode:atPlace ?place.    \n" +
                "}";

        //count used for the ID of the next sighting inserted
        int count = 0;

        //getting the value...
        for (BindingSet solution : GetResultFromQuery(queryForNumberOfSightings)) {
            count = Integer.parseInt(solution.getValue("count").stringValue());
        }

        String insertQueryString = "PREFIX lode: <http://linkedevents.org/ontology/>\n" +
        "                PREFIX : <http://localhost/apulian_fauna#>\n" +
        "                PREFIX wd: <http://www.wikidata.org/entity/>\n" +
        "                PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
        "                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "                PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
        "                INSERT DATA{\n" +
        "                :Sighting_"+count+" lode:involved <"+ animal+"> .\n" +
        "                :Sighting_"+count+" lode:atPlace <"+area+"> .\n" +
        "                :Sighting_"+count+" lode:atTime :"+date+" . \n" +
        "                :Sighting_"+count+" :numberOfSightings \""+numberOfSightings+"\"^^xsd:integer . \n" +
        "                :Sighting_"+count+" rdf:type :Sighting . \n" +
        "                :Sighting_"+count+" rdf:type owl:NamedIndividual .\n" +
        "                :"+ date + " time:inXSDDate \""+ date +"\"^^xsd:date.\n" +
        "                <"+ area +"> rdfs:label \""+ areaLabel +"\"@en  . \n" +
        "                }";

        System.out.println("Inserted a sighting. This is the query:");
        System.out.println(insertQueryString);

        GetResultFromQuery(insertQueryString, true);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString("ok");

        return json;
    }
}
