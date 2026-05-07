rollback;

set search_path to public;

do $$
begin
    begin
        if exists (select 1 from information_schema.tables where table_schema = 'public' and table_name = 'member_payment') then
            truncate table
                member_payment,
                collectivity_transaction,
                financial_account_balance,
                collectivity_financial_account,
                activity_member_attendance,
                collectivity_activity_occupation,
                collectivity_activity,
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
        end if;
    exception when undefined_table then
        null;
    end;
end $$;

insert into collectivity (id, location, federation_approval, name, number)
values
    ('col-1', 'Ambatondrazaka', true, 'Mpanorina', 1),
    ('col-2', 'Ambatondrazaka', true, 'Dobo voalohany', 2),
    ('col-3', 'Brickaville', true, 'Tantely mamy', 3)
on conflict (id) do nothing;

insert into membership_fee (id, eligible_from, frequency, amount, label, status, collectivity_id)
values
    ('cot-1', '2026-01-01', 'ANNUALLY', 200000, 'Cotisation annuelle', 'ACTIVE', 'col-1'),
    ('cot-2', '2026-04-30', 'PUNCTUALLY', 20000, 'Famangiana', 'ACTIVE', 'col-1'),
    ('cot-3', '2026-01-01', 'ANNUALLY', 200000, 'Cotisation annuelle', 'ACTIVE', 'col-2'),
    ('cot-4', '2025-01-01', 'ANNUALLY', 100000, 'Cotisation 2025', 'INACTIVE', 'col-2'),
    ('cot-5', '2026-04-01', 'MONTHLY', 25000, 'Cotisation mensuelle', 'ACTIVE', 'col-3')
on conflict (id) do nothing;

