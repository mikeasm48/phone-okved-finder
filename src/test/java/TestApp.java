import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;

/**
 * Тестовый класс для отладки функциональности.
 * не является UNIT тестом!
 */
public class TestApp {

    public static void main(String[] args) throws Exception {
        System.out.println("🧪 Тестирование Phone OKVED Finder\n");
        testPhoneNormalizer();
        testJsonParsing();
        testIntegration();
    }

    private static void testPhoneNormalizer() {
        System.out.println("1. Тест нормализации номеров:");

        PhoneNormalizer normalizer = new PhoneNormalizer();
        String[] testPhones = {
                "+7 (912) 345-67-89",
                "8(912)3456789",
                "9123456789",
                "+79123456789",
                "7 912 345 67 89"
        };

        for (String phone : testPhones) {
            try {
                String normalized = normalizer.normalize(phone);
                System.out.printf("  %-25s → %s%n", phone, normalized);
            } catch (Exception e) {
                System.out.printf("  %-25s → ОШИБКА: %s%n", phone, e.getMessage());
            }
        }
        System.out.println();
    }

    private static void testJsonParsing() {
        System.out.println("2. Тест парсинга JSON:");

        // Пример вложенного JSON
        String testJson = "[{\"code\":\"01\",\"name\":\"Растениеводство\"," +
                "\"items\":[{\"code\":\"01.1\",\"name\":\"Выращивание\"," +
                "\"items\":[{\"code\":\"01.11\",\"name\":\"Зерновые\"}]}]}]";

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<OkvedItem> items = mapper.readValue(
                    testJson,
                    new com.fasterxml.jackson.core.type.TypeReference<List<OkvedItem>>() {}
            );

            System.out.println("  Успешно распаршено корневых элементов: " + items.size());
            int leafCount = items.get(0).collectLeafItems().size();
            System.out.println("  Листовых элементов: " + leafCount);

        } catch (Exception e) {
            System.out.println("  ОШИБКА парсинга: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testIntegration() {
        System.out.println("3. Интеграционный тест:");

        try {
            PhoneNormalizer normalizer = new PhoneNormalizer();
            String phone = "+79123456789";
            String normalized = normalizer.normalize(phone);

            System.out.println("  Тестовый номер: " + normalized);

            // Тестовые данные OKVED
            OkvedItem item1 = new OkvedItem("01.11.11", "Выращивание пшеницы", null);
            OkvedItem item2 = new OkvedItem("02.22.22", "Лесное хозяйство", null);
            OkvedItem item3 = new OkvedItem("03.33", "Рыболовство", null);

            List<OkvedItem> testItems = Arrays.asList(item1, item2, item3);

            OkvedFinder finder = new OkvedFinder();
            var result = finder.findBestMatch(normalized, testItems);

            if (result.isPresent()) {
                System.out.println("  Найдено совпадение: " + result.get().getCode());
            } else {
                System.out.println("  Совпадений не найдено");
            }

        } catch (Exception e) {
            System.out.println("  ОШИБКА: " + e.getMessage());
        }
    }
}