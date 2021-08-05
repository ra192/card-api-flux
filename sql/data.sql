INSERT INTO public.merchant (id, name, secret) VALUES (1, 'Internal', 'C5X+rivjAkiczaDqkDwMKAVxN+cSGrudaGCRhWZeVos=');
INSERT INTO public.merchant (id, name, secret) VALUES (2, 'Wayne Enterprise', '/05W5ibIDcNPkHXS8mC71CKB+10Ct9gzJ41C9HRMYeQ=');

INSERT INTO public.account (id, active, currency, name, merchant_id) VALUES (1, true, 'USD', 'Cash account', 1);
INSERT INTO public.account (id, active, currency, name, merchant_id) VALUES (2, true, 'USD', 'Card account', 1);
INSERT INTO public.account (id, active, currency, name, merchant_id) VALUES (3, true, 'USD', 'Fee account', 1);
INSERT INTO public.account (id, active, currency, name, merchant_id) VALUES (4, true, 'USD', 'Wayne USD account', 2);

INSERT INTO public.customer (id, active, address, address2, birth_date, city, country, email, first_name, last_name, phone, postal_code, state_region, merchant_id) VALUES (1, true, 'Cave', null, '1978-04-17', 'Gotham', 'US', 'batman@cave.com', 'Bruce', 'Wayne', '10010000001', '01', 'Gotham', 2);

INSERT INTO public.card (id, created, info, provider_reference_id, type, account_id, customer_id) VALUES (1, '2021-08-05 23:30:44.471940', 'xxxx', 'xxxx', 'VIRTUAL', 4, 1);

INSERT INTO public.transaction (id, order_id, status, type) VALUES (1, '1628195477683', 'COMPLETED', 'FUND');
INSERT INTO public.transaction_item (id, amount, created, card_id, dest_account_id, src_account_id, transaction_id) VALUES (1, 1000000, '2021-08-05 23:31:17.700938', null, 4, 1, 1);