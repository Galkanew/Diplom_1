package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
  * Тестируем логику работы
 */
@RunWith(MockitoJUnitRunner.class)
public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun bunMock;

    @Mock
    private Ingredient sauceMock;

    @Mock
    private Ingredient fillingMock;

    @Mock
    private Ingredient additionalSauceMock;

    @Before
    public void setUp() {
        burger = new Burger();
    }

    @Test
    public void testSetBunsShouldSetBunInBurger() {
        burger.setBuns(bunMock);
        assertEquals(bunMock, burger.bun);
    }

    @Test
    public void testSetBunsCanChangeBun() {
        Bun firstBunMock = mock(Bun.class);
        Bun secondBunMock = mock(Bun.class);

        burger.setBuns(firstBunMock);
        assertEquals(firstBunMock, burger.bun);

        burger.setBuns(secondBunMock);
        assertEquals(secondBunMock, burger.bun);
    }

    @Test
    public void testAddIngredientShouldAddToIngredientsList() {
        burger.addIngredient(sauceMock);
        assertEquals(1, burger.ingredients.size());
        assertTrue(burger.ingredients.contains(sauceMock));
    }

    @Test
    public void testAddMultipleIngredients() {
        burger.addIngredient(sauceMock);
        burger.addIngredient(fillingMock);
        burger.addIngredient(additionalSauceMock);
        assertEquals(3, burger.ingredients.size());
    }

    @Test
    public void testRemoveIngredientShouldRemoveFromListByIndex() {
        burger.ingredients.add(sauceMock);
        burger.ingredients.add(fillingMock);

        burger.removeIngredient(0);

        assertEquals(1, burger.ingredients.size());
        assertEquals(fillingMock, burger.ingredients.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredientWithInvalidIndexShouldThrowException() {
        burger.ingredients.add(sauceMock);
        burger.removeIngredient(1);
    }

    @Test
    public void testMoveIngredientShouldChangePosition() {
        burger.ingredients.add(sauceMock);
        burger.ingredients.add(fillingMock);
        burger.ingredients.add(additionalSauceMock);

        burger.moveIngredient(0, 2);

        List<Ingredient> expectedOrder = Arrays.asList(fillingMock, additionalSauceMock, sauceMock);
        assertEquals(expectedOrder, burger.ingredients);
    }

    @Test
    public void testGetPriceShouldCalculateCorrectly() {
        when(bunMock.getPrice()).thenReturn(100f);
        when(sauceMock.getPrice()).thenReturn(50f);
        when(fillingMock.getPrice()).thenReturn(80f);

        burger.setBuns(bunMock);
        burger.addIngredient(sauceMock);
        burger.addIngredient(fillingMock);

        float expectedPrice = 2 * 100f + 50f + 80f;
        float actualPrice = burger.getPrice();

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    @Test
    public void testGetPriceWithOnlyBun() {
        when(bunMock.getPrice()).thenReturn(150f);
        burger.setBuns(bunMock);

        float expectedPrice = 2 * 150f;
        float actualPrice = burger.getPrice();

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    @Test
    public void testGetPriceWithMultipleIngredients() {
        when(bunMock.getPrice()).thenReturn(200f);
        when(sauceMock.getPrice()).thenReturn(30f);
        when(fillingMock.getPrice()).thenReturn(70f);
        when(additionalSauceMock.getPrice()).thenReturn(40f);

        burger.setBuns(bunMock);
        burger.addIngredient(sauceMock);
        burger.addIngredient(fillingMock);
        burger.addIngredient(additionalSauceMock);

        float expectedPrice = 2 * 200f + 30f + 70f + 40f;
        assertEquals(expectedPrice, burger.getPrice(), 0.001);
    }

    @Test
    public void testGetReceiptShouldFormatCorrectly() {
        when(bunMock.getName()).thenReturn("black bun");
        when(bunMock.getPrice()).thenReturn(100f);

        when(sauceMock.getType()).thenReturn(IngredientType.SAUCE);
        when(sauceMock.getName()).thenReturn("hot sauce");

        when(fillingMock.getType()).thenReturn(IngredientType.FILLING);
        when(fillingMock.getName()).thenReturn("cutlet");

        burger.setBuns(bunMock);
        burger.addIngredient(sauceMock);
        burger.addIngredient(fillingMock);

        String receipt = burger.getReceipt();

        // Проверяем ключевые части чека более гибко
        assertTrue(receipt.contains("(==== black bun ====)"));
        assertTrue(receipt.contains("sauce hot sauce"));
        assertTrue(receipt.contains("filling cutlet"));
        assertTrue(receipt.contains("Price:"));
    }

    @Test
    public void testReceiptPriceMatchesCalculatedPrice() {
        when(bunMock.getName()).thenReturn("white bun");
        when(bunMock.getPrice()).thenReturn(200f);

        when(fillingMock.getType()).thenReturn(IngredientType.FILLING);
        when(fillingMock.getName()).thenReturn("dinosaur");
        when(fillingMock.getPrice()).thenReturn(200f);

        burger.setBuns(bunMock);
        burger.addIngredient(fillingMock);

        float calculatedPrice = burger.getPrice();
        String receipt = burger.getReceipt();

        // Проверяем, что цена в чеке содержит расчетную цену
        assertTrue(receipt.contains(String.format("Price: %f", calculatedPrice)));
    }

    @Test
    public void testGetReceiptWithNoIngredients() {
        when(bunMock.getName()).thenReturn("red bun");
        when(bunMock.getPrice()).thenReturn(300f);

        burger.setBuns(bunMock);

        String receipt = burger.getReceipt();

        // Проверяем наличие ключевых элементов
        assertTrue(receipt.contains("red bun"));
        assertTrue(receipt.contains("Price:"));

        // Подсчитываем сколько раз встречается название булочки
        // В чеке булочка должна быть дважды: в начале и в конце
        int bunCount = 0;
        String[] lines = receipt.split(System.lineSeparator());
        for (String line : lines) {
            if (line.contains("red bun")) {
                bunCount++;
            }
        }
        assertTrue("Булочка должна встречаться в чеке минимум 2 раза", bunCount >= 2);
    }

    @Test
    public void testIngredientOrderInReceiptMatchesListOrder() {
        when(bunMock.getName()).thenReturn("black bun");
        when(bunMock.getPrice()).thenReturn(100f);

        when(sauceMock.getType()).thenReturn(IngredientType.SAUCE);
        when(sauceMock.getName()).thenReturn("chili sauce");

        when(fillingMock.getType()).thenReturn(IngredientType.FILLING);
        when(fillingMock.getName()).thenReturn("sausage");

        when(additionalSauceMock.getType()).thenReturn(IngredientType.SAUCE);
        when(additionalSauceMock.getName()).thenReturn("sour cream");

        burger.setBuns(bunMock);
        burger.addIngredient(sauceMock);
        burger.addIngredient(fillingMock);
        burger.addIngredient(additionalSauceMock);

        String receipt = burger.getReceipt();

        // Проверяем порядок ингредиентов в чеке
        int sauceIndex = receipt.indexOf("chili sauce");
        int fillingIndex = receipt.indexOf("sausage");
        int additionalSauceIndex = receipt.indexOf("sour cream");

        assertTrue("Первый соус должен быть раньше начинки", sauceIndex < fillingIndex);
        assertTrue("Начинка должна быть раньше второго соуса", fillingIndex < additionalSauceIndex);
    }

    @Test
    public void testMoveIngredientAffectsReceiptOrder() {
        when(bunMock.getName()).thenReturn("white bun");

        when(sauceMock.getType()).thenReturn(IngredientType.SAUCE);
        when(sauceMock.getName()).thenReturn("hot sauce");

        when(fillingMock.getType()).thenReturn(IngredientType.FILLING);
        when(fillingMock.getName()).thenReturn("cutlet");

        burger.setBuns(bunMock);
        burger.addIngredient(sauceMock);
        burger.addIngredient(fillingMock);

        String receiptBefore = burger.getReceipt();

        // Перемещаем начинку на первое место
        burger.moveIngredient(1, 0);

        String receiptAfter = burger.getReceipt();

        // Проверяем, что порядок изменился
        int sauceIndexBefore = receiptBefore.indexOf("hot sauce");
        int fillingIndexBefore = receiptBefore.indexOf("cutlet");
        int sauceIndexAfter = receiptAfter.indexOf("hot sauce");
        int fillingIndexAfter = receiptAfter.indexOf("cutlet");

        assertTrue("До перемещения соус должен быть раньше начинки",
                sauceIndexBefore < fillingIndexBefore);
        assertTrue("После перемещения начинка должна быть раньше соуса",
                fillingIndexAfter < sauceIndexAfter);
    }
}