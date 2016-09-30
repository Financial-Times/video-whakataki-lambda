package com.ft.whakataki.lambda.blazegraphrepo;

import static java.util.Objects.requireNonNull;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import org.openrdf.model.Model;
import org.openrdf.query.*;

import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BlazeGraphNameSpaceRepo {


    private final Configuration configuration;
    private RemoteRepositoryManager remoteRepositoryManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(BlazeGraphNameSpaceRepo.class);

    public BlazeGraphNameSpaceRepo(Configuration configuration) {
        this.configuration = requireNonNull(configuration);
        this.remoteRepositoryManager = new RemoteRepositoryManager(configuration.getServiceURL(), configuration.getUseLBS());
    }


    public Model executeGraphQuery(String queryString)  {
        try {
            RemoteRepository remoteRepo = getRemoteRepositoryManager().getRepositoryForNamespace(configuration.getNamespace());
            LOGGER.debug("Executing graphQuery:\n{}", queryString);
            GraphQueryResult graphQueryResult = remoteRepo.prepareGraphQuery(queryString).evaluate();

            if (remoteRepo.getBigdataSailRemoteRepository() != null)
                remoteRepo.getBigdataSailRemoteRepository().getConnection().close();
            Model result = QueryResults.asModel(graphQueryResult);
            graphQueryResult.close();
            return result;

        } catch (RepositoryException e) {
            LOGGER.error("Unable to get repository connection. ", e);
            throw new ThingLambdaException("Unable to get repository connection. ", e);
        } catch (MalformedQueryException e) {
            LOGGER.error("Unable to prepare the graph query as it is malformed", e);
            throw new ThingLambdaException("Unable to prepare the graph query as it is malformed", e);
        } catch (QueryEvaluationException e) {
            LOGGER.error("Unable to execute the graph query", e);
            throw new ThingLambdaException("Unable to execute the graph query", e);
        } catch (Exception e) {
            LOGGER.error("Unable to execute the graph query", e);
            throw new ThingLambdaException("Unable to execute the graph query", e);
        } finally {
            try {
                getRemoteRepositoryManager().close();
            } catch (Exception e) {
                LOGGER.warn("Cannot close the repository connection", e.getCause());
                throw new ThingLambdaException("Cannot close the repository connection", e);
            }
        }
    }


    public RemoteRepositoryManager getRemoteRepositoryManager() {
        return remoteRepositoryManager;
    }

    public void setRemoteRepositoryManager(RemoteRepositoryManager remoteRepositoryManager) {
        this.remoteRepositoryManager = remoteRepositoryManager;
    }

    public Configuration getConfiguration() {
        return configuration;
    }



}
