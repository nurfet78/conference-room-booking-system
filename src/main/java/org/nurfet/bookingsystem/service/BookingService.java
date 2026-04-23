package org.nurfet.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nurfet.bookingsystem.dto.request.CreateBookingRequest;
import org.nurfet.bookingsystem.dto.request.UpdateBookingRequest;
import org.nurfet.bookingsystem.dto.response.ActiveBookingsCountResponse;
import org.nurfet.bookingsystem.dto.response.AvailabilityResponse;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.entity.Booking;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.entity.Room;
import org.nurfet.bookingsystem.exception.BookingConflictException;
import org.nurfet.bookingsystem.exception.EntityNotFoundException;
import org.nurfet.bookingsystem.exception.InvalidBookingStateException;
import org.nurfet.bookingsystem.exception.RoomNotAvailableException;
import org.nurfet.bookingsystem.mapper.booking.BookingMapper;
import org.nurfet.bookingsystem.mapper.room.RoomMapper;
import org.nurfet.bookingsystem.repository.BookingRepository;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingMapper bookingMapper;
    private final RoomMapper roomMapper;

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking", id));
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking: {}", request.title());

        Room room = roomRepository.findByIdWithLock(request.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Room", request.roomId()));

        if (!room.isActive()) {
            throw new RoomNotAvailableException(request.roomId(), "Room is not active");
        }

        boolean hasConflict = bookingRepository.existsOverlappingBooking(request.roomId(),
                request.startTime(), request.endTime());

        if (hasConflict) {
            log.info("Booking conflict detected with room: {}", room.getId());
            throw new BookingConflictException(room.getId(), request.startTime(), request.endTime());
        }

        Booking booking = new Booking(room,
                request.title(),
                request.organizerEmail(),
                request.startTime(),
                request.endTime());

        Booking saved = bookingRepository.save(booking);
        log.info("Booking with id: {} ,created", saved.getId());

        return bookingMapper.toResponse(saved);
    }

    @Transactional
    public BookingResponse updateBooking(Long id, UpdateBookingRequest request) {
        log.info("Updating booking with id: {}", id);

        Booking booking = bookingRepository.findByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking", id));

        if (!booking.isActive()) {
            throw new InvalidBookingStateException("Cannot update inactive booking");
        }

        Long roomId = request.roomId() != null ? request.roomId() : booking.getRoom().getId();
        Instant startTime = request.startTime() != null ? request.startTime() : booking.getStartTime();
        Instant endTime = request.endTime() != null ? request.endTime() : booking.getEndTime();

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        boolean roomChanged = !roomId.equals(booking.getRoom().getId());
        Room room = booking.getRoom();

        if (roomChanged) {
            room = roomRepository.findByIdWithLock(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("Room", roomId));

            if (!room.isActive()) {
                throw new RoomNotAvailableException(roomId, "Room is not active");
            }
        }

        boolean timeChanged = !startTime.equals(booking.getStartTime()) ||
                              !endTime.equals(booking.getEndTime());

        if (roomChanged || timeChanged) {
            boolean hasConflict = bookingRepository.existsOverlappingBooking(roomId,
                    startTime, endTime, id);

            if (hasConflict) {
                throw new BookingConflictException(roomId, startTime, endTime);
            }
        }

        if (roomChanged) {
            booking.changeRoom(room);
        }

        if (request.title() != null) {
            booking.setTitle(request.title());
        }

        if (timeChanged) {
            booking.setTimeInterval(startTime, endTime);
        }

        log.info("Booking with id: {} updated", booking.getId());

        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        Booking booking = findBookingById(id);
        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingByRoomAndTimeRange(Long roomId, Instant from, Instant to) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        List<Booking> bookings = bookingRepository.findByRoomAndTimeRange(roomId, from, to);
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getActiveBookingsByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        List<Booking> bookings = bookingRepository.findActiveByRoom(roomId, Instant.now());
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional
    public BookingResponse confirmBooking(Long id) {
        log.info("Booking with id: {} confirming", id);
        Booking booking = findBookingById(id);

        try {
            booking.confirm(Instant.now());
        } catch (IllegalStateException e) {
            throw new InvalidBookingStateException(e.getMessage());
        }

        Booking saved = bookingRepository.save(booking);
        log.info("Booking with id: {} confirmed", saved.getId());

        return bookingMapper.toResponse(saved);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        log.info("Booking with id: {} cancelling", id);
        Booking booking = findBookingById(id);

        try {
            booking.cancel();
        } catch (IllegalStateException e) {
            throw new InvalidBookingStateException(e.getMessage());
        }

        Booking saved = bookingRepository.save(booking);
        log.info("Booking with id: {} cancelled", saved.getId());

        return bookingMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findBookingByOrganizerEmail(String email) {
        List<Booking> bookings = bookingRepository.findBookingsByOrganizerEmail(email);
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findBookingByStatus(BookingStatus status) {
        List<Booking> bookings = bookingRepository.findBookingsByStatus(status);
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
    public AvailabilityResponse checkAvailable(Long roomId,
                                               Instant startTime,
                                               Instant endTime) {

        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        if (!bookingRepository.existsOverlappingBooking(roomId, startTime, endTime)) {
            return AvailabilityResponse.free();
        }

        List<Booking> conflicts = bookingRepository.findOverlappingBooking(roomId, startTime, endTime);

        return AvailabilityResponse.unavailable(bookingMapper.toResponseList(conflicts));
    }

    @Transactional(readOnly = true)
    public long countActiveBookingsByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room", roomId);
        }

        return bookingRepository.countActiveByRoom(roomId, Instant.now());
    }
}