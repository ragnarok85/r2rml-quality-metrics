package ie.tcd.r2rml.luzzu.metrics.representational;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.luzzu.assessment.ComplexQualityMetric;
import io.github.luzzu.datatypes.r2rml.PredicateMap;
import io.github.luzzu.datatypes.r2rml.PredicateObjectMap;
import io.github.luzzu.datatypes.r2rml.R2RML;
import io.github.luzzu.datatypes.r2rml.R2RMLMapping;
import io.github.luzzu.datatypes.r2rml.TriplesMap;
import io.github.luzzu.exceptions.AfterException;
import io.github.luzzu.exceptions.BeforeException;
import io.github.luzzu.exceptions.MetricProcessingException;
import io.github.luzzu.linkeddata.qualitymetrics.commons.VocabularyLoader;
import io.github.luzzu.linkeddata.qualitymetrics.vocabulary.DQM;
import io.github.luzzu.qualityproblems.ProblemCollection;
import io.github.luzzu.qualityproblems.ProblemCollectionModel;
import io.github.luzzu.semantics.commons.ResourceCommons;

public class UsageOfUndefinedProperties implements ComplexQualityMetric<Double> {
	private static Logger logger = LoggerFactory.getLogger(UsageOfUndefinedProperties.class);
	private static final Resource UNDEFINED_PROPERTIES_METRIC = ModelFactory.createDefaultModel().createResource("http://purl.org/eis/vocab/dqm#UndefinedPropertiesMetric");

	private ProblemCollection<Model> problemCollection = new ProblemCollectionModel(UNDEFINED_PROPERTIES_METRIC);
	private int undefinedProperties = 0;
	private int totalProperties = 0;
	private String datasetURI;

	private List<R2RMLMapping> mappings = new ArrayList<R2RMLMapping>();

	public void compute(Quad quad) throws MetricProcessingException {
		for (R2RMLMapping mapping : mappings) {
			for (Map.Entry<Resource, TriplesMap> map : mapping.getTriplesMaps().entrySet()) {
				logger.info("Assessing triples maps: " + map.getKey().getLocalName());
				TriplesMap triplesMap = map.getValue();

				List<PredicateObjectMap> predicateObjectMaps = triplesMap.getPredicateObjectMaps();
				for (PredicateObjectMap predicateObjectMap : predicateObjectMaps) {
					List<PredicateMap> predicateMaps = predicateObjectMap.getPredicateMaps();
					for (PredicateMap predicateMap : predicateMaps) {
						if (predicateMap.isConstantValuedTermMap()) {
							logger.debug("Acessing property: " + predicateMap.getConstant().asResource().getURI());
							totalProperties++;
							Resource predicate = predicateMap.getConstant().asResource();
							if (!VocabularyLoader.getInstance().isProperty(predicate.asNode())) {
								undefinedProperties++;
								((ProblemCollectionModel) problemCollection).addProblem(createProblemModel(predicate), predicate.asResource());
							}
						}
					}
				}
			}
		}

		logger.debug("totalProperties: " + totalProperties);
		logger.debug("undefinedProperties: " + undefinedProperties);
	}

	private Model createProblemModel(Resource node) {
		Model model = ModelFactory.createDefaultModel().add(ResourceCommons.generateURI(), R2RML.predicate, node);

		return model;
	}

	public Double metricValue() {
		return (this.undefinedProperties == 0) ? 1.0 : 1.0 - ((double) this.undefinedProperties / this.totalProperties);
	}

	public Resource getMetricURI() {
		return UNDEFINED_PROPERTIES_METRIC;
	}

	public ProblemCollection<Model> getProblemCollection() {
		return null;
	}

	public boolean isEstimate() {
		return false;
	}

	public Model getObservationActivity() {
		Model activity = ModelFactory.createDefaultModel();

		return activity;
	}

	public Resource getAgentURI() {
		return DQM.LuzzuProvenanceAgent;
	}

	public void setDatasetURI(String datasetURI) {
		this.datasetURI = datasetURI;

	}

	public String getDatasetURI() {
		return datasetURI;
	}

	public void before(Object... args) throws BeforeException {
		mappings = (List<R2RMLMapping>) args[0];
	}

	public void after(Object... args) throws AfterException {
		// TODO check if something is needed to do after
	}

	public List<R2RMLMapping> getMappings() {
		return mappings;
	}
}