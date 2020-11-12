/* TODO:
    - Write documentation
    - Add constraints to attributes
 */

--create schema parking;

/* For debugging relation: To drop table, remove '--' and run the command */

drop role if exists r_user;
drop role if exists staff;
drop role if exists admin;
drop view if exists parking.booking;
drop view if exists parking.mem_pay;
drop view if exists parking.guest_pay;
drop view if exists parking.lot_ratios;
drop view if exists parking.times;
drop table if exists parking.parking_lot cascade;
drop table if exists parking.spot cascade;
drop table if exists parking.reservation;
drop table if exists parking.temporary_license_plate;
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
    user_id varchar(20) not null,
    name varchar(30) not null,
    password varchar(20) not null,
    login_time timestamp,
    logout_time timestamp,
    primary key (user_id)
);

create table parking.member (
    user_id varchar(20) not null,
    registered_license_plate char(7),
    lot_id char(1),
    spot_id int,
    primary key (user_id),
    foreign key (user_id) references parking.user (user_id) on delete cascade,
    foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id) on delete cascade on update cascade
);

create table parking.employee (
    employee_id varchar(20) not null,
    name varchar(30) not null,
    password varchar(20) not null,
    salary numeric(8,2),
    type varchar(10) not null ,
    --check (type in ("staff", "admin")),
    primary key (employee_id)
);

/* TODO: Add remain_time() attribute */
create table parking.temporary_license_plate (
    user_id varchar(20) not null,
    plate_number char(7),
    time_created timestamp,
    primary key (user_id, plate_number),
    foreign key (user_id) references parking.member (user_id)
);

create table parking.update_form (
    user_id varchar(20) not null,
    time_made timestamp,
    field_to_update varchar(20),
    new_value varchar(30),
    primary key (user_id, time_made),
    foreign key (user_id) references parking.user (user_id) on delete cascade
);

create table parking.reservation (
    user_id varchar(20) not null,
    time_created timestamp,
    reservation_time_in timestamp,
    reservation_time_out timestamp,
    license_plate char(7),
    application_type varchar(10),
    temporary bit,
    employee_id varchar(20) not null,
    lot_id char(1) not null,
    spot_id int not null,
    primary key (user_id, time_created),
    foreign key (user_id) references parking.user (user_id) on delete cascade,
    foreign key (employee_id) references parking.employee (employee_id) on delete cascade,
    foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id) on delete cascade on update cascade
);

/* Add foreign key spot (lot_id, spot_id) to member */
alter table parking.member
add foreign key (lot_id, spot_id) references parking.spot (lot_id, spot_id);

--view for checking what spots are available at what time
create view parking.booking as 											
	select reservation_time_in, reservation_time_out, spot_id, lot_id
	from parking.reservation;
	
--the following 4 tables are for running a report
--member_pay and guest_pay will be combined to check the total revenue
create view parking.member_pay as 
	select lot_id, mem_count*membership_fee 
	from (
		select * 
		from (
			select lot_id, count(user_id) as mem_count 
			from parking.member group by lot_id
		) as foo natural join parking.parking_lot
	) as bar;
create view parking.guest_pay as 
	select lot_id, guest_count*guest_fee
	from (
		select *
		from (
			select lot_id, count(distinct (user_id, time_created)) as guest_count 
			from parking.reservation where temporary = '1' group by lot_id
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
		from parking.reservation where application_type = 'drive-in' group by lot_id
	)
	select lot_id, num_mem/num_spot as ratio_mem, num_onl/num_spot as ratio_onl, num_dv/num_spot as ratio_dv
	from total_lot_spots natural join total_members natural join total_online natural join total_drive_in
);

create view parking.times as select user_id, login_time, logout_time from parking.user;
--end report views

create role r_user;
create role staff;
create role admin;

--allow everyone to create an update_form and a reservation or view the bookings
grant insert on parking.update_form, parking.reservation to r_user, staff, admin;
grant select on parking.booking to r_user, staff, admin;

--allow staff members to view, delete, and update update_forms in addition to their permission to insert one earlier
grant select, delete, update on parking.update_form to staff, admin;

--allow only admins to edit profile information
grant select, delete, insert, update on parking.user, parking.member, parking.temporary_license_plate to admin;

--allow only admins to view the report views
grant all on parking.booking, parking.member_pay, parking.guest_pay, parking.lot_ratios, parking.times to admin;
