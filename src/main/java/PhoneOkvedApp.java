import java.util.List;
import java.util.Optional;

/**
 * Основной класс для решения задачи нормализации российских мобильных телефонных номеров и поиска по OKVED.
 * Решение придерживается стандарта документации JavaDoc.
 *
 *  @author Mikhail Asmakovets
 *  @version 1.0
 */
public class PhoneOkvedApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String phoneInput = args[0];

        try {
            // 1. Нормализация номера
            PhoneNormalizer normalizer = new PhoneNormalizer();
            String normalizedPhone = normalizer.normalize(phoneInput);

            System.out.println("✅ Номер нормализован: " + normalizedPhone);

            // 2. Загрузка данных OKVED
            OkvedService okvedService = new OkvedService();
            List<OkvedItem> okvedItems = okvedService.loadOkvedData();

            if (okvedItems.isEmpty()) {
                System.out.println("⚠️  Не удалось загрузить данные OKVED, используется резервная стратегия");
                applyBackupStrategy(normalizedPhone);
                return;
            }

            // 3. Поиск совпадения
            OkvedFinder finder = new OkvedFinder();
            Optional<OkvedFinder.MatchResult> result = finder.findBestMatch(normalizedPhone, okvedItems);

            // 4. Вывод результата
            if (result.isPresent()) {
                printSuccessResult(normalizedPhone, result.get());
            } else {
                System.out.println("⚠️  Совпадений не найдено, используется резервная стратегия");
                applyBackupStrategy(normalizedPhone);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ false - " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("💥 Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void printSuccessResult(String normalizedPhone, OkvedFinder.MatchResult result) {
        System.out.println("\n🎯 РЕЗУЛЬТАТ ПОИСКА:");
        System.out.println("========================");
        System.out.printf("Номер:          %s%n", normalizedPhone);
        System.out.printf("Код ОКВЭД:      %s%n", result.getCode());
        System.out.printf("Название:       %s%n", result.getName());
        System.out.printf("Совпадение:     %d цифр%n", result.getMatchLength());

        if (result.getMatchLength() > 0) {
            System.out.printf("Окончание номера:  ...%s%n", result.getPhoneSuffix());
            System.out.printf("Окончание кода:    ...%s%n", result.getOkvedSuffix());
        }
        System.out.println("========================");
    }

    private static void applyBackupStrategy(String normalizedPhone) {
        OkvedFinder finder = new OkvedFinder();
        OkvedFinder.MatchResult backupResult = finder.createBackupMatch(normalizedPhone);

        System.out.println("\n🔄 РЕЗЕРВНЫЙ РЕЗУЛЬТАТ:");
        System.out.println("========================");
        System.out.printf("Номер:          %s%n", normalizedPhone);
        System.out.printf("Код ОКВЭД:      %s%n", backupResult.getCode());
        System.out.printf("Название:       %s%n", backupResult.getName());
        System.out.println("Длина совпадения: 0 (резервная стратегия)");
        System.out.println("========================");
    }

    private static void printUsage() {
        System.out.println("📱 Phone OKVED Finder");
        System.out.println("========================");
        System.out.println("Использование: java -jar phone-okved-finder.jar <номер_телефона>");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  java -jar phone-okved-finder.jar \"+7 (912) 345-67-89\"");
        System.out.println("  java -jar phone-okved-finder.jar \"8(912)3456789\"");
        System.out.println("  java -jar phone-okved-finder.jar \"9123456789\"");
        System.out.println();
        System.out.println("Формат вывода:");
        System.out.println("  - Нормализованный номер: +79XXXXXXXXX");
        System.out.println("  - Код ОКВЭД с максимальным совпадением");
        System.out.println("  - Длина совпадения цифр");
    }
}