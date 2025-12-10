import java.util.regex.Pattern;

/**
 * Сервис для нормализации российских мобильных номеров.
 */
public class PhoneNormalizer {

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[\\s\\-()]*[78]?[\\s\\-()]*9\\d{2}[\\s\\-()]*\\d{3}[\\s\\-()]*\\d{2}[\\s\\-()]*\\d{2}$"
    );

    private static final Pattern DIGITS_PATTERN = Pattern.compile("[^0-9]");

    /**
     * Нормализует российский мобильный номер.
     *
     * @param input номер в любом формате
     * @return номер в формате +79XXXXXXXXX
     * @throws IllegalArgumentException если номер не может быть нормализован
     */
    public String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Номер телефона не может быть пустым");
        }

        String cleaned = input.trim();

        // Быстрая проверка по паттерну
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Номер не соответствует формату российского мобильного");
        }

        // Извлекаем все цифры
        String digits = DIGITS_PATTERN.matcher(cleaned).replaceAll("");

        // Определяем длину и корректируем
        if (digits.length() == 10) {
            // Номер без кода страны: добавляем 7
            digits = "7" + digits;
        } else if (digits.length() == 11) {
            // Номер с кодом страны: заменяем 8 на 7
            if (digits.startsWith("8")) {
                digits = "7" + digits.substring(1);
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("Номер должен содержать 10 или 11 цифр, получено: %d", digits.length())
            );
        }

        // Проверяем, что номер начинается с 79
        if (!digits.startsWith("79")) {
            throw new IllegalArgumentException("Российские мобильные номера должны начинаться с 79");
        }

        return "+" + digits;
    }

    /**
     * Извлекает последние N цифр из нормализованного номера.
     *
     * @param normalizedPhone нормализованый номер телефона
     * @param count количество извлекаемых цифр
     * @return извлеченные из номера цифры
     */
    public String extractLastDigits(String normalizedPhone, int count) {
        if (normalizedPhone == null || count <= 0) {
            return "";
        }

        String digits = normalizedPhone.replace("+", "");
        if (digits.length() <= count) {
            return digits;
        }

        return digits.substring(digits.length() - count);
    }
}
