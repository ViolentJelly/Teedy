-- DBUPDATE-032-0.SQL

-- Ensure guest login flag exists, then enforce the expected value
insert into T_CONFIG (CFG_ID_C, CFG_VALUE_C)
select 'GUEST_LOGIN', 'true'
where not exists (
    select 1 from T_CONFIG where CFG_ID_C = 'GUEST_LOGIN'
);
update T_CONFIG set CFG_VALUE_C = 'true' where CFG_ID_C = 'GUEST_LOGIN';

-- Ensure guest user exists for guest login
insert into T_USER (USE_ID_C, USE_IDROLE_C, USE_USERNAME_C, USE_PASSWORD_C, USE_EMAIL_C, USE_CREATEDATE_D, USE_PRIVATEKEY_C)
select 'guest', 'user', 'guest', '', 'guest@localhost', NOW(), 'GuestPk'
where not exists (
    select 1 from T_USER where USE_ID_C = 'guest'
);

-- Update the database version
update T_CONFIG set CFG_VALUE_C = '32' where CFG_ID_C = 'DB_VERSION';
