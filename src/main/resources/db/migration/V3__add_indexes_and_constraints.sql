create index idx_room_time on bookings(room_id, start_time, end_time)
where status in('PENDING', 'CONFIRMED');

create index idx_bookings_status on bookings(status);
create index idx_bookings_organizer on bookings(organizer_email);

alter table bookings
add constraint excl_booking_overlap
exclude using gist(
    room_id with =,
    tstzrange(start_time, end_time) with &&
    )
where ( status in('PENDING', 'CONFIRMED') );

comment on constraint excl_booking_overlap on bookings
is 'Prevents overlapping bookings'