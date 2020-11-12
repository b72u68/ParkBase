/* TODO:
    - Write documentation
    - Add constraints to attributes

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
    foreign key (user_id) references parking.user (user_id),
    foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id)
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

create view parking.booking as select (reservation_time_in, reservation_time_out, spot_id, lot_id) from parking.reservation;
create view parking.member_pay as select (lot_id, mem_count*membership_fee) from (select (lot_id, count(user_id) as mem_count) from parking.member group by lot_id) natural join parking.parking_lot;
create view parking.guest_pay as select (lot_id, guest_count*guest_fee) from (select (lot_id, count(user_id, time_created) as guest_count) from parking.reservation where temporary = 1) natural join parking.parking_lot;
create view parking.lot_ratios as (with total_lot_spots (lot_id, num_spot) as (select (lot_id, count(spot_id)) from parking.parking_lot group by lot_id),
	with total_members (lot_id, num_mem) as (select (lot_id, count(user_id)) from parking.member group by lot_id),
	with total_online (lot_id, num_onl) as (select (lot_id, count(user_id, time_created)) from parking.reservation where application_type = 'online' group by lot_id),
	with total_drive_in (lot_id, num_dv) as (select (lot_id, count(user_id, time_created)) from parking.reservation where application_type = 'drive-in' group by lot_id)
	select (lot_id, num_mem/num_spot, num_onl/num_spot, num_dv/num_spot) from total_lot_spots natural join total_members natural join total_online natural join total_drive_in);
create view parking.times as select (user_id, login_time, logout_time) from parking.user;
	
create role r_user;
create role staff;
create role admin;
grant insert on parking.update_form, parking.reservation to r_user, staff, admin;
grant select on parking.booking to r_user, admin;
grant select, delete, update on parking.update_form to staff, admin;
grant select, delete, insert, update on parking.user, parking.member, parking.temporary_license_plate to admin;
grant all on parking.booking, parking.member_pay, parking.guest_pay, parking.lot_ratios, parking.times to adming;

