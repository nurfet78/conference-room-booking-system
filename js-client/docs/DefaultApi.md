# RoomBookingApi.DefaultApi

All URIs are relative to *http://localhost:8888*

Method | HTTP request | Description
------------- | ------------- | -------------
[**cancelBooking**](DefaultApi.md#cancelBooking) | **POST** /api/v1/bookings/{id}/cancel | Отменить бронирование
[**checkAvailable**](DefaultApi.md#checkAvailable) | **GET** /api/v1/bookings/availability | Проверка доступности временного слота
[**checkAvailable_0**](DefaultApi.md#checkAvailable_0) | **GET** /api/v1/bookings/availability | Проверка доступности временного слота
[**confirmBooking**](DefaultApi.md#confirmBooking) | **POST** /api/v1/bookings/{id}/confirm | Подтвердить бронирование
[**countActiveBookings**](DefaultApi.md#countActiveBookings) | **GET** /api/v1/bookings/room/{roomId}/count | Количество активных бронирований комнаты
[**createBooking**](DefaultApi.md#createBooking) | **POST** /api/v1/bookings | Создать бронирование
[**createRoom**](DefaultApi.md#createRoom) | **POST** /api/v1/rooms | Создать переговорную комнату
[**deactivateRoom**](DefaultApi.md#deactivateRoom) | **POST** /api/v1/rooms/{id}/deactivate | Деактивировать комнату
[**getActiveBookingsByRoom**](DefaultApi.md#getActiveBookingsByRoom) | **GET** /api/v1/bookings/room/{roomId}/active | Получить активные бронирования комнаты
[**getAllRooms**](DefaultApi.md#getAllRooms) | **GET** /api/v1/rooms | Получить список комнат
[**getBookingById**](DefaultApi.md#getBookingById) | **GET** /api/v1/bookings/{id} | Получить бронирование по ID
[**getBookingsByOrganizer**](DefaultApi.md#getBookingsByOrganizer) | **GET** /api/v1/bookings/organizer | Найти бронирования по email организатора
[**getBookingsByRoomAndTimeRange**](DefaultApi.md#getBookingsByRoomAndTimeRange) | **GET** /api/v1/bookings/room/{roomId} | Получить бронирования комнаты за период
[**getBookingsByStatus**](DefaultApi.md#getBookingsByStatus) | **GET** /api/v1/bookings/status | Получить бронирования по статусу
[**getRoomById**](DefaultApi.md#getRoomById) | **GET** /api/v1/rooms/{id} | Получить комнату по ID
[**updateBooking**](DefaultApi.md#updateBooking) | **PATCH** /api/v1/bookings/{id} | Обновить бронирование (частично)
[**updateRoom**](DefaultApi.md#updateRoom) | **PATCH** /api/v1/rooms/{id} | Обновить комнату (частично)



## cancelBooking

> BookingResponse cancelBooking(id)

Отменить бронирование

Переводит бронирование в статус CANCELLED. Отменить можно только PENDING или CONFIRMED бронирования

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let id = 789; // Number | 
apiInstance.cancelBooking(id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 

### Return type

[**BookingResponse**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## checkAvailable

> AvailabilityResponse checkAvailable(roomId, startTime, endTime)

Проверка доступности временного слота

Возвращает информацию о доступности временного слота для указанной комнаты.

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let roomId = 1; // Number | ID комнаты
let startTime = new Date("2025-07-01T09:00:00Z"); // Date | Время начала слота (ISO 8601, UTC)
let endTime = new Date("2025-07-01T10:00:00Z"); // Date | Время окончания слота (ISO 8601, UTC)
apiInstance.checkAvailable(roomId, startTime, endTime, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roomId** | **Number**| ID комнаты | 
 **startTime** | **Date**| Время начала слота (ISO 8601, UTC) | 
 **endTime** | **Date**| Время окончания слота (ISO 8601, UTC) | 

### Return type

[**AvailabilityResponse**](AvailabilityResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## checkAvailable_0

> AvailabilityResponse checkAvailable_0(roomId, startTime, endTime)

Проверка доступности временного слота

Возвращает информацию о доступности временного слота для указанной комнаты.

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let roomId = 1; // Number | ID комнаты
let startTime = new Date("2025-07-01T09:00:00Z"); // Date | Время начала слота (ISO 8601, UTC)
let endTime = new Date("2025-07-01T10:00:00Z"); // Date | Время окончания слота (ISO 8601, UTC)
apiInstance.checkAvailable_0(roomId, startTime, endTime, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roomId** | **Number**| ID комнаты | 
 **startTime** | **Date**| Время начала слота (ISO 8601, UTC) | 
 **endTime** | **Date**| Время окончания слота (ISO 8601, UTC) | 

### Return type

[**AvailabilityResponse**](AvailabilityResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## confirmBooking

> BookingResponse confirmBooking(id)

Подтвердить бронирование

Переводит бронирование из статуса PENDING в CONFIRMED. Только бронирования в статусе PENDING могут быть подтверждены

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let id = 789; // Number | 
apiInstance.confirmBooking(id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 

### Return type

[**BookingResponse**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## countActiveBookings

> {String: Number} countActiveBookings(roomId)

Количество активных бронирований комнаты

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let roomId = 1; // Number | ID комнаты
apiInstance.countActiveBookings(roomId, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roomId** | **Number**| ID комнаты | 

### Return type

**{String: Number}**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## createBooking

> BookingResponse createBooking(createBookingRequest)

Создать бронирование

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let createBookingRequest = new RoomBookingApi.CreateBookingRequest(); // CreateBookingRequest | 
apiInstance.createBooking(createBookingRequest, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createBookingRequest** | [**CreateBookingRequest**](CreateBookingRequest.md)|  | 

### Return type

[**BookingResponse**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## createRoom

> RoomResponse createRoom(createRoomRequest)

Создать переговорную комнату

Создаёт новую переговорную комнату с указанным названием, вместимостью и описанием. Комната создаётся в активном состоянии (active &#x3D; true). 

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let createRoomRequest = new RoomBookingApi.CreateRoomRequest(); // CreateRoomRequest | 
apiInstance.createRoom(createRoomRequest, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **createRoomRequest** | [**CreateRoomRequest**](CreateRoomRequest.md)|  | 

### Return type

[**RoomResponse**](RoomResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## deactivateRoom

> RoomResponse deactivateRoom(id)

Деактивировать комнату

Помечает комнату как неактивную (active &#x3D; false). Деактивированная комната не может быть забронирована. Существующие бронирования **не** отменяются автоматически. 

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let id = 789; // Number | 
apiInstance.deactivateRoom(id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 

### Return type

[**RoomResponse**](RoomResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getActiveBookingsByRoom

> [BookingResponse] getActiveBookingsByRoom(roomId)

Получить активные бронирования комнаты

Возвращает бронирования со статусом PENDING или CONFIRMED

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let roomId = 789; // Number | 
apiInstance.getActiveBookingsByRoom(roomId, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roomId** | **Number**|  | 

### Return type

[**[BookingResponse]**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getAllRooms

> [RoomResponse] getAllRooms(opts)

Получить список комнат

Возвращает все комнаты или только активные (при activeOnly&#x3D;true)

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let opts = {
  'activeOnly': false // Boolean | Если true — вернуть только активные комнаты
};
apiInstance.getAllRooms(opts, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **activeOnly** | **Boolean**| Если true — вернуть только активные комнаты | [optional] [default to false]

### Return type

[**[RoomResponse]**](RoomResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getBookingById

> BookingResponse getBookingById(id)

Получить бронирование по ID

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let id = 789; // Number | 
apiInstance.getBookingById(id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 

### Return type

[**BookingResponse**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getBookingsByOrganizer

> [BookingResponse] getBookingsByOrganizer(email)

Найти бронирования по email организатора

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let email = "ivan.petrov@example.com"; // String | Email организатора
apiInstance.getBookingsByOrganizer(email, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **email** | **String**| Email организатора | 

### Return type

[**[BookingResponse]**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getBookingsByRoomAndTimeRange

> [BookingResponse] getBookingsByRoomAndTimeRange(roomId, from, to)

Получить бронирования комнаты за период

Возвращает все бронирования указанной комнаты в заданном временном диапазоне

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let roomId = 789; // Number | 
let from = new Date("2025-07-01T00:00:00Z"); // Date | Начало диапазона (ISO 8601, UTC)
let to = new Date("2025-07-31T23:59:59Z"); // Date | Конец диапазона (ISO 8601, UTC)
apiInstance.getBookingsByRoomAndTimeRange(roomId, from, to, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **roomId** | **Number**|  | 
 **from** | **Date**| Начало диапазона (ISO 8601, UTC) | 
 **to** | **Date**| Конец диапазона (ISO 8601, UTC) | 

### Return type

[**[BookingResponse]**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getBookingsByStatus

> [BookingResponse] getBookingsByStatus(status)

Получить бронирования по статусу

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let status = new RoomBookingApi.BookingStatus(); // BookingStatus | Статус бронирования для фильтрации
apiInstance.getBookingsByStatus(status, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **status** | [**BookingStatus**](.md)| Статус бронирования для фильтрации | 

### Return type

[**[BookingResponse]**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## getRoomById

> RoomResponse getRoomById(id)

Получить комнату по ID

Возвращает полную информацию о переговорной комнате по её уникальному идентификатору

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let id = 1; // Number | Уникальный идентификатор комнаты
apiInstance.getRoomById(id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**| Уникальный идентификатор комнаты | 

### Return type

[**RoomResponse**](RoomResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## updateBooking

> BookingResponse updateBooking(id, updateBookingRequest)

Обновить бронирование (частично)

Частичное обновление бронирования. Передайте только поля, которые нужно изменить.  **Ограничения:** - Нельзя обновить CANCELLED или EXPIRED бронирование - При изменении времени проверяется отсутствие конфликтов 

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let id = 789; // Number | 
let updateBookingRequest = new RoomBookingApi.UpdateBookingRequest(); // UpdateBookingRequest | 
apiInstance.updateBooking(id, updateBookingRequest, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 
 **updateBookingRequest** | [**UpdateBookingRequest**](UpdateBookingRequest.md)|  | 

### Return type

[**BookingResponse**](BookingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## updateRoom

> RoomResponse updateRoom(id, updateRoomRequest)

Обновить комнату (частично)

Обновляет только переданные поля комнаты. Поля со значением null игнорируются

### Example

```javascript
import RoomBookingApi from 'room_booking_api';

let apiInstance = new RoomBookingApi.DefaultApi();
let id = 789; // Number | 
let updateRoomRequest = new RoomBookingApi.UpdateRoomRequest(); // UpdateRoomRequest | 
apiInstance.updateRoom(id, updateRoomRequest, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 
 **updateRoomRequest** | [**UpdateRoomRequest**](UpdateRoomRequest.md)|  | 

### Return type

[**RoomResponse**](RoomResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

