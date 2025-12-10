import java.util.List;
import java.util.Optional;

/**
 * Сервис для поиска кода OKVED по совпадению с номером телефона.
 */
public class OkvedFinder {

    public static class MatchResult {
        private final OkvedItem okvedItem;
        private final int matchLength;
        private final String phoneSuffix;
        private final String okvedSuffix;

        public MatchResult(OkvedItem okvedItem, int matchLength,
                           String phoneSuffix, String okvedSuffix) {
            this.okvedItem = okvedItem;
            this.matchLength = matchLength;
            this.phoneSuffix = phoneSuffix;
            this.okvedSuffix = okvedSuffix;
        }

        public int getMatchLength() { return matchLength; }
        public String getPhoneSuffix() { return phoneSuffix; }
        public String getOkvedSuffix() { return okvedSuffix; }
        public String getCode() { return okvedItem.getCode(); }
        public String getName() { return okvedItem.getName(); }

        @Override
        public String toString() {
            return String.format(
                    "MatchResult{code='%s', matchLength=%d, phoneSuffix='%s', okvedSuffix='%s'}",
                    getCode(), matchLength, phoneSuffix, okvedSuffix
            );
        }
    }

    /**
     * Находит лучший результат совпадения.
     *
     * @param normalizedPhone нормализованый номер телефона
     * @param okvedItems разобранный OKVED
     * @return список результатов поиска
     */
    public Optional<MatchResult> findBestMatch(String normalizedPhone, List<OkvedItem> okvedItems) {
        if (normalizedPhone == null || okvedItems == null || okvedItems.isEmpty()) {
            return Optional.empty();
        }

        // Извлекаем цифры из номера (без +)
        String phoneDigits = normalizedPhone.substring(1);
        MatchResult bestMatch = null;

        for (OkvedItem item : okvedItems) {
            String okvedDigits = item.getDigitsOnly();

            if (okvedDigits.isEmpty()) {
                continue;
            }

            // Ищем максимальное совпадение с конца
            int matchLength = findMaxSuffixMatch(phoneDigits, okvedDigits);

            if (matchLength > 0) {
                String phoneSuffix = phoneDigits.substring(phoneDigits.length() - matchLength);
                String okvedSuffix = okvedDigits.substring(okvedDigits.length() - matchLength);

                MatchResult currentMatch = new MatchResult(
                        item, matchLength, phoneSuffix, okvedSuffix
                );

                // Обновляем лучшее совпадение
                if (bestMatch == null ||
                        matchLength > bestMatch.getMatchLength() ||
                        (matchLength == bestMatch.getMatchLength() &&
                                item.getCode().compareTo(bestMatch.getCode()) > 0)) {
                    bestMatch = currentMatch;
                }
            }
        }

        return Optional.ofNullable(bestMatch);
    }

    /**
     * Резервная стратегия: создает фиктивный OKVED на основе номера.
     *
     * @param normalizedPhone нормализованый номер телефона
     * @return список результатов поиска
     */
    public MatchResult createBackupMatch(String normalizedPhone) {
        PhoneNormalizer normalizer = new PhoneNormalizer();
        String lastDigits = normalizer.extractLastDigits(normalizedPhone, 4);

        String backupCode;
        if (lastDigits.length() >= 2) {
            backupCode = String.format("99.%s%s", lastDigits.charAt(0), lastDigits.charAt(1));
        } else {
            backupCode = "99.00";
        }

        OkvedItem backupItem = new OkvedItem(
                backupCode,
                "Деятельность прочая, не включенная в другие группировки (резервный код)",
                null
        );

        return new MatchResult(backupItem, 0, "", "");
    }

    // Приватные методы

    private int findMaxSuffixMatch(String phoneDigits, String okvedDigits) {
        int maxPossible = Math.min(phoneDigits.length(), okvedDigits.length());

        for (int length = maxPossible; length >= 1; length--) {
            String phoneSuffix = phoneDigits.substring(phoneDigits.length() - length);
            String okvedSuffix = okvedDigits.substring(okvedDigits.length() - length);

            if (phoneSuffix.equals(okvedSuffix)) {
                return length;
            }
        }

        return 0;
    }
}