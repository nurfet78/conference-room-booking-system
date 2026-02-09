# RoomBookingApi.BookingResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **Number** | Уникальный ID бронирования | [optional] [readonly] 
**roomId** | **Number** | ID забронированной комнаты | [optional] 
**roomName** | **String** | Название комнаты | [optional] 
**title** | **String** | Название встречи | [optional] 
**organizerEmail** | **String** | Email организатора | [optional] 
**startTime** | **Date** | Время начала | [optional] 
**endTime** | **Date** | Время окончания | [optional] 
**durationMinutes** | **Number** | Продолжительность в минутах (вычисляется автоматически) | [optional] [readonly] 
**status** | [**BookingStatus**](BookingStatus.md) | Текущий статус бронирования | [optional] 
**createdAt** | **Date** | Дата создания | [optional] [readonly] 
**updatedAt** | **Date** | Дата последнего обновления | [optional] [readonly] 


