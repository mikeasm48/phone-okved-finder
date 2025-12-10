# Phone OKVED Finder

Приложение для нормализации российских мобильных номеров и поиска кодов ОКВЭД по совпадению окончаний.
Это решение задачи: https://github.com/bergstar/testcase

## Требования

- Java 11 или выше
- Maven 3.6+

## Сборка и запуск

### Клонировать репозиторий
git clone https://github.com/mikeasm48/phone-okved-finder.git

cd phone-okved-finder

### Собрать проект
mvn clean package

### Запустить приложение
java -jar target/phone-okved-finder-1.0-SNAPSHOT-jar-with-dependencies.jar "+7 (912) 345-67-89"

## Лицензирование

Программное обеспечение использует библиотеку Jackson (Copyright FasterXML), которая доступна под [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).