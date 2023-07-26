-- Insert Accounts
INSERT INTO accounts (account_id, cvu, alias, available_balance, user_id) VALUES (null, '1828142364587587491111', 'accion.adeudar.afianzamiento',400000.0, 1);
INSERT INTO accounts (account_id, cvu, alias, available_balance, user_id) VALUES (null, '1828142364587587493333', 'afectacion.divisa.cambios',220000.0, 2);
INSERT INTO accounts (account_id, cvu, alias, available_balance, user_id) VALUES (null, '1828142364587587495555', 'riqueza.dineros.devaluacion',120000.0, 3);
INSERT INTO accounts (account_id, cvu, alias, available_balance, user_id) VALUES (null, '2818991716171719171717', 'lampazo.gorila.bufanda',233450.0, 4);

-- Insert Transactions
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 100000.0, '2023-05-10 08:30:20', 'initial transaction', '1828142364587587491111', 1828142364587587493333, 'OUTGOING', 1);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 100000.0, '2023-05-10 08:30:20', 'received transaction', '1828142364587587491111', 1828142364587587493333, 'INCOMING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 20000.0, '2023-05-12 14:30:20', 'payment transaction', '1828142364587587493333', 1828142364587587495555, 'OUTGOING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 20000.0, '2023-05-12 14:30:20', 'received transaction', '1828142364587587493333', 1828142364587587495555, 'INCOMING', 3);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 5300.0, '2023-01-01 16:35:10', 'payment transaction', '1828142364587587491111', 1828142364587587493333, 'INCOMING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 17223.0, '2022-07-01 10:02:00', 'payment transaction', '1828142364587587491111', 1828142364587587493333, 'INCOMING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 112000.0, '2023-01-01 17:21:09', 'payment transaction', '1828142364587587495555', 1828142364587587493333, 'INCOMING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 20000.0, '2018-02-09 12:45:00', 'payment transaction', '1828142364587587495555', 1828142364587587493333, 'INCOMING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 6000.0, '2023-06-01 15:27:20', 'payment transaction', '1828142364587587493333', 1234567891234567891234, 'OUTGOING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 9500.0, '2023-06-02 12:55:20', 'payment transaction', '1828142364587587493333', 2222222222222222222222 , 'OUTGOING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 7650.0, '2023-06-07 11:27:20', 'payment transaction', '1828142364587587493333', 3333333333333333333333, 'OUTGOING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 17300.0, '2023-06-09 10:32:20', 'payment transaction', '1828142364587587493333',4444444444444444444444, 'OUTGOING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 19500.0, '2023-06-15 15:11:20', 'payment transaction', '1828142364587587493333',5555555555555555555555 , 'OUTGOING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 13450.0, '2023-06-19 13:00:20', 'payment transaction', '1828142364587587493333', 1828142364587587495555, 'OUTGOING', 2);
INSERT INTO transactions (transaction_id, amount, realization_date, description, from_cvu, to_cvu, type, account_id) VALUES (null, 13450.0, '2023-06-22 10:54:10', 'payment transaction', '1828142364587587493333', 1828142364587587495555, 'OUTGOING', 2);

-- Insert Card
INSERT INTO cards (card_id, alias, card_number, card_holder, expiration_date, cvv, bank, card_type, card_network, is_enabled, account_id) VALUES (null, 'Tarjeta MP', 5412873403403000, 'Admin Admin', '05/2024', 480, 'Mercado Pago', 'Crédito', "Mastercard",  true, 1);
INSERT INTO cards (card_id, alias, card_number, card_holder, expiration_date, cvv, bank, card_type, card_network, is_enabled, account_id) VALUES (null, 'Tarjeta Amex Platinum', 376455323736878, 'Admin Admin', '03/2025', 1698, 'AMEX', 'Crédito', "AMEX", true, 1);
INSERT INTO cards (card_id, alias, card_number, card_holder, expiration_date, cvv, bank, card_type, card_network,  is_enabled, account_id) VALUES (null, 'Tarjeta Brubank', 4539793783086269, 'User One', '09/2023', 123, 'Brubank S.A.U.', 'Débito', "Visa",  true, 2);
INSERT INTO cards (card_id, alias, card_number, card_holder, expiration_date, cvv, bank, card_type, card_network,  is_enabled, account_id) VALUES (null, 'Tarjeta ICBC', 4539270908211569, 'User One', '02/2024', 123, 'ICBC', 'Débito', "Visa",  true, 2);
INSERT INTO cards (card_id, alias, card_number, card_holder, expiration_date, cvv, bank, card_type, card_network,  is_enabled, account_id) VALUES (null, 'Tarjeta ICBC', 4423056737256761, 'User One', '01/2022', 123, 'ICBC', 'Crédito', "Visa",  true, 2);
INSERT INTO cards (card_id, alias, card_number, card_holder, expiration_date, cvv, bank, card_type, card_network,  is_enabled, account_id) VALUES (null, 'Tarjeta Ualá', 5304690884036864, 'User Two', '10/2028', 123, 'Ualá', 'Pre-Paga', "Mastercard",  true, 3);

