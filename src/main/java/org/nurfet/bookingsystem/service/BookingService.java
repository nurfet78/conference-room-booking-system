package org.nurfet.bookingsystem.service;

import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.request.UpdateBookingRequest;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.Booking;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.entity.Room;
import org.nurfet.bookingsystem.exception.BookingConflictException;
import org.nurfet.bookingsystem.exception.EntityNotFoundException;
import org.nurfet.bookingsystem.exception.InvalidBookingStateException;
import org.nurfet.bookingsystem.exception.RoomNotAvailableException;
import org.nurfet.bookingsystem.mapper.booking.BookingMapper;
import org.nurfet.bookingsystem.repository.BookingRepository;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingMapper bookingMapper;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.bookingMapper = bookingMapper;
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking", id));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking for room {} from {} to {}",
                request.roomId(), request.startTime(), request.endTime());

        Room room = roomRepository.findByIdWithLock(request.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Room", request.roomId()));

        if (!room.isActive()) {
            throw new RoomNotAvailableException(room.getId(), "Room is not active");
        }

        boolean hasConflict = bookingRepository.existsOverlappingBooking(
                room.getId(),
                request.startTime(),
                request.endTime()
        );

        if (hasConflict) {
            log.info("Booking conflict detected for room {}", room.getId());
            throw new BookingConflictException(
                    room.getId(),
                    request.startTime(),
                    request.endTime()
            );
        }

        Booking booking = new Booking(
                room,
                request.title(),
                request.organizerEmail(),
                request.startTime(),
                request.endTime()
        );

        Booking saved = bookingRepository.save(booking);

        log.info("Booking create with id: {}", saved.getId());
        return bookingMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        Booking booking = findBookingById(id);
        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingByRoomAndTimeRange(
            Long roomId, Instant from, Instant to) {

        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        List<Booking> bookings = bookingRepository.findByRoomAndTimeRange(
                roomId, from, to);

        return bookingMapper.toResponseList(bookings);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getActiveBookingsByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        List<Booking> bookings = bookingRepository.findActiveBookingsByRoom(roomId, Instant.now());

        return bookingMapper.toResponseList(bookings);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByOrganizerEmail(String email) {
        List<Booking> bookings = bookingRepository.findByOrganizerEmail(email);

        return bookingMapper.toResponseList(bookings);
    }

    @Transactional
    public BookingResponse confirmBooking(Long id) {
        log.info("Confirming booking: {}", id);

        Booking booking = findBookingById(id);

        try {
            booking.confirm();
        } catch (IllegalStateException e) {
            throw new InvalidBookingStateException(e.getMessage());
        }

        Booking saving = bookingRepository.save(booking);
        log.info("Booking confirmed: {}", id);

        return bookingMapper.toResponse(saving);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        log.info("Cancelling booking: {}", id);

        Booking booking = findBookingById(id);

        try {
            booking.cancel();
        } catch (IllegalStateException e) {
            throw new InvalidBookingStateException(e.getMessage());
        }

        Booking saved = bookingRepository.save(booking);
        log.info("Booking cancelled: {}", id);

        return bookingMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(Long roomId, Instant startTime, Instant endTime) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        return !bookingRepository.existsOverlappingBooking(roomId, startTime, endTime);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findConflictingBooking(
            Long roomId, Instant startTime, Instant endTime) {

        List<Booking> bookings = bookingRepository.findOverlappingBookings(
                roomId, startTime, endTime);

        return bookingMapper.toResponseList(bookings);
    }

    @Transactional
    public int markExpiredBookings() {
        int count = bookingRepository.markExpiredBookings(Instant.now());

        if (count > 0) {
            log.info("Marked {} bookings as expired", count);
        }

        return count;
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingByStatus(BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByStatus(status);
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional(readOnly = true)
    public long countActiveBookings(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        return bookingRepository.countActiveBookingsRoom(roomId, Instant.now());
    }

    @Transactional
    public BookingResponse updateBooking(Long id, UpdateBookingRequest request) {
        Booking booking = bookingRepository.findByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking", id));

        if (!booking.getStatus().isActive()) {
            throw new InvalidBookingStateException("Cannot update inactive booking");
        }

        Long roomId = request.roomId() != null ? request.roomId() : booking.getRoom().getId();
        Instant startTime = request.startTime() != null ? request.startTime() : booking.getStartTime();
        Instant endTime = request.endTime() != null ? request.endTime() : booking.getEndTime();

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Room room = booking.getRoom();
        if (request.roomId() != null && !request.roomId().equals(booking.getRoom().getId())) {
            room = roomRepository.findByIdWithLock(request.roomId())
                    .orElseThrow(() -> new EntityNotFoundException("Room", request.roomId()));

            if (!room.isActive()) {
                throw new RoomNotAvailableException(room.getId(), "Room is not active");
            }
        }

        boolean timeOrRoomChanged = request.roomId() != null ||
                request.startTime() != null ||
                request.endTime() != null;

        if (timeOrRoomChanged) {
            boolean hasConflict = bookingRepository.existsOverlappingBooking(
                    roomId, startTime, endTime, id);

            if (hasConflict) {
                throw new BookingConflictException(roomId, startTime, endTime);
            }
        }

        if (request.roomId() != null) {
            booking.changeRoom(room);
        }
        if (request.title() != null) {
            booking.setTitle(request.title());
        }
        if (request.startTime() != null || request.endTime() != null) {
            booking.setTimeInterval(startTime, endTime);
        }

        return bookingMapper.toResponse(booking);
    }
}
