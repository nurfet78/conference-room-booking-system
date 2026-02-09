# RoomBookingApi.CreateBookingRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**roomId** | **Number** | ID переговорной комнаты для бронирования | 
**title** | **String** | Название встречи / мероприятия | 
**organizerEmail** | **String** | Email организатора бронирования | 
**startTime** | **Date** | Время начала бронирования (ISO 8601, UTC). Должно быть в будущем | 
**endTime** | **Date** | Время окончания бронирования (ISO 8601, UTC). Должно быть после startTime | 


