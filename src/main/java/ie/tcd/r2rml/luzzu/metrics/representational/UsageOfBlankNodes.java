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
import io.github.luzzu.datatypes.r2rml.ObjectMap;
import io.github.luzzu.datatypes.r2rml.PredicateObjectMap;
import io.github.luzzu.datatypes.r2rml.R2RMLMapping;
import io.github.luzzu.datatypes.r2rml.SubjectMap;
import io.github.luzzu.datatypes.r2rml.TriplesMap;
import io.github.luzzu.exceptions.AfterException;
import io.github.luzzu.exceptions.BeforeException;
import io.github.luzzu.exceptions.MetricProcessingException;
import io.github.luzzu.linkeddata.qualitymetrics.vocabulary.DQM;
import io.github.luzzu.qualityproblems.ProblemCollection;

public class UsageOfBlankNodes implements ComplexQualityMetric<Double> {
	private static Logger logger = LoggerFactory.getLogger(UsageOfBlankNodes.class);

	private int blankNodes = 0;
	private int resources = 0;
	private String datasetURI;

	private List<R2RMLMapping> mappings = new ArrayList<R2RMLMapping>();

	public void compute(Quad quad) throws MetricProcessingException {
		for (R2RMLMapping mapping : mappings) {
			// resources = resources + mapping.getTriplesMaps().size();
			for (Map.Entry<Resource, TriplesMap> map : mapping.getTriplesMaps().entrySet()) {
				logger.info("Assessing triples maps: " + map.getKey().getLocalName());
				TriplesMap triplesMap = map.getValue();
				SubjectMap subjectMap = triplesMap.getSubjectMap();
				if (!subjectMap.getClasses().isEmpty()) {
					resources++;
					if (subjectMap.isTermTypeBlankNode()) {
						blankNodes++;
					}
				}

				List<PredicateObjectMap> predicateObjectMaps = triplesMap.getPredicateObjectMaps();
				for (PredicateObjectMap predicateObjectMap : predicateObjectMaps) {
					List<ObjectMap> objectMaps = predicateObjectMap.getObjectMaps();
					for (ObjectMap objectMap : objectMaps) {
						// if (objectMap.isConstantValuedTermMap()) {
						resources++;
						if (objectMap.isTermTypeBlankNode()) {
							blankNodes++;
						}
						// }
					}
				}
			}
		}

		logger.debug("blankNodes: " + blankNodes);
		logger.debug("resouces: " + resources);
	}

	public Double metricValue() {
		return (this.resources == 0) ? 1.0 : 1.0 - ((double) this.blankNodes / this.resources);
	}

	public Resource getMetricURI() {
		return DQM.NoBlankNodeMetric;
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

	@SuppressWarnings("unchecked")
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