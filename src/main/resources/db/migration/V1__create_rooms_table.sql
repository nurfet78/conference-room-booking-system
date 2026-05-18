create extension if not exists btree_gist;

create table rooms (
    id bigserial primary key,
    name varchar(100) not null unique,
    capacity integer not null check ( capacity > 0 ),
    description text,
    is_active boolean not null default true,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now()
);

comment on table rooms is 'Conference room for booking'