create database agricultural_federation;

create role agri_user login password 'password';

grant all privileges on database agricultural_federation to agri_user;
