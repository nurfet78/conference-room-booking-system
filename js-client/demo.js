var RoomBookingApi = require('./dist/index');

var api = new RoomBookingApi.DefaultApi();

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function main() {
    
    console.log('=== ДЕМОНСТРАЦИЯ API КЛИЕНТА ===\n');

    // -----------------------------------------
    // 1. Получить все комнаты
    // -----------------------------------------
    console.log('1. Получаем все комнаты...');
    
    api.getAllRooms({ activeOnly: false }, function(error, data) {
        if (error) {
            console.log('   Ошибка: ' + error.message);
        } else {
            console.log('   Найдено комнат: ' + data.length);
            data.forEach(function(room) {
                console.log('   - ID:' + room.id + ' | ' + room.name + ' | Вместимость: ' + room.capacity);
            });
        }
        console.log('');
    });

    await sleep(1000);

    // -----------------------------------------
    // 2. Создать новую комнату
    // -----------------------------------------
    console.log('2. Создаём новую комнату...');
    
    var newRoom = new RoomBookingApi.CreateRoomRequest();
    newRoom.name = 'Тестовая-' + Date.now();
    newRoom.capacity = 8;
    newRoom.description = 'Создана через JS клиент';

    api.createRoom(newRoom, function(error, data) {
        if (error) {
            console.log('   Ошибка: ' + error.message);
        } else {
            console.log('   Создана комната:');
            console.log('   - ID: ' + data.id);
            console.log('   - Название: ' + data.name);
            global.createdRoomId = data.id;
        }
        console.log('');
    });

    await sleep(1000);

    // -----------------------------------------
    // 3. Получить комнату по ID
    // -----------------------------------------
    console.log('3. Получаем комнату по ID=1...');
    
    api.getRoomById(1, function(error, data) {
        if (error) {
            console.log('   Ошибка: ' + error.message);
        } else {
            console.log('   Комната: ' + data.name);
            console.log('   Описание: ' + data.description);
        }
        console.log('');
    });

    await sleep(1000);

    // -----------------------------------------
    // 4. Создать бронирование
    // -----------------------------------------
    console.log('4. Создаём бронирование...');
    
    var startTime = new Date(Date.now() + 60 * 60 * 1000);
    var endTime = new Date(Date.now() + 2 * 60 * 60 * 1000);

    var newBooking = new RoomBookingApi.CreateBookingRequest();
    newBooking.roomId = 1;
    newBooking.title = 'Тестовое совещание';
    newBooking.organizerEmail = 'test@example.com';
    newBooking.startTime = startTime;
    newBooking.endTime = endTime;

    api.createBooking(newBooking, function(error, data) {
        if (error) {
            console.log('   Ошибка: ' + error.message);
        } else {
            console.log('   Создано бронирование:');
            console.log('   - ID: ' + data.id);
            console.log('   - Комната: ' + data.roomName);
            console.log('   - Статус: ' + data.status);
            global.createdBookingId = data.id;
        }
        console.log('');
    });

    await sleep(1000);

    // -----------------------------------------
    // 5. Подтвердить бронирование
    // -----------------------------------------
    console.log('5. Подтверждаем бронирование...');
    
    if (global.createdBookingId) {
        api.confirmBooking(global.createdBookingId, function(error, data) {
            if (error) {
                console.log('   Ошибка: ' + error.message);
            } else {
                console.log('   Бронирование подтверждено:');
                console.log('   - Статус: ' + data.status);
            }
            console.log('');
        });
    }

    await sleep(1000);

    // -----------------------------------------
    // 6. Отменить бронирование
    // -----------------------------------------
    console.log('6. Отменяем бронирование...');
    
    if (global.createdBookingId) {
        api.cancelBooking(global.createdBookingId, function(error, data) {
            if (error) {
                console.log('   Ошибка: ' + error.message);
            } else {
                console.log('   Бронирование отменено:');
                console.log('   - Статус: ' + data.status);
            }
            console.log('');
        });
    }

    await sleep(1000);

    console.log('=== ДЕМОНСТРАЦИЯ ЗАВЕРШЕНА ===');
}

main();