insert into member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id, creation_date)
values
    ('C1-M1', 'Prénom membre 1', 'Nom membre 1', '1980-02-01', 'MALE', 'Lot II V M Ambato.', 'Riziculteur', '0341234567', 'membre.1@fed-agri.mg', 'PRESIDENT', 'col-1', '2026-01-01'),
    ('C1-M2', 'Prénom membre 2', 'Nom membre 2', '1982-03-05', 'MALE', 'Lot II F Ambato.', 'Agriculteur', '0321234567', 'membre.2@fed-agri.mg', 'VICE_PRESIDENT', 'col-1', '2026-01-01'),
    ('C1-M3', 'Prénom membre 3', 'Nom membre 3', '1992-03-10', 'MALE', 'Lot II J Ambato.', 'Collecteur', '0331234567', 'membre.3@fed-agri.mg', 'SECRETARY', 'col-1', '2026-01-01'),
    ('C1-M4', 'Prénom membre 4', 'Nom membre 4', '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato.', 'Distributeur', '0381234567', 'membre.4@fed-agri.mg', 'TREASURER', 'col-1', '2026-01-01'),
    ('C1-M5', 'Prénom membre 5', 'Nom membre 5', '1999-08-21', 'MALE', 'Lot UV 80 Ambato.', 'Riziculteur', '0373434567', 'membre.5@fed-agri.mg', 'CONFIRMED', 'col-1', '2026-01-01'),
    ('C1-M6', 'Prénom membre 6', 'Nom membre 6', '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato.', 'Riziculteur', '0372234567', 'membre.6@fed-agri.mg', 'CONFIRMED', 'col-1', '2026-01-01'),
    ('C1-M7', 'Prénom membre 7', 'Nom membre 7', '1998-01-31', 'MALE', 'Lot UV 7 Ambato.', 'Riziculteur', '0374234567', 'membre.7@fed-agri.mg', 'CONFIRMED', 'col-1', '2026-01-01'),
    ('C1-M8', 'Prénom membre 8', 'Nom membre 8', '1975-08-20', 'MALE', 'Lot UV 8 Ambato.', 'Riziculteur', '0370234567', 'membre.8@fed-agri.mg', 'CONFIRMED', 'col-1', '2026-01-01'),
    ('C2-M1', 'Prénom membre 1', 'Nom membre 1', '1980-02-01', 'MALE', 'Lot II V M Ambato.', 'Riziculteur', '0341234567', 'membre.1@fed-agri.mg', 'CONFIRMED', 'col-2', '2026-01-01'),
    ('C2-M2', 'Prénom membre 2', 'Nom membre 2', '1982-03-05', 'MALE', 'Lot II F Ambato.', 'Agriculteur', '0321234567', 'membre.2@fed-agri.mg', 'CONFIRMED', 'col-2', '2026-01-01'),
    ('C2-M3', 'Prénom membre 3', 'Nom membre 3', '1992-03-10', 'MALE', 'Lot II J Ambato.', 'Collecteur', '0331234567', 'membre.3@fed-agri.mg', 'CONFIRMED', 'col-2', '2026-01-01'),
    ('C2-M4', 'Prénom membre 4', 'Nom membre 4', '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato.', 'Distributeur', '0381234567', 'membre.4@fed-agri.mg', 'CONFIRMED', 'col-2', '2026-01-01'),
    ('C2-M5', 'Prénom membre 5', 'Nom membre 5', '1999-08-21', 'MALE', 'Lot UV 80 Ambato.', 'Riziculteur', '0373434567', 'membre.5@fed-agri.mg', 'PRESIDENT', 'col-2', '2026-01-01'),
    ('C2-M6', 'Prénom membre 6', 'Nom membre 6', '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato.', 'Riziculteur', '0372234567', 'membre.6@fed-agri.mg', 'VICE_PRESIDENT', 'col-2', '2026-01-01'),
    ('C2-M7', 'Prénom membre 7', 'Nom membre 7', '1998-01-31', 'MALE', 'Lot UV 7 Ambato.', 'Riziculteur', '0374234567', 'membre.7@fed-agri.mg', 'SECRETARY', 'col-2', '2026-01-01'),
    ('C2-M8', 'Prénom membre 8', 'Nom membre 8', '1975-08-20', 'MALE', 'Lot UV 8 Ambato.', 'Riziculteur', '0370234567', 'membre.8@fed-agri.mg', 'TREASURER', 'col-2', '2026-01-01'),
    ('C3-M1', 'Prénom membre 9', 'Nom membre 9', '1988-01-02', 'MALE', 'Lot 33 J Antsirabe', 'Apiculteur', '034034567', 'membre.9@fed-agri.mg', 'PRESIDENT', 'col-3', '2026-01-01'),
    ('C3-M2', 'Prénom membre 10', 'Nom membre 10', '1982-03-05', 'MALE', 'Lot 2 J Antsirabe', 'Agriculteur', '0338634567', 'membre.10@fed-agri.mg', 'VICE_PRESIDENT', 'col-3', '2026-01-01'),
    ('C3-M3', 'Prénom membre 11', 'Nom membre 11', '1992-03-12', 'MALE', 'Lot 8 KM Antsirabe', 'Collecteur', '0338234567', 'membre.11@fed-agri.mg', 'SECRETARY', 'col-3', '2026-01-01'),
    ('C3-M4', 'Prénom membre 12', 'Nom membre 12', '1988-05-10', 'FEMALE', 'Lot A K 50 Antsirabe', 'Distributeur', '0382334567', 'membre.12@fed-agri.mg', 'TREASURER', 'col-3', '2026-01-01'),
    ('C3-M5', 'Prénom membre 13', 'Nom membre 13', '1999-08-11', 'MALE', 'Lot UV 80 Antsirabe.', 'Apiculteur', '0373365567', 'membre.13@fed-agri.mg', 'CONFIRMED', 'col-3', '2026-01-01'),
    ('C3-M6', 'Prénom membre 14', 'Nom membre 14', '1998-08-09', 'FEMALE', 'Lot UV 6 Antsirabe.', 'Apiculteur', '0378234567', 'membre.14@fed-agri.mg', 'CONFIRMED', 'col-3', '2026-01-01'),
    ('C3-M7', 'Prénom membre 15', 'Nom membre 15', '1998-01-13', 'MALE', 'Lot UV 7 Antsirabe.', 'Apiculteur', '0374914567', 'membre.15@fed-agri.mg', 'CONFIRMED', 'col-3', '2026-01-01'),
    ('C3-M8', 'Prénom membre 16', 'Nom membre 16', '1975-08-02', 'MALE', 'Lot UV 8 Antsirabe.', 'Apiculteur', '0370634567', 'membre.16@fed-agri.mg', 'CONFIRMED', 'col-3', '2026-01-01'),
    ('C1-N1', 'Nouveau 1', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000001', 'c1n1@fed-agri.mg', 'JUNIOR', 'col-1', '2026-04-01'),
    ('C1-N2', 'Nouveau 2', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000002', 'c1n2@fed-agri.mg', 'JUNIOR', 'col-1', '2026-04-01'),
    ('C1-N3', 'Nouveau 3', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000003', 'c1n3@fed-agri.mg', 'JUNIOR', 'col-1', '2026-05-01'),
    ('C1-N4', 'Nouveau 4', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000004', 'c1n4@fed-agri.mg', 'JUNIOR', 'col-1', '2026-06-01'),
    ('C2-N1', 'Nouveau 1', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000011', 'c2n1@fed-agri.mg', 'JUNIOR', 'col-2', '2026-03-01'),
    ('C2-N2', 'Nouveau 2', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000012', 'c2n2@fed-agri.mg', 'JUNIOR', 'col-2', '2026-03-01'),
    ('C2-N3', 'Nouveau 3', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000013', 'c2n3@fed-agri.mg', 'JUNIOR', 'col-2', '2026-03-01'),
    ('C3-N1', 'Nouveau 1', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000021', 'c3n1@fed-agri.mg', 'JUNIOR', 'col-3', '2026-03-01'),
    ('C3-N2', 'Nouveau 2', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000022', 'c3n2@fed-agri.mg', 'JUNIOR', 'col-3', '2026-03-01'),
    ('C3-N3', 'Nouveau 3', 'Adherent', '2000-01-01', 'MALE', 'Addr', 'Prof', '0300000023', 'c3n3@fed-agri.mg', 'JUNIOR', 'col-3', '2026-03-01')
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
    ('col-1', 'C1-M8'),
    ('col-2', 'C2-M1'),
    ('col-2', 'C2-M2'),
    ('col-2', 'C2-M3'),
    ('col-2', 'C2-M4'),
    ('col-2', 'C2-M5'),
    ('col-2', 'C2-M6'),
    ('col-2', 'C2-M7'),
    ('col-2', 'C2-M8'),
    ('col-3', 'C3-M1'),
    ('col-3', 'C3-M2'),
    ('col-3', 'C3-M3'),
    ('col-3', 'C3-M4'),
    ('col-3', 'C3-M5'),
    ('col-3', 'C3-M6'),
    ('col-3', 'C3-M7'),
    ('col-3', 'C3-M8'),
    ('col-1', 'C1-N1'),
    ('col-1', 'C1-N2'),
    ('col-1', 'C1-N3'),
    ('col-1', 'C1-N4'),
    ('col-2', 'C2-N1'),
    ('col-2', 'C2-N2'),
    ('col-2', 'C2-N3'),
    ('col-3', 'C3-N1'),
    ('col-3', 'C3-N2'),
    ('col-3', 'C3-N3')
on conflict do nothing;

insert into member_referee (member_id, referee_id)
values
    ('C1-M3', 'C1-M1'),
    ('C1-M3', 'C1-M2'),
    ('C1-M4', 'C1-M1'),
    ('C1-M4', 'C1-M2'),
    ('C1-M5', 'C1-M1'),
    ('C1-M5', 'C1-M2'),
    ('C1-M6', 'C1-M1'),
    ('C1-M6', 'C1-M2'),
    ('C1-M7', 'C1-M1'),
    ('C1-M7', 'C1-M2'),
    ('C1-M8', 'C1-M6'),
    ('C1-M8', 'C1-M7'),
    ('C2-M3', 'C1-M1'),
    ('C2-M3', 'C1-M2'),
    ('C2-M4', 'C1-M1'),
    ('C2-M4', 'C1-M2'),
    ('C2-M5', 'C1-M1'),
    ('C2-M5', 'C1-M2'),
    ('C2-M6', 'C1-M1'),
    ('C2-M6', 'C1-M2'),
    ('C2-M7', 'C1-M1'),
    ('C2-M7', 'C1-M2'),
    ('C2-M8', 'C1-M6'),
    ('C2-M8', 'C1-M7'),
    ('C3-M1', 'C1-M1'),
    ('C3-M1', 'C1-M2'),
    ('C3-M2', 'C1-M1'),
    ('C3-M2', 'C1-M2'),
    ('C3-M3', 'C3-M1'),
    ('C3-M3', 'C3-M2'),
    ('C3-M4', 'C3-M1'),
    ('C3-M4', 'C3-M2'),
    ('C3-M5', 'C3-M1'),
    ('C3-M5', 'C3-M2'),
    ('C3-M6', 'C3-M1'),
    ('C3-M6', 'C3-M2'),
    ('C3-M7', 'C3-M1'),
    ('C3-M7', 'C3-M2'),
    ('C3-M8', 'C3-M1'),
    ('C3-M8', 'C3-M2'),
    ('C1-N1', 'C1-M1'),
    ('C1-N1', 'C1-M2'),
    ('C1-N2', 'C1-M1'),
    ('C1-N2', 'C1-M2'),
    ('C1-N3', 'C1-M1'),
    ('C1-N3', 'C1-M2'),
    ('C1-N4', 'C1-M1'),
    ('C1-N4', 'C1-M2'),
    ('C2-N1', 'C1-M1'),
    ('C2-N1', 'C1-M2'),
    ('C2-N2', 'C1-M1'),
    ('C2-N2', 'C1-M2'),
    ('C2-N3', 'C1-M1'),
    ('C2-N3', 'C1-M2'),
    ('C3-N1', 'C3-M1'),
    ('C3-N1', 'C3-M2'),
    ('C3-N2', 'C3-M1'),
    ('C3-N2', 'C3-M2'),
    ('C3-N3', 'C3-M1'),
    ('C3-N3', 'C3-M2')
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
             'C1-M4',
             'C1-M3'
         ),
         (
             'col-2',
             'C2-M5',
             'C2-M6',
             'C2-M8',
             'C2-M7'
         ),
         (
             'col-3',
             'C3-M1',
             'C3-M2',
             'C3-M4',
             'C3-M3'
         )
on conflict (collectivity_id) do nothing;

insert into financial_account (id, type)
values
    ('C1-A-CASH', 'CASH'),
    ('C1-A-MOBILE-1', 'MOBILE_BANKING'),
    ('C2-A-CASH', 'CASH'),
    ('C2-A-MOBILE-1', 'MOBILE_BANKING'),
    ('C3-A-CASH', 'CASH'),
    ('C3-A-BANK-1', 'BANK_TRANSFER'),
    ('C3-A-BANK-2', 'BANK_TRANSFER'),
    ('C3-A-MOBILE-1', 'MOBILE_BANKING')
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
    ('C2-A-MOBILE-1', 'Dobo voalohany', 'ORANGE_MONEY', '0320489612', 0),
    ('C3-A-MOBILE-1', 'Kolo', 'MVOLA', '0341889612', 0)
on conflict (id) do nothing;

insert into bank_account (id, holder_name, bank_name, account_number, amount)
values
    ('C3-A-BANK-1', 'Koto', 'BMOI', '1234567890', 0),
    ('C3-A-BANK-2', 'Naivo', 'BRED', '4567890123', 0)
on conflict (id) do nothing;

insert into collectivity_financial_account (collectivity_id, financial_account_id)
values
    ('col-1', 'C1-A-CASH'),
    ('col-1', 'C1-A-MOBILE-1'),
    ('col-2', 'C2-A-CASH'),
    ('col-2', 'C2-A-MOBILE-1'),
    ('col-3', 'C3-A-CASH'),
    ('col-3', 'C3-A-BANK-1'),
    ('col-3', 'C3-A-BANK-2'),
    ('col-3', 'C3-A-MOBILE-1')
on conflict do nothing;

do $$
begin
    if exists (select 1 from information_schema.tables where table_schema = 'public' and table_name = 'member_payment') then
        insert into member_payment (id, member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date)
        values
            ('P-C1-M1-20260101', 'C1-M1', 'cot-1', 'C1-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('P-C1-M2-20260101', 'C1-M2', 'cot-1', 'C1-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('P-C1-M3-20260101', 'C1-M3', 'cot-1', 'C1-A-MOBILE-1', 200000, 'MOBILE_BANKING', '2026-01-01'),
            ('P-C1-M4-20260101', 'C1-M4', 'cot-1', 'C1-A-MOBILE-1', 200000, 'MOBILE_BANKING', '2026-01-01'),
            ('P-C1-M5-20260101', 'C1-M5', 'cot-1', 'C1-A-MOBILE-1', 150000, 'MOBILE_BANKING', '2026-01-01'),
            ('P-C1-M6-20260501', 'C1-M6', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-05-01'),
            ('P-C1-M7-20260501', 'C1-M7', 'cot-1', 'C1-A-CASH', 60000, 'CASH', '2026-05-01'),
            ('P-C1-M8-20260501', 'C1-M8', 'cot-1', 'C1-A-CASH', 90000, 'CASH', '2026-05-01'),
            ('P-C2-M1-20260101', 'C2-M1', 'cot-3', 'C2-A-CASH', 120000, 'CASH', '2026-01-01'),
            ('P-C2-M2-20260101', 'C2-M2', 'cot-3', 'C2-A-CASH', 180000, 'CASH', '2026-01-01'),
            ('P-C2-M3-20260101', 'C2-M3', 'cot-3', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('P-C2-M4-20260101', 'C2-M4', 'cot-3', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('P-C2-M5-20260101', 'C2-M5', 'cot-3', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('P-C2-M6-20260101', 'C2-M6', 'cot-3', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('P-C2-M7-20260101', 'C2-M7', 'cot-3', 'C2-A-MOBILE-1', 80000, 'MOBILE_BANKING', '2026-01-01'),
            ('P-C2-M8-20260101', 'C2-M8', 'cot-3', 'C2-A-MOBILE-1', 120000, 'MOBILE_BANKING', '2026-01-01'),
            ('P-C3-M1-20260401', 'C3-M1', 'cot-5', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('P-C3-M2-20260401', 'C3-M2', 'cot-5', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('P-C3-M3-20260401', 'C3-M3', 'cot-5', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('P-C3-M4-20260401', 'C3-M4', 'cot-5', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('P-C3-M5-20260401', 'C3-M5', 'cot-5', 'C3-A-BANK-2', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('P-C3-M6-20260401', 'C3-M6', 'cot-5', 'C3-A-BANK-2', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('P-C3-M7-20260401', 'C3-M7', 'cot-5', 'C3-A-CASH', 25000, 'CASH', '2026-04-01'),
            ('P-C3-M8-20260401', 'C3-M8', 'cot-5', 'C3-A-CASH', 25000, 'CASH', '2026-04-01'),
            ('P-C3-M1-20260501', 'C3-M1', 'cot-5', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-05-01'),
            ('P-C3-M2-20260501', 'C3-M2', 'cot-5', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-05-01'),
            ('P-C3-M3-20260501', 'C3-M3', 'cot-5', 'C3-A-MOBILE-1', 15000, 'MOBILE_BANKING', '2026-05-01'),
            ('P-C3-M4-20260501', 'C3-M4', 'cot-5', 'C3-A-MOBILE-1', 15000, 'MOBILE_BANKING', '2026-05-01'),
            ('P-C3-M5-20260501', 'C3-M5', 'cot-5', 'C3-A-BANK-2', 20000, 'BANK_TRANSFER', '2026-05-01'),
            ('P-C3-M6-20260501', 'C3-M6', 'cot-5', 'C3-A-BANK-2', 25000, 'BANK_TRANSFER', '2026-05-01'),
            ('P-C3-M7-20260501', 'C3-M7', 'cot-5', 'C3-A-CASH', 5000, 'CASH', '2026-05-01'),
            ('P-C3-M8-20260501', 'C3-M8', 'cot-5', 'C3-A-CASH', 5000, 'CASH', '2026-05-01')
        on conflict (id) do nothing;
    end if;
end $$;

do $$
begin
    if exists (select 1 from information_schema.tables where table_schema = 'public' and table_name = 'collectivity_transaction') then
        insert into collectivity_transaction (id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date)
        values
            ('T-C1-M1-20260101', 'col-1', 'C1-M1', 'C1-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('T-C1-M2-20260101', 'col-1', 'C1-M2', 'C1-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('T-C1-M3-20260101', 'col-1', 'C1-M3', 'C1-A-MOBILE-1', 200000, 'MOBILE_BANKING', '2026-01-01'),
            ('T-C1-M4-20260101', 'col-1', 'C1-M4', 'C1-A-MOBILE-1', 200000, 'MOBILE_BANKING', '2026-01-01'),
            ('T-C1-M5-20260101', 'col-1', 'C1-M5', 'C1-A-MOBILE-1', 150000, 'MOBILE_BANKING', '2026-01-01'),
            ('T-C1-M6-20260501', 'col-1', 'C1-M6', 'C1-A-CASH', 100000, 'CASH', '2026-05-01'),
            ('T-C1-M7-20260501', 'col-1', 'C1-M7', 'C1-A-CASH', 60000, 'CASH', '2026-05-01'),
            ('T-C1-M8-20260501', 'col-1', 'C1-M8', 'C1-A-CASH', 90000, 'CASH', '2026-05-01'),
            ('T-C2-M1-20260101', 'col-2', 'C2-M1', 'C2-A-CASH', 120000, 'CASH', '2026-01-01'),
            ('T-C2-M2-20260101', 'col-2', 'C2-M2', 'C2-A-CASH', 180000, 'CASH', '2026-01-01'),
            ('T-C2-M3-20260101', 'col-2', 'C2-M3', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('T-C2-M4-20260101', 'col-2', 'C2-M4', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('T-C2-M5-20260101', 'col-2', 'C2-M5', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('T-C2-M6-20260101', 'col-2', 'C2-M6', 'C2-A-CASH', 200000, 'CASH', '2026-01-01'),
            ('T-C2-M7-20260101', 'col-2', 'C2-M7', 'C2-A-MOBILE-1', 80000, 'MOBILE_BANKING', '2026-01-01'),
            ('T-C2-M8-20260101', 'col-2', 'C2-M8', 'C2-A-MOBILE-1', 120000, 'MOBILE_BANKING', '2026-01-01'),
            ('T-C3-M1-20260401', 'col-3', 'C3-M1', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('T-C3-M2-20260401', 'col-3', 'C3-M2', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('T-C3-M3-20260401', 'col-3', 'C3-M3', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('T-C3-M4-20260401', 'col-3', 'C3-M4', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('T-C3-M5-20260401', 'col-3', 'C3-M5', 'C3-A-BANK-2', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('T-C3-M6-20260401', 'col-3', 'C3-M6', 'C3-A-BANK-2', 25000, 'BANK_TRANSFER', '2026-04-01'),
            ('T-C3-M7-20260401', 'col-3', 'C3-M7', 'C3-A-CASH', 25000, 'CASH', '2026-04-01'),
            ('T-C3-M8-20260401', 'col-3', 'C3-M8', 'C3-A-CASH', 25000, 'CASH', '2026-04-01'),
            ('T-C3-M1-20260501', 'col-3', 'C3-M1', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-05-01'),
            ('T-C3-M2-20260501', 'col-3', 'C3-M2', 'C3-A-BANK-1', 25000, 'BANK_TRANSFER', '2026-05-01'),
            ('T-C3-M3-20260501', 'col-3', 'C3-M3', 'C3-A-MOBILE-1', 15000, 'BANK_TRANSFER', '2026-05-01'),
            ('T-C3-M4-20260501', 'col-3', 'C3-M4', 'C3-A-MOBILE-1', 15000, 'BANK_TRANSFER', '2026-05-01'),
            ('T-C3-M5-20260501', 'col-3', 'C3-M5', 'C3-A-BANK-2', 20000, 'BANK_TRANSFER', '2026-05-01'),
            ('T-C3-M6-20260501', 'col-3', 'C3-M6', 'C3-A-BANK-2', 25000, 'BANK_TRANSFER', '2026-05-01'),
            ('T-C3-M7-20260501', 'col-3', 'C3-M7', 'C3-A-CASH', 5000, 'CASH', '2026-05-01'),
            ('T-C3-M8-20260501', 'col-3', 'C3-M8', 'C3-A-CASH', 5000, 'CASH', '2026-05-01')
        on conflict (id) do nothing;
    end if;
end $$;

insert into collectivity_activity (id, collectivity_id, label, activity_type, executive_date, recurrence_week_ordinal, recurrence_day_of_week)
values
    ('C1-ACT-1', 'col-1', 'Réunion hebdomadaire', 'MEETING', null, 1, 'MO'),
    ('C1-ACT-2', 'col-1', 'Formation compost', 'TRAINING', '2026-01-15', null, null)
on conflict (id) do nothing;

insert into collectivity_activity_occupation (activity_id, member_occupation)
values
    ('C1-ACT-1', 'PRESIDENT'),
    ('C1-ACT-1', 'VICE_PRESIDENT'),
    ('C1-ACT-1', 'TREASURER'),
    ('C1-ACT-1', 'SECRETARY')
on conflict do nothing;

insert into financial_account_balance (financial_account_id, at_date, amount)
values
    ('C1-A-CASH', '2026-01-01', 750000),
    ('C1-A-MOBILE-1', '2026-01-01', 0),
    ('C2-A-CASH', '2026-01-01', 0),
    ('C2-A-MOBILE-1', '2026-01-01', 0),
    ('C3-A-CASH', '2026-01-01', 0)
on conflict (financial_account_id, at_date) do update set amount = excluded.amount;