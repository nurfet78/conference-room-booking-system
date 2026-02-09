# RoomBookingApi.ErrorResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**timeStamp** | **Date** | Временная метка возникновении ошибки (ISO 8601 UTC) | 
**status** | **Number** | HTTP-код статуса | 
**error** | **String** | Краткое описание типа ошибки | [optional] 
**errorCode** | **String** | Машиночитаемый код ошибки для обработке на клиенте | [optional] 
**message** | **String** | Человекочитаемое сообщение об ошибке | 
**path** | **String** | URI запроса, вызывающего ошибку | [optional] 
**fieldErrors** | [**[FieldError]**](FieldError.md) | Список ошибок валидации полей (только для 400 Validation failed) | [optional] 
**details** | **{String: Object}** | Дополнительные детали ошибки (зависят от контекста) | [optional] 


