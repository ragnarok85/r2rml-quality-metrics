package ie.tcd.r2rml.luzzu.metrics.representational;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.luzzu.assessment.ComplexQualityMetric;
import io.github.luzzu.datatypes.r2rml.ObjectMap;
import io.github.luzzu.datatypes.r2rml.PredicateMap;
import io.github.luzzu.datatypes.r2rml.PredicateObjectMap;
import io.github.luzzu.datatypes.r2rml.R2RMLMapping;
import io.github.luzzu.datatypes.r2rml.SubjectMap;
import io.github.luzzu.datatypes.r2rml.TriplesMap;
import io.github.luzzu.exceptions.AfterException;
import io.github.luzzu.exceptions.BeforeException;
import io.github.luzzu.exceptions.MetricProcessingException;
import io.github.luzzu.linkeddata.qualitymetrics.commons.VocabularyLoader;
import io.github.luzzu.linkeddata.qualitymetrics.vocabulary.DQM;
import io.github.luzzu.qualityproblems.ProblemCollection;

public class UsageOfUndefinedClasses implements ComplexQualityMetric<Double> {
	private static final Resource UNDEFINED_CLASSES_METRIC = ModelFactory.createDefaultModel().createResource("http://purl.org/eis/vocab/dqm#UndefinedClassesMetric");
	private static Logger logger = LoggerFactory.getLogger(UsageOfUndefinedClasses.class);

	private int undefinedClasses = 0;
	private int totalClasses = 0;
	private String datasetURI;

	private List<R2RMLMapping> mappings = new ArrayList<R2RMLMapping>();

	public void compute(Quad quad) throws MetricProcessingException {
		for (R2RMLMapping mapping : mappings) {
			for (Map.Entry<Resource, TriplesMap> map : mapping.getTriplesMaps().entrySet()) {
				logger.info("Assessing triples maps: " + map.getKey().getLocalName());
				TriplesMap triplesMap = map.getValue();

				SubjectMap subjectMap = triplesMap.getSubjectMap();

				for (Resource resourceClass : subjectMap.getClasses()) {
					logger.debug("Acessing class: " + resourceClass.getURI());
					totalClasses++;
					if (!VocabularyLoader.getInstance().isClass(resourceClass.asNode())) {
						undefinedClasses++;
						// problemCollection.addProblem(ModelFactory.createDefaultModel().add(s));
					}
				}

				List<PredicateObjectMap> predicateObjectMaps = triplesMap.getPredicateObjectMaps();
				for (PredicateObjectMap predicateObjectMap : predicateObjectMaps) {
					boolean isClassPOM = false;
					List<PredicateMap> predicateMaps = predicateObjectMap.getPredicateMaps();
					for (PredicateMap predicateMap : predicateMaps) {
						if (predicateMap.isConstantValuedTermMap()) {
							RDFNode predicate = predicateMap.getConstant();
							if (predicate.asResource().hasURI(RDF.type.getURI())) {
								isClassPOM = true;
							}
						}
					}
					if (isClassPOM) {
						List<ObjectMap> objectMaps = predicateObjectMap.getObjectMaps();
						for (ObjectMap objectMap : objectMaps) {
							if (objectMap.isConstantValuedTermMap()) {
								logger.debug("Acessing class: " + objectMap.getConstant().asResource().getURI());
								totalClasses++;
								if (!VocabularyLoader.getInstance().isClass(objectMap.getConstant().asNode())) {
									undefinedClasses++;
								}
							}
						}
					}
				}
			}
		}

		logger.debug("totalClasses: " + totalClasses);
		logger.debug("undefinedClasses: " + undefinedClasses);
	}

	public Double metricValue() {
		return (this.undefinedClasses == 0) ? 1.0 : 1.0 - ((double) this.undefinedClasses / this.totalClasses);
	}

	public Resource getMetricURI() {
		return UNDEFINED_CLASSES_METRIC;
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
	}

	public List<R2RMLMapping> getMappings() {
		return mappings;
	}
}