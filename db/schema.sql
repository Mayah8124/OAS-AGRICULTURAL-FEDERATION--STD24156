create table if not exists collectivity (
                                            id varchar(50) primary key,
                                            location text not null,
                                            federation_approval boolean not null,
                                            name text unique,
                                            number integer unique
);

create table if not exists member (
                                      id varchar(50) primary key,
                                      first_name text,
                                      last_name text,
                                      birth_date date,
                                      gender text,
                                      address text,
                                      profession text,
                                      phone_number varchar(50),
                                      email text,
                                      occupation text,
                                      collectivity_id varchar(50) references collectivity(id)
);

create table if not exists member_referee (
                                              member_id varchar(50) not null references member(id),
                                              referee_id varchar(50) not null references member(id),
                                              primary key (member_id, referee_id)
);

create table if not exists collectivity_member (
                                                   collectivity_id varchar(50) not null references collectivity(id),
                                                   member_id varchar(50) not null references member(id),
                                                   primary key (collectivity_id, member_id)
);

create table if not exists collectivity_structure (
                                                      collectivity_id varchar(50) primary key references collectivity(id),
                                                      president_member_id varchar(50) not null references member(id),
                                                      vice_president_member_id varchar(50) not null references member(id),
                                                      treasurer_member_id varchar(50) not null references member(id),
                                                      secretary_member_id varchar(50) not null references member(id)
);

create table if not exists membership_fee (
                                              id varchar(50) primary key,
                                              eligible_from date not null,
                                              frequency text not null,
                                              amount numeric not null,
                                              label text not null,
                                              status text not null,
                                              collectivity_id varchar(50) not null references collectivity(id)
);

create table if not exists financial_account (
                                                 id varchar(50) primary key,
                                                 type text not null
);

create table if not exists cash_account (
                                            id varchar(50) primary key references financial_account(id),
                                            amount numeric
);

create table if not exists mobile_banking_account (
                                                      id varchar(50) primary key references financial_account(id),
                                                      holder_name text,
                                                      mobile_banking_service text,
                                                      mobile_number text,
                                                      amount numeric
);

create table if not exists bank_account (
                                            id varchar(50) primary key references financial_account(id),
                                            holder_name text,
                                            bank_name text,
                                            bank_code integer,
                                            bank_branch_code integer,
                                            bank_account_number integer,
                                            bank_account_key integer,
                                            amount numeric
);

create table if not exists financial_account_balance (
                                                         financial_account_id varchar(50) not null references financial_account(id),
                                                         at_date date not null,
                                                         amount numeric,
                                                         primary key (financial_account_id, at_date)
);

create table if not exists member_payment (
                                              id varchar(50) primary key,
                                              member_id varchar(50) not null references member(id),
                                              membership_fee_id varchar(50) references membership_fee(id),
                                              account_credited_id varchar(50) not null references financial_account(id),
                                              amount numeric,
                                              payment_mode text,
                                              creation_date date
);

create table if not exists collectivity_financial_account (
                                                              collectivity_id varchar(50) not null references collectivity(id),
                                                              financial_account_id varchar(50) not null references financial_account(id),
                                                              primary key (collectivity_id, financial_account_id)
);

create table if not exists collectivity_transaction (
                                                        id varchar(50) primary key,
                                                        collectivity_id varchar(50) not null references collectivity(id),
                                                        member_debited_id varchar(50) references member(id),
                                                        account_credited_id varchar(50) not null references financial_account(id),
                                                        amount numeric,
                                                        payment_mode text,
                                                        creation_date date
);

create index if not exists idx_membership_fee_collectivity_id on membership_fee(collectivity_id);
create index if not exists idx_member_payment_member_id on member_payment(member_id);
create index if not exists idx_member_payment_membership_fee_id on member_payment(membership_fee_id);
create index if not exists idx_member_payment_creation_date on member_payment(creation_date);
create index if not exists idx_financial_account_balance_at_date on financial_account_balance(at_date);
create index if not exists idx_collectivity_financial_account_collectivity_id on collectivity_financial_account(collectivity_id);
create index if not exists idx_collectivity_financial_account_financial_account_id on collectivity_financial_account(financial_account_id);
create index if not exists idx_collectivity_transaction_collectivity_id on collectivity_transaction(collectivity_id);
create index if not exists idx_collectivity_transaction_creation_date on collectivity_transaction(creation_date);
