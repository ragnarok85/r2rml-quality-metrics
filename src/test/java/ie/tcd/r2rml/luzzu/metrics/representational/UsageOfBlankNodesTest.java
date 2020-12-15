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
import io.github.luzzu.datatypes.r2rml.R2RMLException;
import io.github.luzzu.linkeddata.qualitymetrics.commons.TestLoader;

public class UsageOfBlankNodesTest {
	TestLoader loader = new TestLoader();
	List<R2RMLMapping> mappings;
	UsageOfBlankNodes metric;
	String file1 = "blank-nodes/mapping.ttl";
	String file2 = "blank-nodes/mapping2.ttl";
	String file3 = "blank-nodes/mapping3.ttl";
	String file4 = "blank-nodes/mapping4.ttl";
	String file5 = "blank-nodes/mapping5.ttl";

	@Before
	public void before() {
		mappings = new ArrayList<R2RMLMapping>();
		metric = new UsageOfBlankNodes();
	}

	@Test
	public void usageOfBlankNodesReadingR2RMLMappingTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);

		assertEquals(1, metric.getMappings().size());
	}

	@Test
	public void withSubjectAsBlankNodeTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0.5d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withObjectAsBlankNodeTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file2)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0.5d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withSubjectAndObjectAsBlankNodeTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file3)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withoutBlankNodesTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file4)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(1d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withoutClassAnObjectAsBlankNodesTest() throws BeforeException, MetricProcessingException, R2RMLException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file5)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(0d, metric.metricValue(), 0.00001);
	}
}
