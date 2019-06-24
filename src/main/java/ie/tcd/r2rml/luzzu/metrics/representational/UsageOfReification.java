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
import io.github.luzzu.linkeddata.qualitymetrics.vocabulary.DQM;
import io.github.luzzu.qualityproblems.ProblemCollection;

public class UsageOfReification implements ComplexQualityMetric<Double> {
	private static Logger logger = LoggerFactory.getLogger(UsageOfReification.class);

	private int totalTriples = 0;
	private int totalRCCClassesAndProperties = 0;
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
					totalTriples++;
					if (resourceClass.hasURI(RDF.Statement.getURI())) {
						totalRCCClassesAndProperties++;
					}
				}

				List<PredicateObjectMap> predicateObjectMaps = triplesMap.getPredicateObjectMaps();
				for (PredicateObjectMap predicateObjectMap : predicateObjectMaps) {
					boolean isClassPOM = false;
					List<PredicateMap> predicateMaps = predicateObjectMap.getPredicateMaps();
					for (PredicateMap predicateMap : predicateMaps) {
						if (predicateMap.isConstantValuedTermMap()) {
							RDFNode predicate = predicateMap.getConstant();
							totalTriples++;
							if (predicate.asResource().hasURI(RDF.type.getURI())) {
								isClassPOM = true;
							} else if ((predicate.asNode().hasURI(RDF.subject.getURI())) || (predicate.asNode().hasURI(RDF.predicate.getURI())) || (predicate.asNode().hasURI(RDF.object.getURI()))) {
								// if (requireProblemReport) {
								// Quad q = new Quad(null, subject,
								// QPRO.exceptionDescription.asNode(),
								// DQMPROB.UsageOfReification.asNode());
								// problemCollection.addProblem(q);
								// }
								totalRCCClassesAndProperties++;
							}
						}
					}
					if (isClassPOM) {
						List<ObjectMap> objectMaps = predicateObjectMap.getObjectMaps();
						for (ObjectMap objectMap : objectMaps) {
							if (objectMap.isConstantValuedTermMap()) {
								logger.debug("Acessing class: " + objectMap.getConstant().asResource().getURI());
								totalTriples++;
								if (objectMap.getConstant().asNode().hasURI(RDF.Statement.getURI())) {
									totalRCCClassesAndProperties++;
								}
							}
						}
					}
				}
			}
		}

		logger.debug("totalTriples: " + totalTriples);
		logger.debug("totalRCCClassesAndProperties: " + totalRCCClassesAndProperties);
	}

	public Double metricValue() {
		return (this.totalRCCClassesAndProperties == 0) ? 1.0 : 1.0 - ((double) this.totalRCCClassesAndProperties / this.totalTriples);
	}

	public Resource getMetricURI() {
		return DQM.NoProlixRDFMetric; // TODO create a new one? ask jeremy
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