package com.ft.whakataki.lambda.blazegraphrepo;

import com.bigdata.rdf.sail.webapp.client.IPreparedGraphQuery;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

import com.ft.whakataki.lambda.config.Configuration;
import com.ft.whakataki.lambda.thing.exception.ThingLambdaException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.openrdf.model.Model;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.impl.GraphQueryResultImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;


public class BlazeGraphNameSpaceRepoTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final Configuration configuration = mock(Configuration.class);

    private final RemoteRepositoryManager remoteRepositoryManager = mock(RemoteRepositoryManager.class);
    private final RemoteRepository remoteRepository = mock(RemoteRepository.class);
    private final IPreparedGraphQuery graphQuery = mock(IPreparedGraphQuery.class);
    final ValueFactory valueFactory = new ValueFactoryImpl();


    private final String NAMESPACE_URL = "whakataki";
    private final String SERVICE_URL = "http://localhost:9999/blazegraph";


    private final String THING_QUERY_STRING = "";
    private final String SPARQL_QUERY = "";


    protected BlazeGraphNameSpaceRepo getBlazeNameSpaceRepo(Configuration configuration) throws Exception {
        when(this.configuration.getServiceURL()).thenReturn(SERVICE_URL);
        when(this.configuration.getNamespace()).thenReturn(NAMESPACE_URL);
        BlazeGraphNameSpaceRepo blazeGraphNameSpaceRepo = new BlazeGraphNameSpaceRepo(this.configuration);
        blazeGraphNameSpaceRepo.setRemoteRepositoryManager(this.remoteRepositoryManager);
        when(remoteRepositoryManager.getRepositoryForNamespace(NAMESPACE_URL)).thenReturn(remoteRepository);
        return blazeGraphNameSpaceRepo;
    }

    @Test
    public void shouldThrowOpenRDFExceptionWhenGraphQueryIsMalformed() throws Exception {
        expectedException.expect(ThingLambdaException.class);
        BlazeGraphNameSpaceRepo blazeGraphNameSpaceRepo = getBlazeNameSpaceRepo(configuration);
        when(remoteRepository.prepareGraphQuery(anyString())).thenThrow(new MalformedQueryException());
        blazeGraphNameSpaceRepo.executeGraphQuery("This aint SPARQL");
        verify(remoteRepositoryManager, timeout(1)).close();
    }

    @Test
    public void shouldReturnSuccessfullyAndCloseConnection() throws Exception {
        Set<Statement> statements = new HashSet<>();
        StatementImpl statement = new StatementImpl(valueFactory.createURI("urn:1"), valueFactory.createURI("urn:2"), valueFactory.createURI("urn:3"));
        statements.add(statement);
        GraphQueryResult graphQueryResult = new GraphQueryResultImpl(Collections.singletonMap("String", "String"), statements);
        when(remoteRepository.prepareGraphQuery(anyString())).thenReturn(graphQuery);
        when(graphQuery.evaluate()) .thenReturn(graphQueryResult);
        Model result = getBlazeNameSpaceRepo(configuration).executeGraphQuery(SPARQL_QUERY);
        assertThat(result.isEmpty(), is(false));
        verify(remoteRepository, times(1)).prepareGraphQuery(SPARQL_QUERY);
        verify(graphQuery, times(1)).evaluate();
        verify(remoteRepositoryManager, timeout(1)).close();
    }


}
