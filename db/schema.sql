create table if not exists collectivity (
    id uuid primary key,
    location text not null,
    federation_approval boolean not null
);

create table if not exists member (
    id uuid primary key,
    first_name text,
    last_name text,
    birth_date date,
    gender text,
    address text,
    profession text,
    phone_number varchar(50),
    email text,
    occupation text,
    collectivity_id uuid references collectivity(id)
);

create table if not exists member_referee (
    member_id uuid not null references member(id),
    referee_id uuid not null references member(id),
    primary key (member_id, referee_id)
);

create table if not exists collectivity_member (
    collectivity_id uuid not null references collectivity(id),
    member_id uuid not null references member(id),
    primary key (collectivity_id, member_id)
);

create table if not exists collectivity_structure (
    collectivity_id uuid primary key references collectivity(id),
    president_member_id uuid not null references member(id),
    vice_president_member_id uuid not null references member(id),
    treasurer_member_id uuid not null references member(id),
    secretary_member_id uuid not null references member(id)
);
