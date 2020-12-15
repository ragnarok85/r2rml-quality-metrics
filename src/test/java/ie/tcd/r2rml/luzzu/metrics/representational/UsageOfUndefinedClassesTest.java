package ie.tcd.r2rml.luzzu.metrics.representational;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;

import io.github.luzzu.datatypes.r2rml.R2RMLMapping;
import io.github.luzzu.datatypes.r2rml.R2RMLMappingFactory;
import io.github.luzzu.exceptions.BeforeException;
import io.github.luzzu.datatypes.r2rml.R2RMLException;
import io.github.luzzu.exceptions.MetricProcessingException;
import io.github.luzzu.linkeddata.qualitymetrics.commons.TestLoader;

public class UsageOfUndefinedClassesTest {
	TestLoader loader = new TestLoader();
	private List<R2RMLMapping> mappings;
	UsageOfUndefinedClasses metric;
	String file1 = "undefined-classes/mapping.ttl";
	String file2 = "undefined-classes/mapping2.ttl";
	String file3 = "undefined-classes/mapping3.ttl";
	String file4 = "undefined-classes/mapping4.ttl";
	String file5 = "undefined-classes/mapping5.ttl";

	@Before
	public void before() {
		mappings = new ArrayList<R2RMLMapping>();
		metric = new UsageOfUndefinedClasses();
	}

	@Test
	public void undefinedClassesReadingR2RMLMappingTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);

		assertEquals(1, metric.getMappings().size());
	}

	@Test
	public void undefinedClassesTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0.5d, metric.metricValue(), 0.00001);

		// TODO check with jeremy
		// loader.outputProblemReport(metric.getProblemCollection(),
		// "/Users/jeremy/Desktop/luzzu-quality-tests/Representational/multiple-languages.ttl");
	}

	@Test
	public void undefinedClassesWithTwoFilesTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file2)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0.333333d, metric.metricValue(), 0.00001);
		// loader.outputProblemReport(metric.getProblemCollection(),
		// "/Users/jeremy/Desktop/luzzu-quality-tests/Representational/multiple-languages.ttl");
	}

	@Test
	public void undefinedClassesWithDefinedClassTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file3)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(1d, metric.metricValue(), 0.00001);
		// loader.outputProblemReport(metric.getProblemCollection(),
		// "/Users/jeremy/Desktop/luzzu-quality-tests/Representational/multiple-languages.ttl");
	}

	@Test
	public void undefinedClassesWithTwoUndefinedClassesTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file4)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0d, metric.metricValue(), 0.00001);

		// metric.getProblemCollection().getDataset().getDefaultModel().write(System.out);
		// loader.outputProblemReport(metric.getProblemCollection(),
		// "/Users/jeremy/Desktop/luzzu-quality-tests/Representational/multiple-languages.ttl");
	}

	@Test
	public void undefinedClassesWithTwoTriplesMapsWithOneClassEachTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file5)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0.5d, metric.metricValue(), 0.00001);
		// loader.outputProblemReport(metric.getProblemCollection(),
		// "/Users/jeremy/Desktop/luzzu-quality-tests/Representational/multiple-languages.ttl");
	}

}
