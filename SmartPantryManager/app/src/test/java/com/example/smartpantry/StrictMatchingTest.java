Spackage com.example.smartpantry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class StrictMatchingTest {

    @Test
    public void testIngredientNameNormalization() {
        assertEquals("tomato", DatabaseHelper.normalizeName("tomatoes"));
        assertEquals("tomato", DatabaseHelper.normalizeName("Tomato"));
        assertEquals("egg", DatabaseHelper.normalizeName("eggs"));
        assertEquals("potato", DatabaseHelper.normalizeName("potatoes"));
        assertEquals("onion", DatabaseHelper.normalizeName("onions"));
        assertEquals("carrot", DatabaseHelper.normalizeName("carrots"));
        assertEquals("apple", DatabaseHelper.normalizeName("apples"));
        assertEquals("banana", DatabaseHelper.normalizeName("bananas"));
        assertEquals("oat", DatabaseHelper.normalizeName("oats"));
    }

    @Test
    public void testUnitNormalizationAndCompatibility() {
        assertEquals("g", DatabaseHelper.normalizeUnit("grams"));
        assertEquals("kg", DatabaseHelper.normalizeUnit("kilogram"));
        assertEquals("ml", DatabaseHelper.normalizeUnit("millilitres"));
        assertEquals("l", DatabaseHelper.normalizeUnit("liter"));
        assertEquals("piece", DatabaseHelper.normalizeUnit("pieces"));
        assertEquals("clove", DatabaseHelper.normalizeUnit("cloves"));

        assertTrue(DatabaseHelper.compatibleUnit("g", "kg"));
        assertTrue(DatabaseHelper.compatibleUnit("ml", "l"));
        assertTrue(DatabaseHelper.compatibleUnit("piece", "pieces"));
        assertFalse(DatabaseHelper.compatibleUnit("g", "ml"));
    }

    @Test
    public void testUnitConversion() {
        assertEquals(1000.0, DatabaseHelper.convert(1.0, "kg", "g"), 1e-6);
        assertEquals(0.2, DatabaseHelper.convert(200.0, "g", "kg"), 1e-6);
        assertEquals(1000.0, DatabaseHelper.convert(1.0, "l", "ml"), 1e-6);
        assertEquals(0.5, DatabaseHelper.convert(500.0, "ml", "l"), 1e-6);
    }
}
