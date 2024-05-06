# Мини-банк проект

## Описание проекта
Проект представляет собой телеграм-бота, позволяющий управлять банковскими операциями клиента.

## Структура проекта
### Состоит из трёх компонентов:
- `frontend`: Компонент Telegram-bot. Выступает как клиентское приложение, инициирует запросы пользователей.
- `middle`: Java сервис. Принимает запросы от telegram-бота, выполняет валидацию и бизнес логику, маршрутизирует запросы в "Банк".
- `backend`: Java сервис. Принимает запросы от middle-сервиса. Выступает в качестве АБС (автоматизированная банковская система), обрабатывает транзакции, хранит клиентские данные и т. д.

## Схема взаимодействия компонентов
```plantuml
@startuml

actor User as user
participant "Telegram-bot" as frontend
participant "middle" as middle
participant "backend" as backend

user -> frontend : Взаимодействие с ботом
activate user
activate frontend

frontend -> middle : HTTP запрос
activate middle
    middle -> middle : Валидация
alt Данные валидны
    middle -> backend : HTTP запрос

    activate backend
    backend -> backend : Обработка запроса 
    backend --> middle : Результат
    deactivate backend
    
    middle --> frontend : Результат
else Данные невалидны
    middle --> frontend : Ошибка
    deactivate middle
end
    frontend --> user: Ответ
deactivate frontend
deactivate user
@enduml
```