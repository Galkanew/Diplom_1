package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BurgerParameterizedTest {

    private Burger burger;
    private final float bunPrice;
    private final int ingredientCount;
    private final float ingredientPrice;
    private final float expectedPrice;

    public BurgerParameterizedTest(float bunPrice, int ingredientCount,
                                   float ingredientPrice, float expectedPrice) {
        this.bunPrice = bunPrice;
        this.ingredientCount = ingredientCount;
        this.ingredientPrice = ingredientPrice;
        this.expectedPrice = expectedPrice;
    }

    @Before
    public void setUp() {
        burger = new Burger();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][] {
                {100f, 0, 0f, 200f},
                {100f, 1, 50f, 250f},
                {100f, 3, 30f, 290f},
                {150f, 2, 75f, 450f},
                {0f, 2, 100f, 200f},
                {50f, 5, 10f, 150f}
        });
    }

    @Test
    public void testPriceCalculationWithVariousParameters() {
        Bun bunMock = mock(Bun.class);
        when(bunMock.getPrice()).thenReturn(bunPrice);

        burger.setBuns(bunMock);

        for (int i = 0; i < ingredientCount; i++) {
            Ingredient ingredientMock = mock(Ingredient.class);
            when(ingredientMock.getPrice()).thenReturn(ingredientPrice);
            burger.addIngredient(ingredientMock);
        }

        float actualPrice = burger.getPrice();
        assertEquals(expectedPrice, actualPrice, 0.01);
    }
}