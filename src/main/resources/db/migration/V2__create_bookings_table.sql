create table bookings (
    id bigserial primary key,
    room_id bigint not null references rooms(id),
    title varchar(200) not null,
    organizer_email varchar(254) not null,
    start_time timestamp with time zone not null,
    end_time timestamp with time zone not null,
    status varchar(20) not null default 'PENDING'
                      check ( status in ('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED') ),
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),

    constraint chk_booking_time_order check ( end_time > start_time ),

    constraint chk_booking_min_duration check (
        end_time - start_time >= interval '15 minutes'
        ),

    constraint chk_booking_max_duration check (
        end_time - start_time <= interval '8 hours'
        )
);

comment on table bookings is 'Room reservations with time intervals'