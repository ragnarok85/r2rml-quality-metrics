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

public class LanguageSupportTest {
	TestLoader loader = new TestLoader();
	List<R2RMLMapping> mappings;
	LanguageSupport metric;
	String file1 = "language-support/mapping.ttl";
	String file2 = "language-support/mapping2.ttl";
	String file3 = "language-support/mapping3.ttl";

	@Before
	public void before() {
		mappings = new ArrayList<R2RMLMapping>();
		metric = new LanguageSupport();
	}

	@Test
	public void languageSupportReadingR2RMLMappingTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);

		assertEquals(1, metric.getMappings().size());
	}

	@Test
	public void withTwoLanguageTagsTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file1)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(2d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withoutLanguageTagsTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file2)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(1d, metric.metricValue(), 0.00001);
	}

	@Test
	public void withTwoLiteralsOneWithAndOneWithoutLanguageTagTest() throws BeforeException, MetricProcessingException {
		mappings.add(R2RMLMappingFactory.createR2RMLMappingFromModel(ModelFactory.createDefaultModel().read(file3)));
		Object[] args = new Object[] { mappings };
		metric.before(args);
		metric.compute(null);

		assertEquals(1d, metric.metricValue(), 0.00001);
	}
}
