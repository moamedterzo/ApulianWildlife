package webservice.controller;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import java.util.ArrayList;

//This class manages the connection to the Graph DB, in order to get/set data
public final class GraphDBConnector {

    //Address of the GRAPH DB HOST
    static final String DB_GRAPH_HOST = "http://localhost:7200";

    //Name of the repository
    static final String REPOSITORY_NAME = "apulian_fauna";


    public static ArrayList<BindingSet> GetResultFromQuery(String queryString){
        return GetResultFromQuery(queryString, false);
    }

    public static ArrayList<BindingSet> GetResultFromQuery(String queryString, boolean flagUpdateQuery){

        // Instantiate a local repository manager and initialize it
        RepositoryManager repositoryManager = new RemoteRepositoryManager(DB_GRAPH_HOST);
        repositoryManager.initialize();

        // Get the repository from repository manager
        Repository repository = repositoryManager.getRepository(REPOSITORY_NAME);

        // Open a connection to this repository
        RepositoryConnection repositoryConnection = repository.getConnection();

        //it contains the result of the query
        ArrayList<BindingSet> finalResult = new ArrayList<>();

        if(flagUpdateQuery)
        {
            //execute update of data
            repositoryConnection.prepareUpdate(queryString).execute();
        }
        else{

            //read tuples
            TupleQuery query = repositoryConnection.prepareTupleQuery(queryString);

            // A QueryResult is also an AutoCloseable resource, so make sure it gets closed when done.
            try (TupleQueryResult result = query.evaluate()) {

                // we just iterate over all solutions in the result...
                while (result.hasNext()) {

                    finalResult.add(result.next());
                }
            }
        }

        // Shutdown connection, repository and manager
        repositoryConnection.close();
        repository.shutDown();
        repositoryManager.shutDown();

        //return data
        return finalResult;
    }
}
