import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Модель элемента OKVED с поддержкой вложенности.
 * Использует аннотации Jackson для маппинга JSON.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OkvedItem {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("items")
    private List<OkvedItem> items;

    // Конструктор по умолчанию для Jackson
    public OkvedItem() {
        this.items = new ArrayList<>();
    }

    @JsonCreator
    public OkvedItem(
            @JsonProperty("code") String code,
            @JsonProperty("name") String name,
            @JsonProperty("items") List<OkvedItem> items) {
        this.code = code;
        this.name = name;
        this.items = items != null ? items : new ArrayList<>();
    }

    // Геттеры и сеттеры
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OkvedItem> getItems() {
        return items;
    }

    public void setItems(List<OkvedItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    /**
     * Проверяет, является ли элемент листовым.
     */
    public boolean isLeaf() {
        return items == null || items.isEmpty();
    }

    /**
     * Проверяет, содержит ли код цифры.
     */
    public boolean hasNumericCode() {
        return code != null && code.matches(".*\\d.*");
    }

    /**
     * Извлекает только цифры из кода.
     */
    public String getDigitsOnly() {
        if (code == null) {
            return "";
        }
        return code.replaceAll("[^0-9]", "");
    }

    /**
     * Собирает все листовые элементы рекурсивно.
     */
    public List<OkvedItem> collectLeafItems() {
        List<OkvedItem> leafItems = new ArrayList<>();
        collectLeafItemsRecursive(this, leafItems);
        return leafItems;
    }

    /**
     * Собирает все элементы с цифровыми кодами.
     */
    public List<OkvedItem> collectNumericLeafItems() {
        List<OkvedItem> allLeaves = collectLeafItems();
        List<OkvedItem> numericLeaves = new ArrayList<>();

        for (OkvedItem leaf : allLeaves) {
            if (leaf.hasNumericCode()) {
                numericLeaves.add(leaf);
            }
        }

        return numericLeaves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OkvedItem okvedItem = (OkvedItem) o;
        return Objects.equals(code, okvedItem.code) &&
                Objects.equals(name, okvedItem.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }

    @Override
    public String toString() {
        return String.format("OkvedItem{code='%s', name='%s', leaf=%s}",
                code, name, isLeaf());
    }

    // Приватные методы

    private void collectLeafItemsRecursive(OkvedItem item, List<OkvedItem> leafItems) {
        if (item.isLeaf()) {
            leafItems.add(item);
        } else {
            for (OkvedItem child : item.getItems()) {
                collectLeafItemsRecursive(child, leafItems);
            }
        }
    }
}
