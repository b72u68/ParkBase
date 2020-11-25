/* TODO:
    - Write documentation
    - Add constraints to attributes
 */

create schema if not exists parking;

/* For debugging relation: To drop table, remove '--' and run the command */

revoke all privileges on parking.reservation from r_user, member, staff, admin;
revoke all privileges on parking.update_form from r_user, member, staff, admin;
revoke all privileges on parking.booking from r_user, member, staff, admin;
revoke all privileges on parking.member from admin;
revoke all privileges on parking.temporary_license_plate from member, admin;
revoke all privileges on parking.user from admin;

revoke all privileges on parking.member_pay from admin;

drop view if exists parking.booking;
drop view if exists parking.mem_pay;
drop view if exists parking.guest_pay;
drop view if exists parking.lot_ratios;
drop view if exists parking.times;

drop owned by r_user;
drop owned by member;
drop owned by staff;
drop owned by admin;

drop role if exists r_user;
drop role if exists member;
drop role if exists staff;
drop role if exists admin;

drop table if exists parking.parking_lot cascade;
drop table if exists parking.spot cascade;
drop table if exists parking.reservation cascade;
drop table if exists parking.temporary_license_plate;
drop table if exists parking.admin;
drop table if exists parking.employee cascade;
drop table if exists parking.update_form;
drop table if exists parking.member cascade;
drop table if exists parking.user cascade;

create table parking.parking_lot (
    lot_id char(1) not null,
    guest_fee numeric(6,2),
    membership_fee numeric(6,2),
    upkeep_cost numeric(6,2),
    location varchar(20),
    primary key (lot_id)
);

create table parking.spot (
    spot_id int not null,
    lot_id char(1) not null,
    primary key (spot_id, lot_id),
    foreign key (lot_id) references parking.parking_lot (lot_id) on delete cascade on update cascade
);

create table parking.user (
    user_id char(7) not null,
    name varchar(30) not null,
    password varchar(20) not null,
    login_time timestamp,
    logout_time timestamp,
    primary key (user_id)
);

create table parking.member (
    user_id char(7) not null,
    registered_license_plate char(7),
    lot_id char(1),
    spot_id int,
    primary key (user_id),
    foreign key (user_id) references parking.user (user_id) on delete cascade,
    foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id) on delete cascade on update cascade
);

create table parking.employee (
    employee_id char(7) not null,
    name varchar(30) not null,
    password varchar(20) not null,
    salary numeric(8,2),
    type varchar(10) not null check (type in ('admin', 'staff')),
    primary key (employee_id)
);

create table parking.admin (
	admin_id char(7) not null,
	name varchar(30) not null,
	password varchar(20) not null
);

/* TODO: Add remain_time() attribute */
create table parking.temporary_license_plate (
    user_id char(7) not null,
    plate_number char(7),
    time_created timestamp,
    primary key (user_id, plate_number),
    foreign key (user_id) references parking.member (user_id)
);

create table parking.update_form (
    user_id char(7) not null,
    time_made timestamp,
    field_to_update varchar(20),
    new_value varchar(30),
    primary key (user_id, time_made),
    foreign key (user_id) references parking.user (user_id) on delete cascade
);

create table parking.reservation (
    user_id char(7) not null,
    time_created timestamp,
    reservation_time_in timestamp,
    reservation_time_out timestamp,
    license_plate char(7),
    application_type varchar(10) check (application_type in ('online', 'drive in', 'member')),
    employee_id char(7) not null,
    lot_id char(1) not null,
    spot_id int not null,
    primary key (user_id, time_created),
    foreign key (user_id) references parking.user (user_id) on delete cascade,
    foreign key (employee_id) references parking.employee (employee_id) on delete cascade,
    foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id) on delete cascade on update cascade
);

/*
    view for checking what spots are available at what time
*/
create view parking.booking as
	select reservation_time_in, reservation_time_out, spot_id, lot_id
	from parking.reservation;

-- for showing table of available spots on listed days
--create view parking.reserved_days as
--	select date_part('year', reservation_time_in) as start_year, date_part('month', reservation_time_in) as start_month, date_part('day', reservation_time_in) as start_day,
--	date_part('year', reservation_time_out) as end_year, date_part('month', reservation_time_out) as end_month, date_part('day', reservation_time_out) as end_day
--	from parking.reservation;
--drop view parking.reserved_days;
--    the following 4 tables are for running a report
--   member_pay and guest_pay will be combined to check the total revenue

create view parking.member_pay as
	select lot_id, start_month, end_month, mem_count*membership_fee-upkeep_cost as mem_profit		
	from (
		select *
		from (
			select lot_id, date_part('month', reservation_time_in) as start_month, date_part('month', reservation_time_out) as end_month, count(distinct (user_id, time_created)) as mem_count
			from parking.reservation where application_type = 'member' group by lot_id, date_part('month', reservation_time_in), date_part('month', reservation_time_out)
		) as foo natural join parking.parking_lot
	) as bar;

create view parking.guest_pay as
	select lot_id, start_month, end_month, guest_count*guest_fee-upkeep_cost as guest_profit			
	from (
		select *
		from (
			select lot_id, date_part('month', reservation_time_in) as start_month, date_part('month', reservation_time_out) as end_month, count(distinct (user_id, time_created)) as guest_count
			from parking.reservation where application_type != 'member' group by lot_id, date_part('month', reservation_time_in), date_part('month', reservation_time_out)
		) as foo natural join parking.parking_lot
	) as bar;

create view parking.lot_ratios as (
	with total_lot_spots (lot_id, num_spot) as (
		select lot_id, count(spot_id)
		from parking.spot group by lot_id
	),
	total_members (lot_id, num_mem) as (
		select lot_id, count(user_id)
		from parking.member group by lot_id
	),
	total_online (lot_id, num_onl) as (
		select lot_id, count(distinct (user_id, time_created))
		from parking.reservation where application_type = 'online' group by lot_id
	),
	total_drive_in (lot_id, num_dv) as (
		select lot_id, count(distinct(user_id, time_created))
		from parking.reservation where application_type = 'drive in' group by lot_id
	)
	select lot_id, num_mem, num_onl, num_dv, num_spot
	from total_lot_spots natural join total_members natural join total_online natural join total_drive_in
);

create view parking.times as select user_id, login_time, logout_time from parking.user;
--end report views 

create role r_user;
create role member;
create role staff;
create role admin;

/*
    allow everyone to create an update_form and a reservation or view the bookings
*/
grant insert on parking.update_form, parking.reservation to r_user, member, staff, admin;
grant select on parking.booking to r_user, member, staff, admin;

/*
    allow member to create temporary_license_plate
*/
grant insert on parking.temporary_license_plate to member;

/*
    allow staff members to view, delete, and update update_forms in addition to
    their permission to insert one earlier
*/
grant select, delete, update on parking.update_form to staff, admin;

/*
    allow only admins to edit profile information
*/
grant select, delete, insert, update on parking.user, parking.member, parking.temporary_license_plate to admin;

/*
    allow only admins to view the report views
*/
grant all on parking.booking, parking.member_pay, parking.guest_pay, parking.lot_ratios, parking.times to admin;
