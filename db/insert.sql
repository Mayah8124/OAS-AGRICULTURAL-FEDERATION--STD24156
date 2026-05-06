begin;

truncate table
  member_payment,
  collectivity_transaction,
  financial_account_balance,
  collectivity_financial_account,
  collectivity_structure,
  collectivity_member,
  member_referee,
  member,
  membership_fee,
  mobile_banking_account,
  bank_account,
  cash_account,
  financial_account,
  collectivity
restart identity cascade;

insert into collectivity (id, location, federation_approval, name, number)
values
('col-1', 'Ambatondrazaka', true, 'Mpanorina', 1),
('col-2', 'Ambatondrazaka', true, 'Dobo voalohany', 2),
('col-3', 'Brickaville', true, 'Tantely mamy', 3)
on conflict (id) do nothing;

insert into membership_fee (id, eligible_from, frequency, amount, label, status, collectivity_id)
values
('cot-1', '2026-01-01', 'ANNUALLY', 100000, 'Cotisation annuelle', 'ACTIVE', 'col-1'),
('cot-2', '2026-01-01', 'ANNUALLY', 100000, 'Cotisation annuelle', 'ACTIVE', 'col-2'),
('cot-3', '2026-01-01', 'ANNUALLY',  50000, 'Cotisation annuelle', 'ACTIVE', 'col-3')
on conflict (id) do nothing;

insert into member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id)
values
('C1-M1', 'M1', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '111111', 'c1m1@test.com', 'SENIOR', 'col-1'),
('C1-M2', 'M2', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '222222', 'c1m2@test.com', 'SENIOR', 'col-1'),
('C1-M3', 'M3', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '333333', 'c1m3@test.com', 'SENIOR', 'col-1'),
('C1-M4', 'M4', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '444444', 'c1m4@test.com', 'SENIOR', 'col-1'),
('C1-M5', 'M5', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '555555', 'c1m5@test.com', 'SENIOR', 'col-1'),
('C1-M6', 'M6', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '666666', 'c1m6@test.com', 'SENIOR', 'col-1'),
('C1-M7', 'M7', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '777777', 'c1m7@test.com', 'SENIOR', 'col-1'),
('C1-M8', 'M8', 'Test', '2000-01-01', 'MALE', 'Addr', 'Farmer', '888888', 'c1m8@test.com', 'SENIOR', 'col-1')
on conflict (id) do nothing;

insert into collectivity_member (collectivity_id, member_id)
values
('col-1', 'C1-M1'),
('col-1', 'C1-M2'),
('col-1', 'C1-M3'),
('col-1', 'C1-M4'),
('col-1', 'C1-M5'),
('col-1', 'C1-M6'),
('col-1', 'C1-M7'),
('col-1', 'C1-M8')
on conflict do nothing;

insert into collectivity_structure (
    collectivity_id,
    president_member_id,
    vice_president_member_id,
    treasurer_member_id,
    secretary_member_id
) values (
    'col-1',
    'C1-M1',
    'C1-M2',
    'C1-M3',
    'C1-M4'
)
on conflict (collectivity_id) do nothing;

insert into financial_account (id, type)
values
('C1-A-CASH', 'CASH'),
('C1-A-MOBILE-1', 'MOBILE_BANKING'),
('C2-A-CASH', 'CASH'),
('C2-A-MOBILE-1', 'MOBILE_BANKING'),
('C3-A-CASH', 'CASH')
on conflict (id) do nothing;

insert into cash_account (id, amount)
values
('C1-A-CASH', 0),
('C2-A-CASH', 0),
('C3-A-CASH', 0)
on conflict (id) do nothing;

insert into mobile_banking_account (id, holder_name, mobile_banking_service, mobile_number, amount)
values
('C1-A-MOBILE-1', 'Mpanorina', 'ORANGE_MONEY', '0370489612', 0),
('C2-A-MOBILE-1', 'Dobo voalohany', 'ORANGE_MONEY', '0320489612', 0)
on conflict (id) do nothing;

insert into collectivity_financial_account (collectivity_id, financial_account_id)
values
('col-1', 'C1-A-CASH'),
('col-1', 'C1-A-MOBILE-1'),
('col-2', 'C2-A-CASH'),
('col-2', 'C2-A-MOBILE-1'),
('col-3', 'C3-A-CASH')
on conflict do nothing;

insert into member_payment (id, member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date)
values
('P-C1-M1-20260101', 'C1-M1', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('P-C1-M2-20260101', 'C1-M2', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('P-C1-M3-20260101', 'C1-M3', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('P-C1-M4-20260101', 'C1-M4', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('P-C1-M5-20260101', 'C1-M5', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('P-C1-M6-20260101', 'C1-M6', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('P-C1-M7-20260101', 'C1-M7', 'cot-1', 'C1-A-CASH',  60000, 'CASH', '2026-01-01'),
('P-C1-M8-20260101', 'C1-M8', 'cot-1', 'C1-A-CASH',  90000, 'CASH', '2026-01-01')
on conflict (id) do nothing;

insert into collectivity_transaction (id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date)
values
('T-C1-M1-20260101', 'col-1', 'C1-M1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('T-C1-M2-20260101', 'col-1', 'C1-M2', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('T-C1-M3-20260101', 'col-1', 'C1-M3', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('T-C1-M4-20260101', 'col-1', 'C1-M4', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('T-C1-M5-20260101', 'col-1', 'C1-M5', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('T-C1-M6-20260101', 'col-1', 'C1-M6', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
('T-C1-M7-20260101', 'col-1', 'C1-M7', 'C1-A-CASH',  60000, 'CASH', '2026-01-01'),
('T-C1-M8-20260101', 'col-1', 'C1-M8', 'C1-A-CASH',  90000, 'CASH', '2026-01-01')
on conflict (id) do nothing;

insert into financial_account_balance (financial_account_id, at_date, amount)
values
('C1-A-CASH', '2026-01-01', 750000),
('C1-A-MOBILE-1', '2026-01-01', 0),
('C2-A-CASH', '2026-01-01', 0),
('C2-A-MOBILE-1', '2026-01-01', 0),
('C3-A-CASH', '2026-01-01', 0)
on conflict (financial_account_id, at_date) do update set amount = excluded.amount;

commit;
