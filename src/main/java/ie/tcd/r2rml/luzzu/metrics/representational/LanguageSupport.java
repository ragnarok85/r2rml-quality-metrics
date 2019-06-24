package ie.tcd.r2rml.luzzu.metrics.representational;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

public class LanguageSupport implements ComplexQualityMetric<Integer> {
	private static Logger logger = LoggerFactory.getLogger(LanguageSupport.class);
	private String datasetURI;
	private List<R2RMLMapping> mappings = new ArrayList<R2RMLMapping>();
	private ConcurrentHashMap<String, Set<String>> multipleLanguage = new ConcurrentHashMap<String, Set<String>>();

	public void compute(Quad quad) throws MetricProcessingException {
		for (R2RMLMapping mapping : mappings) {
			for (Map.Entry<Resource, TriplesMap> map : mapping.getTriplesMaps().entrySet()) {
				logger.info("Assessing triples maps: " + map.getKey().getLocalName());
				TriplesMap triplesMap = map.getValue();
				SubjectMap subjectMap = triplesMap.getSubjectMap();

				String subject = null;
				if (subjectMap.isTemplateValuedTermMap()) {
					subject = subjectMap.getTemplate();
				} else if (subjectMap.isColumnValuedTermMap()) {
					subject = subjectMap.getColumn();
				} else {
					subject = subjectMap.getConstant().asNode().getURI();
				}

				List<PredicateObjectMap> predicateObjectMaps = triplesMap.getPredicateObjectMaps();
				for (PredicateObjectMap predicateObjectMap : predicateObjectMaps) {
					List<ObjectMap> objectMaps = predicateObjectMap.getObjectMaps();
					for (ObjectMap objectMap : objectMaps) {
						if (!objectMap.getLanguages().isEmpty()) {
							String lang = objectMap.getLanguages().get(0).getObject().asLiteral().toString();
							Set<String> langList = new HashSet<String>();
							if (this.multipleLanguage.containsKey(subject))
								langList = this.multipleLanguage.get(subject);
							langList.add(lang);
							this.multipleLanguage.put(subject, langList);
						}
					}

				}
			}
		}
	}

	public Integer metricValue() {
		double totLang = 0.0;

		for (Set<String> lst : this.multipleLanguage.values()) {
			totLang += (double) lst.size();
		}
		double val = totLang / (double) this.multipleLanguage.size();

		return (Math.round(val) == 0) ? 1 : (int) Math.round(val);
	}

	public Resource getMetricURI() {
		return DQM.MultipleLanguageUsageMetric; // TODO create a new one? ask jeremy
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