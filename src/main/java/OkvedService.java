import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для загрузки и обработки данных OKVED.
 */
public class OkvedService {

    private static final String OKVED_URL =
            "https://raw.githubusercontent.com/bergstar/testcase/refs/heads/master/okved.json";

    private final ObjectMapper objectMapper;

    public OkvedService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Загружает и парсит данные OKVED, возвращая только листовые элементы с цифрами.
     */
    public List<OkvedItem> loadOkvedData() {
        try {
            System.out.println("Загрузка данных OKVED...");
            String jsonContent = downloadOkvedJson();

            System.out.println("Парсинг JSON...");
            List<OkvedItem> rootItems = parseOkvedJson(jsonContent);

            System.out.println("Извлечение листовых элементов...");
            List<OkvedItem> leafItems = extractNumericLeafItems(rootItems);

            System.out.printf("Успешно загружено %d листовых OKVED элементов%n", leafItems.size());

            return leafItems;

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке OKVED данных: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Приватные методы

    private String downloadOkvedJson() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(OKVED_URL);

            return httpClient.execute(request, response -> {
                if (response.getCode() == 200) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    throw new IOException("HTTP ошибка: " + response.getCode());
                }
            });
        }
    }

    private List<OkvedItem> parseOkvedJson(String jsonContent) throws IOException {
        return objectMapper.readValue(
                jsonContent,
                new TypeReference<List<OkvedItem>>() {}
        );
    }

    private List<OkvedItem> extractNumericLeafItems(List<OkvedItem> rootItems) {
        List<OkvedItem> allNumericLeaves = new ArrayList<>();

        for (OkvedItem rootItem : rootItems) {
            allNumericLeaves.addAll(rootItem.collectNumericLeafItems());
        }

        return allNumericLeaves;
    }
}
