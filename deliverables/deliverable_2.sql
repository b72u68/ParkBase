/* TODO:
    - Write documentation
    - Add constraints to attributes
    - Add privileges/roles (staff, administrator)
 */

create schema parking;

/* For debugging relation: To drop table, remove '--' and run the command */

--drop table if exists parking.parking_lot cascade;
--drop table if exists parking.spot cascade;
--drop table if exists parking.reservation;
--drop table if exists parking.temporary_license_plate;
--drop table if exists parking.employee cascade;
--drop table if exists parking.update_form;
--drop table if exists parking.member cascade;
--drop table if exists parking.user cascade;

create table parking.user (
    user_id varchar(20),
    name varchar(30),
    password varchar(20),
    login_time timestamp,
    logout_time timestamp,
    primary key (user_id)
);

create table parking.member (
    user_id varchar(20),
    registered_license_plate char(7),
    lot_id char(1),
    spot_id int,
    primary key (user_id),
    foreign key (user_id) references parking.user (user_id)
);

create table parking.employee (
    employee_id varchar(20),
    name varchar(30),
    salary numeric(8,2),
    type varchar(10),
    primary key (employee_id)
);

/* TODO: Add remain_time() attribute */
create table parking.temporary_license_plate (
    user_id varchar(20),
    plate_number char(7),
    time_created timestamp,
    primary key (user_id, plate_number),
    foreign key (user_id) references parking.member (user_id)
);

create table parking.update_form (
    user_id varchar(20),
    time_made timestamp,
    field_to_update varchar(20),
    new_value varchar(30),
    primary key (user_id, time_made),
    foreign key (user_id) references parking.user (user_id)
);

create table parking.parking_lot (
    lot_id char(1),
    guest_fee numeric(6,2),
    membership_fee numeric(6,2),
    upkeep_cost numeric(6,2),
    location varchar(20),
    primary key (lot_id)
);

create table parking.spot (
    spot_id int,
    lot_id char(1),
    primary key (spot_id, lot_id),
    foreign key (lot_id) references parking.parking_lot (lot_id)
);

create table parking.reservation (
    user_id varchar(20),
    time_created timestamp,
    reservation_time_in timestamp,
    reservation_time_out timestamp,
    license_plate char(7),
    application_type varchar(10),
    temporary bit,
    employee_id varchar(20),
    lot_id char(1),
    spot_id int,
    primary key (user_id, time_created),
    foreign key (user_id) references parking.user (user_id),
    foreign key (employee_id) references parking.employee (employee_id),
    foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id)
);

/* Add foreign key spot (lot_id, spot_id) to member */
alter table parking.member
add foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id);
