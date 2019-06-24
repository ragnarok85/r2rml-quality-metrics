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
import io.github.luzzu.exceptions.MetricProcessingException;
import io.github.luzzu.linkeddata.qualitymetrics.commons.TestLoader;

public class UsageOfReificationTest {
	TestLoader loader = new TestLoader();
	private List<R2RMLMapping> mappings;
	UsageOfReification metric;
	String file1 = "reification/mapping.ttl";
	String file2 = "reification/mapping2.ttl";
	String file3 = "reification/mapping3.ttl";

	@Before
	public void before() {
		mappings = new ArrayList<R2RMLMapping>();
		metric = new UsageOfReification();
	}

	@Test
	public void reificationUsageReadingR2RMLMappingTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);

		assertEquals(1, metric.getMappings().size());
	}

	@Test
	public void withARDFStatementAndANonReificationPropertyTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0.5d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withARDFStatementAndRDFPropertiesTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file2)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withoutReificationTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file3)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(1d, metric.metricValue(), 0.00001);
	}
}
